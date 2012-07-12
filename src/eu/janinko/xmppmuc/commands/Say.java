package eu.janinko.xmppmuc.commands;

import org.jivesoftware.smack.packet.Message;

import eu.janinko.xmppmuc.CommandWrapper;
import eu.janinko.xmppmuc.Helper;

public class Say extends AbstractCommand {
	private CommandWrapper cw;
	
	public Say() {}
	
	public Say(CommandWrapper commandWrapper){
		this.cw = commandWrapper;
	}
	
	@Override
	public Command build(CommandWrapper commandWrapper) {
		return new Say(commandWrapper);
	}

	public String getCommand() {
		return "say";
	}

	public void handle(Message m, String[] args) {
		if(args.length == 1){
			cw.sendMessage("pff");
		}else{
			cw.sendMessage(Helper.implode(args,1));
		}
	}

	public String help(String prefix) {
		return "Řekne text zadaný příkazem '" + prefix + "say text'";
	}
	
	public int getPrivLevel(){
		return 2;
	}
}