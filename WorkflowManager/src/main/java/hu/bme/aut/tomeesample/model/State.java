/**
 * State.java
 *
 * @author Imre Szekeres
 * */
package hu.bme.aut.tomeesample.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
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
		@NamedQuery(name = "State.findById", query = "SELECT s FROM State s " + "WHERE s.id=:id"),
		@NamedQuery(name = "State.findByWorkflowId", query = "SELECT s FROM State s WHERE s.workflow.id=:workflowId"),
		@NamedQuery(name = "State.findChildrenByParentId", query = "SELECT s FROM State s WHERE s.parent.id=:parentId")
})
public class State implements Serializable {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@NotNull
	@Size(min = 4, max = 25)
	private String name;

	@Size(min = 0, max = 512)
	private String description;

	@ManyToOne
	@JoinColumn
	private Workflow workflow;

	@OneToMany
	@MapKeyJoinColumn
	private Map<ActionType, State> nextStates;

	@OneToMany(mappedBy = "state")
	private Set<HistoryEntry> historyEntries;

	@OneToMany(mappedBy = "state")
	private List<BlobFile> files;

	@ManyToOne
	private State parent;

	@OneToMany(mappedBy = "parent")
	private List<State> children;

	public State() {
		super();
	}

	public State(String name, String description, State parent) {
		this.name = name;
		this.description = description;
		this.workflow = null;
		this.historyEntries = new HashSet<>();
		this.files = new ArrayList<>();
		this.nextStates = new HashMap<>();
		this.children = new ArrayList<>();
		if (parent != null) {
			this.parent = parent;
			this.parent.addChild(this);
		}
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

	public Workflow getWorkflow() {
		return workflow;
	}

	public Set<HistoryEntry> getHistoryEntries() {
		return historyEntries;
	}

	public Map<ActionType, State> getNextStates() {
		return nextStates;
	}

	public State getParent() {
		return parent;
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

	public void setWorkflow(Workflow workflow) {
		this.workflow = workflow;
	}

	public void addHistoryEntry(HistoryEntry entry) {
		historyEntries.add(entry);
	}

	public boolean removeHistoryEntry(HistoryEntry entry) {
		return historyEntries.remove(entry);
	}

	public void addChild(State child) {
		children.add(child);
	}

	public boolean removeChild(State child) {
		return children.remove(child);
	}

	@Override
	public boolean equals(Object o) {
		if (!(o instanceof State))
			return false;
		return ((State) o).id.equals(this.id);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	@Override
	public String toString() {
		return "State [id=" + id + ", name=" + name + "]";
	}
}
