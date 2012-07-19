package eu.janinko.xmppmuc.commands;

import eu.janinko.xmppmuc.Message;

public interface MessageCommand {
	
	void handleMessage(Message m);

}
