package hu.bme.aut.wman.controllers;

import hu.bme.aut.wman.view.Messages;
import hu.bme.aut.wman.view.Messages.Severity;
import hu.bme.aut.wman.view.objects.ErrorMessageVO;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.ui.Model;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.google.common.collect.Maps;

/**
 * @version "%I%, %G%"
 */
public class AbstractController {

	public static final String FRAME = "wman_frame";
	public static final String NAV_PREFIX = "/WorkflowManager";
	public static final String ERRORS_MAP = "validationErrors";

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

	public String navigateToFrame(String pageName, Model model, List<ErrorMessageVO> errorList) {
		model.addAttribute("navigationTabs", getNavigationTabs());
		return navigateTo(FRAME, pageName, model, errorList);
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
		return navigateTo(to, pageName, model, new ArrayList<ErrorMessageVO>());
	}

	@SuppressWarnings("unchecked")
	public String navigateTo(String to, String pageName, Model model, List<ErrorMessageVO> errorList) {
		Object errorsRaw = model.asMap().get("errorList");
		List<ErrorMessageVO> errors = new ArrayList<ErrorMessageVO>();
		if (errorsRaw != null) {
			errors = (List<ErrorMessageVO>) errorsRaw;
		}
		if (!errors.isEmpty()) {
			errors.addAll(errorList);
		}

		model.addAttribute("errorList", errors);
		model.addAttribute("pageName", pageName);
		return to;
	}

	/**
	 * Redirects to the frame
	 *
	 * @return a string, with we should redirect
	 */
	public ModelAndView redirectToFrame(String pageSuffix, List<ErrorMessageVO> errorList, RedirectAttributes redirectAttributes) {
		ModelAndView modelAndView = new ModelAndView("redirect:" + pageSuffix);
		//		modelAndView.addObject("navigationTabs", getNavigationTabs());
		redirectAttributes.addFlashAttribute("errorList", errorList);
		return modelAndView;
	}

	public ModelAndView redirectToFrame(String pageSuffix, RedirectAttributes redirectAttributes) {
		return redirectToFrame(pageSuffix, new ArrayList<ErrorMessageVO>(), redirectAttributes);
	}

	/**
	 * @param to
	 *            redirects here
	 * @return a string, with we should redirect
	 */
	@Deprecated
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
	
	/**
	 * Injects a message to the response object in order to display it on the view
	 * to inform the user.
	 * 
	 * @param message
	 * @param severity
	 * @param model
	 * 
	 * @see {@link hu.bme.aut.wman.view.Messages#flash(String, Severity, Model)}
	 * */
	protected static final void flash(String message, Severity severity, Model model) {
		Messages.flash(message, severity, model);
	}
}
