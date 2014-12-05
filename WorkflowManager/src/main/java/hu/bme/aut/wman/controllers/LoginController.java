/**
 * LoginController.java
 */
package hu.bme.aut.wman.controllers;

import hu.bme.aut.wman.handlers.UserHandlerLocal;
import hu.bme.aut.wman.model.User;
import hu.bme.aut.wman.security.SecurityToken;
import hu.bme.aut.wman.service.DomainService;
import hu.bme.aut.wman.service.UserService;

import java.util.Map;

import javax.ejb.EJB;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
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


	@SuppressWarnings("deprecation")
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

	@SuppressWarnings("deprecation")
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
	@SuppressWarnings("deprecation")
	@RequestMapping(value = LOGOUT, method = RequestMethod.GET)
	public String logout(HttpServletRequest request) {
		HttpSession session = request.getSession();
		session.setAttribute("subject", null);
		return redirectTo(LOGIN);
	}

	/**
	 * Responsible for dispatching the request to either an inner page or to the login page
	 * depending on the <code>SecurityToken</code> in the <code>HttpSession</code>.
	 * 
	 * @param session
	 * @param model
	 * @return redirect {@link String} to either to login page or the frame
	 * */
	@SuppressWarnings("deprecation")
	@RequestMapping(value = ACCESS_DENIED, method = RequestMethod.GET)
	public String accessDenied(HttpSession session, Model model) {
		if (session.getAttribute("subject") == null)
			return redirectTo(LOGIN);
		return navigateToFrame("access_denied", model);
	}

	/**
	 * Retrieves the user ID via the passed <code>HttpSession</code>.
	 * 
	 * @param session
	 * @return the user ID of the currently logged in user
	 * */
	public static final long userIDOf(HttpSession session) {
		SecurityToken token = (SecurityToken) session.getAttribute("subject");
		return (token == null) ? -1 : token.getUserID();
	}

	/**
	 * Retrieves the user ID via the passed <code>HttpServletRequest</code>.
	 * 
	 * @param request
	 * @return the user ID of the currently logged in user
	 * */
	public static final long userIDOf(HttpServletRequest request) {
		return userIDOf(request.getSession());
	}
}