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

	public Role selectByName(String name) {
		List<Entry<String, Object>> parameterList = new ArrayList<Entry<String, Object>>();
		parameterList.add(new AbstractMap.SimpleEntry<String, Object>(Role.PR_NAME, name));
		// FIXME should check if has exactly one element
		List<Role> results = selectByParameters(parameterList);
		return (results.size() > 0) ? results.get(0) : null;
	}

	/**
	 * @param actionType
	 * @return roles who can execute the given actionType
	 */
	public List<Role> selectByActionType(ActionType actionType) {
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
}
