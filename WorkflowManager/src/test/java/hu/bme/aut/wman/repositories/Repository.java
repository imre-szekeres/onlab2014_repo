/**
 * Repository.java
 */
package hu.bme.aut.wman.repositories;

/**
 * @author Imre Szekeres
 * @version "%I%, %G%"
 */
public interface Repository <K, V> {
	
	/**
	 * Creates a new entry in the <code>Repository</code> and returns
	 * the stored entity.
	 * 
	 * @param key
	 * @param value
	 * @return the stored entry 
	 * */
	V create(K key, V value);

	/**
	 * Returns the value corresponding to the given key.
	 * 
	 * @param key
	 * @return the value
	 * */
	V read(K key);

	/**
	 * Updates the entry corresponding to the key value with the value passed.
	 * 
	 * @param key
	 * @param value
	 * @return the updated entry
	 * */
	V update(K key, V value);

	/**
	 * Removes the entry corresponding to the given key from the data source 
	 * and returns the value previously stored.
	 * 
	 * @param key
	 * @return the value removed
	 * */
	V remove(K key);
}
