package eu.janinko.xmppmuc.data;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.apache.log4j.Logger;

public class PropertiesPluginDataTree implements PluginDataTree {
    PropertiesPluginData ppd;
    private String prefix;
    
    private static Logger logger = Logger.getLogger(PropertiesPluginDataTree.class);

    public PropertiesPluginDataTree(PropertiesPluginData ppd) {
        this(ppd, "");
    }

    private PropertiesPluginDataTree(PropertiesPluginData ppd, String prefix) {
        this.ppd = ppd;
        this.prefix = prefix;
    }

    @Override
    public PluginDataTree getDataTree(String key) {
        key = key.replaceAll("\\.", "\\\\.");
        return new PropertiesPluginDataTree(ppd, prefix + key + ".");
    }

    @Override
    public String getValue(String key) {
        key = key.replaceAll("\\.", "\\\\.");
        return ppd.props.getProperty(prefix + key);
    }

    @Override
    public PluginDataTree push(String key, String value){
        key = key.replaceAll("\\.", "\\\\.");
        
        ppd.props.setProperty(prefix + key, value);
        if (ppd.isPersistent()) {
            try {
                ppd.save();
            } catch (IOException ex) {
                ppd.setPersistent(false);
                logger.error("Failed to persist data, turning off persistence.", ex);
            }
        }
        return this;
    }

    @Override
    public Map<String, String> getMap() {
        HashMap<String, String> ret = new HashMap<String, String>();
        int plen = prefix.length();
        for(Map.Entry<Object, Object> e : ppd.props.entrySet()){
            String key = (String) e.getKey();
            if(key.startsWith(prefix)){
                String pseudokey = key.substring(plen).replaceAll("\\\\.", "");
                if(! pseudokey.contains(".")){
                    String value = (String) e.getValue();
                    ret.put(key.substring(plen).replaceAll("\\\\.", "."), value);
                }
            }
        }
        return ret;
    }

    @Override
    public boolean containsKey(String key) {
        key = key.replaceAll("\\.", "\\\\.");
        return ppd.props.get(prefix + key) != null;
    }

    @Override
    public boolean containsSubtree(String key) {
        key = key.replaceAll("\\.", "\\\\.");
        int klen = key.length();
        for(Object o :  ppd.props.keySet()){
            String k = (String) o;
            
            if(k.startsWith(key) && !k.substring(klen).isEmpty()){
                return true;
            }
        }
        return false;
    }

    @Override
    public void removeKey(String key) {
        key = key.replaceAll("\\.", "\\\\.");
        ppd.props.remove(prefix + key);
        if (ppd.isPersistent()) {
            try {
                ppd.save();
            } catch (IOException ex) {
                ppd.setPersistent(false);
                logger.error("Failed to persist data, turning off persistence.", ex);
            }
        }
    }

	@Override
	public Set<String> getSubtrees() {
        HashSet<String> ret = new HashSet<String>();
        int plen = prefix.length();
		ALL:
        for(Map.Entry<Object, Object> e : ppd.props.entrySet()){
            String key = (String) e.getKey();
            if(key.startsWith(prefix)){
				key = key.substring(plen);
                String pseudokey = key.replaceAll("\\\\.", "");
                if(pseudokey.contains(".")){
					int index=-1;
					do{
						index = key.indexOf('.', index+1);
						if(index != -1){
							if(key.charAt(index) != '\\'){
								ret.add(key.substring(0, index).replaceAll("\\\\.", "."));
								continue ALL;
							}
						}
					}while(index != -1);
                }
            }
        }
        return ret;
	}
}
