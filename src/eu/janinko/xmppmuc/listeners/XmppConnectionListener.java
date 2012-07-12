package eu.janinko.xmppmuc.listeners;

import org.jivesoftware.smack.ConnectionListener;

import eu.janinko.xmppmuc.XmppConnection;

public class XmppConnectionListener implements ConnectionListener {
	XmppConnection connection;
	
	public XmppConnectionListener(XmppConnection connection){
		this.connection = connection;
	}

	@Override
	public void connectionClosed() {
		connection.disconnect();
	}

	@Override
	public void connectionClosedOnError(Exception arg0) {
		connection.disconnect();
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
