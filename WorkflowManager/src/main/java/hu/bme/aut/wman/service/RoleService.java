package hu.bme.aut.wman.service;

import hu.bme.aut.wman.exceptions.EntityNotFoundException;
import hu.bme.aut.wman.exceptions.TooMuchElementException;
import hu.bme.aut.wman.model.ActionType;
import hu.bme.aut.wman.model.Role;

import java.io.Serializable;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.TypedQuery;

/**
 * Helps make operations with <code>Role</code>.
 * 
 * @version "%I%, %G%"
 */
@LocalBean
@Stateless
@SuppressWarnings("serial")
public class RoleService extends AbstractDataService<Role> implements Serializable {

	// private Validator validator;

	// @PostConstruct
	// private void init() {
	// validator = Validation.buildDefaultValidatorFactory().getValidator();
	// }

	public Role findByName(String name) throws TooMuchElementException, EntityNotFoundException {
		ArrayList<Entry<String, Object>> parameterList = new ArrayList<Entry<String, Object>>();
		parameterList.add(new AbstractMap.SimpleEntry<String, Object>(Role.PR_NAME, name));

		return checkHasExactlyOneElement(selectByOwnProperties(parameterList)).get(0);
	}

	/**
	 * Use findByParameters method instead
	 */
	@Deprecated
	public List<Role> findByActionType(ActionType actionType) {
		try {
			TypedQuery<Role> selectOne = em.createQuery("SELECT r FROM Role r WHERE :actionType MEMBER OF r.actionTypes", Role.class);
			selectOne.setParameter("actionType", actionType);
			return selectOne.getResultList();
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * Use findByParameters method instead
	 */
	@Deprecated
	public String[] findRoleNames() {
		return em.createQuery("SELECT r.name FROM Role r", String[].class).getResultList().toArray(new String[0]);
	}

	@Override
	protected Class<Role> getEntityClass() {
		return Role.class;
	}

	// /**
	// * Validates the given name against the constraints given in the <code>Role</code> class.
	// *
	// * @param name
	// * that will be validated
	// * @return true only if the given name corresponds to the constraints given
	// * in the class <code>Role</code>
	// * */
	// public boolean validateName(String name) {
	// return validator.validateValue(Role.class, "name", name).size() == 0;
	// }
}
