package hu.bme.aut.wman.controllers;

import hu.bme.aut.wman.exceptions.EntityNotDeletableException;
import hu.bme.aut.wman.model.Project;
import hu.bme.aut.wman.model.User;
import hu.bme.aut.wman.model.Workflow;
import hu.bme.aut.wman.security.SecurityToken;
import hu.bme.aut.wman.service.ProjectService;
import hu.bme.aut.wman.service.UserService;
import hu.bme.aut.wman.service.WorkflowService;
import hu.bme.aut.wman.view.objects.ErrorMessageVO;
import hu.bme.aut.wman.view.objects.NewProjectVO;

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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.google.common.collect.Maps;

/**
 * @version "%I%, %G%"
 */
@Controller
public class ProjectsViewController extends AbstractController {

	public static final String PROJECTS = "/projects";
	public static final String NEW_PROJECT = "/new/project";
	public static final String CLOSE_PROJECT = "/close/project";
	public static final String DELETE_PROJECT = "/delete/project";

	@EJB(mappedName = "java:module/ProjectService")
	private ProjectService projectService;
	@EJB(mappedName = "java:module/WorkflowService")
	private WorkflowService workflowService;
	@EJB(mappedName="java:module/UserService")
	private UserService userService;

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

		model.addAttribute("project", new NewProjectVO());
		model.addAttribute("availableWorkflows", worfklowNamesById);
		model.addAttribute("message", "Create new project");
		return navigateToFrame("new_project", model);
	}

	@RequestMapping(value = NEW_PROJECT, method = RequestMethod.POST)
	public ModelAndView postNewProject(@ModelAttribute("project") NewProjectVO projectVO, HttpServletRequest request, Model model, RedirectAttributes redirectAttributes) {

		// project.setStates(Workflow.getBasicStates());

		Workflow workflow = workflowService.selectById(projectVO.getWorkflowId());

		// FIXME
		User user = null;
		try {
			user = userService.selectById(((SecurityToken) request.getSession().getAttribute("securityToken")).getUserID());
		} catch (NullPointerException e) {

		}

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

	@RequestMapping(value = CLOSE_PROJECT, method = RequestMethod.GET)
	public ModelAndView delete(@RequestParam("id") Long projectId, HttpServletRequest request, Model model, RedirectAttributes redirectAttributes) {

		projectService.closeById(projectId);

		ModelAndView view = redirectToFrame(PROJECTS, redirectAttributes);
		view.setViewName(view.getViewName() + "?active=true");
		return view;
	}

	@RequestMapping(value = DELETE_PROJECT, method = RequestMethod.GET)
	public ModelAndView deleteWorkflow(@RequestParam("id") Long projectId, HttpServletRequest request, Model model, RedirectAttributes redirectAttributes) {
		List<ErrorMessageVO> errors = new ArrayList<ErrorMessageVO>();

		try {
			projectService.deleteById(projectId);
		} catch (EntityNotDeletableException e) {
			errors.add(new ErrorMessageVO("The workflow is not deletable.", e.getMessage()));
		}

		ModelAndView view = redirectToFrame(PROJECTS, errors, redirectAttributes);
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