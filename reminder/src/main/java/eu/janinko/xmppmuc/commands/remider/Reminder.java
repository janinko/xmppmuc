package eu.janinko.xmppmuc.commands.remider;

import eu.janinko.xmppmuc.CommandWrapper;
import eu.janinko.xmppmuc.Helper;
import eu.janinko.xmppmuc.Message;
import eu.janinko.xmppmuc.Status;
import eu.janinko.xmppmuc.api.plugin.AbstractCommand;
import eu.janinko.xmppmuc.api.plugin.PresenceCommand;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import org.apache.log4j.Logger;
import org.jivesoftware.smack.packet.Presence;

public class Reminder extends AbstractCommand implements PresenceCommand {
	private static Logger logger = Logger.getLogger(Reminder.class);

	private HashMap<String,Notes> notes;

    @Override
    public void setWrapper(CommandWrapper commandWrapper) {
		super.setWrapper(commandWrapper);

		notes = (HashMap<String, Notes>) cw.loadData();
		if(notes == null){
			notes = new HashMap<>();
		}
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
		String nick = m.getNick().toLowerCase();
		if(args.length < 2){
			print(nick);
			return;
		}
		switch (args[1]) {
			case "ok":
				if(args.length == 2){
					deleteOne(nick);
				}else{
					if("vse".equals(args[2])){
						deactivate(nick);
					}else{
						delete(nick, args);
					}
				}
				break;
			case "vypis":
				if(args.length < 3){
					print(nick);
				}else{
					print(args[2].toLowerCase());
				}
				break;
			case "pridej":
				if (args.length < 4) return;
				StringBuilder sb = new StringBuilder(Helper.implode(args,3));
				sb.append(" (by ");
				sb.append(m.getNick());
				sb.append(')');
				String receiver = args[2].toLowerCase();
				Notes n = notes.get(receiver);
				if(n == null){
					n = new Notes();
					notes.put(receiver, n);
				}
				n.add(sb.toString());
				try {
					cw.saveData(notes);
					logger.info("Pridana pripominka pro " + receiver + ": " + sb);
					cw.sendMessage("Jasně! Budu to " + args[2] + " omlacovat o hlavu!");
				} catch (IOException ex) {
					logger.warn("Note couldn't be added", ex);
				}
				break;
		}
	}

	private String getMessage(String nick){
		Notes n = notes.get(nick);
		if(n == null) return null;
		return n.toString();
	}

	private void print(String nick){
		String msg = getMessage(nick);

		if(msg != null){
			cw.sendMessage(msg);
		}
	}

	private void deactivate(String nick){
		if(notes.remove(nick) != null)
			save();
	}

	@Override
	public void handlePresence(Presence p) {}

	@Override
	public void handleStatus(Status s) {
		if (s.getType() != Status.Type.joined) {
			return;
		}
		String nick = s.getNick().toLowerCase();

		String msg = getMessage(nick);

		if (msg != null) {
			cw.sendPrivateMessage(nick, msg);
		}
	}

	private void save(){
		try {
			cw.saveData(notes);
		} catch (IOException ex) {
			logger.warn("Notes couldn't be deleted", ex);
		}
	}

	private void deleteOne(String nick) {
		Notes n = notes.get(nick);
		if(n == null) return;
		if(n.size() == 1){
			deactivate(nick);
		}
	}

	private void delete(String nick, String[] args) {
		Notes n = notes.get(nick);
		if(n == null) return;
		ArrayList<Integer> ids = new ArrayList<>();
		for(int i=2; i<args.length; i++){
			try{
				Integer id = Integer.valueOf(args[i]);
				if(!ids.contains(id)){
					ids.add(id);
				}
			}catch(NumberFormatException ex){
				//
			}
		}
		Collections.sort(ids, Collections.reverseOrder());
		for(int id : ids){
			n.remove(id);
		}
		save();
	}

	private static class Notes extends ArrayList<String> implements Serializable{
		@Override
		public String toString() {
			if(size()==0) return null;
			StringBuilder sb = new StringBuilder();

			for(int i=0; i<size(); i++){
				sb.append(i);
				sb.append(" : ");
				sb.append(get(i));
				sb.append('\n');
			}
			sb.deleteCharAt(sb.length()-1);
			return sb.toString();
		}
	}
}
