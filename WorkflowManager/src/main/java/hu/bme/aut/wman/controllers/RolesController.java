/**
 * RolesController.java
 */
package hu.bme.aut.wman.controllers;

import static hu.bme.aut.wman.controllers.LoginController.refreshTokens;
import static hu.bme.aut.wman.controllers.LoginController.userIDOf;
import static hu.bme.aut.wman.utils.StringUtils.asString;
import static java.lang.String.format;
import hu.bme.aut.wman.managers.RoleManager;
import hu.bme.aut.wman.model.Domain;
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
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.ejb.EJB;
import javax.servlet.http.HttpServletRequest;
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
 * Handles <code>HttpServletRequest</code>s regarding the management of <code>Role</code> life cycle.
 * 
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
	

	@EJB(mappedName = "java:module/RoleManager")
	private RoleManager roleManager;
	
	@EJB(mappedName = "java:module/RoleService")
	private RoleService roleService;
	@EJB(mappedName = "java:module/PrivilegeService")
	private PrivilegeService privilegeService;
	@EJB(mappedName = "java:module/DomainService")
	private DomainService domainService;
	@EJB(mappedName = "java:module/UserService")
	private UserService userService;
	

	/**
	 * Processes the data transferred, and after a successful validation it creates the <code>Role</code> 
	 * according to the data obtained from the <code>RoleTransferObject</code>, which involves not just the 
	 * setting the <code>Set</code> of <code>Privilege</code>s it provides.
	 * 
	 * @param newRole
	 * @param model
	 * @param session
	 * 
	 * @return a {@link String} that specifies where to navigate to
	 * */
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
			Set<String> privileges = newRole.privileges();
			
			String message = null;
			try {
				roleManager.assignNew(role, domainName, privileges);
				message = format("Role %s was added to Domain %s.", role, domainName);
				LOGGER.info(format("%s by User %s.", message, subject));
				flash(message, Severity.INFO, model);
				return redirectTo(AdminViewController.ROLES);
			} catch(Exception e) {
				LOGGER.error(format("Unable to create Role %s due to %s.", role, e.getMessage()), e);
				errors.put("privileges", messageOf( e ));
				model.addAttribute(AbstractController.ERRORS_MAP, errors);
			}
		}

		model.addAttribute(AbstractController.ERRORS_MAP, errors);
		AdminViewController.setAdminRolesContent(model, subjectID, domainService, Arrays.asList(new String[] {"View Role"}));
		List<String> authorities = Arrays.asList(new String[] {"Assign Privilege", "Create Role"});
		setFormAttributes(newRole, subjectID, domainService, RolesController.CREATE, "create", authorities, model);
		model.addAttribute("pageName", "admin_roles");
		reset(newRole, model);
		return AbstractController.FRAME;
	}

	/**
	 * Attempts to remove the exception-like parts of the message obtained from the given <code>Exception</code>.
	 * 
	 * @param e
	 * @return the message
	 * */
	private static final String messageOf(Exception e) {
		String message = e.getMessage();
		int idx = message.lastIndexOf(":");
		if (idx > 0)
			return message.substring(idx + 1, message.length());
		return message;
	}

	/**
	 * Processes the data transferred, and after a successful validation it updates the <code>Role</code> 
	 * according to the data obtained from the <code>RoleTransferObject</code>, which involves not just the 
	 * refreshing of its name, but also the <code>Set</code> of <code>Privilege</code>s it provides.
	 * 
	 * @param updated
	 * @param model
	 * @param request
	 * @param session
	 * 
	 * @return a {@link String} that specifies where to navigate to
	 * */
	@PreAuthorize("hasPermission(#updated.id, 'Role', 'Assign Privilege')")
	@RequestMapping(value = UPDATE, method = RequestMethod.POST)
	public String updateRole(@ModelAttribute("role") RoleTransferObject updated, Model model, HttpServletRequest request, HttpSession session) {
		String domainName  = updated.getDomainName();
		
		Role role = roleService.selectById(updated.getId());
		role.setName( updated.getRoleName() );
		Map<String, String> errors = roleService.validate(role, domainName);
		
		Long subjectID = userIDOf(session);
		if (errors.isEmpty()) {
			User subject = userService.selectById( subjectID );
			Set<String> privileges = updated.privileges();
			
			tryAssign(role, privileges, subject, model);
			String message = role.toString() + " was updated";
			LOGGER.info(message + " by " + subject.getUsername());
			flash(message, Severity.INFO, model);

			refreshTokens(subject, subject, request, privilegeService);
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
	 * Attempts to assign the given <code>Privilege</code>s to the given <code>Role</code>.
	 * <p>
	 * Flashes messages according to the outcome of the operation.
	 * 
	 * @param role
	 * @param privileges
	 * @param subject
	 * @param model
	 * */
	private final void tryAssign(Role role, Set<String> privileges, User subject, Model model) {
		try {
			
			roleManager.assign(role, privileges);
			LOGGER.info(format("Role %s now owns %s as Privileges, was updated by %s", role, asString(privileges), subject.getUsername()));
			flash(format("Role %s was updated.", role), Severity.INFO, model);
		} catch(Exception e) {
			LOGGER.error(e.getMessage(),e);
			flash(format("Role %s could not be updated due to %s", role, e.getMessage()), Severity.ERROR, model);
		}
	}

	/**
	 * Provides the parameters required by the view to build an HTML form for creating a <code>Role</code>.
	 * 
	 * @param model
	 * @param session
	 * @return a name that identifies the View
	 * */
	@PreAuthorize("hasRole('Assign Privilege') and hasRole('Create Role')")
	@RequestMapping(value = CREATE_FORM, method = RequestMethod.GET)
	public String requestCreateForm(Model model, HttpSession session) {
		Long subjectID = userIDOf(session);
		List<String> authorities = Arrays.asList(new String[] {"Assign Privilege", "Create Role"});
		setFormAttributes(new RoleTransferObject(), subjectID, domainService, RolesController.CREATE, "create", authorities, model);
		return "fragments/role_form_modal";
	}

	/**
	 * Provides the parameters required by the view to build an HTML form for updating a <code>Role</code>.
	 * 
	 * @param roleID
	 * @param model
	 * @param session
	 * 
	 * @return a name that identifies the View
	 * */
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

	/**
	 * Removes the <code>Role</code> specified by its id.
	 * 
	 * @param roleID
	 * @param session
	 * @param model
	 * @return a name that identifies the View
	 * */
	@PreAuthorize("hasPermission(#roleID, 'Role', 'Create Role')")
	@RequestMapping(value = DELETE, method = RequestMethod.DELETE)
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
