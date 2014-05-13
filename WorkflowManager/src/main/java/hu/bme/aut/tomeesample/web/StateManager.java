package hu.bme.aut.tomeesample.web;

import hu.bme.aut.tomeesample.model.State;
import hu.bme.aut.tomeesample.model.Workflow;
import hu.bme.aut.tomeesample.service.StateService;
import hu.bme.aut.tomeesample.service.WorkflowService;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;
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
	private List<State> stateList;

	private String name;
	private String description;

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
		stateService.removeDetached(state);
		return createReturnString();
	}

	// public void validateName(FacesContext context, UIComponent component,
	// Object value)
	// throws ValidatorException {
	// logger.debug("validating: " + value.toString());
	// logger.debug("in validateName - stateService: " + stateService == null ?
	// "null" : stateService.toString());
	// if (!stateService.validateName(((String) value).trim()))
	// throw new ValidatorException(new
	// FacesMessage(FacesMessage.SEVERITY_ERROR,
	// "stateService name validating error",
	// "stateService name validating error"));
	// }
	//
	// public void validateDescription(FacesContext context, UIComponent
	// component, Object value)
	// throws ValidatorException {
	// logger.debug("validating: " + value.toString());
	// logger.debug("in validateName - stateService: " + stateService == null ?
	// "null" : stateService.toString());
	// if (!stateService.validateDescription(((String) value).trim()))
	// throw new ValidatorException(new
	// FacesMessage(FacesMessage.SEVERITY_ERROR,
	// "stateService description validating error",
	// "stateService description validating error"));
	// }

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

	// public void setInitial(ValueChangeEvent event) {
	// State intialState = stateService.findInitial();
	// intialState.setInitial(false);
	// stateService.update(intialState);
	//
	// }

	public String getName() {
		return name;
	}

	public String getDescription() {
		return description;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setDescription(String description) {
		this.description = description;
	}
}
