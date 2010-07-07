import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class CaptchaQuoueu implements Runnable{

	public CaptchaQuoueu(){
		accounts = new LinkedList<AccountThread>();
		e = Executors.newFixedThreadPool(1);
		System.out.println("handler");
	}
	
	public void add(AccountThread a){
		this.accounts.add(a);
		System.out.println("added");
	}
	
	@Override
	public void run() {
		running = true;
		while(running){
			if(accounts.size()>0){
				AccountThread a = accounts.pop();
				captchaHandler(a);
				a.fireCaptcha();
			}
		}
	}
	
	public void interrupt(){
		running = false;
	}
	
	private void captchaHandler(AccountThread account){
		System.out.println("------------------");
		System.out.println("Captcha detected.");
		System.out.println("Login: " + account.getAccount().first);
		System.out.println("Pass: " + account.getAccount().second);
		try {
			System.in.read();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private boolean running = true;
	private ExecutorService e;
	private LinkedList<AccountThread> accounts;
	
}
