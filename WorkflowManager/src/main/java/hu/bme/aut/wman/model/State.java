/**
 * State.java
 *
 * @author Imre Szekeres
 * */
package hu.bme.aut.wman.model;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.validation.constraints.NotNull;

/**
 * Entity implementation class for Entity: State
 *
 * @version "%I%, %G%"
 */
@SuppressWarnings("serial")
@Entity
@NamedQueries({
	@NamedQuery(name = "State.findByWorkflowId", query = "SELECT s FROM State s WHERE s.workflow.id=:id"),
	@NamedQuery(name = "State.findInitialInWorkflow", query = "SELECT s FROM State s WHERE s.workflow.id=:id and s.initial=true"),
})
public class State extends AbstractEntity {

	public static final String NQ_FIND_BY_WORKFLOW_ID = "State.findByWorkflowId";
	public static final String NQ_FIND_INTIAL_IN_WORKFLOW = "State.findInitialInWorkflow";

	public static final String PR_NAME = "name";
	public static final String PR_INITIAL = "initial";
	public static final String PR_WORKFLOW = "workflow";
	public static final String PR_DESCRIPTION = "description";

	@NotNull
	// @Size(min = 4, max = 25)
	private String name;

	// @Size(min = 0, max = 512)
	private String description;

	private boolean initial;

	@ManyToOne
	private Workflow workflow;

	public State() {
		super();
	}

	public State(String name, String description, boolean initial) {
		this.name = name;
		this.description = description;
		workflow = null;
		this.initial = initial;
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

	public boolean isInitial() {
		return initial;
	}

	public void setInitial(boolean initial) {
		this.initial = initial;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void setWorkflow(Workflow workflow) {
		this.workflow = workflow;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((description == null) ? 0 : description.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		State other = (State) obj;
		if (id == null) {
			if (other.id != null) {
				return false;
			}
		} else if (!id.equals(other.id)) {
			return false;
		}
		if (name == null) {
			if (other.name != null) {
				return false;
			}
		} else if (!name.equals(other.name)) {
			return false;
		}
		if (workflow == null) {
			if (other.workflow != null) {
				return false;
			}
		} else if (!workflow.equals(other.workflow)) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "State [id=|" + id + "|, name=" + name + ", initial=" + initial + "]";
	}
}
