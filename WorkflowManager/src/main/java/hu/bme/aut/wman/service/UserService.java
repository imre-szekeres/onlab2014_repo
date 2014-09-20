package hu.bme.aut.wman.service;

import hu.bme.aut.wman.exceptions.EntityNotFoundException;
import hu.bme.aut.wman.exceptions.TooMuchElementException;
import hu.bme.aut.wman.model.Role;
import hu.bme.aut.wman.model.User;

import java.io.Serializable;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.TypedQuery;

/**
 * Helps make operations with <code>User</code>.
 * 
 * @version "%I%, %G%"
 */
@LocalBean
@Stateless
@SuppressWarnings("serial")
public class UserService extends AbstractDataService<User> implements Serializable {

	// private Validator validator;

	// @PostConstruct
	// public void init() {
	// validator = Validation.buildDefaultValidatorFactory().getValidator();
	// }

	public User selectByName(String username) {
		ArrayList<Entry<String, Object>> parameterList = new ArrayList<Entry<String, Object>>();
		parameterList.add(new AbstractMap.SimpleEntry<String, Object>(User.PR_NAME, username));

		try {
			return checkHasExactlyOneElement(selectByOwnProperties(parameterList)).get(0);
		} catch (TooMuchElementException e) {
			// TODO handle users with same name, although it should not happen :)
			return null;
		} catch (EntityNotFoundException e) {
			return null;
		}
	}

	/**
	 * Use findByParameters method instead
	 */
	@Deprecated
	public List<User> selectByProject(Long projectID) {
		TypedQuery<User> selectFor = em.createQuery("SELECT u FROM User u, ProjectAssignment pa "
				+ "WHERE pa.user = u AND pa.project.id = :projectID", User.class);
		selectFor.setParameter("projectID", projectID);
		return selectFor.getResultList();
	}

	@Deprecated
	public Set<User> findUsersBy(Long roleId) {
		return em.find(Role.class, roleId).getUsers();
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