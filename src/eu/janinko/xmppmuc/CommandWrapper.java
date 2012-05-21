package eu.janinko.xmppmuc;

import java.io.File;

import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Packet;

import eu.janinko.xmppmuc.commands.Command;
import eu.janinko.xmppmuc.commands.PluginBuildException;

/** Middle layer between Command and MucCommands,
 * providing helpful functionality for Command.
 * @author janinko
 *
 */
public class CommandWrapper {
	private MucCommands mucc;
	Command command;
	
	public CommandWrapper(Command c, MucCommands m) throws PluginBuildException{
		mucc = m;
		command = c;
		command = c.build(this);
	}
	
	public MucCommands getMucCommands(){
		return mucc;
	}

	public boolean sendMessage(String message) {
		try {
			mucc.getMuc().sendMessage(message);
		} catch (XMPPException e) {
			return false;
		}
		
		return true;
	}
	public File getConfigFile(){
		return new File(mucc.pluginDir
				       + command.getClass().getSimpleName()
				       + ".conf");
	}
	
	public String hGetCommand(Message m){
		return m.getBody().substring(mucc.prefix.length());
	}
	
	public static String hGetNick(Packet p){
		return p.getFrom().split("/")[1];
	}

}
