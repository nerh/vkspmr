import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.StringTokenizer;

/*
 * Класс-парсер, реализует шаблон singleton
 */
public class Parser {

	private static Parser single = null;
	
	public static Parser createParser(){
		if(single==null){
			single = new Parser();
		}
		return single;
	}
	
	private Parser(){}
	
	/*
	 * Возвращает список групп из которых нужно брать id пользователей
	 */
	public LinkedList<Topic> getTargetList(String source){
		LinkedList<Topic> topics = new LinkedList<Topic>();
		int s_pos = 0, e_pos = 0, res;
		while((res = getNextTagPos(source,"<list>",e_pos)) != -1){
			s_pos = res + "<list>".length();
			e_pos = getNextTagPos(source,"</list>",s_pos);
			topics.add(getSingleTopic(source.substring(s_pos,e_pos)));
		}
		
		return topics;
	}
	
	/*
	 * Возвращает список встреч на которые нужно приглашать
	 */
	public LinkedList<Container> getPartiesList(String source){
		return null;
	}
	
	//TODO: найти косяки
	/*
	 * Возвращает список аккаунтов с которых вести рассылку
	 */
	public LinkedList<Container> getAccountList(String source){
		LinkedList<Container> accounts = new LinkedList<Container>();
		String userTag = null;
		int s_pos=0,e_pos=0,res;
		while((res = getNextTagPos(source,"<user>",e_pos)) != -1){
			s_pos = res + "<user>".length();
			e_pos = getNextTagPos(source,"</user>",s_pos);
			userTag = source.substring(s_pos, e_pos);
			accounts.add(getSingleAccount(userTag));
		}
		
		return accounts;
	}
	
	/*
	 * Возвращает черный спиок (id которым слать ничего не надо)
	 */
	public LinkedList<String> getBlackList(String source){
		LinkedList<String> blacks = new LinkedList<String>();
		String userTag = null;
		int s_pos=0,e_pos=0,res;
		while((res = getNextTagPos(source,"<black>",e_pos)) != -1){
			s_pos = res + "<black>".length();
			e_pos = getNextTagPos(source,"</black>",s_pos);
			userTag = source.substring(s_pos, e_pos);
			userTag.replaceAll("[^0-9]", "");
			blacks.add(userTag);
		}
		
		return blacks;
	}
	
	/*
	 * Возвращает заголовок сообщения
	 */
	public String getTitle(String source){

		return getXMLNodeValue(source,"title");

	}
	
	/*
	 * Возвращает текст сообщения
	 */
	public String getMessage(String source){
		
		return getXMLNodeValue(source,"message");
		
	}
	
	/*
	 * Возвращает sid, подключаемый к куки.
	 * Входной параметр - содержимое страницы login.vk.com/?cat=login
	 */
	public String getSessionSid(String source){
		int s_pos=0, e_pos =0;
		String SID = null,
				openTag = "<input type='hidden' name='s' value='",
				closeTag = "' />";
		s_pos = getNextTagPos(source,openTag,0) + openTag.length();
		e_pos = getNextTagPos(source,closeTag,s_pos);
		SID = source.substring(s_pos, e_pos);
		return SID;
	}
	
	public List<String> getFriendsIDs(String source){
		LinkedList<String> ids = new LinkedList<String>();
		String idList = null;
		StringTokenizer strTok = null;
		int s_pos, e_pos;
		
		s_pos = getNextTagPos(source,"var friendCats = {",0) + 
								"var friendCats = {".length();
		e_pos = getNextTagPos(source,"};",s_pos);
		idList = source.substring(s_pos, e_pos);
		idList.replaceAll("\"", "");
		strTok = new StringTokenizer(idList,":,");
		while(strTok.hasMoreTokens()){
			String tok = strTok.nextToken();
			if(0 != tok.compareTo("1")){
				ids.add(tok);
			}
		}
		return ids;
	}
	
	/*
	 * Возвращает "чистый" wall-hash
	 */
	public String getWallHash(String source){
		String hash = null;
		int s_pos,e_pos;
		if(source!=null)
		{
		s_pos = getNextTagPos(source,"decodehash('",0) + 
								"decodehash('".length();
		e_pos = getNextTagPos(source,"');",s_pos);
		if(s_pos!=-1&&e_pos!=-1)
			hash = source.substring(s_pos, e_pos);
		}
		return hash;
	}
	
	public String getChas(String source){
		String chas = null;
		int s_pos, e_pos;
		s_pos = getNextTagPos(source,"id=\"chas\" name=\"chas\" value=\"",0);
		s_pos=s_pos+"id=\"chas\" name=\"chas\" value=\"".length();
		e_pos = getNextTagPos(source,"\"",s_pos);
		chas = source.substring(s_pos, e_pos);
		return chas;
	}
	
	public String getToIdValue(String source){
		String toId = null;
		int s_pos, e_pos;
		
		s_pos = getNextTagPos(source,"<input type=\"hidden\" id=\"to_id\" " +
					"name=\"to_id\" value=\"",0) + 
					"<input type=\"hidden\" id=\"to_id\" name=\"to_id\" value=\"".length();
		e_pos = getNextTagPos(source,"\"/>",s_pos);
		
		toId = source.substring(s_pos, e_pos);
		
		return toId;
	}
	
