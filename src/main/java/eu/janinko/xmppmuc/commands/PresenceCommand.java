package eu.janinko.xmppmuc.commands;

import eu.janinko.xmppmuc.Status;
import org.jivesoftware.smack.packet.Presence;

public interface PresenceCommand extends Command {

	void handlePresence(Presence p);

	void handleStatus(Status s);
}
