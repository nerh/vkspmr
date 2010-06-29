import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedList;


public class ConfigReader {
	private static ConfigReader single = null;
	
	private static String configFile = null;
	private static String path = null;
	private static boolean opened = false;
	private static BufferedReader input = null;
	private static Parser parser = Parser.createParser();
	
	private ConfigReader(){}
	
	public static ConfigReader createConfigReader(String file) 
		throws FileNotFoundException,IOException{
		if(single==null){
			single = new ConfigReader();
		}
		if(!opened){
			path = file;
			input = new BufferedReader(new FileReader(path));
			single.readFile();
			opened = true;
		}
		return single;
	}
	
	private void readFile() throws IOException{
		int c;
		StringBuilder strBuild = new StringBuilder();
		while((c = input.read()) != -1){
			strBuild.append((char)c);
		}
		configFile = strBuild.toString();
	}
	
	public LinkedList<Topic> getTargetList(){
		return parser.getTargetList(configFile);
	}
	
	public LinkedList<Container> getPartiesList(){
		return parser.getPartiesList(configFile);
	}
	public LinkedList<Container> getAccountList(){
		return parser.getAccountList(configFile);
	}
	public LinkedList<String> getBlackList(){
		return parser.getBlackList(configFile);
	}
	
	public String getTitle(){
		return parser.getTitle(configFile);
	}
	
	public String getMessage(){
		return parser.getMessage(configFile);
	}
	
}
