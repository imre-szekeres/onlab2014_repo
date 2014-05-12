/**
 * RoleManager.java
 */
package hu.bme.aut.tomeesample.web;

import hu.bme.aut.tomeesample.model.Role;
import hu.bme.aut.tomeesample.model.User;
import hu.bme.aut.tomeesample.service.RoleService;
import hu.bme.aut.tomeesample.service.UserService;
import hu.bme.aut.tomeesample.utils.FacesMessageUtils;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.ValidatorException;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.log4j.Logger;

/**
 *
 * @author Imre Szekeres
 * @version "%I%, %G%"
 */
@Named
@RequestScoped
public class RoleManager {

	private static final Logger logger = Logger.getLogger(RoleManager.class);

	@Inject
	private RoleService roleService;
	@Inject
	private UserService userService;
	private Role newRole;

	@PostConstruct
	public void init() {
		newRole = new Role();
	}

	/**
	 * Navigates to the profile page of the specified <code>Role</code>.
	 * 
	 * @param role
	 * @return the page id of the profile page
	 * */
	public String profileOf(Role role) {
		newRole = role;
		return "role_profile";
	}

	/**
	 * Fetches the already created <code>Role</code>s from the persistence
	 * context.
	 *
	 * @return a list containing all the roles
	 * */
	public List<Role> listRoles() {
		return this.roleService.findAll();
	}

	/**
	 * Creates a new <code>Role</code> with the previously specified name, and
	 * persists it.
	 *
	 * @return the view id to navigate to.
	 * */
	public String create() {
		FacesContext context = FacesContext.getCurrentInstance();
		try {
			roleService.create(newRole);
			String message = "created new role: " + newRole.toString();
			FacesMessageUtils.infoMessage(context, message);
			logger.debug(message);
		} catch (Exception e) {
			FacesMessageUtils.errorMessage(context, "failed to create " + newRole);
			logger.error("in RoleManager.create: ", e);
		}
		return "add_role";
	}

	/**
	 * Delegates the call to a <code>RoleService</code> instance to ensure
	 * whether the given name is not already defined.
	 *
	 * @param context
	 *            representing the current JSF context
	 * @param component
	 *            the <code>UIComponent</code> from which the given value came
	 *            from
	 * @param value
	 *            representing a role name
	 *
	 * @throws ValidatorException
	 * */
	public void validateName(FacesContext context, UIComponent component, Object value)
			throws ValidatorException {
		if (!roleService.validateName(((String) value).trim()))
			throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_ERROR, "role name already exists", "role name already exists"));
	}

	/**
	 * 
	 * 
	 * @return a list of <code>User</code> assigned to the given
	 *         <code>Role</code>
	 * */
	public List<User> listUsers() {
		return new ArrayList<User>(roleService.findUsersBy(newRole.getId()));
		// new ArrayList<User>(roleService.findUsersBy(role.getId()));
	}

	/**
	 * Adds a new role to the user specified by user name.
	 *
	 * @return the string representation of the page to navigate to
	 * */
	public String addFor(User user, Role role) {
		FacesContext context = FacesContext.getCurrentInstance();
		try {
			// user.add(role);
			role.add(user);

			// userService.update(user);
			roleService.update(role);

			String message = "role " + role + " was added to " + user;
			FacesMessageUtils.infoMessage(context, message);
			logger.debug(message);
			return "users";
		} catch (Exception e) {
			FacesMessageUtils.infoMessage(context, "failed to add " + role);
			logger.error("in addRoleFor: ", e);
			return "add_role";
		}
	}

	/**
	 * Removes the specified <code>Role</code> from the application.
	 * 
	 * @param role
	 *            to be removed permanently
	 * */
	public String removeFrom(User user, String roleName) {
		FacesContext context = FacesContext.getCurrentInstance();
		try {
			System.out.println("user: " + user);
			System.out.println("role: " + roleName);

			logPropsOf(user);

			newRole = roleService.findByName(roleName);
			// user.remove(role);
			newRole.remove(user);
			// userService.update(user);
			roleService.update(newRole);

			String message = "role " + newRole + " was removed from " + user;
			FacesMessageUtils.infoMessage(context, message);
			logger.debug(message);
		} catch (Exception e) {
			FacesMessageUtils.errorMessage(context, "failed to remove " + newRole);
			logger.error("ERROR in removeFrom: ", e);
		}
		return "add_role";
	}

	// TODO: delete
	private void logPropsOf(User user) {
		try {
			logger.debug("\nloginManager: " + this.toString());
			logger.debug("user: " + user.toString());
			logger.debug("props:"
					+ "\nUsername: " + user.getUsername()
					+ "\nPassword: " + user.getPassword()
					+ "\nEmail: " + user.getEmail()
					+ "\nDescription: " + user.getDescription()
					+ "\nRoles: " + user.getRoles()
					+ "\nComments: " + user.getComments()
					+ "\nProjectAssignments: " + user.getProjectAssignments() + "\n");
		} catch (Exception e) {
			logger.error("ERROR in logProps ~ " + e.getClass() + ": " + e.getMessage());
			logger.error(e, e);
		}

	}

	/**
	 * Removes the specified <code>Role</code> from the application.
	 * 
	 * @param role
	 *            to be removed permanently
	 * */
	public String delete(Role role) {
		FacesContext context = FacesContext.getCurrentInstance();
		try {
			roleService.removeDetached(role);

			String message = "role " + role + " was removed";
			FacesMessageUtils.infoMessage(context, message);
			logger.debug(message);
		} catch (Exception e) {
			FacesMessageUtils.errorMessage(context, "failed to remove " + role);
			logger.error("ERROR in delete: ", e);
		}
		return "add_role";
	}

	/**
	 * @return the newRole
	 */
	public Role getNewRole() {
		return newRole;
	}

	/**
	 * @param newRole
	 */
	public void setNewRole(Role role) {
		this.newRole = role;
	}
}
