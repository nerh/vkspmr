import java.io.IOException;


public class AccountThread implements Runnable {

	public AccountThread(Container account, UserQuoueu q, Container msg){
		System.out.println("new thread");
		this.account = account;
		this.message = msg;
		quoueu = q;
		q.addListener(this);
	}
	
	@Override
	public void run() {
		SessionVK session = new SessionVK(account);
		String id = null;
		System.out.println("Starting sending @ " + account.first);
		boolean addtofriend = false;
		while(quoueu.isAvalible()){
			if(pause){
				try {
					Thread.sleep(2000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			} else if(null != (id = quoueu.take())){
				int res = 0;
				if(!addtofriend)
					res = session.sendMessage(id, message.second, 
						message.first);
				else{ 
					res = session.addToFriends(id, message.second);
					addtofriend = false;
				}
				
				count = (res==-2?count+1:0);
				
				try {
					Thread.sleep(1500);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				
				switch (res){
				case -2:
					quoueu.put(id);
					if(count>5) capchaHandler();
					break;
				case -3:
					quoueu.put(id);
					timeout();
					break;
				case -4:
					addtofriend = true;
					break;
				}
			}
		}
	}
	
	public void fire(){
		pause = false;
	}
	
	private void capchaHandler(){
		System.out.println("------------------");
		System.out.println("Captcha detected.");
		System.out.println("Login: " + account.first);
		System.out.println("Pass: " + account.second);
		try {
			System.in.read();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void timeout(){
		try {
			//System.out.println("Timeout");
			Thread.sleep(10000);
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
	}
	
	private UserQuoueu quoueu;
	private Container account;
	private Container message;
	private Boolean pause = false;
	private int count = 0;

}
