package eu.janinko.xmppmuc;

public class Message {
	private org.jivesoftware.smack.packet.Message message;
	
	public Message(org.jivesoftware.smack.packet.Message msg ){
		message = msg;
	}
	
	public org.jivesoftware.smack.packet.Message getSmackMessage(){
		return message;
	}
	
	public String getNick(){
		return message.getFrom().split("/")[1];
	}
	

}
