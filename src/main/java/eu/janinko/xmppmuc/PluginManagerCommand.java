package eu.janinko.xmppmuc;

import eu.janinko.xmppmuc.commands.AbstractCommand;
import org.apache.log4j.Logger;

public class PluginManagerCommand extends AbstractCommand {
	private static Logger logger = Logger.getLogger(PluginManagerCommand.class);
	
	@Override
	public String getCommand() {
		return "pm";
	}

	@Override
	public void handle(Message m, String[] args) {
		switch (args[1]) {
			case "stop":
				logger.info("request stop command: " + args[2]);
				cw.getCommands().getPlugins().stopPlugin(args[2]);
				cw.sendMessage("Plugin " + args[2] + " byl zastaven.");
				break;
			case "start":
				logger.info("request start command: " + args[2]);
				if(cw.getCommands().getPlugins().startPlugin(args[2])){
					cw.sendMessage("Plugin " + args[2] + " byl spuštěn.");
				}
				break;
		}
	}

	@Override
	public String help(String prefix) {
		return prefix + getCommand() + " ((stop|start) command|load fullname)";
	}

	@Override
	public int getPrivLevel() {
		return 100;
	}
}
