/**
 * LoginController.java
 */
package hu.bme.aut.wman.controllers;

import org.springframework.stereotype.Controller;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
/**
 * 
 * @author Imre Szekeres
 * @version "%I%, %G%"
 */
@Controller
public class LoginController {

	@RequestMapping("/")
	public String home(Model model) {
		
		model.addAttribute("pageName", "index");
		model.addAttribute("message", "Welcome to WorkflowManager!");
		return "wman_frame";
	}
}
