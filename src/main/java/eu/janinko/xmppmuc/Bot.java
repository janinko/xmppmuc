package eu.janinko.xmppmuc;

import org.apache.log4j.Logger;

/**
 * The XMPP MUX bot.
 *
 * @author Honza Brázdil <janinko.g@gmail.com>
 */
public class Bot {
	public static final String DATA_DIR = System.getProperty("user.home") + "/.xmppmuc/";
	private Connection connection;
	private Commands commands;
	
	private static Logger logger = Logger.getLogger(Bot.class);

	/**
	 * Creates new Bot instance.
	 *
	 */
	public Bot() {
	}

}
