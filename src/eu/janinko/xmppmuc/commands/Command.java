package eu.janinko.xmppmuc.commands;

import org.jivesoftware.smack.packet.Message;

import eu.janinko.xmppmuc.MucCommands;

public interface Command {
	
	Command build(MucCommands mucCommands) throws PluginBuildException;
	
	String getCommand();
	
	void handle(Message m);

	String help(String prefix);
	
	int getPrivLevel();
	
	void destroy();
}
