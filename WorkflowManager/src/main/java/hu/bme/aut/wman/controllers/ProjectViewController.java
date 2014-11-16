package hu.bme.aut.wman.controllers;

import hu.bme.aut.wman.model.Project;
import hu.bme.aut.wman.model.Workflow;
import hu.bme.aut.wman.service.ProjectService;
import hu.bme.aut.wman.service.WorkflowService;
import hu.bme.aut.wman.view.objects.ProjectVO;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.ejb.EJB;
import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.google.common.collect.Maps;

/**
 * @version "%I%, %G%"
 */
@Controller
public class ProjectViewController extends AbstractController {

	public static final String PROJECTS = "/projects";
	public static final String NEW_PROJECT = "/new/project";
	public static final String CLOSE_PROJECT = "/close/project";

	@EJB(mappedName = "java:module/ProjectService")
	private ProjectService projectService;
	@EJB(mappedName = "java:module/WorkflowService")
	private WorkflowService workflowService;

	@RequestMapping(value = PROJECTS, method = RequestMethod.GET)
	public String workflowsView(@RequestParam("active") Boolean actives, Model model, HttpServletRequest request) {

		List<Entry<String, Object>> parameterList = new ArrayList<Entry<String, Object>>();
		parameterList.add(new AbstractMap.SimpleEntry<String, Object>(Project.PR_ACTIVE, actives));
		List<Project> projects = projectService.selectByParameters(parameterList);

		model.addAttribute("projects", projects);
		return navigateToFrame("projects", model);
	}

	@RequestMapping(value = NEW_PROJECT, method = RequestMethod.GET)
	public String newWorkflowView(Model model, HttpServletRequest request) {

		List<Workflow> workflows = workflowService.selectAll();

		Map<Long, String> worfklowNamesById = Maps.newHashMap();
		for (Workflow workflow : workflows) {
			worfklowNamesById.put(workflow.getId(), workflow.getName());
		}

		model.addAttribute("project", new ProjectVO());
		model.addAttribute("availableWorkflows", worfklowNamesById);
		model.addAttribute("message", "Create new project");
		return navigateToFrame("new_project", model);
	}

	@RequestMapping(value = NEW_PROJECT, method = RequestMethod.POST)
	public ModelAndView postNewWorkflow(@ModelAttribute("project") ProjectVO projectVO, HttpServletRequest request, Model model) {

		// project.setStates(Workflow.getBasicStates());

		// if (projectService.verify(project)) {
		projectService.save(projectVO);
		// }

		ModelAndView view = redirectToFrame(PROJECTS);
		view.setViewName(view.getViewName() + "?active=true");
		return view;
	}

	@RequestMapping(value = CLOSE_PROJECT, method = RequestMethod.GET)
	public ModelAndView delete(@RequestParam("id") Long projectId, HttpServletRequest request, Model model) {

		projectService.closeById(projectId);

		ModelAndView view = redirectToFrame(PROJECTS);
		view.setViewName(view.getViewName() + "?active=true");
		return view;
	}

	@Override
	public Map<String, String> getNavigationTabs() {
		Map<String, String> navMap = new LinkedHashMap<>();
		navMap.put(NAV_PREFIX + PROJECTS + "?active=true", "Active projects");
		navMap.put(NAV_PREFIX + PROJECTS + "?active=false", "Closed projects");
		navMap.put(NAV_PREFIX + NEW_PROJECT, "Create new project");
		return navMap;
	}

}