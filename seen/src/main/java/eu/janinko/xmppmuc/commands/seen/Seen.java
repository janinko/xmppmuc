package eu.janinko.xmppmuc.commands.seen;

import eu.janinko.xmppmuc.CommandWrapper;
import eu.janinko.xmppmuc.Helper;
import eu.janinko.xmppmuc.Status;
import eu.janinko.xmppmuc.api.plugin.AbstractCommand;
import eu.janinko.xmppmuc.api.plugin.MessageCommand;
import eu.janinko.xmppmuc.api.plugin.PresenceCommand;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.apache.log4j.Logger;
import org.jivesoftware.smack.packet.Presence;

public class Seen extends AbstractCommand implements PresenceCommand, MessageCommand {
	private static Logger logger = Logger.getLogger(Seen.class);

	private Map<String,Pritomnost> seeny;
	private int counter = 0;

    @Override
    public void setWrapper(CommandWrapper commandWrapper) {
		super.setWrapper(commandWrapper);
		cw = commandWrapper;
		seeny = (Map<String,Pritomnost>) cw.loadData();
		if (seeny == null) {
			seeny = new HashMap<>();
		}
	}

	@Override
	public String getCommand() {
		return "seen";
	}

	@Override
	public void handle(eu.janinko.xmppmuc.Message m, String[] args) {
		if(args.length != 2){
			cw.sendMessage("Jsem slepý! Nikoho jsem neviděl!");
		}else{
			String kdo = args[1].toLowerCase();
			if(kdo.equals("botninko")){
				cw.sendMessage("To jsem přeci já!");
			}else if(seeny.containsKey(kdo)){
				cw.sendMessage(seeny.get(args[1].toLowerCase()).toString());
			}else{
				cw.sendMessage("Ten tu nebyl! Přísahám!");
			}
		}
	}

	@Override
	public String help(String prefix) {
		return prefix + getCommand() + " nick";
	}

	@Override
	public int getPrivLevel() {
		return 0;
	}

	@Override
	public void handlePresence(Presence p) {
		String kdo = Helper.getNick(p);
		seeny.put(kdo.toLowerCase(), new Pritomnost(p.getType()));
		logger.debug("Seeing " + kdo + " doing " + p.getType());
		if(counter++ % 20 == 0){
			saveConfig();
		}
	}

	@Override
	public void handleMessage(eu.janinko.xmppmuc.Message m) {
		String kdo = m.getNick();
		org.jivesoftware.smack.packet.Message msg = m.getSmackMessage();
		seeny.put(kdo.toLowerCase(), new Pritomnost(msg.getType()));
		logger.debug("Seeing " + kdo + " doing " + msg.getType());
		if(counter++ % 20 == 0){
			saveConfig();
		}
	}
	
	private void saveConfig() {
		try {
			cw.saveData(seeny);
		} catch (IOException ex) {
			logger.warn("Can't save seend data.", ex);
		}
	}

	@Override
	public void handleStatus(Status s) {}
}