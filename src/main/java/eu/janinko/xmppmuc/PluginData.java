package eu.janinko.xmppmuc;

import java.io.FileNotFoundException;
import java.io.IOException;

import eu.janinko.xmppmuc.commands.Command;

public abstract class PluginData implements PluginDataTree {
	protected boolean persistent;
	protected PluginDataTree data;
	protected Class<Command> clazz;

	public final String DATA_PATH = System.getProperty("user.home") + "/.xmppmuc/plugins/data/";

	public PluginData(Class<Command> clazz) {
		this.clazz = clazz;
	}
	
	public abstract void save() throws IOException;

	public abstract void load() throws FileNotFoundException, IOException;

	public boolean isPersistent(){
		return persistent;
	}

	public void setPersistent(boolean persistent){
		this.persistent = persistent;
	}
	
	public PluginDataTree getDataTree(String key){
		return data.getDataTree(key);
	}

	public String getValue(String key){
		return data.getValue(key);
	}

    public PluginDataTree push(String key, String value) throws IOException{
		return data.push(key, value);
	}

}
