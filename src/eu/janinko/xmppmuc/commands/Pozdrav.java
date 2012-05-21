package eu.janinko.xmppmuc.commands;

import java.util.Map;

import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Presence;

import eu.janinko.xmppmuc.CommandWrapper;
import eu.janinko.xmppmuc.MucCommands;

public class Pozdrav implements PresenceCommand{
	private CommandWrapper cw;

	ConfigManager configManager;
	private Map<String, String> pozdravy;
	
	public Pozdrav() {}

	public Pozdrav(CommandWrapper commandWrapper){
		this.cw = commandWrapper;
		configManager = new ConfigManager(System.getProperty("user.home") + "/.xmppmuc/plugins/pozdravy.xml");
		pozdravy = configManager.getConfig("nick");
	}
	
	@Override
	public Command build(CommandWrapper commandWrapper) {
		return new Pozdrav(commandWrapper);
	}

	public String getCommand() {
		return "pozdrav";
	}

	public int getPrivLevel() {
		return 0;
	}

	public void handle(Message m, String[] args) {

			if(args.length == 1){
				String nick = MucCommands.hGetNick(m);
				if(pozdravy.containsKey(nick)){
					cw.sendMessage(nick + ": " + pozdravy.get(nick));
				}
			}else if(args.length < 3){
				if(pozdravy.containsKey(args[1])){
					cw.sendMessage(args[1] + ": " + pozdravy.get(args[1]));
				}
			}else if(args[1].equals("set")){
				StringBuilder sb = new StringBuilder();
				for(int i=3; i<args.length; i++){
					sb.append(args[i]);
					sb.append(' ');
				}
				sb.deleteCharAt(sb.length()-1);

				pozdravy.put(args[2], sb.toString());
				configManager.setConfig("nick", args[2], sb.toString());
			}else if(args[1].equals("reset")){
				if(pozdravy.remove(args[2]) != null){
					configManager.removeConfig("nick", args[2]);
					cw.sendMessage("Pozdrav pro " + args[2] + " byl zrušen");
				}
			}else{
				cw.sendMessage(this.help(cw.getMucCommands().getPrefix()));
			}
	}

	public String help(String prefix) {
		return "Syntaxe pro prikaz pozdrav je:\n"
		       + prefix + "pozdrav nick\n"
		       + prefix + "pozdrav set nick pozdrav\n"
		       + prefix + "pozdrav reset nick";
	}

	public void handlePresence(Presence p) {
		if(p.getType() == Presence.Type.available){
			if(pozdravy.containsKey(MucCommands.hGetNick(p))){
				cw.sendMessage(MucCommands.hGetNick(p) + ": " + pozdravy.get(MucCommands.hGetNick(p)));
			}
		}
	}

	@Override
	public void destroy() {		
	}
}
