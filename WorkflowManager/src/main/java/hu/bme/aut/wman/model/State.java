/**
 * State.java
 *
 * @author Imre Szekeres
 * */
package hu.bme.aut.wman.model;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.validation.constraints.NotNull;

/**
 * Entity implementation class for Entity: State
 * 
 * @version "%I%, %G%"
 */
@SuppressWarnings("serial")
@Entity
@NamedQueries({
		// @NamedQuery(name = "State.findAll", query = "SELECT s FROM State s"),
		// @NamedQuery(name = "State.findById", query = "SELECT s FROM State s " + "WHERE s.id=:id"),
		@NamedQuery(name = "State.findByWorkflowId", query = "SELECT s FROM State s WHERE s.workflow.id=:workflowId"),
		@NamedQuery(name = "State.findChildrenByParentId", query = "SELECT s FROM State s WHERE s.parent.id=:parentId"),
		// @NamedQuery(name = "State.findByInitial", query = "SELECT s FROM State s WHERE s.initial=:initial"),
		@NamedQuery(name = "State.findRootStatesByWorkflowId", query = "SELECT s FROM State s WHERE s.workflow.id=:workflowId and s.parent IS NULL")
})
public class State extends AbstractEntity {

	public static final String NQ_FIND_BY_WORKFLOW_ID = "State.findByWorkflowId";
	// TODO I'm 75% sure, we don't need this two any more
	public static final String NQ_FIND_CHILDREN_BY_PARENT_ID = "State.findChildrenByParentId";
	public static final String NQ_FIND_ROOT_STATES_BY_WORKFLOW_ID = "State.findRootStatesByWorkflowId";

	@NotNull
	// @Size(min = 4, max = 25)
	private String name;

	// @Size(min = 0, max = 512)
	private String description;

	private boolean initial;

	@ManyToOne
	@JoinColumn
	private Workflow workflow;

	@OneToMany(mappedBy = "parent", fetch = FetchType.EAGER, cascade = { CascadeType.MERGE })
	private List<StateNavigationEntry> nextStates;

	@OneToMany(mappedBy = "state", fetch = FetchType.EAGER, cascade = CascadeType.MERGE)
	private Set<HistoryEntry> historyEntries;

	@OneToMany(mappedBy = "state", fetch = FetchType.EAGER, cascade = CascadeType.MERGE)
	private List<BlobFile> files;

	@ManyToOne
	@JoinColumn
	private State parent;

	@OneToMany(mappedBy = "parent", fetch = FetchType.LAZY)
	private List<State> children;

	public State() {
		super();
	}

	public State(String name, String description, boolean initial) {
		this.name = name;
		this.description = description;
		this.workflow = null;
		this.initial = initial;
		this.historyEntries = new HashSet<>();
		this.files = new ArrayList<>();
		this.children = new ArrayList<>();
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

	public Set<HistoryEntry> getHistoryEntries() {
		return historyEntries;
	}

	public List<StateNavigationEntry> getNextStates() {
		return nextStates;
	}

	public State getParent() {
		return parent;
	}

	public boolean isInitial() {
		return initial;
	}

	public void setInitial(boolean initial) {
		this.initial = initial;
	}

	public void setParent(State parent) {
		this.parent = parent;
	}

	public List<State> getChildren() {
		return children;
	}

	public void setChildren(List<State> children) {
		this.children = children;
	}

	public void setNextStates(List<StateNavigationEntry> nextStates) {
		this.nextStates = nextStates;
	}

	public List<BlobFile> getFiles() {
		return files;
	}

	public void setFiles(List<BlobFile> files) {
		this.files = files;
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

	/**
	 * Add {@link HistoryEntry} to this State
	 * 
	 * @param historyEntry
	 *            {@link HistoryEntry} to add
	 */
	public void addHistoryEntry(HistoryEntry historyEntry) {
		historyEntries.add(historyEntry);
	}

	/**
	 * Remove {@link HistoryEntry} from this State
	 * 
	 * @param historyEntry
	 *            {@link HistoryEntry} to remove
	 */
	public boolean removeHistoryEntry(HistoryEntry historyEntry) {
		return historyEntries.remove(historyEntry);
	}

	/**
	 * Remove {@link StateNavigationEntry} from this State
	 * 
	 * @param stateNavigationEntry
	 *            {@link StateNavigationEntry} to remove
	 */
	public void removeNexState(StateNavigationEntry stateNavigationEntry) {
		nextStates.remove(stateNavigationEntry);
		stateNavigationEntry.setParent(null);
	}

	/**
	 * Add {@link StateNavigationEntry} to this State
	 * 
	 * @param stateNavigationEntry
	 *            {@link StateNavigationEntry} to add
	 */
	public void addNextState(StateNavigationEntry stateNavigationEntry) {
		getNextStates().add(stateNavigationEntry);
		stateNavigationEntry.setParent(this);
	}

	/**
	 * Add a child state to this State
	 * 
	 * @param child
	 *            child state to add
	 */
	public void addChild(State child) {
		if (getChildren() == null) {
			setChildren(new ArrayList<State>());
		}
		if (child != null && !getChildren().contains(child)) {
			getChildren().add(child);
			child.setParent(this);
		}
	}

	/**
	 * Remove the child state from this State's children
	 * 
	 * @param child
	 *            child state to remove
	 */
	public boolean removeChild(State child) {
		return children.remove(child);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((description == null) ? 0 : description.hashCode());
		result = prime * result + ((files == null) ? 0 : files.hashCode());
		result = prime * result + ((historyEntries == null) ? 0 : historyEntries.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((workflow == null) ? 0 : workflow.hashCode());
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
		if (parent == null) {
			if (other.parent != null) {
				return false;
			}
		} else if (!parent.equals(other.parent)) {
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

	public State copy() {
		State copy = new State(name, description, initial);
		copy.setNextStates(nextStates);
		copy.setWorkflow(workflow);
		return copy;
	}
}
