/**
 * UserTransferObject.java
 */
package hu.bme.aut.wman.view.objects.transfer;

import hu.bme.aut.wman.model.User;

import java.util.Arrays;
import java.util.List;

/**
 * @author Imre Szekeres
 * @version "%I%, %G%"
 */
public class UserTransferObject extends User {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -5868958621058535692L;
	private String oldPassword;
	private String confirmPassword;
	private String domainName;
	private String userRoles;


	public UserTransferObject() {
		super("", "", "", "");
		this.oldPassword = this.confirmPassword = this.domainName = this.userRoles = "";
		super.id = -1L;
	}

	public UserTransferObject(User user) {
		super(user.getUsername(), "", user.getEmail(), user.getDescription());
		super.id = user.getId();
	    this.oldPassword = this.confirmPassword = this.domainName = this.userRoles = "";
	}


	/**
	 * @param id
	 * */
	public void setId(long id) {
		super.id = id;
	}

	/**
	 * @return the oldPassword
	 */
	public String getOldPassword() {
		return oldPassword;
	}

	/**
	 * @param oldPassword the oldPassword to set
	 */
	public void setOldPassword(String oldPassword) {
		this.oldPassword = oldPassword;
	}

	/**
	 * @return the confirmPassword
	 */
	public String getConfirmPassword() {
		return confirmPassword;
	}

	/**
	 * @param confirmPassword the confirmPassword to set
	 */
	public void setConfirmPassword(String confirmPassword) {
		this.confirmPassword = confirmPassword;
	}

	/**
	 * @return the domainName
	 */
	public String getDomainName() {
		return domainName;
	}

	/**
	 * @param domainName the domainName to set
	 */
	public void setDomainName(String domainName) {
		this.domainName = domainName;
	}

	/**
	 * @return the userRoles
	 */
	public String getUserRoles() {
		return userRoles;
	}

	/**
	 * @param userRoles the userRoles to set
	 */
	public void setUserRoles(String userRoles) {
		this.userRoles = userRoles;
	}
	
	public List<String> userRoles() {
		return Arrays.asList(userRoles.split("\\|"));
	}
	
	/**
	 * @return a User represented by this transfer object
	 * */
	public User asUser() {
		return new User(username, password, email, description);
	}
}
