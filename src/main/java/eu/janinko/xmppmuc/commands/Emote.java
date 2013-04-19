package eu.janinko.xmppmuc.commands;

import eu.janinko.xmppmuc.Helper;
import eu.janinko.xmppmuc.Message;
import eu.janinko.xmppmuc.api.plugin.AbstractCommand;

public class Emote extends AbstractCommand{
	
    @Override
	public String getCommand() {
		return "emote";
	}

    @Override
	public void handle(Message m, String[] args) {
		switch(args.length){
			case 0:
			case 1:
				cw.sendMessage("/me zije");
				return;
			default: // >1
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