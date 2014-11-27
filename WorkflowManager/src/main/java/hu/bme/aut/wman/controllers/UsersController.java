/**
 * UsersController.java
 */
package hu.bme.aut.wman.controllers;

import static hu.bme.aut.wman.controllers.LoginController.userIDOf;
import hu.bme.aut.wman.model.Domain;
import hu.bme.aut.wman.model.DomainAssignment;
import hu.bme.aut.wman.model.Role;
import hu.bme.aut.wman.model.User;
import hu.bme.aut.wman.service.DomainAssignmentService;
import hu.bme.aut.wman.service.DomainService;
import hu.bme.aut.wman.service.RoleService;
import hu.bme.aut.wman.service.UserService;
import hu.bme.aut.wman.view.Messages.Severity;
import hu.bme.aut.wman.view.objects.transfer.UserTransferObject;

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
public class UsersController extends AbstractController {

	private static final Logger LOGGER = Logger.getLogger( UsersController.class );

	public static final String ROOT_URL = "/users";
	public static final String CREATE = ROOT_URL + "/create";
	public static final String CREATE_FORM = CREATE + "/form";
	
	public static final String UPDATE = ROOT_URL + "/update";
	public static final String UPDATE_FORM = UPDATE + "/form";
	public static final String DELETE = ROOT_URL + "/delete";
	public static final String DOMAINS = ROOT_URL + "/domains";
	public static final String PROFILE = ROOT_URL + "/profile";


	@EJB(mappedName = "java:module/DomainAssignmentService")
	private DomainAssignmentService daService;
	@EJB(mappedName = "java:module/UserService")
	private UserService userService;
	@EJB(mappedName = "java:module/DomainService")
	private DomainService domainService;
	@EJB(mappedName = "java:module/RoleService")
	private RoleService roleService;




	@RequestMapping(value = PROFILE, method = RequestMethod.GET)
	public String viewProfile(@RequestParam("user") long userID, Model model, HttpSession session) {
		User user = userService.selectById(userID);
		model.addAttribute("user", user);

		boolean isEditable = userID == userIDOf(session);
		model.addAttribute("isEditable", isEditable);
		return navigateToFrame("user_profile", model);
	}

	@RequestMapping(value = CREATE_FORM, method = RequestMethod.GET)
	public String requestCreateForm(Model model) {
		model.addAttribute("user", new UserTransferObject());
		model.addAttribute("postUserAction", UsersController.CREATE);
		return "fragments/user_form_modal";
	}
	
	@SuppressWarnings("deprecation")
	@RequestMapping(value = DELETE, method = RequestMethod.GET)
	public String deleteRole(@RequestParam("user") long userID, HttpSession session, Model model) {
		User user = userService.selectById(userID);
		
		if (user != null) {
			long subjectID = userIDOf(session);
			User subject = userService.selectById(subjectID);
			
			tryRemove(user, subject, model);
		}
		return redirectTo(AdminViewController.USERS);
	}
	
	private final void tryRemove(User user, User subject, Model model) {
		try {
			
			List<DomainAssignment> assignments = daService.selectByUserID(user.getId());
			for(DomainAssignment da : assignments) {
				daService.delete( da );
				LOGGER.info(da.getUser().getUsername() + " was deassigned from " + da.getDomain().getName());
			}

			userService.delete( user );
			String message = "User " + user.getUsername() + " was removed";
			LOGGER.info(message + " by " + subject.getUsername());
			flash(message, Severity.INFO, model);
		} catch(Exception e) {
			String message = "Unable to delete user " + user.getUsername();
			LOGGER.error(message + ": " + e.getMessage(), e);
			flash(message, Severity.ERROR, model);
		}
	}
	
	@SuppressWarnings("deprecation")
	@RequestMapping(value = CREATE, method = RequestMethod.POST)
	public String createUser(@ModelAttribute("user") UserTransferObject newUser, Model model, HttpSession session) {
		User user = newUser.asUser();
		
		Map<String, String> errors = userService.validate(user, newUser.getConfirmPassword(), true);
		if (errors.isEmpty()) {
			long subjectID = userIDOf(session);
			User subject = userService.selectById(subjectID);

			Domain domain = domainService.selectByName(newUser.getDomainName());
			List<String> roles = newUser.userRoles();
			
			assign(user, domain, roles, model);
			assignToDefault(user, domain);
			String message = user.getUsername() + " was created";
			LOGGER.info(message + " by " + subject.getUsername());
			flash(message, Severity.INFO, model);
			return redirectTo(AdminViewController.USERS);
		}
		
		model.addAttribute(AbstractController.ERRORS_MAP, errors);
		AdminViewController.setAdminUsersContent(model, userService);
		model.addAttribute("postUserAction", UsersController.CREATE);
		reset(newUser, model);
		model.addAttribute("pageName", "admin_users");
		return AbstractController.FRAME;
	}
	
	private final void assignToDefault(User user, Domain domain) {
		DomainAssignment da = daService.selectByDomainFor(user.getUsername(), DomainService.DEFAULT_DOMAIN);
		
		if (!DomainService.DEFAULT_DOMAIN.equals( domain.getName() ) && da == null) {
			userService.save( user );
			Domain defDomain = domainService.selectByName(DomainService.DEFAULT_DOMAIN);
			Role defRole = roleService.selectByName(DomainService.DEFAULT_ROLE);
			da = new DomainAssignment(user, defDomain, defRole);
			daService.save( da );
			LOGGER.info("User " + user.getUsername() + " was assigned to Domain " + defDomain.getName() + " by default.");
		}
	}

	private final void assign(User user, Domain domain, List<String> roles, Model model) {
		userService.save(user);
		DomainAssignment da = new DomainAssignment();
		
		for(String r : roles) {
			Role role = domain.roleOf( r );
			da.addUserRole(role);
		}
		da.setUser(user);
		da.setDomain(domain);
		daService.save( da );
		String message = user.getUsername() + " was assigned to domain " + domain.getName();
		LOGGER.info(message);
		flash(message, Severity.INFO, model);
	}
	
	@RequestMapping(value = DOMAINS, method = RequestMethod.GET)
	public String listDomains(@RequestParam("userID") long userID, Model model) {
		model.addAttribute("assignments", daService.selectByUserID(userID));
		return "fragments/user_role_list";
	}
	
	public static final void reset(UserTransferObject newUser, Model model) {
		newUser.setUserRoles("");
		model.addAttribute("user", newUser);
	}
}
