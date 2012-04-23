package eu.janinko.xmppmuc.commands;

import org.jivesoftware.smack.packet.Message;

public interface MessageCommand {
	
	void handleMessage(Message m);

}
