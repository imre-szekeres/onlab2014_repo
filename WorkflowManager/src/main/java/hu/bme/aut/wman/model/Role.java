package hu.bme.aut.wman.model;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToMany;
import javax.persistence.NamedQuery;

/**
 * Entity implementation class for Entity: Role
 * 
 * @version "%I%, %G%"
 */
@Entity
@SuppressWarnings("serial")
@NamedQuery(name = "Role.findByActionType", query = "SELECT r FROM Role r WHERE :actionType MEMBER OF r.actionTypes")
public class Role extends AbstractEntity {

	public static final String NQ_FIND_BY_ACTIONTYPE = "Role.findByActionType";

	public static final String PR_NAME = "name";
	public static final String PR_ACTION_TYPES = "actionTypes";
	public static final String PR_USERS = "users";
	public static final String PR_PRIVILEGS = "privileges";

	@Column(unique = true)
	private String name;

	@ManyToMany(targetEntity = ActionType.class, fetch = FetchType.EAGER)
	private Set<ActionType> actionTypes;

	@ManyToMany(targetEntity = hu.bme.aut.wman.model.User.class, mappedBy = "roles")
	private Set<User> users;

	@ManyToMany(targetEntity = hu.bme.aut.wman.model.Privilege.class, fetch = FetchType.EAGER)
	private Set<Privilege> privileges;

	public Role() {
		this("");
	}

	public Role(String name) {
		super();
		this.name = name;
		this.users = new HashSet<User>();
		this.actionTypes = new HashSet<ActionType>();
		this.privileges = new HashSet<Privilege>();
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name
	 *            the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the users
	 */
	public Set<User> getUsers() {
		return users;
	}

	/**
	 * Add {@link User} to this Role
	 * 
	 * @param user
	 *            the {@link User} to add
	 * @return true if the {@link User} is added
	 */
	public boolean addUser(User user) {
		return this.users.add(user);
	}

	/**
	 * Remove {@link User} from this Role
	 * 
	 * @param user
	 *            the {@link User} to remove
	 * @return true if the {@link User} is removed
	 */
	public boolean removeUser(User user) {
		return this.users.remove(user);
	}

	/**
	 * Add {@link ActionType} to this Role
	 * 
	 * @param actionType
	 *            the {@link ActionType} to add
	 * @return true if the {@link ActionType} is added
	 */
	public boolean addActionType(ActionType actionType) {
		return actionTypes.add(actionType);
	}

	/**
	 * Remove {@link ActionType} from this Role
	 * 
	 * @param actionType
	 *            the {@link ActionType} to remove
	 * @return true if the {@link ActionType} is removed
	 */
	public boolean removeActionType(ActionType actionType) {
		return actionTypes.remove(actionType);
	}

	/**
	 * @return the actionTypes
	 */
	public Set<ActionType> getActionTypes() {
		return actionTypes;
	}

	/**
	 * @param actionTypes
	 *            the actionTypes to set
	 */
	public void setActionTypes(Set<ActionType> actionTypes) {
		this.actionTypes = actionTypes;
	}

	/**
	 * @return the privileges
	 */
	public Set<Privilege> getPrivileges() {
		return privileges;
	}

	/**
	 * @param privileges
	 *            the privileges to set
	 */
	public void setPrivileges(Set<Privilege> privileges) {
		this.privileges = privileges;
	}

	/**
	 * Add {@link Privilege} to this Role
	 * 
	 * @param privilege
	 *            the {@link Privilege} to add
	 * @return true if the {@link Privilege} is added
	 */
	public boolean addPrivilege(Privilege privilege) {
		return this.privileges.add(privilege);
	}

	/**
	 * Remove {@link Privilege} from this Role
	 * 
	 * @param privilege
	 *            the {@link Privilege} to remove
	 * @return true if the {@link Privilege} is removed
	 */
	public boolean removePrivilege(Privilege privilege) {
		return this.privileges.remove(privilege);
	}

	// TODO: comment
	public boolean hasPrivilege(String name) {
		for (Privilege p : privileges) {
			if (p.getName().equals(name)) {
				return true;
			}
		}
		return false;
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
		if (!(obj instanceof Role)) {
			return false;
		}
		Role other = (Role) obj;
		if (id == null) {
			if (other.id != null) {
				return false;
			}
		} else if (!id.equals(other.id)) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return name;
	}
}