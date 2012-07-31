package eu.janinko.xmppmuc;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Properties;

import eu.janinko.xmppmuc.commands.Command;

public class PropertiesPluginData extends PluginData {
	Properties props;
	private File file;

	public PropertiesPluginData(Class<Command> clazz){
		super(clazz);
		file = new File(DATA_PATH + clazz.getName() + ".properties");
		props = new Properties();
	}		
	
	public void save() throws IOException{
		props.store(new FileWriter(file),null);
	}

	public void load() throws FileNotFoundException, IOException{
		props.load(new FileReader(file));
		data = new PropertiesPluginDataTree(this);

		/*  //This could be used if we were backing to memory, not Properties
		for(Map.Entry<Object,Object> e : props.entrySet()){
			String[] key = ((String) e.getKey()).split("\\.",1);
			String key = (String) e.getValue();
			
			while(key.length() > 1){
				data = data.getDataTree(key);
				key = key.split("\\.",1);			
			}
			data.push(key,value);
		}*/
	}

	
}
