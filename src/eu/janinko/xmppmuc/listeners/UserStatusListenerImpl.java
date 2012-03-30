package eu.janinko.xmppmuc.listeners;

import org.apache.log4j.Logger;

import org.jivesoftware.smackx.muc.UserStatusListener;

import eu.janinko.xmppmuc.Xmppmuc;


public class UserStatusListenerImpl implements UserStatusListener {
	
	private Xmppmuc xmppmuc;
	private static Logger logger = Logger.getLogger(UserStatusListenerImpl.class);
	
	public UserStatusListenerImpl(Xmppmuc xmppmuc){
		this.xmppmuc = xmppmuc;
	}

	@Override
	public void kicked(String actor, String reason) {
		logger.debug("kicked: actor='"+actor+"', reason='"+reason+"'");
	}

	@Override
	public void voiceGranted() {
		logger.debug("voiceGranted");
	}

	@Override
	public void voiceRevoked() {
		logger.debug("voiceRevoked");
	}

	@Override
	public void banned(String actor, String reason) {
		logger.debug("banned: actor='"+actor+"', reason='"+reason+"'");
	}

	@Override
	public void membershipGranted() {
		logger.debug("membershipGranted");
	}

	@Override
	public void membershipRevoked() {
		logger.debug("membershipRevoked");
	}

	@Override
	public void moderatorGranted() {
		logger.debug("moderatorGranted");
	}

	@Override
	public void moderatorRevoked() {
		logger.debug("moderatorRevoked");
	}

	@Override
	public void ownershipGranted() {
		logger.debug("ownershipGranted");
	}

	@Override
	public void ownershipRevoked() {
		logger.debug("ownershipRevoked");
	}

	@Override
	public void adminGranted() {
		logger.debug("adminGranted");
	}

	@Override
	public void adminRevoked() {
		logger.debug("adminRevoked");
	}

}
