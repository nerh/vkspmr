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
		Logger logger = Logger.createLogger();
		ConfigReader confReader = null;
		String message = null;
		String title = null;
		Parser parser = Parser.createParser();
		LinkedList<Container> accounts = null;
		LinkedList<Topic> topics = null;
		ArrayList<AccountThread> runningAccounts= new ArrayList<AccountThread>();
		UserQuoueu quoueu = null;
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
		SessionVK s = new SessionVK(accounts.get(0));
		s.connect();
		
		System.out.println("Getting ids from topics...");
		LinkedList<String> ids = new LinkedList<String>();
		for(Topic t : topics){
			System.out.println(t.url);
			ids.addAll(s.getIdFromTopic(t));
		}
		quoueu = new UserQuoueu(ids);
		System.out.println("Accounts count is " + ids.size());
		System.out.println("Strating sending...");
		ExecutorService pool = Executors.newCachedThreadPool();
		pool.execute(captcha);
		for(Container a : accounts){
			AccountThread at = new AccountThread(a,quoueu,
					captcha, new Container(title, message));
			runningAccounts.add(at);
			pool.execute(at);
		}
		pool.execute(new InfoThread(runningAccounts, quoueu));
		pool.shutdown();

		//boolean running = false;
		/*while(!running){
			try{
			 running = pool.awaitTermination(Long.MAX_VALUE, TimeUnit.HOURS);
			} catch (Throwable t){
				running = false;
			}
		}
		
		captcha.interrupt();
		*/
		
		while(runningAccounts.size()>0){
			for(AccountThread t : runningAccounts){
				if(!t.running)
					runningAccounts.remove(t);
			}
			Thread.sleep(1000);
		}
		for(AccountThread t : runningAccounts){
			if(t.running)
				t.running = false;
		}
		System.out.println("Program stopped");
		
	}
	
}
