package eu.janinko.xmppmuc.commands;

import eu.janinko.xmppmuc.CommandWrapper;
import eu.janinko.xmppmuc.Message;

public class Kill extends AbstractCommand{
        CommandWrapper cw;
    
	public Kill() {}

        private Kill(CommandWrapper commandWrapper) {
            cw = commandWrapper;
        }
	
	@Override
	public Command build(CommandWrapper commandWrapper) {
		return new Kill(commandWrapper);
	}

	@Override
	public String getCommand() {
		return "kill";
	}

	@Override
	public int getPrivLevel() {
		return 20;
	}

	@Override
	public void handle(Message m, String[] args) {
            cw.getCommands().getBot().stop();
                    
            if("9".equals(args[1])){
		Runtime.getRuntime().exit(0);
            }
	}

	@Override
	public String help(String prefix) {
		return "Vypne bota";
	}

}
