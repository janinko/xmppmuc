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
import org.jivesoftware.smackx.muc.MultiUserChat;

public class Commands {
	private XmppConnection connection;
	private MultiUserChat muc;
	private Map<String,Integer> privs;
	private Plugins plugins;
	private Bot bot;
	private Timer timer;
	
	String prefix = ".";
	
	private static Logger logger = Logger.getLogger(Commands.class);
	
	public Commands(Bot bot){
		privs = new HashMap<>();
		timer = new Timer();
		plugins = new Plugins();
		this.bot = bot;
	}

	void init(){
		plugins.setCommands(this);
		plugins.loadPlugins();
		plugins.loadPluginsFromConfigFile();
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

	void setMuc(MultiUserChat muc) {
		if(logger.isTraceEnabled()){logger.trace("Setting muc: "+muc);}
		this.muc = muc;
		if(muc == null){
			plugins.disconnected();
		}else{
			plugins.connected();
		}
	}

	public void handlePresence(Presence presence) {
		if(muc == null) return;

		for(CommandWrapper cw : plugins.getPresencePlugins()){
			PresenceCommand command = (PresenceCommand) cw.command;
                        if(logger.isTraceEnabled()){logger.trace("Handling presence with: " + command);}
			command.handlePresence(presence);
		}
	}

	public void handleStatus(Status status) {
		if(muc == null) return;

		for(CommandWrapper cw : plugins.getPresencePlugins()){
			PresenceCommand command = (PresenceCommand) cw.command;
                        if(logger.isTraceEnabled()){logger.trace("Handling status with: " + command);}
			command.handleStatus(status);
		}
	}

	public void handleMessage(Message message) {
		if(muc == null) return;
		
		if(message.getBody().startsWith(prefix) || message.getBody().startsWith(connection.getNick())){
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
		if(muc == null) return;

		String body = message.getBody();
		if(body.startsWith(prefix)){
			body = body.substring(prefix.length());
		}else{ //(message.getBody().startsWith(connection.getNick()){
			body = body.substring(connection.getNick().length());
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
		if(muc == null) return;

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

    /**
	 * Returns {@link XmppConnection}.
	 *
	 * @return XmppConnection that handle connection to XMPP server and MUC.
	 */
	public XmppConnection getConnection() {
		return connection;
	}

	Plugins getPlugins() {
		return plugins;
	}

	public void privSet(String userJid, Integer decode) {
		privs.put(userJid.toLowerCase(), decode);		
	}

	void setConnection(XmppConnection xmppConnection) {
		this.connection = xmppConnection;
	}

    /**
     * Returns {@link Bot} instance.
	 *
	 * @return Bot instance.
	 */
    public Bot getBot(){
        return bot;
    }

	Timer getTimer() {
		return timer;
	}

	void stop() {
		timer.cancel();
		timer = new Timer();
	}

}
