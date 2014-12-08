/**
 * MessagedAccessDeniedException.java
 */
package hu.bme.aut.wman.exceptions;

import org.springframework.security.access.AccessDeniedException;

/**
 * @author Imre Szekeres
 * @version "%I%, %G%"
 */
public class MessagedAccessDeniedException extends AccessDeniedException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6544728448676905119L;

	public MessagedAccessDeniedException(String msg) {
		super(msg);
	}
}
