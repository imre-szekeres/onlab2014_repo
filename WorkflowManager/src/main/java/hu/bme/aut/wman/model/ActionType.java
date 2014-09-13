/**
 * ActionType.java
 * 
 * @author Imre Szekeres
 * */
package hu.bme.aut.wman.model;

import java.io.Serializable;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.validation.constraints.NotNull;

/**
 * Entity implementation class for Entity: ActionType
 * 
 */
@SuppressWarnings("serial")
@Entity
@NamedQueries({
		@NamedQuery(name = "ActionType.findAll", query = "SELECT at FROM ActionType at"),
		@NamedQuery(name = "ActionType.findById", query = "SELECT at FROM ActionType at WHERE at.id=:id") })
public class ActionType implements Serializable {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@NotNull
	@Column(unique = true)
	private String actionTypeName;

	@ManyToMany(targetEntity = Role.class, mappedBy = "actionTypes", fetch = FetchType.EAGER)
	private Set<Role> roles;

	public ActionType() {
	}

	public ActionType(String actionTypeName) {
		this.actionTypeName = actionTypeName;
	}

	public String getActionTypeName() {
		return actionTypeName;
	}

	public void setActionTypeName(String actionTypeName) {
		this.actionTypeName = actionTypeName;
	}

	public Set<Role> getRoles() {
		return roles;
	}

	public void setRoles(Set<Role> roles) {
		this.roles = roles;
	}

	/**
	 * Add {@link Role} to this ActionType
	 * 
	 * @param role
	 *            {@link Role} to add
	 */
	public void addRole(Role role) {
		roles.add(role);
		role.addActionType(this);
	}

	/**
	 * Remove {@link Role} from this ActionType
	 * 
	 * @param role
	 *            {@link Role} to remove
	 */
	public void removeRole(Role role) {
		role.removeActionType(this);
		roles.remove(role);
	}

	public Long getId() {
		return id;
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
		ActionType other = (ActionType) obj;
		if (actionTypeName == null) {
			if (other.actionTypeName != null) {
				return false;
			}
		} else if (!actionTypeName.equals(other.actionTypeName)) {
			return false;
		}
		if (id == null) {
			if (other.id != null) {
				return false;
			}
		} else if (!id.equals(other.id)) {
			return false;
		}
		if (roles == null) {
			if (other.roles != null) {
				return false;
			}
		} else if (!roles.equals(other.roles)) {
			return false;
		}
		return true;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((actionTypeName == null) ? 0 : actionTypeName.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((roles == null) ? 0 : roles.hashCode());
		return result;
	}

	@Override
	public String toString() {
		// do not remove | around id, it is used to get the id in converter
		return "ActionType [id=|" + id + "|, actionTypeName=" + actionTypeName + "]";
	}

}
