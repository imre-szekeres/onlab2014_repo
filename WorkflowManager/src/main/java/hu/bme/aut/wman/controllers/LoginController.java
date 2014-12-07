/**
 * LoginController.java
 */
package hu.bme.aut.wman.controllers;

import static hu.bme.aut.wman.utils.StringUtils.asString;
import hu.bme.aut.wman.exceptions.DetailedAccessDeniedException;
import hu.bme.aut.wman.exceptions.MessagedAccessDeniedException;
import hu.bme.aut.wman.handlers.UserHandlerLocal;
import hu.bme.aut.wman.model.User;
import hu.bme.aut.wman.security.SecurityToken;
import hu.bme.aut.wman.service.DomainService;
import hu.bme.aut.wman.service.UserService;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.ejb.EJB;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 *
 * @author Imre Szekeres
 * @version "%I%, %G%"
 */
@Controller
public class LoginController extends AbstractController {

	private static final Logger LOGGER = Logger.getLogger(LoginController.class);

	public static final String APP_ROOT = "/";
	public static final String LOGIN = "/login";
	public static final String LOGOUT = "/logout";
	public static final String ACCESS_DENIED = "/403";
	

	@EJB(mappedName = "java:module/UserService")
	private UserService userService;

	@EJB(mappedName = "java:module/UserHandlerImpl")
	private UserHandlerLocal userHandler;

	@Autowired
	@Qualifier("authenticationManager")
	private AuthenticationManager authManager;
	
	@Autowired
	@Qualifier("bcryptEncoder")
	private PasswordEncoder encoder;


	@RequestMapping(value = APP_ROOT, method = RequestMethod.GET)
	public String home(Model model, HttpServletRequest request, HttpSession session) {
		if (request.getRemoteUser() == null)
			return redirectTo(LOGIN);

		if (session.getAttribute("subject") == null)
			session.setAttribute("subject", new SecurityToken(userService.selectIDOf( request.getRemoteUser() )));
		model.addAttribute("message", "Welcome to WorkflowManager!");
		return navigateToFrame("index", model);
	}


	@RequestMapping(value = LOGIN, method = RequestMethod.GET)
	public String getLogin(Model model) {
		User subject = new User();
		model.addAttribute("user", subject);
		return navigateTo(LOGIN, "login", model);
	}

	/**
	 * Handles the registration of a new <code>User</code> to the Login page; assigns the default <code>Role</code> to it in the
	 * default <code>Domain</code>.
	 * 
	 * @param user
	 * @param request
	 * @param model
	 * @return the name of the View that renders the Html response or the URL to redirect to
	 * */
	@RequestMapping(value = "/register", method = RequestMethod.POST)
	public String register(@ModelAttribute("user") User user, HttpServletRequest request, Model model) {
		Map<String, String> validationErrors = userService.validate(user, request.getParameter("password-again"));
		if (validationErrors.isEmpty()) {

			String plainPassword = user.getPassword();
			user.setPassword(encoder.encode( user.getPassword() )); /* hashes the password with BCrypt algorithm */
			userHandler.createUser(user, DomainService.DEFAULT_ROLE, DomainService.DEFAULT_DOMAIN);
			setTokensOf(user.getId(), user.getUsername(), plainPassword, request);

			LOGGER.info("user: " + user.getUsername() + " registered as " + DomainService.DEFAULT_ROLE);
			return redirectTo(APP_ROOT);
		}
		model.addAttribute(AbstractController.ERRORS_MAP, validationErrors);
		return "login";
	}

	/**
	 * Responsible for setting the <code>SecurityToken</code> and the credentials in the <code>HttpSession</code> instance
	 * for the freshly registered user.
	 * 
	 * @param id
	 * @param username
	 * @param plainPassword
	 * @param request
	 * */
	private final void setTokensOf(Long id, String username, String plainPassword, HttpServletRequest request) {
		request.getSession();
		UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(username, plainPassword);
		token.setDetails(new WebAuthenticationDetails( request ));
		Authentication auth = authManager.authenticate( token );
		request.getSession().setAttribute("subject", new SecurityToken( id ));
		
		SecurityContextHolder.getContext().setAuthentication( auth );
	}

	/**
	 * Responsible for handling the logout operation in which the <code>User</code> credentials are
	 * cleared from the given <code>HttpSession</code> accessed from the <code>HttpServletRequest</code> passed.
	 * 
	 * @param request
	 * @return redirect to the login page
	 * */
	@RequestMapping(value = LOGOUT, method = RequestMethod.GET)
	public String logout(HttpServletRequest request) {
		HttpSession session = request.getSession();
		session.setAttribute("subject", null);
		return redirectTo(LOGIN);
	}

