package eu.janinko.xmppmuc.commands;

import eu.janinko.xmppmuc.Message;
import eu.janinko.xmppmuc.api.plugin.AbstractCommand;
import eu.janinko.xmppmuc.api.plugin.MessageCommand;
import org.apache.log4j.Logger;

public class MessageLogger extends AbstractCommand implements MessageCommand{
	private boolean logging = true;
    private static Logger logger = Logger.getLogger(MessageLogger.class);
	
    @Override
	public String getCommand() {
		return "lg";
	}

    @Override
	public void handle(Message m, String[] args) {
		if(args.length == 2){
			switch (args[1]) {
				case "start":
					logging = true;
					break;
				case "stop":
					logging = false;
					break;
			}
		}
	}

    @Override
	public String help(String prefix) {
		return prefix + getCommand() + " (start|stop)";
	}
	
    @Override
	public int getPrivLevel(){
		return 80;
	}

	@Override
	public void handleMessage(Message m) {
		if(!logging) return;
		logger.info(m.getNick()+ ": " + m.getSmackMessage().getBody());
	}
}