package eu.janinko.xmppmuc;

import java.util.Collection;

import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.ConnectionListener;
import org.jivesoftware.smack.XMPPConnection;

public class XMPPConnection2 extends XMPPConnection {

	public XMPPConnection2(ConnectionConfiguration config) {
		super(config);
	}
	
	public Collection<ConnectionListener> getConnectionListeners(){
		return super.getConnectionListeners();
	}

}
