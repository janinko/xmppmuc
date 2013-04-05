package eu.janinko.xmppmuc.commands;

import eu.janinko.xmppmuc.CommandWrapper;
import eu.janinko.xmppmuc.Message;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.apache.log4j.Logger;

public class Privset extends AbstractCommand {
    Map<String, Integer> privs;
    private static Logger logger = Logger.getLogger(Privset.class);

    @Override
    public void setWrapper(CommandWrapper commandWrapper) {
		super.setWrapper(commandWrapper);
        this.cw = commandWrapper;

		privs = (Map<String, Integer>) cw.loadData();
		if(privs == null){
			logger.warn("Privs file not found, creating empty privset.");
			privs = new HashMap<>();
		}

        for (Map.Entry<String, Integer> e : privs.entrySet()) {
            if (logger.isTraceEnabled()) {
                logger.trace("Priv for " + e.getKey() + " set to " + e.getValue());
            }
            cw.getCommands().privSet(e.getKey(), e.getValue());
        }
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

		privs.put(args[1], Integer.decode(args[2]));
        cw.getCommands().privSet(args[1], Integer.decode(args[2]));
		String message = "Práva pro " + args[1] + " byla ";
		try {
			cw.saveData(privs);
		} catch (IOException ex) {
			logger.warn("Privs couldn't be changed.",ex);
			message += "dočasně ";
		}
        message += "nastaven na: " + Integer.decode(args[2]);
        logger.info(message);
        cw.sendMessage(message);
    }

    @Override
    public String help(String prefix) {
        return prefix + getCommand() + " jid prvlevel";
    }
}
