import java.io.IOException;
import java.util.Random;


public class AccountThread implements Runnable {

	public AccountThread(Container account, UserQuoueu q, 
							CaptchaQuoueu c, Container msg, Restriction r){
		//System.out.println("new thread");
		this.account = account;
		this.message = msg;
		quoueu = q;
		q.addListener(this);
		cQuoueu = c;
		restrictions = r;
		//System.out.println("Quit cinstructor");
	}
	
	@Override
	public void run() {
		SessionVK session = new SessionVK(account);
		boolean loggedin = false;
		if(restrictions.messages==0){
			running = false;
			return;
		}
		while(!loggedin && quoueu.isAvalible()){
			if(!captcha)
			if(session.connect()==-1){
				captcha = true;
				cQuoueu.add(this);
			} else {
				loggedin = true;
			}
		}
		String id = null;
		System.out.println("Starting sending @ " + account.first);
		boolean addtofriend = false;
		boolean dot = false;
		while(quoueu.isAvalible() && running && (restrictions.messages>-1 && messages_send>=restrictions.messages))
		{
			if(pause || captcha){
				try {
					Thread.sleep(2000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			} else if(null != (id = quoueu.take())){
				int res = 0;
				dot = !dot;
				if(!addtofriend)
					res = session.sendMessage(id, message.second+ (dot?".":"..") + 
									(char)(rand.nextInt()%(122-97) + 97), 
									message.first );
				else{ 
					res = session.addToFriends(id, message.second+ (dot?'.':".."));
					addtofriend = false;
					friends_added++;
				}
				
				count = (res==-2?count+1:0);
				
				try {
					Thread.sleep(1500);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				
				switch (res){
				case 0:
					messages_send++;
					break;
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
					if(restrictions.friends>-1 
							&& restrictions.friends>this.friends_added)
						addtofriend = true;
					else
						quoueu.put(id);
					break;
				}
			}
			if(restrictions.messages>-1 && messages_send>=restrictions.messages){
				running = false;
			}
		}
		running = false;
		System.out.println("stopped");
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
	private Random rand = new Random();
	private Restriction restrictions;
	public boolean running = true;
	private int messages_send = 0, friends_added = 0;

}
