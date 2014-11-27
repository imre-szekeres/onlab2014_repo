/**
 * AdminViewController.java
 */
package hu.bme.aut.wman.controllers;

import hu.bme.aut.wman.model.Domain;
import hu.bme.aut.wman.service.DomainService;
import hu.bme.aut.wman.service.PrivilegeService;
import hu.bme.aut.wman.service.RoleService;
import hu.bme.aut.wman.service.UserService;
import hu.bme.aut.wman.view.objects.transfer.RoleTransferObject;
import hu.bme.aut.wman.view.objects.transfer.UserTransferObject;

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

	public static final String ROOT_URL = "/admin";
	public static final String PRIVILEGES = ROOT_URL + "/privileges";
	public static final String ROLES = ROOT_URL + "/roles";
	public static final String DOMAINS = ROOT_URL + "/domains";
	public static final String USERS = ROOT_URL + "/users";
	
	private static final Map<String, String> NAVIGATION_TABS;
	
	static {
		NAVIGATION_TABS = new HashMap<>();
		NAVIGATION_TABS.put(NAV_PREFIX + PRIVILEGES, "Privileges");
		NAVIGATION_TABS.put(NAV_PREFIX + ROLES, "Roles");
		NAVIGATION_TABS.put(NAV_PREFIX + DOMAINS, "Domains");
		NAVIGATION_TABS.put(NAV_PREFIX + USERS, "Users");
	}

	@EJB(mappedName = "java:module/UserService")
	private UserService userService;
	@EJB(mappedName = "java:module/RoleService")
	private RoleService roleService;
	@EJB(mappedName = "java:module/DomainService")
	private DomainService domainService;
	@EJB(mappedName = "java:module/PrivilegeService")
	private PrivilegeService privilegeService;

	
	@RequestMapping(value = ROOT_URL, method = RequestMethod.GET)
	public String admin(Model model) {
		return navigateToFrame("admin", model);
	}

	@RequestMapping(value = ROLES, method = RequestMethod.GET)
	public String adminRoles(Model model) {
		model.addAttribute("newRole", new RoleTransferObject());
		setAdminRolesContent(model, domainService);
		return navigateToFrame("admin_roles", model);
	}

	@RequestMapping(value = DOMAINS, method = RequestMethod.GET)
	public String adminDomains(Model model) {
		model.addAttribute("domains", domainService.selectAll());
		model.addAttribute("newDomain", new Domain());
		model.addAttribute("selectRolesUrl", RolesController.ROOT_URL);
		model.addAttribute("createDomainAction", DomainsController.CREATE);
		return navigateToFrame("admin_domains", model);
	}

	@RequestMapping(value = PRIVILEGES, method = RequestMethod.GET)
	public String adminPrivileges(Model model) {
		model.addAttribute("privileges", privilegeService.selectAll());
		return navigateToFrame("admin_privileges", model);
	}
	
	@RequestMapping(value = USERS, method = RequestMethod.GET)
	public String adminUsers(Model model) {
		model.addAttribute("newUser", new UserTransferObject());
		setAdminUsersContent(model, userService);
		return navigateToFrame("admin_users", model);
	}
	
	@Override
	public Map<String, String> getNavigationTabs() {
		return NAVIGATION_TABS;
	}
	
	public static final void setAdminRolesContent(Model model, DomainService domainService) {
		model.addAttribute("domains", domainService.selectAll());
		model.addAttribute("selectPrivilegesUrl", PrivilegesController.ROOT_URL);
		model.addAttribute("createRoleAction", RolesController.CREATE);
		model.addAttribute("deleteRoleAction", RolesController.DELETE);
		model.addAttribute("navigationTabs", NAVIGATION_TABS);
	}
	
	public static final void setAdminDomainsContent(Model model, DomainService domainService) {
		model.addAttribute("domains", domainService.selectAll());
		model.addAttribute("selectRolesUrl", RolesController.ROOT_URL);
		model.addAttribute("createDomainAction", DomainsController.CREATE);
		model.addAttribute("navigationTabs", NAVIGATION_TABS);
	}
	
	public static final void setAdminUsersContent(Model model, UserService userService) {
		model.addAttribute("users", userService.selectAll());
		model.addAttribute("selectDomainsForUrl", UsersController.DOMAINS);
		model.addAttribute("selectDomainNamesUrl", DomainsController.NAMES);
		model.addAttribute("createUserAction", UsersController.CREATE);
		model.addAttribute("selectRolesForUrl", RolesController.ROOT_URL);
		model.addAttribute("navigationTabs", NAVIGATION_TABS);
	}
}
