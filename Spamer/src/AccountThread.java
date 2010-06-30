import java.io.IOException;


public class AccountThread implements Runnable {

	public AccountThread(Container account, UserQuoueu q, 
							CaptchaQuoueu c, Container msg){
		System.out.println("new thread");
		this.account = account;
		this.message = msg;
		quoueu = q;
		q.addListener(this);
		cQuoueu = c;
	}
	
	@Override
	public void run() {
		SessionVK session = new SessionVK(account);
		String id = null;
		System.out.println("Starting sending @ " + account.first);
		boolean addtofriend = false;
		while(quoueu.isAvalible()){
			if(pause || captcha){
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
					if(count>5){
						captcha = true;
						cQuoueu.add(this);
					}
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
	
	public void fireCaptcha(){
		captcha = false;
	}
	
	public Container getAccount() {
		return account;
	}

	public void setAccount(Container account) {
		this.account = account;
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
	private CaptchaQuoueu cQuoueu;
	private Container account;
	private Container message;
	private Boolean pause = false;
	private Boolean captcha = false;
	private int count = 0;

}
