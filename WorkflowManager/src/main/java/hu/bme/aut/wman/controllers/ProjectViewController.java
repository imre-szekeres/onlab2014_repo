package hu.bme.aut.wman.controllers;

import hu.bme.aut.wman.exceptions.EntityNotDeletableException;
import hu.bme.aut.wman.model.ActionType;
import hu.bme.aut.wman.model.HistoryEntryEventType;
import hu.bme.aut.wman.model.Project;
import hu.bme.aut.wman.model.State;
import hu.bme.aut.wman.model.Transition;
import hu.bme.aut.wman.model.User;
import hu.bme.aut.wman.security.SecurityToken;
import hu.bme.aut.wman.service.ActionTypeService;
import hu.bme.aut.wman.service.HistoryEntryService;
import hu.bme.aut.wman.service.ProjectService;
import hu.bme.aut.wman.service.TransitionService;
import hu.bme.aut.wman.service.UserService;
import hu.bme.aut.wman.view.objects.FileUploadVO;
import hu.bme.aut.wman.view.objects.NewProjectVO;
import hu.bme.aut.wman.view.objects.StringWrapperVO;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.EJB;
import javax.servlet.http.HttpServletRequest;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
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
	public static final String COMMENT_ON_PROJECT = "/project/comment";
	public static final String DO_ACTION = "/do/action";
	public static final String SAVE_PROJECT = "/save/project";
	public static final String ASSIGN_USER = "/assign/user";
	public static final String UNASSIGN_USER = "/unassign/user";

	@EJB(mappedName = "java:module/ProjectService")
	private ProjectService projectService;
	@EJB(mappedName = "java:module/TransitionService")
	private TransitionService transitionService;
	@EJB(mappedName="java:module/UserService")
	private UserService userService;
	@EJB(mappedName="java:module/HistoryEntryService")
	private HistoryEntryService historyService;
	@EJB(mappedName="java:module/ActionTypeService")
	private ActionTypeService actionService;

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

		List<User> assignedUsers = userService.selectUsersForProject(projectId);
		// TODO in the projects domain !
		List<User> assignableUsers = userService.selectAll();

		//		model.addAttribute("projectVO", new ProjectVO(project, actions));
		model.addAttribute("project", project);
		model.addAttribute("actions", actions);
		model.addAttribute("message", "Project " + project.getName());
		model.addAttribute("commentMessage", new StringWrapperVO());
		model.addAttribute("fileUploadVO", new FileUploadVO());
		model.addAttribute("assignedUsers", assignedUsers);
		model.addAttribute("assignableUsers", assignableUsers);
		return navigateToFrame("project", model);
	}

	@RequestMapping(value = COMMENT_ON_PROJECT, method = RequestMethod.POST)
	public ModelAndView comment(@RequestParam("id") Long projectId, @ModelAttribute("commentMessage") StringWrapperVO commentMessage, Model model, HttpServletRequest request, RedirectAttributes redirectAttributes) {

		User user = userService.selectById(((SecurityToken) request.getSession().getAttribute("subject")).getUserID());

		projectService.commentOnProject(projectId, user, commentMessage.getValue());

		historyService.log(user.getUsername(), new Date(), HistoryEntryEventType.COMMENTED, "commented", projectId);

		ModelAndView view = redirectToFrame("project", redirectAttributes);
		view.setViewName("redirect:/project?id=" + projectId);
		return view;
	}

	@RequestMapping(value = DO_ACTION, method = RequestMethod.GET)
	public ModelAndView doAction(@RequestParam("projectId") Long projectId, @RequestParam("actionId") Long actionId, Model model, HttpServletRequest request, RedirectAttributes redirectAttributes) {

		User user = userService.selectById(((SecurityToken) request.getSession().getAttribute("subject")).getUserID());
		ActionType action = actionService.selectById(actionId);
		Project project = projectService.selectById(projectId);
		State previousState = project.getCurrentState();

		projectService.executeAction(projectId, actionId);

		project = projectService.selectById(projectId);
		historyService.log(user.getUsername(), new Date(), HistoryEntryEventType.DONE_ACTION, "done: "+action.getActionTypeName()+" in state: "+previousState.getName(), project, project.getCurrentState().getName());

		ModelAndView view = redirectToFrame("project", redirectAttributes);
		view.setViewName("redirect:/project?id=" + projectId);
		return view;
	}

	@RequestMapping(value = SAVE_PROJECT, method = RequestMethod.POST)
	@ResponseStatus(value = HttpStatus.OK)
	public void saveProject(@RequestBody NewProjectVO project, HttpServletRequest request, Model model, RedirectAttributes redirectAttributes) {
		Long projectId = Long.parseLong(request.getParameter("id"));
		User user = userService.selectById(((SecurityToken) request.getSession().getAttribute("subject")).getUserID());

		projectService.save(projectId, project.getName(), project.getDescription());
		historyService.log(user.getUsername(), new Date(), HistoryEntryEventType.EDITED_PROJECT, "edited the project", projectId);
	}

	@RequestMapping(value = ASSIGN_USER, method = RequestMethod.GET)
	@ResponseStatus(value = HttpStatus.OK)
	public void assignUser(@RequestParam("projectId") Long projectId, @RequestParam("id") Long userId, HttpServletRequest request, Model model, RedirectAttributes redirectAttributes) {
		User user = userService.selectById(((SecurityToken) request.getSession().getAttribute("subject")).getUserID());
		try {
			projectService.assignUser(projectId, userId);
			User assignedUser = userService.selectById(userId);
			historyService.log(user.getUsername(), new Date(), HistoryEntryEventType.ASSIGNED_USER, "assigned "+ assignedUser.getUsername(), projectId);
		} catch (Exception e) {
			//TODO
		}

	}

	@RequestMapping(value = UNASSIGN_USER, method = RequestMethod.GET)
	@ResponseStatus(value = HttpStatus.OK)
	public void unassignUser(@RequestParam("projectId") Long projectId, @RequestParam("id") Long userId, HttpServletRequest request, Model model, RedirectAttributes redirectAttributes) {
		User user = userService.selectById(((SecurityToken) request.getSession().getAttribute("subject")).getUserID());
		try {
			projectService.unassignUser(projectId, userId);
		} catch (EntityNotDeletableException e) {
			//TODO
		}


		User unassignedUser = userService.selectById(userId);
		historyService.log(user.getUsername(), new Date(), HistoryEntryEventType.UNASSIGNED_USER, "unassigned "+ unassignedUser.getUsername(), projectId);
	}

	@Override
	public Map<String, String> getNavigationTabs() {
		return new HashMap<String, String>();
	}

}