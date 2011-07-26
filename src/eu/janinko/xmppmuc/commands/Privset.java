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

	public void handle(Message m) {
		String command = mucc.hGetCommand(m);
		
		if(command.matches("privset [A-Za-z.-]*@[A-Za-z.-]* = [0-9-]*")){
			String[] prikaz = command.split("=");
			String jid = prikaz[0].substring(8, prikaz[0].length()-1);
			String priv = prikaz[1].substring(1);

			configManager.setConfig("jid", jid, priv);
			System.out.println("Práva pro " + jid + " byla nastaven na: " + priv);
			mucc.privSet(jid, Integer.decode(priv));
		}
	}

	public String help(String prefix) {
		return null;
	}

	@Override
	public void destroy() {		
	}

}
