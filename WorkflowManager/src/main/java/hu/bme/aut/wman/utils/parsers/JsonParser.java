/**
 * JsonParser.java
 */
package hu.bme.aut.wman.utils.parsers;

import java.util.Map;
/**
 * Generic Parser for parsing JSON content from and into <code>Map</code> objects.
 * 
 * @author Imre Szekeres
 * @version "%I%, %G%"
 */
public interface JsonParser <K, V>{

	/**
	 * Parses the given JSON string into a <code>Map</code>.
	 * 
	 * @param json
	 * @return the constructed {@link Map}
	 * @throws {@link Exception}
	 * */
	Map<K, V> parse(String json) throws Exception;

	/**
	 * Transforms the given <code>Map</code> instance into a JSON <code>String</code>.
	 * 
	 * @param map
	 * @return a JSON formatted {@link String}
	 * @throws {@link Exception}
	 * */
	String stringify(Map<K, V> map) throws Exception;
}
