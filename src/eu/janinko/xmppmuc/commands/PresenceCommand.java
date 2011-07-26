package eu.janinko.xmppmuc.commands;

import org.jivesoftware.smack.packet.Presence;

public interface PresenceCommand extends Command {

	void handlePresence(Presence p);
}
