package hu.bme.aut.wman.view.objects;

import hu.bme.aut.wman.model.Project;
import hu.bme.aut.wman.model.Workflow;

import java.util.List;

public class WorkflowVO {

	private Workflow workflow;

	private List<Project> projects;

	public WorkflowVO() {
	}

	public WorkflowVO(Workflow workflow, List<Project> projects) {
		this.workflow = workflow;
		this.projects = projects;
	}

	public List<Project> getProjects() {
		return projects;
	}

	public void setProjects(List<Project> projects) {
		this.projects = projects;
	}

	public Workflow getWorkflow() {
		return workflow;
	}

	public void setWorkflow(Workflow workflow) {
		this.workflow = workflow;
	}


}
