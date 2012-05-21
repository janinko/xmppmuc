package eu.janinko.xmppmuc.commands;

import org.jivesoftware.smack.packet.Message;

import eu.janinko.xmppmuc.CommandWrapper;

public interface Command {

	Command build(CommandWrapper commandWrapper) throws PluginBuildException;
	
	String getCommand();
	
	void handle(Message m, String[] args);

	String help(String prefix);
	
	int getPrivLevel();
	
	void destroy();

}
