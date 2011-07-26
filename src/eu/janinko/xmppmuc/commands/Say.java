package eu.janinko.xmppmuc.commands;

import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Message;

import eu.janinko.xmppmuc.MucCommands;

public class Say implements Command{
	public Say() {}

	MucCommands mucc;
	
	public Say(MucCommands mucc){
		this.mucc = mucc;
	}
	
	@Override
	public Command build(MucCommands mucCommands) {
		return new Say(mucCommands);
	}

	public String getCommand() {
		return "say";
	}

	public void handle(Message m) {
		String command = mucc.hGetCommand(m);
	
		try {
			if(command.length() < 4){
				mucc.getMuc().sendMessage("pff");
			}else{
				mucc.getMuc().sendMessage(command.substring(4));
			}
		} catch (XMPPException e) {
			e.printStackTrace();
		}
	}

	public String help(String prefix) {
		return "Řekne text zadaný příkazem '" + prefix + "say text'";
	}
	
	public int getPrivLevel(){
		return 2;
	}

	@Override
	public void destroy() {		
	}
}