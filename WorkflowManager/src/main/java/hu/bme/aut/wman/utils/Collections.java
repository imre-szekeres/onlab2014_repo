/**
 * Collections.java
 */
package hu.bme.aut.wman.utils;

import java.util.List;
import java.util.Map;
import java.util.Set;
/**
 * @author Imre Szekeres
 * @version "%I%, %G%"
 */
public class Collections {

	/**
	 * Prints the given <code>Map</code> to the standard output.
	 * 
	 * @param map
	 * */
	public static final <K, V> void print(Map<K, V> map) {
		StringBuffer buffer = new StringBuffer("{");
		for(K key : map.keySet())
			buffer.append(String.format("%s: %s, ", key, map.get(key)));
		System.out.println( buffer.substring(0, buffer.length() - 1) + "}" );
	}
	
	/**
	 * Prints the given <code>List</code> to the standard output.
	 * 
	 * @param list
	 * */
	public static final <T> void print(List<T> list) {
		StringBuffer buffer = new StringBuffer("[");
		for(T element : list)
			buffer.append(String.format("%s, ", element));
		System.out.println( buffer.substring(0, buffer.length() - 1) + "]" );
	}
	
	/**
	 * Prints the given <code>Set</code> to the standard output.
	 * 
	 * @param set
	 * */
	public static final <T> void print(Set<T> set) {
		StringBuffer buffer = new StringBuffer("(");
		for(T element : set)
			buffer.append(String.format("%s, ", element));
		System.out.println( buffer.substring(0, buffer.length() - 1) + ")" );
	}
}
