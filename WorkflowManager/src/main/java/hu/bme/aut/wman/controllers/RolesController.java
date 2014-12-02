/**
 * RolesController.java
 */
package hu.bme.aut.wman.controllers;

import static hu.bme.aut.wman.controllers.LoginController.userIDOf;
import hu.bme.aut.wman.model.Domain;
import hu.bme.aut.wman.model.Privilege;
import hu.bme.aut.wman.model.Role;
import hu.bme.aut.wman.model.User;
import hu.bme.aut.wman.service.DomainService;
import hu.bme.aut.wman.service.PrivilegeService;
import hu.bme.aut.wman.service.RoleService;
import hu.bme.aut.wman.service.UserService;
import hu.bme.aut.wman.view.DroppableName;
import hu.bme.aut.wman.view.Messages.Severity;
import hu.bme.aut.wman.view.objects.transfer.RoleTransferObject;

import java.util.HashSet;
import java.util.List;
import java.util.Map;

import javax.ejb.EJB;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @author Imre Szekeres
 * @version "%I%, %G%"
 */
@Controller
public class RolesController extends AbstractController {

	private static final Logger LOGGER = Logger.getLogger( RolesController.class );
	
	public static final String ROOT_URL = "/roles";
	public static final String CREATE = ROOT_URL + "/create";
	public static final String CREATE_FORM = CREATE + "/form";
	
	public static final String UPDATE = ROOT_URL + "/update";
	public static final String UPDATE_FORM = UPDATE + "/form";
	public static final String DELETE = ROOT_URL + "/delete";
	

	@EJB(mappedName = "java:module/RoleService")
	private RoleService roleService;
	@EJB(mappedName = "java:module/PrivilegeService")
	private PrivilegeService privilegeService;
	@EJB(mappedName = "java:module/DomainService")
	private DomainService domainService;
	@EJB(mappedName = "java:module/UserService")
	private UserService userService;
	
	
	@SuppressWarnings("deprecation")
	@RequestMapping(value = CREATE, method = RequestMethod.POST)
	public String createRole(@ModelAttribute("role") RoleTransferObject newRole, Model model, HttpSession session) {
		String roleName = newRole.getRoleName();
		String domainName  = newRole.getDomainName();
		
		Role role = new Role(roleName);
		Map<String, String> errors = roleService.validate(role, domainName, true);
		
		if (errors.isEmpty()) {
			User subject = userService.selectById( userIDOf(session) );
			List<String> privileges = newRole.privileges();
			
			LOGGER.debug("found \'" + newRole.getPrivileges() + "\' for role " + roleName);
			LOGGER.debug("parsed (" + privileges.size() + ") privileges for role " + roleName);
			
			for(String s : privileges) {
				Privilege p = privilegeService.selectByName(s);
				assign(role, p);
			}
			roleService.save(role);
			LOGGER.info(role.toString() + " was created by " + subject.getUsername());
			
			Domain d = domainService.selectByName(domainName);
			d.addRole(role);
			domainService.save( d );
			String message = role.toString() + " was added to " + d.toString();
			LOGGER.info(message);
			flash(message, Severity.INFO, model);
			return redirectTo(AdminViewController.ROLES);
		}

		model.addAttribute(AbstractController.ERRORS_MAP, errors);
		AdminViewController.setAdminRolesContent(model, domainService);
		model.addAttribute("pageName", "admin_roles");
		reset(newRole, model);
		return AbstractController.FRAME;
	}
	
	@SuppressWarnings("deprecation")
	@RequestMapping(value = UPDATE, method = RequestMethod.POST)
	public String updateRole(@ModelAttribute("role") RoleTransferObject newRole, Model model, HttpSession session) {
		String roleName = newRole.getRoleName();
		String domainName  = newRole.getDomainName();
		
		Role role = roleService.selectById(newRole.getId());
		role.setName( newRole.getRoleName() );
		Map<String, String> errors = roleService.validate(role, domainName, false);
		
		if (errors.isEmpty()) {
			User subject = userService.selectById( userIDOf(session) );
			List<String> privileges = newRole.privileges();
			
			LOGGER.debug("found \'" + newRole.getPrivileges() + "\' for role " + roleName);
			LOGGER.debug("parsed (" + privileges.size() + ") privileges for role " + roleName);
			
			role.setPrivileges(new HashSet<Privilege>());
			for(String s : privileges) {
				Privilege p = privilegeService.selectByName(s);
				assign(role, p);
			}
			roleService.save(role);
			String message = role.toString() + " was updated";
			LOGGER.info(message + " by " + subject.getUsername());
			flash(message, Severity.INFO, model);
			return redirectTo(AdminViewController.ROLES);
		}

		model.addAttribute(AbstractController.ERRORS_MAP, errors);
		AdminViewController.setAdminRolesContent(model, domainService);
		model.addAttribute("pageName", "admin_roles");
		model.addAttribute("postRoleAction", RolesController.UPDATE);
		model.addAttribute("formType", "update");
		return AbstractController.FRAME;
	}

