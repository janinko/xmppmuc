package eu.janinko.xmppmuc.commands;

import eu.janinko.xmppmuc.CommandWrapper;
import eu.janinko.xmppmuc.Message;
import eu.janinko.xmppmuc.data.PluginData;
import java.util.Map;
import org.apache.log4j.Logger;

public class Privset extends AbstractCommand {

    private CommandWrapper cw;
    private PluginData config;
    
    private static Logger logger = Logger.getLogger(Privset.class);

    public Privset() {
    }

    public Privset(CommandWrapper commandWrapper) {
        this.cw = commandWrapper;

        config = cw.getConfig();

        Map<String, String> privs = config.getDataTree("jid").getMap();

        for (Map.Entry<String, String> e : privs.entrySet()) {
            if (logger.isTraceEnabled()) {
                logger.trace("Priv for " + e.getKey() + " set to " + Integer.decode(e.getValue()));
            }
            cw.getCommands().privSet(e.getKey(), Integer.decode(e.getValue()));
        }
    }

    @Override
    public Command build(CommandWrapper commandWrapper) {
        return new Privset(commandWrapper);
    }

    @Override
    public String getCommand() {
        return "privset";
    }

    @Override
    public int getPrivLevel() {
        return 100;
    }

    @Override
    public void handle(Message m, String[] args) {
        if (args.length != 3) {
            return;
        }

        if (!args[1].matches("[A-Za-z.-]+@[A-Za-z.-]+.[a-z]+")) {
            return;
        }

        if (!args[2].matches("-?[0-9]+")) {
            return;
        }

        config.getDataTree("jid").push(args[1], args[2]);
        cw.getCommands().privSet(args[1], Integer.decode(args[2]));
        String message = "Práva pro " + args[1] + " byla nastaven na: " + Integer.decode(args[2]);
        logger.info(message);
        cw.sendMessage(message);
    }

    @Override
    public String help(String prefix) {
        return prefix + getCommand() + " jid prvlevel";
    }
}
