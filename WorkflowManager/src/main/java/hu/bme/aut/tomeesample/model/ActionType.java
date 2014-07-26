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

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Entity implementation class for Entity: ActionType
 * 
 */
@SuppressWarnings("serial")
@Entity
@EqualsAndHashCode
@NoArgsConstructor
@NamedQueries({ @NamedQuery(name = "ActionType.findAll", query = "SELECT at FROM ActionType at"), @NamedQuery(name = "ActionType.findById", query = "SELECT at FROM ActionType at " + "WHERE at.id=:id") })
public class ActionType implements Serializable {

	@Id
	@Getter
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Getter
	@Setter
	@NotNull
	@Column(unique = true)
	private String actionTypeName;

	@Getter
	@Setter
	@ManyToMany(targetEntity = Role.class, mappedBy = "actionTypes", fetch = FetchType.EAGER)
	private Set<Role> roles;

	public ActionType(String actionTypeName) {
		this.actionTypeName = actionTypeName;
	}

	public void addRole(Role role) {
		roles.add(role);
		role.addActionType(this);
	}

	public void remove(Role role) {
		role.removeActionType(this);
		roles.remove(role);
	}

	@Override
	public String toString() {
		// do not remove | around id, it is used to get the id in converter
		return "ActionType [id=|" + id + "|, actionTypeName=" + actionTypeName + "]";
	}

}
