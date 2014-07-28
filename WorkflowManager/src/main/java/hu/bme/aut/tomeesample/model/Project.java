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

import javax.persistence.CascadeType;
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

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Entity implementation class for Entity: Project
 * 
 * @author Imre Szekeres
 * @version "%I%, %G%"
 */
@SuppressWarnings("serial")
@Entity
@NoArgsConstructor
@EqualsAndHashCode
@Getter
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

	@Setter
	@NotNull
	@Size(min = 5, max = 16)
	@Column(unique = true)
	private String name;

	@Setter
	@Size(min = 13, max = 512)
	private String description;

	@Setter
	@NotNull
	@OneToOne(fetch = FetchType.EAGER)
	private State currentState;

	@Setter
	@NotNull
	@ManyToOne
	private Workflow workflow;

	@Setter
	@OneToMany(mappedBy = "project")
	private List<HistoryEntry> historyEntries;

	@Setter
	@OneToMany(mappedBy = "project", fetch = FetchType.EAGER, cascade = CascadeType.REMOVE)
	private Set<ProjectAssignment> projectAssignments;

	@Setter
	@OneToMany(mappedBy = "project", cascade = CascadeType.REMOVE)
	private List<Comment> comments;

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

	public void add(HistoryEntry entry) {
		if (historyEntries == null) {
			historyEntries = new ArrayList<HistoryEntry>();
		}
		historyEntries.add(entry);
	}

	public void add(ProjectAssignment assignment) {
		if (this.getProjectAssignments() == null) {
			this.setProjectAssignments(new HashSet<ProjectAssignment>());
		}
		projectAssignments.add(assignment);
	}

	public boolean remove(HistoryEntry entry) {
		return historyEntries.remove(entry);
	}

	public boolean remove(ProjectAssignment assignment) {
		return projectAssignments.remove(assignment);
	}

	public void add(Comment comment) {
		if (comments == null) {
			comments = new ArrayList<Comment>();
		}
		comments.add(comment);
	}

	public void remove(Comment comment) {
		comments.remove(comment);
	}

	@Override
	public String toString() {
		return ManagingUtils.fetchFrom(super.toString(), "Project");
	}
}
