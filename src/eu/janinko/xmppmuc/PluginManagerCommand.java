package eu.janinko.xmppmuc;

import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Message;

import eu.janinko.xmppmuc.commands.Command;
import eu.janinko.xmppmuc.commands.PluginBuildException;

public class PluginManagerCommand implements Command {
	MucCommands mucc;
	PluginManager pm;
	
	public PluginManagerCommand(){};
	
	public PluginManagerCommand(MucCommands mucCommands){
		mucc = mucCommands;
		pm = mucc.pm;
	}

	@Override
	public Command build(MucCommands mucCommands) throws PluginBuildException {
		return new PluginManagerCommand(mucCommands);
	}

	@Override
	public String getCommand() {
		return "pm";
	}

	@Override
	public void handle(Message m, String[] args) {
		try {
			if(args[1].equals("stop")){
				if(pm.removeCommand(args[2])){
					mucc.getMuc().sendMessage("Plugin " + args[2] + " byl zastaven.");
				}
			}else if(args[1].equals("load")){
				if(pm.loadPlugin(args[2])){
					mucc.getMuc().sendMessage("Plugin " + args[2] + " byl načten.");
				}
			}else if(args[1].equals("start")){
				if(pm.startPlugin(args[2])){
					mucc.getMuc().sendMessage("Plugin " + args[2] + " byl spuštěn.");
				}
			}
		} catch (XMPPException e) {
			System.err.println("PluginManager.handleCommand() A");
			e.printStackTrace();
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
