package hu.bme.aut.wman.view.objects;

import hu.bme.aut.wman.model.ActionType;
import hu.bme.aut.wman.model.Project;

import java.util.List;

public class ProjectVO {

	private Project project;

	private List<ActionType> actions;

	public ProjectVO() {

	}

	public ProjectVO(Project project, List<ActionType> actions) {
		this.project = project;
		this.actions = actions;
	}

	public List<ActionType> getActions() {
		return actions;
	}

	public void setActions(List<ActionType> actions) {
		this.actions = actions;
	}

	public Project getProject() {
		return project;
	}

	public void setProject(Project project) {
		this.project = project;
	}


}
