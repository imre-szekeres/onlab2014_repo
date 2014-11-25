package hu.bme.aut.wman.controllers;

import hu.bme.aut.wman.model.ActionType;
import hu.bme.aut.wman.model.Project;
import hu.bme.aut.wman.model.Transition;
import hu.bme.aut.wman.model.User;
import hu.bme.aut.wman.security.SecurityToken;
import hu.bme.aut.wman.service.ProjectService;
import hu.bme.aut.wman.service.TransitionService;
import hu.bme.aut.wman.service.UserService;
import hu.bme.aut.wman.view.objects.StringWrapperVO;

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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.google.common.base.Function;
import com.google.common.collect.Lists;

/**
 * @version "%I%, %G%"
 */
@Controller
public class ProjectViewController extends AbstractController {

	public static final String PROJECT = "/project";
	public static final String COMMENT_ON_PROJECT = "/comment/project";

	@EJB(mappedName = "java:module/ProjectService")
	private ProjectService projectService;
	@EJB(mappedName = "java:module/TransitionService")
	private TransitionService transitionService;
	@EJB(mappedName="java:module/UserService")
	private UserService userService;

	@RequestMapping(value = PROJECT, method = RequestMethod.GET)
	public String projectView(@RequestParam("id") Long projectId, Model model, HttpServletRequest request) {

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
		model.addAttribute("commentMessage", new StringWrapperVO());
		return navigateToFrame("project", model);
	}

	@RequestMapping(value = COMMENT_ON_PROJECT, method = RequestMethod.POST)
	public ModelAndView comment(@RequestParam("id") Long projectId, @ModelAttribute("commentMessage") StringWrapperVO commentMessage, Model model, HttpServletRequest request, RedirectAttributes redirectAttributes) {

		User user = null;
		try {
			user = userService.selectById(((SecurityToken) request.getSession().getAttribute("subject")).getUserID());
		} catch (NullPointerException e) {
			// TODO what to do? We don't have authorized user??
		}

		projectService.commentOnProject(projectId, user, commentMessage.getValue());

		ModelAndView view = redirectToFrame("project", redirectAttributes);
		view.setViewName("redirect:/project?id=" + projectId);
		return view;
	}

	@Override
	public Map<String, String> getNavigationTabs() {
		return new HashMap<String, String>();
	}

}