package eu.janinko.xmppmuc.listeners;

import org.apache.log4j.Logger;

import org.jivesoftware.smackx.muc.InvitationRejectionListener;

import eu.janinko.xmppmuc.Xmppmuc;

public class InvitationRejectionListenerImpl implements
		InvitationRejectionListener {
	
	private Xmppmuc xmppmuc;
	private static Logger logger = Logger.getLogger(InvitationRejectionListenerImpl.class);

	public InvitationRejectionListenerImpl(Xmppmuc xmppmuc){
		this.xmppmuc = xmppmuc;
	}

	@Override
	public void invitationDeclined(String invitee, String reason) {
		logger.debug("invitationDeclined: invitee='"+invitee+"', reason='"+reason+"'");
	}

}
