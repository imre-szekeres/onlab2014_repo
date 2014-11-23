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

import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * @author Imre Szekeres
 * @version "%I%, %G%"
 */
@Controller
public class RolesController extends AbstractController {

	private static final Logger LOGGER = Logger.getLogger( RolesController.class );
	
	public static final String ROLES_ROOT = "/roles";
	public static final String ROLES_CREATE = ROLES_ROOT + "/create";
	

	@EJB(mappedName = "java:module/RoleService")
	private RoleService roleService;
	@EJB(mappedName = "java:module/PrivilegeService")
	private PrivilegeService privilegeService;
	@EJB(mappedName = "java:module/DomainService")
	private DomainService domainService;
	@EJB(mappedName = "java:module/UserService")
	private UserService userService;
	
	
	@SuppressWarnings("deprecation")
	@RequestMapping(value = ROLES_CREATE, method = RequestMethod.POST)
	public String createRole(@ModelAttribute("newRole")RoleTransferObject newRole, Model model, HttpServletRequest request) {
		String roleName = newRole.getRoleName();
		String domainName  = newRole.getDomainName();
		
		Role role = new Role(roleName);
		Map<String, String> errors = roleService.validate(role, domainName);
		
		if (errors.isEmpty()) {
			User subject = userService.selectById(((SecurityToken) request.getSession().getAttribute("subject")).getUserID());
			List<String> privileges = newRole.privileges();
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
		} else {
			model.addAttribute("errorMessages", errors);
		}

		model.addAttribute("message", "createRole reached");
		return redirectTo(AdminViewController.ROLES_ROOT);
	}
}