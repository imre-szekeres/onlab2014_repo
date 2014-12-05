package hu.bme.aut.wman.service;

import hu.bme.aut.wman.model.Domain;
import hu.bme.aut.wman.model.User;
import hu.bme.aut.wman.service.validation.PasswordValidator;
import hu.bme.aut.wman.service.validation.UserValidator;
import hu.bme.aut.wman.service.validation.ValidationEngine;
import hu.bme.aut.wman.utils.StringUtils;

import java.io.Serializable;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.annotation.PostConstruct;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;

import org.springframework.security.crypto.password.PasswordEncoder;


/**
 * Helps make operations with <code>User</code>.
 * 
 * @version "%I%, %G%"
 */
@LocalBean
@Stateless
@SuppressWarnings("serial")
public class UserService extends AbstractDataService<User> implements Serializable {

	private ValidationEngine<User> validator;
	private ValidationEngine<User> passwordValidator;

	/**
	 * Initializes the validation mechanism used.
	 * */
	@PostConstruct
	public void setup() {
		validator = new UserValidator();
		passwordValidator = new PasswordValidator();
	}

	/**
	 * Validates the given <code>User</code> object against its given <code>ValidationConstraint</code>s 
	 * as Java annotation using Bean Validation mechanisms.
	 * 
	 * @param user
	 * @param confirmPassword
	 * @return the {@link java.util.Map} representation of (<code>propertyName</code>, <code>errorMessage</code>) entries
	 * */
	public Map<String, String> validate(User user, String confirmPassword) {
		Map<String, String> errors = validator.validate( user );
		unicityOf(user, errors);
		validateConfirmationPassword(user, confirmPassword, errors);
		return errors;
	}
	
	/**
	 * Determines whether the given <code>User</code> instance corresponds to the unicity
	 * constraint given to the username property. Upon it does not it places an error message into the
	 * <code>Map</code> passed as argument with the key "username".
	 * 
	 * @param user
	 * @param errors
	 * @see {@link User}
	 * */
	private void unicityOf(User user, Map<String, String> errors) {
		User other = selectByName(user.getUsername());
		if ((other != null) && (!other.getId().equals( user.getId() ))) {
			errors.put("username", "Given username already exists.");
		}
	}

	/**
	 * Validates the confirmation password and places an error message into the given <code>Map</code>
	 * with a key "confirmPassword".
	 * 
	 * @param user
	 * @param confirmPassword
	 * @param errors
	 * */
	private void validateConfirmationPassword(User user, String confirmPassword, Map<String, String> errors) {
		if (!user.getPassword().equals(confirmPassword)) {
			errors.put("confirmPassword", "Confirmation password does not match.");
		}
	}
	
	/**
	 * Validates the given <code>User</code> object against its given <code>ValidationConstraint</code>s 
	 * as Java annotation using Bean Validation mechanisms.
	 * 
	 * @param user
	 * @param oldPassword
	 * @param confirmPassword
	 * @param encoder
	 * @return the {@link java.util.Map} representation of (<code>propertyName</code>, <code>errorMessage</code>) entries
	 * */
	public Map<String, String> validate(User old, String oldPassword, String newPassword, String confirmPassword, PasswordEncoder encoder) {
		Map<String, String> errors = new HashMap<>();
		
		if (!encoder.matches(oldPassword, old.getPassword()))
			errors.put("oldPassword", "Given value does not match the previous one.");
		if (StringUtils.isEmpty( newPassword ))
			errors.put("password", "cannot be empty");
		else if (StringUtils.isEmpty( confirmPassword ))
			errors.put("confirmPassword", "cannot be empty");
		else if (!newPassword.equals( confirmPassword ))
			errors.put("confirmPassword", "Does not match the new password");
		
		String pass = old.getPassword();
		old.setPassword( newPassword );
		
		Map<String, String> err = passwordValidator.validate( old );
		errors.putAll( err );
		old.setPassword( pass );
		return errors;
	}

	/**
	 * Retrieves the <code>User</code> specified by its username in case it is found
	 * otherwise null.
	 * 
	 * @param username
	 * @return the {@link User} corresponding to it
	 * */
	public User selectByName(String username) {
		ArrayList<Entry<String, Object>> parameterList = new ArrayList<Entry<String, Object>>();
		parameterList.add(new AbstractMap.SimpleEntry<String, Object>(User.PR_NAME, username));
		List<User> users = callNamedQuery(User.NQ_FIND_BY_NAME, parameterList);
		return users.size() > 0 ? users.get(0) : null;
	}

	/**
	 * Returns all <code>User</code>s owning the <code>Role</code> given by its name.
	 * 
	 * @param roleName
	 * @return a list of {@link User}s owning the {@link Role} with name roleName
	 * */
	public List<User> listUsersOf(String roleName) {
		List<Entry<String, Object>> parameters = new ArrayList<>();
		parameters.add(new AbstractMap.SimpleEntry<String, Object>("roleName", roleName));
		return callNamedQuery("User.findUsersOf", parameters);
	}

	/**
	 * Selects all the <code>User</code>s assigned to all the <code>Domain</code>s the <code>User</code> specified
	 * by its id is also assigned to.
	 * 
	 * @param userID
	 * @return a {@link List} of {@link User}s in the same {@link Domain} as the given {@link User}
	 * */
	public List<User> selectUsersInDomainOf(Long userID) {
		List<Entry<String, Object>> parameters = new ArrayList<>();
		parameters.add(new AbstractMap.SimpleEntry<String, Object>("userID", userID));
		return callNamedQuery(User.NQ_FIND_USERS_IN_DOMAIN_OF, parameters);
	}

	/**
	 * Retrieves the password of the given <code>User<code>.
	 * 
	 * @param username
	 * @return the corresponding password
	 * */
	public String selectPasswordOf(String username) {
		List<Entry<String, Object>> parameters = new ArrayList<>();
		parameters.add(new AbstractMap.SimpleEntry<String, Object>("username", username));
		List<String> results = callNamedQuery(User.NQ_FIND_PASSWORD_OF, parameters, String.class);
		return (results == null || results.isEmpty()) ? null : results.get(0);
	}

	/**
	 * Retrieves the ID of the given <code>User<code>.
	 * 
	 * @param username
	 * @return the corresponding ID
	 * */
	public Long selectIDOf(String username) {
		List<Entry<String, Object>> parameters = new ArrayList<>();
		parameters.add(new AbstractMap.SimpleEntry<String, Object>("username", username));
		List<Long> results = callNamedQuery(User.NQ_FIND_ID_OF, parameters, Long.class);
		return (results == null || results.isEmpty()) ? null : results.get(0);
	}

	/**
	 * @see {@link AbstractDataService#getClass()}
	 * */
	@Override
	protected Class<User> getEntityClass() {
		return User.class;
	}
}