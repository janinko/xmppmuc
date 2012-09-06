package eu.janinko.xmppmuc.commands;

import eu.janinko.xmppmuc.CommandWrapper;
import eu.janinko.xmppmuc.Helper;
import eu.janinko.xmppmuc.Message;
import eu.janinko.xmppmuc.PluginManagerCommand;
import eu.janinko.xmppmuc.Status;
import eu.janinko.xmppmuc.data.PluginData;
import java.util.Map.Entry;
import org.apache.log4j.Logger;
import org.jivesoftware.smack.packet.Presence;

public class Reminder extends AbstractCommand implements PresenceCommand {
	private CommandWrapper cw;
	
	private static Logger logger = Logger.getLogger(PluginManagerCommand.class);
	private PluginData data;
	
	public Reminder() {
	}
	
	public Reminder(CommandWrapper CommandWrapper) {
		cw = CommandWrapper;
		
		data = cw.getConfig();
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
			
			data.getDataTree(args[2]).push(String.valueOf(sb.toString().hashCode()), sb.toString());
			logger.info("Pridana pripominka pro " + args[2] + ": " + sb);
			cw.sendMessage("Jasně! Budu to " + args[2] + " omlacovat o hlavu!");
		}
	}
	
	private void print(String nick){
		int count=0;
		StringBuilder sb = new StringBuilder(nick);
		sb.append(": ");
		
		for(Entry<String, String> e : data.getDataTree(nick).getMap().entrySet()){
			sb.append(e.getValue());
			sb.append('\n');
			count++;
		}
		sb.deleteCharAt(sb.length()-1);

		if(count > 0){
			cw.sendMessage(sb.toString());
		}
		logger.debug("Vytisteno " + count + " pripominek pro " + nick);
	}
	
	private void deactivate(String nick){
		for(Entry<String, String> e : data.getDataTree(nick).getMap().entrySet()){
			data.getDataTree(nick).removeKey(e.getKey());
		}
	}

	@Override
	public void handlePresence(Presence p) {}
	
	@Override
	public void handleStatus(Status s) {
		if (s.getType() != Status.Type.joined) {
			return;
		}
		String nick = s.getNick();
		int count = 0;
		for(Entry<String, String> e : data.getDataTree(nick).getMap().entrySet()){
			count++;
		}

		if (count > 0) {
			cw.sendMessage(nick + ": Máš u mě nevřízené zprávy, celkem "
					+ count + ". Pro precteni dej "
					+ cw.getCommands().getPrefix() + getCommand() + " vypis");
		}
	}
}
