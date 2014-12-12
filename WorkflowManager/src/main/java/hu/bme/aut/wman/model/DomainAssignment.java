/**
 * DomainAssignment.java
 */
package hu.bme.aut.wman.model;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
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
	@NamedQuery(name = "DomainAssignment.findByDomain", query = "SELECT da FROM DomainAssignment da WHERE da.domain.name = :domainName "),
	@NamedQuery(name = "DomainAssignment.findByUserID", query = "SELECT da FROM DomainAssignment da WHERE da.user.id = :userID "),
	@NamedQuery(name = "DomainAssignment.findByDomainFor", query = "SELECT da FROM DomainAssignment da "+ 
																   "WHERE da.user.id = :userID "+
																		 "AND da.domain.name = :domainName "),
	@NamedQuery(name = "DomainAssignment.findByUsernameFor", query = "SELECT da FROM DomainAssignment da "+ 
																	 "WHERE da.user.username = :username "+
																		 "AND da.domain.name = :domainName "),
	@NamedQuery(name = "DomainAssignment.findAllByUser", query = "SELECT da FROM DomainAssignment da WHERE da.user.username = :username "),
	@NamedQuery(name = "DomainAssignment.findDomainsAndIds", query = "SELECT da.domain.name, da.id FROM DomainAssignment da " +
	                                                                 "WHERE da.user.id = :userID "),
    @NamedQuery(name = "DomainAssignment.deleteById", query = "DELETE FROM DomainAssignment da " +
	                                                          "WHERE da.id = :id "),
    @NamedQuery(name = "DomainAssignment.deleteByUserID", query = "DELETE FROM DomainAssignment da " +
	    	                                                      "WHERE da.user.id = :userID "),
    @NamedQuery(name = "DomainAssignment.deleteByDomainID", query = "DELETE FROM DomainAssignment da " +
	    	    	                                                "WHERE da.domain.id = :domainID ")
})
public class DomainAssignment extends AbstractEntity {

	public static final String NQ_FIND_BY_DOMAIN = "DomainAssignment.findByDomain";
	public static final String NQ_FIND_BY_USER_ID = "DomainAssignment.findByUserID";
	public static final String NQ_FIND_BY_DOMAIN_FOR = "DomainAssignment.findByDomainFor";
	public static final String NQ_FIND_BY_USERNAME_FOR = "DomainAssignment.findByUsernameFor";
	public static final String NQ_FIND_ALL_BY_USER = "DomainAssignment.findAllByUser";
	public static final String NQ_FIND_DOMAINS_AND_IDS = "DomainAssignment.findDomainsAndIds";
	public static final String NQ_DELETED_BY_ID = "DomainAssignment.deleteById";
	public static final String NQ_DELETED_BY_USER_ID = "DomainAssignment.deleteByUserID";
	public static final String NQ_DELETED_BY_DOMAIN_ID = "DomainAssignment.deleteByDomainID";
	
	@NotNull
	@ManyToOne
	private User user;
	
	@NotNull
	@ManyToOne
	private Domain domain;
	
	@NotNull
	@ManyToMany(fetch = FetchType.EAGER)
	private List<Role> userRoles;
	
	public DomainAssignment() {
		super();
		this.userRoles = new ArrayList<>();
	}
	
	public DomainAssignment(User user, Domain domain, Role role) {
		super();
		this.user = user;
		this.domain = domain;
		
		this.userRoles = new ArrayList<>();
		this.userRoles.add(role);
	}

	public DomainAssignment(User user, Domain domain, List<Role> roles) {
		super();
		this.user = user;
		this.domain = domain;
		this.userRoles = roles;
	}


	/**
	 * @return the user
	 */
	public User getUser() {
		return user;
	}

	/**
	 * @param user the user to set
	 */
	public void setUser(User user) {
		this.user = user;
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
	 * @return the userRoles
	 */
	public List<Role> getUserRoles() {
		return userRoles;
	}

	/**
	 * @param userRoles the userRoles to set
	 */
	public void setUserRoles(List<Role> userRoles) {
		this.userRoles = userRoles;
	}
	
	/**
	 * Attempts to maintain the uniqueness of the <code>Role</code>s already contained.
	 * 
	 * @param role
	 * */
	public boolean addUserRole(Role role) {
		if (userRoles.contains( role ))
			return false;
		return this.userRoles.add(role);
	}
	
	/**
	 * @param role
	 * */
	public boolean removeUserRole(Role role) {
		return this.userRoles.remove(role);
	}

	/**
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((domain == null) ? 0 : domain.hashCode());
		result = prime * result + ((user == null) ? 0 : user.hashCode());
		result = prime * result
				+ ((userRoles == null) ? 0 : userRoles.hashCode());
		return result;
	}

	/**
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof DomainAssignment))
			return false;
		DomainAssignment other = (DomainAssignment) obj;
		if (domain == null) {
			if (other.domain != null)
				return false;
		} else if (!domain.equals(other.domain))
			return false;
		if (user == null) {
			if (other.user != null)
				return false;
		} else if (!user.equals(other.user))
			return false;
		if (userRoles == null) {
			if (other.userRoles != null)
				return false;
		} else if (!userRoles.equals(other.userRoles))
			return false;
		return true;
	}
}
