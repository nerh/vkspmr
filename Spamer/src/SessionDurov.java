import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;


public class SessionDurov implements Session {
	
	private String sid = null;
	private Container account = null;
	@SuppressWarnings("restriction")
	private HttpClient httpClient = null;
	private Parser parser = null;
	private Logger logger = null;
	
	@SuppressWarnings("restriction")
	public SessionDurov(Container account){
		this.account = account;
		httpClient = new HttpClient();
		parser = Parser.createParser();
		logger= Logger.createLogger();
		logger.redirectWarnings();
	}
	
	@Override
	public int connect() {
		PostMethod p = new PostMethod("http://login.userapi.com/auth");
		p.addParameter("site", "2");
		p.addParameter("id","0");
		p.addParameter("fccode", "0");
		p.addParameter("fcsid", "0");
		p.addParameter("login","force");
		p.addParameter("email",account.first);
		p.addParameter("pass",account.second);
		
		try {
			httpClient.executeMethod(p);
			for(Header h : p.getRequestHeaders()){
				System.out.println(h.getName());
			}
		} catch (HttpException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		this.sid = this.getSid(p.getRequestHeader("Location").getValue());
		
		p = new PostMethod("http://durov.ru");
		p.addParameter("Coookie","sid="+this.sid);
		try {
			httpClient.executeMethod(p);
			System.out.println(p.getResponseBodyAsString());
		} catch (HttpException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return 0;
	}

	@Override
	public int addToFriends(String id, String msg) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int sendMessage(String id, String title, String msg) {
		/*try {
			msg = URLEncoder.encode(msg, "UTF-8");
			title = URLEncoder.encode(title, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			logger.printExceptionInfo(e);
		}
		StringBuilder url = new StringBuilder("http://userapi.com/data?");
		url.append("&act=add_message")
		   .append("&id")
		GetMethod g = new GetMethod();*/
		return 0;
	}
	
	protected String getSid(String location){
		int s_from = parser.getNextTagPos(location, "sid=", 0) + 
								"sid=".length();
		return location.substring(s_from);
	}

}
