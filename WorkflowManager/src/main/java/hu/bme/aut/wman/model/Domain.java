/**
 * Domain.java
 */
package hu.bme.aut.wman.model;

import hu.bme.aut.wman.view.DragNDroppable;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.validation.constraints.NotNull;
/**
 * @author Imre Szekeres
 * @version "%I%, %G%"
 */
@SuppressWarnings("serial")
@Entity
@NamedQueries({
	@NamedQuery(name = "Domain.findByName", query = "SELECT d FROM Domain d WHERE d.name = :domainName "),
	@NamedQuery(name = "Domain.findByRoleID", query = "SELECT d FROM Domain d, Role r "+
	                                                  "WHERE r.id = :roleID AND r MEMBER OF d.roles ")
})
public class Domain extends AbstractEntity implements DragNDroppable {
	
	public static final String PR_NAME = "name";
	public static final String PR_DOMAIN = "domain";
	public static final String PR_WORKFLOWS = "workflows";
	public static final String PR_ACTION_TYPES = "actionTypes";
	public static final String PR_DOMAIN_ASSIGNMENTS = "domainAssignments";

	@Column(unique = true)
	@NotNull
	private String name;
	
	@NotNull
	@OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.REMOVE)
	private List<Role> roles;
	
	@NotNull
	@OneToMany(mappedBy = "domain", cascade = CascadeType.REMOVE)
	private Set<Workflow> workflows;
		
	@NotNull
	@OneToMany(mappedBy = "domain")
	private Set<ActionType> actionTypes;

	
	public Domain() {
		super();
		this.roles = new ArrayList<>();
		this.workflows = new HashSet<>();
		this.actionTypes = new HashSet<>();
	}
	
	public Domain(String name) {
		super();
		this.name = name;
		this.roles = new ArrayList<>();
		this.workflows = new HashSet<>();
		this.actionTypes = new HashSet<>();
	}
	
	// TODO: the setting of role names is the RESPONSIBILITY of the Service layer..
	public Domain(String name, List<Role> initialRoles) {
		super();
		this.name = name;
		this.roles = initialRoles;
		this.workflows = new HashSet<>();
		this.actionTypes = new HashSet<>();
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the workflows
	 */
	public Set<Workflow> getWorkflows() {
		return workflows;
	}

	/**
	 * @param workflows the workflows to set
	 */
	public void setWorkflows(Set<Workflow> workflows) {
		this.workflows = workflows;
	}

	/**
	 * @return the actionTypes
	 */
	public Set<ActionType> getActionTypes() {
		return actionTypes;
	}

	/**
	 * @param actionTypes the actionTypes to set
	 */
	public void setActionTypes(Set<ActionType> actionTypes) {
		this.actionTypes = actionTypes;
	}
	
	/**
	 * @return the roles
	 */
	public List<Role> getRoles() {
		return roles;
	}

	/**
	 * @param roles the roles to set
	 */
	public void setRoles(List<Role> roles) {
		this.roles = roles;
	}
	
	/**
	 * @param role
	 * */
	public boolean addRole(Role role) {
		return this.roles.add(role);
	}
	
	/**
	 * @param roleName
	 * */
	public Role roleOf(String roleName) {
		for(Role r : roles) {
			if (r.getName().equals( roleName ))
				return r;
		}
		return null;
	}
	
	/**
	 * @param role
	 * */
	public boolean removeRole(Role role) {
		return this.roles.remove(role);
	}

	/**
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result
				+ ((actionTypes == null) ? 0 : actionTypes.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((roles == null) ? 0 : roles.hashCode());
		result = prime * result
				+ ((workflows == null) ? 0 : workflows.hashCode());
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
		if (!(obj instanceof Domain))
			return false;
		Domain other = (Domain) obj;
		if (actionTypes == null) {
			if (other.actionTypes != null)
				return false;
		} else if (!actionTypes.equals(other.actionTypes))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (roles == null) {
			if (other.roles != null)
				return false;
		} else if (!roles.equals(other.roles))
			return false;
		if (workflows == null) {
			if (other.workflows != null)
				return false;
		} else if (!workflows.equals(other.workflows))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Domain -- " + name;
	}

	@Override
	public String getValue() {
		return this.name;
	}

	@Override
	public String getLabel() {
		return this.name;
	}
}
