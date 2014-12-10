/**
 * RoleTransferObject.java
 */
package hu.bme.aut.wman.view.objects.transfer;

import hu.bme.aut.wman.model.Privilege;
import hu.bme.aut.wman.model.Role;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Imre Szekeres
 * @version "%I%, %G%"
 */
public class RoleTransferObject {

	private long id;
	private String roleName;
	private String domainName;
	private String privileges;

	public RoleTransferObject(Role role, String domainName) {
		this.id = role.getId();
		this.roleName = role.getName();
		this.domainName = domainName;
		this.privileges = privilegesStringOf(role.getPrivileges());
	}
	
	public RoleTransferObject() {
		super();
		roleName = domainName = privileges = "";
		id = -1L;
	}


	/**
	 * @return the id
	 */
	public long getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(long id) {
		this.id = id;
	}

	/**
	 * @return the roleName
	 */
	public final String getRoleName() {
		return roleName;
	}

	/**
	 * @param roleName the roleName to set
	 */
	public final void setRoleName(String roleName) {
		this.roleName = roleName;
	}

	/**
	 * @return the domainName
	 */
	public final String getDomainName() {
		return domainName;
	}

	/**
	 * @param domainName the domainName to set
	 */
	public final void setDomainName(String domainName) {
		this.domainName = domainName;
	}

	/**
	 * @return the privileges
	 */
	public final String getPrivileges() {
		return privileges;
	}

	/**
	 * @param privileges the privileges to set
	 */
	public final void setPrivileges(String privileges) {
		this.privileges = privileges;
	}

	/**
	 * Transforms the privileges <code>String</code> into a <code>Set</code> of 
	 * <code>Privilege</code> names.
	 * 
	 * @return a {@link Set} of {@link String}s
	 * */
	public Set<String> privileges() {
		Set<String> results = new HashSet<>();
		Collections.addAll(results, privileges.split("\\|"));
		return results;
	}

	/**
	 * Transforms the <code>Collection</code> of <code>Privilege</code>s to a <code>String</code>. 
	 * 
	 * @param privileges
	 * @return a {@link String} representing the {@link Collection} passed as argument
	 * */
	public static final String privilegesStringOf(Collection<Privilege> privileges) {
		if (privileges.isEmpty())
			return "";
		StringBuffer buffer = new StringBuffer();
		for(Privilege p : privileges) {
			buffer.append(p.getName() + "|");
		}
		return buffer.substring(0, buffer.length() - 1).toString();
	}
}
