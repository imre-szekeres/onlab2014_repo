/**
 * UsersController.java
 */
package hu.bme.aut.wman.controllers;

import hu.bme.aut.wman.model.Domain;
import hu.bme.aut.wman.model.DomainAssignment;
import hu.bme.aut.wman.model.Role;
import hu.bme.aut.wman.model.User;
import hu.bme.aut.wman.security.SecurityToken;
import hu.bme.aut.wman.service.DomainAssignmentService;
import hu.bme.aut.wman.service.DomainService;
import hu.bme.aut.wman.service.UserService;
import hu.bme.aut.wman.view.objects.transfer.UserTransferObject;

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

/**
 * @author Imre Szekeres
 * @version "%I%, %G%"
 */
@Controller
public class UsersController extends AbstractController {

	private static final Logger LOGGER = Logger.getLogger( UsersController.class );

	public static final String ROOT_URL = "/users";
	public static final String CREATE = ROOT_URL + "/create";
	public static final String DOMAINS = ROOT_URL + "/domains";
	
	@EJB(mappedName = "java:module/DomainAssignmentService")
	private DomainAssignmentService daService;
	@EJB(mappedName = "java:module/UserService")
	private UserService userService;
	@EJB(mappedName = "java:module/DomainService")
	private DomainService domainService;

	
	@SuppressWarnings("deprecation")
	@RequestMapping(value = CREATE, method = RequestMethod.POST)
	public String createUser(@ModelAttribute("newUser") UserTransferObject newUser, Model model, HttpSession session) {
		User user = newUser.asUser();
		
		Map<String, String> errors = userService.validate(user, newUser.getConfirmPassword(), true);
		if (errors.isEmpty()) {
			SecurityToken token = (SecurityToken) session.getAttribute("subject");
			User subject = userService.selectById(token.getUserID());

			Domain domain = domainService.selectByName(newUser.getDomainName());
			List<String> roles = newUser.userRoles();
			
			assign(user, domain, roles);
			LOGGER.info(user.getUsername() + " was created by " + subject.getUsername());
			return redirectTo(AdminViewController.USERS);
		}
		
		model.addAttribute(AbstractController.ERRORS_MAP, errors);
		AdminViewController.setAdminUsersContent(model, userService);
		reset(newUser, model);
		model.addAttribute("pageName", "admin_users");
		return AbstractController.FRAME;
	}

	private final void assign(User user, Domain domain, List<String> roles) {
		userService.save(user);
		DomainAssignment da = new DomainAssignment();
		
		for(String r : roles) {
			Role role = domain.roleOf( r );
			da.addUserRole(role);
		}
		da.setUser(user);
		da.setDomain(domain);
		daService.save( da );
		LOGGER.info(user.getUsername() + " was assigned to domain " + domain.getName());
	}
	
	@RequestMapping(value = DOMAINS, method = RequestMethod.GET)
	public String listDomains(Model model, HttpServletRequest request) {
		long userID = Long.parseLong( request.getParameter("userID") );
		model.addAttribute("assignments", daService.selectByUserID(userID));
		return "fragments/user_role_list";
	}
	
	public static final void reset(UserTransferObject newUser, Model model) {
		newUser.setUserRoles("");
		model.addAttribute("newUser", newUser);
	}
}
