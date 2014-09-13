/**
 * Action.java
 * 
 * @author Imre Szekeres
 * */
package hu.bme.aut.wman.model;

import java.io.Serializable;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.validation.constraints.NotNull;

/**
 * @author Gergely Varkonyi
 */
@SuppressWarnings("serial")
@Entity
@NamedQueries({
		@NamedQuery(name = "StateNavigationEntry.findAll", query = "SELECT sne FROM StateNavigationEntry sne"),
		@NamedQuery(name = "StateNavigationEntry.findById", query = "SELECT sne FROM StateNavigationEntry sne WHERE sne.id=:id"),
		@NamedQuery(name = "StateNavigationEntry.findByParentId", query = "SELECT sne FROM StateNavigationEntry sne WHERE sne.parent.id=:parentId"),
		@NamedQuery(name = "StateNavigationEntry.findByActionType", query = "SELECT sne FROM StateNavigationEntry sne WHERE sne.actionType.id=:typeId AND sne.parent.id=:parentId")
})
public class StateNavigationEntry implements Serializable {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(cascade = CascadeType.MERGE)
	@JoinColumn
	private State parent;

	@Column
	@ManyToOne(fetch = FetchType.EAGER)
	@NotNull
	private State nextState;

	@Column
	@ManyToOne(fetch = FetchType.EAGER)
	@NotNull
	private ActionType actionType;

	public StateNavigationEntry() {

	}

	public StateNavigationEntry(ActionType actionType, State nextState, State parent) {
		this.actionType = actionType;
		this.nextState = nextState;
		this.parent = parent;
	}

	public Long getId() {
		return id;
	}

	public State getParent() {
		return parent;
	}

	public void setParent(State parent) {
		this.parent = parent;
	}

	public State getNextState() {
		return nextState;
	}

	public void setNextState(State nextState) {
		this.nextState = nextState;
	}

	public ActionType getActionType() {
		return actionType;
	}

	public void setActionType(ActionType actionType) {
		this.actionType = actionType;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((actionType == null) ? 0 : actionType.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((nextState == null) ? 0 : nextState.hashCode());
		result = prime * result + ((parent == null) ? 0 : parent.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		StateNavigationEntry other = (StateNavigationEntry) obj;
		if (actionType == null) {
			if (other.actionType != null) {
				return false;
			}
		} else if (!actionType.equals(other.actionType)) {
			return false;
		}
		if (id == null) {
			if (other.id != null) {
				return false;
			}
		} else if (!id.equals(other.id)) {
			return false;
		}
		if (nextState == null) {
			if (other.nextState != null) {
				return false;
			}
		} else if (!nextState.equals(other.nextState)) {
			return false;
		}
		if (parent == null) {
			if (other.parent != null) {
				return false;
			}
		} else if (!parent.equals(other.parent)) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "StateNavigationEntry [id=" + id + ", parent=" + parent.getName() + ", nextState=" + nextState.getName() + ", actionType=" + actionType + "]";
	}

}
