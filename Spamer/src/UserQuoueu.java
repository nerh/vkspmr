import java.util.ArrayList;
import java.util.LinkedList;

public class UserQuoueu {

	public UserQuoueu(LinkedList<String> u){
		this.users = u;
		this.listeners = new ArrayList<AccountThread>();
	}
	
	public String take(){
		if(users.size()>0){
			this.blockedCount++;
			return users.pop();
		} else {
			return null;
		}
	}
	
	public void put(String u){
		if(this.blockedCount > 0){
			users.push(u);
			blockedCount--;
			if(users.size()==1){

			}
		}
	}
	
	public void fireListeners(){
		for(int i = 0; i<listeners.size(); i++){
			listeners.get(i).fire();
		}
	}
	
	public boolean isAvalible(){
		return users.size()>0 || blockedCount>0;
	}
	
	public void addListener(AccountThread l){
		listeners.add(l);
	}
	
	public void removeListener(AccountThread l){
		listeners.remove(l);
	}
	
	public LinkedList<String> getUsers() {
		return users;
	}

	private LinkedList<String> users;
	private ArrayList<AccountThread> listeners;
	private int blockedCount = 0; 
}
