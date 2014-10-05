package hu.bme.aut.wman.model;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.validation.constraints.NotNull;

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
	@NamedQuery(name = "Role.findByDomain", query = "SELECT r FROM Role r WHERE r.domain.name = :domainName ")
})
public class Role extends AbstractEntity {

	public static final String NQ_FIND_BY_ACTIONTYPE = "Role.findByActionType";

	public static final String PR_NAME = "name";
	public static final String PR_DOMAIN = "domain";
	public static final String PR_ACTION_TYPES = "actionTypes";
	public static final String PR_DOMAIN_ASSIGNMENTS = "domainAssognments";
	public static final String PR_PRIVILEGS = "privileges";

	@Column(unique = true)
	private String name;
	
	@NotNull
	@ManyToOne
	private Domain domain;

	@NotNull
	@ManyToMany(targetEntity = ActionType.class, fetch = FetchType.EAGER)
	private Set<ActionType> actionTypes;

	@NotNull
	@ManyToMany(targetEntity = hu.bme.aut.wman.model.DomainAssignment.class, fetch = FetchType.EAGER)
	private Set<DomainAssignment> domainAssignments;

	@NotNull
	@ManyToMany(targetEntity = hu.bme.aut.wman.model.Privilege.class, fetch = FetchType.EAGER)
	private Set<Privilege> privileges;

	@Deprecated
	public Role() {
		super();
	}

	public Role(String name, Domain domain) {
		super();
		this.name = name;
		this.domain = domain;
		this.actionTypes = new HashSet<ActionType>();
		this.privileges = new HashSet<Privilege>();
		this.domainAssignments = new HashSet<DomainAssignment>();
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
	 * @return the domainAssignments
	 */
	public Set<DomainAssignment> getDomainAssignments() {
		return domainAssignments;
	}

	/**
	 * @param domainAssignments the domainAssignments to set
	 */
	public void setDomainAssignments(Set<DomainAssignment> domainAssignments) {
		this.domainAssignments = domainAssignments;
	}
	
	/**
	 * @param domainAssignment
	 * */
	public boolean addDomainAssignment(DomainAssignment domainAssignment) {
		return this.domainAssignments.add(domainAssignment);
	}
	
	/**
	 * @param domainAssignment
	 * */
	public boolean removeDomainAssignmnet(DomainAssignment domainAssignment) {
		return this.domainAssignments.remove(domainAssignment);
	}
	

	/**
	 * @see {@link java.lang.Object#hashCode()}
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result
				+ ((actionTypes == null) ? 0 : actionTypes.hashCode());
		result = prime * result + ((domain == null) ? 0 : domain.hashCode());
		result = prime
				* result
				+ ((domainAssignments == null) ? 0 : domainAssignments
						.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result
				+ ((privileges == null) ? 0 : privileges.hashCode());
		return result;
	}

	/**
	 * @see {@link java.lang.Object#equals(java.lang.Object)}
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (!(obj instanceof Role))
			return false;
		Role other = (Role) obj;
		if (actionTypes == null) {
			if (other.actionTypes != null)
				return false;
		} else if (!actionTypes.equals(other.actionTypes))
			return false;
		if (domain == null) {
			if (other.domain != null)
				return false;
		} else if (!domain.equals(other.domain))
			return false;
		if (domainAssignments == null) {
			if (other.domainAssignments != null)
				return false;
		} else if (!domainAssignments.equals(other.domainAssignments))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (privileges == null) {
			if (other.privileges != null)
				return false;
		} else if (!privileges.equals(other.privileges))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return name;
	}
}