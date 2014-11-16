/**
 * SecurityToken.java
 */
package hu.bme.aut.wman.security;

import java.io.Serializable;
/**
 * @author Imre Szekeres
 * @version "%I%, %G%"
 */
public class SecurityToken implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -990387414281134984L;
	private final long userID;
	
	public SecurityToken(long userID) {
		this.userID = userID;
	}
	
	/**
	 * 
	 * @return the id identifying a user
	 * */
	public long getUserID() {
		return userID;
	}

	/**
	 * 
	 * @return a hash value corresponding to this instance
	 * @see {@link java.lang.Object#hashCode()}
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (userID ^ (userID >>> 32));
		return result;
	}

	/**
	 * 
	 * @param obj
	 * 
	 * @return 
	 * @see {@link java.lang.Object#equals(java.lang.Object)}
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof SecurityToken))
			return false;
		SecurityToken other = (SecurityToken) obj;
		if (userID != other.userID)
			return false;
		return true;
	}
}
