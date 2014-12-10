/**
 * StringUtils.java
 */
package hu.bme.aut.wman.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 * Responsible for handling several <code>String</code> operations.
 * 
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
		return (s == null) || s.isEmpty();
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

	/**
	 * Obtains the <code>String</code> representation of the given <code>Object</code> or returns "null"
	 * if the <code>Object</code> was null.
	 * 
	 * @param object 
	 * @return the {@link String} representation of the given {@link Object}
	 * */
	public static final String asString(Object object) {
		return (object == null) ? "null" : object.toString();
	}

	/**
	 * Transforms the input <code>Collection</code> to a <code>String</code> in a format of 
	 * [e1, e2, ..., eN] calling the toString() method of each element considering null values.
	 * 
	 * @param elements
	 * @return the {@link String} representation of the given {@link List}
	 * */
	public static final <T> String asString(Collection<T> elements) {
		StringBuffer buffer = new StringBuffer("[");
		for(T element : elements) {
			buffer.append(String.format("%s, ", (element == null) ? "null" : element.toString()));
		}
		return buffer.substring(0, buffer.length() - 1) + ']';
	}

	/**
	 * Transforms the input <code>List</code> to a <code>String</code> in a format of 
	 * [e1, e2, ..., eN] calling the toString() method of each element considering null values.
	 * 
	 * @param elements
	 * @return the {@link String} representation of the given {@link List}
	 * */
	public static final <T> String asString(List<T> elements) {
		StringBuffer buffer = new StringBuffer("[");
		for(T element : elements) {
			buffer.append(String.format("%s, ", (element == null) ? "null" : element.toString()));
		}
		return buffer.substring(0, buffer.length() - 1) + ']';
	}

	/**
	 * Transforms the input <code>Set</code> to a <code>String</code> in a format of 
	 * (e1, e2, ..., eN) calling the toString() method of each element considering null values.
	 * 
	 * @param elements
	 * @return the {@link String} representation of the given {@link Set}
	 * */
	public static final <T> String asString(Set<T> elements) {
		StringBuffer buffer = new StringBuffer("(");
		for(T element : elements) {
			buffer.append(String.format("%s, ", (element == null) ? "null" : element.toString()));
		}
		return buffer.substring(0, buffer.length() - 1) + ')';
	}
}
