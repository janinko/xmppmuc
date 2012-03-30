package eu.janinko.xmppmuc.listeners;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.jivesoftware.smack.ConnectionListener;
import org.jivesoftware.smack.XMPPException;

import eu.janinko.xmppmuc.Xmppmuc;

public class ConnectionListenerImpl implements ConnectionListener {
	Xmppmuc xmppmuc;

	public ConnectionListenerImpl(Xmppmuc xmppmuc) {
		this.xmppmuc = xmppmuc;
	}

	@Override
	public void connectionClosed() {
		System.out.println("ConnectionListenerImpl.connectionClosed()");
	}

	@Override
	public void connectionClosedOnError(Exception e) {
		System.out.println("ConnectionListenerImpl.connectionClosedOnError(Exception e="+e+"):");
		e.printStackTrace();
		System.out.println(":ConnectionListenerImpl.connectionClosedOnError(Exception e="+e+")");
	}

	@Override
	public void reconnectingIn(int seconds) {
		System.out.println("ConnectionListenerImpl.reconnectingIn(int seconds="+seconds+")");
	}

	@Override
	public void reconnectionFailed(Exception e) {
		System.out.println("ConnectionListenerImpl.connectionClosedOnError(Exception e="+e+"):");
		e.printStackTrace();
		System.out.println(":ConnectionListenerImpl.connectionClosedOnError(Exception e="+e+")");
	}

	@Override
	public void reconnectionSuccessful() {
		System.out.println("ConnectionListenerImpl.reconnectionSuccessful()");
		try {
			System.out.println("PreJoin + " + (new SimpleDateFormat("yyyy/MM/dd HH:mm:ss.S")).format(new Date()));
			xmppmuc.getMuc().join(xmppmuc.getNick());
			System.out.println("PastJoin + " + (new SimpleDateFormat("yyyy/MM/dd HH:mm:ss.S")).format(new Date()));
		} catch (XMPPException e) {
			e.printStackTrace();
		}
	}

}
