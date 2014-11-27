/**
 * DomainsController.java
 */
package hu.bme.aut.wman.controllers;

import hu.bme.aut.wman.model.Domain;
import hu.bme.aut.wman.model.DomainAssignment;
import hu.bme.aut.wman.model.Role;
import hu.bme.aut.wman.model.User;
import hu.bme.aut.wman.security.SecurityToken;
import hu.bme.aut.wman.service.DomainAssignmentService;
import hu.bme.aut.wman.service.DomainService;
import hu.bme.aut.wman.service.RoleService;
import hu.bme.aut.wman.service.UserService;
import hu.bme.aut.wman.view.Messages.Severity;

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
public class DomainsController extends AbstractController {

	private static final Logger LOGGER = Logger.getLogger( DomainsController.class );
	
	public static final String ROOT_URL = "/domains";
	public static final String CREATE = ROOT_URL + "/create";
	public static final String CREATE_FORM = CREATE + "/form";
	
	public static final String UPDATE = ROOT_URL + "/update";
	public static final String UPDATE_FORM = UPDATE + "/form";
	public static final String DELETE = ROOT_URL + "/delete";
	public static final String NAMES = ROOT_URL + "/names";
	
	
	@EJB(mappedName = "java:module/DomainAssignmentService")
	private DomainAssignmentService daService;
	@EJB(mappedName = "java:module/DomainService")
	private DomainService domainService;
	@EJB(mappedName = "java:module/RoleService")
	private RoleService roleService;
	@EJB(mappedName = "java:module/UserService")
	private UserService userService;
	
	@SuppressWarnings("deprecation")
	@RequestMapping(value = CREATE, method = RequestMethod.POST)
	public String createDomain(@ModelAttribute("domain") Domain newDomain, Model model, HttpSession session) {
		Map<String, String> errors = domainService.validate( newDomain );
		
		if (errors.isEmpty()) {
			List<Role> defaults = domainService.selectByName(DomainService.DEFAULT_DOMAIN).getRoles();
			
			SecurityToken token = (SecurityToken) session.getAttribute("subject");
			User subject = userService.selectById(token.getUserID());
			
			for(Role role : defaults) {
				int lastIndex = role.getName().lastIndexOf(" ");
				String roleName = role.getName().substring(lastIndex);
				Role newRole = new Role(newDomain.getName() + roleName);
				
				newRole.setPrivileges( role.getPrivileges() );
				roleService.save( newRole );
				newDomain.addRole( newRole );
				LOGGER.info("Role " + newRole.getName() + " was found and added to " + newDomain.getName());
			}
			
			domainService.save( newDomain );
			String message = "Domain " + newDomain.getName() + " was created";
			LOGGER.info(message + " by " + subject.getUsername());
			flash(message, Severity.INFO, model);
			return redirectTo(AdminViewController.DOMAINS);
		}

		model.addAttribute(AbstractController.ERRORS_MAP, errors);
		AdminViewController.setAdminDomainsContent(model, domainService);
		model.addAttribute("pageName", "admin_domains");
		return "wman_frame";
	}
	
	@SuppressWarnings("deprecation")
	@RequestMapping(value = UPDATE, method = RequestMethod.POST)
	public String updateDomain(@ModelAttribute("domain") Domain newDomain, @RequestParam("oldId") long oldId, Model model, HttpSession session) {
		Domain domain = domainService.selectById(oldId);
		domain.setName(newDomain.getName());
		Map<String, String> errors = domainService.validate( domain );
		
		if (errors.isEmpty()) {
			SecurityToken token = (SecurityToken) session.getAttribute("subject");
			User subject = userService.selectById(token.getUserID());
			
			domainService.save( domain );
			String message = "Domain " + domain.getName() + " was updated";
			LOGGER.info(message + " by " + subject.getUsername());
			flash(message, Severity.INFO, model);
			return redirectTo(AdminViewController.DOMAINS);
		}

		model.addAttribute(AbstractController.ERRORS_MAP, errors);
		AdminViewController.setAdminDomainsContent(model, domainService);
		model.addAttribute("pageName", "admin_domains");
		return "wman_frame";
	}
	
	@RequestMapping(value = CREATE_FORM, method = RequestMethod.GET)
	public String requestCreateForm(Model model) {
		model.addAttribute("domain", new Domain());
		model.addAttribute("postDomainAction", DomainsController.CREATE);
		return "fragments/domain_form_modal";
	}
	
	@RequestMapping(value = UPDATE_FORM, method = RequestMethod.GET)
	public String requestUpdateForm(@RequestParam("domain") long domainID, Model model) {
		Domain domain = domainService.selectById(domainID);
		model.addAttribute("domain", domain);
		model.addAttribute("oldId", domainID);
		model.addAttribute("postDomainAction", DomainsController.UPDATE);
		model.addAttribute("formType", "update");
		return "fragments/domain_form_modal";
	}
	
	@SuppressWarnings("deprecation")
	@RequestMapping(value = DELETE, method = RequestMethod.GET)
	public String deleteRole(@RequestParam("domain") long domainID, HttpSession session, Model model) {
		Domain domain = domainService.selectById(domainID);
		
		if (domain != null) {
			SecurityToken token = (SecurityToken) session.getAttribute("subject");
			User subject = userService.selectById(token.getUserID());
			
			tryRemove(domain, subject, session, model);
		}
		return redirectTo(AdminViewController.DOMAINS);
	}
	
	private final void tryRemove(Domain domain, User subject, HttpSession session, Model model) {
		try {
			
			List<DomainAssignment> assignments = daService.selectByDomainName(domain.getName());
			for(DomainAssignment da : assignments) {
				daService.delete( da );
				LOGGER.info(da.getUser().getUsername() + " was deassigned from " + da.getDomain().getName());
			}
			domainService.delete( domain );
			LOGGER.info("Domain " + domain.getName() + " was removed by " + subject.getUsername());
		} catch(Exception e) {
			String message = "Unable to delete domain " + domain.getName();
			LOGGER.error(message + ": " + e.getMessage(), e);
			flash(message, Severity.ERROR, model);
		}
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
