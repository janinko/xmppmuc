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

	public void handle(Message m, String[] args) {
		try {
			if(args.length == 1){
				mucc.getMuc().sendMessage("/me zije");
			}else{
				mucc.getMuc().sendMessage("/me " + mucc.hGetCommand(m).substring(getCommand().length()+1));
			}
		} catch (XMPPException e) {
			System.err.println("Emote.handle() A");
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