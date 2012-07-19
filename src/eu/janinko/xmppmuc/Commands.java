package eu.janinko.xmppmuc;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smackx.muc.MultiUserChat;

import eu.janinko.xmppmuc.commands.Command;
import eu.janinko.xmppmuc.commands.MessageCommand;
import eu.janinko.xmppmuc.commands.PresenceCommand;
import eu.janinko.xmppmuc.listeners.PluginsPacketListener;

public class Commands {
	private XmppConnection connection;
	private MultiUserChat muc;
	private Map<String,Integer> privs;
	private Plugins plugins;
	
	String prefix = ".";
	
	private static Logger logger = Logger.getLogger(Commands.class);
	
	Commands(){
		privs = new HashMap<String,Integer>();
		plugins = new Plugins(this);
		
		
	}

	public String getPrefix() {
		return prefix;
	}

	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}

	public void setMuc(MultiUserChat muc) {
		if(logger.isTraceEnabled()){logger.trace("Setting muc: '"+muc);}
		this.muc = muc;
		if(muc == null){
			plugins.disconnected();
		}else{
			muc.addMessageListener(new PluginsPacketListener(this));
			plugins.connected();
		}
	}

	public void handlePresence(Presence presence) {
		if(muc == null) return;

		for(CommandWrapper cw : plugins.getPresencePlugins()){
			PresenceCommand command = (PresenceCommand) cw.command;
			command.handlePresence(presence);
		}
	}

	public void handleMessage(Message message) {
		if(muc == null) return;
		
		if(message.getBody().startsWith(prefix)){
			handleCommand(message);
			return;
		}
		for(CommandWrapper cw : plugins.getMessagePlugins()){
			MessageCommand command = (MessageCommand) cw.command;
			command.handleMessage(new eu.janinko.xmppmuc.Message(message));
		}
	}
	
	public void handleCommand(Message message) {
		if(muc == null) return;
		
		String[] command = message.getBody().substring(prefix.length()).split(" +");
		if(logger.isTraceEnabled()){logger.trace("Handling command: " + command);}
		
		String from = message.getFrom();
		int priv = getPrivLevel(from);
		
		if("commands".equals(command[0])){
			printCommands();
		}else if("help".equals(command[0])){
			printHelp(command[1]);
		}else{
			CommandWrapper cw = plugins.getPlugin(command[0]);
			if(cw == null) return;
			Command c = cw.command;
			if(c.getPrivLevel() <= priv){
				c.handle(new eu.janinko.xmppmuc.Message(message), command);
			}
		}
	}
	
	private void printHelp(String what){
		if(muc == null) return;

		String help = null;
		
		if("help".equals(what)){
			help="Si ze mě děláš srandu?";
		}else if("commands".equals(what)){
			help="Proč to prostě nezkusíš? Jen to vypíše dostupné příkazy.";
		}else{
			CommandWrapper cw = plugins.getPlugin(what);
			if(cw != null){
				help = cw.command.help(prefix);
			}
		}
		if(help == null){
			help = "O tomhle ale vůbec nic nevím!";
		}
		connection.sendMessage(help);
	}

	private void printCommands(){
		if(muc == null) return;
		
		StringBuilder sb = new StringBuilder("Příkazy jsou: ");
		sb.append(prefix);
		sb.append("commands, ");
		sb.append(prefix);
		sb.append("help");
		
		for(CommandWrapper cw : plugins.getPlugins()){
			sb.append(", ");
			sb.append(prefix);
			sb.append(cw.command.getCommand());
		}
		connection.sendMessage(sb.toString());
	}

	private int getPrivLevel(String usser){
		String jid = muc.getOccupant(usser).getJid().split("/")[0].toLowerCase();
		Integer priv = privs.get(jid);
		if(priv == null) return 0;
		return priv;
	}

	public XmppConnection getConnection() {
		return connection;
	}

	public Plugins getPlugins() {
		return plugins;
	}

	public void privSet(String userJid, Integer decode) {
		privs.put(userJid.toLowerCase(), decode);		
	}

	public void setConnection(XmppConnection xmppConnection) {
		this.connection = xmppConnection;
	}
	

}
