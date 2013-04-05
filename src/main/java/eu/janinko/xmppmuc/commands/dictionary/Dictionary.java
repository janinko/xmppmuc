package eu.janinko.xmppmuc.commands.dictionary;

import eu.janinko.xmppmuc.CommandWrapper;
import eu.janinko.xmppmuc.Helper;
import eu.janinko.xmppmuc.Message;
import eu.janinko.xmppmuc.commands.AbstractCommand;
import java.io.IOException;
import java.util.HashMap;
import org.apache.log4j.Logger;

/**
 *
 * @author jbrazdil
 */
public class Dictionary extends AbstractCommand {
	private static Logger logger = Logger.getLogger(Dictionary.class);
	
	private HashMap<String,String> dict;

    @Override
    public void setWrapper(CommandWrapper commandWrapper) {
		super.setWrapper(commandWrapper);
		cw = commandWrapper;

		dict = (HashMap<String, String>) cw.loadData();
		if(dict == null){
			dict = new HashMap<>();
		}
	}

	@Override
	public String getCommand() {
		return "?";
	}

	@Override
	public void handle(Message m, String[] args) {
		if(args.length < 2){
			cw.sendMessage(this.help(cw.getCommands().getPrefix()));
		}else if(args.length == 2){
			String key = args[1];
			if(dict.containsKey(key)){
				cw.sendMessage(key + ": " + dict.get(key));
			}else{
				cw.sendMessage("A prd... prostě jsem " + key + " nenašel :/");
			}
		}else{
			String key = args[1];
			String value = Helper.implode(args, 2);
			dict.put(key, value);
			try {
				cw.saveData(dict);
			cw.sendMessage(key + " = " + value);
			} catch (IOException ex) {
				logger.warn("Failed to save dictionary", ex);
			}
		}
	}

	@Override
	public String help(String prefix) {
		return prefix + getCommand() + " SLOVO - Vypíše poznámku k zadanému slovu.\n" +
			   prefix + getCommand() + " SLOVO VYZNAM - Nastavi zadany vyznam k zadanemu slovu";
	}
	
	
}
