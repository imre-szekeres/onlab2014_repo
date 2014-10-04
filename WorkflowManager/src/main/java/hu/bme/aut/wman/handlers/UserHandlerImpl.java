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
	public User createUser(User user, String initialRole, String domain) {
		Role initial = roleService.selectByName(initialRole);
		return assignUser(user, domain, initial);
	}

	@Override
	public User removeUser(long userID) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public User addRole(long userID, String role) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public User removeRole(long userID, String role) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public User assignUser(long userID, String domain, String role) {
		User user = userService.selectById(userID);
		Role r = roleService.selectByName(role);
		return assignUser(user, domain, r);
	}
	
	private User assignUser(User user, String domain, Role role) {
		Domain d = domainService.selectByName(domain);
		DomainAssignment assignment = new DomainAssignment(user, d, role);
		user.addDomainAssignment(assignment);
		d.addDomainAssignment(assignment);
		
		userService.save(user);
		domainService.save(d);
		domainAssignmentService.save(assignment);
		return null;
	}

	@Override
	public User deassignUser(long userID, String domain) throws EntityNotDeletableException {
		User user = userService.selectById(userID);
		DomainAssignment da = domainAssignmentService.selectByDomainName(domain);
		Domain d = da.getDomain();
		
		user.removeDomainAssignment(da);
		d.removeDomainAssignment(da);
		
		userService.save(user);
		domainService.save(d);
		domainAssignmentService.delete(da);
		return null;
	}

	@Override
	public Map<String, String> validate(User user, String otherPassword,
			boolean isRegistered) {
		// TODO Auto-generated method stub
		return null;
	}

}
