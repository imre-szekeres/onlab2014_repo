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
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

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
	@Size(min = 2, max = 20)
	@Column(unique = true)
	private String actionTypeName;

	@NotNull
	@OneToMany(mappedBy = "actionType")
	private Set<Role> roles;

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

	public void add(Role role) {
		this.roles.add(role);
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

}
