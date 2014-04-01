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
 *
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
	
	@Override
	public boolean equals(Object o){
		if(!(o instanceof Project)) return false;
		return ((Project)o).id.equals(this.id);
	}
	
	@Override
	public int hashCode(){
		int hash = 0;
		hash = 31*hash + id.hashCode();
		hash = 31*hash + name.hashCode();
		hash = 31*hash + description.hashCode();
		hash = 31*hash + currentState.hashCode();
		hash = 31*hash + workflow.hashCode();
		hash = 31*hash + historyEntries.hashCode();
		hash = 31*hash + projectAssignments.hashCode();
		return hash;
	}
}
