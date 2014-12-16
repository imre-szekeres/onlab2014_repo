package hu.bme.aut.wman.controllers;

import static java.lang.String.format;
import hu.bme.aut.wman.exceptions.EntityNotDeletableException;
import hu.bme.aut.wman.model.ActionType;
import hu.bme.aut.wman.model.Domain;
import hu.bme.aut.wman.model.Project;
import hu.bme.aut.wman.model.State;
import hu.bme.aut.wman.model.User;
import hu.bme.aut.wman.model.Workflow;
import hu.bme.aut.wman.model.graph.GraphNode;
import hu.bme.aut.wman.model.graph.StateGraph;
import hu.bme.aut.wman.security.SecurityToken;
import hu.bme.aut.wman.service.ActionTypeService;
import hu.bme.aut.wman.service.DomainService;
import hu.bme.aut.wman.service.GraphNodeService;
import hu.bme.aut.wman.service.ProjectService;
import hu.bme.aut.wman.service.StateGraphService;
import hu.bme.aut.wman.service.StateService;
import hu.bme.aut.wman.service.TransitionService;
import hu.bme.aut.wman.service.UserService;
import hu.bme.aut.wman.service.WorkflowService;
import hu.bme.aut.wman.view.Messages.Severity;
import hu.bme.aut.wman.view.objects.NewTransitionVO;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.EJB;
import javax.servlet.http.HttpServletRequest;

import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

/**
 * @version "%I%, %G%"
 */
@Controller
public class WorkflowViewController extends AbstractController {

	public static final String WORKFLOW = "/workflow";
	public static final String STATE_GRAPH = "/workflow/stategraph";
	public static final String SAVE_STATE = "/new/state";
	public static final String NEW_TRANSITION = "/new/transition";
	public static final String DELETE_STATE = "/delete/state";
	public static final String DELETE_TRANSITION = "/delete/transition";
	public static final String INITIAL_STATE = "/initial/state";
	public static final String SAVE_WORKFLOW = "/save/workflow";

	@EJB(mappedName = "java:module/WorkflowService")
	private WorkflowService workflowService;
	@EJB(mappedName = "java:module/StateService")
	private StateService stateService;
	@EJB(mappedName = "java:module/ProjectService")
	private ProjectService projectService;
	@EJB(mappedName = "java:module/StateGraphService")
	private StateGraphService graphService;
	@EJB(mappedName = "java:module/GraphNodeService")
	private GraphNodeService nodeService;
	@EJB(mappedName = "java:module/TransitionService")
	private TransitionService transitionService;
	@EJB(mappedName = "java:module/ActionTypeService")
	private ActionTypeService actionService;
	@EJB(mappedName="java:module/UserService")
	private UserService userService;
	@EJB(mappedName="java:module/DomainService")
	private DomainService domainService;

	@RequestMapping(value = WORKFLOW, method = RequestMethod.GET)
	@PreAuthorize("hasRole('View Workflow')")
	public String workflowView(@RequestParam("id") Long workflowId, Model model, HttpServletRequest request) {
		User user = userService.selectById(((SecurityToken) request.getSession().getAttribute("subject")).getUserID());
		List<Domain> domainsWithViewPriv = domainService.domainsOf(user.getId(), Lists.newArrayList("View Workflow"));

		Workflow workflow = workflowService.selectById(workflowId);
		List<Project> projects = projectService.selectAllByWorkflowName(workflow.getName());

		List<ActionType> actions = actionService.selectAll();
		List<ActionType> availableActions = new ArrayList<ActionType>();
		for (ActionType action : actions) {
			if (domainsWithViewPriv.contains(action.getDomain())) {
				availableActions.add(action);
			}
		}
		Map<Long, String> actionNamesById = Maps.newHashMap();
		for (ActionType action : availableActions) {
			actionNamesById.put(action.getId(), action.getActionTypeName());
		}

		//		model.addAttribute("workflowVO", new WorkflowVO(workflow, projects));
		model.addAttribute("workflow", workflow);
		model.addAttribute("projects", projects);
		model.addAttribute("actionMap", actionNamesById);
		model.addAttribute("newState", new State());
		model.addAttribute("newTransition", new NewTransitionVO());
		model.addAttribute("message", "Workflow " + workflow.getName());
		return navigateToFrame("workflow", model);
	}

	@RequestMapping(value = STATE_GRAPH, method = RequestMethod.GET)
	@ResponseStatus(value = HttpStatus.OK)
	@PreAuthorize("hasRole('View Workflow')")
	public @ResponseBody StateGraph getGraph(@RequestParam("id") Long workflowId, Model model, HttpServletRequest request) {
		StateGraph graph = graphService.selectByWorkflowId(workflowId).get(0);
		return graph;
	}

