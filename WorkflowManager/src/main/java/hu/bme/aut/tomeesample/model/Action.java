/**
  * Action.java
  * 
  * @author Imre Szekeres
  * */
package hu.bme.aut.tomeesample.model;

import javax.validation.constraints.*;
import java.io.Serializable;
import javax.persistence.*;

/**
 * Entity implementation class for Entity: Action
 *
 */
@SuppressWarnings("serial")
@Entity
@NamedQueries({
	@NamedQuery(name="Action.findAll", query="SELECT a FROM Action a"),
	@NamedQuery(name="Action.findById", query="SELECT a FROM Action a "
											 +"WHERE a.id=:id"),
	@NamedQuery(name="Action.findByType", query="SELECT a FROM Action a "
											   +"WHERE a.actionType.id=:typeID")
})
public class Action implements Serializable {
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long id;
	
	@NotNull
	@ManyToOne
	private ActionType actionType;
	
	@Size(min=10, max=512)
	private String description;
	
	public Action() {
		super();
	}
	
	public Action(ActionType actionType, String description){
		this.actionType  = actionType;
		this.description = description;
	}
	
	public Long getId() {
		return id;
	}
	
	public ActionType getActionType() {
		return actionType;
	}
	
	public String getDescription() {
		return description;
	}
	
	public void setActionType(ActionType actionType) {
		this.actionType = actionType;
	}
	
	public void setDescription(String description) {
		this.description = description;
	}
   
	@Override
	public boolean equals(Object o){
		if(!(o instanceof Action)) return false;
		return (((Action)o).id).equals(this.id);
	}
	
	@Override
	public int hashCode(){
		int hash = 0;
		hash = 31*hash + id.hashCode();
		hash = 31*hash + actionType.hashCode();
		hash = 31*hash + description.hashCode();
		return hash;
	}
}
