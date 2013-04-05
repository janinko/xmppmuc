package eu.janinko.xmppmuc.commands;

import eu.janinko.xmppmuc.Helper;
import eu.janinko.xmppmuc.Message;

public class Say extends AbstractCommand {

    @Override
	public String getCommand() {
		return "say";
	}

    @Override
	public void handle(Message m, String[] args) {
		if(args.length == 1){
			cw.sendMessage("pff");
		}else{
			cw.sendMessage(Helper.implode(args,1));
		}
	}

    @Override
	public String help(String prefix) {
		return "Řekne text zadaný příkazem '" + prefix + "say text'";
	}
	
    @Override
	public int getPrivLevel(){
		return 2;
	}
}