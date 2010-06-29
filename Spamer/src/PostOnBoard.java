import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.LinkedList;


public class PostOnBoard {

	public static void main(String[] args) {
		Logger logger = Logger.createLogger();
		ConfigReader confReader = null;
		String message = null;
		Parser parser = Parser.createParser();
		LinkedList<Container> accounts = null;
		try {
			confReader = ConfigReader.createConfigReader("board.xml");
		} catch (FileNotFoundException e) {
			logger.printExceptionInfo(e);
		} catch (IOException e) {
			logger.printExceptionInfo(e);
		}
		
		
		message = confReader.getMessage();
		accounts = confReader.getAccountList();
		
		for(Container acc : accounts){
			SessionVK ses = new SessionVK(acc);
			LinkedList<String> friends = null;
			ses.connect();
			friends = (LinkedList<String>) parser.getFriendsIDs(ses.getFriendsPage());
			for(String id : friends){
				id = id.replaceAll("\"", "");
				int res = -1;
				while(res!=0){
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					res = ses.postOnWall(id, message); //TODO
					if(res == -2){
						System.out.println("------------------");
						System.out.println("Captcha detected.");
						System.out.println("Login: " + acc.first);
						System.out.println("Pass: " + acc.second);
						try {
							System.in.read();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						//TODO: captcha handler
					}
				}
			}
		}

	}

}
