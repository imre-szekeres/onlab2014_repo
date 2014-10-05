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

import java.util.Map;

import javax.ejb.EJB;
import javax.ejb.Stateless;

/**
 * @author Imre Szekeres
 * @version "%I%, %G%"
 */
@Stateless
public class UserHandlerImpl implements UserHandlerLocal {
	
	@EJB(mappedName="java:module/UserService")
	private UserService userService;
	
	@EJB(mappedName="java:module/RoleService")
	private RoleService roleService;
	
	@EJB(mappedName="java:module/DomainService")
	private DomainService domainService;
	
	@EJB(mappedName="java:module/DomainAssignmentService")
	private DomainAssignmentService domainAssignmentService;
	
	

	@Override
	public User createUser(User user, String initialRole) {
		Role initial = roleService.selectByName(initialRole);
		return assignUser(user, initial);
	}

	@Override
	public User removeUser(long userID) throws Exception {
		User user = userService.selectById(userID);
		
		for(DomainAssignment da : user.getDomainAssignments()) {
			deassignUser(user, da);
		}
		
		userService.delete(user);
		return user;
	}

	@Override
	public boolean addRole(long userID, String role) {
		Role r = roleService.selectByName(role);
		DomainAssignment da = domainAssignmentService.selectByDomainFor(userID, r.getDomain().getName());
		
		boolean isSucceeded = da.addUserRole(r);
		domainAssignmentService.save(da);
		roleService.save(r);
		return isSucceeded;
	}

	@Override
	public boolean removeRole(long userID, String role) {
		Role r = roleService.selectByName(role);
		DomainAssignment da = domainAssignmentService.selectByDomainFor(userID, r.getDomain().getName());
		
		boolean isSucceeded = da.removeUserRole(r);
		domainAssignmentService.save(da);
		roleService.save(r);
		return isSucceeded;
	}

	@Override
	public User assignUser(long userID, String role) {
		User user = userService.selectById(userID);
		Role r = roleService.selectByName(role);
		return assignUser(user, r);
	}
	
	private User assignUser(User user, Role role) {
		Domain domain = role.getDomain();
		DomainAssignment assignment = new DomainAssignment(user, domain, role);
		
		user.addDomainAssignment(assignment);
		domain.addDomainAssignment(assignment);
		role.addDomainAssignment(assignment);
		
		userService.save(user);
		domainService.save(domain);
		roleService.save(role);
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
		
		user.removeDomainAssignment(da);
		d.removeDomainAssignment(da);
		
		for(Role r : da.getUserRoles()) {
			r.removeDomainAssignmnet(da);
			roleService.save(r);
		}
		
		userService.save(user);
		domainService.save(d);
		domainAssignmentService.delete(da);
		return user;
	}

	@Override
	public Map<String, String> validate(User user, String otherPassword,
			boolean isRegistered) {
		return userService.validate(user, otherPassword, isRegistered);
	}

}
