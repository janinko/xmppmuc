package eu.janinko.xmppmuc.commands;


import eu.janinko.xmppmuc.CommandWrapper;
import eu.janinko.xmppmuc.Message;

public interface Command {

	Command build(CommandWrapper commandWrapper) throws PluginBuildException;
	
	String getCommand();
	
	void handle(Message m, String[] args);

	String help(String prefix);
	
	int getPrivLevel();
	
	void destroy();

	void connected();

	void disconnected();

}
