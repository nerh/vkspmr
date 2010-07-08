import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Spamer {
	public static void main(String args[]) throws InterruptedException{
		SessionRestorer sessionRestorer = null;
		Logger logger = Logger.createLogger();
		ConfigReader confReader = null;
		String message = null;
		String title = null;
		Parser parser = Parser.createParser();
		LinkedList<Container> accounts = null;
		LinkedList<Topic> topics = null;
		ArrayList<AccountThread> runningAccounts= new ArrayList<AccountThread>();
		UserQuoueu quoueu = null;
		Restriction restriction = null;
		CaptchaQuoueu captcha = new CaptchaQuoueu();
		try {
			confReader = ConfigReader.createConfigReader("spamer.xml");
		} catch (FileNotFoundException e) {
			logger.printExceptionInfo(e);
		} catch (IOException e) {
			logger.printExceptionInfo(e);
		}
		
		
		message = confReader.getMessage();
		title = confReader.getTitle();
		accounts = confReader.getAccountList();
		topics = confReader.getTargetList();
		restriction = confReader.getRestriction();
		sessionRestorer = new SessionRestorer();
		ArrayList<String> to_restore = null;
		boolean restore_readed = true;
		boolean send_restore = false;
		
		try {
			sessionRestorer.open("resend", SessionRestorer.READ);
			to_restore = (ArrayList<String>) sessionRestorer.read();
		} catch (IOException e2) {
			restore_readed = false;
			logger.printExceptionInfo(e2);
		}
		
		if(restore_readed == true && to_restore != null){
			System.out.println("Send old? y/n");
			try {
				int r = System.in.read();
				if(r=='y')
					send_restore = true;
			} catch (IOException e) {
				logger.printExceptionInfo(e);
			}
		}
		
		if(!send_restore){
			SessionVK s = new SessionVK(accounts.get(0));
			int res;
			while(-1==(res = s.connect())){
				System.out.println("------------------");
				System.out.println("Captcha detected.");
				System.out.println("Login: " + accounts.get(0).first);
				System.out.println("Pass: " + accounts.get(0).second);
			}
			
			System.out.println("Getting ids from topics...");
			LinkedList<String> ids = new LinkedList<String>();
			for(Topic t : topics){
				System.out.println(t.url);
				ids.addAll(s.getIdFromTopic(t));
			}
			quoueu = new UserQuoueu(ids);
		} else {
			quoueu = new UserQuoueu(new LinkedList<String>(to_restore));
		}
		System.out.println("Accounts count is " + quoueu.getUsers().size());
		System.out.println("Strating sending...");
		ExecutorService pool = Executors.newCachedThreadPool();
		pool.execute(captcha);
		for(Container a : accounts){
			AccountThread at = new AccountThread(a,quoueu,
					captcha, new Container(title, message), restriction);
			runningAccounts.add(at);
			pool.execute(at);
		}
		InfoThread info = new InfoThread(runningAccounts, quoueu);
		pool.execute(info);
		pool.shutdown();
		
		while(runningAccounts.size()>0){
			int running = 0;
			for(AccountThread t : runningAccounts){
				if(t.running)
					running++;
			}
			if(running==0) break;
		}
		pool.shutdownNow();
		
		boolean restored = true;
		while(info.running){
			Thread.sleep(1000);
			Thread.yield();
		}
		captcha.interrupt();
		try {
			sessionRestorer.open("resend", SessionRestorer.WRITE);
			sessionRestorer.write(quoueu.getUsers());
			sessionRestorer.close();
		} catch (IOException e1) {
			logger.printExceptionInfo(e1);
			restored = false;
		}
		
		if(!restored){
			System.out.println(quoueu.getUsers());
		}
		
		System.out.println("Program stopped");
		try {
			System.in.read();
			System.in.read();
			System.in.read();
		} catch (IOException e) {
			
		}
	}
	
}
