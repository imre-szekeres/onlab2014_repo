package hu.bme.aut.tomeesample.web;

import hu.bme.aut.tomeesample.model.Workflow;
import hu.bme.aut.tomeesample.service.WorkflowService;

import java.util.List;

import javax.enterprise.context.RequestScoped;
import javax.faces.bean.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

/**
 * @author Gergely Várkonyi
 */
@Named
@ViewScoped
public class WorkflowManager {

	@Inject
	WorkflowService workflowService;
	private List<Workflow> workflowList;

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
	 * @return The created workflow
	 */
	public Workflow addWorkflow() {
		Workflow newWorkflow = new Workflow(name, description);
		workflowService.create(newWorkflow);
		return newWorkflow;
	}

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
