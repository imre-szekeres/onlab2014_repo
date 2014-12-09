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

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import javax.ejb.EJB;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.springframework.security.access.prepost.PreAuthorize;
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
	

	@PreAuthorize("hasPermission(#newRole.domainName, 'Domain', 'Create Role') and hasPermission(#newRole.domainName, 'Domain', 'Assign Privilege')")
	@RequestMapping(value = CREATE, method = RequestMethod.POST)
	public String createRole(@ModelAttribute("role") RoleTransferObject newRole, Model model, HttpSession session) {
		String roleName = newRole.getRoleName();
		String domainName  = newRole.getDomainName();
		
		Role role = new Role(roleName);
		Map<String, String> errors = roleService.validate(role, domainName);
		
		Long subjectID = userIDOf(session);
		if (errors.isEmpty()) {
			User subject = userService.selectById( subjectID );
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
		AdminViewController.setAdminRolesContent(model, subjectID, domainService, Arrays.asList(new String[] {"View Role"}));
		List<String> authorities = Arrays.asList(new String[] {"Assign Privilege", "Create Role"});
		setFormAttributes(newRole, subjectID, domainService, RolesController.CREATE, "create", authorities, model);
		model.addAttribute("pageName", "admin_roles");
		reset(newRole, model);
		return AbstractController.FRAME;
	}

	@PreAuthorize("hasPermission(#updated.id, 'Role', 'Assign Privilege')")
	@RequestMapping(value = UPDATE, method = RequestMethod.POST)
	public String updateRole(@ModelAttribute("role") RoleTransferObject updated, Model model, HttpSession session) {
		String roleName = updated.getRoleName();
		String domainName  = updated.getDomainName();
		
		Role role = roleService.selectById(updated.getId());
		role.setName( updated.getRoleName() );
		Map<String, String> errors = roleService.validate(role, domainName);
		
		Long subjectID = userIDOf(session);
		if (errors.isEmpty()) {
			User subject = userService.selectById( subjectID );
			List<String> privileges = updated.privileges();
			
			LOGGER.debug("found \'" + updated.getPrivileges() + "\' for role " + roleName);
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
		List<String> authorities = Arrays.asList(new String[] {"Assign Privilege"});
		setFormAttributes(updated, subjectID, domainService, RolesController.UPDATE, "update", authorities, model);
		AdminViewController.setAdminRolesContent(model, subjectID, domainService, Arrays.asList(new String[] {"View Role"}));
		model.addAttribute("pageName", "admin_roles");
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

	@PreAuthorize("hasRole('Assign Privilege') and hasRole('Create Role')")
	@RequestMapping(value = CREATE_FORM, method = RequestMethod.GET)
	public String requestCreateForm(Model model, HttpSession session) {
		Long subjectID = userIDOf(session);
		List<String> authorities = Arrays.asList(new String[] {"Assign Privilege", "Create Role"});
		setFormAttributes(new RoleTransferObject(), subjectID, domainService, RolesController.CREATE, "create", authorities, model);
		return "fragments/role_form_modal";
	}

	@PreAuthorize("hasPermission(#roleID, 'Role', 'Assign Privilege')")
	@RequestMapping(value = UPDATE_FORM, method = RequestMethod.GET)
	public String requestUpdateForm(@RequestParam(value = "role", defaultValue = "-1") Long roleID, Model model, HttpSession session) {
		Role role = roleService.selectById(roleID);
		Domain domain = domainService.selectByRoleID(roleID);
		Long subjectID = userIDOf(session);
		List<String> authorities = Arrays.asList(new String[] {"Assign Privilege"});
		setFormAttributes(new RoleTransferObject(role, domain.getName()), subjectID, domainService, RolesController.UPDATE, "update", authorities, model);
		return "fragments/role_form_modal";
	}

	/**
	 * Sets the <code>Model</code> attributes required by the forms used to represent <code>Role</code> data for
	 * either creation or update operations.
	 * 
	 * @param role
	 * @param domainService
	 * @param postRoleAction
	 * @param formType
	 * @param authorities
	 * @param model
	 * */
	public static final void setFormAttributes(RoleTransferObject role, Long subjectID, DomainService domainService, String postRoleAction, String formType, Collection<String> authorities, Model model) {
		model.addAttribute("role", role);
		List<String> domainNames = domainService.domainNamesOf(subjectID, authorities);
		model.addAttribute("domainNames", DroppableName.namesOf(domainNames, ""));
		model.addAttribute("postRoleAction", postRoleAction);
		model.addAttribute("formType", formType);
	} 

	@PreAuthorize("hasPermission(#roleID, 'Role', 'Create Role')")
	@RequestMapping(value = DELETE, method = RequestMethod.GET)
	public String deleteRole(@RequestParam(value = "role", defaultValue = "-1") Long roleID, Model model, HttpSession session) {
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
