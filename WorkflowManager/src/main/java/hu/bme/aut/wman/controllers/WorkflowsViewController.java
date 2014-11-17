package hu.bme.aut.wman.controllers;

import hu.bme.aut.wman.exceptions.EntityNotDeletableException;
import hu.bme.aut.wman.model.State;
import hu.bme.aut.wman.model.Workflow;
import hu.bme.aut.wman.service.ProjectService;
import hu.bme.aut.wman.service.StateService;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

/**
 * @version "%I%, %G%"
 */
@Controller
public class WorkflowsViewController extends AbstractController {

	public static final String WORKFLOWS = "/workflows";
	public static final String NEW_WORKFLOW = "/new/workflow";
	public static final String DELETE_WORKFLOW = "/delete/workflow";

	@EJB(mappedName = "java:module/WorkflowService")
	private WorkflowService workflowService;
	@EJB(mappedName = "java:module/StateService")
	private StateService stateService;
	@EJB(mappedName = "java:module/ProjectService")
	private ProjectService projectService;

	@RequestMapping(value = WORKFLOWS, method = RequestMethod.GET)
	public String workflowsView(Model model, HttpServletRequest request) {

		List<Workflow> allWorkflow = workflowService.selectAll();

		model.addAttribute("workflows", allWorkflow);
		return navigateToFrame("workflows", model);
	}

	@RequestMapping(value = NEW_WORKFLOW, method = RequestMethod.GET)
	public String newWorkflowView(Model model, HttpServletRequest request) {

		model.addAttribute("workflow", new Workflow());
		model.addAttribute("message", "Create new workflow");
		return navigateToFrame("new_workflow", model);
	}

	@RequestMapping(value = NEW_WORKFLOW, method = RequestMethod.POST)
	public ModelAndView postNewWorkflow(@ModelAttribute("workflow") Workflow workflow, HttpServletRequest request, Model model) {

		workflow.setStates(Workflow.getBasicStates());

		for (State state : workflow.getStates()) {
			state.setWorkflow(workflow);
		}

		if (workflowService.verify(workflow)) {
			workflowService.save(workflow);
		}

		return redirectToFrame(WORKFLOWS);
	}

	@RequestMapping(value = DELETE_WORKFLOW, method = RequestMethod.GET)
	public ModelAndView deleteWorkflow(@RequestParam("id") Long workflowId, HttpServletRequest request, Model model) {
		// TODO better exception handling :)
		try {
			workflowService.deleteById(workflowId);
		} catch (EntityNotDeletableException e) {
			e.printStackTrace();
		}

		return redirectToFrame(WORKFLOWS);
	}

	@Override
	public Map<String, String> getNavigationTabs() {
		Map<String, String> navMap = new HashMap<>();
		navMap.put(NAV_PREFIX + WORKFLOWS, "Workflows");
		navMap.put(NAV_PREFIX + NEW_WORKFLOW, "Create new workflow");
		navMap.put(NAV_PREFIX + ActionsViewController.ACTIONS, "Manage actions");
		return navMap;
	}

}