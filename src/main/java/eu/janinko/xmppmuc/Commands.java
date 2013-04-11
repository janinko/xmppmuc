package eu.janinko.xmppmuc;

import eu.janinko.xmppmuc.commands.Command;
import eu.janinko.xmppmuc.commands.MessageCommand;
import eu.janinko.xmppmuc.commands.PresenceCommand;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import org.apache.log4j.Logger;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Presence;

public class Commands {
	private Room conn;
	private Plugins plugins;

	private Map<String,Integer> privs = new HashMap<>();
	private Timer timer = new Timer();
	
	private String prefix = ".";
	
	private static Logger logger = Logger.getLogger(Commands.class);
	
	public Commands(Plugins plugins, Room connection){
		this.plugins = plugins;
		this.conn = connection;
	}

	void init(){
		plugins.setCommands(this);
		conn.setCommands(this);
		plugins.startPlugins();
	}

    /**
	 * Returns current command prefix.
	 *
	 * @return Command prefix.
	 */
	public String getPrefix() {
		return prefix;
	}

    /**
	 * Sets command prefix. Default is '.' (dot).
	 *
	 * @param prefix Command prefix.
	 */
	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}

	void disconnected(){
		plugins.disconnected();
	}

	void connected(){
		plugins.connected();
	}

	public void handlePresence(Presence presence) {
		if(!conn.conected()) return;

		for(CommandWrapper cw : plugins.getPresencePlugins()){
			PresenceCommand command = (PresenceCommand) cw.command;
                        if(logger.isTraceEnabled()){logger.trace("Handling presence with: " + command);}
			command.handlePresence(presence);
		}
	}

	public void handleStatus(Status status) {
		if(!conn.conected()) return;

		for(CommandWrapper cw : plugins.getPresencePlugins()){
			PresenceCommand command = (PresenceCommand) cw.command;
                        if(logger.isTraceEnabled()){logger.trace("Handling status with: " + command);}
			command.handleStatus(status);
		}
	}

	public void handleMessage(Message message) {
		if(!conn.conected()) return;
		
		if(message.getBody().startsWith(prefix) || message.getBody().startsWith(conn.getNick())){
			handleCommand(message);
			return;
		}
		for(CommandWrapper cw : plugins.getMessagePlugins()){
			MessageCommand command = (MessageCommand) cw.command;
                        if(logger.isTraceEnabled()){logger.trace("Handling message with: " + command);}
			command.handleMessage(new eu.janinko.xmppmuc.Message(message,this));
		}
	}

	public void handleCommand(Message message) {
		if(!conn.conected()) return;

		String body = message.getBody();
		if(body.startsWith(prefix)){
			body = body.substring(prefix.length());
		}else{ //(message.getBody().startsWith(connection.getNick()){
			body = body.substring(conn.getNick().length());
			if(body.matches("[:>]? .*")){
				body = body.substring(1).trim();
			}else{
				return;
			}
		}

		String[] command = body.split(" +");
		if(logger.isTraceEnabled()){logger.trace("Handling command: " + Arrays.toString(command));}
		
		String from = message.getFrom();
		int priv = getPrivLevel(from);
		switch (command[0]) {
			case "commands":
				printCommands();
				break;
			case "help":
				printHelp(command[1]);
				break;
			default:
				CommandWrapper cw = plugins.getPlugin(command[0]);
				if(cw == null) return;
				Command c = cw.command;
				if(c.getPrivLevel() <= priv){
					c.handle(new eu.janinko.xmppmuc.Message(message,this), command);
				}else if(logger.isInfoEnabled()){
					logger.info("User " + from + " (priv " + priv + ") tried to do '" + message.getBody() + "' (priv " + c.getPrivLevel() + ")");
				}
				break;
		}
	}
	
	private void printHelp(String what){
		if(!conn.conected()) return;

		String help = null;
		switch (what) {
			case "help":
				help="Si ze mě děláš srandu?";
				break;
			case "commands":
				help="Proč to prostě nezkusíš? Jen to vypíše dostupné příkazy.";
				break;
			default:
				CommandWrapper cw = plugins.getPlugin(what);
				if(cw != null){
					help = cw.command.help(prefix);
				}
				break;
		}
		if(help == null){
			help = "O tomhle ale vůbec nic nevím!";
		}
		conn.sendMessage(help);
	}

	private void printCommands(){
		if(!conn.conected()) return;
		
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
		conn.sendMessage(sb.toString());
	}

	private int getPrivLevel(String usser){
		String jid = conn.getMuc().getOccupant(usser).getJid().split("/")[0].toLowerCase();
		Integer priv = privs.get(jid);
		if(priv == null) return 0;
		return priv;
	}

    /**
	 * Returns {@link XmppConnection}.
	 *
	 * @return XmppConnection that handle connection to XMPP server and MUC.
	 */
	public Room getRoom() {
		return conn;
	}

	public Plugins getPlugins() {
		return plugins;
	}

	public void privSet(String userJid, Integer decode) {
		privs.put(userJid.toLowerCase(), decode);		
	}

	public Timer getTimer() {
		return timer;
	}

	void stop() {
		timer.cancel();
		timer = new Timer();
	}
}
