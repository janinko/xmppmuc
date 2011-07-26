package eu.janinko.xmppmuc;

import java.io.IOException;

import org.apache.commons.httpclient.URIException;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smackx.muc.MultiUserChat;

import yarfraw.core.datamodel.YarfrawException;

public class Xmppmuc {
	private XMPPConnection connection;
	private MultiUserChat muc;
	
	private String server;
	private String prefix;
	private String jid;
	private String pass;
	private String room;
	private String nick;

	public Xmppmuc() {
		
		
	}
	
	public boolean connect(){
		connection = new XMPPConnection(server);
		try {
			connection.connect();
			connection.login(jid,pass);
		} catch (XMPPException e) {
			e.printStackTrace();
			connection = null;
			return false;
		}
	    muc = new MultiUserChat(connection, room);
	    try {
			muc.join(nick);
		} catch (XMPPException e) {
			e.printStackTrace();
			muc = null;
			connection = null;
			return false;
		}
	    
		MucCommands mucCommands = new MucCommands(prefix);
		MucSeer mucSeer = new MucSeer();
	    
	    muc.addMessageListener(new PacketListenerConsole(mucCommands, mucSeer,muc));
	    muc.addParticipantListener(new PacketListenerConsole(mucCommands, mucSeer,muc));
	    
		connection.getChatManager().addChatListener(new ChatManagerListenerMucCommand(room,mucCommands));
		
	    try {
		    mucCommands.setMUC(muc);
			mucSeer.setMUC(muc);
		} catch (XMPPException e) {
			e.printStackTrace();
			muc = null;
			connection = null;
			return false;
		}
		

	    try {
			RssReader.lunchRssFeed("http://www.andaria.cz/rss_trubaci.php",muc,"Trubači");
		    RssReader.lunchRssFeed("http://www.andaria.cz/rss_prohresky.php",muc,"Prohřešky",
		               RssReader.AUTHOR | RssReader.CONTENT | RssReader.LINK | RssReader.TITLE);
		} catch (URIException e) {
			e.printStackTrace();
		} catch (YarfrawException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
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

	public void sendMessage(String message) {
		try {
			muc.sendMessage(message);
		} catch (XMPPException e) {
			e.printStackTrace();
		}
	}
}
