
public class Decoder {
	private static Decoder single = null;
	
	private Decoder(){};
	
	public static Decoder createDecoder(){
		if(single==null) single = new Decoder();
		return single;
	}
	
	public static String decodeWallHash(String clearHash){
		StringBuilder decodedHash = new StringBuilder();
		decodedHash.append(clearHash.substring(clearHash.length()-5))
				   .append(clearHash.substring(4,clearHash.length()-8));
		return revertString(decodedHash.toString());
	}
	
	private static String revertString(String source){
		StringBuilder strBuilder = new StringBuilder();
		for(int i = 0; i<source.length(); i++){
			strBuilder.append(source.charAt(source.length()-i-1));
		}
		return strBuilder.toString();
	}
}
