package hu.bme.aut.wman.controllers;

import hu.bme.aut.wman.exceptions.EntityNotDeletableException;
import hu.bme.aut.wman.model.ActionType;
import hu.bme.aut.wman.model.Role;
import hu.bme.aut.wman.service.ActionTypeService;
import hu.bme.aut.wman.service.RoleService;
import hu.bme.aut.wman.view.objects.ErrorMessageVO;

import java.util.ArrayList;
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

/**
 * @version "%I%, %G%"
 */
@Controller
public class ActionsViewController extends AbstractController {

	public static final String ACTIONS = "/actions";
	public static final String NEW_ACTION = "/new/action";
	public static final String DELETE_ACTION = "/delete/action";
	public static final String ACTION_ADD_ROLE = "/action/add/role";
	public static final String ACTION_REMOVE_ROLE = "/action/remove/role";

	@EJB(mappedName = "java:module/ActionTypeService")
	private ActionTypeService actionTypeService;
	@EJB(mappedName = "java:module/RoleService")
	private RoleService roleService;

	@RequestMapping(value = ACTIONS, method = RequestMethod.GET)
	public String actionsView(Model model, HttpServletRequest request) {

		List<ActionType> allActions = actionTypeService.selectAll();
		List<Role> allRoles = roleService.selectAll();

		Map<Long, List<Role>> rolesByActionTypeIdsAdded = new HashMap<Long, List<Role>>();
		Map<Long, List<Role>> rolesByActionTypeIdsAddable = new HashMap<Long, List<Role>>();

		for (ActionType actionType : allActions) {
			List<Role> addedActions = roleService.selectByActionType(actionType);
			rolesByActionTypeIdsAdded.put(actionType.getId(), addedActions);
			List<Role> notAddedActions = new ArrayList<Role>(allRoles);
			notAddedActions.removeAll(addedActions);
			rolesByActionTypeIdsAddable.put(actionType.getId(), notAddedActions);
		}

		model.addAttribute("actions", allActions);
		model.addAttribute("newAction", new ActionType());
		model.addAttribute("rolesOnActionsMap", rolesByActionTypeIdsAdded);
		model.addAttribute("rolesToAddMap", rolesByActionTypeIdsAddable);
		return navigateToFrame("actions", model);
	}

	@RequestMapping(value = NEW_ACTION, method = RequestMethod.POST)
	public ModelAndView postNewWorkflow(@ModelAttribute("action") ActionType action, HttpServletRequest request, Model model, RedirectAttributes redirectAttributes) {

		actionTypeService.save(action);

		return redirectToFrame(ACTIONS, redirectAttributes);
	}

	@RequestMapping(value = DELETE_ACTION, method = RequestMethod.GET)
	public ModelAndView deleteWorkflow(@RequestParam("id") Long actionId, HttpServletRequest request, Model model, RedirectAttributes redirectAttributes) {
		List<ErrorMessageVO> errors = new ArrayList<ErrorMessageVO>();

		try {
			actionTypeService.deleteById(actionId);
		} catch (EntityNotDeletableException e) {
			errors.add(new ErrorMessageVO("The workflow is not deletable.", e.getMessage()));
		}

		return redirectToFrame(ACTIONS, errors, redirectAttributes);
	}

	@RequestMapping(value = ACTION_ADD_ROLE, method = RequestMethod.POST)
	public void addRole(HttpServletRequest request, RedirectAttributes redirectAttributes) {

		Long actionId = Long.parseLong(request.getParameter("actionid"));
		Long roleId = Long.parseLong(request.getParameter("roleid"));

		Role role = roleService.selectById(roleId);
		role.addActionType(actionTypeService.selectById(actionId));
		roleService.save(role);
	}

	@RequestMapping(value = ACTION_REMOVE_ROLE, method = RequestMethod.POST)
	public void removeRole(HttpServletRequest request, RedirectAttributes redirectAttributes) {

		Long actionId = Long.parseLong(request.getParameter("actionid"));
		Long roleId = Long.parseLong(request.getParameter("roleid"));

		Role role = roleService.selectById(roleId);
		role.removeActionType(actionTypeService.selectById(actionId));
		roleService.save(role);
	}

	@Override
	public Map<String, String> getNavigationTabs() {
		Map<String, String> navMap = new HashMap<>();
		navMap.put(NAV_PREFIX + WorkflowsViewController.WORKFLOWS, "Workflows");
		navMap.put(NAV_PREFIX + WorkflowsViewController.NEW_WORKFLOW, "Create new workflow");
		navMap.put(NAV_PREFIX + ACTIONS, "Manage actions");
		return navMap;
	}

}