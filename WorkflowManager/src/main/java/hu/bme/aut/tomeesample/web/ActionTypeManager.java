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

import org.apache.log4j.Logger;

/**
 * @author Gergely Várkonyi
 */
@Named
@RequestScoped
public class ActionTypeManager {

	private static final Logger logger = Logger.getLogger(ActionTypeManager.class);

	@Inject
	private ActionTypeService actionTypeService;
	@Inject
	private RoleService roleService;
	private List<ActionType> actionTypeList;
	private Map<Long, Boolean> isVisible;

	private String actionTypeName = "";
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
		logger.debug("ActionTypes listed: " + actionTypeList.size());
		logger.debug("ActionTypes visibleMap: " + isVisible);
		return actionTypeList;
	}

	/**
	 * Creates an {@link ActionType} from the attributes and saves it.
	 */
	public String addActionType() {
		try {
			// Create new actionType
			ActionType newActionType = new ActionType(actionTypeName);
			logger.debug("Creating actionType: " + newActionType.getActionTypeName());
			actionTypeService.create(newActionType);
		} catch (Exception e) {
			logger.debug("Error while creating actionType");
			logger.debug(e.getMessage());
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
			logger.debug("Deleting actionType: " + actionType.toString());
		} catch (Exception e) {
			logger.debug("Error while deleting actionType");
			logger.debug(e.getMessage());
		}
		return "/auth/admin/actionTypes.xhtml";
	}

	/**
	 * Setting the given {@link ActionType}'s visibility 
	 * 
	 * @param actionTypeID
	 * 				Id of the actionType to hide or show
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
			actionType.removeRole(role);
			// save changes
			actionTypeService.update(actionType);
			roleService.update(role);
			logger.debug("Role: " + role + " was removed from " + actionType + ".");
		} catch (Exception e) {
			logger.debug("Error while deleting role from actionType");
			logger.debug(e.getMessage());
		}
		return "/auth/admin/actionTypes.xhtml";
	}

	/**
	 * Adding {@link ActionType} to the selected {@link Role}
	 * 
	 * @param actionType
	 * 		 to add to the {@link Role}
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
				logger.debug("Role: " + role + " was added to " + actionType + ".");
			}
		} catch (Exception e) {
			logger.debug("Error while adding role for actionType");
			logger.debug(e.getMessage());
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
	 * 			related {@link ActionType}
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

	public String getActionTypeName() {
		return actionTypeName;
	}

	public void setActionTypeName(String actionTypeName) {
		this.actionTypeName = actionTypeName;
	}

	public Long getSelectedRoleId() {
		return selectedRoleId;
	}

	public void setSelectedRoleId(Long selectedRoleId) {
		this.selectedRoleId = selectedRoleId;
	}

}
