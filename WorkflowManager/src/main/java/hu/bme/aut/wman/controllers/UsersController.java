/**
 * UsersController.java
 */
package hu.bme.aut.wman.controllers;

import static hu.bme.aut.wman.controllers.LoginController.refreshTokens;
import static hu.bme.aut.wman.controllers.LoginController.userIDOf;
import static hu.bme.aut.wman.utils.StringUtils.asString;
import static java.lang.String.format;
import hu.bme.aut.wman.exceptions.EntityNotDeletableException;
import hu.bme.aut.wman.managers.DomainManager;
import hu.bme.aut.wman.managers.UserManager;
import hu.bme.aut.wman.model.Domain;
import hu.bme.aut.wman.model.User;
import hu.bme.aut.wman.service.DomainAssignmentService;
import hu.bme.aut.wman.service.DomainService;
import hu.bme.aut.wman.service.PrivilegeService;
import hu.bme.aut.wman.service.RoleService;
import hu.bme.aut.wman.service.UserService;
import hu.bme.aut.wman.utils.parsers.JsonParser;
import hu.bme.aut.wman.view.DroppableName;
import hu.bme.aut.wman.view.Messages.Severity;
import hu.bme.aut.wman.view.objects.transfer.UserTransferObject;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.EJB;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.springframework.security.access.prepost.PostFilter;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Handles <code>HttpServletRequest</code>s regarding the management of <code>User</code> life cycle.
 * 
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


	@EJB(mappedName = "java:module/DomainManager")
	private DomainManager domainManager;
	@EJB(mappedName = "java:module/UserManager")
	private UserManager userManager;

	@EJB(mappedName = "java:module/PrivilegeService")
	private PrivilegeService privilegeService;
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



	/**
	 * Handles the navigation to the profile page of the <code>User</code> specified by its id 
	 * and data setting required by it.
	 * 
	 * @param userID
	 * @param model
	 * @param session
	 * @return the name of the View that renders the Html response
	 * */
	@RequestMapping(value = PROFILE, method = RequestMethod.GET)
	public String viewProfile(@RequestParam("user") Long userID, Model model, HttpSession session) {
		User user = userService.selectById(userID);
		setProfileAttributes(user, model, session);
		return navigateToFrame("user_profile", model);
	}

	/**
	 * Pre-sets the required values to display the profile page for the given <code>User</code>.
	 * 
	 * @param user
	 * @param model
	 * @param session
	 * */
	public static void setProfileAttributes(User user, Model model, HttpSession session) {
		model.addAttribute("user", user);
		boolean isEditable = user.getId().equals( userIDOf(session) );
		model.addAttribute("isEditable", isEditable);
		model.addAttribute("viewProjectAction", ProjectViewController.PROJECT);
		model.addAttribute("selectDNRTable", UsersController.DOMAINS_AND_ROLES);
		model.addAttribute("selectDetailsForm", UsersController.UPDATE_DETAILS_FORM);
	}

	/**
	 * Provides the parameters required by the view to build an HTML form for creating a <code>User</code>.
	 * 
	 * @param model
	 * @param session
	 * @return a name that identifies the View
	 * */
	@PreAuthorize("hasRole('Create User') and hasRole('Assign User') and hasRole('Assign Role')")
	@RequestMapping(value = CREATE_FORM, method = RequestMethod.GET)
	public String requestCreateForm(Model model, HttpSession session) {
		UserTransferObject user = new UserTransferObject();
		user.setUserRoles("{}");
		setFormAttributes( user, userIDOf(session), domainService, UsersController.CREATE, "create", 
				           Arrays.asList(new String[] {"Assign User", "Assign Role", "Create User"}), 
				           model );
		return "fragments/user_form_modal";
	}

	/**
	 * Provides the parameters required by the view to build an HTML form for updating the <code>DomainAssignment</code>s 
	 * and <code>Role</code>s associated with a given <code>User</code>.
	 * 
	 * @param userID
	 * @param model
	 * @param session
	 * @return a name that identifies the View
	 * */
	@PreAuthorize("hasPermission(#userID, 'User', 'Assign User') and hasPermission(#userID, 'User', 'Assign Role')")
	@RequestMapping(value = UPDATE_FORM, method = RequestMethod.GET)
	public String requestUpdateForm(@RequestParam(value = "user", defaultValue = "-1") Long userID, Model model, HttpSession session) {
		UserTransferObject user = new UserTransferObject(userService.selectById(userID));
		Map<String, List<String>> assignments = daService.assignmentsOf(userID);
		user.setUserRoles(tryStringifyAssignments(assignments, parser));
		setFormAttributes( user, userIDOf(session), domainService, UsersController.UPDATE, "update",
				           Arrays.asList(new String[] {"Assign User", "Assign Role"}), 
				           model );
		return "fragments/user_form_modal";
	}

	/**
	 * Sets the <code>Model</code> attributes required by the forms used to represent <code>User</code> data for
	 * either creation or update operations.
	 * 
	 * @param user
	 * @param subjectID
	 * @param domainService
	 * @param postUserAction
	 * @param formType
	 * @param authorities
	 * @param model
	 * */
	public static final void setFormAttributes(UserTransferObject user, Long subjectID, DomainService domainService, String postUserAction, String formType, Collection<? extends String> authorities, Model model) {
		model.addAttribute("user", user);
		model.addAttribute("postUserAction", postUserAction);
		List<String> domainNames = domainService.domainNamesOf(subjectID, authorities);
		model.addAttribute("domains", DroppableName.namesOf(domainNames, ""));
		model.addAttribute("formType", formType);
	}
	
	/**
	 * Stringifies the given <code>Map</code> into a JSON <code>String</code>.
	 * 
	 * @param assignments
	 * @param parser
	 * @param model
	 * @return the {@link Map} containing the values
	 * */
	private static String tryStringifyAssignments(Map<String, List<String>> assignments, JsonParser<String, List<String>> parser) {
		try {
			return parser.stringify( assignments );
		} catch(Exception e) {
			LOGGER.error("Exception while stringifying assignments Map: " + e.getMessage(), e);
		}
		return null;
	}

	/**
	 * Removes the <code>User</code> specified by its id.
	 * 
	 * @param userID
	 * @param session
	 * @param model
	 * @return a name that identifies the View
	 * */
	@PreAuthorize("hasPermission(#userID, 'User', 'Create User')")
	@RequestMapping(value = DELETE, method = RequestMethod.DELETE)
	public String deleteUser(@RequestParam("user") Long userID, HttpSession session, Model model) {		

		Long subjectID = userIDOf(session);
		
		User removed = tryRemove(userID, subjectID, model);
		if (removed != null && subjectID.equals( userID ))
			return redirectTo(LoginController.LOGOUT);

		return redirectTo(AdminViewController.USERS);
	}

	/**
	 * Attempts to delete the given <code>User</code> permanently on behalf of the other <code>User</code> specified. 
	 * It involves deleting all its <code>DomainAssignment</code>s, and with that all <code>Role</code>s assigned to it.
	 * <p>
	 * Also logs the action, the output and sets a flash message.
	 * 
	 * @param userID
	 * @param subjectID
	 * @param model
	 * @return the removed {@link User}
	 * */
	private User tryRemove(Long userID, Long subjectID, Model model) {
		try {
			
			User removed = userManager.remove(userID);
			
			User subject = userService.selectById(subjectID);
			String message = format("User %s was removed", removed);
			LOGGER.info(format("%s by %s", message, subject));
			flash(message, Severity.INFO, model);
			
			return removed;
		} catch(NullPointerException npe) {
			LOGGER.error(npe);
			flash("The User could not be found!", Severity.ERROR, model);
		} catch(Exception e) {
			String message = "Unable to delete user";
			LOGGER.error(format("%s: %s", message, e.getMessage()), e);
			flash(message, Severity.ERROR, model);
		}
		return null;
	}

	/**
	 * Responsible for conducting the creation of a <code>User</code> from the values transfered, 
	 * which includes the validation and assignment process.
	 * 
	 * @param newUser
	 * @param model
	 * @param request
	 * @param session
	 * 
	 * @return a {@link String} identifying the View
	 * */
	@PreAuthorize("hasRole('Create User') and hasRole('Assign User') and hasRole('Assign Role')")
	@RequestMapping(value = CREATE, method = RequestMethod.POST)
	public String createUser(@ModelAttribute("user") UserTransferObject newUser, Model model, HttpServletRequest request, HttpSession session) {
		User user = newUser.asUser();
		
		Long subjectID = userIDOf(session);
		Map<String, String> errors = userManager.validate(user, newUser.getConfirmPassword());
		if (errors.isEmpty()) {
			
			user = userManager.create( user );
			User subject = userService.selectById(subjectID);
			/** filters those assignments out in which the current user has no privilege to Assign User or to Assign Role */
			Map<String, List<String>> assignments = filter(tryParseAssignments(newUser.getUserRoles(), parser));
			
			if (assignments != null) {

				for(String domainName : assignments.keySet())
					tryAssign(user, domainName, assignments.get(domainName), model);

				if (!assignments.containsKey(DomainService.DEFAULT_DOMAIN))
						domainManager.assignToDefault(user);

				String message = format("%s was created", user);
				LOGGER.info(format("%s by %s", message, subject));
				flash(message, Severity.INFO, model);
			}
			else {
				LOGGER.error("Unable to process the given JSON String: " + newUser.getUserRoles());
				flash("Some errors occurred during the creation of user " + newUser.getUsername(), Severity.ERROR, model);
			}
			return redirectTo(AdminViewController.USERS);
		}
		
		model.addAttribute(AbstractController.ERRORS_MAP, errors);
		AdminViewController.setAdminUsersContent(model, subjectID, userService, Arrays.asList(new String[] {"View User"}));
		setFormAttributes( newUser, subjectID, domainService, UsersController.CREATE, "create",  
				           Arrays.asList(new String[] {"Create User", "Assign User", "Assign Role"}), 
				           model );
		model.addAttribute("pageName", "admin_users");
		return AbstractController.FRAME;
	}

	/**
	 * Updates the <code>DomainAssignment</code>s and <code>Role</code>s associated with a <code>User</code>.
	 * 
	 * @param updated
	 * @param model
	 * @param request
	 * @param session
	 * 
	 * @return a {@link String} identifying the View
	 * */
	@PreAuthorize("hasPermission(#updated.id, 'User', 'Assign User') and hasPermission(#updated.id, 'User', 'Assign Role')")
	@RequestMapping(value = UPDATE, method = RequestMethod.POST)
	public String updateUser(@ModelAttribute("user") UserTransferObject updated, Model model, HttpServletRequest request, HttpSession session) {
		User user = userService.selectById( updated.getId() );
		Long subjectID = userIDOf(session);
		User subject = userService.selectById(subjectID);
		/** only consider those that can be manipulated by the current user */
		Map<String, List<String>> assignments = filter(tryParseAssignments(updated.getUserRoles(), parser));
		
		if (assignments != null) {

			/** only remove those that can be removed by the current user */
			Map<String, Long> removables = filterRemovables( daService.selectDomainsAndIds(user.getId()) );
			
			for(String domainName : assignments.keySet()) {
				tryAssign(user, domainName, assignments.get(domainName), model);
				removables.remove( domainName );	
			}
			deassign(user, subject, removables, model);

			String message = user.getUsername() + " was updated";
			LOGGER.info(message + " by " + subject.getUsername());
			flash(message, Severity.INFO, model);
			refreshTokens(user, subject, request, privilegeService);
		}
		else {
			LOGGER.error("Unable to process the given JSON String: " + updated.getUserRoles());
			flash("Some errors occurred during the creation of user " + updated.getUsername(), Severity.ERROR, model);
		}
		return redirectTo(AdminViewController.USERS);
	}

	/**
	 * Attempts to assign the <code>User</code> to the given <code>Domain</code> with the given <code>Role</code>s.
	 * 
	 * @param user
	 * @param domainName
	 * @param roleNames
	 * @param notFound
	 * @param model
	 * */
	private final void tryAssign(User user, String domainName, List<String> roleNames, Model model) {
		try {
			Domain domain = domainService.selectByName(domainName);
			if (domain != null) {
				domainManager.assign(user, domain, roleNames);
				LOGGER.info(format("User %s was assigned to Domain %s as %s.", user, domainName, asString(roleNames)));
			}
			else 
				flash(format("Domain %s was not found.", domainName), Severity.ERROR, model);
		} catch(Exception e) {
			LOGGER.error(e.getMessage(), e);
			flash(format("Unable to assign %s to %s due to %s", user, domainName, e.getMessage()), Severity.ERROR, model);
		}
	}

	/**
	 * Filters the <code>Map</code> of assignments according to the authorities owned by the executing 
	 * <code>User</code>.
	 * 
	 * @param assignments
	 * @return the filtered {@link Map}
	 * */
	private final Map<String, List<String>> filter(Map<String, List<String>> assignments) {
		Map<String, List<String>> results = new HashMap<>();
		for(String domain : filterByPrivilege( assignments.keySet() ))
			results.put(domain, assignments.get(domain));
		return results;
	}

	/**
	 * Filters the <code>Map</code> of assignments according to the authorities owned by the executing 
	 * <code>User</code>.
	 * 
	 * @param removables
	 * @return the filtered {@link Map}
	 * */
	private final Map<String, Long> filterRemovables(Map<String, Long> removables) {
		Map<String, Long> results = new HashMap<>();
		for(String domain : filterByPrivilege( removables.keySet() ))
			results.put(domain, removables.get(domain));
		return results;
	}

	/**
	 * Filters the given <code>Collection</code> according to the <code>Privilege</code>s owned by the 
	 * executing <code>User</code>.
	 * 
	 * @param collection
	 * @return the filtered {@link Collection}
	 * */
	@PostFilter("hasPermission(filterObject, 'Domain', 'Assign User') and hasPermission(filterObject, 'Domain', 'Assign Role')")
	private final Collection<? extends String> filterByPrivilege(Collection<? extends String> collection) {
		return collection;
	}

	/**
	 * Parses the given JSON <code>String</code> into a <code>Map</code>.
	 * 
	 * @param json
	 * @param parser
	 * @param model
	 * @return the {@link Map} containing the values
	 * */
	private static Map<String, List<String>> tryParseAssignments(String json, JsonParser<String, List<String>> parser) {
		try {
			return parser.parse( json );
		} catch(Exception e) {
			LOGGER.error("Exception while parsing JSON String: " + e.getMessage(), e);
		}
		return null;
	}

	/**
	 * Attempts to remove all <code>DomainAssignment</code>s, remove the given <code>User</code> from the <code>Domain</code>s
	 * specified by their names from the database. 
	 * <p>
	 * Also logs the operation and flashes a message.
	 * 
	 * @param user
	 * @param subject
	 * @param removables
	 * @param model
	 * */
	private void deassign(User user, User subject, Map<String, Long> removables, Model model) {
		for(String domain : removables.keySet()) {
			try {
				domainManager.deassign(user, removables.get(domain));
			} catch(EntityNotDeletableException e) {
				LOGGER.error(e.getMessage(), e);
				flash(format("Unable to deassing %s from %s.", user, domain), Severity.ERROR, model);
				removables.remove(domain);
			}
		}
		String message = String.format("User %s was removed from Domains %s", user.getUsername(), asString(removables.keySet()));
		LOGGER.info(String.format("%s by %s.", message, subject.getUsername()));
		flash(message, Severity.INFO, model);
	}

	/**
	 * Retrieves the <code>DomainAssignment</code>s corresponding to the <code>User</code> specified by its id, 
	 * and returns them to be displayed as a <code>List</code> of <code>Role</code>s.
	 * 
	 * @param userID
	 * @param model
	 * 
	 * @return a {@link String} identifying the correspoding View
	 * */
	@RequestMapping(value = DOMAINS, method = RequestMethod.GET)
	public String listDomains(@RequestParam(value = "userID", defaultValue = "-1") Long userID, Model model) {
		model.addAttribute("assignments", daService.selectByUserID(userID));
		return "fragments/user_role_list";
	}

	/**
	 * Retrieves the <code>DomainAssignment</code>s corresponding to the <code>User</code> specified by its id, 
	 * and returns them to be displayed as tabular data.
	 * 
	 * @param userID
	 * @param model
	 * 
	 * @return a {@link String} identifying the corresponding View
	 * */
	@RequestMapping(value = DOMAINS_AND_ROLES, method = RequestMethod.GET)
	public String requestDomainsAndRoles(@RequestParam(value = "user", defaultValue = "-1") Long userID, Model model) {
		model.addAttribute("assignments", daService.selectByUserID(userID));
		return "fragments/domains_n_roles_table";
	}

	/**
	 * Provides the parameters required by the view to build an HTML form for updating the detailed information or the password  
	 * associated with a given <code>User</code>.
	 * 
	 * @param userID
	 * @param model
	 * 
	 * @return a {@link String} that identifies the View
	 * */
	@RequestMapping(value = UPDATE_DETAILS_FORM, method = RequestMethod.GET)
	public String requestDetailsForm(@RequestParam(value = "user", defaultValue = "-1") Long userID, Model model) {
		User user = userService.selectById(userID);
		setUpdateDetailsAttributes(new UserTransferObject( user ), model);
		return "fragments/user_details_form";
	}

	/**
	 * Updates the detailed information of a <code>User</code>
	 * 
	 * @param updated
	 * @param model
	 * @param session
	 * 
	 * @return a {@link String} that identifies the View
	 * */
	@RequestMapping(value = UPDATE_DETAILS, method = RequestMethod.POST)
	public String updateDetails(@ModelAttribute("updated") UserTransferObject updated, Model model, HttpSession session) {
		User old = userService.selectById( updated.getId() );
		updated.setPassword( old.getPassword() );

		Map<String, String> errors = userService.validate(updated, updated.getPassword());		
		if (errors.isEmpty()) {
			old.setUsername( updated.getUsername() );
			old.setEmail( updated.getEmail() );
			old.setDescription( updated.getDescription() );
			userService.save( old ); /** to preserve the collections in User */

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

	/**
	 * Updates the password of a <code>User</code>
	 * 
	 * @param updated
	 * @param model
	 * @param session
	 * 
	 * @return a {@link String} that identifies the View
	 * */
	@RequestMapping(value = UPDATE_PASSWORD, method = RequestMethod.POST) 
	public String updatePassword(@ModelAttribute("updated") UserTransferObject updated, Model model, HttpSession session) {
		User user = userService.selectById( updated.getId() );

		Map<String, String> errors = userManager.validate(
					user, updated.getOldPassword(), updated.getPassword(), updated.getConfirmPassword()
		);
		if (errors.isEmpty()) {
			
			userManager.update(user, updated.getPassword());
			String message = format("Password of %s was modified", user);
			LOGGER.info(message);
			flash(message, Severity.INFO, model);
			return redirectTo(UsersController.PROFILE + "?user=" + updated.getId());
		}
		updated.setUsername( user.getUsername() );
		updated.setEmail( user.getEmail() );
		updated.setDescription( user.getDescription() );
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
	public static void setUpdateDetailsAttributes(UserTransferObject updated, Model model) {
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
	public static void setUpdateDetailsAttributes(UserTransferObject updated, Map<String, String> errors, Model model) {
		setUpdateDetailsAttributes(updated, model);
		model.addAttribute("validationErrors", errors);
	}
}
