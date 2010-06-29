import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.httpclient.methods.*;
import org.apache.commons.httpclient.*;

public class SessionVK implements Session {
	
	private String cookie = null;
	private Container account = null;
	@SuppressWarnings("restriction")
	private HttpClient httpClient = null;
	private Parser parser = null;
	private Logger logger = null;
	
	@SuppressWarnings("restriction")
	public SessionVK(Container account){
		this.account = account;
		httpClient = new HttpClient();
		parser = Parser.createParser();
		logger= Logger.createLogger();
		logger.redirectWarnings();
	}
	
	@SuppressWarnings("restriction")
	@Override
	public void connect() {
		String SID = null;
		String pg = "";
		PostMethod post = new PostMethod("http://login.vk.com/?act=login");
		post.addParameter("email", account.first);
		post.addParameter("pass", account.second);
		post.addParameter("expire", "");
		post.addParameter("vk", "");
		post.addRequestHeader("Cookie", "remixlang=3; remixchk=5");
		while(pg.length()<10){
		try {
			httpClient.executeMethod(post);
			pg=post.getResponseBodyAsString();
		} catch (HttpException e) {
			logger.printExceptionInfo(e);
		} catch (IOException e) {
			logger.printExceptionInfo(e);
		}
			//System.out.println(pg);
		}
		
		SID = parser.getSessionSid(pg);
		
		setCookie(SID);
		//System.out.println(SID);
		//System.out.println(this.cookie);
	}
	
