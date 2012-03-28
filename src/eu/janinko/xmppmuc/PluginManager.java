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

import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Message;

import eu.janinko.xmppmuc.commands.Command;
import eu.janinko.xmppmuc.commands.PluginBuildException;

public class PluginManager {
	private MucCommands mucc;
	private Map<String, Command> commands;
	
	
	public PluginManager(MucCommands mucCommands) {
		this.mucc = mucCommands;
		commands = new HashMap<String, Command>();
	}

	public void loadPlugins(){
		StringBuilder sb = new StringBuilder("Loaded plugins: ");
		for(Command c : ServiceLoader.load(Command.class)){
			try {
				commands.put(c.getCommand(),c.build(mucc));
				sb.append(c.getCommand());
				sb.append(", ");
			} catch (PluginBuildException e) {
				System.err.println("PluginManager.loadPlugins() A");
				e.printStackTrace();
			}
		}
		sb.delete(sb.length()-2,sb.length());
		System.out.println(sb.toString());
	}
	
	@SuppressWarnings("unchecked")
	public boolean loadPlugin(String binaryName){
		File pluginDirectory = new File(System.getProperty("user.home") + "/.xmppmuc/plugins/jar/");
		ArrayList<URL> urls = new ArrayList<URL>();
		for(File f : pluginDirectory.listFiles()){
			try {
				urls.add(f.toURI().toURL());
			} catch (MalformedURLException e) {
				System.err.println("PluginManager.loadPlugin() A");
				e.printStackTrace();
				return false;
			}
		}
		URLClassLoader classLoader = new URLClassLoader(urls.toArray(new URL[0]), getClass().getClassLoader());
		try {
			Class clazz = classLoader.loadClass(binaryName);
			Object o = clazz.newInstance();
			Command c = (Command) o;

			
			Command in = c.build(mucc);

			if(in == null){
				return false;
			}
			
			commands.put(in.getCommand(),in);
			System.out.println("NACTENO: " + in.getCommand());
		} catch (ClassNotFoundException e1) {
			System.err.println("PluginManager.loadPlugin() B");
			e1.printStackTrace();
			return false;
		} catch (InstantiationException e) {
			System.err.println("PluginManager.loadPlugin() C");
			e.printStackTrace();
			return false;
		} catch (IllegalAccessException e) {
			System.err.println("PluginManager.loadPlugin() D");
			e.printStackTrace();
			return false;
		} catch (PluginBuildException e) {
			System.err.println("PluginManager.loadPlugin() E");
			e.printStackTrace();
			return false;
		}
		
		return true;
	}
	
	public boolean removeCommand(String command){
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

	public void handleCommand(Message m) {
		String command = mucc.hGetCommand(m);
		String[] args = command.split(" ");
		try {
			if(args[1].equals("stop")){
				if(removeCommand(args[2])){
					mucc.getMuc().sendMessage("Plugin " + args[2] + " byl zastaven.");
				}
			}else if(args[1].equals("load")){
				if(loadPlugin(args[2])){
					mucc.getMuc().sendMessage("Plugin " + args[2] + " byl načten.");
				}
			}else if(args[1].equals("start")){
				if(startPlugin(args[2])){
					mucc.getMuc().sendMessage("Plugin " + args[2] + " byl spuštěn.");
				}
			}
		} catch (XMPPException e) {
			System.err.println("PluginManager.handleCommand() A");
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
