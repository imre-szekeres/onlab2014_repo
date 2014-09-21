/**
 * LoginController.java
 */
package hu.bme.aut.wman.controllers;

import hu.bme.aut.wman.model.User;
import hu.bme.aut.wman.service.UserService;

import javax.ejb.EJB;
import javax.servlet.http.HttpServletRequest;

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

	public static final String LOGIN = "/login";

	@EJB(mappedName = "java:module/UserService")
	private UserService userService;

	@RequestMapping(value = "/", method = RequestMethod.GET)
	public String home(Model model, HttpServletRequest request) {

		if (request.getSession().getAttribute("subject") != null) {
			model.addAttribute("message", "Welcome to WorkflowManager!");
			return navigateToFrame("index", model);
		}
		return redirectTo(LOGIN);
	}

	@RequestMapping(value = "/login", method = RequestMethod.GET)
	public String getLogin(Model model) {

		User subject = new User();
		model.addAttribute("subject", subject);
		return navigateTo(LOGIN, "login", model);
	}

	@RequestMapping(value = "/login", method = RequestMethod.POST)
	public String postLogin(@ModelAttribute("subject") User subject, HttpServletRequest request, Model model) {

		if (isAuthenticated(subject)) {
			request.getSession().setAttribute("subject", subject);
			return redirectTo("/");
		}
		subject.setPassword("");
		model.addAttribute("loginError", "Username or password is invalid!");
		return LOGIN;
	}

	private final boolean isAuthenticated(User subject) {

		User user = userService.selectByName(subject.getUsername());
		if (user == null) {
			return false;
		}
		return user.getPassword().equals(subject.getPassword());
	}
}