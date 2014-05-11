/**
 * RoleService.java
 */
package hu.bme.aut.tomeesample.service;

import hu.bme.aut.tomeesample.model.Role;
import hu.bme.aut.tomeesample.model.User;

import java.io.Serializable;
import java.util.List;
import java.util.Set;

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
public class RoleService implements Serializable {

	@PersistenceContext
	EntityManager em;
	private Validator validator;

	public RoleService() {
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
	 * Persists the given <code>Role</code> to the database.
	 *
	 * @param role
	 *            to be persisted
	 * @throws Exception
	 *             if the persist fails
	 * */
	public void create(Role role) throws Exception {
		em.persist(role);
	}

	/**
	 * Updates (or inserts) the given <code>Role</code> thus the database will
	 * reflect the values contained by the <code>Role</code> instance passed as
	 * argument.
	 *
	 * @param role
	 *            to be inserted / updated
	 * @return the new values inserted / updated to
	 *
	 * */
	public Role update(Role role) {
		return em.merge(role);
	}

	/**
	 * Removed the given <code>Role</code> to the database.
	 *
	 * @param role
	 *            to be removed
	 * @throws Exception
	 *             if the removal fails
	 * */
	public void remove(Role role) throws Exception {
		em.remove(role);
	}

	/**
	 * Removes permanently a detached <code>Role</code> by first replacing it
	 * into the persistence context.
	 * 
	 * @param role
	 *            to be removed permanently
	 * */
	public void removeDetached(Role role) {
		Object managed = em.merge(role);
		em.remove(managed);
	}

	/**
	 * Fetches the already created <code>Role</code>s from the persistence
	 * context.
	 *
	 * @return a list containing all the roles
	 * */
	public List<Role> findAll() {
		return em.createQuery("SELECT r FROM Role r", Role.class).getResultList();
	}

	/**
	 *
	 * @param name
	 * @return
	 * */
	public Role findByName(String name) {
		try {
			TypedQuery<Role> selectOne = em.createQuery("SELECT r FROM Role r WHERE r.name=:name", Role.class);
			selectOne.setParameter("name", name);
			return selectOne.getSingleResult();
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * 
	 * @param roleId
	 * @return a set or users owning the specified role
	 * */
	public Set<User> findUsersBy(Long roleId) {
		return em.find(Role.class, roleId).getUsers();
	}

	/**
	 * @return the role names available
	 * */
	public String[] findRoleNames() {
		return em.createQuery("SELECT r.name FROM Role r", String[].class).getResultList().toArray(new String[0]);
	}

	/**
	 * Validates the given name against the constraints given in the
	 * <code>Role</code> class.
	 *
	 * @param name
	 *            that will be validated
	 * @return true only if the given name corresponds to the constraints given
	 *         in the class <code>Role</code>
	 * */
	public boolean validateName(String name) {
		return validator.validateValue(Role.class, "name", name).size() == 0;
	}
}
