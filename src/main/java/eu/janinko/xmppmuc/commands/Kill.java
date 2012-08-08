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
		if(args.length > 1){
			if("9".equals(args[1])){
				Runtime.getRuntime().exit(0);
			}else if("0".equals(args[1])){
				cw.sendMessage("Vedle!");
				return;
			}else{
				cw.sendMessage("uargh");
			}
		}
        cw.getCommands().getBot().stop();
	}

	@Override
	public String help(String prefix) {
		return "Vypne bota";
	}

}
