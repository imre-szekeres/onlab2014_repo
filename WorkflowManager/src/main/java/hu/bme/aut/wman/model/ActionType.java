package hu.bme.aut.wman.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;

/**
 * Entity implementation class for Entity: ActionType
 * 
 * @version "%I%, %G%"
 */
@SuppressWarnings("serial")
@Entity
public class ActionType extends AbstractEntity {

	public static final String PR_NAME = "actionTypeName";
	public static final String PR_ROLES = "roles";
	public static final String PR_DOMAIN = "domain";
	

	@NotNull
	@Column(unique = true)
	private String actionTypeName;
	
	@NotNull
	@ManyToOne
	private Domain domain;


	@Deprecated
	public ActionType() {
		super();
	}

	public ActionType(String actionTypeName, Domain domain) {
		this.actionTypeName = actionTypeName;
		this.domain = domain;
	}

	public String getActionTypeName() {
		return actionTypeName;
	}

	public void setActionTypeName(String actionTypeName) {
		this.actionTypeName = actionTypeName;
	}

	/**
	 * @return the domain
	 */
	public Domain getDomain() {
		return domain;
	}

	/**
	 * @param domain the domain to set
	 */
	public void setDomain(Domain domain) {
		this.domain = domain;
	}

	/**
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result
				+ ((actionTypeName == null) ? 0 : actionTypeName.hashCode());
		result = prime * result + ((domain == null) ? 0 : domain.hashCode());
		return result;
	}

	/**
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (!(obj instanceof ActionType))
			return false;
		ActionType other = (ActionType) obj;
		if (actionTypeName == null) {
			if (other.actionTypeName != null)
				return false;
		} else if (!actionTypeName.equals(other.actionTypeName))
			return false;
		if (domain == null) {
			if (other.domain != null)
				return false;
		} else if (!domain.equals(other.domain))
			return false;
		return true;
	}

	@Override
	public String toString() {
		// do not remove | around id, it is used to get the id in converter
		return "ActionType [id=|" + id + "|, actionTypeName=" + actionTypeName + "]";
	}

}
