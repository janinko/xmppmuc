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

	public void handle(Message m, String[] args) {

		try {
			if(args.length == 1){
				String nick = MucCommands.hGetNick(m);
				if(pozdravy.containsKey(nick)){
					mucc.getMuc().sendMessage(nick + ": " + pozdravy.get(nick));
				}
			}else if(args.length < 3){
				if(pozdravy.containsKey(args[1])){
					mucc.getMuc().sendMessage(args[1] + ": " + pozdravy.get(args[1]));
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
				if(pozdravy.remove(args[1]) != null){
					configManager.removeConfig("nick", args[1]);
						mucc.getMuc().sendMessage("Pozdrav pro " + args[1] + " byl zrušen");
				}
			}else{
				mucc.getMuc().sendMessage(this.help(mucc.getPrefix()));
			}
		} catch (XMPPException e) {
			e.printStackTrace();
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
				try {
					mucc.getMuc().sendMessage(MucCommands.hGetNick(p) + ": " + pozdravy.get(MucCommands.hGetNick(p)));
				} catch (XMPPException e) {
					System.err.println("Pozdrav.handlePresence() A");
					e.printStackTrace();
				}
			}
		}
	}

	@Override
	public void destroy() {		
	}
}
