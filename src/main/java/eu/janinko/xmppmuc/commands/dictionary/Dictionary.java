/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.janinko.xmppmuc.commands.dictionary;

import eu.janinko.xmppmuc.CommandWrapper;
import eu.janinko.xmppmuc.Helper;
import eu.janinko.xmppmuc.Message;
import eu.janinko.xmppmuc.commands.AbstractCommand;
import eu.janinko.xmppmuc.commands.Command;
import eu.janinko.xmppmuc.commands.PluginBuildException;
import eu.janinko.xmppmuc.data.PluginData;

/**
 *
 * @author jbrazdil
 */
public class Dictionary extends AbstractCommand {
	private CommandWrapper cw;
	private PluginData data;
	
	public Dictionary(){}
	
	public Dictionary(CommandWrapper commandWrapper){
		cw = commandWrapper;
		data = cw.getConfig();
	}

	@Override
	public Command build(CommandWrapper commandWrapper) throws PluginBuildException {
		return new Dictionary(commandWrapper);
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
			if(data.containsKey(key)){
				cw.sendMessage(key + ": " + data.getValue(key));
			}else{
				cw.sendMessage("A prd... prostě jsem " + key + " nenašel :/");
			}
		}else{
			String key = args[1];
			String value = Helper.implode(args, 2);
			data.push(key, value);
			cw.sendMessage(key + " = " + value);
		}
	}

	@Override
	public String help(String prefix) {
		return prefix + getCommand() + " SLOVO - Vypíše poznámku k zadanému slovu.\n" +
			   prefix + getCommand() + " SLOVO VYZNAM - Nastavi zadany vyznam k zadanemu slovu";
	}
	
	
}
