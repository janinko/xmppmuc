package eu.janinko.xmppmuc;

import eu.janinko.xmppmuc.commands.*;

import java.util.HashMap;
import java.util.Map;

import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smackx.muc.MultiUserChat;


public class MucCommands {
	private MultiUserChat muc;
	private String prefix;
	private Map<String,Integer> privs;
	private PluginManager pm;
	
	public MucCommands(String prefix){
		muc = null;
		this.prefix = prefix;
		privs = new HashMap<String,Integer>();
		pm = new PluginManager(this);
	}
	
	public String getPrefix() {
		return prefix;
	}

	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}
	
	public void setMUC(MultiUserChat muc) throws XMPPException{
		this.muc = muc;
		pm.loadPlugins();
		pm.loadPluginsFromConfigFile(System.getProperty("user.home") + "/.xmppmuc/plugins/plugins");
	}

	public void handleCommand(Message m){
		String command = hGetCommand(m);
		
		Integer priv = privs.get(muc.getOccupant(m.getFrom()).getJid().split("/")[0].toLowerCase());
		int ppriv;
		if(priv != null){
			ppriv=priv.intValue();
		}else{
			ppriv = 0;
		}
		
		if(command.startsWith("commands")){
			cCommands();
		}else if(command.startsWith("help")){
			cHelp(command);
		}else if(command.startsWith("pm") && ppriv >= 100){
			pm.handleCommand(m);
		}
		for(Command c : pm.getCommands()){
			if(command.startsWith(c.getCommand())){
				if(c.getPrivLevel() <= ppriv ){
					c.handle(m);
				}
			}
		}
	}

	private void cCommands() {	
		if(muc == null) return;
		
		try {
			String prikazy = "Příkazy jsou: ";
			for(Command c : pm.getCommands()){
				prikazy += prefix + c.getCommand() + ", ";				
			}
			prikazy += prefix + "pm, " + prefix + "help, " + prefix + "commands";
			muc.sendMessage(prikazy);
		} catch (XMPPException e) {
			e.printStackTrace();
		}
	}
	
	private void cHelp(String command){
		if(muc == null) return;
		String what = command.substring(5);

		for(Command c : pm.getCommands()){
			if(what.equals(c.getCommand())){
				String message = c.help(prefix);
				if(message != null){
					try {
						muc.sendMessage(message);
					} catch (XMPPException e) {
						e.printStackTrace();
					}
				}
			}
		}
		
		
	}

	public void handlePresence(Presence p) {
		for(Command c : pm.getCommands()){
			if (c instanceof PresenceCommand){
				PresenceCommand pc = (PresenceCommand) c;
				pc.handlePresence(p);
			}
		}
	}

	public MultiUserChat getMuc() {
		return muc;
	}
	
	public String hGetCommand(Message m){
		return m.getBody().substring(prefix.length());
	}
	
	public static String hGetNick(Packet p){
		return p.getFrom().split("/")[1];
	}

	public void privSet(String userJid, Integer decode) {
		privs.put(userJid.toLowerCase(), decode);		
	}
}






