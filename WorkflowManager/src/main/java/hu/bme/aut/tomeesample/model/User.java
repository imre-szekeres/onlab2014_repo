/**
 * User.java
 *
 * @author Imre Szekeres
 * */
package hu.bme.aut.tomeesample.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * Entity implementation class for Entity: User
 *
 * @author Imre Szekeres
 * @version "%I%, %G%"
 */
@SuppressWarnings("serial")
@Entity
@NamedQueries({
		@NamedQuery(name = "User.findAll", query = "SELECT u FROM User u"),
		@NamedQuery(name = "User.findById", query = "SELECT u FROM User u "
				+ "WHERE u.id=:id"),
		@NamedQuery(name = "User.findByName", query = "SELECT u FROM User u "
				+ "WHERE u.username=:username"),
})
@Table(name = "WM_USER")
// just in case table User already exists...
public class User implements Serializable {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@NotNull
	@Size(min = 5, max = 16)
	@Column(unique = true)
	private String username;

	@NotNull
	@Size(min = 7, max = 32)
	private String password;

	@NotNull
	private String email;

	@Size(min = 64, max = 1024)
	private String description;

	// TODO: refine the term ROLE ~ specify it in JAAS/Shiro
	@NotNull
	@ManyToMany(targetEntity = hu.bme.aut.tomeesample.model.Role.class, fetch = FetchType.EAGER)
	private Set<Role> roles;

	@OneToMany(mappedBy = "user")
	private List<Comment> comments;

	@OneToMany(mappedBy = "user")
	private Set<ProjectAssignment> projectAssignments;

	public User() {
		super();
		this.roles = new HashSet<Role>();
		this.roles.add(new Role("visitor"));
	}

	public User(String username, String password, String email, Role role) {
		this(username, password, email, "");
		this.roles = new java.util.HashSet<>();
		this.roles.add(role);
	}

	public User(String username, String password, String email, Set<Role> roles) {
		this(username, password, email, "");
		this.roles = roles;
	}

	public User(String username, String password, String email, Role role, String description) {
		this(username, password, email, description);
		this.roles = new java.util.HashSet<>();
		this.roles.add(role);
	}

	public User(String username, String password, String email, Set<Role> roles, String description) {
		this(username, password, email, description);
		this.roles = roles;
	}

	protected User(String username, String password, String email, String description) {
		super();
		this.username = username;
		this.password = password;
		this.email = email;
		this.description = description;
		this.comments = new ArrayList<>();
		this.projectAssignments = new java.util.HashSet<>();
	}

	public Long getId() {
		return id;
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

	public void add(Comment comment) {
		this.comments.add(comment);
	}

	public void add(Role role) {
		this.roles.add(role);
	}

	public void add(ProjectAssignment assignment) {
		this.projectAssignments.add(assignment);
	}

	public void setRoles(Set<Role> roles) {
		this.roles = roles;
	}

	public void setProjectAssignments(Set<ProjectAssignment>
			projectAssignments) {
		this.projectAssignments = projectAssignments;
	}

	public boolean remove(Comment comment) {
		return comments.remove(comment);
	}

	public boolean remove(Role role) {
		return roles.remove(role);
	}

	public boolean remove(ProjectAssignment assignment) {
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
		for (Role role : roles) {
			if (role.getName().equals(name))
				return true;
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
