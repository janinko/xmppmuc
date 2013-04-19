package eu.janinko.xmppmuc;

import eu.janinko.xmppmuc.api.plugin.Command;
import eu.janinko.xmppmuc.api.plugin.MessageCommand;
import eu.janinko.xmppmuc.api.plugin.PresenceCommand;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Set;
import org.apache.log4j.Logger;

public class Plugins {
	private static Logger logger = Logger.getLogger(Plugins.class);
	
	private Commands commands;

	private HashMap<String, CommandWrapper> pluginClasses = new HashMap<>();
	private HashMap<String, CommandWrapper> pluginCommands = new HashMap<>();
	private HashSet<CommandWrapper> plugins = new HashSet<>();
	private HashSet<CommandWrapper> presencePlugins = new HashSet<>();
	private HashSet<CommandWrapper> messagePlugins = new HashSet<>();

	PluginsManager pm;
	
	public Plugins(PluginsManager pm) {
		this.pm = pm;
	}

	public void setCommands(Commands commands){
		this.commands = commands;
	}

	public int startPlugins(){
		StringBuilder sb = new StringBuilder("Loaded plugins: ");
		int count = 0;
		for(Class<? extends Command> clazz : pm.getCommands()){
			if(pluginClasses.containsKey(clazz.getCanonicalName())){
				continue;
			}
			if(addPlugin(clazz)){
				sb.append(clazz.getCanonicalName());
				sb.append(", ");
				count++;
			}
		}
		sb.delete(sb.length()-2,sb.length());
		logger.info(sb.toString());
		return count;
	}

	public boolean startPlugin(String binaryName){
		if(pluginClasses.containsKey(binaryName)) return false;
		Class<? extends Command> clazz = pm.getCommand(binaryName);
		if(clazz == null) return false;

		if(!addPlugin(clazz)) return false;
		logger.info("Loaded plugin: " + clazz.getCanonicalName());
		return true;
	}

	private boolean addPlugin(Class<? extends Command> clazz){
		Command c;
		try {
			c = clazz.newInstance();
		} catch (InstantiationException | IllegalAccessException ex) {
			logger.error("Failed to add plugin " + clazz, ex);
			return false;
		}

		CommandWrapper cw = new CommandWrapper(c, commands);
		c.setWrapper(cw);
		plugins.add(cw);
		
		pluginClasses.put(clazz.getCanonicalName(), cw);
		pluginCommands.put(cw.command.getCommand(), cw);

		if (c instanceof MessageCommand) {
			messagePlugins.add(cw);
		}
		if (c instanceof PresenceCommand) {
			presencePlugins.add(cw);
		}

		return true;
	}

	public void stopPlugin(String binaryName){
		CommandWrapper cw;
		synchronized(this){
			if(!pluginClasses.containsKey(binaryName)) return;
			cw = pluginClasses.get(binaryName);
			pluginClasses.remove(binaryName);
		}

		Iterator<Entry<String, CommandWrapper>> it = pluginCommands.entrySet().iterator();
		while(it.hasNext()){
			if(it.next().getValue().equals(cw)){
				it.remove();
			}
		}

		plugins.remove(cw);
		presencePlugins.remove(cw);
		messagePlugins.remove(cw);
		cw.destroy();
	}
	
	/*public final void loadPluginsFromConfigFile(){
		String path = PLUGIN_DIR + "plugins";
		BufferedReader in=null;
		try {
			in = new BufferedReader(new FileReader(path));
			String line;
			while((line = in.readLine()) != null ){
				loadPlugin(line);
			}
		} catch (FileNotFoundException e) {
			logger.error("Failed loading file", e);
		} catch (IOException e) {
			logger.error("Failed loading file", e);
		}finally{
			if(in != null){
				try {
					in.close();
				} catch (IOException e) {
					logger.error("Failed closing file", e);
				}
			}
		}
	}*/
	
	public void connected(){
		for(CommandWrapper cw : plugins){
		if(logger.isTraceEnabled()){logger.trace("Connecting: " + cw.command);}
			cw.command.connected();
		}
	}
	
	public void disconnected(){
		for(CommandWrapper cw : plugins){
		if(logger.isTraceEnabled()){logger.trace("Disconnecting: " + cw.command);}
			cw.command.disconnected();
		}
	}

	public CommandWrapper getPlugin(String command) {
		return pluginCommands.get(command);
	}

	public Set<CommandWrapper> getPlugins() {
		return Collections.unmodifiableSet(plugins);
	}

	public Set<CommandWrapper> getPresencePlugins() {
		return Collections.unmodifiableSet(presencePlugins);
	}

	public Set<CommandWrapper> getMessagePlugins() {
		return Collections.unmodifiableSet(messagePlugins);
	}

	public PluginsManager getManager() {
		return pm;
	}

	public void reload() {
		throw new UnsupportedOperationException("Not yet implemented");
	}
}
