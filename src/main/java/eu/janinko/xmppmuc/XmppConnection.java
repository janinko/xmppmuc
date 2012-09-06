package eu.janinko.xmppmuc;

import eu.janinko.xmppmuc.listeners.ConnectionListenerLogger;
import eu.janinko.xmppmuc.listeners.InvitationRejectionListenerLogger;
import eu.janinko.xmppmuc.listeners.PacketListenerLogger;
import eu.janinko.xmppmuc.listeners.ParticipantStatusListenerLogger;
import eu.janinko.xmppmuc.listeners.PluginsPacketListener;
import eu.janinko.xmppmuc.listeners.PluginsParticipantStatusListener;
import eu.janinko.xmppmuc.listeners.SubjectUpdatedListenerLogger;
import eu.janinko.xmppmuc.listeners.UserStatusListenerLogger;
import eu.janinko.xmppmuc.listeners.XmppConnectionListener;
import org.apache.log4j.Logger;
import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.ConnectionConfiguration.SecurityMode;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.filter.FromMatchesFilter;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smackx.muc.MultiUserChat;

/**
 * This class handle connecting to XMPP server and MUC. It also handle
 * connection break and reconnects.
 *
 * @author Honza Brázdil <janinko.g@gmail.com>
 */
public class XmppConnection {
	private String server;
	private String jid;
	private String password;
	private String room;
	private String nick;

	private Commands commands;
	private FromMatchesFilter messageFilter;

	XMPPConnection connection;
	private MultiUserChat muc;

	private boolean connected = false;
	private boolean alive = true;

	private static Logger logger = Logger.getLogger(XmppConnection.class);

	public XmppConnection(Commands commands) {
		this.commands = commands;
		commands.setConnection(this);
	}

	XmppConnection(XmppConnection connection) {
		this.server = connection.server;
		this.jid = connection.jid;
		this.password = connection.password;
		this.room = connection.room;
		this.nick = connection.nick;

		this.commands = connection.commands;
		this.messageFilter = connection.messageFilter;
	}

	public boolean connect() {
		if (!alive) {
			return false;
		}
		connected = false;
		connection = null;
		muc = null;
		if (server == null || jid == null || password == null || room == null || nick == null) {
			throw new NullPointerException();
		}

		if (logger.isDebugEnabled()) {
			logger.debug("Connecting " + jid + " (" + server + ")" + " as " + nick + " to " + room + " with password '" + password + "'");
		}

		ConnectionConfiguration conf = new ConnectionConfiguration(server);
		conf.setCompressionEnabled(true);
		//conf.setDebuggerEnabled(true);
		conf.setReconnectionAllowed(false);
		conf.setRosterLoadedAtLogin(false);
		conf.setSecurityMode(SecurityMode.enabled);
		conf.setSendPresence(false);

		connection = new XMPPConnection(conf);

		try {
			connection.connect();
			connection.login(jid, password);
		} catch (XMPPException e) {
			logger.error("Connection failed", e);
			return false;
		}

		muc = new MultiUserChat(connection, room);
		try {
			muc.join(nick);
		} catch (XMPPException e) {
			logger.error("Connection to room failed", e);
			return false;
		}

		connection.addConnectionListener(new XmppConnectionListener(this));

		// FOR DEBUG AND DEVEL ->
		connection.addConnectionListener(new ConnectionListenerLogger());
		muc.addUserStatusListener(new UserStatusListenerLogger());
		muc.addInvitationRejectionListener(new InvitationRejectionListenerLogger());
		muc.addMessageListener(new PacketListenerLogger());
		muc.addParticipantListener(new PacketListenerLogger());
		muc.addParticipantStatusListener(new ParticipantStatusListenerLogger());
		muc.addSubjectUpdatedListener(new SubjectUpdatedListenerLogger());
		// <- FOR DEBUG AND DEVEL

		connected = true;
		logger.info("Connected");
		commands.setMuc(muc);
		connection.addPacketListener(new PluginsPacketListener(commands), messageFilter);
		muc.addParticipantStatusListener(new PluginsParticipantStatusListener(commands));
		return true;
	}

	public void disconnect() {
		connected = false;
		commands.setMuc(null);
		logger.warn("Disconnected");
		reconnect();
	}

	private void reconnect() {
		if (!alive) {
			return;
		}
		logger.info("Reconnecting");
		int retry = 1;
		while (alive && !connect()) {
			int sec = retry * 2 + 3;
			if (sec > 5 * 60) {
				sec = 5 * 60;
			}
			logger.warn("Recconect " + retry + " failed, waiting " + sec + " seconds.");
			try {
				Thread.sleep(1000 * sec);
			} catch (InterruptedException e) {
				logger.error("Reconnecting interupted", e);
				return;
			}
			retry++;
		}

	}

	public void stop() {
		alive = false;
		if (connected) {
			commands.setMuc(null);
			muc.leave();
			muc = null;
			connection.disconnect();
			connection = null;
			connected = false;
		}
	}

	public void sendMessage(String message) {
		if (!connected) {
			return;
		}
		try {
			muc.sendMessage(message);
		} catch (XMPPException e) {
			logger.error("Failed to send message", e);
		}
	}

	public void sendsMessage(Message message) {
		if (!connected) {
			return;
		}
		try {
			muc.sendMessage(message);
		} catch (XMPPException e) {
			logger.error("Failed to send message", e);
		}
	}

	public boolean isConnected() {
		return connected;
	}

	/**
	 * Returns current {@link MultiUserChat} we are connected into.
	 *
	 * @return MUC object to which we ar connected or null.
	 */
	public MultiUserChat getMuc() {
		if (!connected) {
			return null;
		}
		return muc;
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
	public XmppConnection setServer(String server) {
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
	public XmppConnection setJid(String jid) {
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
	public XmppConnection setPassword(String password) {
		this.password = password;
		return this;
	}

	/**
	 * Returns current MUC room name.
	 *
	 * @return MUC room name.
	 */
	public String getRoom() {
		return room;
	}

	/**
	 * Sets MUC room name.
	 *
	 * @param room MUC room name.
	 * @return This instance of XmppConnection.
	 */
	public XmppConnection setRoom(String room) {
		this.room = room;
		messageFilter = new FromMatchesFilter(room);
		return this;
	}

	/**
	 * Returns current bot nickname.
	 *
	 * @return Bot nickname.
	 */
	public String getNick() {
		return nick;
	}

	/**
	 * Sets bot nickname.
	 *
	 * @param nick Bot nickname.
	 * @return This instance of XmppConnection.
	 */
	public XmppConnection setNick(String nick) {
		this.nick = nick;
		return this;
	}
}
