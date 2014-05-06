/**
 * State.java
 *
 * @author Imre Szekeres
 * */
package hu.bme.aut.tomeesample.model;

import java.io.Serializable;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.MapKeyJoinColumn;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * Entity implementation class for Entity: State
 *
 */
@SuppressWarnings("serial")
@Entity
@NamedQueries({
		@NamedQuery(name = "State.findAll", query = "SELECT s FROM State s"),
		@NamedQuery(name = "State.findById", query = "SELECT s FROM State s " + "WHERE s.id=:id")
})
public class State implements Serializable {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@NotNull
	@Size(min = 4, max = 25)
	private String name;

	@Size(min = 13, max = 512)
	private String description;

	@NotNull
	@ManyToOne
	private Workflow workflow;

	@OneToMany
	@MapKeyJoinColumn
	private Map<ActionType, State> nextStates;

	@OneToMany(mappedBy = "state")
	private Set<HistoryEntry> historyEntries;

	@OneToMany(mappedBy = "state")
	private List<BlobFile> files;

	public State() {
		super();
	}

	public State(String name, String description, Workflow workflow) {
		this.name = name;
		this.description = description;
		this.workflow = workflow;
		this.workflow.add(this);
		this.historyEntries = new HashSet<>();
	}

	public Long getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public String getDescription() {
		return description;
	}

	// public State getCurrentState() {
	// return currentState;
	// }

	public Workflow getWorkflow() {
		return workflow;
	}

	public Set<HistoryEntry> getHistoryEntries() {
		return historyEntries;
	}

	public Map<ActionType, State> getNextStates() {
		return nextStates;
	}

	public void setNextStates(Map<ActionType, State> nextStates) {
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

	public void setDescription(String description) {
		this.description = description;
	}

	// public void setCurrentState(State currentState) {
	// this.currentState = currentState;
	// }

	public void setWorkflow(Workflow workflow) {
		this.workflow = workflow;
	}

	public void add(HistoryEntry entry) {
		historyEntries.add(entry);
	}

	public boolean remove(HistoryEntry entry) {
		return historyEntries.remove(entry);
	}

	@Override
	public boolean equals(Object o) {
		if (!(o instanceof State))
			return false;
		return ((State) o).id.equals(this.id);
	}

	@Override
	public int hashCode() {
		int hash = 0;
		hash = 31 * hash + id.hashCode();
		hash = 31 * hash + name.hashCode();
		hash = 31 * hash + description.hashCode();
		// hash = 31 * hash + currentState.hashCode();
		hash = 31 * hash + workflow.hashCode();
		hash = 31 * hash + historyEntries.hashCode();
		return hash;
	}
}
