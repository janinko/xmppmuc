package eu.janinko.xmppmuc.commands;

import org.jivesoftware.smack.packet.Message;

import eu.janinko.xmppmuc.CommandWrapper;

public class Kill extends AbstractCommand{
	public Kill() {}
	
	@Override
	public Command build(CommandWrapper mucCommands) {
		return new Kill();
	}

	@Override
	public String getCommand() {
		return "kill";
	}

	@Override
	public int getPrivLevel() {
		return 20;
	}

	@Override
	public void handle(Message m, String[] args) {
		Runtime.getRuntime().exit(0);
	}

	@Override
	public String help(String prefix) {
		return "Vypne bota";
	}

}
