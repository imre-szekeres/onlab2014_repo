package hu.bme.aut.wman.controllers;

import org.springframework.ui.Model;

/**
 * @version "%I%, %G%"
 */
// @Controller
public class AbstractController {

	public static final String FRAME = "wman_frame";

	public String navigateToFrame(String pageName, Model model) {
		return navigateTo(FRAME, pageName, model);
	}

	public String navigateTo(String to, String pageName, Model model) {
		model.addAttribute("pageName", pageName);
		return to;
	}

	public String redirectTo(String to) {
		return "redirect:" + to;
	}
}
