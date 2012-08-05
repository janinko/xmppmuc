package eu.janinko.xmppmuc.commands;

import eu.janinko.xmppmuc.CommandWrapper;
import eu.janinko.xmppmuc.Helper;
import eu.janinko.xmppmuc.Message;

public class Emote extends AbstractCommand{
	private CommandWrapper cw;
	
	public Emote() {}
	
	public Emote(CommandWrapper commandWrapper){
		this.cw = commandWrapper;
	}
	
	@Override
	public Command build(CommandWrapper commandWrapper) {
		return new Emote(commandWrapper);
	}

    @Override
	public String getCommand() {
		return "emote";
	}

    @Override
	public void handle(Message m, String[] args) {
		if(args.length == 1){
			cw.sendMessage("/me zije");
		}else{
			cw.sendMessage("/me " + Helper.implode(args,1));
		}
	}

    @Override
	public String help(String prefix) {
		return "Zaemotuje text zadaný příkazem '" + prefix + "emote text'";
	}
	
    @Override
	public int getPrivLevel(){
		return 1;
	}

}