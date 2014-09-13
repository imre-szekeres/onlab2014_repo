/**
 * UserService.java
 */
package hu.bme.aut.wman.service;

import hu.bme.aut.wman.model.User;

import java.io.Serializable;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.validation.Validation;
import javax.validation.Validator;

/**
 *
 * @author Imre Szekeres
 * @version "%I%, %G%"
 */
@LocalBean
@Stateless
@SuppressWarnings("serial")
public class UserService implements Serializable {

	@PersistenceContext
	EntityManager em;

	private Validator validator;

	public UserService() {
		super();
	}

	/**
	 * Initialises the <code>Validator</code> for future use.
	 * */
	@PostConstruct
	public void init() {
		validator = Validation.buildDefaultValidatorFactory().getValidator();
	}

	/**
	 * Validates the given user name against the constraints given in the
	 * <code>User</code> class.
	 *
	 * @param username
	 *            that will be validated
	 * @return true only if the given user name corresponds to the constraints
	 *         given in the class <code>User</code>
	 * */
	public boolean validateUsername(String username) {
		return validator.validateValue(User.class, "username", username).size() == 0;
	}

	/**
	 * Validates the given password against the constraints given in the
	 * <code>User</code> class.
	 *
	 * @param password
	 *            that will be validated
	 * @return true only if the given password corresponds to the constraints
	 *         given in the class <code>User</code>
	 * */
	public boolean validatePassword(String password) {
		return validator.validateValue(User.class, "password", password).size() == 0;
	}

	/**
	 * Validates the given passwords against each other, check if they match or
	 * not.
	 *
	 * @param first
	 * @param again
	 * @return true only if the given passwords do match
	 * */
	public boolean validatePasswords(String first, String again) {
		if (first == again && again == null)
			return false;
		return again.equals(first);
	}

	/**
	 * Validates the given email against the constraints given in the
	 * <code>User</code> class.
	 *
	 * @param email
	 *            that will be validated
	 * @return true only if the given email corresponds to the constraints given
	 *         in the class <code>User</code>
	 * */
	public boolean validateEmail(String email) {
		return validator.validateValue(User.class, "email", email).size() == 0;
	}

	/**
	 * Validates the given description against the constraints given in the
	 * <code>User</code> class.
	 *
	 * @param description
	 *            that will be validated
	 * @return true only if the given description corresponds to the constraints
	 *         given in the class <code>User</code>
	 * */
	public boolean validateDescription(String description) {
		return validator.validateValue(User.class, "description", description).size() == 0;
	}

	/**
	 *
	 * @param user
	 * @throws
	 * */
	public void create(User user) throws Exception {
		em.persist(user);
	}

	/**
	 *
	 * @param user
	 * @throws
	 * @return
	 * */
	public User update(User user) {
		return em.merge(user);
	}

	/**
	 *
	 * @param user
	 * @throws
	 * */
	public void remove(User user) throws Exception {
		em.remove(user);
	}

	/**
	 * Removes permanently a detached <code>User</code> by first replacing it
	 * into the persistence context.
	 * 
	 * @param user
	 *            to be removed permanently
	 * */
	public void removeDetached(User user) {
		Object managed = em.merge(user);
		em.remove(managed);
	}

	/**
	 *
	 * @return
	 * */
	public List<User> findAll() {
		return em.createQuery("SELECT u FROM User u", User.class).getResultList();
	}

	/**
	 *
	 * @param id
	 * @return
	 * */
	public User findById(Long id) {
		return em.find(User.class, id);
	}

	/**
	 *
	 * @param username
	 * @return
	 * */
	public User findByName(String username) {
		try {
			TypedQuery<User> selectOne = em.createQuery("SELECT u FROM User u WHERE u.username = :username", User.class);
			selectOne.setParameter("username", username);
			return selectOne.getSingleResult();
		} catch (Exception e) {
			return null;
		}
	}
}