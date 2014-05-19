/**
 * ActionType.java
 * 
 * @author Imre Szekeres
 * */
package hu.bme.aut.tomeesample.model;

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
		@NamedQuery(name = "ActionType.findById", query = "SELECT at FROM ActionType at " + "WHERE at.id=:id") })
public class ActionType implements Serializable {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@NotNull
	@Column(unique = true, nullable = false)
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

	public void addRole(Role role) {
		roles.add(role);
		role.addActionType(this);
	}

	public void remove(Role role) {
		role.removeActionType(this);
		roles.remove(role);
	}

	public Long getId() {
		return id;
	}

	@Override
	public boolean equals(Object o) {
		if (!(o instanceof ActionType))
			return false;
		return (((ActionType) o).id).equals(this.id);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((actionTypeName == null) ? 0 : actionTypeName.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((id == null) ? 0 : roles.hashCode());
		return result;
	}

	@Override
	public String toString() {
		return "ActionType [id=" + id + ", actionTypeName=" + actionTypeName + "]";
	}

}
