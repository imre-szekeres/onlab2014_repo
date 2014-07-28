package hu.bme.aut.tomeesample.web;

import hu.bme.aut.tomeesample.model.ActionType;
import hu.bme.aut.tomeesample.model.Role;
import hu.bme.aut.tomeesample.service.ActionTypeService;
import hu.bme.aut.tomeesample.service.RoleService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.faces.bean.RequestScoped;
import javax.faces.event.ValueChangeEvent;
import javax.inject.Inject;
import javax.inject.Named;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j;

/**
 * @author Gergely Várkonyi
 */
@Named
@RequestScoped
// TODO: check if logger works fine with this annotation
@Log4j
public class ActionTypeManager {

	// private static final log log = log.getlog(ActionTypeManager.class);

	@Inject
	private ActionTypeService actionTypeService;
	@Inject
	private RoleService roleService;
	private List<ActionType> actionTypeList;
	private Map<Long, Boolean> isVisible;

	@Getter
	@Setter
	private String actionTypeName = "";
	@Getter
	@Setter
	private Long selectedRoleId;

	@PostConstruct
	public void init() {
		isVisible = new HashMap<Long, Boolean>();
	}

	/**
	 * @return All {@link ActionType}
	 */
	public List<ActionType> getActionTypeList() {
		actionTypeList = actionTypeService.findAll();
		if (!actionTypeList.isEmpty()) {
			for (ActionType actionType : actionTypeList) {
				isVisible.put(actionType.getId(), false);
			}
		}
		log.debug("ActionTypes listed: " + actionTypeList.size());
		log.debug("ActionTypes visibleMap: " + isVisible);
		return actionTypeList;
	}

	/**
	 * Creates an {@link ActionType} from the attributes and saves it.
	 */
	public String addActionType() {
		try {
			// Create new actionType
			ActionType newActionType = new ActionType(actionTypeName);
			log.debug("Creating actionType: " + newActionType.getActionTypeName());
			actionTypeService.create(newActionType);
		} catch (Exception e) {
			log.debug("Error while creating actionType");
			log.debug(e.getMessage());
		}
		return "/auth/admin/actionTypes.xhtml";
	}

	/**
	 * Deletes the given {@link ActionType}
	 * 
	 * @param actionType
	 *            ActionType to delete
	 */
	public String deleteActionType(ActionType actionType) {
		try {
			actionTypeService.removeDetached(actionType);
			log.debug("Deleting actionType: " + actionType.toString());
		} catch (Exception e) {
			log.debug("Error while deleting actionType");
			log.debug(e.getMessage());
		}
		return "/auth/admin/actionTypes.xhtml";
	}

	/**
	 * Setting the given {@link ActionType}'s visibility
	 * 
	 * @param actionTypeID
	 *            Id of the actionType to hide or show
	 */
	public String showOrHide(Long actionTypeID) {
		if (isVisible.get(actionTypeID)) {
			isVisible.put(actionTypeID, false);
		} else {
			isVisible.put(actionTypeID, true);
		}
		return "/auth/man/actionTypes.xhtml";
	}

	public String deleteRoleFromActionType(Role role, ActionType actionType) {
		try {
			// Remove role
			actionType.remove(role);
			// save changes
			actionTypeService.update(actionType);
			roleService.update(role);
			log.debug("Role: " + role + " was removed from " + actionType + ".");
		} catch (Exception e) {
			log.debug("Error while deleting role from actionType");
			log.debug(e.getMessage());
		}
		return "/auth/admin/actionTypes.xhtml";
	}

	/**
	 * Adding {@link ActionType} to the selected {@link Role}
	 * 
	 * @param actionType
	 *            to add to the {@link Role}
	 */
	public String addRoleToActionType(ActionType actionType) {
		try {
			// Get selected role
			if (selectedRoleId != null) {
				Role role = roleService.findById(selectedRoleId);
				// add role to actionType
				actionType.addRole(role);
				// save changes
				actionTypeService.update(actionType);
				roleService.update(role);
				log.debug("Role: " + role + " was added to " + actionType + ".");
			}
		} catch (Exception e) {
			log.debug("Error while adding role for actionType");
			log.debug(e.getMessage());
			e.printStackTrace();
		}
		return "/auth/admin/actionTypes.xhtml";
	}

	/**
	 * Change the selected {@link Role}
	 * 
	 * @param event
	 */
	public void selectedRoleChanged(ValueChangeEvent event) {
		selectedRoleId = (Long) event.getNewValue();
	}

	/**
	 * Returns the list of {@link Role}s which connected with the given {@link ActionType}
	 * 
	 * @param actionType
	 *            related {@link ActionType}
	 * 
	 * @return list of {@link Role}s
	 */
	public List<Role> getRoleList(ActionType actionType) {
		ArrayList<Role> roles = new ArrayList<>();
		Set<Role> roleSet = actionType.getRoles();
		if (roleSet != null) {
			roles.addAll(roleSet);
		}
		return roles;
	}

}
