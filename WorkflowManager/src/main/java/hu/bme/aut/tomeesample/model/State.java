/**
 * State.java
 *
 * @author Imre Szekeres
 * */
package hu.bme.aut.tomeesample.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.validation.constraints.NotNull;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Entity implementation class for Entity: State
 * 
 */
@SuppressWarnings("serial")
@Entity
@NoArgsConstructor
@EqualsAndHashCode
@NamedQueries({
		@NamedQuery(name = "State.findAll", query = "SELECT s FROM State s"),
		@NamedQuery(name = "State.findById", query = "SELECT s FROM State s " + "WHERE s.id=:id"),
		@NamedQuery(name = "State.findByWorkflowId", query = "SELECT s FROM State s WHERE s.workflow.id=:workflowId"),
		@NamedQuery(name = "State.findChildrenByParentId", query = "SELECT s FROM State s WHERE s.parent.id=:parentId"),
		@NamedQuery(name = "State.findByInitial", query = "SELECT s FROM State s WHERE s.initial=:initial"),
		@NamedQuery(name = "State.findRootStatesByWorkflowId", query = "SELECT s FROM State s WHERE s.workflow.id=:workflowId and s.parent IS NULL")
})
public class State implements Serializable {

	@Id
	@Getter
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Getter
	@Setter
	@NotNull
	private String name;

	@Getter
	@Setter
	private String description;

	@Getter
	@Setter
	private boolean initial;

	@Getter
	@Setter
	@ManyToOne
	@JoinColumn
	private Workflow workflow;

	@Getter
	@Setter
	@OneToMany(mappedBy = "parent", fetch = FetchType.EAGER, cascade = { CascadeType.MERGE })
	private List<StateNavigationEntry> nextStates;

	@Getter
	@Setter
	@OneToMany(mappedBy = "state", fetch = FetchType.EAGER, cascade = CascadeType.MERGE)
	private Set<HistoryEntry> historyEntries;

	@Getter
	@Setter
	@OneToMany(mappedBy = "state", fetch = FetchType.EAGER, cascade = CascadeType.MERGE)
	private List<BlobFile> files;

	@Getter
	@Setter
	@ManyToOne
	@JoinColumn
	private State parent;

	@Getter
	@Setter
	@OneToMany(mappedBy = "parent", fetch = FetchType.LAZY)
	private List<State> children;

	public State(String name, String description, boolean initial) {
		this.name = name;
		this.description = description;
		this.workflow = null;
		this.initial = initial;
		this.historyEntries = new HashSet<>();
		this.files = new ArrayList<>();
		this.children = new ArrayList<>();
	}

	public void addHistoryEntry(HistoryEntry entry) {
		historyEntries.add(entry);
	}

	public boolean removeHistoryEntry(HistoryEntry entry) {
		return historyEntries.remove(entry);
	}

	public void removeNexState(StateNavigationEntry stateNavigationEntry) {
		nextStates.remove(stateNavigationEntry);
		stateNavigationEntry.setParent(null);
	}

	public void addNextState(StateNavigationEntry stateNavigationEntry) {
		getNextStates().add(stateNavigationEntry);
		stateNavigationEntry.setParent(this);
	}

	public void addChild(State child) {
		if (getChildren() == null) {
			setChildren(new ArrayList<State>());
		}
		if (child != null && !getChildren().contains(child)) {
			getChildren().add(child);
			child.setParent(this);
		}
	}

	public boolean removeChild(State child) {
		return children.remove(child);
	}

	@Override
	public String toString() {
		// do not remove | around id, it is used to get the id in converter
		return "State [id=|" + id + "|, name=" + name + ", initial=" + initial + "]";
	}

	public State copy() {
		State copy = new State(name, description, initial);
		copy.setNextStates(nextStates);
		copy.setWorkflow(workflow);
		return copy;
	}
}
