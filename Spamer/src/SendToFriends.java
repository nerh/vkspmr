import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;


public class SendToFriends {

	public static void main(String[] args) {
		Logger logger = Logger.createLogger();
		ConfigReader confReader = null;
		String message = null;
		String title = null;
		Parser parser = Parser.createParser();
		LinkedList<Container> accounts = null;
		LinkedList<Container> blacks = null;
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
		blacks = confReader.getAccountList();
		
		for(Container acc : accounts){
			System.out.println("Starting on " + acc.first);
			SessionVK ses = new SessionVK(acc);
			LinkedList<String> friends = null;
			int res;
			while(-1 == (res = ses.connect())){
				System.out.println("------------------");
				System.out.println("Captcha detected.");
				System.out.println("Login: " + acc.first);
				System.out.println("Pass: " + acc.second);
				try {
					System.in.read();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			String frPg = " ";
			while(frPg.length()<100){
				frPg = ses.getFriendsPage();
				//System.out.println(frPg);
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			friends = (LinkedList<String>) parser.getFriendsIDs(frPg);
			friends = parser.clearFriends(friends);
			for(String friend : friends){
				if(blacks.contains(friend.replaceAll("[^0-9]", "0"))){
					friends.remove(friend);
				}
			}
			double all = friends.size();
			double done = 0;
			boolean ch = true;
			for(int i = 0; i<friends.size()/10 || (friends.size() < 10 && i<1); i++)
			{
				ch = !ch;
				int count = 0;
				res = -1;
				while(res!=0){
					

						int s, en;
						s = i*10;
						en = (i+1)*10 > friends.size()-1 ? friends.size() : (i+1)*10;
						//System.out.println(friends.subList(s,en));
					res = ses.sendToFriends(friends.subList(s,en), message + (ch ? "\n." : "\n.."), title);
					//res = 0;
				//	System.out.println(friends);
						count = (res==-2? count+1 : 0);
					try {
						Thread.sleep(1500);
						if(res==-3){
							System.out.println("Timeout");
							Thread.sleep(10000);
						}
					} catch (InterruptedException e1) {
						e1.printStackTrace();
					}

					if(res == -2 && count > 5){
						System.out.println("------------------");
						System.out.println("Captcha detected.");
						System.out.println("Login: " + acc.first);
						System.out.println("Pass: " + acc.second);
						try {
							System.in.read();
						} catch (IOException e) {
							e.printStackTrace();
						}
						//TODO: captcha handler
					}
					
					}
					done+=10;
				System.out.println((100/all)*done + " % done");
				}
			
			System.out.println("All messages from " + acc.first +  " were sended.");
		}

	}

}