	@RequestMapping(value = SAVE_STATE, method = RequestMethod.POST)
	@PreAuthorize("hasRole('Create Workflow')")
	public ModelAndView postNewState(@ModelAttribute("newState") State newState, @RequestParam("workflowId") Long workflowId, @RequestParam("stateId") Long stateId, HttpServletRequest request, Model model, RedirectAttributes redirectAttributes) {
		//		newState.setInitial(false);
		if (stateId == -1) {
			stateService.saveNew(newState, workflowId);
		} else {
			State state = stateService.selectById(stateId);
			state.setDescription(newState.getDescription());
			state.setName(newState.getName());
			stateService.save(state);
		}

		ModelAndView view = redirectToFrame("workflow", redirectAttributes);
		view.setViewName("redirect:/workflow?id=" + workflowId);
		return view;
	}

	@RequestMapping(value = INITIAL_STATE, method = RequestMethod.GET)
	@PreAuthorize("hasRole('Create Workflow')")
	public ModelAndView setInitialState(@RequestParam("nodeId") Long nodeId, @RequestParam("workflowId") Long workflowId, HttpServletRequest request, Model model, RedirectAttributes redirectAttributes) {
		Workflow workflow = workflowService.selectById(workflowId);
		State oldInitState = workflow.getInitialState();
		oldInitState.setInitial(false);
		GraphNode oldInitNode = nodeService.selectByStateId(oldInitState.getId());
		oldInitNode.setInitial(false);

		State newInitState = null;
		Long stateId = graphService.getStateIdOfNode(nodeId);
		for (State state : workflow.getStates()) {
			if(state.getId().equals(stateId)) {
				newInitState = state;
				break;
			}
		}
		newInitState.setInitial(true);
		GraphNode newInitNode = nodeService.selectById(nodeId);
		newInitNode.setInitial(true);

		stateService.save(oldInitState);
		stateService.save(newInitState);
		nodeService.save(oldInitNode);
		nodeService.save(newInitNode);

		ModelAndView view = redirectToFrame("workflow", redirectAttributes);
		view.setViewName("redirect:/workflow?id=" + workflowId);
		return view;
	}

	@RequestMapping(value = NEW_TRANSITION, method = RequestMethod.POST)
	@PreAuthorize("hasRole('Create Workflow')")
	public ModelAndView postNewTransition(@ModelAttribute("newTransition") NewTransitionVO newTransitionVO, @RequestParam("from") Long fromId, @RequestParam("to") Long toId,
			HttpServletRequest request, Model model, RedirectAttributes redirectAttributes) {

		try {
			transitionService.save(newTransitionVO, fromId, toId);
		} catch (Exception e) {
			flash(format("Error occurred: %s", e.getMessage()), Severity.ERROR, model);
		}

		ModelAndView view = redirectToFrame("workflow", redirectAttributes);
		view.setViewName("redirect:/workflow?id=" + newTransitionVO.getWorkflowId());
		return view;
	}

	@RequestMapping(value = DELETE_STATE, method = RequestMethod.GET)
	@PreAuthorize("hasRole('Create Workflow')")
	public ModelAndView deleteState(@RequestParam("workflowId") Long workflowId, @RequestParam("nodeId") Long nodeId, HttpServletRequest request, Model model, RedirectAttributes redirectAttributes) {
		Long stateId = graphService.getStateIdOfNode(nodeId);

		try {
			workflowService.removeState(workflowId, stateId);
			graphService.deleteNode(nodeId);
		} catch (EntityNotDeletableException e) {
			flash(format("The State is not deletable due to: %s", e.getMessage()), Severity.ERROR, model);
		}

		ModelAndView view = redirectToFrame("workflow", redirectAttributes);
		view.setViewName("redirect:/workflow?id=" + workflowId);
		return view;
	}

	@RequestMapping(value = DELETE_TRANSITION, method = RequestMethod.GET)
	@ResponseStatus(value = HttpStatus.OK)
	@PreAuthorize("hasRole('Create Workflow')")
	public void deleteTransition(HttpServletRequest request, Model model, RedirectAttributes redirectAttributes) {
		Long edgeId = Long.parseLong(request.getParameter("edgeId"));
		Long transitionId = graphService.getTransitionIdOfEdge(edgeId);

		try {
			transitionService.deleteById(transitionId);
			graphService.deleteEdge(edgeId);
		} catch (EntityNotDeletableException e) {
			flash(format("The State is not deletable due to: %s", e.getMessage()), Severity.ERROR, model);
		}
	}

	@RequestMapping(value = SAVE_WORKFLOW, method = RequestMethod.GET)
	@ResponseStatus(value = HttpStatus.OK)
	@PreAuthorize("hasRole('Create Workflow')")
	public void saveWorkflow(HttpServletRequest request, Model model, RedirectAttributes redirectAttributes) {
		Long workflowId = Long.parseLong(request.getParameter("id"));
		String name = request.getParameter("name");
		String description = request.getParameter("description");

		workflowService.save(workflowId, name, description);
	}

	@Override
	public Map<String, String> getNavigationTabs() {
		return new HashMap<String, String>();
	}

}