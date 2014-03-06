/**
 * Workflow.java
 * 
 * @author Imre Szekeres
 * */
package hu.bme.aut.tomeesample.model;

import javax.validation.constraints.*;
import java.io.Serializable;
import javax.persistence.*;
import java.util.*;

/**
 * Entity implementation class for Entity: Workflow
 *
 */
@SuppressWarnings("serial")
@Entity
@NamedQueries({
	@NamedQuery(name="Workflow.findAll", query="SELECT w FROM Workflow w"),
	@NamedQuery(name="Workflow.findById", query="SELECT w FROM Workflow w "
											   +"WHERE w.id=:id"),
	@NamedQuery(name="Workflow.findByName", query="SELECT w FROM Workflow w "
											     +"WHERE w.name=:name")
})
public class Workflow implements Serializable {

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long id;
	
	@NotNull
	@Size(min=5, max=32)
	private String name;
	
	@NotNull
	@Size(min=16, max=512)
	private String description;
	
	@NotNull
	@OneToMany(mappedBy="workflow")
	private Set<State> states;
	
	@OneToMany(mappedBy="workflow")
	private List<Project> projects;
	
	
	public Workflow() {
		super();
	}
   
	public Workflow(String name, String descripion){
		this.name = name;
		this.description = description;
		/*this.states = new HashSet<>();
		states.add(new State(State.init, this));
		states.add(new State(State.dead, this));
		states.add(new State(State.pause, this));*/
		this.projects = new ArrayList<>();
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
	
	//TODO:
	public State getInitialState(){
		return null; //states.get(State.initial);
	}
	
	public Set<State> getStates() {
		return states;
	}
	
	public List<Project> getProjects() {
		return projects;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public void setDescription(String description) {
		this.description = description;
	}
	
	public void add(State state){
		states.add(state);
	}
	
	public void add(Project project){
		projects.add(project);
	}
	
	public boolean remove(State state){
		return states.remove(state);
	}
	
	public boolean remove(Project project){
		return projects.remove(project);
	}
	
	@Override
	public boolean equals(Object o){
		if(!(o instanceof Workflow)) return false;
		return ((Workflow)o).id.equals(this.id);
	}
	
	@Override
	public int hashCode(){
		int hash = 0;
		hash = 31*hash + id.hashCode();
		hash = 31*hash + name.hashCode();
		hash = 31*hash + description.hashCode();
		hash = 31*hash + states.hashCode();
		hash = 31*hash + projects.hashCode();
		return hash;
	}
}
