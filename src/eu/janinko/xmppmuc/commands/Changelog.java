package eu.janinko.xmppmuc.commands;

import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Message;

import eu.janinko.xmppmuc.MucCommands;

public class Changelog implements Command {
	private MucCommands mucc;
	
	public Changelog(){
		mucc = null;
	}
	
	public Changelog(MucCommands mucCommands){
		mucc = mucCommands;
	}

	@Override
	public Command build(MucCommands mucCommands) throws PluginBuildException {
		return new Changelog(mucCommands);
	}

	@Override
	public void destroy() {
	}

	@Override
	public String getCommand() {
		return "changelog";
	}

	@Override
	public int getPrivLevel() {
		return 0;
	}

	@Override
	public void handle(Message m, String[] args) {
		try {
			mucc.getMuc().sendMessage("201109091447:\n" +
			                          " M command 'pozdrav' - corrected help" +
			                          " + command 'pozdrav': ability to show current greeting" + 
			                          "201109091350:\n" +
			                          "   Refactored handeler methods\n" +
			                          " + command 'changelog'\n" +
			                          " M command 'pozdrav' and 'privset': changed syntax\n");
		} catch (XMPPException e) {
			System.err.println("Changelog.handle() A");
			e.printStackTrace();
		}
	}

	@Override
	public String help(String prefix) {
		return "Vypíše poslední změny";
	}

}
