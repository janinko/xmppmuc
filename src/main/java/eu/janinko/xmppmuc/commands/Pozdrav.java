package eu.janinko.xmppmuc.commands;

import eu.janinko.xmppmuc.CommandWrapper;
import eu.janinko.xmppmuc.Message;
import eu.janinko.xmppmuc.Status;
import eu.janinko.xmppmuc.data.PluginDataTree;
import org.apache.log4j.Logger;
import org.jivesoftware.smack.packet.Presence;

public class Pozdrav extends AbstractCommand implements PresenceCommand {

    private CommandWrapper cw;
    PluginDataTree pozdravy;
    private static Logger logger = Logger.getLogger(Pozdrav.class);

    public Pozdrav() {
    }

    public Pozdrav(CommandWrapper commandWrapper) {
        this.cw = commandWrapper;
        pozdravy = cw.getConfig().getDataTree("nick");
    }

    @Override
    public Command build(CommandWrapper commandWrapper) {
        return new Pozdrav(commandWrapper);
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
                cw.sendMessage(nick + ": " + pozdravy.getValue(nick));
            }
        } else if (args.length < 3) {
            if (pozdravy.containsKey(args[1])) {
                cw.sendMessage(args[1] + ": " + pozdravy.getValue(args[1]));
            }
        } else if (args[1].equals("set")) {
            StringBuilder sb = new StringBuilder();
            for (int i = 3; i < args.length; i++) {
                sb.append(args[i]);
                sb.append(' ');
            }
            sb.deleteCharAt(sb.length() - 1);

            pozdravy.push(args[2], sb.toString());
        } else if (args[1].equals("reset")) {
            pozdravy.removeKey(args[2]);
            cw.sendMessage("Pozdrav pro " + args[2] + " byl zrušen");
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
			cw.sendMessage(nick + ": " + pozdravy.getValue(nick));
        }
	}
}
