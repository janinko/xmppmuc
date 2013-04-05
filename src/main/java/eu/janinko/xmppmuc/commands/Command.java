package eu.janinko.xmppmuc.commands;


import eu.janinko.xmppmuc.CommandWrapper;
import eu.janinko.xmppmuc.Message;

/** Interface all plugins must implement.
 * 
 * @author Honza Brázdil <janinko.g@gmail.com>
 * @version 0.1
 * @see AbstractCommand
 */
public interface Command {

	/** Provides new {@link Command} and with wrapper.
	 * This method is invoked when plugin is loaded.
	 * 
	 * @param commandWrapper Wrapper for this {@link Command}.
	 */
	void setWrapper(CommandWrapper commandWrapper);
	
	/** Returns muc command string.
	 * 
	 * @return String command that invokes this {@link Command}.
	 */
	String getCommand();
	
	/** Handle command.
	 * This method is invoked when message starting with command prefix followed by command.
	 * 
	 * @param m Received command message.
	 * @param args Array of words (command arguments). Eg: when handling command ".help foo bar" args is: ["help","foo","bar"].
	 *             args[0] is command name
	 * @see eu.janinko.xmppmuc.Bot#setPrefix
	 */
	void handle(Message m, String[] args);

	/** Returns help for this plugin.  
	 * 
	 * @param prefix Current command prefix. May be used for generating help message.
	 * @return Help for plugin.
	 */
	String help(String prefix);
	
	/** Returns minimal priv level required for this plugin.
	 * 
	 * @return Required priv level.
	 * @see Privset
	 */
	int getPrivLevel();

	/** This method is invoked when bot connects to MUC.
	 */
	void connected();

	/** This method is invoked when bot disconnects from MUC.
	 */
	void disconnected();

}
