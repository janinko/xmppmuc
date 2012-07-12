package eu.janinko.xmppmuc;

import java.io.File;

import org.jivesoftware.smack.packet.Message;

import eu.janinko.xmppmuc.commands.Command;
import eu.janinko.xmppmuc.commands.PluginBuildException;

/** Middle layer between Command and MucCommands,
 * providing helpful functionality for Command.
 * @author janinko
 *
 */
public class CommandWrapper {
	private Commands commands;
	Command command;
	
	public CommandWrapper(Command c, Commands commands)  throws PluginBuildException{
		this.commands = commands;
		command = c;
		command = c.build(this);
	}

	public Commands getCommands(){
		return commands;
	}

	public void sendMessage(String message) {
		commands.getConnection().sendMessage(message);
	}
	
	public void sendMessage(Message message) {
		commands.getConnection().sendsMessage(message);
	}
	
	public File getConfigFile(){
		return new File(commands.getPlugins().pluginDir
				       + command.getClass().getSimpleName()
				       + ".conf");
	}


	
	//public String hGetCommand(Message m){
	//	return m.getBody().substring(mucc.prefix.length());
	//}
	
	//public static String hGetNick(Packet p){
	//	return p.getFrom().split("/")[1];
	//}

}
