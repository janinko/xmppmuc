package eu.janinko.xmppmuc.commands;

import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Message;

import eu.janinko.xmppmuc.MucCommands;

public class Emote implements Command{
	public Emote() {}

	MucCommands mucc;
	
	public Emote(MucCommands mucc){
		this.mucc = mucc;
	}
	
	@Override
	public Command build(MucCommands mucCommands) {
		return new Emote(mucCommands);
	}

	public String getCommand() {
		return "emote";
	}

	public void handle(Message m) {
		String command = mucc.hGetCommand(m);
	
		try {
			if(command.length() < 6){
				mucc.getMuc().sendMessage("/me zije");
			}else{
				mucc.getMuc().sendMessage("/me " + command.substring(6));
			}
		} catch (XMPPException e) {
			e.printStackTrace();
		}
	}

	public String help(String prefix) {
		return "Zaemotuje text zadaný příkazem '" + prefix + "emote text'";
	}
	
	public int getPrivLevel(){
		return 1;
	}

	@Override
	public void destroy() {		
	}
}