	/**
	 * Handles any <code>DetailedAccessDeniedException</code> thrown by any part of the application, fetches the required <code>ConfigAttribute</code>s
	 * or <code>GrantedAuthority</code>s (a.k.a. <code>Privilege</code>s) to obtain access to the given page then redirects to the access denied URL.
	 * 
	 * @param request
	 * @param exception
	 * @return the redirect URL
	 * */
	@SuppressWarnings("unchecked")
	@ExceptionHandler(DetailedAccessDeniedException.class)
	public String handleDetailedAccessDenied(HttpServletRequest request, DetailedAccessDeniedException exception) {
		request.setAttribute("authoritiesRequired", exception.getRequiredAuthorities());
		LOGGER.info(String.format( "Access Denied for User %s due to lacking %s", 
				                   userIDOf(request), 
				                   asString((List<? extends ConfigAttribute>) request.getAttribute("authoritiesRequired"))) );
		return redirectTo(ACCESS_DENIED);
	}

	/**
	 * Handles any <code>MessagedAccessDeniedException</code> thrown by any part of the application, fetches the message as cause of the access denial 
     * then redirects to the access denied URL.
	 * 
	 * @param request
	 * @param exception
	 * @return the redirect URL
	 * */
	@ExceptionHandler(MessagedAccessDeniedException.class)
	public String handleMessagedAccessDenied(HttpServletRequest request, MessagedAccessDeniedException exception) {
		request.setAttribute("denialMessage", exception.getMessage());
		LOGGER.info(String.format("Access Denied for User %s due to %s", userIDOf(request), request.getAttribute("denialMessage")));
		return redirectTo(ACCESS_DENIED);
	}

	/**
	 * Responsible for dispatching the request to either an inner page or to the login page
	 * depending on the <code>SecurityToken</code> in the <code>HttpSession</code> instance.
	 * 
	 * @param session
	 * @param model
	 * @return redirect {@link String} to either to login page or the frame
	 * */
	@SuppressWarnings("unchecked")
	@RequestMapping(value = ACCESS_DENIED)
	public String accessDenied(HttpServletRequest request, HttpSession session, Model model) {
		if (session.getAttribute("subject") == null)
			return redirectTo(LOGIN);

		if (request.getAttribute("denialMessage") != null)
			model.addAttribute("denialMessage", buildDenialMessage( (String) request.getAttribute("denialMessage") ));
		
		else if (request.getAttribute("authoritiesRequired") != null)
			buildDenialMessage(userIDOf(session), (List<? extends ConfigAttribute>) request.getAttribute("authoritiesRequired"), userService, model);
		return navigateToFrame("fragments/access_denied", model);
	}

	/**
	 * Builds a simple denial message to be displayed from the simple cause of denial passed as a <code>String</code> argument.
	 * 
	 * @param denialCause
	 * @return the message to be displayed
	 * */
	public static final String buildDenialMessage(String denialCause) {
		return String.format("Sorry, you are not authorized to execute that operation, becasuse %s.", denialCause);
	}

	/**
	 * Builds a more complex denial message to be displayed from the authorities passed as a <code>List</code>.
	 * 
	 * @param subjectID
	 * @param authorities
	 * @param userService
	 * @return the message to be displayed
	 * */
	public static final void buildDenialMessage(Long subjectID, List<? extends ConfigAttribute> authorities, UserService userService, Model model) {
		model.addAttribute("detailedAccessDenied", "Sorry, you are not authorized to execute that operation.");
		model.addAttribute("personellLine", "Please contact one of the personell mentioned below to grant the required privileges.");
		model.addAttribute("personellInfo", userService.personellInfoOf(subjectID, Arrays.asList(new String[] {"Assign Role", "Assign User"})));
	}

	/**
	 * Retrieves the user ID via the passed <code>HttpSession</code>.
	 * 
	 * @param session
	 * @return the user ID of the currently logged in user
	 * */
	public static final Long userIDOf(HttpSession session) {
		SecurityToken token = (SecurityToken) session.getAttribute("subject");
		return (token == null) ? -1 : token.getUserID();
	}

	/**
	 * Retrieves the user ID via the passed <code>HttpServletRequest</code>.
	 * 
	 * @param request
	 * @return the user ID of the currently logged in user
	 * */
	public static final Long userIDOf(HttpServletRequest request) {
		return userIDOf(request.getSession());
	}
}