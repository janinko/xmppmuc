package eu.janinko.xmppmuc;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smackx.muc.MultiUserChat;
import org.jivesoftware.smackx.muc.Occupant;

import eu.janinko.xmppmuc.commands.Command;
import eu.janinko.xmppmuc.commands.MessageCommand;
import eu.janinko.xmppmuc.commands.PresenceCommand;


public class MucCommands {
	private MultiUserChat muc;
	String prefix;
	private Map<String,Integer> privs;
	PluginManager pm;
	
	String pluginDir = System.getProperty("user.home") + "/.xmppmuc/plugins/";
	
	private static Logger logger = Logger.getLogger(MucCommands.class);
	
	public MucCommands(String prefix){
		muc = null;
		this.prefix = prefix;
		privs = new HashMap<String,Integer>();
		pm = new PluginManager(this);
		
		pm.loadPlugins();
		pm.loadPluginsFromConfigFile(pluginDir + "plugins");
	}
	
	public String getPrefix() {
		return prefix;
	}

	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}
	
	public void setMUC(MultiUserChat muc){
		if(logger.isTraceEnabled()){logger.trace("Setting muc: '"+muc);}
		this.muc = muc;
	}

	public void handleCommand(Message m){
		String command = hGetCommand(m);
		if(logger.isTraceEnabled()){logger.trace("handling command '"+command+"' from message: "+m);}
		String from = m.getFrom();
		if(logger.isTraceEnabled()){logger.trace("from: '"+from);}
		Occupant occupant = muc.getOccupant(from);
		if(logger.isTraceEnabled()){logger.trace("occupant: '"+occupant);}
		String jid = occupant.getJid();
		if(logger.isTraceEnabled()){logger.trace("jid: '"+jid);}
		String[] jids = jid.split("/");
		if(logger.isTraceEnabled()){logger.trace("jids: '"+jids);}
		String sjid = jids[0];
		if(logger.isTraceEnabled()){logger.trace("sjid: '"+sjid);}
		String mjid = sjid.toLowerCase();
		if(logger.isTraceEnabled()){logger.trace("mjid: '"+mjid);}
		Integer priv = privs.get(mjid);
		if(logger.isTraceEnabled()){logger.trace("priv: '"+priv);}
		int ppriv;
		if(priv != null){
			if(logger.isTraceEnabled()){logger.trace("priv not null");}
			ppriv=priv.intValue();
		}else{
			if(logger.isTraceEnabled()){logger.trace("priv null ~> 0");}
			ppriv = 0;
		}

		if(logger.isTraceEnabled()){logger.trace("priv level is "+ ppriv);}
		
		if(command.startsWith("commands")){
			cCommands();
		}else if(command.startsWith("help")){
			cHelp(command);
		}
		for(CommandWrapper cw : pm.getCommands()){
			Command c = cw.command;
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
			for(CommandWrapper cw : pm.getCommands()){
				Command c = cw.command;
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
		if("help".equals(what)){
			try {
				muc.sendMessage("Si ze mě děláš srandu?");
			} catch (XMPPException e) {
				System.err.println("MucCommands.cHelp() A");
				e.printStackTrace();
			}
			return;
		}
		if("commands".equals(what)){
			try {
				muc.sendMessage("Proč to prostě nezkusíš? Jen to vypíše dostupné příkazy.");
			} catch (XMPPException e) {
				System.err.println("MucCommands.cHelp() A");
				e.printStackTrace();
			}
			return;
		}

		for(CommandWrapper cw : pm.getCommands()){
			Command c = cw.command;
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
		for(CommandWrapper cw : pm.getCommands()){
			Command c = cw.command;
			if (c instanceof PresenceCommand){
				PresenceCommand pc = (PresenceCommand) c;
				pc.handlePresence(p);
			}
		}
	}
	
	public void handleMessage(Message m) {
		for(CommandWrapper cw : pm.getCommands()){
			Command c = cw.command;
			if (c instanceof MessageCommand){
				MessageCommand mc = (MessageCommand) c;
				mc.handleMessage(m);
			}
		}
	}

	public MultiUserChat getMuc() {
		return muc;
	}

	public void privSet(String userJid, Integer decode) {
		privs.put(userJid.toLowerCase(), decode);		
	}
	
	private String[] splitToArgs(String s){
		return s.split(" +");
	}
	
	public String hGetCommand(Message m){
		return m.getBody().substring(prefix.length());
	}
	
	public static String hGetNick(Packet p){
		return p.getFrom().split("/")[1];
	}
	
}