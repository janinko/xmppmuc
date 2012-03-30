package eu.janinko.xmppmuc;

import java.io.IOException;
import java.net.URL;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.log4j.Logger;
import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.ConnectionConfiguration.SecurityMode;
import org.jivesoftware.smack.ConnectionListener;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smackx.muc.MultiUserChat;

import eu.janinko.xmppmuc.listeners.ChatManagerListenerMucCommand;
import eu.janinko.xmppmuc.listeners.ConnectionListenerImpl;
import eu.janinko.xmppmuc.listeners.InvitationRejectionListenerImpl;
import eu.janinko.xmppmuc.listeners.PacketListenerConsole;
import eu.janinko.xmppmuc.listeners.PacketListenerImpl;
import eu.janinko.xmppmuc.listeners.ParticipantStatusListenerImpl;
import eu.janinko.xmppmuc.listeners.SubjectUpdatedListenerImpl;
import eu.janinko.xmppmuc.listeners.UserStatusListenerImpl;

public class Xmppmuc {
	private XMPPConnection2 connection;
	private MultiUserChat muc;
	private ConnectionConfiguration conf;
	
	private String server;
	private String prefix;
	private String jid;
	private String pass;
	private String room;
	private String nick;
	
	private static Logger logger = Logger.getLogger(Xmppmuc.class);

	public Xmppmuc() {
		
		
	}
	
	public boolean connect(){
		//Connection.DEBUG_ENABLED = true;

		conf = new ConnectionConfiguration(server);
		conf.setCompressionEnabled(true);
		//conf.setDebuggerEnabled(true);
		conf.setReconnectionAllowed(true);
		conf.setRosterLoadedAtLogin(false);
		conf.setSecurityMode(SecurityMode.enabled);
		conf.setSendPresence(false);
		
		connection = new XMPPConnection2(conf);
		try {
			connection.connect();
			connection.login(jid,pass);
		} catch (XMPPException e) {
			System.err.println("Xmppmuc.connect() A");
			e.printStackTrace();
			connection = null;
			return false;
		}
		connection.addConnectionListener(new ConnectionListenerImpl(this));
		
		
	    muc = new MultiUserChat(connection, room);
	    try {
			muc.join(nick);
		} catch (XMPPException e) {
			System.err.println("Xmppmuc.connect() B");
			e.printStackTrace();
			muc = null;
			connection = null;
			return false;
		}
	    muc.addUserStatusListener(new UserStatusListenerImpl(this));
	    muc.addInvitationRejectionListener(new InvitationRejectionListenerImpl(this));
	    muc.addMessageListener(new PacketListenerImpl(this));
	    muc.addParticipantListener(new PacketListenerImpl(this));
	    muc.addParticipantStatusListener(new ParticipantStatusListenerImpl(this));
	    muc.addSubjectUpdatedListener(new SubjectUpdatedListenerImpl(this));
	    
		MucCommands mucCommands = new MucCommands(prefix);
		MucSeer mucSeer = new MucSeer();
	    
	    muc.addMessageListener(new PacketListenerConsole(mucCommands, mucSeer,muc));
	    muc.addParticipantListener(new PacketListenerConsole(mucCommands, mucSeer,muc));
	    
		connection.getChatManager().addChatListener(new ChatManagerListenerMucCommand(room,mucCommands));
		
	    try {
		    mucCommands.setMUC(muc);
			mucSeer.setMUC(muc);
		} catch (XMPPException e) {
			System.err.println("Xmppmuc.connect() C");
			e.printStackTrace();
			muc = null;
			connection = null;
			return false;
		}
		

	    try {
			RssReader.lunchRssFeed(new URL("http://www.andaria.cz/rss_novinky.php"),muc,"Novinky");
			RssReader.lunchRssFeed(new URL("http://www.andaria.cz/rss_trubaci.php"),muc,"Trubači");
		    RssReader.lunchRssFeed(new URL("http://www.andaria.cz/rss_prohresky.php"),muc,"Prohřešky",
		               RssReader.AUTHOR | RssReader.CONTENT | RssReader.LINK | RssReader.TITLE);
		} catch (IOException e) {
			System.err.println("Xmppmuc.connect() F");
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		}

		for(ConnectionListener  cl : connection.getConnectionListeners()){
			System.out.println(cl + " --- " + cl.getClass());
		}
		
		return true;
	}

	public void setServer(String server) {
		this.server = server;
	}

	public void setPrefix(String prefix) {
		this.prefix=prefix;
	}

	public void setJid(String jid) {
		this.jid=jid;
	}

	public void setPass(String pass) {
		this.pass=pass;
	}

	public void setRoom(String room) {
		this.room=room;
	}

	public void setNick(String nick) {
		this.nick=nick;
	}

	public String getNick() {
		return nick;
	}

	public void sendMessage(String message) {
		try {
			muc.sendMessage(message);
		} catch (XMPPException e) {
			System.err.println("Xmppmuc.sendMessage() A");
			e.printStackTrace();
		}
	}
	

	public MultiUserChat getMuc() {
		return muc;
	}
}
