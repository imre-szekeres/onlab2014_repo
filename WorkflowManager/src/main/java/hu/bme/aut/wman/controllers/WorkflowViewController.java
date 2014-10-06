package hu.bme.aut.wman.controllers;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * @version "%I%, %G%"
 */
@Controller
public class WorkflowViewController extends AbstractController {

	public static final String INDEX = "/index";
	public static final String WORKFLOWS = "/workflows";
	public static final String NEW_WORKFLOW = "/new/workflow";

	@RequestMapping(value = WORKFLOWS, method = RequestMethod.GET)
	public String workflowsView(Model model, HttpServletRequest request) {

		model.addAttribute("message", "My workflows");
		return navigateToFrame("workflows", model);
	}

	@RequestMapping(value = NEW_WORKFLOW, method = RequestMethod.GET)
	public String newWorkflowView(Model model, HttpServletRequest request) {

		model.addAttribute("message", "Create new workflow");
		return navigateToFrame("new_workflow", model);
	}

	@Override
	public Map<String, String> getNavigationTabs() {
		Map<String, String> navMap = new HashMap<>();
		navMap.put(NAV_PREFIX + WORKFLOWS, "Workflows");
		navMap.put(NAV_PREFIX + NEW_WORKFLOW, "Create new workflow");
		return navMap;
	}

}