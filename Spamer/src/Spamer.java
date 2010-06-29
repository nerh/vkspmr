import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Spamer {
	public static void main(String args[]) throws InterruptedException{
		Logger logger = Logger.createLogger();
		ConfigReader confReader = null;
		String message = null;
		String title = null;
		Parser parser = Parser.createParser();
		LinkedList<Container> accounts = null;
		LinkedList<Topic> topics = null;
		UserQuoueu quoueu = null;
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
		ArrayList<String> ids = new ArrayList<String>();
		for(Topic t : topics)
			ids.addAll(s.getIdFromTopic(t));
		System.out.println("Strating sending...");
		ExecutorService pool = Executors.newCachedThreadPool();
		for(Container a : accounts){
			pool.execute(new AccountThread(a,quoueu, new Container(title, message)));
		}
		pool.shutdown();
		System.out.println("Program stopped");
		
	}
	
}
