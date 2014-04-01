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
 * @author Imre Szekeres
 * @version "%I%, %G%"
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
   
	public Workflow(String name, String description){
		this.name = name;
		this.description = description;
		this.states = new HashSet<>();
		this.states.addAll(Workflow.getBasicStates());
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
	
	public void addAll(Collection<State> states){
		states.addAll(states);
	}
	
	public boolean remove(State state){
		return states.remove(state);
	}
	
	public boolean removeAll(Collection<State> states){
		return states.removeAll(states);
	}
	
	public boolean remove(Project project){
		return projects.remove(project);
	}
	
	/**
	 * Checks if this workflow contains all the states given as argument.
	 * 
	 * @param states that are checked if they are contained already by this <code>Workflow</code>
	 * @return true if and only if all the states are contained
	 * */
	public boolean containsAll(Collection<State> states){
		return this.states.containsAll(states);
	}

	/**
	 * Returns the pre-definit collection of <code>State</code>s that every and
	 * each <code>Workflow</code> has like Initial, Paused, Closed.
	 * 
	 * @return basic states that every <code>Workflow</code> has by default.
	 * */
	public static List<State> getBasicStates(){
		//TODO: something more sophisticated :D 
		return new java.util.ArrayList<State>();
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
		if (!(obj instanceof Workflow)) {
			return false;
		}
		Workflow other = (Workflow) obj;
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
