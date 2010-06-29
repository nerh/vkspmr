
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Logger {
	
	private final String warnings = "wrng.log";
	private final String exception = "excp.log";
	private PrintStream warn = null;
	private BufferedWriter excp = null;
	@SuppressWarnings("unused")
	private SimpleDateFormat dateFormat = null;
	private Date date = null;
	private static Logger single = null;
	
	
	
	private Logger(){
		dateFormat = new SimpleDateFormat("dd.MM.yyyy hh:mm:ss");
		date = new Date();
	}
	
	public static Logger createLogger(){
		if(single == null) single = new Logger();
		return single;
	}
	
	public void redirectWarnings(){
		File warnFile = new File(warnings);	
		try {
			warnFile.createNewFile();
		} catch (IOException e1) {
			System.err.println("Cant' open warnings file");
			e1.printStackTrace();
		}
		try {
			warn = new PrintStream(warnFile);
		} catch (FileNotFoundException e) {
			System.err.println("Cant' open warnings file");
			e.printStackTrace();
		}
		System.setErr(warn);
		//System.setOut(warn);
	}
	
	public void printExceptionInfo(Exception e){
		if(excp==null) initExcp();
		try {
			excp.write(date.toString() + "\t" + e.toString() + "\n");
			excp.flush();
		} catch (IOException e1) {
			System.err.println("Can't write to exception file");
			e1.printStackTrace();
		}
	}
	
	private void initExcp(){
		try {
			excp = new BufferedWriter(new FileWriter(exception));
		} catch (IOException e) {
			System.err.println("Can't init exception file");
			e.printStackTrace();
		}
	}
	
}
