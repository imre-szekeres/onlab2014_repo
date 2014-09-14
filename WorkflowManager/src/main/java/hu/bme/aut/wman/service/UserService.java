package hu.bme.aut.wman.service;

import hu.bme.aut.wman.model.User;

import java.io.Serializable;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Map.Entry;

import javax.annotation.PostConstruct;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;

@LocalBean
@Stateless
@SuppressWarnings("serial")
public class UserService extends AbstractDataService<User> implements Serializable {

	// private Validator validator;

	@PostConstruct
	public void init() {
		this.setClass(User.class);
		// validator = Validation.buildDefaultValidatorFactory().getValidator();
	}

	public User findByName(String username) {
		ArrayList<Entry<String, Object>> parameterList = new ArrayList<Entry<String, Object>>();
		parameterList.add(new AbstractMap.SimpleEntry<String, Object>("name", username));
		// FIXME should check if has exactly one element
		return findByParameters(parameterList).get(0);
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