package hu.bme.aut.wman.model;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToMany;
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
public class User extends AbstractEntity {

	public static final String PR_NAME = "username";
	public static final String PR_PASSWORD = "password";
	public static final String PR_EMAIL = "email";
	public static final String PR_DESCRIPTION = "description";
	public static final String PR_ROLES = "roles";
	public static final String PR_COMMENTS = "comments";
	public static final String PR_PROJECT_ASSIGNMENTS = "projectAssignments";

	@NotNull
	@Size(min = 5, max = 16, message = "must be between 5 and 16 chars.")
	@Column(unique = true)
	private String username;

	@NotNull
	@Size(min = 7, max = 32, message = "must be between 7 and 32 chars.")
	private String password;

	@NotNull
	@Pattern(regexp = "[-0-9a-zA-Z.+_]+@[-0-9a-zA-Z.+_]+\\.[a-zA-Z]{2,4}", message = "invalid format.")
	private String email;

	@Size(min = 32, max = 1024, message = "must be between 32 and 1024 chars.")
	private String description;

	@NotNull
	@ManyToMany(targetEntity = hu.bme.aut.wman.model.Role.class, fetch = FetchType.EAGER)
	private Set<Role> roles;

	@OneToMany(mappedBy = "user", cascade = CascadeType.REMOVE)
	private List<Comment> comments;

	@OneToMany(mappedBy = "user", fetch = FetchType.EAGER, cascade = CascadeType.REMOVE)
	private Set<ProjectAssignment> projectAssignments;

	public User() {
		super();
		this.roles = new HashSet<Role>();
	}

	public User(String userName, String password, String email, Role role) {
		this(userName, password, email, role, "");
	}

	public User(String userName, String password, String email, Set<Role> roles) {
		this(userName, password, email, roles, "");
	}

	public User(String userName, String password, String email, Role role, String description) {
		this(userName, password, email, description);
		this.roles = new java.util.HashSet<Role>();
		this.roles.add(role);
	}

	public User(String userName, String password, String email, Set<Role> roles, String description) {
		this(userName, password, email, description);
		this.roles = roles;
	}

	protected User(String username, String password, String email, String description) {
		super();
		this.username = username;
		this.password = password;
		this.email = email;
		this.description = description;
		this.comments = new ArrayList<Comment>();
		this.projectAssignments = new java.util.HashSet<ProjectAssignment>();
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

	public Set<ProjectAssignment> getProjectAssignments() {
		return projectAssignments;
	}

	public Set<Role> getRoles() {
		return roles;
	}

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
		if (comments == null) {
			comments = new ArrayList<Comment>();
		}
		this.comments.add(comment);
	}

	/**
	 * Add {@link Role} to this User
	 * 
	 * @param role
	 *            {@link Role} to add
	 */
	public void addRole(Role role) {
		this.roles.add(role);
	}

	/**
	 * Add {@link ProjectAssignment} to this User
	 * 
	 * @param projectAssignment
	 *            {@link ProjectAssignment} to add
	 */
	public void addProjectAssignment(ProjectAssignment assignment) {
		if (this.getProjectAssignments() == null) {
			this.setProjectAssignments(new HashSet<ProjectAssignment>());
		}
		this.projectAssignments.add(assignment);
	}

	public void setRoles(Set<Role> roles) {
		this.roles = roles;
	}

	public void setProjectAssignments(Set<ProjectAssignment>
			projectAssignments) {
		this.projectAssignments = projectAssignments;
	}

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

	/**
	 * Remove {@link Role} from this User
	 * 
	 * @param role
	 *            {@link Role} to remove
	 */
	public boolean removeRole(Role role) {
		return roles.remove(role);
	}

	/**
	 * Remove {@link ProjectAssignment} from this User
	 * 
	 * @param projectAssignment
	 *            {@link ProjectAssignment} to remove
	 */
	public boolean removeProjectAssignment(ProjectAssignment assignment) {
		return projectAssignments.remove(assignment);
	}

	/**
	 * Checks whether the <code>User</code> has any <code>Role</code> with the
	 * given name.
	 * 
	 * @param name
	 * @return true only if any of the roles has a name like the passed argument
	 * */
	public boolean hasRole(String name) {
		if (name == null) {
			return true;
		}
		for (Role role : roles) {
			if (name.equals(role.getName()) || "administrator".equals(role.getName())) {
				return true;
			}
		}
		return false;
	}

	// TODO: comment
	public boolean hasPrivilege(String name) {
		for (Role r : roles) {
			if (r.hasPrivilege(name)) {
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
		if (!(obj instanceof User)) {
			return false;
		}
		User other = (User) obj;
		if (id == null) {
			if (other.id != null) {
				return false;
			}
		} else if (!id.equals(other.id)) {
			return false;
		}
		return true;
	}
}
