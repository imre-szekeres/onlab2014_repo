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
import hu.bme.aut.wman.utils.parsers.JsonParser;
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
	public static final String UPDATE_DETAILS = UPDATE + "/details";
	public static final String UPDATE_PASSWORD = UPDATE + "/passwords";
	
	public static final String UPDATE_FORM = UPDATE + "/form";
	public static final String UPDATE_DETAILS_FORM = UPDATE_FORM + "/details";
	public static final String DELETE = ROOT_URL + "/delete";
	public static final String DOMAINS = ROOT_URL + "/domains";
	public static final String PROFILE = ROOT_URL + "/profile";
	public static final String DOMAINS_AND_ROLES = PROFILE + "/dnr";


	@EJB(mappedName = "java:module/DomainAssignmentService")
	private DomainAssignmentService daService;
	@EJB(mappedName = "java:module/UserService")
	private UserService userService;
	@EJB(mappedName = "java:module/DomainService")
	private DomainService domainService;
	@EJB(mappedName = "java:module/RoleService")
	private RoleService roleService;
	@EJB(mappedName = "java:module/UserRolesParser")
	private JsonParser<String, List<String>> parser;




	@RequestMapping(value = PROFILE, method = RequestMethod.GET)
	public String viewProfile(@RequestParam("user") long userID, Model model, HttpSession session) {
		User user = userService.selectById(userID);
		setProfileAttributes(user, model, session);
		return navigateToFrame("user_profile", model);
	}
	
	public static final void setProfileAttributes(User user, Model model, HttpSession session) {
		model.addAttribute("user", user);
		boolean isEditable = (user.getId() == userIDOf(session));
		model.addAttribute("isEditable", isEditable);
		model.addAttribute("viewProjectAction", ProjectViewController.PROJECT);
		model.addAttribute("selectDNRTable", UsersController.DOMAINS_AND_ROLES);
		model.addAttribute("selectDetailsForm", UsersController.UPDATE_DETAILS_FORM);
	}

	@RequestMapping(value = CREATE_FORM, method = RequestMethod.GET)
	public String requestCreateForm(Model model) {
		UserTransferObject user = new UserTransferObject();
		user.setUserRoles("{}");
		model.addAttribute("user", user);
		model.addAttribute("postUserAction", UsersController.CREATE);
		return "fragments/user_form_modal";
	}
	
	@RequestMapping(value = UPDATE_FORM, method = RequestMethod.GET)
	public String requestUpdateForm(@RequestParam(value = "user", defaultValue = "-1") long userID, Model model) {
		UserTransferObject user = new UserTransferObject(userService.selectById(userID));
		Map<String, List<String>> assignments = daService.assignmentsOf(userID);
		user.setUserRoles(tryStringifyAssignments(assignments, parser));

		model.addAttribute("user", user);
		model.addAttribute("postUserAction", UsersController.UPDATE);
		model.addAttribute("formType", "update");
		return "fragments/user_form_modal";
	}
	
	/**
	 * Stringifies the given <code>Map</code> into a JSON <code>String</code>.
	 * 
	 * @param assignments
	 * @param parser
	 * @param model
	 * @return the {@link Map} containing the values
	 * */
	private static final String tryStringifyAssignments(Map<String, List<String>> assignments, JsonParser<String, List<String>> parser) {
		try {
			return parser.stringify( assignments );
		} catch(Exception e) {
			LOGGER.error("Exception while stringifying assignments Map: " + e.getMessage(), e);
		}
		return null;
	}
	
	@SuppressWarnings("deprecation")
	@RequestMapping(value = DELETE, method = RequestMethod.GET)
	public String deleteUser(@RequestParam("user") long userID, HttpSession session, Model model) {
		User user = userService.selectById(userID);
		
		if (user != null) {
			long subjectID = userIDOf(session);
			User subject = userService.selectById(subjectID);
			
			tryRemove(user, subject, model);
			if (user.getId() == subjectID)
				return redirectTo(LoginController.LOGOUT);
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
		
		Map<String, String> errors = userService.validate(user, newUser.getConfirmPassword());
		if (errors.isEmpty()) {
			long subjectID = userIDOf(session);
			User subject = userService.selectById(subjectID);
			Map<String, List<String>> assignments = tryParseAssignments(newUser.getUserRoles(), parser);
			
			if (assignments != null) {

				Domain domain = null;
				for(String domainName : assignments.keySet()) {
					domain = domainService.selectByName( domainName );
					assign(user, domain, assignments.get( domainName ), model);
				}
				assignToDefault(user, domain);

				String message = user.getUsername() + " was created";
				LOGGER.info(message + " by " + subject.getUsername());
				flash(message, Severity.INFO, model);
			}
			else {
				LOGGER.error("Unable to process the given JSON String: " + newUser.getUserRoles());
				flash("Some errors occurred during the creation of user " + newUser.getUsername(), Severity.ERROR, model);
			}
			return redirectTo(AdminViewController.USERS);
		}
		
		model.addAttribute(AbstractController.ERRORS_MAP, errors);
		AdminViewController.setAdminUsersContent(model, userService);
		model.addAttribute("postUserAction", UsersController.CREATE);
		reset(newUser, model);
		model.addAttribute("pageName", "admin_users");
		return AbstractController.FRAME;
	}

	/**
	 * Parses the given JSON <code>String</code> into a <code>Map</code>.
	 * 
	 * @param json
	 * @param parser
	 * @param model
	 * @return the {@link Map} containing the values
	 * */
	private static final Map<String, List<String>> tryParseAssignments(String json, JsonParser<String, List<String>> parser) {
		try {
			return parser.parse( json );
		} catch(Exception e) {
			LOGGER.error("Exception while parsing JSON String: " + e.getMessage(), e);
		}
		return null;
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
	public String listDomains(@RequestParam(value = "userID", defaultValue = "-1") long userID, Model model) {
		model.addAttribute("assignments", daService.selectByUserID(userID));
		return "fragments/user_role_list";
	}
	
	@RequestMapping(value = DOMAINS_AND_ROLES, method = RequestMethod.GET)
	public String requestDomainsAndRoles(@RequestParam(value = "user", defaultValue = "-1") long userID, Model model) {
		model.addAttribute("assignments", daService.selectByUserID(userID));
		return "fragments/domains_n_roles_table";
	}
	
	@RequestMapping(value = UPDATE_DETAILS_FORM, method = RequestMethod.GET)
	public String requestDetailsForm(@RequestParam(value = "user", defaultValue = "-1") long userID, Model model) {
		User user = userService.selectById(userID);
		setUpdateDetailsAttributes(new UserTransferObject( user ), model);
		return "fragments/user_details_form";
	}
	
	@SuppressWarnings("deprecation")
	@RequestMapping(value = UPDATE_DETAILS, method = RequestMethod.POST)
	public String updateDetails(@ModelAttribute("updated") UserTransferObject updated, Model model, HttpSession session) {
		User old = userService.selectById( updated.getId() );
		updated.setPassword( old.getPassword() );

		Map<String, String> errors = userService.validate(updated, updated.getPassword());		
		if (errors.isEmpty()) {
			old.setUsername( updated.getUsername() );
			old.setEmail( updated.getEmail() );
			old.setDescription( updated.getDescription() );
			userService.save( old ); /* to preserve the collections in User */

			String message = "User " + old.getUsername() + " was updated";
			LOGGER.info(message);
			flash(message, Severity.INFO, model);
			return redirectTo(UsersController.PROFILE + "?user=" + updated.getId());
		}
		updated.setPassword( "" );
		setUpdateDetailsAttributes(updated, errors, model);
		setProfileAttributes(old, model, session);
		return navigateToFrame("user_profile", model);
	}

	@SuppressWarnings("deprecation")
	@RequestMapping(value = UPDATE_PASSWORD, method = RequestMethod.POST)
	public String updatePassword(@ModelAttribute("updated") UserTransferObject updated, Model model, HttpSession session) {
		User user = userService.selectById( updated.getId() );

		Map<String, String> errors = userService.validate(
					user, updated.getOldPassword(), updated.getPassword(), updated.getConfirmPassword()
		);
		if (errors.isEmpty()) {
			user.setPassword( updated.getPassword() );
			userService.save( user );
			String message = "Password of user " + user.getUsername() + " was modified";
			LOGGER.info(message);
			flash(message, Severity.INFO, model);
			return redirectTo(UsersController.PROFILE + "?user=" + updated.getId());
		}
		setUpdateDetailsAttributes(updated, errors, model);
		setProfileAttributes(user, model, session);
		return navigateToFrame("user_profile", model);
	} 

	/**
	 * Sets the general attribute values in the given <code>Model</code> instance required for the view corresponding to
	 * the update mechanism(s) to render properly.
	 * 
	 * @param updated
	 * @param errors
	 * */
	public static final void setUpdateDetailsAttributes(UserTransferObject updated, Model model) {
		model.addAttribute("updated", updated);
		model.addAttribute("updateDetailsAction", UsersController.UPDATE_DETAILS);
		model.addAttribute("updatePasswordAction", UsersController.UPDATE_PASSWORD);
	}

	/**
	 * Sets the general attribute values in the given <code>Model</code> instance required for the view corresponding to
	 * the update mechanism(s) to render properly with the given collection of errors.
	 * 
	 * @param updated
	 * @param errors
	 * @param model
	 * */
	public static final void setUpdateDetailsAttributes(UserTransferObject updated, Map<String, String> errors, Model model) {
		setUpdateDetailsAttributes(updated, model);
		model.addAttribute("validationErrors", errors);
	}

	/**
	 * Resets the given <code>UserTransferObject</code> into a state where it contains no domain-role
	 * assignments and puts it into the <code>Mode</code> instance (as an attribute) passed as argument.
	 * 
	 * @param newUser
	 * @param model
	 * */
	public static final void reset(UserTransferObject newUser, Model model) {
		newUser.setUserRoles("");
		model.addAttribute("user", newUser);
	}
}
