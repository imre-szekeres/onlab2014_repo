/**
 * Privilege.java
 */
package hu.bme.aut.wman.model;

import hu.bme.aut.wman.view.DragNDroppable;

import javax.persistence.Entity;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.validation.constraints.NotNull;

/**
 * @author Imre Szekeres
 * @version "%I%, %G%"
 */
@SuppressWarnings("serial")
@Entity
@NamedQueries({
	@NamedQuery(name = "Privilege.findAllNamesByUsername", query = "SELECT DISTINCT p.name FROM Privilege p, DomainAssignment da, Role r " +
                                                                   "WHERE p MEMBER OF r.privileges " +
			                                                            "AND r MEMBER OF da.userRoles " +
                                                                        "AND da.user.username = :username ")
})
public class Privilege extends AbstractEntity implements DragNDroppable {

	public static final String NQ_FIND_ALL_NAMES_BY_USERNAME = "Privilege.findAllNamesByUsername";
	
	public static final String PR_NAME = "name";
	public static final String PR_ROLES = "roles";

	@NotNull
	private String name;

	@Deprecated
	public Privilege() {
		this("");
	}

	public Privilege(String name) {
		super();
		this.name = name;
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
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((name == null) ? 0 : name.hashCode());
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
		if (!(obj instanceof Privilege))
			return false;
		Privilege other = (Privilege) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}
	
	@Override
	public String toString() {
		return "Privilege -- " + name;
	}
	
	public String getValue() {
		return name;
	}
	
	public String getLabel() {
		return name;
	}
	
	public String getOwner() {
		return "";
	}
}