	/**
	 * Assigns the given <code>Privilege</code> to the given <code>Role</code> instance and logs the operation.
	 * 
	 * @param role
	 * @param privilege
	 * */
	private void assign(Role role, Privilege privilege) {
		if (privilege != null) {
			LOGGER.debug(privilege.toString() + " was found");
			role.addPrivilege( privilege );
			LOGGER.debug(privilege.toString() + " was addoed to " + role.toString());
		}
	}
	
	@RequestMapping(value = CREATE_FORM, method = RequestMethod.GET)
	public String requestCreateForm(Model model) {
		model.addAttribute("role", new RoleTransferObject());
		model.addAttribute("domains", DroppableName.namesOf(domainService.selectAllNames(), ""));
		model.addAttribute("postRoleAction", RolesController.CREATE);
		return "fragments/role_form_modal";
	}
	
	@RequestMapping(value = UPDATE_FORM, method = RequestMethod.GET)
	public String requestUpdateForm(@RequestParam(value = "role", defaultValue = "-1") long roleID, Model model) {
		Role role = roleService.selectById(roleID);
		Domain domain = domainService.selectByRoleID(roleID);
		model.addAttribute("role", new RoleTransferObject(role, domain.getName()));
		model.addAttribute("domains", DroppableName.namesOf(domainService.selectAllNames(), ""));
		model.addAttribute("postRoleAction", RolesController.UPDATE);
		model.addAttribute("formType", "update");
		return "fragments/role_form_modal";
	}

	@SuppressWarnings("deprecation")
	@RequestMapping(value = DELETE, method = RequestMethod.GET)
	public String deleteRole(@RequestParam(value = "role", defaultValue = "-1") long roleID, Model model, HttpSession session) {
		Role role = roleService.selectById(roleID);
		Domain domain = domainService.selectByRoleID(roleID);
		
		if (role != null) {
			User subject = userService.selectById( userIDOf(session) );
			
			tryRemove(role, domain, subject, model);
		}
		return redirectTo(AdminViewController.ROLES);
	}

	/**
	 * Attempts to remove the given <code>Role</code> from the specified <code>Domain</code> on behalf
	 * the <code>User</code> passed as argument.
	 * <p>
	 * Not only logs the operation, also sets a flash message.
	 * 
	 * @param role
	 * @param domain
	 * @param subject
	 * @param model
	 * */
	private void tryRemove(Role role, Domain domain, User subject, Model model) {
		try {
			if (domain == null)
				throw new Exception("Domain does not exist!");

			roleService.delete(role);
			domain.removeRole(role);
			domainService.save(domain); 
			LOGGER.info(String.format("Role %s was removed from %s by %s.", role.getName(), domain.getName(), subject.getUsername()));
			flash( String.format("Role %s was removed from %s", role.getName(), domain.getName()),
				   Severity.INFO,
				   model );
		} catch(Exception e) {
			String message = "unable to delete role " + role.getName();
			LOGGER.error(message + ": " + e.getMessage(), e);
			flash(message, Severity.ERROR, model);
		}
	}

	/**
	 * Resets the given <code>RoleTransferObject</code> into a state where it contains no privileges and 
	 * puts it into the <code>Mode</code> instance (as an attribute) passed as argument.
	 * 
	 * @param newRole
	 * @param model
	 * */
	public static void reset(RoleTransferObject newRole, Model model) {
		newRole.setPrivileges("");
		model.addAttribute("role", newRole);
	}

	/**
	 * Lists the <code>Role</code> instances assigned to the given <code>Domain</code> as its name 
	 * as Drag-n-Drop elements.
	 * 
	 * @param domain
	 * @param model
	 * @return the name of the JSP that generates Html output from the values passed
	 * */
	@RequestMapping(value = ROOT_URL, method = RequestMethod.GET)
	public String listRoles(@RequestParam("domain") String domain, Model model) {
		List<String> roleNames = roleService.selectNamesByDomain(domain);
		model.addAttribute("elements", DroppableName.namesOf(roleNames, domain));
		model.addAttribute("elementBodyClass", "role-body");
		return "fragments/dnd_elements";
	}
}
