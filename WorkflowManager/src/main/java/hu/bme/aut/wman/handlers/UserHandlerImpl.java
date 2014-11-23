/**
 * UserHandlerImpl.java
 */
package hu.bme.aut.wman.handlers;

import hu.bme.aut.wman.exceptions.EntityNotDeletableException;
import hu.bme.aut.wman.model.Domain;
import hu.bme.aut.wman.model.DomainAssignment;
import hu.bme.aut.wman.model.Role;
import hu.bme.aut.wman.model.User;
import hu.bme.aut.wman.service.DomainAssignmentService;
import hu.bme.aut.wman.service.DomainService;
import hu.bme.aut.wman.service.RoleService;
import hu.bme.aut.wman.service.UserService;

import javax.ejb.Stateless;
import javax.inject.Inject;

/**
 * @author Imre Szekeres
 * @version "%I%, %G%"
 */
@Stateless
public class UserHandlerImpl implements UserHandlerLocal {
	
	@Inject
	private UserService userService;
	
	@Inject
	private RoleService roleService;
	
	@Inject
	private DomainService domainService;
	
	@Inject
	private DomainAssignmentService domainAssignmentService;
	
	

	@Override
	public User createUser(User user, String initialRole, String domain) {
		Role initial = roleService.selectByName(initialRole);
		return assignUser(user, initial, domain);
	}

	@Override
	public User removeUser(long userID) throws Exception {
		User user = userService.selectById(userID);
		
		for(DomainAssignment da : domainAssignmentService.selectByUserID(userID)) {
			domainAssignmentService.delete( da );
		}
		
		userService.delete(user);
		return user;
	}

	@Override
	public boolean addRole(long userID, String role, String domain) {
		Role r = roleService.selectByName(role);
		DomainAssignment da = domainAssignmentService.selectByDomainFor(userID, domain);
		
		boolean isSucceeded = da.addUserRole(r);
		domainAssignmentService.save(da);
		return isSucceeded;
	}

	@Override
	public boolean removeRole(long userID, String role, String domain) {
		Role r = roleService.selectByName(role);
		DomainAssignment da = domainAssignmentService.selectByDomainFor(userID, domain);
		
		boolean isSucceeded = da.removeUserRole(r);
		domainAssignmentService.save(da);
		return isSucceeded;
	}

	@Override
	public User assignUser(long userID, String role, String domain) {
		User user = userService.selectById(userID);
		Role r = roleService.selectByName(role);
		return assignUser(user, r, domain);
	}
	
	private User assignUser(User user, Role role, String domain) {
		Domain d = domainService.selectByName(domain);
		DomainAssignment assignment = new DomainAssignment(user, d, role);		
		
		userService.save(user);
		domainService.save(d);
		domainAssignmentService.save(assignment);
		return user;
	}

	@Override
	public User deassignUser(long userID, String domain) throws EntityNotDeletableException {
		User user = userService.selectById(userID);
		DomainAssignment da = domainAssignmentService.selectByDomainFor(userID, domain);
		return deassignUser(user, da);
	}
	
	private User deassignUser(User user, DomainAssignment da) throws EntityNotDeletableException {
		Domain d = da.getDomain();
		
		userService.save(user);
		domainService.save(d);
		domainAssignmentService.delete(da);
		return user;
	}
}
