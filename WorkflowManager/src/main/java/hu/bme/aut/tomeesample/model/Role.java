/**
 * Role.java
 * */
package hu.bme.aut.tomeesample.model;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * Entity implementation class for Entity: Role
 * 
 * @author Imre Szekeres
 * @version "%I%, %G%"
 */
@Entity
@NoArgsConstructor
@EqualsAndHashCode
@ToString
@SuppressWarnings("serial")
public class Role implements Serializable {

	@Id
	@Getter
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Getter
	@Setter
	@Column(unique = true)
	private String name;

	@Getter
	@Setter
	@ManyToMany(targetEntity = ActionType.class, fetch = FetchType.EAGER)
	private Set<ActionType> actionTypes = new HashSet<>();;

	@Getter
	@ManyToMany(targetEntity = hu.bme.aut.tomeesample.model.User.class, mappedBy = "roles")
	private Set<User> users = new HashSet<>();;

	public Role(String name) {
		this();
		this.name = name;
	}

	// TODO: check delegate in lombok for these methods:

	/**
	 * @param user
	 *            the user to add
	 * @return true if the user is added
	 */
	public boolean add(User user) {
		return this.users.add(user);
	}

	/**
	 * @param user
	 *            the user to remove
	 * @return true if the user is removed
	 */
	public boolean remove(User user) {
		return this.users.remove(user);
	}

	/**
	 * @param actionType
	 *            the actionType to add
	 * @return true if the actionType is added
	 */
	public boolean addActionType(ActionType actionType) {
		return actionTypes.add(actionType);
	}

	/**
	 * @param actionType
	 *            the actionType to remove
	 * @return true if the actionType is removed
	 */
	public boolean removeActionType(ActionType actionType) {
		return actionTypes.remove(actionType);
	}
}