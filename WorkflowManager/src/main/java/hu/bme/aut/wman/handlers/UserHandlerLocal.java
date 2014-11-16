/**
 * UserHandlerLocal.java
 */
package hu.bme.aut.wman.handlers;

import hu.bme.aut.wman.model.User;

import javax.ejb.Local;
/**
 * @author Imre Szekeres
 * @version "%I%, %G%"
 */
@Local
public interface UserHandlerLocal {

	User createUser(User user, String initialRole, String domain);

	User removeUser(long userID)  throws Exception;

	boolean addRole(long userID, String role, String domain);
	boolean removeRole(long userID, String role, String domain) throws Exception;

	User assignUser(long userID, String role, String domain);
	User deassignUser(long userID, String domain) throws Exception;
}
