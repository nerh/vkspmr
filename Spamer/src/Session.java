
public interface Session {
	
	public int connect();
	public int addToFriends(String id,String msg);
	public int sendMessage(String id, String title, String msg);
}
