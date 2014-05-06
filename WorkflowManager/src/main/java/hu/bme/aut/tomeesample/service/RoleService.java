/**
 * RoleService.java
 */
package hu.bme.aut.tomeesample.service;

import hu.bme.aut.tomeesample.model.Role;

import java.io.Serializable;
import java.util.List;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

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

	public RoleService() {
		super();
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
	 * @return
	 * */
	public String[] findRoleNames() {
		return em.createQuery("SELECT r.name FROM Role r", String[].class).getResultList().toArray(new String[0]);
	}
}
