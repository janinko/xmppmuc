package eu.janinko.xmppmuc.commands;

import org.jivesoftware.smack.packet.Message;

import eu.janinko.xmppmuc.MucCommands;

public class Kill implements Command {
	public Kill() {}

	public Kill(MucCommands mucCommands) {
	}
	
	@Override
	public Command build(MucCommands mucCommands) {
		return new Kill(mucCommands);
	}

	@Override
	public String getCommand() {
		return "kill";
	}

	@Override
	public int getPrivLevel() {
		return 100;
	}

	@Override
	public void handle(Message m) {
		Runtime.getRuntime() .exit(0);
	}

	@Override
	public String help(String prefix) {
		return "Vypne bota";
	}

	@Override
	public void destroy() {
		// TODO Auto-generated method stub
		
	}

}
