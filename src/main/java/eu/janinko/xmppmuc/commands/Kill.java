package eu.janinko.xmppmuc.commands;

import eu.janinko.xmppmuc.Message;

public class Kill extends AbstractCommand{
    
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
			switch (args[1]) {
				case "9":
					Runtime.getRuntime().exit(0);
					break;
				case "0":
					cw.sendMessage("Vedle!");
					return;
				default:
					cw.sendMessage("uargh");
					break;
			}
		}
		Runtime.getRuntime().exit(0);
	}

	@Override
	public String help(String prefix) {
		return "Vypne bota";
	}

}
