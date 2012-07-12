package eu.janinko.xmppmuc.listeners;

import org.apache.log4j.Logger;
import org.jivesoftware.smackx.muc.InvitationRejectionListener;

public class InvitationRejectionListenerLogger implements
		InvitationRejectionListener {
	
	private static Logger logger = Logger.getLogger(InvitationRejectionListenerLogger.class);

	public InvitationRejectionListenerLogger(){
	}

	@Override
	public void invitationDeclined(String invitee, String reason) {
		logger.debug("invitationDeclined: invitee='"+invitee+"', reason='"+reason+"'");
	}

}
