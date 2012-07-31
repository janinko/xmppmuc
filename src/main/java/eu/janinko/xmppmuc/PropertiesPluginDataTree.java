package eu.janinko.xmppmuc;

import java.io.IOException;


public class PropertiesPluginDataTree implements PluginDataTree {
	PropertiesPluginData ppd;
	private String prefix;

	public PropertiesPluginDataTree(PropertiesPluginData ppd){
		this(ppd, "");
	}

	private PropertiesPluginDataTree(PropertiesPluginData ppd, String prefix){
		this.ppd = ppd;
		this.prefix = prefix;
	}
	
	public PluginDataTree getDataTree(String key){
		return new PropertiesPluginDataTree(ppd,prefix + key + ".");	
	}

	public String getValue(String key){
		return ppd.props.getProperty(prefix + key);
	}

	public PluginDataTree push(String key, String value) throws IOException{
		ppd.props.setProperty(prefix + key, value);
		if(ppd.isPersistent()){
			ppd.save();
		}
		return this;
	}

}
