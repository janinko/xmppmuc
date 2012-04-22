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
import java.util.ServiceLoader;

import org.apache.log4j.Logger;
import org.jivesoftware.smack.packet.Message;

import eu.janinko.xmppmuc.commands.Command;
import eu.janinko.xmppmuc.commands.PluginBuildException;

public class PluginManager{
	private MucCommands mucc;
	private Map<String, Command> commands;
	private String pluginDirectoryPath = System.getProperty("user.home") + "/.xmppmuc/plugins/jar/";

	private static Logger logger = Logger.getLogger(PluginManager.class);
	
	public PluginManager(MucCommands mucCommands) {
		this.mucc = mucCommands;
		commands = new HashMap<String, Command>();
	}

	void loadPlugins(){
		StringBuilder sb = new StringBuilder("Loaded plugins: ");
		for(Command c : ServiceLoader.load(Command.class)){
			try {
				commands.put(c.getCommand(),c.build(mucc));
				sb.append(c.getCommand());
				sb.append(", ");
			} catch (PluginBuildException e) {
				logger.error("loadPlugins", e);
			}
		}
		sb.delete(sb.length()-2,sb.length());
		logger.info(sb.toString());
	}
	
	boolean loadPlugin(String binaryName){
		File pluginDirectory = new File(pluginDirectoryPath);
		
		ArrayList<URL> urls = new ArrayList<URL>();
		for(File f : pluginDirectory.listFiles()){
			try {
				urls.add(f.toURI().toURL());
			} catch (MalformedURLException e) {
				logger.error("loadPlugin", e);
				return false;
			}
		}
		
		URLClassLoader classLoader = new URLClassLoader(urls.toArray(new URL[0]), getClass().getClassLoader());

		Class clazz;
		try {
			clazz = classLoader.loadClass(binaryName);
		} catch (ClassNotFoundException e) {
			logger.error("loadPlugin", e);
			return false;
		}

		Object o;
		try {
			o = clazz.newInstance();
		} catch (InstantiationException e) {
			logger.error("loadPlugin", e);
			return false;
		} catch (IllegalAccessException e) {
			logger.error("loadPlugin", e);
			return false;
		}
		
		Command in = null;
		try {
			in = ((Command) o).build(mucc);
		} catch (PluginBuildException e) {
			logger.error("loadPlugin", e);
			return false;
		}
		if(in == null){
			return false;
		}

		commands.put(in.getCommand(),in);
		logger.debug("NACTENO: " + in.getCommand());
		return true;
	}
	
	boolean removeCommand(String command){
		if(!commands.containsKey(command))
			return false;
		commands.get(command).destroy();
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
					commands.put(c.getCommand(),c.build(mucc));
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

	public Collection<Command> getCommands() {
		return commands.values();
	}
}
