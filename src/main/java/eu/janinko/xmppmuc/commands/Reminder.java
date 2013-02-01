package eu.janinko.xmppmuc.commands;

import eu.janinko.xmppmuc.CommandWrapper;
import eu.janinko.xmppmuc.Helper;
import eu.janinko.xmppmuc.Message;
import eu.janinko.xmppmuc.PluginManagerCommand;
import eu.janinko.xmppmuc.Status;
import eu.janinko.xmppmuc.data.PluginData;
import java.util.Map;
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
		               + prefix + getCommand() + " [vypis [nick]]\n"
		               + prefix + getCommand() + " ok [cislo ...|vse]\n"
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
			if(args.length == 2){
				deleteOne(nick);
			}else{
				if("vse".equals(args[2])){
					deactivate(nick);
				}else{
					delete(nick, args);
				}
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

			String receiver = args[2].toLowerCase();

			data.getDataTree(receiver).push(String.valueOf(sb.toString().hashCode()), sb.toString());
			logger.info("Pridana pripominka pro " + receiver + ": " + sb);
			cw.sendMessage("Jasně! Budu to " + args[2] + " omlacovat o hlavu!");
		}
	}

	private String getMessage(String nick){
		int count=0;
		StringBuilder sb = new StringBuilder(nick);
		sb.append(": ");

		for(Entry<String, String> e : data.getDataTree(nick.toLowerCase()).getMap().entrySet()){
			sb.append(Integer.toHexString(Integer.valueOf(e.getKey())));
			sb.append(" : ");
			sb.append(e.getValue());
			sb.append('\n');
			count++;
		}
		if(count == 0) return null;
		sb.deleteCharAt(sb.length()-1);
		logger.debug("Nalezeno " + count + " pripominek pro " + nick);
		return sb.toString();
	}

	private void print(String nick){
		String msg = getMessage(nick);

		if(msg != null){
			cw.sendMessage(msg);
		}
	}

	private void deactivate(String nick){
		for(Entry<String, String> e : data.getDataTree(nick.toLowerCase()).getMap().entrySet()){
			data.getDataTree(nick.toLowerCase()).removeKey(e.getKey());
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

		String msg = getMessage(nick);
		int count = 0;
		for(Entry<String, String> e : data.getDataTree(nick.toLowerCase()).getMap().entrySet()){
			count++;
		}

		if (msg != null) {
			cw.sendPrivateMessage(nick, msg);
		}
	}

	private void deleteOne(String nick) {
		Map<String, String> messages = data.getDataTree(nick.toLowerCase()).getMap();
		if(messages.size() == 1){
			String key = messages.keySet().iterator().next();
			data.getDataTree(nick.toLowerCase()).removeKey(key);
		}
	}

	private void delete(String nick, String[] args) {
		for(int i=2; i<args.length; i++){
			try{
				String key = Integer.valueOf(args[i], 16).toString();
				data.getDataTree(nick.toLowerCase()).removeKey(key);
			}catch(NumberFormatException ex){
				//
			}
		}
		throw new UnsupportedOperationException("Not yet implemented");
	}
}
