/**
 * RoleManager.java
 */
package hu.bme.aut.tomeesample.web;

import hu.bme.aut.tomeesample.model.ActionType;
import hu.bme.aut.tomeesample.model.Role;
import hu.bme.aut.tomeesample.model.User;
import hu.bme.aut.tomeesample.service.RoleService;
import hu.bme.aut.tomeesample.service.UserService;
import hu.bme.aut.tomeesample.utils.FacesMessageUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Conversation;
import javax.enterprise.context.ConversationScoped;
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
@ConversationScoped
@SuppressWarnings("serial")
public class RoleManager implements Serializable {

	private static final Logger logger = Logger.getLogger(RoleManager.class);

	@Inject
	private Conversation conversation;
	@Inject
	private RoleService roleService;
	@Inject
	private UserService userService;
	private Role role;

	@PostConstruct
	public void init() {
		this.role = new Role();
		if (conversation.isTransient())
			conversation.begin();
	}

	/**
	 * Navigates to the profile page of the specified <code>Role</code>.
	 * 
	 * @param role
	 * @return the page id of the profile page
	 * */
	public String profileOf(Role role) {
		this.role = role;
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
			roleService.create(this.role);
			String message = "created new role: " + this.role.toString();
			FacesMessageUtils.infoMessage(context, message);
			logger.debug(message);
		} catch (Exception e) {
			FacesMessageUtils.errorMessage(context, "failed to create " + this.role);
			logger.error("in RoleManager.create: ", e);
		}
		conversation.end();
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
	 * @return a list of <code>User</code>s assigned to the given
	 *         <code>Role</code>
	 * */
	public List<User> listUsers() {
		return (this.role == null || this.role.getId() == null) ?
				new ArrayList<User>() :
				new ArrayList<User>(roleService.findUsersBy(this.role.getId()));
	}

	/**
	 * 
	 * 
	 * @return a list of <code>ActionType</code>s assigned to the given
	 *         <code>Role</code>
	 * */
	public List<ActionType> listActions() {
		return (this.role == null || this.role.getId() == null) ?
				new ArrayList<ActionType>() :
				new ArrayList<ActionType>(this.role.getActionTypes());
	}

	/**
	 * Adds a new role to the user specified by user name.
	 *
	 * @return the string representation of the page to navigate to
	 * */
	// TODO: remove
	public String addFor(User user) {
		FacesContext context = FacesContext.getCurrentInstance();
		try {
			user.add(this.role);
			user = userService.update(user);

			String message = "role " + this.role.toString() + " was added to " + user.toString();
			FacesMessageUtils.infoMessage(context, message);
			logger.debug(message);

			conversation.end();
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
	public String removeFrom(User user) {
		FacesContext context = FacesContext.getCurrentInstance();
		try {
			user.remove(this.role);
			user = userService.update(user);

			String message = "role " + this.role.toString() + " was removed from " + user.toString();
			logger.debug(message);

			conversation.end();
		} catch (Exception e) {
			FacesMessageUtils.errorMessage(context, "failed to remove " + this.role.toString());
			logger.error("ERROR in removeFrom: ", e);
		}
		return "add_role";
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

			String message = "role " + role.toString() + " was removed";
			FacesMessageUtils.infoMessage(context, message);
			logger.debug(message);
			conversation.end();
		} catch (Exception e) {
			FacesMessageUtils.errorMessage(context, "failed to remove " + role.toString());
			logger.error("ERROR in delete: ", e);
		}
		return "add_role";
	}

	/**
	 * @return the role
	 */
	public Role getRole() {
		return role;
	}

	/**
	 * @param role
	 */
	public void setRole(Role role) {
		this.role = role;
	}
}
