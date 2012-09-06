package eu.janinko.xmppmuc.commands;

import eu.janinko.xmppmuc.CommandWrapper;
import eu.janinko.xmppmuc.Helper;
import eu.janinko.xmppmuc.Status;
import eu.janinko.xmppmuc.data.PluginData;
import eu.janinko.xmppmuc.data.PluginDataTree;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map.Entry;
import org.apache.log4j.Logger;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Presence;

public class Seen extends AbstractCommand implements PresenceCommand, MessageCommand {
	private CommandWrapper cw;
	private PluginDataTree data;
	private PluginData pd;
	HashMap<String,Pritomnost> seeny;
	private static int counter = 0;
		
	private static Logger logger = Logger.getLogger(Seen.class);
	
	public Seen(){};
	
	public Seen(CommandWrapper commandWrapper){
		cw = commandWrapper;
		pd = cw.getConfig();
        pd.setPersistent(false);
		data = pd.getDataTree("seen");
		seeny = new HashMap<String,Pritomnost>();
		loadConfig();
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
	public void handle(eu.janinko.xmppmuc.Message m, String[] args) {
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
	
	
	public void handleMessage(eu.janinko.xmppmuc.Message m) {
		String kdo = m.getNick();
		org.jivesoftware.smack.packet.Message msg = m.getSmackMessage();
		seeny.put(kdo.toLowerCase(), new Pritomnost(msg.getType()));
		logger.debug("Seeing " + kdo + " doing " + msg.getType());
		if(counter++ % 20 == 0){
			try {
				saveConfig();
			} catch (Exception e) {
				logger.warn("Couldn't save config ",e);
			}
		}
	}
	
	private void saveConfig() throws FileNotFoundException, IOException{
		for( Entry<String, Pritomnost> e : seeny.entrySet()){
			e.getValue().save(data.getDataTree(e.getKey()));
		}
		pd.save();
	}
	
	private void loadConfig() {
		for(String key : data.getSubtrees()){
			if(logger.isTraceEnabled()){logger.trace("Found key: " + key);}
			try{
				seeny.put(key, new Pritomnost(data.getDataTree(key)));
			}catch(IllegalArgumentException e){
				
				logger.error("Failed to initialize Pritomnost. Key: " + key + "; data: " + data.getDataTree(key).getMap(), e);
			}
		}
		/*
		File f = cw.getConfigFile();
		ObjectInputStream is = new ObjectInputStream(new FileInputStream(f));

		@SuppressWarnings("unchecked")
		HashMap<String, Pritomnost> seeny = (HashMap<String, Pritomnost>) is.readObject();
		this.seeny = seeny;
		is.close();*/
	}

	public void handleStatus(Status s) {}
	
	private static class Pritomnost implements Serializable{
		private static final long serialVersionUID = 1L;

		Date date;
		Presence.Type presence;
		Message.Type message;
		private static SimpleDateFormat sdf = new SimpleDateFormat("E d.M.yyyy HH:mm:ss");
		private static SimpleDateFormat pdf = new SimpleDateFormat("yyyyMMddHHmmss");
		
		private static final String DATE = "date";
		private static final String PRESENCE = "presence";
		private static final String MESSAGE = "message";

		public Pritomnost(Presence.Type type){
			date = new Date();
			presence = type;
			message = null;
		}

		public Pritomnost(Message.Type type) {
			date = new Date();
			presence = null;
			message = type;
		}
		
		public Pritomnost(PluginDataTree dt){
			if(!dt.containsKey(DATE) || !(dt.containsKey(PRESENCE) || dt.containsKey(MESSAGE)) )
				throw new IllegalArgumentException("DataTree don't have required data.");
			
			try {
				this.date = pdf.parse(dt.getValue(DATE));
			} catch (ParseException ex) {
				throw new IllegalArgumentException("DataTree don't have required data.", ex);
			}
			
			presence = null;
			message = null;
			if(dt.containsKey(PRESENCE)){
				presence = Presence.Type.valueOf(dt.getValue(PRESENCE));
				if(presence == null)
					throw new IllegalArgumentException("DataTree don't have required data.");
			}else{
				message = Message.Type.valueOf(dt.getValue(MESSAGE));
				if(message == null)
					throw new IllegalArgumentException("DataTree don't have required data.");
			}
		}
		
		public void save(PluginDataTree dt){
			dt.push(DATE, pdf.format(date));
			if(presence != null){
				dt.push(PRESENCE, presence.name());
			}else{
				dt.push(MESSAGE, message.name());
			}
		}

		@Override
		public String toString(){
			if(presence != null){
				switch(presence){
				case available: return "Teď tu byl! Vlastně by tu furt měl být, od " + sdf.format(date);
				case error: return "HUH! WTF? " + sdf.format(date);
				case subscribe: return "Teď tu byl! A cosi chtěl v " + sdf.format(date);
				case subscribed: return "Teď tu byl! A cosi bylo schváleno v " + sdf.format(date);
				case unavailable: return "Teď tu byl! Ale zdrhnul v " + sdf.format(date);
				case unsubscribe: return "Teď tu byl! A cosi chtěl zrušit v " + sdf.format(date);
				case unsubscribed: return "Teď tu byl! A cosi bylo zrušeno v " + sdf.format(date);
				}
			}
			if(message != null){
				switch(message){
				case chat: return "Teď tu byl! A cosi žvatlal kolem " + sdf.format(date);
				case error: return "WTF? HUH! " + sdf.format(date);
				case groupchat: return "Teď tu byl! A cosi vykládal kolem " + sdf.format(date);
				case headline: return "Teď tu byl! A cosi zdůrazňoval kolem " + sdf.format(date);
				case normal: return "Teď tu byl! A cosi psal kolem " + sdf.format(date);
				}
			}
			return "Bug!";
		}

	}

}


