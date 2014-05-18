package hu.bme.aut.tomeesample.web;

import hu.bme.aut.tomeesample.model.ActionType;
import hu.bme.aut.tomeesample.model.State;
import hu.bme.aut.tomeesample.model.Workflow;
import hu.bme.aut.tomeesample.service.ActionTypeService;
import hu.bme.aut.tomeesample.service.StateService;
import hu.bme.aut.tomeesample.service.WorkflowService;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ValueChangeEvent;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.log4j.Logger;

/**
 * @author Gergely Várkonyi
 */
@Named
@RequestScoped
public class StateManager {

	private static final Logger logger = Logger.getLogger(StateManager.class);

	@Inject
	StateService stateService;
	@Inject
	WorkflowService workflowService;
	@Inject
	ActionTypeService actionTypeService;
	private List<State> stateList;

	private String name;
	private String description;

	private Long selectedActionTypeId;
	private Long selectedNextStateId;

	/**
	 * @return All state
	 */
	public List<State> getStateList() {
		if (getIdParam("parentId") == null) {
			stateList = stateService.findRootStatesByWorkflowId(getIdParam("workflowId"));
		} else {
			stateList = stateService.findChildrenByParentId(getIdParam("parentId"));
			if (stateList == null)
			{
				return new ArrayList<State>();
			}
		}
		logger.debug("States listed: " + stateList.size());
		logger.debug(stateList);
		return stateList;
	}

	/**
	 * Creates a state from the attributes and saves it, then adds it to the
	 * current workflow.
	 */
	public String addState() {
		// Get parameters
		Long workflowId = getIdParam("workflowId");
		Long parentId = getIdParam("parentId");

		try {
			logger.debug("Try to create new state for workflowID: " + workflowId);
			Workflow workflow = workflowService.findById(workflowId);

			// Create new state
			State newState = new State(name, description, false);

			// Set parent if parentId is not null
			if (parentId != null) {
				State parent = null;
				try {
					parent = stateService.findById(parentId);
				} catch (Exception e) {
					System.out.println("findby");
				}
				logger.debug("Parent: " + parent.toString());
				stateService.createWithParent(parent, newState);
			} else {
				// Persist newState
				stateService.create(newState);
				logger.debug("New state created: " + newState.toString());
			}

			// Add state to workflow
			workflowService.setWorkflowToState(workflow, newState);
		} catch (Exception e) {
			logger.debug("Error while creating state");
			logger.debug(e);
			e.printStackTrace();
		}

		// Create return string
		// String returnString = "states?workflowId=" + workflowId;
		// if (parentId != null) {
		// returnString += "&parentId=" + parentId;
		// }
		return createReturnString();
	}

	/**
	 * Deletes the given state
	 * 
	 * @param state
	 *            State to delete
	 */
	public String deleteState(State state) {
		try {
			logger.debug("Deleting actionType: " + state.toString());
			stateService.removeDetached(state);
		} catch (Exception e) {
			logger.debug("Error while deleting actionType");
			logger.debug(e.getMessage());
			e.printStackTrace();
		}
		return createReturnString();
	}

	public String deleteActionTypeFromState(ActionType actionType, State state) {
		try {
			// remove actionType
			state.removeNexState(actionType);
			// save changes
			stateService.update(state);
		} catch (Exception e) {
			logger.debug("Error while deleting actionType from state");
			logger.debug(e.getMessage());
			e.printStackTrace();
		}
		return createReturnString();
	}

	public String addActionTypeToState(State state) {
		try {
			// Get selected actionType and state
			if (selectedActionTypeId != null && selectedNextStateId != null) {
				stateService.addActionTypeToState(selectedActionTypeId, selectedNextStateId, state);
			}
		} catch (Exception e) {
			logger.debug("Error while adding actionType for state");
			logger.debug(e.getMessage());
			e.printStackTrace();
		}
		return createReturnString();
	}

	public void selectedActionTypeChanged(ValueChangeEvent e) {
		selectedActionTypeId = (Long) e.getNewValue();
	}

	public void selectedNextStateChanged(ValueChangeEvent e) {
		selectedNextStateId = (Long) e.getNewValue();
	}

	public List<ActionType> getActionTypeList(State state) {
		ArrayList<ActionType> actionTypes = new ArrayList<>();
		Map<ActionType, State> stateMap = state.getNextStates();
		if (stateMap != null) {
			actionTypes.addAll(state.getNextStates().keySet());
		}
		return actionTypes;
	}

	public Long getIdParam(String paramName) {
		FacesContext context = FacesContext.getCurrentInstance();
		Map<String, String> paramMap = context.getExternalContext().getRequestParameterMap();
		String param = paramMap.get(paramName);
		if (param != null && !"".equals(param)) {
			return Long.valueOf(param);
		} else {
			return null;
		}
	}

	public boolean isInitial(State state) {
		return state.isInitial();
	}

	public String setInitial(State newInitState) {
		Long workflowId = getIdParam("workflowId");

		try {
			logger.debug("Try to get workflow with ID: " + workflowId);
			Workflow workflow = workflowService.findById(workflowId);
			State oldInitState = workflow.getInitialState();
			oldInitState.setInitial(false);
			stateService.update(oldInitState);
			newInitState.setInitial(true);
			stateService.update(newInitState);
			logger.debug("Initial state setting ended.");
		} catch (IllegalArgumentException illExc) {
			logger.warn("There was no initial state.");
			newInitState.setInitial(true);
			stateService.update(newInitState);
		} catch (Exception e) {
			logger.debug("Error while setting initial state");
			logger.debug(e.getMessage());
			e.printStackTrace();
		}
		return createReturnString();
	}

	public String createReturnString() {
		String returnString = "states?workflowId=" + getIdParam("workflowId");
		Long parentId = getIdParam("parentId");
		if (parentId != null) {
			returnString += "&parentId=" + parentId;
		}
		return returnString;
	}

	public String getTitle() {
		Workflow workflow = workflowService.findById(getIdParam("workflowId"));
		String title = workflow.getName();
		Long parentId = getIdParam("parentId");
		if (parentId != null) {
			title += ": " + parentsName(parentId);
		}
		return title;
	}

	public String parentsName(Long parentId) {
		State parent = stateService.findById(parentId);
		String parentsName = "";
		while (parent != null) {
			parentsName += parent.getName() + " ";
			parent = parent.getParent();
		}
		return parentsName;
	}

	public List<State> getStateForWorkflow() {
		if (getIdParam("workflowId") != null) {
			stateList = stateService.findRootStatesByWorkflowId(getIdParam("workflowId"));
		} else {
			stateList = new ArrayList<>();
		}
		return stateList;
	}

	public String getName() {
		return name;
	}

	public String getDescription() {
		return description;
	}

	public Long getSelectedActionTypeId() {
		return selectedActionTypeId;
	}

	public void setSelectedActionTypeId(Long selectedActionTypeId) {
		this.selectedActionTypeId = selectedActionTypeId;
	}

	public Long getSelectedNextStateId() {
		return selectedNextStateId;
	}

	public void setSelectedNextStateId(Long selectedNextStateId) {
		this.selectedNextStateId = selectedNextStateId;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setDescription(String description) {
		this.description = description;
	}
}
