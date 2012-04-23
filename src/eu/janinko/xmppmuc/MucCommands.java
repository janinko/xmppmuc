package eu.janinko.xmppmuc;

import java.util.HashMap;
import java.util.Map;

import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smackx.muc.MultiUserChat;
import org.jivesoftware.smackx.muc.ParticipantStatusListener;

import eu.janinko.xmppmuc.commands.Command;
import eu.janinko.xmppmuc.commands.MessageCommand;
import eu.janinko.xmppmuc.commands.PresenceCommand;


public class MucCommands {
	private MultiUserChat muc;
	private String prefix;
	private Map<String,Integer> privs;
	PluginManager pm;
	
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
		}
		for(Command c : pm.getCommands()){
			if(command.startsWith(c.getCommand())){
				if(c.getPrivLevel() <= ppriv ){
					c.handle(m,splitToArgs(command));
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
			prikazy += prefix + "help, " + prefix + "commands";
			muc.sendMessage(prikazy);
		} catch (XMPPException e) {
			System.err.println("MucCommands.cCommands() A");
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
						System.err.println("MucCommands.cHelp() A");
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
	
	public void handleMessage(Message m) {
		for(Command c : pm.getCommands()){
			if (c instanceof MessageCommand){
				MessageCommand mc = (MessageCommand) c;
				mc.handleMessage(m);
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
	
	private String[] splitToArgs(String s){
		return s.split(" +");
	}
}