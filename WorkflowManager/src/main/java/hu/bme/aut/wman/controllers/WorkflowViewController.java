package hu.bme.aut.wman.controllers;

import hu.bme.aut.wman.exceptions.EntityNotDeletableException;
import hu.bme.aut.wman.model.Workflow;
import hu.bme.aut.wman.service.WorkflowService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.EJB;
import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
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

	@EJB(mappedName = "java:module/WorkflowService")
	private WorkflowService workflowService;

	@RequestMapping(value = WORKFLOWS, method = RequestMethod.GET)
	public String workflowsView(Model model, HttpServletRequest request) {

		List<Workflow> allWorkflow = workflowService.selectAll();

		model.addAttribute("workflows", allWorkflow);
		return navigateToFrame("workflows", model);
	}

	@RequestMapping(value = NEW_WORKFLOW, method = RequestMethod.GET)
	public String newWorkflowView(Model model, HttpServletRequest request) {

		model.addAttribute("message", "Create new workflow");
		return navigateToFrame("new_workflow", model);
	}

	@RequestMapping(value = NEW_WORKFLOW, method = RequestMethod.POST)
	public String postLogin(@ModelAttribute("workflow") Workflow workflow, HttpServletRequest request, Model model) {

		workflow.setStates(Workflow.getBasicStates());

		if (workflowService.verify(workflow)) {
			workflowService.save(workflow);
		}
		return navigateToFrame("workflows", model);
	}

	@RequestMapping(value = "/generate/workflow", method = RequestMethod.GET)
	public String generate(HttpServletRequest request, Model model) {
		Workflow workflow = new Workflow("TestWorkflowAgain", "A new test workflow again.");

		workflow.setStates(Workflow.getBasicStates());

		if (workflowService.verify(workflow)) {
			workflowService.save(workflow);
		}
		return navigateToFrame("workflows", model);
	}

	@RequestMapping(value = "/delete/workflow", method = RequestMethod.GET)
	public String deleteAll(HttpServletRequest request, Model model) {

		for (Workflow workflow : workflowService.selectAll()) {
			try {
				workflowService.delete(workflow);
			} catch (EntityNotDeletableException e) {
				e.printStackTrace();
			}
		}

		return navigateToFrame("workflows", model);
	}

	@Override
	public Map<String, String> getNavigationTabs() {
		Map<String, String> navMap = new HashMap<>();
		navMap.put(NAV_PREFIX + WORKFLOWS, "Workflows");
		navMap.put(NAV_PREFIX + NEW_WORKFLOW, "Create new workflow");
		return navMap;
	}

}