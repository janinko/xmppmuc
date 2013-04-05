package eu.janinko.xmppmuc.data;

import com.thoughtworks.xstream.XStream;
import eu.janinko.xmppmuc.Bot;
import eu.janinko.xmppmuc.commands.Command;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

/**
 * @author Honza Brázdil <jbrazdil@redhat.com>
 */
public class XMLStorage {
	protected static final String DATA_PATH = Bot.DATA_DIR + "plugins/data/";

	protected Class<? extends Command> clazz;
	private XStream xstream;

	public XMLStorage(Class<? extends Command> clazz, ClassLoader cl){
		this.clazz = clazz;
		this.xstream = new XStream();
		xstream.setClassLoader(cl);
	}

	public void save(Object o) throws IOException{
		FileWriter out = new FileWriter(DATA_PATH + clazz.getCanonicalName() + ".xml");
		xstream.toXML(o, out);
	}

	public Object load() throws IOException{
		FileReader out = new FileReader(DATA_PATH + clazz.getCanonicalName() + ".xml");

		return xstream.fromXML(out);
	}

}
