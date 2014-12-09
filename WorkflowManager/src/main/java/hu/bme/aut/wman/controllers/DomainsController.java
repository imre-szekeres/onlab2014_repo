/**
 * DomainsController.java
 */
package hu.bme.aut.wman.controllers;

import static hu.bme.aut.wman.controllers.LoginController.refreshTokens;
import static hu.bme.aut.wman.controllers.LoginController.userIDOf;
import static java.lang.String.format;
import hu.bme.aut.wman.managers.DomainManager;
import hu.bme.aut.wman.model.Domain;
import hu.bme.aut.wman.model.User;
import hu.bme.aut.wman.service.DomainService;
import hu.bme.aut.wman.service.PrivilegeService;
import hu.bme.aut.wman.service.UserService;
import hu.bme.aut.wman.view.DroppableName;
import hu.bme.aut.wman.view.Messages.Severity;

import java.util.List;
import java.util.Map;

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
 * Handles <code>Domain</code> management specific requests.
 * 
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
	

	@EJB(mappedName = "java:module/DomainService")
	private DomainService domainService;
	@EJB(mappedName = "java:module/UserService")
	private UserService userService;
	@EJB(mappedName = "java:module/PrivilegeService")
	private PrivilegeService privilegeService;
	
	@EJB(mappedName = "java:module/DomainManager")
	private DomainManager domainManager;

	/**
	 * Responsible for executing the creation of a <code>Domain</code> instance in the name of the currently authenticated
	 * <code>User</code>.
	 * <p>
	 * Also logs the operation and flashes message according to the outcome.
	 * 
	 * @param newDomain
	 * @param model
	 * @param session
	 * @return the name of the View that renders the Html response
	 * */
	@PreAuthorize("hasRole('Create Domain')")
	@RequestMapping(value = CREATE, method = RequestMethod.POST)
	public String createDomain(@ModelAttribute("domain") Domain newDomain, Model model, HttpServletRequest request, HttpSession session) {
		
		Map<String, String> errors = domainManager.validate( newDomain );
		Long subjectID = userIDOf(session);
		if (errors.isEmpty()) {
			
			User subject = userService.selectById( subjectID );
			String message = format("Domain %s was created", domainManager.create(subject, newDomain));
			LOGGER.info(format("%s by %s.", message, subject));
			flash(message, Severity.INFO, model);
			refreshTokens(subject, subject, request, privilegeService);
			return redirectTo(AdminViewController.DOMAINS);
		}

		model.addAttribute(AbstractController.ERRORS_MAP, errors);
		model.addAttribute("postDomainAction", DomainsController.CREATE);
		AdminViewController.setAdminDomainsContent(model, subjectID, domainService);
		model.addAttribute("pageName", "admin_domains");
		return "wman_frame";
	}

	/**
	 * Responsible for executing the update of a <code>Domain</code> instance in the name of the currently authenticated
	 * <code>User</code>.
	 * <p>
	 * Also logs the operation and flashes message according to the outcome.
	 * 
	 * @param newDomain
	 * @param oldId
	 * @param model
	 * @param session
	 * @return the name of the View that renders the Html response
	 * */
	@PreAuthorize("hasPermission(#oldId, 'Domain', 'Update Domain')")
	@RequestMapping(value = UPDATE, method = RequestMethod.POST)
	public String updateDomain(@ModelAttribute("domain") Domain newDomain, @RequestParam("oldId") Long oldId, Model model, HttpSession session) {
		Domain domain = domainService.selectById(oldId);
		domain.setName(newDomain.getName());
		Map<String, String> errors = domainService.validate( domain );
		
		Long subjectID = userIDOf(session);
		if (errors.isEmpty()) {
			User subject = userService.selectById( subjectID );
			
			domainService.save( domain );
			String message = "Domain " + domain.getName() + " was updated";
			LOGGER.info(message + " by " + subject.getUsername());
			flash(message, Severity.INFO, model);
			return redirectTo(AdminViewController.DOMAINS);
		}

		model.addAttribute(AbstractController.ERRORS_MAP, errors);
		setUpdateFormAttributes(newDomain, oldId, model);
		AdminViewController.setAdminDomainsContent(model, subjectID, domainService);
		model.addAttribute("pageName", "admin_domains");
		return "wman_frame";
	}

	/**
	 * Pre-sets the required data for the Create Domain Form, then returns the name of the View.
	 * 
	 * @param model
	 * @return the name of the View that renders the Html response
	 * */
	@PreAuthorize("hasRole('Create Domain')")
	@RequestMapping(value = CREATE_FORM, method = RequestMethod.GET)
	public String requestCreateForm(Model model) {
		model.addAttribute("domain", new Domain());
		model.addAttribute("postDomainAction", DomainsController.CREATE);
		return "fragments/domain_form_modal";
	}

	/**
	 * Pre-sets the required data for the Update Domain Form, then returns the name of the View.
	 * 
	 * @param domainID
	 * @param model
	 * @return the name of the View that renders the Html response
	 * */
	@PreAuthorize("hasPermission(#domainID, 'Domain', 'Update Domain')")
	@RequestMapping(value = UPDATE_FORM, method = RequestMethod.GET)
	public String requestUpdateForm(@RequestParam("domain") Long domainID, Model model) {
		Domain domain = domainService.selectById(domainID);
		setUpdateFormAttributes(domain, domainID, model);
		return "fragments/domain_form_modal";
	}

	/**
	 * Helper method for setting the <code>Model</code> attributes for the response for
	 * the request of <code>DomainsService.UPDATE_FORM</code>.
	 * 
	 * @param domain
	 * @param model
	 * */
	public static final void setUpdateFormAttributes(Domain domain, Long oldId, Model model) {
		model.addAttribute("domain", domain);
		model.addAttribute("oldId", oldId);
		model.addAttribute("postDomainAction", DomainsController.UPDATE);
		model.addAttribute("formType", "update");
	}

	/**
	 * Responsible for executing the removal of a <code>Domain</code> instance in the name of the currently authenticated
	 * <code>User</code>.
	 * <p>
	 * Also logs the operation and flashes message according to the outcome.
	 * 
	 * @param domainID
	 * @param session
	 * @param model
	 * @return the name of the View that renders the Html response
	 * */
	@PreAuthorize("hasPermission(#domainID, 'Domain', 'Remove Domain')")
	@RequestMapping(value = DELETE, method = RequestMethod.GET)
	public String deleteDomain(@RequestParam("domain") Long domainID, HttpServletRequest request, HttpSession session, Model model) {
		Domain domain = domainService.selectById(domainID);
		
		if (domain != null) {
			User subject = userService.selectById( userIDOf(session) );
			tryRemove(domain, subject, model);
			refreshTokens(subject, subject, request, privilegeService);
		}
		return redirectTo(AdminViewController.DOMAINS);
	}

	/**
	 * Attempts to remove all the <code>DomainAssignment</code>s corresponding to the given <code>Domain</code> instance,
	 * and delete the <code>Domain</code> in the name of the <code>User</code> specified.
	 * <p>
	 * Also logs the operation and sets a flash message according to the outcome.
	 * 
	 * @param domain
	 * @param subject
	 * @param model
	 * @see {@link DomainsController#deleteDomain(Long, HttpSession, Model)}
	 * */
	private final void tryRemove(Domain domain, User subject, Model model) {
		try {
			domainManager.remove(subject, domain);
		} catch(Exception e) {
			String message = "Unable to delete domain " + domain.getName();
			LOGGER.error(message + ": " + e.getMessage(), e);
			flash(message, Severity.ERROR, model);
		}
	}

	/**
	 * Lists the <code>Domain</code> names available to the <code>User</code> currently authenticated,
	 * as an option list.
	 * 
	 * @param model
	 * @param session
	 * @return the name of the View that renders the Html response
	 * */
	@PreAuthorize("hasRole('View Domain')")
	@RequestMapping(value = NAMES, method = RequestMethod.GET)
	public String listDomainNames(Model model, HttpSession session) {
		List<String> domainNames = domainService.domainNamesOf( userIDOf(session) );
		model.addAttribute("options", DroppableName.namesOf(domainNames, ""));
		return "fragments/option_list";
	}
}
