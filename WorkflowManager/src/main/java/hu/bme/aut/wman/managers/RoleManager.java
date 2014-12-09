/**
 * RoleManager.java
 */
package hu.bme.aut.wman.managers;

import hu.bme.aut.wman.model.Role;
import hu.bme.aut.wman.service.PrivilegeService;
import hu.bme.aut.wman.service.RoleService;

import java.io.Serializable;
import java.util.Map;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.log4j.Logger;

/**
 * @author Imre Szekeres
 * @version "%I%, %G%"
 */
@Stateless
@LocalBean
public class RoleManager implements Serializable {

	private static final Logger LOGGER = Logger.getLogger( RoleManager.class );
	private static final long serialVersionUID = 2761482106409759733L;

	@Inject
	private RoleService roleService;
	@Inject
	private PrivilegeService privilegeService;

	/**
	 * Validates the given <code>Role</code> against the constraints defined on it.
	 * 
	 * @param role
	 * @param domain
	 * @return a {@link Map} containing the errors
	 * */
	public Map<String, String> validate(Role role, String domain) {
		return roleService.validate(role, domain);
	}
}
