/**
 * RolesController.java
 */
package hu.bme.aut.wman.controllers;

import hu.bme.aut.wman.model.Domain;
import hu.bme.aut.wman.model.Privilege;
import hu.bme.aut.wman.model.Role;
import hu.bme.aut.wman.model.User;
import hu.bme.aut.wman.security.SecurityToken;
import hu.bme.aut.wman.service.DomainService;
import hu.bme.aut.wman.service.PrivilegeService;
import hu.bme.aut.wman.service.RoleService;
import hu.bme.aut.wman.service.UserService;
import hu.bme.aut.wman.view.objects.transfer.RoleTransferObject;

import java.util.List;
import java.util.Map;

import javax.ejb.EJB;
import javax.servlet.http.HttpServletRequest;
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
	public String createRole(@ModelAttribute("newRole") RoleTransferObject newRole, Model model, HttpServletRequest request) {
		String roleName = newRole.getRoleName();
		String domainName  = newRole.getDomainName();
		
		Role role = new Role(roleName);
		Map<String, String> errors = roleService.validate(role, domainName);
		
		if (errors.isEmpty()) {
			User subject = userService.selectById(((SecurityToken) request.getSession().getAttribute("subject")).getUserID());
			List<String> privileges = newRole.privileges();
			
			LOGGER.debug("found \'" + newRole.getPrivileges() + "\' for role " + roleName);
			LOGGER.debug("parsed (" + privileges.size() + ") privileges for role " + roleName);
			
			for(String s : privileges) {
				Privilege p = privilegeService.selectByName(s);
				LOGGER.debug(p.toString() + " was found");
				role.addPrivilege( p );
				LOGGER.debug(p.toString() + " was addoed to " + role.toString());
			}
			roleService.save(role);
			LOGGER.info(role.toString() + " was created by " + subject.getUsername());
			
			Domain d = domainService.selectByName(domainName);
			d.addRole(role);
			domainService.save( d );
			LOGGER.info(role.toString() + " was added to " + d.toString());
			return redirectTo(AdminViewController.ROLES);
		}

		model.addAttribute(AbstractController.ERRORS_MAP, errors);
		AdminViewController.setAdminRolesContent(model, domainService);
		model.addAttribute("pageName", "admin_roles");
		reset(newRole, model);
		return AbstractController.FRAME;
	}
	
	@SuppressWarnings("deprecation")
	@RequestMapping(value = DELETE, method = RequestMethod.GET)
	public String deleteRole(@RequestParam("role") long roleID, HttpSession session, Model model) {
		Role role = roleService.selectById(roleID);
		Domain domain = domainService.selectByRoleID(roleID);
		
		if (role != null) {
			SecurityToken token = (SecurityToken) session.getAttribute("subject");
			User subject = userService.selectById(token.getUserID());
			
			tryRemove(role, domain, subject, session, model);
		}
		return redirectTo(AdminViewController.ROLES);
	}
	
	private final void tryRemove(Role role, Domain domain, User subject, HttpSession session, Model model) {
		try {
			if (domain == null)
				throw new Exception("Domain does not exist!");

			roleService.delete(role);
			domain.removeRole(role);
			domainService.save(domain);
			LOGGER.info(String.format("Role %s was removed from %s by %s.", role.getName(), domain.getName(), subject.getUsername()));
		} catch(Exception e) {
			String message = "unable to delete role " + role.getName() + ": " + e.getMessage();
			LOGGER.error(message, e);
			model.addAttribute("errorMessage", message);
		}
	}
	
	public static final void reset(RoleTransferObject newRole, Model model) {
		newRole.setPrivileges("");
		model.addAttribute("newRole", newRole);
	}
	
	@RequestMapping(value = ROOT_URL, method = RequestMethod.GET)
	public String listRoles(Model model, HttpServletRequest request) {
		String domain = request.getParameter("domain");
		model.addAttribute("elements", roleService.selectByDomain(domain));
		model.addAttribute("elementBodyClass", "role-body");
		return "fragments/dnd_elements";
	}
}
