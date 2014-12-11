package hu.bme.aut.wman.controllers;

import static java.lang.String.format;
import hu.bme.aut.wman.exceptions.EntityNotDeletableException;
import hu.bme.aut.wman.exceptions.MessagedAccessDeniedException;
import hu.bme.aut.wman.model.ActionType;
import hu.bme.aut.wman.model.HistoryEntryEventType;
import hu.bme.aut.wman.model.Project;
import hu.bme.aut.wman.model.State;
import hu.bme.aut.wman.model.Transition;
import hu.bme.aut.wman.model.User;
import hu.bme.aut.wman.security.SecurityToken;
import hu.bme.aut.wman.service.ActionTypeService;
import hu.bme.aut.wman.service.DomainService;
import hu.bme.aut.wman.service.HistoryEntryService;
import hu.bme.aut.wman.service.ProjectService;
import hu.bme.aut.wman.service.TransitionService;
import hu.bme.aut.wman.service.UserService;
import hu.bme.aut.wman.view.Messages.Severity;
import hu.bme.aut.wman.view.objects.FileUploadVO;
import hu.bme.aut.wman.view.objects.StringWrapperVO;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
	@EJB(mappedName="java:module/DomainService")
	private DomainService domainService;

	@RequestMapping(value = PROJECT, method = RequestMethod.GET)
	@PreAuthorize("hasRole('View Project')")
	public String projectView(@RequestParam("id") Long projectId, Model model, HttpServletRequest request) {
		User user = userService.selectById(((SecurityToken) request.getSession().getAttribute("subject")).getUserID());

		Project project = projectService.selectById(projectId);
		List<User> assignedUsers = userService.selectUsersForProject(projectId);
		List<User> assignableUsers = userService.listUsersInDomain(project.getWorkflow().getDomain().getName());
		if (!assignedUsers.contains(user) && !project.getOwner().equals(user)) {
			throw new MessagedAccessDeniedException("you are not assigned to this project.");
		}

		List<Transition> transitions = transitionService.selectByParentId(project.getCurrentState().getId());
		List<ActionType> actions = Lists.transform(transitions, new Function<Transition, ActionType>() {
			@Override
			public ActionType apply(Transition input) {
				return input.getActionType();
			}
		});

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
	@PreAuthorize("hasRole('View Project')")
	public ModelAndView comment(@RequestParam("id") Long projectId, @ModelAttribute("commentMessage") StringWrapperVO commentMessage, Model model, HttpServletRequest request, RedirectAttributes redirectAttributes) {

		User user = userService.selectById(((SecurityToken) request.getSession().getAttribute("subject")).getUserID());

		projectService.commentOnProject(projectId, user, commentMessage.getValue());

		historyService.log(user.getUsername(), new Date(), HistoryEntryEventType.COMMENTED, "commented", projectId);

		ModelAndView view = redirectToFrame("project", redirectAttributes);
		view.setViewName("redirect:/project?id=" + projectId);
		return view;
	}

	@RequestMapping(value = DO_ACTION, method = RequestMethod.GET)
	@PreAuthorize("hasRole('View Project')")
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

	@RequestMapping(value = SAVE_PROJECT, method = RequestMethod.GET)
	@PreAuthorize("hasRole('View Project')")
	public void saveProject(HttpServletRequest request, Model model, RedirectAttributes redirectAttributes) {
		Long projectId = Long.parseLong(request.getParameter("id"));
		String name = request.getParameter("name");
		String description = request.getParameter("description");
		User user = userService.selectById(((SecurityToken) request.getSession().getAttribute("subject")).getUserID());

		projectService.save(projectId, name, description);
		historyService.log(user.getUsername(), new Date(), HistoryEntryEventType.EDITED_PROJECT, "edited the project", projectId);
	}

	@RequestMapping(value = ASSIGN_USER, method = RequestMethod.GET)
	@PreAuthorize("hasRole('Assign Project')")
	public void assignUser(@RequestParam("projectId") Long projectId, @RequestParam("id") Long userId, HttpServletRequest request, Model model, RedirectAttributes redirectAttributes) {
		User user = userService.selectById(((SecurityToken) request.getSession().getAttribute("subject")).getUserID());
		try {
			projectService.assignUser(projectId, userId);
			User assignedUser = userService.selectById(userId);
			historyService.log(user.getUsername(), new Date(), HistoryEntryEventType.ASSIGNED_USER, "assigned "+ assignedUser.getUsername(), projectId);
		} catch (Exception e) {
			flash(format("Error occurred: %s", e.getMessage()), Severity.ERROR, model);
		}

	}

	@RequestMapping(value = UNASSIGN_USER, method = RequestMethod.GET)
	@PreAuthorize("hasRole('Assign Project')")
	public void unassignUser(@RequestParam("projectId") Long projectId, @RequestParam("id") Long userId, HttpServletRequest request, Model model, RedirectAttributes redirectAttributes) {
		User user = userService.selectById(((SecurityToken) request.getSession().getAttribute("subject")).getUserID());
		try {
			projectService.unassignUser(projectId, userId);
		} catch (EntityNotDeletableException e) {
			flash(format("Error occurred: %s", e.getMessage()), Severity.ERROR, model);
		}


		User unassignedUser = userService.selectById(userId);
		historyService.log(user.getUsername(), new Date(), HistoryEntryEventType.UNASSIGNED_USER, "unassigned "+ unassignedUser.getUsername(), projectId);
	}

	@Override
	public Map<String, String> getNavigationTabs() {
		return new HashMap<String, String>();
	}

}