package hu.bme.aut.wman.service;

import hu.bme.aut.wman.model.ActionType;
import hu.bme.aut.wman.model.Domain;
import hu.bme.aut.wman.model.Role;
import hu.bme.aut.wman.service.validation.RoleValidator;
import hu.bme.aut.wman.service.validation.ValidationEngine;

import java.io.Serializable;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.annotation.PostConstruct;
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

	private ValidationEngine<Role> validator;

	@PostConstruct
	public void setup() {
		validator = new RoleValidator();
	}
	
	public Map<String, String> validate(Role role, String domain) {
		Map<String, String> errors = validator.validate(role);
		unicityOf(role, domain, errors);
		if (DomainService.DEFAULT_DOMAIN.equals( domain ))
			errors.put("domain", "Domain " + DomainService.DEFAULT_DOMAIN + " cannot be modified!");
		return errors;
	}

	/**
	 * Ensures the unicity of a <code>Role</code>'s name in a given <code>Domain</code> thus
	 * the unicity of that given <code>Role</code>. If that constraint is violated, then places a 
	 * <code>ConstraintViolation</code> into the given <code>Map</code> with the key value of "name".
	 * 
	 * @param role
	 * @param domain
	 * @param errors
	 * */
	private void unicityOf(Role role, String domain, Map<String, String> errors) {
		Role old = selectByName(role.getName(), domain);
		if ((old != null) && (!old.getId().equals( role.getId() )))
			errors.put("name", "Role already exists in the given domain!");
	}

	public Role selectByName(String name) {
		List<Entry<String, Object>> parameterList = new ArrayList<Entry<String, Object>>();
		parameterList.add(new AbstractMap.SimpleEntry<String, Object>(Role.PR_NAME, name));
		List<Role> results = selectByParameters(parameterList);
		return (results.size() > 0) ? results.get(0) : null;
	}

	public Role selectByName(String name, String domain) {
		List<Entry<String, Object>> parameterList = new ArrayList<Entry<String, Object>>();
		parameterList.add(new AbstractMap.SimpleEntry<String, Object>("domainName", domain));
		parameterList.add(new AbstractMap.SimpleEntry<String, Object>("roleName", name));
		List<Role> roles = callNamedQuery(Role.NQ_FIND_BY_DOMAIN_AND_NAME, parameterList);
		return roles.size() > 0 ? roles.get(0) : null;
	}

	public List<Role> selectByDomain(String domainName) {
		List<Entry<String, Object>> parameterList = new ArrayList<Entry<String, Object>>();
		parameterList.add(new AbstractMap.SimpleEntry<String, Object>("domainName", domainName));
		return callNamedQuery(Role.NQ_FIND_BY_DOMAIN, parameterList);
	}

	/**
	 * Retrieves the <code>Role</code> names corresponding to the given <code>Domain</code> name.
	 * 
	 * @param domainName
	 * @return a list of {@link Role} name corresponding to that {@link Domain} name 
	 * */
	public List<String> selectNamesByDomain(String domainName) {
		List<Entry<String, Object>> parameterList = new ArrayList<Entry<String, Object>>();
		parameterList.add(new AbstractMap.SimpleEntry<String, Object>("domainName", domainName));
		return callNamedQuery(Role.NQ_FIND_NAMES_BY_DOMAIN, parameterList, String.class);
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
	 * Determines whether the <code>User</code> specified by its name has all the required <code>Privilege</code>s accounted
	 * as permissions in the <code>Domain</code> that the <code>Role</code> specified by its id corresponds to.
	 * 
	 * @param username
	 * @param roleID
	 * @param privilegeNames
	 * @return whether the given {@link User} has permissions to execute operations on the given {@link Role}
	 * */
	public boolean hasPrivileges(String username, Long roleID, Collection<? extends String> privilegeNames) {
		List<Entry<String, Object>> parameters = new ArrayList<Entry<String, Object>>();
		parameters.add(new AbstractMap.SimpleEntry<String, Object>("username", username));
		parameters.add(new AbstractMap.SimpleEntry<String, Object>("count", privilegeNames.size()));
		parameters.add(new AbstractMap.SimpleEntry<String, Object>("privilegeNames", privilegeNames));
		parameters.add(new AbstractMap.SimpleEntry<String, Object>("roleID", roleID));
		List<? extends Number> count = callNamedQuery(Role.NQ_FIND_COUNT_BY_PRIVILEGES, parameters, Integer.class);
		return count.size() > 0 ? (count.get(0).intValue() > 0) : false;
	}

	/**
	 * Determines whether the <code>User</code> specified by its name owns the required <code>Privilege</code> accounted
	 * as permission in the <code>Domain</code> that the <code>Role</code> specified by its id corresponds to.
	 * 
	 * @param username
	 * @param roleID
	 * @param privilegeName
	 * @return whether the given {@link User} has permissions to execute operations on the given {@link Role}
	 * */
	public boolean hasPrivilege(String username, Long roleID, String privilegeName) {
		List<Entry<String, Object>> parameters = new ArrayList<Entry<String, Object>>();
		parameters.add(new AbstractMap.SimpleEntry<String, Object>("username", username));
		parameters.add(new AbstractMap.SimpleEntry<String, Object>("privilegeName", privilegeName));
		parameters.add(new AbstractMap.SimpleEntry<String, Object>("roleID", roleID));
		List<? extends Number> count = callNamedQuery(Role.NQ_FIND_COUNT_BY_PRIVILEGE, parameters, Integer.class);
		return count.size() > 0 ? (count.get(0).intValue() > 0) : false;
	}

	/**
	 * Determines whether the <code>User</code> specified by its name owns the required <code>Privilege</code> accounted
	 * as permission in the <code>Domain</code> that the <code>Role</code> specified by its name corresponds to.
	 * 
	 * @param username
	 * @param roleName
	 * @param privilegeName
	 * @return whether the given {@link User} has permissions to execute operations on the given {@link Role}
	 * */
	public boolean hasPrivilege(String username, String roleName, String privilegeName) {
		List<Entry<String, Object>> parameters = new ArrayList<Entry<String, Object>>();
		parameters.add(new AbstractMap.SimpleEntry<String, Object>("username", username));
		parameters.add(new AbstractMap.SimpleEntry<String, Object>("privilegeName", privilegeName));
		parameters.add(new AbstractMap.SimpleEntry<String, Object>("roleName", roleName));
		List<? extends Number> count = callNamedQuery(Role.NQ_FIND_COUNT_BY_PRIVILEGE_AND_NAME, parameters, Integer.class);
		return count.size() > 0 ? (count.get(0).intValue() > 0) : false;
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
