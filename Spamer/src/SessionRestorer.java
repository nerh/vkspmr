import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SessionRestorer {
	
	private BufferedReader reader;
	private BufferedWriter writer;
	private int mode = 0;
	private String file = null;
	
	public static int READ = 0;
	public static int WRITE = 1;
	
	public void open(String path, int mode) throws IOException{
		this.mode = mode;
		if(mode == READ)
			reader = new BufferedReader(new FileReader(path));
		else if(mode == WRITE){
			writer = new BufferedWriter(new FileWriter(path));
		}
	}
	
	public void close() throws IOException{
		try {
			if(mode == READ)
				reader.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(mode ==WRITE)
			writer.close();
	}
	
	public void write(List<String> users) throws IOException{
		if(mode==WRITE){
			writer.write(users.toString());
		}
	}
	
	public List<String> read() throws IOException{
		if(this.mode == WRITE) return null;
		int c;
		StringBuilder strBuild = new StringBuilder();
		while((c = reader.read()) != -1){
			strBuild.append((char)c);
		}
		file = strBuild.toString();
		file = file.replaceAll("[\\[\\]]", "");
		ArrayList<String> list = new ArrayList<String>();
		String[] ids = file.split(", ");
		for(String id : ids){
			list.add(id);
		}
		return list;
	}
}
