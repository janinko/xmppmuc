package eu.janinko.xmppmuc.commands;

import eu.janinko.xmppmuc.CommandWrapper;
import eu.janinko.xmppmuc.Message;
import eu.janinko.xmppmuc.Status;
import java.io.IOException;
import java.util.HashMap;
import org.apache.log4j.Logger;
import org.jivesoftware.smack.packet.Presence;

public class Pozdrav extends AbstractCommand implements PresenceCommand {
    private static Logger logger = Logger.getLogger(Pozdrav.class);
	HashMap<String, String> pozdravy;

    @Override
    public void setWrapper(CommandWrapper commandWrapper) {
		super.setWrapper(commandWrapper);
		pozdravy = (HashMap<String, String>) cw.loadData();
		if(pozdravy == null){
			pozdravy = new HashMap<>();
		}
    }

    @Override
    public String getCommand() {
        return "pozdrav";
    }

    @Override
    public void handle(Message m, String[] args) {

        if (args.length == 1) {
            String nick = m.getNick();
            if (pozdravy.containsKey(nick)) {
                cw.sendMessage(nick + ": " + pozdravy.get(nick));
            }
        } else if (args.length < 3) {
            if (pozdravy.containsKey(args[1])) {
                cw.sendMessage(args[1] + ": " + pozdravy.get(args[1]));
            }
        } else if (args[1].equals("set")) {
            StringBuilder sb = new StringBuilder();
            for (int i = 3; i < args.length; i++) {
                sb.append(args[i]);
                sb.append(' ');
            }
            sb.deleteCharAt(sb.length() - 1);

            pozdravy.put(args[2], sb.toString());
			try {
				cw.saveData(pozdravy);
			} catch (IOException ex) {
				logger.warn("Pozdravy couldn't be saved.", ex);
			}
        } else if (args[1].equals("reset")) {
            pozdravy.remove(args[2]);
			try {
				cw.saveData(pozdravy);
				cw.sendMessage("Pozdrav pro " + args[2] + " byl zrušen");
			} catch (IOException ex) {
				logger.warn("Pozdravy couldn't be saved.", ex);
			}
        } else {
            cw.sendMessage(this.help(cw.getCommands().getPrefix()));
        }
    }

    @Override
    public String help(String prefix) {
        return "Syntaxe pro prikaz pozdrav je:\n"
                + prefix + "pozdrav nick\n"
                + prefix + "pozdrav set nick pozdrav\n"
                + prefix + "pozdrav reset nick";
    }

    @Override
    public void handlePresence(Presence p) {}

	@Override
	public void handleStatus(Status s) {
        if(logger.isTraceEnabled()){logger.trace("Handling status " + s);}
		if(s.getType() != Status.Type.joined) return;

        String nick = s.getNick();
		if (pozdravy.containsKey(nick)) {
			cw.sendMessage(nick + ": " + pozdravy.get(nick));
        }
	}
}
