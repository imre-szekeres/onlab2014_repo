/**
 * DomainManager.java
 */
package hu.bme.aut.wman.managers;

import hu.bme.aut.wman.exceptions.EntityNotDeletableException;
import hu.bme.aut.wman.model.Domain;
import hu.bme.aut.wman.model.DomainAssignment;
import hu.bme.aut.wman.model.Role;
import hu.bme.aut.wman.model.User;
import hu.bme.aut.wman.service.DomainAssignmentService;
import hu.bme.aut.wman.service.DomainService;
import hu.bme.aut.wman.service.RoleService;
import hu.bme.aut.wman.service.UserService;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

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
public class DomainManager implements Serializable {

	private static final long serialVersionUID = 8311321169142074267L;

	@Inject
	private DomainService domainService;
	@Inject
	private DomainAssignmentService daService;
	@Inject
	private UserService userService;
	@Inject
	private RoleService roleService;

	/**
	 * Creates and initializes a new <code>Domain</code> instance by selecting the <code>Roles</code> corresponding to the 
	 * Default <code>Domain</code> instance persisted and applying the <code>Domain</code> - <code>Role</code> name convention,
	 * thus creating a name prefix for each <code>Role</code> obtained, using the name of the newly created <code>Domain</code> as 
	 * a prefix.
	 * <p>
	 * Also makes the given <code>User</code> Administrator of the new <code>Domain</code>.
	 * 
	 * @param subject on whose behalf the operation is executed
	 * @param domain
	 * @return the persisted {@link Domain} instance
	 * */
	public Domain create(User subject, Domain domain) {
		List<Role> defaults = defaultDomain().getRoles();

		Role admin = null;
		for(Role role : defaults) {
			int lastIndex = role.getName().lastIndexOf(" ");
			String roleName = role.getName().substring(lastIndex);
			Role newRole = new Role(domain.getName() + roleName);
			
			newRole.setPrivileges( role.getPrivileges() );
			roleService.save( newRole );
			domain.addRole( newRole );
			admin = ("Administrator".equals( roleName.trim() ) ? newRole : null);
		}
		domainService.save( domain );
		assignNew(subject, domain, admin);
		return domain;
	}

	/**
	 * Assigns an existing <code>User</code> to an existing <code>Domain</code> with the <code>Role</code> specified by its name.
	 * 
	 * @param user
	 * @param domain
	 * @param roleNames
	 * @return the {@link User} assigned
	 * */	
	public User assign(User user, Domain domain, Role role) {
		DomainAssignment da = daService.selectByDomainFor(user.getUsername(), domain.getName());
		if (da == null)
			return assignNew(user, domain, role);
		da.addUserRole( role );
		daService.save( da );
		userService.save( user );
		return user;
	}

	/**
	 * Overrides the previously owned <code>Role</code>s of an existing <code>User</code> in an existing <code>Domain</code> 
	 * with the <code>Role</code>s specified as a <code>List</code> as names.
	 * 
	 * @param user
	 * @param domain
	 * @param roleNames
	 * @return the {@link User} assigned
	 * @throws {@link EntityNotLFoundException}
	 * */	
	public User assign(User user, Domain domain, List<String> roleNames) throws EntityNotFoundException {
		DomainAssignment da = daService.selectByDomainFor(user.getUsername(), domain.getName());
		if (da == null)
			return assignNew(user, domain, roleNames);
		da.setUserRoles( roleService.rolesOf(domain.getName(), roleNames) );
		userService.save( user );
		daService.save( da );
		return user;
	}

	/**
	 * Assigns either a new <code>User</code> to an existing <code>Domain</code> or an existing <code>User</code> to a newly 
	 * created <code>Domain</code> with the <code>Role</code>s specified by its name.
	 * 
	 * @param user
	 * @param domain
	 * @param role
	 * @return the {@link User} assigned
	 * */
	public User assignNew(User user, Domain domain, Role role) {
		userService.save( user );
		DomainAssignment da = new DomainAssignment(user, domain, role);
		daService.save( da );
		return user;
	}

	/**
	 * Assigns either a new <code>User</code> to an existing <code>Domain</code> or an existing <code>User</code> to a newly 
	 * created <code>Domain</code> with the <code>Role</code>s specified as a <code>List</code> os names.
	 * 
	 * @param user
	 * @param domain
	 * @param roleNames
	 * @return the {@link User} assigned
	 * @throws {@link EntityNotFoundException}
	 * */
	public User assignNew(User user, Domain domain, List<String> roleNames)  throws EntityNotFoundException {
		userService.save( user );
		DomainAssignment da = new DomainAssignment(user, domain, roleService.rolesOf(domain.getName(), roleNames));
		daService.save( da );
		return user;
	}

	/**
	 * Shortcut method for assigning the given <code>User</code> to the Default <code>Domain</code> with 
	 * the Default <code>Role</code>.
	 * 
	 * @param user
	 * @return the persisted {@link User} instance 
	 * */
	public User assignToDefault(User user) {
		Domain defaultDomain = defaultDomain();
		Role defaultRole = roleService.selectByName(DomainService.DEFAULT_ROLE, DomainService.DEFAULT_DOMAIN);
		return assign(user, defaultDomain, defaultRole);
	}

	/**
	 * Removes the given <code>User</code> from the <code>Domain</code> passed as argument.
	 * 
	 * @param user
	 * @param domain
	 * @throws EntityNotDeletableException 
	 * */
	public User deassign(User user, Domain domain) throws EntityNotDeletableException {
		daService.deleteAssignmentById( domain.getId() );
		return user;
	}

	/**
	 * Removes the given <code>User</code> from the <code>Domain</code> specified by its id.
	 * 
	 * @param user
	 * @param domainID
	 * @throws EntityNotDeletableException 
	 * */
	public User deassign(User user, Long domainID) throws EntityNotDeletableException {
		daService.deleteAssignmentById( domainID );
		return user;
	}

	/**
	 * Shortcut method for selecting the default <code>Domain</code> defined by WorkflowManager.
	 * 
	 * @return the default {@link Domain} instance
	 * */
	public Domain defaultDomain() {
		return domainService.selectByName(DomainService.DEFAULT_DOMAIN);
	}

    /**
     * Removes the given <code>Domain</code> from the database.
     * 
     * @param subject
     * @param domain
     * @return the  detached {@link Domain} instance
     * @throws {@link EntityNotDeletableException}
     * */
	public Domain remove(User subject, Domain domain) throws EntityNotDeletableException {
		List<DomainAssignment> assignments = daService.selectByDomainName(domain.getName());
		for(DomainAssignment da : assignments)
			daService.delete( da );
		domainService.delete( domain );
		return domain;
	}

	/**
	 * @see {@link DomainService#validate(Domain)}
	 * */
	public Map<String, String> validate(Domain domain) {
		return domainService.validate(domain);
	}
}
