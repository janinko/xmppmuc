package eu.janinko.xmppmuc.commands;

import eu.janinko.xmppmuc.CommandWrapper;
import eu.janinko.xmppmuc.Helper;
import eu.janinko.xmppmuc.Message;
import eu.janinko.xmppmuc.PluginManagerCommand;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import org.apache.log4j.Logger;
import org.jivesoftware.smack.packet.Presence;

public class Reminder extends AbstractCommand implements PresenceCommand {
	private CommandWrapper cw;
	
	ArrayList<Task> tasks;
	
	private static Logger logger = Logger.getLogger(PluginManagerCommand.class);

	
	public Reminder() {
	}
	
	public Reminder(CommandWrapper CommandWrapper) {
		cw = CommandWrapper;
		try {
			loadConfig();
		} catch (Exception e) {
			logger.warn("Couldn't load config",e);
			tasks = new ArrayList<Task>();
		}
	}

	@Override
	public Command build(CommandWrapper cw) throws PluginBuildException {
		return new Reminder(cw);
	}

	@Override
	public void destroy() {
	}

	@Override
	public String getCommand() {
		return "pripominky";
	}

	@Override
	public int getPrivLevel() {
		return 5;
	}

	@Override
	public String help(String prefix) {
		return "Syntaxe pro prikaz "+getCommand()+" je:\n"
			       + prefix + getCommand() + " vypis [nick]\n"
			       + prefix + getCommand() + " ok\n"
			       + prefix + getCommand() + " pridej nick zprava";
	}

	@Override
	public void handle(Message m, String[] args) {
		String nick = m.getNick();
		if(args.length < 2){
			print(nick);
			return;
		}
		
		if("ok".equals(args[1])){
			deactivate(nick);
			try {
				saveConfig();
			} catch (Exception e) {
				logger.warn("Couldn't save config",e);
			}
		}else if("vypis".equals(args[1])){
			if(args.length < 3){
				print(nick);
			}else{
				print(args[2]);
			}
		}else if("pridej".equals(args[1])){
			if (args.length < 4) return;
			StringBuilder sb = new StringBuilder(Helper.implode(args,3));
			sb.append(" (by ");
			sb.append(m.getNick());
			sb.append(')');
			
			tasks.add(new Task(args[2], sb.toString()));
			logger.info("Pridana pripominka pro " + args[2] + ": " + sb);
			cw.sendMessage("Jasně! Budu to " + args[2] + " omlacovat o hlavu!");
			try {
				saveConfig();
			} catch (Exception e) {
				logger.warn("Couldn't save config",e);
			}
		}
	}
	
	private void print(String nick){
		int count=0;
		StringBuilder sb = new StringBuilder(nick);
		sb.append(": ");
		for(Task t : tasks){
			logger.trace("Pripominka pro " + t.getWho() + " zneni: '" + t.getSubject() + "' ma stav: " + t.isActive());

			if(t.isActive() && t.getWho().equals(nick)){
				sb.append(t.getSubject());
				sb.append('\n');
				count++;
			}
		}
		sb.deleteCharAt(sb.length()-1);

		if(count > 0){
			cw.sendMessage(sb.toString());
		}
		logger.debug("Vytisteno " + count + " pripominek pro " + nick + " (celkem z " + tasks.size() + ")");
	}
	
	private void deactivate(String nick){
		for(Task t : tasks){
			if(t.isActive() && t.getWho().equals(nick)){
				t.deactivate();
			}
		}
	}

	@Override
	public void handlePresence(Presence p) {
		if(p.getType() == Presence.Type.available){
			String nick = Helper.getNick(p);
			
			int count = 0;
			for(Task t : tasks){
				if(t.isActive() && t.getWho().equals(nick)){
					count++;
				}
			}
			
			if(count > 0){
				cw.sendMessage(nick + ": Máš u mě nevřízené zprávy, celkem "
						+ count +". Pro precteni dej "
						+ cw.getCommands().getPrefix() + getCommand() + " vypis");
			}
		}
	}
	
	private void saveConfig() throws FileNotFoundException, IOException{
		File f = cw.getConfigFile();
		ObjectOutputStream os = new ObjectOutputStream(new FileOutputStream(f));
		os.writeObject(tasks);
		os.close();
	}
	
	private void loadConfig() throws IOException, ClassNotFoundException{
		File f = cw.getConfigFile();
		ObjectInputStream is = new ObjectInputStream(new FileInputStream(f));
		@SuppressWarnings("unchecked")
		ArrayList<Task> t = (ArrayList<Task>) is.readObject();
		is.close();
		tasks = t;
	}

}
