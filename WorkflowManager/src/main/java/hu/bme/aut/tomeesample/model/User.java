/**
 * User.java
 *
 * @author Imre Szekeres
 * */
package hu.bme.aut.tomeesample.model;

import hu.bme.aut.tomeesample.utils.ManagingUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
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
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Entity implementation class for Entity: User
 * 
 * @author Imre Szekeres
 * @version "%I%, %G%"
 */
@SuppressWarnings("serial")
@Entity
@NoArgsConstructor
@EqualsAndHashCode
@Getter
@NamedQueries({
		@NamedQuery(name = "User.findAll", query = "SELECT u FROM User u"),
		@NamedQuery(name = "User.findById", query = "SELECT u FROM User u "
				+ "WHERE u.id=:id"),
		@NamedQuery(name = "User.findByName", query = "SELECT u FROM User u "
				+ "WHERE u.username=:username"),
})
@Table(name = "WM_USER")
public class User implements Serializable {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Setter
	@NotNull
	@Size(min = 5, max = 16)
	@Column(unique = true)
	private String username;

	@Setter
	@NotNull
	@Size(min = 7, max = 32)
	private String password;

	@Setter
	@NotNull
	@Pattern(regexp = "[-0-9a-zA-Z.+_]+@[-0-9a-zA-Z.+_]+\\.[a-zA-Z]{2,4}")
	private String email;

	@Setter
	@Size(min = 0, max = 1024)
	private String description;

	@Setter
	@NotNull
	@ManyToMany(targetEntity = hu.bme.aut.tomeesample.model.Role.class, fetch = FetchType.EAGER)
	private Set<Role> roles = new HashSet<Role>();;

	@Setter
	@OneToMany(mappedBy = "user", cascade = CascadeType.REMOVE)
	private List<Comment> comments;

	@Setter
	@OneToMany(mappedBy = "user", fetch = FetchType.EAGER, cascade = CascadeType.REMOVE)
	private Set<ProjectAssignment> projectAssignments;

	public User(String username, String password, String email, Role role) {
		this(username, password, email, role, "");
	}

	public User(String username, String password, String email, Set<Role> roles) {
		this(username, password, email, roles, "");
	}

	public User(String username, String password, String email, Role role, String description) {
		this(username, password, email, description);
		this.roles = new java.util.HashSet<Role>();
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
		this.comments = new ArrayList<Comment>();
		this.projectAssignments = new java.util.HashSet<ProjectAssignment>();
	}

	public void add(Comment comment) {
		if (comments == null) {
			comments = new ArrayList<Comment>();
		}
		this.comments.add(comment);
	}

	public void add(Role role) {
		this.roles.add(role);
	}

	public void add(ProjectAssignment assignment) {
		if (this.getProjectAssignments() == null) {
			this.setProjectAssignments(new HashSet<ProjectAssignment>());
		}
		this.projectAssignments.add(assignment);
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

	@Override
	public String toString() {
		return ManagingUtils.fetchFrom(super.toString(), "User");
	}
}
