/**
 * RoleManager.java
 */
package hu.bme.aut.tomeesample.web;

import hu.bme.aut.tomeesample.model.Role;
import hu.bme.aut.tomeesample.service.RoleService;

import java.util.List;

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
	private Role newRole = new Role();

	public RoleManager() {
		super();
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
		try {
			roleService.create(newRole);
			logger.debug("created new role: " + newRole.toString());
		} catch (Exception e) {
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
