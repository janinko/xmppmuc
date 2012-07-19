package eu.janinko.xmppmuc.commands;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import org.apache.log4j.Logger;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Presence;

import eu.janinko.xmppmuc.CommandWrapper;
import eu.janinko.xmppmuc.Helper;

public class Seen extends AbstractCommand implements PresenceCommand, MessageCommand {
	CommandWrapper cw;
	HashMap<String,Pritomnost> seeny;
	private static int counter = 0;
		
	private static Logger logger = Logger.getLogger(Seen.class);
	
	public Seen(){};
	
	public Seen(CommandWrapper commandWrapper){
		cw = commandWrapper;
		try {
			loadConfig();
		} catch (Exception e) {
			logger.warn("Couldn't load config",e);
			seeny = new HashMap<String,Pritomnost>();
		}
	}

	@Override
	public Command build(CommandWrapper cw) throws PluginBuildException {
		return new Seen(cw);
	}

	@Override
	public String getCommand() {
		return "seen";
	}

	@Override
	public void handle(Message m, String[] args) {
		if(args.length != 2){
			cw.sendMessage("Jsem slepý! Nikoho jsem neviděl!");
		}else if(args[1].toLowerCase().equals("botninko")){
			cw.sendMessage("To jsem přeci já!");
		}else if(seeny.containsKey(args[1].toLowerCase())){
			cw.sendMessage(seeny.get(args[1].toLowerCase()).toString());
		}else{
			cw.sendMessage("Ten tu nebyl! Přísahám!");
		}
	}

	@Override
	public String help(String prefix) {
		return "Tralala";
	}

	@Override
	public int getPrivLevel() {
		return 0;
	}

	@Override
	public void destroy() {
		
	}

	@Override
	public void handlePresence(Presence p) {
		String kdo = Helper.getNick(p);
		seeny.put(kdo.toLowerCase(), new Pritomnost(p.getType()));
		logger.debug("Seeing " + kdo + " doing " + p.getType());
		if(counter++ % 20 == 0){
			try {
				saveConfig();
			} catch (Exception e) {
				logger.warn("Couldn't save config",e);
			}
		}
	}
	
	
	public void handleMessage(Message m) {
		String kdo = Helper.getNick(m);
		seeny.put(kdo.toLowerCase(), new Pritomnost(m.getType()));
		logger.debug("Seeing " + kdo + " doing " + m.getType());
		if(counter++ % 20 == 0){
			try {
				saveConfig();
			} catch (Exception e) {
				logger.warn("Couldn't save config ",e);
			}
		}
	}
	
	private void saveConfig() throws FileNotFoundException, IOException{
		File f = cw.getConfigFile();
		ObjectOutputStream os = new ObjectOutputStream(new FileOutputStream(f));
		os.writeObject(seeny);
		os.close();
	}
	
	private void loadConfig() throws IOException, ClassNotFoundException{
		File f = cw.getConfigFile();
		ObjectInputStream is = new ObjectInputStream(new FileInputStream(f));

		@SuppressWarnings("unchecked")
		HashMap<String, Pritomnost> seeny = (HashMap<String, Pritomnost>) is.readObject();
		this.seeny = seeny;
		is.close();
	}

}

class Pritomnost implements Serializable{
	private static final long serialVersionUID = 1L;
	
	Date d;
	Presence.Type presence;
	Message.Type message;
	private static SimpleDateFormat sdf = new SimpleDateFormat("E d.M.yyyy HH:mm:ss");
	
	Pritomnost(Presence.Type type){
		d = new Date();
		presence = type;
		message = null;
	}
	
	public Pritomnost(Message.Type type) {
		d = new Date();
		presence = null;
		message = type;
	}

	public String toString(){
		if(presence != null){
			switch(presence){
			case available: return "Teď tu byl! Vlastně by tu furt měl být, od " + sdf.format(d);
			case error: return "HUH! WTF? " + sdf.format(d);
			case subscribe: return "Teď tu byl! A cosi chtěl v " + sdf.format(d);
			case subscribed: return "Teď tu byl! A cosi bylo schváleno v " + sdf.format(d);
			case unavailable: return "Teď tu byl! Ale zdrhnul v " + sdf.format(d);
			case unsubscribe: return "Teď tu byl! A cosi chtěl zrušit v " + sdf.format(d);
			case unsubscribed: return "Teď tu byl! A cosi bylo zrušeno v " + sdf.format(d);
			}
		}
		if(message != null){
			switch(message){
			case chat: return "Teď tu byl! A cosi žvatlal kolem " + sdf.format(d);
			case error: return "WTF? HUH! " + sdf.format(d);
			case groupchat: return "Teď tu byl! A cosi vykládal kolem " + sdf.format(d);
			case headline: return "Teď tu byl! A cosi zdůrazňoval kolem " + sdf.format(d);
			case normal: return "Teď tu byl! A cosi psal kolem " + sdf.format(d);
			}
		}
		return "Bug!";
	}
	
}
