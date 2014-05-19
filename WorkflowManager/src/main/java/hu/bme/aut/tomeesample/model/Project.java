/**
 * Project.java
 * 
 * @author Imre Szekeres
 * */
package hu.bme.aut.tomeesample.model;

import hu.bme.aut.tomeesample.utils.ManagingUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * Entity implementation class for Entity: Project
 * 
 * @author Imre Szekeres
 * @version "%I%, %G%"
 */
@SuppressWarnings("serial")
@Entity
@NamedQueries({
		@NamedQuery(name = "Project.findAll", query = "SELECT p FROM Project p"),
		@NamedQuery(name = "Project.findById", query = "SELECT p FROM Project p "
				+ "WHERE p.id=:id"),
		@NamedQuery(name = "Project.findByName", query = "SELECT p FROM Project p "
				+ "WHERE p.name=:name"),
		@NamedQuery(name = "Project.findAllByWorkflowName", query = "SELECT p FROM Project p "
				+ "WHERE p.workflow.name=:name")
})
public class Project implements Serializable {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@NotNull
	@Size(min = 5, max = 16)
	@Column(unique = true)
	private String name;

	@Size(min = 13, max = 512)
	private String description;

	@NotNull
	@OneToOne(fetch = FetchType.EAGER)
	private State currentState;

	@NotNull
	@ManyToOne
	private Workflow workflow;

	@OneToMany(mappedBy = "project")
	private List<HistoryEntry> historyEntries;

	@OneToMany(mappedBy = "project", fetch = FetchType.EAGER)
	private Set<ProjectAssignment> projectAssignments;

	@OneToMany(mappedBy = "project")
	private List<Comment> comments;

	public Project() {
		super();
	}

	public Project(String name, String description, Workflow workflow) {
		this.name = name;
		this.description = description;
		this.workflow = workflow;
		this.workflow.addProject(this);
		this.currentState = this.workflow.getInitialState();
		this.projectAssignments = new HashSet<>();
		this.historyEntries = new ArrayList<>();
		this.comments = new ArrayList<>();
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

	public State getCurrentState() {
		return currentState;
	}

	public Workflow getWorkflow() {
		return workflow;
	}

	public List<HistoryEntry> getHistoryEntries() {
		return historyEntries;
	}

	/**
	 * @param historyEntries
	 *            the historyEntries to set
	 */
	public void setHistoryEntries(List<HistoryEntry> historyEntries) {
		this.historyEntries = historyEntries;
	}

	public Set<ProjectAssignment> getProjectAssignments() {
		return projectAssignments;
	}

	/**
	 * @param projectAssignments
	 *            the projectAssignments to set
	 */
	public void setProjectAssignments(Set<ProjectAssignment> projectAssignments) {
		this.projectAssignments = projectAssignments;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void setCurrentState(State currentState) {
		this.currentState = currentState;
	}

	public void setWorkflow(Workflow workflow) {
		this.workflow = workflow;
	}

	public void add(HistoryEntry entry) {
		if (historyEntries == null)
			historyEntries = new ArrayList<HistoryEntry>();
		historyEntries.add(entry);
	}

	public void add(ProjectAssignment assignment) {
		if (this.getProjectAssignments() == null)
			this.setProjectAssignments(new HashSet<ProjectAssignment>());
		projectAssignments.add(assignment);
	}

	public boolean remove(HistoryEntry entry) {
		return historyEntries.remove(entry);
	}

	public boolean remove(ProjectAssignment assignment) {
		return projectAssignments.remove(assignment);
	}

	/**
	 * @return the comments
	 */
	public List<Comment> getComments() {
		return comments;
	}

	/**
	 * @param comments
	 *            the comments to set
	 */
	public void setComments(List<Comment> comments) {
		this.comments = comments;
	}

	public void add(Comment comment) {
		if (comments == null)
			comments = new ArrayList<Comment>();
		comments.add(comment);
	}

	public void remove(Comment comment) {
		comments.remove(comment);
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
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof Project)) {
			return false;
		}
		Project other = (Project) obj;
		if (id == null) {
			if (other.id != null) {
				return false;
			}
		} else if (!id.equals(other.id)) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return ManagingUtils.fetchFrom(super.toString(), "Project");
	}
}
