package hu.bme.aut.wman.model;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToMany;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * Entity implementation class for Entity: Role
 *
 * @version "%I%, %G%"
 */
@Entity
@SuppressWarnings("serial")
@NamedQueries({
	@NamedQuery(name = "Role.findByActionType", query = "SELECT r FROM Role r WHERE :actionType MEMBER OF r.actionTypes"),

	@NamedQuery(name = "Role.findByAssignment", query = "SELECT r FROM Role r, DomainAssignment d "+
			"WHERE d.domain.name = :domainName "+
			"AND d.user.id = :userID "+
			"AND r MEMBER OF d.userRoles "),

			@NamedQuery(name = "Role.findByDomain", query = "SELECT r FROM Role r, Domain d "+
					"WHERE d.name = :domainName "+
					"AND r MEMBER OF d.roles"),

					@NamedQuery(name = "Role.findNamesByDomain", query = "SELECT r.name FROM Role r, Domain d "+
							"WHERE d.name = :domainName "+
							"AND r MEMBER OF d.roles"),

							@NamedQuery(name = "Role.findByDomainAndName", query = "SELECT r FROM Role r, Domain d "+
									"WHERE d.name = :domainName "+
									"AND r.name = :roleName " +
									"AND r MEMBER OF d.roles "),

									@NamedQuery(name = "Role.findCountByPrivileges", query = "SELECT COUNT(DISTINCT r) FROM Role r, Domain d, DomainAssignment da " +
											"WHERE r MEMBER OF d.roles " +
											"AND r.id = :roleID " +
											"AND da.user.username = :username " +
											"AND da.domain = d " +
											"AND :count = ( SELECT COUNT(DISTINCT p) FROM Role r1, Privilege p " +
											"WHERE r1 MEMBER OF da.userRoles " +
											"AND p MEMBER OF r1.privileges " +
											"AND p.name IN :privilegeNames " +
											")"),

											@NamedQuery(name = "Role.findCountByPrivilege", query = "SELECT COUNT(DISTINCT r2) FROM Role r, Role r2, Domain d, DomainAssignment da, Privilege p " +
													"WHERE r MEMBER OF d.roles " +
													"AND r.id = :roleID " +
													"AND da.user.username = :username " +
													"AND da.domain = d " +
													"AND p MEMBER OF r2.privileges " +
													"AND p.name = :privilegeName " +
													"AND r2 MEMBER OF da.userRoles ")
})
public class Role extends AbstractEntity {

	public static final String NQ_FIND_BY_ACTIONTYPE = "Role.findByActionType";
	public static final String NQ_FIND_BY_DOMAIN = "Role.findByDomain";
	public static final String NQ_FIND_NAMES_BY_DOMAIN = "Role.findNamesByDomain";
	public static final String NQ_FIND_BY_DOMAIN_AND_NAME = "Role.findByDomainAndName";
	public static final String NQ_FIND_COUNT_BY_PRIVILEGES = "Role.findCountByPrivileges";

	public static final String NQ_FIND_COUNT_BY_PRIVILEGE = "Role.findCountByPrivilege";

	public static final String PR_NAME = "name";
	public static final String PR_DOMAIN = "domain";
	public static final String PR_ACTION_TYPES = "actionTypes";
	public static final String PR_DOMAIN_ASSIGNMENTS = "domainAssognments";
	public static final String PR_PRIVILEGS = "privileges";

	@Column(unique = true)
	@Size(min = 5, message = "must be longer than 5 characters.")
	@NotNull
	private String name;

	@NotNull
	@ManyToMany(targetEntity = ActionType.class, fetch = FetchType.EAGER)
	private Set<ActionType> actionTypes;

	@NotNull
	@ManyToMany(targetEntity = hu.bme.aut.wman.model.Privilege.class, fetch = FetchType.EAGER)
	private Set<Privilege> privileges;

	@Deprecated
	public Role() {
		super();
	}

	public Role(String name) {
		super();
		this.name = name;
		actionTypes = new HashSet<ActionType>();
		privileges = new HashSet<Privilege>();
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
		return privileges.add(privilege);
	}

	/**
	 * Remove {@link Privilege} from this Role
	 *
	 * @param privilege
	 *            the {@link Privilege} to remove
	 * @return true if the {@link Privilege} is removed
	 */
	public boolean removePrivilege(Privilege privilege) {
		return privileges.remove(privilege);
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
		int result = super.hashCode();
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result
				+ ((privileges == null) ? 0 : privileges.hashCode());
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
		if (!super.equals(obj)) {
			return false;
		}
		if (!(obj instanceof Role)) {
			return false;
		}
		Role other = (Role) obj;
		if (actionTypes == null) {
			if (other.actionTypes != null) {
				return false;
			}
		} else if (!actionTypes.equals(other.actionTypes)) {
			return false;
		}
		if (name == null) {
			if (other.name != null) {
				return false;
			}
		} else if (!name.equals(other.name)) {
			return false;
		}
		if (privileges == null) {
			if (other.privileges != null) {
				return false;
			}
		} else if (!privileges.equals(other.privileges)) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return name;
	}
}