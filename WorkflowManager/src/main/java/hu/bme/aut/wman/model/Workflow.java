package hu.bme.aut.wman.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * Entity implementation class for Entity: Workflow
 *
 * @version "%I%, %G%"
 */
@SuppressWarnings("serial")
@Entity
public class Workflow extends AbstractEntity {

	public static final String PR_NAME = "name";
	public static final String PR_STATES = "states";
	public static final String PR_DESCRIPTION = "description";
	public static final String PR_PROJECTS = "projects";

	@NotNull
	@Size(min = 5, max = 32)
	private String name;

	@NotNull
	@Size(min = 16, max = 512)
	private String description;

	@OneToMany(mappedBy = "workflow", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	private List<State> states = new ArrayList<State>();

	//	@OneToMany(mappedBy = "workflow")
	//	private List<Project> projects = new ArrayList<Project>();

	public Workflow() {
	}

	public Workflow(String name, String description) {
		this.name = name;
		this.description = description;
	}

	public String getName() {
		return name;
	}

	public String getDescription() {
		return description;
	}

	public State getInitialState() {
		// Search the root of the state hierarchy
		for (State state : states) {
			if (state.isInitial())
				return state;
		}
		// Should never happen
		throw new IllegalArgumentException("There is not intial state for workflow: " + id);
	}

	public List<State> getStates() {
		return states;
	}

	//	public List<Project> getProjects() {
	//		return projects;
	//	}

	public void setStates(List<State> states) {
		this.states = states;
	}

	//	public void setProjects(List<Project> projects) {
	//		this.projects = projects;
	//	}

	public void setName(String name) {
		this.name = name;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	//	/**
	//	 * Add {@link Project} to this Workflow
	//	 *
	//	 * @param project
	//	 *            {@link Project} to add
	//	 */
	//	public void addProject(Project project) {
	//		projects.add(project);
	//	}

	//	/**
	//	 * Remove {@link Project} from this Workflow
	//	 *
	//	 * @param project
	//	 *            {@link Project} to remove
	//	 */
	//	public boolean removeProject(Project project) {
	//		return projects.remove(project);
	//	}

	/**
	 * Checks if this workflow contains all the states given as argument.
	 *
	 * @param states
	 *            that are checked if they are contained already by this <code>Workflow</code>
	 * @return true if and only if all the states are contained
	 * */
	public boolean containsAll(Collection<State> states) {
		return this.states.containsAll(states);
	}

	/**
	 * Returns the pre-definit collection of <code>State</code>s that every and
	 * each <code>Workflow</code> has like Initial.
	 *
	 * @return basic states that every <code>Workflow</code> has by default.
	 * */
	public static List<State> getBasicStates() {
		State initialState = new State("Initial state",
				"This is the first state, when a project is created.", true);
		// Create the basic states list
		List<State> basicStates = new ArrayList<>();
		basicStates.add(initialState);
		return basicStates;
	}

	/**
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	/**
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof Workflow))
			return false;
		Workflow other = (Workflow) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Workflow [id=" + id + ", name=" + name + "]";
	}

}
