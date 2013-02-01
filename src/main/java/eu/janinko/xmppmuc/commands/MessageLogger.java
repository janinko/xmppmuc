package eu.janinko.xmppmuc.commands;

import eu.janinko.xmppmuc.CommandWrapper;
import eu.janinko.xmppmuc.Message;
import org.apache.log4j.Logger;

public class MessageLogger extends AbstractCommand implements MessageCommand{
	private CommandWrapper cw;
	private boolean logging = true;
    private static Logger logger = Logger.getLogger(MessageLogger.class);
	
	public MessageLogger() {}
	
	public MessageLogger(CommandWrapper commandWrapper){
		this.cw = commandWrapper;
	}
	
	@Override
	public Command build(CommandWrapper commandWrapper) {
		return new MessageLogger(commandWrapper);
	}

    @Override
	public String getCommand() {
		return "lg";
	}

    @Override
	public void handle(Message m, String[] args) {
		if(args.length == 2){
			if("start".equals(args[1])){
				logging = true;
			}else if("stop".equals(args[1])){
				logging = false;
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