package hu.bme.aut.wman.model;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.validation.constraints.NotNull;

/**
 * Entity implementation class for connections between <code>States</code>.
 * 
 * @version "%I%, %G%"
 */
@SuppressWarnings("serial")
@Entity
@NamedQueries({
		@NamedQuery(name = "Transition.findByParentId", query = "SELECT t FROM Transition t WHERE t.parentState.id=:parentId"),
		@NamedQuery(name = "Transition.findByNextId", query = "SELECT t FROM Transition t WHERE t.nextState.id=:nextId"),
		@NamedQuery(name = "Transition.findByActionTypeId", query = "SELECT t FROM Transition t WHERE t.actionType.id=:typeId")
})
public class Transition extends AbstractEntity {

	public static final String NQ_FIND_BY_PARENT_ID = "Transition.findByParentId";
	public static final String NQ_FIND_BY_NEXT_STATE_ID = "Transition.findByNextId";
	public static final String NQ_FIND_BY_ACTIONTYPE_ID = "Transition.findByActionTypeId";

	public static final String PR_PARENT_STATE = "parentState";
	public static final String PR_NEXT_STATE = "nextState";
	public static final String PR_ACTION_TYPE = "actionType";

	@ManyToOne(cascade = CascadeType.MERGE)
	@JoinColumn
	private State parentState;

	@Column
	@ManyToOne(fetch = FetchType.EAGER)
	@NotNull
	private State nextState;

	@Column
	@ManyToOne(fetch = FetchType.EAGER)
	@NotNull
	private ActionType actionType;

	@Deprecated
	public Transition() {
	}

	public Transition(ActionType actionType, State nextState, State parent) {
		this.actionType = actionType;
		this.nextState = nextState;
		this.parentState = parent;
	}

	public State getParent() {
		return parentState;
	}

	public void setParent(State parent) {
		this.parentState = parent;
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
		result = prime * result + ((parentState == null) ? 0 : parentState.hashCode());
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
		Transition other = (Transition) obj;
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
		if (parentState == null) {
			if (other.parentState != null) {
				return false;
			}
		} else if (!parentState.equals(other.parentState)) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "Transition [id=" + id + ", parent=" + parentState.getName() + ", nextState=" + nextState.getName() + ", actionType=" + actionType + "]";
	}

}
