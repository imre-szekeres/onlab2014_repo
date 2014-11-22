package hu.bme.aut.wman.controllers;

import hu.bme.aut.wman.model.ActionType;
import hu.bme.aut.wman.model.Project;
import hu.bme.aut.wman.model.Transition;
import hu.bme.aut.wman.service.ProjectService;
import hu.bme.aut.wman.service.TransitionService;

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

import com.google.common.base.Function;
import com.google.common.collect.Lists;

/**
 * @version "%I%, %G%"
 */
@Controller
public class ProjectViewController extends AbstractController {

	public static final String PROJECT = "/project";

	@EJB(mappedName = "java:module/ProjectService")
	private ProjectService projectService;
	@EJB(mappedName = "java:module/TransitionService")
	private TransitionService transitionService;

	@RequestMapping(value = PROJECT, method = RequestMethod.GET)
	public String workflowsView(@RequestParam("id") Long projectId, Model model, HttpServletRequest request) {

		Project project = projectService.selectById(projectId);

		List<Transition> transitions = transitionService.selectByParentId(project.getCurrentState().getId());
		List<ActionType> actions = Lists.transform(transitions, new Function<Transition, ActionType>() {
			@Override
			public ActionType apply(Transition input) {
				return input.getActionType();
			}
		});

		//		model.addAttribute("projectVO", new ProjectVO(project, actions));
		model.addAttribute("project", project);
		model.addAttribute("actions", actions);
		model.addAttribute("message", "Project " + project.getName());
		return navigateToFrame("project", model);
	}

	@Override
	public Map<String, String> getNavigationTabs() {
		return new HashMap<String, String>();
	}

}