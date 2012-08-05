package eu.janinko.xmppmuc.data;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Properties;

import eu.janinko.xmppmuc.commands.Command;
import org.apache.log4j.Logger;

public final class PropertiesPluginData extends PluginData {

    Properties props;
    private File file;
    private static Logger logger = Logger.getLogger(PropertiesPluginData.class);

    public PropertiesPluginData(Class<Command> clazz) {
        super(clazz);
        file = new File(DATA_PATH + clazz.getName() + ".properties");
        props = new Properties();
        data = new PropertiesPluginDataTree(this);
        
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException ex) {
                logger.error("Faile doesn't exist and can't be created.", ex);
            }
        }

        try {
            load();
            persistent = true;
        } catch (FileNotFoundException ex) {
            logger.error("Failed to find data file, persistence turned off.", ex);
        } catch (IOException ex) {
            logger.error("Failed to read data file, persistence turned off.", ex);
        }

    }

    @Override
    public void save() throws IOException {
        if(logger.isTraceEnabled()){logger.trace("Saving properties to " + file);}
        props.store(new FileWriter(file), null);
    }

    @Override
    public void load() throws FileNotFoundException, IOException {
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
