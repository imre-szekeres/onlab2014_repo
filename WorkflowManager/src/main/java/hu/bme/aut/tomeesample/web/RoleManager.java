/**
 * RoleManager.java
 */
package hu.bme.aut.tomeesample.web;

import hu.bme.aut.tomeesample.model.Role;
import hu.bme.aut.tomeesample.service.RoleService;

import java.util.List;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;

/**
 *
 * @author Imre Szekeres
 * @version "%I%, %G%"
 */
@Named
@RequestScoped
public class RoleManager {

	@Inject
	private RoleService roleService;

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
}
