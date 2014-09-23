package hu.bme.aut.wman.service;

import hu.bme.aut.wman.model.ActionType;
import hu.bme.aut.wman.model.Role;

import java.io.Serializable;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map.Entry;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;

import com.google.common.base.Function;
import com.google.common.collect.Collections2;

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

	/**
	 * @param name
	 * @return role which has the given name
	 */
	public Role findByName(String name) {
		ArrayList<Entry<String, Object>> parameterList = new ArrayList<Entry<String, Object>>();
		parameterList.add(new AbstractMap.SimpleEntry<String, Object>(Role.PR_NAME, name));
		return selectByParameters(parameterList).get(0);
	}

	/**
	 * @param actionType
	 * @return roles who can execute the given actionType
	 */
	public List<Role> findByActionType(ActionType actionType) {
		ArrayList<Entry<String, Object>> parameterList = new ArrayList<Entry<String, Object>>();
		parameterList.add(new AbstractMap.SimpleEntry<String, Object>("actionType", actionType));
		return callNamedQuery(Role.NQ_FIND_BY_ACTIONTYPE, parameterList);
	}

	/**
	 * @return all role's names
	 */
	public Collection<String> findRoleNames() {
		return Collections2.transform(selectAll(), new Function<Role, String>() {

			@Override
			public String apply(Role role) {
				return role.getName();
			}
		});
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
