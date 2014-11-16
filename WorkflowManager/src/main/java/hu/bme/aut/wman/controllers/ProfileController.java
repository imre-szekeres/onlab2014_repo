/**
 * ProfileController.java
 */
package hu.bme.aut.wman.controllers;

import hu.bme.aut.wman.security.SecurityToken;
import hu.bme.aut.wman.service.UserService;

import javax.ejb.EJB;
import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @author Imre Szekeres
 * @version "%I%, %G%"
 */
@Controller
public class ProfileController extends AbstractController {

	@EJB(mappedName="java:module/UserService")
	private UserService userService;
	
	@RequestMapping(value = "/users/profile", method = RequestMethod.GET)
	public String getProfile(HttpServletRequest request, Model model, @RequestParam(value = "id", defaultValue = "-1") long id) {
		long userID = (id == -1) ? ((SecurityToken) request.getSession().getAttribute("securityToken")).getUserID() : id;
		model.addAttribute("user", userService.selectById(userID));
		return navigateToFrame("user_profile", model);
	}
}