	public LinkedList<String> clearFriends(LinkedList<String> f){
		for(int i = 0; i<f.size(); ){
			if(!f.get(i).contains("\"")){
				f.remove(i);
			} else {
				i++;
			}
		}
		return f;
	}
	
	public String getHash(String page){
		int s_pos = 0, e_pos;
		s_pos = getNextTagPos(page,"var friendsData = {",s_pos)
					+"var friendsData = {".length();
		s_pos = getNextTagPos(page,"'hash':'",s_pos)
					+"'hash':'".length();
		e_pos = getNextTagPos(page,"'",s_pos);
		
		return page.substring(s_pos, e_pos);
	}
	
	public int getPagesCountFromTopic(String first_page){
		int s_pos = getNextTagPos(first_page, 
				"<div id=\"summary\" class=\"summary\">",0) 
				+ "<div id=\"summary\" class=\"summary\">".length();
		int e_pos = getNextTagPos(first_page, "</div>", s_pos);
		String summary = first_page.substring(s_pos,e_pos);
		s_pos = getNextTagPos(summary, "В теме ", 0) + "В теме ".length();
		e_pos = getNextTagPos(summary, " ", s_pos);
		return new Integer(summary.substring(s_pos, e_pos));
	}
	
	public ArrayList<String> getIdFromTopicPage(String source){
		ArrayList<String> ids = new ArrayList<String>();
		int s_pos = 0, e_pos = 0, res;
		while((res = getNextTagPos(source,"<span class=\\\"postAuthor\\\"><a href=\\\"",e_pos)) != -1){
			s_pos = res + "<span class=\\\"postAuthor\\\"><a href=\\\"".length();
			e_pos = getNextTagPos(source,"\\\">",s_pos);
			ids.add(source.substring(s_pos,e_pos).replaceAll("id", ""));
		}
		return ids;
	}
	
	/*
	 * Возвращает содержимое xml узла
	 */
	private String getXMLNodeValue(String source, String tag){
		String value = null,
			   closeTag = "</" + tag + ">",
			   openTag = '<' + tag + '>';
		int s_pos = 0,e_pos = 0;	
		s_pos = getNextTagPos(source,openTag,0) + openTag.length();
		e_pos = getNextTagPos(source,closeTag,s_pos);
		value = source.substring(s_pos,e_pos);
		return value;
	}
	
	/*Метод возвращает номер символа из строки source с которого начинается
	 * подстрока tag. Поиск подстроки ведется начиная с символа start.
	 * Если подстрока не найдена - функция вернет -1.
	 */
	public int getNextTagPos(String source, String tag, int start_from){
		boolean flag = true;
		int i = start_from,s_pos=0;
		if(source==null) return -1;
		for(;flag && i<source.length()-tag.length();i++){
			if(source.charAt(i)==tag.charAt(0) &&
				(source.charAt(i+tag.length()-1)==
					tag.charAt(tag.length()-1))){
				if(source.substring(i, i+tag.length()).
						compareTo(tag)==0){
					flag = false;
					s_pos = i;
				}
			}
		}
		return (flag ? -1 : s_pos);
	}
	
	//TODO: найти косяки
	/*
	 * Возвращает контейнер с информацией об аккаунте.
	 * В качестве параметра должна идти строка с содержанием 
	 * узла <user></user>
	 */
	private Container getSingleAccount(String source){
		Container account = new Container();
		int s_pos=0,e_pos=0;
		String email=null,pass=null;
		
		s_pos = getNextTagPos(source,"<login>",0) + "<login>".length();
		e_pos = getNextTagPos(source,"</login>",s_pos);
		email=source.substring(s_pos,e_pos);
		
		s_pos = getNextTagPos(source,"<pass>",0) + "<pass>".length();
		e_pos = getNextTagPos(source,"</pass>",s_pos);
		pass=source.substring(s_pos,e_pos);
		
		account.first = email;
		account.second = pass;
		
		return account;
	}
	
	private Topic getSingleTopic(String source){
		Topic topic = new Topic();
		int s_pos=0,e_pos=0;
		int start = 0, end = 0;
		String url = null;
		
		s_pos = getNextTagPos(source,"<url>",0) + "<url>".length();
		e_pos = getNextTagPos(source,"</url>",s_pos);
		url=source.substring(s_pos,e_pos);
		
		s_pos = getNextTagPos(source,"<start>",0) + "<start>".length();
		e_pos = getNextTagPos(source,"</start>",s_pos);
		try{
			start = new Integer(source.substring(s_pos,e_pos));
		} catch (Throwable e){}
		
		s_pos = getNextTagPos(source,"<end>",0) + "<end>".length();
		e_pos = getNextTagPos(source,"</end>",s_pos);
		try{
			end=new Integer(source.substring(s_pos,e_pos));
		} catch (Throwable e){}
		topic.url = url;
		topic.start = start;
		topic.end = end;
		
		return topic;
	}
	
	/*public static void main(String arg[]){
		Parser p = new Parser();
		System.out.println("start");
		System.out.println(p.getXMLNodeValue(
				"hui<url>http://vkontakte.ru/topic-12586_21955707</url>hui",
				"url"));
	}*/
}
