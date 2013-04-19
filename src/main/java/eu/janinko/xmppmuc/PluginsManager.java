package eu.janinko.xmppmuc;

import eu.janinko.xmppmuc.api.plugin.Command;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.ServiceLoader;
import java.util.Set;
import java.util.WeakHashMap;
import org.apache.log4j.Logger;

/**
 * @author Honza Brázdil <jbrazdil@redhat.com>
 */
public class PluginsManager {
	public static final String DATA_DIR = System.getProperty("user.home") + "/.xmppmuc/";
	private static final String PLUGIN_DIR = DATA_DIR + "plugins/";
	private static final String PLUGIN_JAR_DIR = PLUGIN_DIR + "jar/";
	private static Logger logger = Logger.getLogger(PluginsManager.class);
	private HashMap<String, Class<? extends Command>> plugins = new HashMap<>();

	private XmppmucClassLoader rootClassLoader = new XmppmucClassLoader(getClass().getClassLoader());
	private ClassLoader classLoader = PluginsManager.class.getClassLoader();

	public void scanPluginFolder(){
		logger.trace("Scanning pluginFolder");

		File pluginDirectory = new File(PLUGIN_JAR_DIR);

		ArrayList<URL> urls = new ArrayList<>();
		for(File f : pluginDirectory.listFiles()){
			if(logger.isTraceEnabled()){logger.trace("Checking file: " + f.getAbsolutePath());}
			try {
				urls.add(f.toURI().toURL());
			} catch (MalformedURLException e) {
				logger.warn("Failed to procces file: " + f, e);
			}
		}

		URLClassLoader cl = URLClassLoader.newInstance(urls.toArray(new URL[urls.size()]));
		synchronized(this){
			classLoader = cl;
			rootClassLoader.addClassLoader(cl);
		}
	}

	synchronized public Set<Class<? extends Command>> getCommands(){
		return new HashSet<>(plugins.values());
	}

	synchronized public Class<? extends Command> getCommand(String canonicalName){
		if(plugins.containsKey(canonicalName)){
			return plugins.get(canonicalName);
		}
		try {
			@SuppressWarnings("unchecked")
			Class<? extends Command> clazz = (Class<? extends Command>) classLoader.loadClass(canonicalName);
			plugins.put(clazz.getCanonicalName(), clazz);
			return clazz;
		} catch (ClassNotFoundException | ClassCastException ex) {
			return null;
		}
	}

	synchronized public void loadPlugins(){
		plugins.clear();
		for(Command c : ServiceLoader.load(Command.class, classLoader)){
			Class<? extends Command> clazz = c.getClass();
			plugins.put(clazz.getCanonicalName(), clazz);
		}
	}

	public ClassLoader getClassLoader(){
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
