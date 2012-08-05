package eu.janinko.xmppmuc.data;

import java.io.IOException;
import java.util.Map;


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
	public PluginDataTree push(String key, String value);

	/** Return Map of keys and values.
	 *  If no keys are present, it must return empty map.
	 *
	 */
	public Map<String, String> getMap();
        
	/** Return true if this tree contains specified key.
	 *  If key isn't present, but subtree with that key is, return false.
	 *
	 */
        public boolean containsKey(String key);
        
	/** Return true if this tree contains subtree named by this key.
	 *  If key is present, but subtree with that key isn't, return false.
	 *
	 */
        public boolean containsSubtree(String key);
        
	/** Delete key and it's value.
	 *  If key isn't present, do nothing.
	 *
	 */
        public void removeKey(String key);
}
