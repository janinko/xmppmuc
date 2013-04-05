package eu.janinko.xmppmuc;

import eu.janinko.xmppmuc.commands.Command;
import eu.janinko.xmppmuc.commands.MessageCommand;
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
import java.util.WeakHashMap;
import org.apache.log4j.Logger;

public class Plugins {
	private static final String PLUGIN_DIR = Bot.DATA_DIR + "plugins/";
	private static final String PLUGIN_JAR_DIR = PLUGIN_DIR + "jar/";
	private static Logger logger = Logger.getLogger(Plugins.class);
	
	private Commands commandsManager;

	private HashMap<String, CommandWrapper> commands = new HashMap<>();
	private HashSet<CommandWrapper> plugins = new HashSet<>();
	private HashSet<CommandWrapper> presencePlugins = new HashSet<>();
	private HashSet<CommandWrapper> messagePlugins = new HashSet<>();

	private XmppmucClassLoader rootClassLoader = new XmppmucClassLoader(getClass().getClassLoader());
	
	public void setCommands(Commands cm) {
		commandsManager = cm;
	}

	public final void loadPlugins(){
		StringBuilder sb = new StringBuilder("Loaded plugins: ");

		try{
			for(Command c : ServiceLoader.load(Command.class)){
				CommandWrapper cw = new CommandWrapper(c, commandsManager);
				c.setWrapper(cw);
				plugins.add(cw);
				commands.put(c.getCommand(), cw);
				if (c instanceof MessageCommand) {
					messagePlugins.add(cw);
				}
				if (c instanceof PresenceCommand) {
					presencePlugins.add(cw);
				}
				if (commandsManager.getConnection() != null && commandsManager.getConnection().isConnected()) {
					cw.command.connected();
				}
				sb.append(c.getCommand());
				sb.append(", ");
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
		File pluginDirectory = new File(PLUGIN_JAR_DIR);
		
		ArrayList<URL> urls = new ArrayList<>();
		for(File f : pluginDirectory.listFiles()){
			if(logger.isTraceEnabled()){logger.trace("Checking file: " + f.getAbsolutePath());}
			try {
				urls.add(f.toURI().toURL());
			} catch (MalformedURLException e) {
				logger.error("Failed to load plugin: " + binaryName, e);
				return false;
			}
		}
		
		URLClassLoader classLoader = new URLClassLoader(urls.toArray(new URL[urls.size()]));//, getClass().getClassLoader());

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
		} catch (InstantiationException | IllegalAccessException e) {
			logger.error("Failed to load plugin: " + binaryName, e);
			return false;
		}
		rootClassLoader.addClassLoader(classLoader);

		Command c = (Command) o;
		CommandWrapper cw = new CommandWrapper(c,commandsManager);
		c.setWrapper(cw);
		
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
	}
	
	public boolean startPlugin(String command) {
		if(commands.containsKey(command))
			return false;
		for(Command c : ServiceLoader.load(Command.class)){
			if(c.getCommand().equals(command)){
				CommandWrapper cw = new CommandWrapper(c, commandsManager);
				c.setWrapper(cw);
				plugins.add(cw);
				commands.put(c.getCommand(), cw);
				if (c instanceof MessageCommand) {
					messagePlugins.add(cw);
				}
				if (c instanceof PresenceCommand) {
					presencePlugins.add(cw);
				}
				logger.info("Plugin " + command + " started.");
				if (commandsManager.getConnection() != null && commandsManager.getConnection().isConnected()) {
					cw.command.connected();
				}
				return true;
			}
		}
		logger.error("Failed to start plugin: " + command + " (not found)");
		return false;
	}
	
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

	public boolean removeCommand(String command){
		if(logger.isTraceEnabled()){logger.trace("Removing command: '" + command + "' commands: " + commands);}
		if(!commands.containsKey(command)) return false;
		if(logger.isDebugEnabled()){logger.debug("Removing command " + command);}
		
		CommandWrapper cw = commands.get(command);
		commands.remove(command);
		plugins.remove(cw);
		presencePlugins.remove(cw);
		messagePlugins.remove(cw);
		
		cw.destroy();
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

	ClassLoader getClassLoader(){
		return rootClassLoader;
	}

	private static class XmppmucClassLoader extends ClassLoader{

		private Set<ClassLoader> weakHashSet;

		XmppmucClassLoader(ClassLoader parrent){
			super(parrent);
			weakHashSet = Collections.newSetFromMap(new WeakHashMap<ClassLoader, Boolean>());
		}

		@Override
		protected Class<?> findClass(String name) throws ClassNotFoundException{
			for(ClassLoader cl : weakHashSet){
				try {
					return cl.loadClass(name);
				} catch (ClassNotFoundException ex) {}
			}
			throw new ClassNotFoundException();
		}

		void addClassLoader(ClassLoader cl){
			weakHashSet.add(cl);
		}
	}
}
