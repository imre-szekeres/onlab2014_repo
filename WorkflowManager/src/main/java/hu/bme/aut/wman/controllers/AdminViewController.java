/**
 * AdminViewController.java
 */
package hu.bme.aut.wman.controllers;

import hu.bme.aut.wman.service.DomainService;
import hu.bme.aut.wman.service.RoleService;
import hu.bme.aut.wman.view.objects.transfer.RoleTransferObject;

import java.util.HashMap;
import java.util.Map;

import javax.ejb.EJB;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * @author Imre Szekeres
 * @version "%I%, %G%"
 */
@Controller
public class AdminViewController extends AbstractController {

	public static final String ADMIN_ROOT = "/admin";
	public static final String PRIVILEGES_ROOT = ADMIN_ROOT + "/privileges";
	public static final String ROLES_ROOT = ADMIN_ROOT + "/roles";
	public static final String DOMAINS_ROOT = ADMIN_ROOT + "/domains";
	public static final String USERS_ROOT = ADMIN_ROOT + "/users";
	
	private static final Map<String, String> NAVIGATION_TABS;
	
	static {
		NAVIGATION_TABS = new HashMap<>();
		NAVIGATION_TABS.put(NAV_PREFIX + PRIVILEGES_ROOT, "Privileges");
		NAVIGATION_TABS.put(NAV_PREFIX + ROLES_ROOT, "Roles");
		NAVIGATION_TABS.put(NAV_PREFIX + DOMAINS_ROOT, "Domains");
		NAVIGATION_TABS.put(NAV_PREFIX + USERS_ROOT, "Users");
	}
	
	@EJB(mappedName = "java:module/RoleService")
	private RoleService roleService;
	@EJB(mappedName = "java:module/DomainService")
	private DomainService domainService;
	
	
	@RequestMapping(value = ADMIN_ROOT, method = RequestMethod.GET)
	public String admin(Model model) {
		return navigateToFrame("admin", model);
	}
	
	@SuppressWarnings("deprecation")
	@RequestMapping(value = ROLES_ROOT, method = RequestMethod.GET)
	public String adminRoles(Model model) {
		model.addAttribute("domains", domainService.selectAll());
		model.addAttribute("newRole", new RoleTransferObject());
		model.addAttribute("selectPrivilegesUrl", PrivilegesController.ASYNC_SELECT);
		return navigateToFrame("admin_roles", model);
	}
	
	@Override
	public Map<String, String> getNavigationTabs() {
		return NAVIGATION_TABS;
	}
}
