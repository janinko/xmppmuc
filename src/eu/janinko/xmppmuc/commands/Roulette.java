package eu.janinko.xmppmuc.commands;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smackx.muc.MultiUserChat;

import eu.janinko.xmppmuc.MucCommands;

public class Roulette implements Command{
	public Roulette() {}

	private MucCommands mucc;
	
	private Set<String> deadNicks;
	private int chamber;
	private Random random;
	private String lastNick;
	
	public Roulette(MucCommands mucc){
		this.mucc = mucc;
		deadNicks = new HashSet<String>();
		chamber = 1;
		random = new Random();
		lastNick = "";
	}
	
	@Override
	public Command build(MucCommands mucCommands) {
		return new Roulette(mucCommands);
	}
	

	public String getCommand() {
		return "roulette";
	}

	public void handle(Message m) {
		String command = mucc.hGetCommand(m);
		String nick = MucCommands.hGetNick(m);
		
		if(command.equals("roulette restart")){
			deadNicks.clear();
			chamber = 1;
			lastNick = "";
		}
		MultiUserChat muc = mucc.getMuc();
		try {
			if(deadNicks.contains(nick)){
				muc.sendMessage(nick + ": Jseš mrtvej!");
			}else if(lastNick.equals(nick)){
				muc.sendMessage(nick + ": Nemůžeš mačkat kohoutek dvakrat po sobě!");
			}else{
				if(random.nextInt(7-chamber) == 0){
					muc.sendMessage(nick + ": komora #" + chamber + " z 6 => *BANG*");
					muc.sendMessage("/me přebil");
					deadNicks.add(nick);
					chamber = 1;
				}else{
					muc.sendMessage(nick + ": komora #" + chamber + " z 6 => +klik+");
					if(chamber == 6){
						muc.sendMessage("WTF?");
						muc.sendMessage("/me přebil");
						chamber = 1;
					}else{
						chamber++;
					}
				}
				lastNick=nick;
			}
		} catch (XMPPException e) {
			e.printStackTrace();
		}
		
	}

	public String help(String prefix) {
		return "Příkazem '" + prefix + "roulette' si zahraješ ruskou ruletu.\n" +
			   "Příkazem '" + prefix + "roulette restart' restartuješ ruletu a vrátíš mrtvé mezi živé.";
	}
	
	public int getPrivLevel(){
		return 0;
	}

	@Override
	public void destroy() {
		deadNicks.clear();
		chamber = 1;
		lastNick = "";		
	}

}
