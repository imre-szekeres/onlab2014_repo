package hu.bme.aut.wman.service;

import hu.bme.aut.wman.model.User;
import hu.bme.aut.wman.service.validation.UserValidator;
import hu.bme.aut.wman.service.validation.ValidationEngine;

import java.io.Serializable;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Map;
import java.util.Map.Entry;

import javax.annotation.PostConstruct;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;

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

	@PostConstruct
	public void setup() {
		validator = new UserValidator();
	}

	public Map<String, String> validate(User user, String otherPassword, boolean isRegistered) {
		Map<String, String> errors = validator.validate(user);
		if (isRegistered) {
			validateUsernameSingularity(user, errors);
		} else {
			validateOldPassword(user, errors);
		}
		validateConfirmationPassword(user, otherPassword, errors);
		return errors;
	}

	private void validateUsernameSingularity(User user, Map<String, String> errors) {
		if (selectByName(user.getUsername()) != null) {
			errors.put("username", "Given username already exists.");
		}
	}

	private void validateConfirmationPassword(User user, String confirmationPassword, Map<String, String> errors) {
		if (!user.getPassword().equals(confirmationPassword)) {
			errors.put("confirmPassword", "Confirmation password does not match.");
		}
	}

	private void validateOldPassword(User user, Map<String, String> errors) {
		User old = selectByName(user.getUsername());
		if (!old.getPassword().equals(user.getPassword())) {
			errors.put("oldPassword", "Given password is incorrect.");
		}
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

}