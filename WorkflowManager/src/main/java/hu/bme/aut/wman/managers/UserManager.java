/**
 * UserManager.java
 */
package hu.bme.aut.wman.managers;

import hu.bme.aut.wman.exceptions.EntityNotDeletableException;
import hu.bme.aut.wman.model.User;
import hu.bme.aut.wman.service.DomainAssignmentService;
import hu.bme.aut.wman.service.UserService;

import java.io.Serializable;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * @author Imre Szekeres
 * @version "%I%, %G%"
 */
@Stateless
@LocalBean
public class UserManager implements Serializable {

	private static final long serialVersionUID = -6803494666340100557L;

	@Inject
	private UserService userService;
	@Inject
	private DomainAssignmentService daService;
	
	private PasswordEncoder encoder;

	/**
	 * Initializes the encoder.
	 * */
	@PostConstruct
	public void init() {
		encoder = new BCryptPasswordEncoder();
	}

	/**
	 * Creates a new <code>User</code> and encrypts its password then stores the instance.
	 * 
	 * @param user
	 * @return the persisted {@link User} instance
	 * */
	public User create(User user) {
		user.setPassword(encoder.encode( user.getPassword() ));
		userService.save( user );
		return user;
	}

	/**
	 * Updates the <code>User</code> instance by setting an encrypted version of the password given.
	 * 
	 * @param user
	 * @param password
	 * @return the persisted {@link User} instance
	 * */
	public User update(User user, String password) {
		user.setPassword(encoder.encode( password ));
		userService.save( user );
		return user;
	}

	/**
	 * Attempts to remove the given <code>User</code> from the database and all the 
	 * <code>DomainAssignmnet</code>s corresponding to it.
	 * 
	 * @param user
	 * @return the detached {@link User} instance
	 * */
	public User remove(User user) throws EntityNotDeletableException {
		daService.deleteByUserID(user.getId());
		userService.delete(user);
		return user;
	}

	/**
	 * Attempts to remove the given <code>User</code> from the database and all the 
	 * <code>DomainAssignmnet</code>s corresponding to it.
	 * 
	 * @param userID
	 * @return the detached {@link User} instance
	 * */
	public User remove(Long userID) throws EntityNotDeletableException {
		User user = userService.selectById( userID );
		daService.deleteByUserID(userID);
		userService.delete(user);
		return user;
	}
	
	/**
	 * Delegates the validation mechanism to the <code>UserService</code>.
	 * 
	 * @param user
	 * @param confirmPassword
	 * @return the errors found
	 * @see {@link UserService}
	 * */
	public Map<String, String> validate(User user, String confirmPassword) {
		return userService.validate(user, confirmPassword);
	}

	/**
	 * Delegates the validation mechanism to the <code>UserService</code>.
	 * 
	 * @param old
	 * @param oldPassword
	 * @param newPassword
	 * @param confirmPassword
	 * @return the errors found
	 * @see {@link UserService}
	 * */
	public Map<String, String> validate(User old, String oldPassword, String newPassword, String confirmPassword) {
		return userService.validate(old, oldPassword, newPassword, confirmPassword, encoder);
	}
}
