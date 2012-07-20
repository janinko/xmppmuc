package eu.janinko.xmppmuc;

import org.jivesoftware.smack.packet.Packet;

public class Helper {
	
	

	public static String implode(String[] args){
		return implode(args," ",0);
	}
	
	public static String implode(String[] args, int start){
		return implode(args," ",start);
	}
	
	public static String implode(String[] args, String delimiter){
		return implode(args,delimiter,0);
	}
	
	public static String implode(String[] args, String delimiter, int start) {
		StringBuilder sb = new StringBuilder();
		for(int i=start; i < args.length; i++){
			if(i != start){
				sb.append(delimiter);
			}
			sb.append(args[i]);
		}
		return sb.toString();
	}

	public static String getNick(Packet p) {
		return p.getFrom().split("/")[1];
	}
	
	
}
