package eu.janinko.xmppmuc;

import java.io.IOException;


public interface PluginDataTree {
	
	/** Return PluginDataTree for specified key.
	 *  If PluginData isn't present, it must be created and returned.
	 *
	 */
	public PluginDataTree getDataTree(String key);

	/** Return value for specified key.
	 *  If value isn't set, return null.
	 *
	 */
	public String getValue(String key);

	/** Set value for specified key overwriting old one.
	 *  Return this modiffied PluginDataTree.
	 * @throws IOException 
	 *
	 */
	public PluginDataTree push(String key, String value) throws IOException;
}
