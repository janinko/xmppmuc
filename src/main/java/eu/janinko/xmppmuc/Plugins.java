package eu.janinko.xmppmuc;

import eu.janinko.xmppmuc.commands.Command;
import eu.janinko.xmppmuc.commands.MessageCommand;
import eu.janinko.xmppmuc.commands.PluginBuildException;
import eu.janinko.xmppmuc.commands.PresenceCommand;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.ServiceConfigurationError;
import java.util.ServiceLoader;
import java.util.Set;
import org.apache.log4j.Logger;

public class Plugins {
	String pluginDir = System.getProperty("user.home") + "/.xmppmuc/plugins/";
	private String pluginDirectoryPath = pluginDir + "jar/";
	
	private Commands commandsManager;

	private HashMap<String, CommandWrapper> commands = new HashMap<String, CommandWrapper>();
	private HashSet<CommandWrapper> plugins = new HashSet<CommandWrapper>();
	private HashSet<CommandWrapper> presencePlugins = new HashSet<CommandWrapper>();
	private HashSet<CommandWrapper> messagePlugins = new HashSet<CommandWrapper>();
	
	private static Logger logger = Logger.getLogger(Plugins.class);
	
	public Plugins(Commands cm) {
		commandsManager = cm;
		
		loadPlugins();
		loadPluginsFromConfigFile();
	}
	
	
	public final void loadPlugins(){
		StringBuilder sb = new StringBuilder("Loaded plugins: ");

		try{
			for(Command c : ServiceLoader.load(Command.class)){
				try{
					CommandWrapper cw = new CommandWrapper(c,commandsManager);
					plugins.add(cw);
					commands.put(c.getCommand(), cw);
					if(c instanceof MessageCommand){
						messagePlugins.add(cw);
					}
					if(c instanceof PresenceCommand){
						presencePlugins.add(cw);
					}
					if(commandsManager.getConnection() != null && commandsManager.getConnection().isConnected()){
						cw.command.connected();
					}
					sb.append(c.getCommand());
					sb.append(", ");
				}catch (PluginBuildException e) {
					logger.error("Building of plugin failed", e);
				}
			}
		}catch(ServiceConfigurationError e){
			if(e.getCause() instanceof InstantiationException){
				logger.error("Can't instantiate class. Does it have simple constructor?", e);
			}
			logger.error("Failed to load plugins", e);
		}
			
		sb.delete(sb.length()-2,sb.length());
		logger.info(sb.toString());
	}
	

	public boolean loadPlugin(String binaryName){
		logger.info("Loading plugin: " + binaryName);
		File pluginDirectory = new File(pluginDirectoryPath);
		
		ArrayList<URL> urls = new ArrayList<URL>();
		for(File f : pluginDirectory.listFiles()){
			if(logger.isTraceEnabled()){logger.trace("Checking file: " + f.getAbsolutePath());}
			try {
				urls.add(f.toURI().toURL());
			} catch (MalformedURLException e) {
				logger.error("Failed to load plugin: " + binaryName, e);
				return false;
			}
		}
		
		URLClassLoader classLoader = new URLClassLoader(urls.toArray(new URL[0]), getClass().getClassLoader());

		Class<?> clazz;
		try {
			clazz = classLoader.loadClass(binaryName);
		} catch (ClassNotFoundException e) {
			logger.error("Failed to load plugin: " + binaryName, e);
			return false;
		}
		
		Object o;
		try {
			o = clazz.newInstance();
		} catch (InstantiationException e) {
			logger.error("Failed to load plugin: " + binaryName, e);
			return false;
		} catch (IllegalAccessException e) {
			logger.error("Failed to load plugin: " + binaryName, e);
			return false;
		}
		
		CommandWrapper cw;
		try {
			cw = new CommandWrapper((Command) o,commandsManager);
		} catch (PluginBuildException e) {
			logger.error("Failed to load plugin: " + binaryName, e);
			return false;
		}
		
		plugins.add(cw);
		commands.put(cw.command.getCommand(), cw);
		if(cw.command instanceof MessageCommand){
			messagePlugins.add(cw);
		}
		if(cw.command instanceof PresenceCommand){
			presencePlugins.add(cw);
		}

		if(commandsManager.getConnection() != null && commandsManager.getConnection().isConnected()){
			cw.command.connected();
		}
		logger.info("Loaded plugin: " + cw.command.getCommand());
		return true;
	}
	
	public final void loadPluginsFromConfigFile(){
		String path = pluginDir + "plugins";
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
	}
	
	public boolean startPlugin(String command) {
		if(commands.containsKey(command))
			return false;
		for(Command c : ServiceLoader.load(Command.class)){
			if(c.getCommand().equals(command)){
				try {
					CommandWrapper cw = new CommandWrapper(c,commandsManager);
					plugins.add(cw);
					commands.put(c.getCommand(), cw);
					if(c instanceof MessageCommand){
						messagePlugins.add(cw);
					}
					if(c instanceof PresenceCommand){
						presencePlugins.add(cw);
					}
					logger.info("Plugin " + command + " started.");
					if(commandsManager.getConnection() != null && commandsManager.getConnection().isConnected()){
						cw.command.connected();
					}
					return true;
				} catch (PluginBuildException e) {
					logger.error("Failed to start plugin: " + command, e);
					return false;
				}
			}
		}
		logger.error("Failed to start plugin: " + command + " (not found)");
		return false;
	}
	
	public void connected(){
		for(CommandWrapper cw : plugins){
			cw.command.connected();
		}
	}
	
	public void disconnected(){
		for(CommandWrapper cw : plugins){
			cw.command.disconnected();
		}
	}

	public boolean removeCommand(String command){
		if(!commands.containsKey(command)) return false;
		if(logger.isDebugEnabled()){logger.debug("Removing command " + command);}
		
		CommandWrapper cw = commands.get(command);
		commands.remove(command);
		plugins.remove(cw);
		presencePlugins.remove(cw);
		messagePlugins.remove(cw);
		
		cw.command.destroy();
		return true;
	}

	public CommandWrapper getPlugin(String command) {
		return commands.get(command);
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

}
