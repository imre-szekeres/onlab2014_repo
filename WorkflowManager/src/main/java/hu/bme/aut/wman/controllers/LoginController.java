/**
 * LoginController.java
 */
package hu.bme.aut.wman.controllers;

import hu.bme.aut.wman.model.User;
import hu.bme.aut.wman.service.UserService;

import java.util.Map;
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


	@RequestMapping(value="/login", method=RequestMethod.POST)
	public String postLogin(@ModelAttribute("subject") User subject, HttpServletRequest request, Model model){

		User user = doAuthenticate(subject);
		if (user != null) {
			request.getSession().setAttribute("subject", user);
			return "redirect:/";
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


	@RequestMapping(value = "/register", method = RequestMethod.POST)
	public String register(@ModelAttribute("subject") User user, HttpServletRequest request, Model model) {
		Map<String, String> validationErrors = userService.validate(user, request.getParameter("password-again"), true);
		if (validationErrors.size() <= 0) {
			// TODO: userService.save(user);
			request.getSession().setAttribute("subject", user);
			// TODO: add a newbie Role to the user..
		}
		model.addAttribute("validationErrors", validationErrors);
		return redirectTo("/");
	}
}