/**
 * ManagingUtils.java
 */
package hu.bme.aut.tomeesample.utils;

import java.util.Collection;

/**
 * 
 * @author Imre Szekeres
 * @version "%I%, %G%"
 */
@SuppressWarnings("unchecked")
public class ManagingUtils {

	/**
	 * Slices down the characters from before the first occurrence of the given
	 * 'from' indicator.
	 * 
	 * @param whole
	 *            the <code>String</code> to slice
	 * @param from
	 *            the substring
	 * 
	 * @return a sub<code>String</code> from 'from'
	 * */
	public static String fetchFrom(String whole, String from) {
		return whole.substring(whole.indexOf(from));
	}

	/**
	 * 
	 * 
	 * @param value
	 * @param defValue
	 * 
	 * @return the default value only if the value specified is null
	 * */
	public static <T> T nullThenDefault(T value, T defValue) {
		return value == null ? defValue : value;
	}

	/**
	 * Returns the first element in the specified array.
	 * 
	 * @param array
	 * @return the first element
	 * */
	public static <T> T firstOf(T[] array) {
		return array[0];
	}

	/**
	 * Returns the first element in the specified <code>Collection</code>.
	 * 
	 * @param array
	 * @return the first element
	 * 
	 * @see java.util.Collection#toArray()
	 * */
	public static <T> T firstOf(Collection<? extends T> coll) {
		return (T) coll.toArray()[0];
	}

	/**
	 * Returns the last element of the specified array.
	 * 
	 * @param arrray
	 *            to return the last element from
	 * @return the element at the index array.length - 1
	 * */
	public static <T> T lastOf(T[] array) {
		return array[array.length - 1];
	}

	/**
	 * Returns the last element of the specified <code>Collection</code>.
	 * 
	 * @param coll
	 *            to return the last element from
	 * @return the element at index size() - 1
	 * 
	 * @see java.util.Collection#toArray()
	 * @see java.util.Collection#size()
	 * */
	public static <T> T lastOf(Collection<? extends T> coll) {
		return (T) coll.toArray()[coll.size() - 1];
	}
}
