/**
 * RoleTransferObject.java
 */
package hu.bme.aut.wman.view.objects.transfer;

import java.util.Arrays;
import java.util.List;

/**
 * @author Imre Szekeres
 * @version "%I%, %G%"
 */
public class RoleTransferObject {

	private String roleName;
	private String domainName;
	private String privileges;

	public RoleTransferObject() {
		super();
		roleName = domainName = privileges = "";
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

	public List<String> privileges() {
		return Arrays.asList(privileges.split("\\|"));
	}
}
