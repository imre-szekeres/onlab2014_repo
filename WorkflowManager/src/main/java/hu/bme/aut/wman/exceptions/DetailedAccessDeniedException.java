/**
 * DetailedAccessDeniedException.java
 */
package hu.bme.aut.wman.exceptions;

import java.util.List;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.ConfigAttribute;

/**
 * Responsible for providing detailed information about the denial of access by also storing the <code>List</code>
 * of <code>ConfigAttribute</code>s required by the requested Secure Object.
 * 
 * @author Imre Szekeres
 * @version "%I%, %G%"
 */
public class DetailedAccessDeniedException extends AccessDeniedException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2095800114147146277L;
	private final List<? extends ConfigAttribute> requiredAuthorities;
	
	public DetailedAccessDeniedException(String msg, List<? extends ConfigAttribute> requiredAuthorities ) {
		super(msg);
		this.requiredAuthorities = requiredAuthorities;
	}

	/**
	 * @return requiredAuthorities
	 * */
	public List<? extends ConfigAttribute> getRequiredAuthorities() {
		return requiredAuthorities;
	}
}
