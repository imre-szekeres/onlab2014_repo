/**
 * RoleManager.java
 */
package hu.bme.aut.wman.managers;

import hu.bme.aut.wman.model.Domain;
import hu.bme.aut.wman.model.Privilege;
import hu.bme.aut.wman.model.Role;
import hu.bme.aut.wman.service.DomainService;
import hu.bme.aut.wman.service.PrivilegeService;
import hu.bme.aut.wman.service.RoleService;

import java.io.Serializable;
import java.util.Map;
import java.util.Set;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityNotFoundException;

/**
 * @author Imre Szekeres
 * @version "%I%, %G%"
 */
@Stateless
@LocalBean
public class RoleManager implements Serializable {

	private static final long serialVersionUID = 2761482106409759733L;

	@Inject
	private RoleService roleService;
	@Inject
	private PrivilegeService privilegeService;
	@Inject
	private DomainService domainService;

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

	/**
	 * Assigns the given <code>Privilege</code> to the given <code>Role</code>.
	 * 
	 * @param role
	 * @param privilege
	 * @return the {@link Role} owning the given {@link Privilege}
	 * */
	public Role assign(Role role, Privilege privilege) {
		role.addPrivilege(privilege);
		roleService.save( role );
		return role;
	}

	/**
	 * Overrides the <code>Privilege</code>s owned by the given <code>Role</code> to the ones specified by their names.
	 * 
	 * @param role
	 * @param privilegeName
	 * @return the {@link Role} owning the given {@link Privilege}s only
	 * @throws {@link EntityNotFoundException}
	 * */
	public Role assign(Role role, Set<String> privilegeNames) throws EntityNotFoundException {
		Set<Privilege> privileges = privilegeService.privilegesOf( privilegeNames );
		role.setPrivileges( privileges );
		roleService.save( role );
		return role;
	}

	/**
	 * Constructs a new <code>Role</code> by assigning the <code>Privilege</code>s to it and the <code>Role</code> to the 
	 * <code>Domain</code> instance specified by its name.
	 * 
	 * @param role
	 * @param domain
	 * @param privilegeName
	 * @return the {@link Role} owning the given {@link Privilege}s only
	 * @throws {@link EntityNotFoundException}
	 * */
	public Role assignNew(Role role, String domain, Set<String> privilegeNames) throws EntityNotFoundException {
		assign(role, privilegeNames);
		Domain d = domainService.selectByName( domain );
		d.addRole( role );
		domainService.save( d );
		return role;
	}
}
