package eu.janinko.xmppmuc;

import eu.janinko.xmppmuc.commands.AbstractCommand;
import eu.janinko.xmppmuc.commands.Command;
import eu.janinko.xmppmuc.commands.PluginBuildException;
import java.util.Arrays;
import org.apache.log4j.Logger;

public class PluginManagerCommand extends AbstractCommand {
	CommandWrapper cw;
	Plugins plugins;
	
	private static Logger logger = Logger.getLogger(PluginManagerCommand.class);
	
	public PluginManagerCommand(){};
	
	public PluginManagerCommand(CommandWrapper commandWrapper){
		cw = commandWrapper;
	}

	@Override
	public Command build(CommandWrapper cw) throws PluginBuildException {
		return new PluginManagerCommand(cw);
	}

	@Override
	public String getCommand() {
		return "pm";
	}

	@Override
	public void connected(){
		plugins = cw.getCommands().getPlugins();
	}

	@Override
	public void handle(Message m, String[] args) {
		if(args[1].equals("stop")){
			logger.info("request stop command: " + args[2]);
			if(plugins.removeCommand(args[2])){
				cw.sendMessage("Plugin " + args[2] + " byl zastaven.");
			}
		}else if(args[1].equals("load")){
			logger.info("request load command: " + args[2]);
			if(plugins.loadPlugin(args[2])){
				cw.sendMessage("Plugin " + args[2] + " byl načten.");
			}
		}else if(args[1].equals("start")){
			logger.info("request start command: " + args[2]);
			if(plugins.startPlugin(args[2])){
				cw.sendMessage("Plugin " + args[2] + " byl spuštěn.");
			}
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
