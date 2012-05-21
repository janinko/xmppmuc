package eu.janinko.xmppmuc;

import org.apache.log4j.Logger;
import org.jivesoftware.smack.packet.Message;

import eu.janinko.xmppmuc.commands.Command;
import eu.janinko.xmppmuc.commands.PluginBuildException;

public class PluginManagerCommand implements Command {
	CommandWrapper cw;
	PluginManager pm;
	
	private static Logger logger = Logger.getLogger(PluginManagerCommand.class);
	
	public PluginManagerCommand(){};
	
	public PluginManagerCommand(CommandWrapper commandWrapper){
		cw = commandWrapper;
		pm = cw.getMucCommands().pm;
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
	public void handle(Message m, String[] args) {
		logger.debug("got command: " + args);
		if(args[1].equals("stop")){
			logger.info("request stop command: " + args[2]);
			if(pm.removeCommand(args[2])){
				cw.sendMessage("Plugin " + args[2] + " byl zastaven.");
			}
		}else if(args[1].equals("load")){
			logger.info("request load command: " + args[2]);
			if(pm.loadPlugin(args[2])){
				cw.sendMessage("Plugin " + args[2] + " byl načten.");
			}
		}else if(args[1].equals("start")){
			logger.info("request start command: " + args[2]);
			if(pm.startPlugin(args[2])){
				cw.sendMessage("Plugin " + args[2] + " byl spuštěn.");
			}
		}
	}

	@Override
	public String help(String prefix) {
		return null;
	}

	@Override
	public int getPrivLevel() {
		return 100;
	}

	@Override
	public void destroy() {
	}

}
