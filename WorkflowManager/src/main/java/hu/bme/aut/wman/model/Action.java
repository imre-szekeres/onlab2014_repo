package hu.bme.aut.wman.model;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * Entity implementation class for Entity: Action
 * 
 * DEPRECATED, don't use it, we should delete it soon
 * 
 * @version "%I%, %G%"
 */
@Deprecated
@SuppressWarnings("serial")
@Entity
@NamedQueries({
		@NamedQuery(name = "Action.findAll", query = "SELECT a FROM Action a"),
		@NamedQuery(name = "Action.findById", query = "SELECT a FROM Action a WHERE a.id=:id"),
		@NamedQuery(name = "Action.findByType", query = "SELECT a FROM Action a WHERE a.actionType.id=:typeId")
})
public class Action implements Serializable {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@NotNull
	@ManyToOne
	private ActionType actionType;

	@Size(min = 10, max = 512)
	private String description;

	public Action() {
		super();
	}

	public Action(ActionType actionType, String description) {
		this.actionType = actionType;
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
		if (!(obj instanceof Action)) {
			return false;
		}
		Action other = (Action) obj;
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
