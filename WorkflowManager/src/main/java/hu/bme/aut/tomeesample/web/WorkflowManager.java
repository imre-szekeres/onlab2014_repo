package hu.bme.aut.tomeesample.web;

import hu.bme.aut.tomeesample.model.State;
import hu.bme.aut.tomeesample.model.Workflow;
import hu.bme.aut.tomeesample.service.StateService;
import hu.bme.aut.tomeesample.service.WorkflowService;

import java.util.List;

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
public class WorkflowManager {

	private static final Logger logger = Logger.getLogger(WorkflowManager.class);

	@Inject
	private WorkflowService workflowService;
	@Inject
	private StateService stateService;
	private List<Workflow> workflowList;
	private Workflow workflow;

	private String name = "Default Workflow";
	private String description = "This is a default workflow.";

	// public Workflow getNewWorkflow() {
	// return newWorkflow;
	// }

	/**
	 * @return All workflow
	 */
	public List<Workflow> getWorkflowList() {
		if (workflowList == null) {
			workflowList = workflowService.findAll();
		}
		logger.debug("Workflows listed: " + workflowList.size());
		return workflowList;
	}

	/**
	 * Creates a workflow from the attributes and saves it.
	 */
	public String addWorkflow() {
		try {
			// Create new workflow
			Workflow newWorkflow = new Workflow(name, description);
			workflowService.create(newWorkflow);
			logger.debug("New workflow created: " + newWorkflow.toString());
			// Add basic states
			List<State> basicStates = Workflow.getBasicStates(newWorkflow);
			for (State state : basicStates) {
				stateService.create(state);
				logger.debug("Basic state created: '" + state.getName() + "' with id: " + state.getId());
			}
			newWorkflow.addAllStates(basicStates);
			workflowService.update(newWorkflow);
			stateService.updateAll(basicStates);
			logger.debug("New workflow's states: " + newWorkflow.getStates());
		} catch (Exception e) {
			logger.debug("Error while creating workflow");
			logger.debug(e.getMessage());
			e.printStackTrace();
		}
		return "workflows";
	}

	/**
	 * Deletes the given workflow
	 * 
	 * @param workflow
	 *            Workflow to delete
	 */
	public String deleteWorkflow(Workflow workflow) {
		try {
			logger.debug("Deleting workflow: " + workflow.toString());
			workflowService.removeDetached(workflow);
		} catch (Exception e) {
			logger.debug("Error while deleting workflow");
			logger.debug(e.getMessage());
		}
		return "workflows";
	}

	public void validateName(FacesContext context, UIComponent component, Object value)
			throws ValidatorException {
		logger.debug("validating: " + value.toString());
		logger.debug("in validateName - workflowService: " + workflowService == null ? "null" : workflowService.toString());
		if (!workflowService.validateName(((String) value).trim()))
			throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_ERROR, "workflowService name validating error", "workflowService name validating error"));
	}

	public void validateDescription(FacesContext context, UIComponent component, Object value)
			throws ValidatorException {
		logger.debug("validating: " + value.toString());
		logger.debug("in validateName - workflowService: " + workflowService == null ? "null" : workflowService.toString());
		if (!workflowService.validateDescription(((String) value).trim()))
			throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_ERROR, "workflowService description validating error", "workflowService description validating error"));
	}

	public String getName() {
		return name;
	}

	public String getDescription() {
		return description;
	}

	public Workflow getWorkflow() {
		return workflow;
	}

	public void setWorkflow(Workflow workflow) {
		this.workflow = workflow;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setDescription(String description) {
		this.description = description;
	}
}
