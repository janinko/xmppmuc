package eu.janinko.xmppmuc;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.ServiceConfigurationError;
import java.util.ServiceLoader;

import org.apache.log4j.Logger;

import eu.janinko.xmppmuc.commands.Command;
import eu.janinko.xmppmuc.commands.PluginBuildException;

public class PluginManager{
	private MucCommands mucc;
	private Map<String, CommandWrapper> commands;
	private String pluginDirectoryPath = System.getProperty("user.home") + "/.xmppmuc/plugins/jar/";

	private static Logger logger = Logger.getLogger(PluginManager.class);
	
	public PluginManager(MucCommands mucCommands) {
		this.mucc = mucCommands;
		commands = new HashMap<String, CommandWrapper>();
	}

	void loadPlugins(){
		StringBuilder sb = new StringBuilder("Loaded plugins: ");
		try{
			for(Command c : ServiceLoader.load(Command.class)){
				try {
					commands.put(c.getCommand(),new CommandWrapper(c, mucc));
					sb.append(c.getCommand());
					sb.append(", ");
				} catch (PluginBuildException e) {
					logger.error("loadPlugins", e);
				}
			}
		}catch(ServiceConfigurationError e){
			if(e.getCause() instanceof InstantiationException){
				logger.error("Can't instantiate class. Does it have simple constructor?", e);
			}
			logger.error("loadPlugins", e);
		}
		sb.delete(sb.length()-2,sb.length());
		logger.info(sb.toString());
	}
	
	boolean loadPlugin(String binaryName){
		logger.info("Loading plugin: " + binaryName);
		File pluginDirectory = new File(pluginDirectoryPath);
		
		ArrayList<URL> urls = new ArrayList<URL>();
		for(File f : pluginDirectory.listFiles()){
			logger.trace("Checking file: " + f.getAbsolutePath());
			try {
				urls.add(f.toURI().toURL());
			} catch (MalformedURLException e) {
				logger.error("loadPlugin", e);
				return false;
			}
		}
		
		URLClassLoader classLoader = new URLClassLoader(urls.toArray(new URL[0]), getClass().getClassLoader());

		Class<?> clazz;
		try {
			clazz = classLoader.loadClass(binaryName);
		} catch (ClassNotFoundException e) {
			logger.error("loadPlugin", e);
			return false;
		}

		Object o;
		try {
			o = clazz.newInstance();
			logger.debug("Instance " + o + " created");
		} catch (InstantiationException e) {
			logger.error("loadPlugin", e);
			return false;
		} catch (IllegalAccessException e) {
			logger.error("loadPlugin", e);
			return false;
		}
		
		CommandWrapper in = null;
		try {
			in = new CommandWrapper((Command) o,mucc);
			logger.debug("CommandWrapper " + in + " created");
		} catch (PluginBuildException e) {
			logger.error("loadPlugin", e);
			return false;
		}

		commands.put(in.command.getCommand(),in);
		logger.info("NACTENO: " + in.command.getCommand());
		return true;
	}
	
	boolean removeCommand(String command){
		if(!commands.containsKey(command))
			return false;
		commands.get(command).command.destroy();
		commands.remove(command);
		return true;
	}
	
	public void loadPluginsFromConfigFile(String path){
		try {
			BufferedReader in = new BufferedReader(new FileReader(path));
			String line;
			while((line = in.readLine()) != null ){
				loadPlugin(line);
			}
		} catch (FileNotFoundException e) {
			System.err.println("PluginManager.loadPluginsFromConfigFile() A");
			e.printStackTrace();
		} catch (IOException e) {
			System.err.println("PluginManager.loadPluginsFromConfigFile() B");
			e.printStackTrace();
		}
	}

	public boolean startPlugin(String command) {
		if(commands.containsKey(command))
			return false;
		for(Command c : ServiceLoader.load(Command.class)){
			if(c.getCommand().equals(command)){
				try {
					commands.put(c.getCommand(),new CommandWrapper(c,mucc));
					return true;
				} catch (PluginBuildException e) {
					System.err.println("PluginManager.startPlugin() A");
					e.printStackTrace();
					return false;
				}
			}
		}
		return false;
	}

	public Collection<CommandWrapper> getCommands() {
		return commands.values();
	}
}
