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

	@EJB(mappedName = "java:module/UserService")
	private UserService userService;

	@EJB(mappedName = "java:module/UserHandlerImpl")
	private UserHandlerLocal userHandler;



	@SuppressWarnings("deprecation")
	@RequestMapping(value = APP_ROOT, method = RequestMethod.GET)
	public String home(Model model, HttpServletRequest request) {
		if (request.getSession().getAttribute("subject") != null) {
			model.addAttribute("message", "Welcome to WorkflowManager!");
			return navigateToFrame("index", model);
		}
		return redirectTo(LOGIN);
	}


	@RequestMapping(value = LOGIN, method = RequestMethod.GET)
	public String getLogin(Model model) {
		User subject = new User();
		model.addAttribute("subject", subject);
		return navigateTo(LOGIN, "login", model);
	}


	@SuppressWarnings("deprecation")
	@RequestMapping(value = LOGIN, method=RequestMethod.POST)
	public String postLogin(@ModelAttribute("subject") User subject, HttpServletRequest request, Model model){
		User user = doAuthenticate(subject);
		if (user != null) {
			request.getSession().setAttribute("subject", new SecurityToken(user.getId()));

			LOGGER.info("user: " + user.getUsername() + " logged in");
			return redirectTo(APP_ROOT);
		}
		subject.setPassword("");
		model.addAttribute("loginError", true);
		return "login";
	}


	private final User doAuthenticate(User subject) {
		User user = userService.selectByName(subject.getUsername());
		if(user == null || user.getPassword().equals(subject.getPassword()))
			return user;
		return null;
	}


	@SuppressWarnings("deprecation")
	@RequestMapping(value = "/register", method = RequestMethod.POST)
	public String register(@ModelAttribute("subject") User user, HttpServletRequest request, Model model) {
		Map<String, String> validationErrors = userService.validate(user, request.getParameter("password-again"), true);
		if (validationErrors.isEmpty()) {

			userHandler.createUser(user, DomainService.DEFAULT_ROLE, DomainService.DEFAULT_DOMAIN);
			request.getSession().setAttribute("subject", user);

			LOGGER.info("user: " + user.getUsername() + " registered as System Reader");
			return redirectTo(APP_ROOT);
		}
		model.addAttribute(AbstractController.ERRORS_MAP, validationErrors);
		return "login";
	}
	
	@SuppressWarnings("deprecation")
	@RequestMapping(value = LOGOUT, method = RequestMethod.GET)
	public String logout(HttpServletRequest request) {
		HttpSession session = request.getSession();
		session.setAttribute("subject", null);
		return redirectTo(APP_ROOT);
	}
}