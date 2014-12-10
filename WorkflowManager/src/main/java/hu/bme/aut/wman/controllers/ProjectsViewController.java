package hu.bme.aut.wman.controllers;

import static java.lang.String.format;
import hu.bme.aut.wman.exceptions.EntityNotDeletableException;
import hu.bme.aut.wman.model.HistoryEntryEventType;
import hu.bme.aut.wman.model.Project;
import hu.bme.aut.wman.model.User;
import hu.bme.aut.wman.model.Workflow;
import hu.bme.aut.wman.security.SecurityToken;
import hu.bme.aut.wman.service.HistoryEntryService;
import hu.bme.aut.wman.service.ProjectService;
import hu.bme.aut.wman.service.UserService;
import hu.bme.aut.wman.service.WorkflowService;
import hu.bme.aut.wman.view.Messages.Severity;
import hu.bme.aut.wman.view.objects.NewProjectVO;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.ejb.EJB;
import javax.servlet.http.HttpServletRequest;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.google.common.collect.Maps;

/**
 * @version "%I%, %G%"
 */
@Controller
public class ProjectsViewController extends AbstractController {

	public static final String PROJECTS = "/projects";
	public static final String NEW_PROJECT = "/new/project";
	public static final String REOPEN_PROJECT = "/reopen/project";
	public static final String CLOSE_PROJECT = "/close/project";
	public static final String DELETE_PROJECT = "/delete/project";

	@EJB(mappedName = "java:module/ProjectService")
	private ProjectService projectService;
	@EJB(mappedName = "java:module/WorkflowService")
	private WorkflowService workflowService;
	@EJB(mappedName="java:module/UserService")
	private UserService userService;
	@EJB(mappedName="java:module/HistoryEntryService")
	private HistoryEntryService historyService;

	@PreAuthorize("hasRole('View Project')")
	@RequestMapping(value = PROJECTS, method = RequestMethod.GET)
	public String workflowsView(@RequestParam("active") Boolean actives, Model model, HttpServletRequest request) {

		List<Entry<String, Object>> parameterList = new ArrayList<Entry<String, Object>>();
		parameterList.add(new AbstractMap.SimpleEntry<String, Object>(Project.PR_ACTIVE, actives));
		List<Project> projects = projectService.selectByParameters(parameterList);

		model.addAttribute("projects", projects);
		return navigateToFrame("projects", model);
	}

	@PreAuthorize("hasRole('Create Project')")
	@RequestMapping(value = NEW_PROJECT, method = RequestMethod.GET)
	public String newWorkflowView(Model model, HttpServletRequest request) {

		List<Workflow> workflows = workflowService.selectAll();

		Map<Long, String> worfklowNamesById = Maps.newHashMap();
		for (Workflow workflow : workflows) {
			worfklowNamesById.put(workflow.getId(), workflow.getName());
		}

		model.addAttribute("project", new NewProjectVO());
		model.addAttribute("availableWorkflows", worfklowNamesById);
		model.addAttribute("message", "Create new project");
		return navigateToFrame("new_project", model);
	}

	/* @PreAuthorize("hasPermission(#projectVO.workflowId, 'Workflow', 'Create Project')") *//* TODO: authorize */
	@RequestMapping(value = NEW_PROJECT, method = RequestMethod.POST)
	public ModelAndView postNewProject(@ModelAttribute("project") NewProjectVO projectVO, HttpServletRequest request, Model model, RedirectAttributes redirectAttributes) {
		Workflow workflow = workflowService.selectById(projectVO.getWorkflowId());

		User user = userService.selectById(((SecurityToken) request.getSession().getAttribute("subject")).getUserID());

		Project project = new Project();
		project.setName(projectVO.getName());
		project.setDescription(projectVO.getDescription());
		project.setWorkflow(workflow);
		project.setCurrentState(workflow.getInitialState());
		project.setOwner(user);
		project.setActive(true);

		// if (projectService.verify(project)) {
		projectService.save(project);
		// }

		ModelAndView view = redirectToFrame(PROJECTS, redirectAttributes);
		view.setViewName(view.getViewName() + "?active=true");
		return view;
	}

	@PreAuthorize("hasPermission(#projectId, 'Project', 'Create Project')")
	@RequestMapping(value = CLOSE_PROJECT, method = RequestMethod.GET)
	public ModelAndView close(@RequestParam("id") Long projectId, HttpServletRequest request, Model model, RedirectAttributes redirectAttributes) {

		projectService.closeById(projectId);

		User user = userService.selectById(((SecurityToken) request.getSession().getAttribute("subject")).getUserID());
		historyService.log(user.getUsername(), new Date(), HistoryEntryEventType.CLOSED_PROJECT, "closed this project", projectId);

		ModelAndView view = redirectToFrame(PROJECTS, redirectAttributes);
		view.setViewName(view.getViewName() + "?active=true");
		return view;
	}

	@PreAuthorize("hasPermission(#projectId, 'Project', 'Create Project')")
	@RequestMapping(value = REOPEN_PROJECT, method = RequestMethod.GET)
	public ModelAndView reopen(@RequestParam("id") Long projectId, HttpServletRequest request, Model model, RedirectAttributes redirectAttributes) {

		try {
			projectService.reopenById(projectId);
		} catch (Exception e) {
			flash(format("Error occurred: %s", e.getMessage()), Severity.ERROR, model); /* TODO: refine */
		}

		User user = userService.selectById(((SecurityToken) request.getSession().getAttribute("subject")).getUserID());
		historyService.log(user.getUsername(), new Date(), HistoryEntryEventType.REOPENED_PROJECT, "reopened this project", projectId);

		ModelAndView view = redirectToFrame(PROJECTS, redirectAttributes);
		view.setViewName(view.getViewName() + "?active=false");
		return view;
	}

	@PreAuthorize("hasPermission(#projectId, 'Project', 'Create Project')")
	@RequestMapping(value = DELETE_PROJECT, method = RequestMethod.GET)
	public ModelAndView deleteWorkflow(@RequestParam("id") Long projectId, HttpServletRequest request, Model model, RedirectAttributes redirectAttributes) {
		try {
			projectService.deleteById(projectId);
		} catch (EntityNotDeletableException e) {
			flash(format("The workflow is not deletable due to: %s", e.getMessage()), Severity.ERROR, model);
		}

		ModelAndView view = redirectToFrame(PROJECTS, redirectAttributes);
		view.setViewName(view.getViewName() + "?active=false");
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