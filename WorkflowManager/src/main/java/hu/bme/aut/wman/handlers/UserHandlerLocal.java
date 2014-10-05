/**
 * UserHandlerLocal.java
 */
package hu.bme.aut.wman.handlers;

import hu.bme.aut.wman.model.User;

import java.util.Map;

import javax.ejb.Local;
/**
 * @author Imre Szekeres
 * @version "%I%, %G%"
 */
@Local
public interface UserHandlerLocal {

	User createUser(User user, String initialRole, String domain);

	User removeUser(long userID)  throws Exception;

	boolean addRole(long userID, String role);
	boolean removeRole(long userID, String role) throws Exception;

	User assignUser(long userID, String domain, String role);
	User deassignUser(long userID, String domain) throws Exception;

	Map<String, String> validate(User user, String otherPassword, boolean isRegistered);
}
