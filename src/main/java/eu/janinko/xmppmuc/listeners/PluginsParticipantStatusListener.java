/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.janinko.xmppmuc.listeners;

import eu.janinko.xmppmuc.Commands;
import eu.janinko.xmppmuc.Status;
import org.apache.log4j.Logger;
import org.jivesoftware.smackx.muc.ParticipantStatusListener;

/**
 *
 * @author jbrazdil
 */
public class PluginsParticipantStatusListener implements ParticipantStatusListener{
	private Commands commands;
	
	private Logger logger = Logger.getLogger(PluginsParticipantStatusListener.class);
	
	public PluginsParticipantStatusListener(Commands commands){
		this.commands = commands;
	}

	@Override
	public void joined(String participant) {
		commands.handleStatus(new Status(Status.Type.joined, participant));
	}

	@Override
	public void left(String participant) {
		commands.handleStatus(new Status(Status.Type.left, participant));
	}

	@Override
	public void kicked(String participant, String actor, String reason) {
		commands.handleStatus(new Status(Status.Type.kicked, participant, actor, reason));
	}

	@Override
	public void voiceGranted(String participant) {
		commands.handleStatus(new Status(Status.Type.voiceGranted, participant));
	}

	@Override
	public void voiceRevoked(String participant) {
		commands.handleStatus(new Status(Status.Type.voiceRevoked, participant));
	}

	@Override
	public void banned(String participant, String actor, String reason) {
		commands.handleStatus(new Status(Status.Type.banned, participant, actor, reason));
	}

	@Override
	public void membershipGranted(String participant) {
		commands.handleStatus(new Status(Status.Type.membershipGranted, participant));
	}

	@Override
	public void membershipRevoked(String participant) {
		commands.handleStatus(new Status(Status.Type.membershipRevoked, participant));
	}

	@Override
	public void moderatorGranted(String participant) {
		commands.handleStatus(new Status(Status.Type.moderatorGranted, participant));
	}

	@Override
	public void moderatorRevoked(String participant) {
		commands.handleStatus(new Status(Status.Type.moderatorRevoked, participant));
	}

	@Override
	public void ownershipGranted(String participant) {
		commands.handleStatus(new Status(Status.Type.ownershipGranted, participant));
	}

	@Override
	public void ownershipRevoked(String participant) {
		commands.handleStatus(new Status(Status.Type.ownershipRevoked, participant));
	}

	@Override
	public void adminGranted(String participant) {
		commands.handleStatus(new Status(Status.Type.adminGranted, participant));
	}

	@Override
	public void adminRevoked(String participant) {
		commands.handleStatus(new Status(Status.Type.adminRevoked, participant));
	}

	@Override
	public void nicknameChanged(String participant, String newNickname) {
		commands.handleStatus(new Status(Status.Type.nicknameChanged, participant, newNickname));
	}
	
}
