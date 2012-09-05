package eu.janinko.xmppmuc;

import org.apache.log4j.Logger;
import org.jivesoftware.smack.Chat;

public class Message {
	private org.jivesoftware.smack.packet.Message message;
	private Commands commands;

	private static Logger logger = Logger.getLogger(Message.class);

	public Message(org.jivesoftware.smack.packet.Message msg, Commands cmd ){
		message = msg;
		commands = cmd;
	}

	public org.jivesoftware.smack.packet.Message getSmackMessage(){
		return message;
	}

	public String getNick(){
		return message.getFrom().split("/")[1];
	}

	public Chat getTarget(){
		try{
			logger.trace(message.getThread());
			logger.trace(commands.getConnection().connection.getChatManager().getThreadChat(message.getThread()));
			logger.trace(commands.getConnection().connection.getChatManager().getThreadChat(message.getThread()).getParticipant());
			return commands.getConnection().connection.getChatManager().getThreadChat(message.getThread());
		}catch(Exception e){
			logger.warn("Exception cought", e);
			return null;
		}
	}
	

}
