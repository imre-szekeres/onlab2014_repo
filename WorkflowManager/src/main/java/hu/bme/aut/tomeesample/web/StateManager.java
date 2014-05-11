package hu.bme.aut.tomeesample.web;

import hu.bme.aut.tomeesample.model.State;
import hu.bme.aut.tomeesample.model.Workflow;
import hu.bme.aut.tomeesample.service.StateService;
import hu.bme.aut.tomeesample.service.WorkflowService;

import java.util.List;
import java.util.Map;

import javax.faces.application.FacesMessage;
import javax.faces.bean.RequestScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.ValidatorException;
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
	private State state;

	private String name;
	private String description;
	// private Long workflowId;
	private State parent;

	/**
	 * @return All state
	 */
	public List<State> getStateList() {
		if (state == null) {
			stateList = stateService.findByWorkflowId(getWorkflowId());
		}
		logger.debug("States listed: " + stateList.size());
		return stateList;
	}

	/**
	 * Creates a state from the attributes and saves it.
	 */
	public String addState() {
		try {
			// Get workflow by id
			Long workflowId = getWorkflowId();
			logger.debug("Try to create new state for workflowID: " + workflowId);
			Workflow workflow = workflowService.findById(workflowId);
			logger.debug("Workflow's current states: " + workflow.getStates());
			// Create new state
			State newState = new State(name, description, parent);
			stateService.create(newState);
			logger.debug("New state created: " + newState.toString());
			// Add state to workflow
			workflow.addState(newState);
			workflowService.update(workflow);
		} catch (Exception e) {
			logger.debug("Error while creating state");
			logger.debug(e);
			e.printStackTrace();
		}
		return "states";
	}

	/**
	 * Deletes the given workflow
	 * 
	 * @param workflow
	 *            Workflow to delete
	 */
	// public String deleteWorkflow(Workflow workflow) {
	// workflowService.removeDetached(workflow);
	// return "workflows";
	// }

	public void validateName(FacesContext context, UIComponent component, Object value)
			throws ValidatorException {
		logger.debug("validating: " + value.toString());
		logger.debug("in validateName - stateService: " + stateService == null ? "null" : stateService.toString());
		if (!stateService.validateName(((String) value).trim()))
			throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_ERROR, "stateService name validating error", "stateService name validating error"));
	}

	public void validateDescription(FacesContext context, UIComponent component, Object value)
			throws ValidatorException {
		logger.debug("validating: " + value.toString());
		logger.debug("in validateName - stateService: " + stateService == null ? "null" : stateService.toString());
		if (!stateService.validateDescription(((String) value).trim()))
			throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_ERROR, "stateService description validating error", "stateService description validating error"));
	}

	public String getName() {
		return name;
	}

	public String getDescription() {
		return description;
	}

	public Long getWorkflowId() {
		FacesContext context = FacesContext.getCurrentInstance();
		Map<String, String> paramMap = context.getExternalContext().getRequestParameterMap();
		String workflowIdParam = paramMap.get("workflowId");

		return Long.valueOf(workflowIdParam);
	}

	// public void setWorkflowId(Long workflowId) {
	// this.workflowId = workflowId;
	// }

	public void setName(String name) {
		this.name = name;
	}

	public void setDescription(String description) {
		this.description = description;
	}
}
