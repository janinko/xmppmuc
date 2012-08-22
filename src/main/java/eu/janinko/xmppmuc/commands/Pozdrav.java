package eu.janinko.xmppmuc.commands;

import eu.janinko.xmppmuc.CommandWrapper;
import eu.janinko.xmppmuc.Helper;
import eu.janinko.xmppmuc.Message;
import eu.janinko.xmppmuc.data.PluginDataTree;
import java.util.HashSet;
import org.apache.log4j.Logger;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smackx.muc.Affiliate;
import org.jivesoftware.smackx.muc.Occupant;

public class Pozdrav extends AbstractCommand implements PresenceCommand {

    private CommandWrapper cw;
    PluginDataTree pozdravy;
    private HashSet<String> online = new HashSet<String>();
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
    public void handlePresence(Presence p) {
        if(logger.isTraceEnabled()){logger.trace("Handling presence " + p);}
        String nick = Helper.getNick(p);
        if (p.getType() == Presence.Type.available && !online.contains(nick)) {
            online.add(nick);
            if (pozdravy.containsKey(nick)) {
                cw.sendMessage(nick + ": " + pozdravy.getValue(nick));
            }
        } else if (p.getType() == Presence.Type.unavailable) {
            online.remove(nick);
        }
    }

    @Override
    public void connected() {
        try {
			StringBuilder sb = null;
			if(logger.isTraceEnabled()){ sb = new StringBuilder("Participants: "); }
            for ( Occupant member : cw.getCommands().getConnection().getMuc().getParticipants()) {
				if(logger.isTraceEnabled()){ sb.append(member.getNick()).append(", "); }
                online.add(member.getNick());
            }
			if(logger.isTraceEnabled()){ logger.trace(sb.delete(sb.length()-2, sb.length())); }
			if(logger.isTraceEnabled()){ sb = new StringBuilder("Moderators: "); }
            for ( Occupant member : cw.getCommands().getConnection().getMuc().getModerators()) {
				if(logger.isTraceEnabled()){ sb.append(member.getNick()).append(", "); }
                online.add(member.getNick());
            }
			if(logger.isTraceEnabled()){ logger.trace(sb.delete(sb.length()-2, sb.length())); }
        } catch (XMPPException e) {
            logger.error("Failed to obrain members", e);
        }
    }

    @Override
    public void disconnected() {
        online.clear();
    }
}
