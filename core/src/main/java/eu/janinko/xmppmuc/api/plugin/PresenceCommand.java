package eu.janinko.xmppmuc.api.plugin;

import eu.janinko.xmppmuc.Status;
import org.jivesoftware.smack.packet.Presence;

public interface PresenceCommand extends Command {

	/** Handle presence.
	 * This method is invoked when presence packet is received.
	 *
	 * @param m Received message.
	 */
	void handlePresence(Presence p);

	/** Handle status.
	 * This method is invoked when presence packet is received.
	 *
	 * @param m Received message.
	 */
	void handleStatus(Status s);
}
