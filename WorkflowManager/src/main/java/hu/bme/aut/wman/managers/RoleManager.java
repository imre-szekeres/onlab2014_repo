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


	/**
	 * Only for testing purposes.
	 * 
	 * @param domainService
	 * */
	public void set(DomainService domainService) {
		this.domainService = domainService;
	}

	/**
	 * Only for testing purposes.
	 * 
	 * @param privilegeService
	 * */
	public void set(PrivilegeService privilegeService) {
		this.privilegeService = privilegeService;
	}

	/**
	 * Only for testing purposes.
	 * 
	 * @param roleService
	 * */
	public void set(RoleService roleService) {
		this.roleService = roleService;
	}


	/**
	 * Only for testing purposes.
	 * 
	 * @return domainService
	 * */
	public DomainService getDomainService() {
		return this.domainService;
	}

	/**
	 * Only for testing purposes.
	 * 
	 * @return privilegeService
	 * */
	public PrivilegeService getPrivilegeService() {
		return this.privilegeService;
	}

	/**
	 * Only for testing purposes.
	 * 
	 * @return roleService
	 * */
	public RoleService getRoleService() {
		return this.roleService;
	}

	public static class TestWrapper {
		
		private RoleManager manager;


		public TestWrapper(RoleManager manager) {
			this.manager = manager;
		}


		public RoleService getRoleService() {
			return manager.roleService;
		}

		public DomainService getDomainService() {
			return manager.domainService;
		}

		public PrivilegeService getPrivilegeService() {
			return manager.privilegeService;
		}

		public void setRoleService(RoleService roleService) {
			this.manager.roleService = roleService;
		}

		public void setDomainService(DomainService domainService) {
			this.manager.domainService = domainService;
		}

		public void setPrivilegeService(PrivilegeService privilegeService) {
			this.manager.privilegeService = privilegeService;
		}

		public RoleManager manager() {
			return manager;
		}

		public void manager(RoleManager manager) {
			this.manager = manager;
		}
	}
}
