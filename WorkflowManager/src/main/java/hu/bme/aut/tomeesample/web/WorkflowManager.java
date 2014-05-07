package hu.bme.aut.tomeesample.web;

import hu.bme.aut.tomeesample.model.Workflow;
import hu.bme.aut.tomeesample.service.WorkflowService;

import java.util.List;

import javax.faces.bean.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;

/**
 * @author Gergely Várkonyi
 */
@Named
@RequestScoped
public class WorkflowManager {

	@Inject
	WorkflowService workflowService;
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
		return workflowList;
	}

	/**
	 * Creates a workflow from the name and description and saves it.
	 * 
	 * @param name
	 *            The name of the workflow
	 * @param description
	 *            The description of the workflow
	 */
	public String addWorkflow() {
		Workflow newWorkflow = new Workflow(name, description);
		workflowService.create(newWorkflow);
		return "workflows";
	}

	/**
	 * Deletes the given workflow
	 * 
	 * @param workflow
	 *            Workflow to delete
	 */
	public String deleteWorkflow(Workflow workflow) {
		workflowService.removeDetached(workflow);
		return "workflows";
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
