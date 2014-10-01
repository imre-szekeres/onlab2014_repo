package hu.bme.aut.wman.controllers;

import java.util.Map;

import org.springframework.ui.Model;

import com.google.common.collect.Maps;

/**
 * @version "%I%, %G%"
 */
public class AbstractController {

	public static final String FRAME = "wman_frame";
	public static final String NAV_PREFIX = "/WorkflowManager";

	/**
	 * Navigates to the given content page in the frame.
	 * 
	 * @param pageName
	 *            content page name, without .jsp extension
	 * @param model
	 *            the spring model
	 * @return a string, where we should navigate.
	 */
	public String navigateToFrame(String pageName, Model model) {
		model.addAttribute("navigationTabs", getNavigationTabs());
		return navigateTo(FRAME, pageName, model);
	}

	/**
	 * Navigates to the given content page with or without the frame
	 * 
	 * @param to
	 *            the frame to navigate to
	 * @param pageName
	 *            content page name, without .jsp extension
	 * @param model
	 *            the spring model
	 * @return a string, where we should navigate.
	 */
	public String navigateTo(String to, String pageName, Model model) {
		model.addAttribute("pageName", pageName);
		return to;
	}

	/**
	 * @param to
	 *            redirects here
	 * @return a string, with we should redirect
	 */
	public String redirectTo(String to) {
		return "redirect:" + to;
	}

	/**
	 * The navigation tabs in the wm_frame, you should overide it if you want any tab.
	 * 
	 * @return A map, where the keys are the links on the tabs, and the values are the display name in the tab.
	 */
	public Map<String, String> getNavigationTabs() {
		return Maps.newHashMap();
	}
}
