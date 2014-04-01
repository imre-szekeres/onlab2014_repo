/**
 * Project.java
 * 
 * @author Imre Szekeres
 * */
package hu.bme.aut.tomeesample.model;

import javax.validation.constraints.*;

import java.io.Serializable;

import javax.persistence.*;

import java.util.*;

/**
 * Entity implementation class for Entity: Project
 * @author Imre Szekeres
 * @version "%I%, %G%"
 */
@SuppressWarnings("serial")
@Entity
@NamedQueries({
	@NamedQuery(name="Project.findAll", query="SELECT p FROM Project p"),
	@NamedQuery(name="Project.findById", query="SELECT p FROM Project p "
											  +"WHERE p.id=:id"),
	@NamedQuery(name="Project.findByName", query="SELECT p FROM Project p "
											    +"WHERE p.name=:name"),
	@NamedQuery(name="Project.findAllByWorkflowName", query="SELECT p FROM Project p "
												        +"WHERE p.workflow.name=:name")
})
public class Project implements Serializable {

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long id;

	@NotNull
	@Size(min=5, max=16)
	@Column(unique=true)
	private String name;
	
	@Size(min=13, max=512)
	private String description;
	
	@NotNull
	private State currentState;
	
	@NotNull
	@ManyToOne
	private Workflow workflow;
	
	@OneToMany(mappedBy="project")
	private List<HistoryEntry> historyEntries;
	
	@OneToMany(mappedBy="project")
	private List<ProjectAssignment> projectAssignments;
	
	public Project() {
		super();
	}
	
	public Project(String name, String description, Workflow workflow){
		this.name = name;
		this.description = description;
		this.workflow = workflow;
		this.workflow.add(this);
		this.currentState = this.workflow.getInitialState();
		this.projectAssignments = new ArrayList<>();
		this.historyEntries = new ArrayList<>();
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
	
	public List<ProjectAssignment> getProjectAssignments() {
		return projectAssignments;
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
	
	public void add(HistoryEntry entry){
		historyEntries.add(entry);
	}
	
	public void add(ProjectAssignment assignment){
		projectAssignments.add(assignment);
	}
	
	public boolean remove(HistoryEntry entry){
		return historyEntries.remove(entry);
	}
	
	public boolean remove(ProjectAssignment assignment){
		return projectAssignments.remove(assignment);
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
}
