package hu.bme.aut.wman.model;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

/**
 * Entity implementation class for Entity: User
 *
 * @version "%I%, %G%"
 */
@SuppressWarnings("serial")
@Entity
@Table(name = "WM_USER")
@NamedQueries({
	@NamedQuery(name = "User.findUsersForProject", query = "SELECT u FROM User u, ProjectAssignment pa WHERE pa.user = u AND pa.project.id = :projectID"),
	// TODO:
	@NamedQuery(name = "User.findUsersOf", query = "SELECT u FROM User u, Role r, DomainAssignment d "+
												   "WHERE r.name=:roleName "+
												       "AND d.user = u "+
													   "AND r MEMBER OF d.userRoles "),
    @NamedQuery(name = "User.findByName", query = "SELECT u FROM User u " +
											      "WHERE u.username = :username "),
    @NamedQuery(name = "User.findUsersInDomainOf", query = "SELECT DISTINCT u FROM User u, DomainAssignment da1, DomainAssignment da2 " +
                                                           "WHERE da1.domain = da2.domain " + 
    		                                                      "AND da1.user = u AND da2.user.id = :userID ")
})
public class User extends AbstractEntity {

	public static final String NQ_FIND_USERS_FOR_PROJECT = "User.findUsersForProject";
	public static final String NQ_FIND_USERS_OF = "User.findUsersOf";
	public static final String NQ_FIND_BY_NAME = "User.findByName";
	public static final String NQ_FIND_USERS_IN_DOMAIN_OF = "User.findUsersInDomainOf";

	public static final String PR_NAME = "username";
	public static final String PR_PASSWORD = "password";
	public static final String PR_EMAIL = "email";
	public static final String PR_DESCRIPTION = "description";
	public static final String PR_DOMAIN_ASSIGNMENTS = "domainAssignments";
	public static final String PR_COMMENTS = "comments";
	public static final String PR_PROJECT_ASSIGNMENTS = "projectAssignments";
	

	@NotNull
	@Size(min = 5, max = 16, message = "must be between 5 and 16 characters.")
	@Column(unique = true)
	protected String username;

	@NotNull
	@Size(min = 7, max = 32, message = "must be between 7 and 32 characters.")
	protected String password;

	@NotNull
	@Pattern(regexp = "[-0-9a-zA-Z.+_]+@[-0-9a-zA-Z.+_]+\\.[a-zA-Z]{2,4}", message = "invalid format.")
	protected String email;

	@Size(min = 32, max = 1024, message = "must be between 32 and 1024 characters.")
	protected String description;

	@OneToMany(mappedBy = "user", fetch = FetchType.EAGER, cascade = CascadeType.REMOVE)
	private List<Comment> comments = new ArrayList<Comment>();

	// TODO: elaborate ?
	// @OneToMany(mappedBy = "user", fetch = FetchType.EAGER, cascade = CascadeType.REMOVE)
	// private Set<ProjectAssignment> projectAssignments;
	
	public User() {
		super();
	}

	public User(String username, String password, String email, String description) {
		super();
		this.username = username;
		this.password = password;
		this.email = email;
		this.description = description;
		comments = new ArrayList<>();

		// TODO: elaborate ?
		//this.projectAssignmnets = new HashSet<ProjectAssignment>();
	}

	public String getUsername() {
		return username;
	}

	public String getPassword() {
		return password;
	}

	public List<Comment> getComments() {
		return comments;
	}

	// TODO: elaborate ?
	// public Set<ProjectAssignment> getProjectAssignments() {
	// return projectAssignments;
	// }

	public void setUsername(String username) {
		this.username = username;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * Add {@link Comment} to this User
	 *
	 * @param comment
	 *            {@link Comment} to add
	 */
	public void addComment(Comment comment) {
		//		if (comments == null) {
		//			setComments( new ArrayList<Comment>());
		//		}
		comments.add(comment);
	}

	// /**
	// * Add {@link ProjectAssignment} to this User
	// *
	// * @param projectAssignment
	// * {@link ProjectAssignment} to add
	// */
	// public void addProjectAssignment(ProjectAssignment assignment) {
	// if (this.getProjectAssignments() == null) {
	// this.setProjectAssignments(new HashSet<ProjectAssignment>());
	// }
	// this.projectAssignments.add(assignment);
	// }

	// public void setProjectAssignments(Set<ProjectAssignment>
	// projectAssignments) {
	// this.projectAssignments = projectAssignments;
	// }

	/**
	 * @param comments
	 *            the comments to set
	 */
	public void setComments(List<Comment> comments) {
		this.comments = comments;
	}

	/**
	 * Remove {@link Comment} from this User
	 *
	 * @param comment
	 *            {@link Comment} to remove
	 */
	public boolean removeComment(Comment comment) {
		return comments.remove(comment);
	}

	// /**
	// * Remove {@link ProjectAssignment} from this User
	// *
	// * @param projectAssignment
	// * {@link ProjectAssignment} to remove
	// */
	// public boolean removeProjectAssignment(ProjectAssignment assignment) {
	// return projectAssignments.remove(assignment);
	// }


	/**
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		//		result = prime * result
		//				+ ((comments == null) ? 0 : comments.hashCode());
		result = prime * result
				+ ((description == null) ? 0 : description.hashCode());
		result = prime * result + ((email == null) ? 0 : email.hashCode());
		result = prime * result
				+ ((password == null) ? 0 : password.hashCode());
		result = prime * result
				+ ((username == null) ? 0 : username.hashCode());
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
		if (!(obj instanceof User))
			return false;
		User other = (User) obj;
		if (comments == null) {
			if (other.comments != null)
				return false;
		} else if (!comments.equals(other.comments))
			return false;
		if (description == null) {
			if (other.description != null)
				return false;
		} else if (!description.equals(other.description))
			return false;
		if (email == null) {
			if (other.email != null)
				return false;
		} else if (!email.equals(other.email))
			return false;
		if (password == null) {
			if (other.password != null)
				return false;
		} else if (!password.equals(other.password))
			return false;
		if (username == null) {
			if (other.username != null)
				return false;
		} else if (!username.equals(other.username))
			return false;
		return true;
	}

	// TODO: PUT IT INTO BUSINESS LOGIC!!
	/**
	 * Checks whether the <code>User</code> has any <code>Role</code> with the
	 * given name.
	 *
	 * @param name
	 * @return true only if any of the roles has a name like the passed argument
	 * */
	/*public boolean hasRole(String name) {
		if (name == null) {
			return true;
		}
		for (Role role : roles) {
			if (name.equals(role.getName()) || "administrator".equals(role.getName())) {
				return true;
			}
		}
		return false;
	}*/

	// TODO: comment
	/*public boolean hasPrivilege(String name) {
		for (Role r : roles) {
			if (r.hasPrivilege(name)) {
				return true;
			}
		}
		return false;
	}*/

	@Override
	public String toString() {
		return "user -- " + username;
	}
}
