import java.util.ArrayList;


public class InfoThread implements Runnable {

	public InfoThread(ArrayList<AccountThread> accs, UserQuoueu users){
		a = accs;
		u = users;
	}
	
	@Override
	public void run() {
		int aa = 1;
		while(aa>0){
			aa = 0;
			for(AccountThread ac : a){
				if(ac.running){
					aa++;
				}
			}
			System.out.println("Active accounts: " + aa);
			System.out.println("Users waiting their messages: " + 
					u.getUsers().size());
			try {
				Thread.sleep(10000);
			} catch (InterruptedException e) {
			}
		}

	}
	
	private ArrayList<AccountThread> a;
	private UserQuoueu u;

}
