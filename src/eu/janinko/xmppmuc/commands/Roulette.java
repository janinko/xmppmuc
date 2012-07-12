package eu.janinko.xmppmuc.commands;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import org.jivesoftware.smack.packet.Message;

import eu.janinko.xmppmuc.CommandWrapper;
import eu.janinko.xmppmuc.MucCommands;

public class Roulette extends AbstractCommand{
	private CommandWrapper cw;
	
	private Set<String> deadNicks;
	private int chamber;
	private Random random;
	private String lastNick;
	
	public Roulette() {}
	
	public Roulette(CommandWrapper commandWrapper){
		this.cw = commandWrapper;
		deadNicks = new HashSet<String>();
		chamber = 1;
		random = new Random();
		lastNick = "";
	}
	
	@Override
	public Command build(CommandWrapper commandWrapper) {
		return new Roulette(commandWrapper);
	}
	

	public String getCommand() {
		return "roulette";
	}

	public void handle(Message m, String[] args) {
		String nick = MucCommands.hGetNick(m);
		
		if(args.length == 2 && args[1].equals("restart")){
			deadNicks.clear();
			chamber = 1;
			lastNick = "";
		}

		if(deadNicks.contains(nick)){
			cw.sendMessage(nick + ": Jseš mrtvej!");
		}else if(lastNick.equals(nick)){
			cw.sendMessage(nick + ": Nemůžeš mačkat kohoutek dvakrat po sobě!");
		}else{
			if(random.nextInt(8-chamber) == 0){
				cw.sendMessage(nick + ": komora #" + chamber + " z 6 => *BANG*");
				cw.sendMessage("/me přebil");
				deadNicks.add(nick);
				chamber = 1;
			}else{
				cw.sendMessage(nick + ": komora #" + chamber + " z 6 => +klik+");
				if(chamber == 6){
					cw.sendMessage("WTF?");
					cw.sendMessage("/me přebil");
					chamber = 1;
				}else{
					chamber++;
				}
			}
			lastNick=nick;
		}

	}

	public String help(String prefix) {
		return "Příkazem '" + prefix + "roulette' si zahraješ ruskou ruletu.\n" +
			   "Příkazem '" + prefix + "roulette restart' restartuješ ruletu a vrátíš mrtvé mezi živé.";
	}

}
