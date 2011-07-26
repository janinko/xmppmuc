package eu.janinko.xmppmuc.commands;

import java.util.Map;

import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Presence;

import eu.janinko.xmppmuc.MucCommands;

public class Pozdrav implements PresenceCommand{
	public Pozdrav() {}

	MucCommands mucc;
	ConfigManager configManager;
	private Map<String, String> pozdravy;


	public Pozdrav(MucCommands mucc){
		this.mucc = mucc;
		configManager = new ConfigManager(System.getProperty("user.home") + "/.xmppmuc/plugins/pozdravy.xml");
		pozdravy = configManager.getConfig("nick");
	}
	
	@Override
	public Command build(MucCommands mucCommands) {
		return new Pozdrav(mucCommands);
	}

	public String getCommand() {
		return "pozdrav";
	}

	public int getPrivLevel() {
		return 0;
	}

	public void handle(Message m) {
		String command = mucc.hGetCommand(m);
		
		if(command.matches("pozdrav [A-Za-z._-]* = .*")){
			String[] prikaz = command.split("=");
			String nick = prikaz[0].substring(8, prikaz[0].length()-1);
			String pozdrav = prikaz[1].substring(1);
			
			if(pozdrav.length() != 0){
				pozdravy.put(nick, pozdrav);
				configManager.setConfig("nick", nick, pozdrav);
			}else{
				pozdravy.remove(nick);
				configManager.removeConfig("nick", nick);
			}
			
			
			System.out.println("Pozdrav pro " + nick + " byl nastaven na: " + pozdrav);
		}else{
			try {
				mucc.getMuc().sendMessage("Syntaxe pro prikaz pozdrav je:\n" + mucc.getPrefix() + "pozdrav nick = pozdrav");
			} catch (XMPPException e) {
				e.printStackTrace();
			}
		}
		
	}

	public String help(String prefix) {
		return "Syntaxe pro prikaz pozdrav je:\n" + prefix + "pozdrav nick = pozdrav";
	}

	public void handlePresence(Presence p) {
		if(p.getType() == Presence.Type.available){
			if(pozdravy.containsKey(MucCommands.hGetNick(p))){
				try {
					mucc.getMuc().sendMessage(MucCommands.hGetNick(p) + ": " + pozdravy.get(MucCommands.hGetNick(p)));
				} catch (XMPPException e) {
					e.printStackTrace();
				}
			}
		}
	}

	@Override
	public void destroy() {		
	}
}
