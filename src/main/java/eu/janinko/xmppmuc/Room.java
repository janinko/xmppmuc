package eu.janinko.xmppmuc;

import eu.janinko.xmppmuc.listeners.InvitationRejectionListenerLogger;
import eu.janinko.xmppmuc.listeners.PacketListenerLogger;
import eu.janinko.xmppmuc.listeners.ParticipantStatusListenerLogger;
import eu.janinko.xmppmuc.listeners.PluginsPacketListener;
import eu.janinko.xmppmuc.listeners.PluginsParticipantStatusListener;
import eu.janinko.xmppmuc.listeners.SubjectUpdatedListenerLogger;
import eu.janinko.xmppmuc.listeners.UserStatusListenerLogger;
import org.apache.log4j.Logger;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.filter.FromMatchesFilter;
import org.jivesoftware.smackx.muc.DiscussionHistory;
import org.jivesoftware.smackx.muc.MultiUserChat;

/**
 * @author Honza Brázdil <jbrazdil@redhat.com>
 */
public class Room {
	private static Logger logger = Logger.getLogger(Room.class);
	private String room;
	private String nick;
	private Connection connection;
	private MultiUserChat muc;
	private Commands commands;

	private FromMatchesFilter messageFilter;

	private static final DiscussionHistory dh = new DiscussionHistory();
	static{ dh.setMaxChars(0);}

	public Room(Connection connection, String room, String nick) {
		this.connection = connection;
		this.room = room;
		this.nick = nick;
		this.messageFilter = new FromMatchesFilter(room);
	}

	public boolean connect(){
		muc = new MultiUserChat(connection.getXMPPConnection(), room);
		try {
			muc.join(nick, null, dh, 5000);
		} catch (XMPPException e) {
			logger.error("Connection to room failed", e);
			return false;
		}
		
		// FOR DEBUG AND DEVEL ->
		muc.addUserStatusListener(new UserStatusListenerLogger());
		muc.addInvitationRejectionListener(new InvitationRejectionListenerLogger());
		muc.addMessageListener(new PacketListenerLogger());
		muc.addParticipantListener(new PacketListenerLogger());
		muc.addParticipantStatusListener(new ParticipantStatusListenerLogger());
		muc.addSubjectUpdatedListener(new SubjectUpdatedListenerLogger());
		// <- FOR DEBUG AND DEVEL

		connection.getXMPPConnection().addPacketListener(new PluginsPacketListener(commands), messageFilter);
		muc.addParticipantStatusListener(new PluginsParticipantStatusListener(commands));
		return true;
	}

	public void setCommands(Commands commands){
		this.commands = commands;
	}

	/**
	 * Sets bot nickname.
	 *
	 * @param nick Bot nickname.
	 * @return This instance of XmppConnection.
	 */
	public Room setNick(String nick) {
		this.nick = nick;
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

	public String getRoom(){
		return room;
	}

	public void sendMessage(String message) {
		if (!connection.isConnected()) {
			return;
		}
		try {
			muc.sendMessage(message);
		} catch (XMPPException e) {
			logger.error("Failed to send message", e);
		}
	}

	public void sendsMessage(org.jivesoftware.smack.packet.Message message) {
		if (!connection.isConnected()) {
			return;
		}
		try {
			muc.sendMessage(message);
		} catch (XMPPException e) {
			logger.error("Failed to send message", e);
		}
	}

	/**
	 * Returns current {@link MultiUserChat} we are connected into.
	 *
	 * @return MUC object to which we ar connected or null.
	 */
	public MultiUserChat getMuc() {
		if (!connection.isConnected()) {
			return null;
		}
		return muc;
	}

	public boolean conected() {
		return connection.isConnected();
	}

	public Connection getConnection() {
		return connection;
	}
}
