/**
 * StringUtils.java
 */
package hu.bme.aut.wman.utils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Imre Szekeres
 * @version "%I%, %G%"
 */
public class StringUtils {

	/**
	 * Concatenates a given series of <code>String</code> parts into one <code>String</code>
	 * in a memory-efficient way.
	 * 
	 * @param parts of <code>String</code>s to be concatenated
	 * @return the <code>String</code> concatenation of the given parts
	 * */
	public static final String build(String... parts) {
		StringBuffer buffer = new StringBuffer();
		for(String part : parts)
			buffer.append(part);
		return buffer.toString();
	}
	
	/**
	 * Checks whether a given <code>String</code> is null or an empty string.
	 * 
	 * @param s
	 * @return whether s can be handled as an empty <code>String</code>
	 * */
	public static final boolean isEmpty(String s) {
		return (s != null) && !s.isEmpty();
	}
	
	/**
	 * Removes the empty <code>String</code>s from the given list.
	 * 
	 * @param strings
	 * @return a <code>List</code> without the empty strings
	 * */
	public static final List<String> removeEmpty(List<String> strings) {
		List<String> results = new ArrayList<>();
		for(String s : strings) {
			if (!StringUtils.isEmpty( s ))
				results.add( s );
		}
		return results;
	}
}
