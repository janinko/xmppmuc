package eu.janinko.xmppmuc.commands;

import org.jivesoftware.smack.packet.Message;

import eu.janinko.xmppmuc.CommandWrapper;

public class Emote implements Command{
	private CommandWrapper cw;
	
	
	public Emote() {}
	
	public Emote(CommandWrapper commandWrapper){
		this.cw = commandWrapper;
	}
	
	@Override
	public Command build(CommandWrapper commandWrapper) {
		return new Emote(commandWrapper);
	}

	public String getCommand() {
		return "emote";
	}

	public void handle(Message m, String[] args) {
		if(args.length == 1){
			cw.sendMessage("/me zije");
		}else{
			cw.sendMessage("/me " + cw.hGetCommand(m).substring(getCommand().length()+1));
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