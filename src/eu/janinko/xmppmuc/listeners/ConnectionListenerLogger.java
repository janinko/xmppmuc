package eu.janinko.xmppmuc.listeners;

import org.apache.log4j.Logger;
import org.jivesoftware.smack.ConnectionListener;

public class ConnectionListenerLogger implements ConnectionListener {
	private static Logger logger = Logger.getLogger(ConnectionListenerLogger.class);


	public ConnectionListenerLogger( ) {}

	@Override
	public void connectionClosed() {
		logger.trace("connectionClosed");
	}

	@Override
	public void connectionClosedOnError(Exception e) {
		logger.warn("connectionClosedOnError", e);
	}

	@Override
	public void reconnectingIn(int seconds) {
		logger.trace("Reconnecting in "+seconds+" seconds");
	}

	@Override
	public void reconnectionFailed(Exception e) {
		logger.warn("reconnectionFailed",e);
	}

	@Override
	public void reconnectionSuccessful() {
		logger.trace("reconnectionSuccessful");
		/*int retry=3;
		while(retry-- > 0 && !xmppmuc.connectToMUC()){
			logger.error("failet to re-join MUC");
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				logger.error("Unexcepted interuption", e);
			}
		}*/
	}

}
