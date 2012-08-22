/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.janinko.xmppmuc.data;

import eu.janinko.xmppmuc.commands.Command;
import eu.janinko.xmppmuc.commands.Kill;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import junit.framework.TestCase;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 *
 * @author jbrazdil
 */
public class PluginDataTests{
	File temp;
			
	@Before
	public void setFile() throws IOException{
		temp = File.createTempFile("xmppmuc", Long.toString(System.nanoTime()));
		temp.delete();
		temp.mkdir();
		temp.deleteOnExit();
		if(PluginData.DATA_PATH.isEmpty()){
			PluginData.DATA_PATH=temp.getAbsolutePath();
		}else{
			PluginData.DATA_PATH=temp.getAbsolutePath();
		}
	}
	
	@After
	public void delFile() throws IOException{
		File[] files = temp.listFiles();
		for(int i=0; i<files.length; i++) {
			files[i].delete();
		}
		temp.delete();
	}
	
	@Test
	public void testPropertiesPD(){
		test(new PropertiesPluginData(Command.class), Command.class);
		test(new PropertiesPluginData(Kill.class), Kill.class);
	}

	private void test(PluginData pd, Class<? extends Command> clazz) {
		testPush(pd); // abra = kadabra; foo = bar; John = Doe;
		testRemove(pd); // abra = kadabra; John = Doe;
		testMap(pd); // abra = kadabra; John = Doe;
	}
	
	private void testPush(PluginData pd){
		Assert.assertFalse(pd.containsKey("abra"));
		pd.push("abra", "kadabra");
		Assert.assertTrue(pd.containsKey("abra"));
		Assert.assertEquals("kadabra", pd.getValue("abra"));
		
		Assert.assertFalse(pd.containsKey("foo"));
		pd.push("foo", "bar");
		Assert.assertTrue(pd.containsKey("foo"));
		Assert.assertEquals("bar", pd.getValue("foo"));
		
		Assert.assertFalse(pd.containsKey("John"));
		pd.push("John", "Doe");
		Assert.assertTrue(pd.containsKey("John"));
		Assert.assertEquals("Doe", pd.getValue("John"));
	}
	
	private void testRemove(PluginData pd){
		Assert.assertTrue(pd.containsKey("foo"));
		pd.removeKey("foo");
		Assert.assertFalse(pd.containsKey("foo"));
	}
	
	private void testMap(PluginData pd){
		HashMap<String, String> map = new HashMap<String, String>();
		map.put("abra", "kadabra");
		map.put("John", "Doe");
		Assert.assertEquals(map, pd.getMap());
	}
	
}
