package eu.janinko.xmppmuc.listeners;

import org.apache.log4j.Logger;

import org.jivesoftware.smackx.muc.ParticipantStatusListener;

import eu.janinko.xmppmuc.Xmppmuc;

public class ParticipantStatusListenerImpl implements ParticipantStatusListener {
	
	private Xmppmuc xmppmuc;
	private static Logger logger = Logger.getLogger(ParticipantStatusListenerImpl.class);

	public ParticipantStatusListenerImpl(Xmppmuc xmppmuc){
		this.xmppmuc = xmppmuc;
	}

	@Override
	public void joined(String participant) {
		logger.debug("joined: participant='"+participant+"'");
	}

	@Override
	public void left(String participant) {
		logger.debug("left: participant='"+participant+"'");
	}

	@Override
	public void kicked(String participant, String actor, String reason) {
		logger.debug("kicked: participant='"+participant+"', actor='"+actor+"', reason='" + reason + "'");
	}

	@Override
	public void voiceGranted(String participant) {
		logger.debug("voiceGranted: participant='"+participant+"'");
	}

	@Override
	public void voiceRevoked(String participant) {
		logger.debug("voiceRevoked: participant='"+participant+"'");
	}

	@Override
	public void banned(String participant, String actor, String reason) {
		logger.debug("banned: participant='"+participant+"', actor='"+actor+"', reason='" + reason + "'");
	}

	@Override
	public void membershipGranted(String participant) {
		logger.debug("membershipGranted: participant='"+participant+"'");
	}

	@Override
	public void membershipRevoked(String participant) {
		logger.debug("membershipRevoked: participant='"+participant+"'");
	}

	@Override
	public void moderatorGranted(String participant) {
		logger.debug("moderatorGranted: participant='"+participant+"'");
	}

	@Override
	public void moderatorRevoked(String participant) {
		logger.debug("moderatorRevoked: participant='"+participant+"'");
	}

	@Override
	public void ownershipGranted(String participant) {
		logger.debug("ownershipGranted: participant='"+participant+"'");
	}

	@Override
	public void ownershipRevoked(String participant) {
		logger.debug("ownershipRevoked: participant='"+participant+"'");
	}

	@Override
	public void adminGranted(String participant) {
		logger.debug("adminGranted: participant='"+participant+"'");
	}

	@Override
	public void adminRevoked(String participant) {
		logger.debug("adminRevoked: participant='"+participant+"'");
	}

	@Override
	public void nicknameChanged(String participant, String newNickname) {
		logger.debug("nicknameChanged: participant='"+participant+"', newNickname='"+newNickname+"'");
	}

}
