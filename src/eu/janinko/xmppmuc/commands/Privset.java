package eu.janinko.xmppmuc.commands;

import java.util.Map;

import org.jivesoftware.smack.packet.Message;

import eu.janinko.xmppmuc.CommandWrapper;

public class Privset implements Command {
	private CommandWrapper cw;

	public Privset() {}

	ConfigManager configManager;

	public Privset(CommandWrapper commandWrapper){
		this.cw = commandWrapper;
		configManager = new ConfigManager(System.getProperty("user.home") + "/.xmppmuc/plugins/privsets.xml");
		
		
		Map<String, String> config = configManager.getConfig("jid");
		
		for(String userJid : config.keySet()){
			cw.getMucCommands().privSet(userJid, Integer.decode(config.get(userJid)));
		}
	}
	
	@Override
	public Command build(CommandWrapper commandWrapper) {
		return new Privset(commandWrapper);
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
		cw.getMucCommands().privSet(args[1], Integer.decode(args[2]));
		System.out.println("Práva pro " + args[1] + " byla nastaven na: " + args[2]);
	}

	public String help(String prefix) {
		return null;
	}

	@Override
	public void destroy() {		
	}

}
