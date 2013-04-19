package eu.janinko.xmppmuc;

import eu.janinko.xmppmuc.listeners.ConnectionListenerLogger;
import eu.janinko.xmppmuc.listeners.XmppConnectionListener;
import java.util.HashMap;
import java.util.Map;
import org.apache.log4j.Logger;
import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.ConnectionConfiguration.SecurityMode;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;

/**
 * This class handle connecting to XMPP server and MUC. It also handle
 * connection break and reconnects.
 *
 * @author Honza Brázdil <janinko.g@gmail.com>
 */
public class Connection {
	private static Logger logger = Logger.getLogger(Connection.class);
	
	private String server;
	private String jid;
	private String password;
	private HashMap<String, Room> rooms = new HashMap<>();

	private XMPPConnection connection;

	private boolean connected = false;
	private boolean recconnecting = false;

	public Connection() {
	}

	synchronized public void connect() {
		if (connected) {
			return;
		}
		if (server == null || jid == null || password == null) {
			throw new NullPointerException();
		}

		if (logger.isDebugEnabled()) {
			logger.debug("Connecting " + jid + " (" + server + ")");
		}

		ConnectionConfiguration conf = new ConnectionConfiguration(server);
		conf.setCompressionEnabled(true);
		conf.setReconnectionAllowed(false);
		conf.setRosterLoadedAtLogin(false);
		conf.setSecurityMode(SecurityMode.enabled);
		conf.setSendPresence(false);
		//conf.setDebuggerEnabled(true);

		connection = new XMPPConnection(conf);

		try {
			connection.connect();
			connection.login(jid, password);
		} catch (XMPPException e) {
			logger.error("Connection failed", e);
			connection = null;
			return;
		}

		connection.addConnectionListener(new XmppConnectionListener(this));

		// FOR DEBUG AND DEVEL ->
		connection.addConnectionListener(new ConnectionListenerLogger());
		// <- FOR DEBUG AND DEVEL

		connected = true;

		for(Room room : rooms.values()){
			room.connect();
		}
		
		recconnecting = true;

		logger.info("Connected");
	}

	synchronized public void disconnect() {
		connected = false;
		recconnecting = false;
		if (connection != null){
			connection.disconnect();
		}
		connection = null;
		logger.warn("Disconnected");
	}

	synchronized public void disconnected() {
		connected = false;
		connection = null;
	}

	synchronized public void reconnect() {
		if(!recconnecting) return;
		logger.info("Reconnecting");
		int retry = 1;
		connect();
		while (recconnecting && !isConnected()) {
			long sec = retry * 2 + 3;
			if (sec > 5 * 60) {
				sec = 5 * 60;
			}
			logger.warn("Recconect " + retry + " failed, waiting " + sec + " seconds.");
			
			try {
				this.wait(1000 * sec);
			} catch (InterruptedException e) {
				logger.error("Reconnecting interupted", e);
				return;
			}
			connect();
			retry++;
		}

	}

	/**
	 * Returns current server hostname.
	 *
	 * @return Server hostname.
	 */
	public String getServer() {
		return server;
	}

	/**
	 * Sets server hostname.
	 *
	 * @param server Server hostname.
	 * @return This instance of XmppConnection.
	 */
	public Connection setServer(String server) {
		this.server = server;
		return this;
	}

	/**
	 * Returns current jid.
	 *
	 * @return Jid.
	 */
	public String getJid() {
		return jid;
	}

	/**
	 * Sets jid.
	 *
	 * @param jid Jid.
	 * @return This instance of XmppConnection.
	 */
	public Connection setJid(String jid) {
		this.jid = jid;
		return this;
	}

	/**
	 * Returns current password.
	 *
	 * @return Password.
	 */
	public String getPassword() {
		return password;
	}

	/**
	 * Sets password.
	 *
	 * @param password Password.
	 * @return This instance of XmppConnection.
	 */
	public Connection setPassword(String password) {
		this.password = password;
		return this;
	}

	/**
	 * Returns current MUC room name.
	 *
	 * @return MUC room name.
	 */
	public Map<String,Room> getRooms() {
		return rooms;
	}

	/**
	 * Sets MUC room name.
	 *
	 * @param room MUC room name.
	 * @return This instance of XmppConnection.
	 */
	public Room addRoom(String room, String nick) {
		if(rooms.containsKey(room)) return rooms.get(room);
		Room rc = new Room(this, room, nick);
		rooms.put(room, rc);
		return rc;
	}

	public boolean isConnected() {
		return connected;
	}

	public XMPPConnection getXMPPConnection() {
		return connection;
	}

	public boolean isActive(){
		return recconnecting;
	}
}
