package hu.bme.aut.wman.service;

import hu.bme.aut.wman.model.User;
import hu.bme.aut.wman.service.validation.UserValidator;
import hu.bme.aut.wman.service.validation.ValidationEngine;

import java.io.Serializable;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Map.Entry;

import javax.annotation.PostConstruct;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;

import java.util.Map;
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
	// private Validator validator;

	// @PostConstruct
	// public void init() {
	// validator = Validation.buildDefaultValidatorFactory().getValidator();
	// }

	@PostConstruct
	public void setup() {
		validator = new UserValidator();
	}
	
	public Map<String, String> validate(User user, String otherPassword, boolean isRegistered) {
		Map<String, String> errors = validator.validate(user);
		if(isRegistered)
			validateUsernameSingularity(user, errors);
		else
			validateOldPassword(user, errors);
		validateConfirmationPassword(user, otherPassword, errors);
		return errors;
	}
	
	private void validateUsernameSingularity(User user, Map<String, String> errors) {
		if(selectByName(user.getUsername()) != null)
			errors.put("username", "Given username already exists.");
	}
	
	private void validateConfirmationPassword(User user, String confirmationPassword, Map<String, String> errors) {
		if(!user.getPassword().equals(confirmationPassword))
			errors.put("confirmPassword", "Confirmation password does not match.");
	}
	
	private void validateOldPassword(User user, Map<String, String> errors) {
		User old = selectByName(user.getUsername());
		if(!old.getPassword().equals(user.getPassword()))
			errors.put("oldPassword", "Given password is incorrect.");
	}
	
	public User selectByName(String username) {
		ArrayList<Entry<String, Object>> parameterList = new ArrayList<Entry<String, Object>>();
		parameterList.add(new AbstractMap.SimpleEntry<String, Object>(User.PR_NAME, username));
		// FIXME should check if has exactly one element
		if (selectByParameters(parameterList).size() > 0) {
			return selectByParameters(parameterList).get(0);
		}
		return null;
	}

	@Override
	protected Class<User> getEntityClass() {
		return User.class;
	}

	// /**
	// * Validates the given user name against the constraints given in the <code>User</code> class.
	// *
	// * @param username
	// * that will be validated
	// * @return true only if the given user name corresponds to the constraints
	// * given in the class <code>User</code>
	// * */
	// public boolean validateUsername(String username) {
	// return validator.validateValue(User.class, "username", username).size() == 0;
	// }
	//
	// /**
	// * Validates the given password against the constraints given in the <code>User</code> class.
	// *
	// * @param password
	// * that will be validated
	// * @return true only if the given password corresponds to the constraints
	// * given in the class <code>User</code>
	// * */
	// public boolean validatePassword(String password) {
	// return validator.validateValue(User.class, "password", password).size() == 0;
	// }
	//
	// /**
	// * Validates the given passwords against each other, check if they match or
	// * not.
	// *
	// * @param first
	// * @param again
	// * @return true only if the given passwords do match
	// * */
	// public boolean validatePasswords(String first, String again) {
	// if (first == again && again == null) {
	// return false;
	// }
	// return again.equals(first);
	// }
	//
	// /**
	// * Validates the given email against the constraints given in the <code>User</code> class.
	// *
	// * @param email
	// * that will be validated
	// * @return true only if the given email corresponds to the constraints given
	// * in the class <code>User</code>
	// * */
	// public boolean validateEmail(String email) {
	// return validator.validateValue(User.class, "email", email).size() == 0;
	// }
	//
	// /**
	// * Validates the given description against the constraints given in the <code>User</code> class.
	// *
	// * @param description
	// * that will be validated
	// * @return true only if the given description corresponds to the constraints
	// * given in the class <code>User</code>
	// * */
	// public boolean validateDescription(String description) {
	// return validator.validateValue(User.class, "description", description).size() == 0;
	// }
}