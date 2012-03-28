package eu.janinko.xmppmuc.commands;

import java.util.Map;

import org.jivesoftware.smack.packet.Message;

import eu.janinko.xmppmuc.MucCommands;

public class Privset implements Command {

	public Privset() {}

	MucCommands mucc;
	ConfigManager configManager;

	public Privset(MucCommands mucCommands) {
		mucc = mucCommands;
		configManager = new ConfigManager(System.getProperty("user.home") + "/.xmppmuc/plugins/privsets.xml");
		
		
		Map<String, String> config = configManager.getConfig("jid");
		
		for(String userJid : config.keySet()){
			mucc.privSet(userJid, Integer.decode(config.get(userJid)));
		}
	}
	
	@Override
	public Command build(MucCommands mucCommands) {
		return new Privset(mucCommands);
	}
	
	public String getCommand() {
		return "privset";
	}

	public int getPrivLevel() {
		return 100;
	}

	public void handle(Message m, String[] args) {

		if(args.length != 3) return;

		if(!args[1].matches("[A-Za-z.-]+@[A-Za-z.-]+.[a-z]+")) return;
		
		if(!args[2].matches("-?[0-9]+")) return;
		
		configManager.setConfig("jid", args[1], args[2]);
		mucc.privSet(args[1], Integer.decode(args[2]));
		System.out.println("Práva pro " + args[1] + " byla nastaven na: " + args[2]);
	}

	public String help(String prefix) {
		return null;
	}

	@Override
	public void destroy() {		
	}

}
