package eu.janinko.xmppmuc;

import eu.janinko.xmppmuc.commands.Command;
import eu.janinko.xmppmuc.commands.PluginBuildException;
import eu.janinko.xmppmuc.data.PluginData;
import eu.janinko.xmppmuc.data.PropertiesPluginData;
import java.io.File;
import org.apache.log4j.Logger;
import org.jivesoftware.smack.packet.Message;

/** Middle layer between Command and MucCommands,
 * providing helpful functionality for Command.
 * @author janinko
 *
 */
public class CommandWrapper {
	private Commands commands;
	Command command;
	PluginData data;

	private static Logger logger = Logger.getLogger(CommandWrapper.class);
	
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

	public void sendPrivateMessage(String nick, Message message){
		if(logger.isTraceEnabled()){logger.trace("Sending private message to " + nick + ": " + message.getBody());}
		message.setTo(commands.getConnection().getRoom()+"/"+nick);
		message.setType(Message.Type.chat);
		sendMessage(message);
	}

	public void sendPrivateMessage(String nick, String message){
		Message m = new Message();
		m.setBody(message);
		sendPrivateMessage(nick,m);
	}
	
	@Deprecated
	public File getConfigFile(){
		return new File(commands.getPlugins().pluginDir
				       + command.getClass().getSimpleName()
				       + ".conf");
	}
	
	@SuppressWarnings("unchecked")
	public PluginData getConfig(){
		if(data == null){
			data = new PropertiesPluginData((Class<Command>) command.getClass());
		}
		return data;
	}


	
	//public String hGetCommand(Message m){
	//	return m.getBody().substring(mucc.prefix.length());
	//}
	
	//public static String hGetNick(Packet p){
	//	return p.getFrom().split("/")[1];
	//}

}
