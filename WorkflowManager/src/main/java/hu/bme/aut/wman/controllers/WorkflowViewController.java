package hu.bme.aut.wman.controllers;

import hu.bme.aut.wman.model.Project;
import hu.bme.aut.wman.model.Workflow;
import hu.bme.aut.wman.service.ProjectService;
import hu.bme.aut.wman.service.StateService;
import hu.bme.aut.wman.service.WorkflowService;
import hu.bme.aut.wman.view.objects.WorkflowVO;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.EJB;
import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @version "%I%, %G%"
 */
@Controller
public class WorkflowViewController extends AbstractController {

	public static final String WORKFLOW = "/workflow";

	@EJB(mappedName = "java:module/WorkflowService")
	private WorkflowService workflowService;
	@EJB(mappedName = "java:module/StateService")
	private StateService stateService;
	@EJB(mappedName = "java:module/ProjectService")
	private ProjectService projectService;

	@RequestMapping(value = WORKFLOW, method = RequestMethod.GET)
	public String workflowView(@RequestParam("id") Long workflowId, Model model, HttpServletRequest request) {

		Workflow workflow = workflowService.selectById(workflowId);
		List<Project> projects = projectService.selectAllByWorkflowName(workflow.getName());

		model.addAttribute("workflowVO", new WorkflowVO(workflow, projects));
		model.addAttribute("message", "Workflow " + workflow.getName());
		return navigateToFrame("workflow", model);
	}

	@Override
	public Map<String, String> getNavigationTabs() {
		return new HashMap<String, String>();
	}

}