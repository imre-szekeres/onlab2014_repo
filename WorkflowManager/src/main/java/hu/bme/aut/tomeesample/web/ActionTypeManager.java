package hu.bme.aut.tomeesample.web;

import hu.bme.aut.tomeesample.model.ActionType;
import hu.bme.aut.tomeesample.model.Role;
import hu.bme.aut.tomeesample.service.ActionTypeService;
import hu.bme.aut.tomeesample.service.RoleService;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

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

	private String actionTypeName = "";
	private Long selectedRoleId;

	/**
	 * @return All actionType
	 */
	public List<ActionType> getActionTypeList() {
		actionTypeList = actionTypeService.findAll();
		logger.debug("ActionTypes listed: " + actionTypeList.size());
		return actionTypeList;
	}

	/**
	 * Creates a actionType from the attributes and saves it.
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
			e.printStackTrace();
		}
		return "actionTypes";
	}

	/**
	 * Deletes the given actionType
	 * 
	 * @param actionType
	 *            ActionType to delete
	 */
	public String deleteActionType(ActionType actionType) {
		try {
			logger.debug("Deleting actionType: " + actionType.toString());
			actionTypeService.removeDetached(actionType);
		} catch (Exception e) {
			logger.debug("Error while deleting actionType");
			logger.debug(e.getMessage());
			e.printStackTrace();
		}
		return "actionTypes";
	}

	public String deleteRoleFromActionType(Role role, ActionType actionType) {
		try {
			// Remove role
			actionType.remove(role);
			logger.debug("Role: " + role + " was removed from " + actionType + ".");
			logger.debug(role.getActionTypes());
			logger.debug(actionType.getRoles());
			// save changes
			actionTypeService.update(actionType);
			roleService.update(role);
			logger.debug("Deleting was persisted.");
		} catch (Exception e) {
			logger.debug("Error while deleting role from actionType");
			logger.debug(e.getMessage());
			e.printStackTrace();
		}
		return "actionTypes";
	}

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
			}
		} catch (Exception e) {
			logger.debug("Error while adding role for actionType");
			logger.debug(e.getMessage());
			e.printStackTrace();
		}
		return "actionTypes";
	}

	public void selectedRoleChanged(ValueChangeEvent e) {
		selectedRoleId = (Long) e.getNewValue();
	}

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
