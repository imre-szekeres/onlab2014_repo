/**
 * StringUtils.java
 */
package hu.bme.aut.wman.utils;

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
	public static String build(String... parts) {
		StringBuffer buffer = new StringBuffer();
		for(String part : parts)
			buffer.append(part);
		return buffer.toString();
	}
}
