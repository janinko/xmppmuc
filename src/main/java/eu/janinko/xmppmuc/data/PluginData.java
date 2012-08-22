package eu.janinko.xmppmuc.data;

import eu.janinko.xmppmuc.commands.Command;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Map;
import java.util.Set;

public abstract class PluginData implements PluginDataTree {
    protected boolean persistent = false;
    protected PluginDataTree data;
    protected Class<Command> clazz;
    public final String DATA_PATH = System.getProperty("user.home") + "/.xmppmuc/plugins/data/";
    
    public PluginData(Class<Command> clazz) {
        this.clazz = clazz;
    }
    
    public abstract void save() throws IOException;
    
    public abstract void load() throws FileNotFoundException, IOException;
    
    public boolean isPersistent() {
        return persistent;
    }
    
    public void setPersistent(boolean persistent) {
        this.persistent = persistent;
    }
    
    @Override
    public PluginDataTree getDataTree(String key) {
        return data.getDataTree(key);
    }
    
    @Override
    public String getValue(String key) {
        return data.getValue(key);
    }
    
    @Override
    public PluginDataTree push(String key, String value) {
        return data.push(key, value);
    }
    
    @Override
    public Map<String, String> getMap() {
        return data.getMap();
    }
    
    @Override
    public boolean containsKey(String key) {
        return data.containsKey(key);
    }
    
    @Override
    public boolean containsSubtree(String key) {
        return data.containsSubtree(key);
    }
    
    @Override
    public void removeKey(String key) {
        data.removeKey(key);
    }
    
    @Override
    public Set<String> getSubtrees() {
        return data.getSubtrees();
    }
}
