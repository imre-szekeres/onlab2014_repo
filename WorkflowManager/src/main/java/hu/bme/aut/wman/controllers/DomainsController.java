/**
 * DomainsController.java
 */
package hu.bme.aut.wman.controllers;

import hu.bme.aut.wman.model.Domain;
import hu.bme.aut.wman.model.Role;
import hu.bme.aut.wman.service.DomainService;
import hu.bme.aut.wman.service.RoleService;

import java.util.List;
import java.util.Map;

import javax.ejb.EJB;
import javax.servlet.http.HttpServletRequest;

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
public class DomainsController extends AbstractController {

	public static final String ROOT_URL = "/domains";
	public static final String CREATE = ROOT_URL + "/create";
	public static final String NAMES = ROOT_URL + "/names";
	
	@EJB(mappedName = "java:module/DomainService")
	private DomainService domainService;
	@EJB(mappedName = "java:module/RoleService")
	private RoleService roleService;
	
	@SuppressWarnings("deprecation")
	@RequestMapping(value = CREATE, method = RequestMethod.POST)
	public String createDomain(@ModelAttribute("newDomain") Domain newDomain, Model model) {
		Map<String, String> errors = domainService.validate( newDomain );
		
		if (errors.isEmpty()) {
			List<Role> defaults = domainService.selectByName(DomainService.DEFAULT_DOMAIN).getRoles();
			
			for(Role role : defaults) {
				int lastIndex = role.getName().lastIndexOf(" ");
				String roleName = role.getName().substring(lastIndex);
				Role newRole = new Role(newDomain.getName() + roleName);
				
				newRole.setPrivileges( role.getPrivileges() );
				roleService.save( newRole );
				newDomain.addRole( newRole );
			}
			
			domainService.save( newDomain );
			return redirectTo(AdminViewController.DOMAINS);
		}

		model.addAttribute(AbstractController.ERRORS_MAP, errors);
		AdminViewController.setAdminDomainsContent(model, domainService);
		model.addAttribute("pageName", "admin_domains");
		return "wman_frame";
	}
	
	@RequestMapping(value = NAMES, method = RequestMethod.GET)
	public String listDomainNames(Model model, HttpServletRequest request) {
		List<Domain> domains = domainService.selectAll();
		model.addAttribute("options", domains);
		return "fragments/option_list";
	}
	
	public static final String[] namesOf(List<Domain> domains) {
		String[] names = new String[domains.size()];
		for(int i = 0; i < names.length; ++i)
			names[i] = domains.get( i ).getName();
		return names;
	}
}
