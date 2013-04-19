package eu.janinko.xmppmuc.listeners;

import eu.janinko.xmppmuc.Connection;
import org.jivesoftware.smack.ConnectionListener;

public class XmppConnectionListener implements ConnectionListener {
	Connection connection;
	
	public XmppConnectionListener(Connection connection){
		this.connection = connection;
	}

	@Override
	public void connectionClosed() {
		connection.disconnected();
		connection.reconnect();
	}

	@Override
	public void connectionClosedOnError(Exception arg0) {
		connection.disconnected();
		connection.reconnect();
	}

	@Override
	public void reconnectingIn(int arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void reconnectionFailed(Exception arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void reconnectionSuccessful() {
		// TODO Auto-generated method stub

	}

}
