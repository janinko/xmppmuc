package eu.janinko.xmppmuc.commands;

import eu.janinko.xmppmuc.Message;
import eu.janinko.xmppmuc.api.plugin.AbstractCommand;
import org.apache.log4j.Logger;

public class PluginManagerCommand extends AbstractCommand {
	private static Logger logger = Logger.getLogger(PluginManagerCommand.class);

	@Override
	public String getCommand() {
		return "pm";
	}

	@Override
	public void handle(Message m, String[] args) {
		switch(args.length){
			case 0:
			case 1:
				cw.sendHelp();
				return;
			default: // >=3
				switch (args[1]) {
					case "stop":
						logger.info("request stop command: " + args[2]);
						cw.getCommands().getPlugins().stopPlugin(args[2]);
						cw.sendMessage("Plugin " + args[2] + " byl zastaven.");
						return;
					case "start":
						logger.info("request start command: " + args[2]);
						if(cw.getCommands().getPlugins().startPlugin(args[2])){
							cw.sendMessage("Plugin " + args[2] + " byl spuštěn.");
						}
						return;
				}
			case 2:
				switch (args[1]) {
					case "reload":
						cw.getCommands().getPlugins().reload();
						cw.sendMessage("Plugin " + args[2] + " byl zastaven.");
					return;
				}
				break;
		}
		cw.sendHelp();
	}

	@Override
	public String help(String prefix) {
		return prefix + getCommand() + " ((stop|start) command|reload)";
	}

	@Override
	public int getPrivLevel() {
		return 100;
	}
}
