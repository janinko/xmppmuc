package eu.janinko.xmppmuc.api.plugin;

import eu.janinko.xmppmuc.Message;

public interface MessageCommand extends Command{

	/** Handle message.
	 * This method is invoked when message is sent to MUC.
	 *
	 * @param m Received message.
	 */
	void handleMessage(Message m);
}
