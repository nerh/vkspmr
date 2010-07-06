import java.util.ArrayList;


public class InfoThread implements Runnable {

	public InfoThread(ArrayList<AccountThread> accs, UserQuoueu users){
		a = accs;
		u = users;
	}
	
	@Override
	public void run() {
		while(true){
			System.out.println("Active accounts: " + a.size());
			System.out.println("Users waiting their messages: " + 
					u.getUsers().size());
			try {
				Thread.sleep(10000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}
	
	private ArrayList<AccountThread> a;
	private UserQuoueu u;

}