	public LinkedList<String> getIdFromTopic(Topic topic){
		LinkedList<String> ids = new LinkedList<String>();
		ArrayList<String> from_page = null;
		String first_page = getPage(topic.url);
		int overall = parser.getPagesCountFromTopic(first_page);
		int end;
		int page;
		if(topic.start <1) page = 0;
		else page = topic.start - 1;
		
		if(topic.end<=page) end = ((overall/20)%1==0)?
							(overall/20)+1:(overall/20)+2;
		else end = topic.end;
		
		while(page<end){
			from_page = parser.getIdFromTopicPage(
					getTopicPage(topic, page));
			for(String id : from_page){
				if(!ids.contains(id))
					ids.add(id);
			}
			page++;
			try {
				Thread.sleep(1500);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		return ids;
	}
	
	public String getTopicPage(Topic topic, int page){
		String tid = "", oid = "";
		String topic_no = topic.url.substring(topic.url.indexOf('-'));
		tid = topic_no.substring(topic_no.indexOf('_')+1);
		oid = topic_no.substring(0, topic_no.indexOf('_'));
		return getPage("http://vkontakte.ru/board.php?act=a_get_posts_page&tid="
							+tid+"&oid="+oid+"&offset="+20*page);
	}
	
	public int postOnWall(String id,String message){
		String postData = null,
			   page = null,
			   result = null;

		try {
			message = URLEncoder.encode(message, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			logger.printExceptionInfo(e);
		}
		System.out.println(id);
		page = getPage("http://vkontakte.ru/id"+id);

		postData = preparePostOnWallParams(page,message);
		result = getPage("http://vkontakte.ru/wall.php?"+postData);
		
		//System.out.println(result);
		if(result!=null && result.contains("captcha_sid")){		
			return -2;
		}
		return 0; //TODO
	}
	
	public int addToFriends(String id, String message){
		String my_page = getFriendsPage();
		String hash = parser.getHash(my_page);
		try {
			message = URLEncoder.encode(message, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			logger.printExceptionInfo(e);
		}
		String postData = "act=accept_friend&fid="+id+"&hash="+hash+
							"&message="+message;
		String res = getPage("http://vkontakte.ru/friends_ajax.php?"+postData);
		if(res==null) return -3;
		if(res.contains("capthca_sid")) return -2;
		return 0;
	}
	
	public int sendMessage(String id, String message, String title){
		//TODO
		String postData = null,
		page = null,
		result = null;
		try {
			message = URLEncoder.encode(message, "UTF-8");
			title = URLEncoder.encode(title, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			logger.printExceptionInfo(e);
		}
		
		page = " ";
		while(page.length()<25 || page.contains("В Контакте | Ошибка")){
			page = getPage("http://vkontakte.ru/mail.php?act=write&to="+id);
			if(page.contains("Вы не можете отправить сообщение данному пользователю"))
				return -4;
			if(page.contains("В Контакте | Ошибка")){
				//System.out.println("Timeout");
				try {
					Thread.sleep(10000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		
		postData = prepareSendParamsToId(page,id,title,message);
		//System.out.println(postData);
		result = getPage("http://vkontakte.ru/mail.php?"+postData);
		if(result==null) return -3;
		if(result!=null && result.contains("captcha_sid")){		
			return -2;
		}
		return 0;
	}
	
	public int sendToFriends(List<String> ids, String message, String title){
		String postData = null,
		page = null,
		result = null;

		try {
			message = URLEncoder.encode(message, "UTF-8");
			title = URLEncoder.encode(title, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			logger.printExceptionInfo(e);
		}
		page = " ";
		while(page.length()<25 || page.contains("В Контакте | Ошибка")){
			page = getPage("http://vkontakte.ru/mail.php?act=write");
			if(page.contains("В Контакте | Ошибка")){
				System.out.println("Timeout");
				try {
					Thread.sleep(10000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		//System.err.println("\n\n\n-------------------------------------");
		//System.err.println(page);
		//System.err.println("-------------------------------------\n\n\n");
		postData = prepareSendParamsToFriends(page,ids,title,message);
		//System.out.println(postData);
		result = getPage("http://vkontakte.ru/mail.php?"+postData);
		if(result==null) return -3;
		if(result!=null && result.contains("captcha_sid")){		
			return -2;
		}
		return 0;
	}
	
	private String prepareSendParamsToId(String page, String id,
			String message, String title){
		String clearChas = null,
				chas = null;
		StringBuilder params = new StringBuilder();
		clearChas = parser.getChas(page);
		chas = Decoder.decodeWallHash(clearChas);
		params.append("act=sent&ajax=1&misc=1&secure=")
		  .append("&chas=")
		  .append(chas)
		  .append("&photo=")
		  .append("&to_id=")
		  .append(id)
		  .append("&to_reply=0")
		  .append("&title=")
		  .append(title)
		  .append("&message=")
		  .append(message);
		return params.toString();
	}
	
	private String prepareSendParamsToFriends(String page, List<String> ids,
										String title, String message){
		//TODO
		String clearChas = null,
				chas = null;
		StringBuilder params = new StringBuilder();
		StringBuilder targets = new StringBuilder();
		clearChas = parser.getChas(page);
		//System.out.println(clearChas);
		chas = Decoder.decodeWallHash(clearChas);
		for(String id : ids){
			id = id.replaceAll("\"", "");
			targets.append(id)
				   .append(",");
		}
		if(targets.length()>0) targets.deleteCharAt(targets.length()-1);
		
		params.append("act=sent&ajax=1&misc=1&secure=")
			  .append("&chas=")
			  .append(chas)
			  .append("&photo=")
			  .append("&to_id=")
			  .append(targets.toString())
			  .append("&to_reply=0&toFriends=1")
			  .append("&title=")
			  .append(title)
			  .append("&message=")
			  .append(message)
			  .append("&to_ids=")
			  .append(targets.toString());
		return params.toString();
	}
	
	public String getFriendsPage(){
		return getPage("http://vkontakte.ru/friends.php");
	}
	
	private String preparePostOnWallParams(String pageContent, String message){
		String clearHash = null,
			   toId = null,
			   wallHash = null;
		StringBuilder params = new StringBuilder();
		clearHash = parser.getWallHash(pageContent);
		wallHash = Decoder.decodeWallHash(clearHash);
		toId = parser.getToIdValue(pageContent);
		
		params.append("act=get10&wall_hash=")
			  .append(wallHash)
			  .append("&wpage=100000&seed=")
			  .append(message)
			  .append("&fpage=1&mid=")
			  .append(toId)
			  .append("&n=3");
		
		return params.toString();
	}
	
	@SuppressWarnings("restriction")
	private String getPage(String url) {
		String result = null;
		GetMethod get = null;
		try{
			get = new GetMethod(url);
		} catch (Exception e){
			return null;
		}
		get.addRequestHeader("Cookie", cookie);
		try {
			httpClient.executeMethod(get);
		} catch (HttpException e) {
			logger.printExceptionInfo(e);
		} catch (IOException e) {
			logger.printExceptionInfo(e);
		}
		try {
			result = get.getResponseBodyAsString();
		} catch (IOException e) {
			logger.printExceptionInfo(e);
		}
		
		return result;
	}
	
	private void setCookie(String SID){
		StringBuilder strBuild = new StringBuilder();
		strBuild.append("remixchk=5; ")
				.append("remixnews_privacy_filter=5; ")
				.append("remixclosed_tabs=0; ")
				.append("remixsid=")
				.append(SID);
		cookie=strBuild.toString();
	}
	
	/*public static void main(String[] arg){
		Container c = new Container();
		c.first="drunkfil@mail.ru";
		c.second="nf~vLwj9";
		SessionVK svk = new SessionVK(c);
		svk.connect();
		Parser p = Parser.createParser();
		svk.postOnWall("17650760","балблабла");//TODO сообщение преобразовать в нужную кодировку
	}*/

}
