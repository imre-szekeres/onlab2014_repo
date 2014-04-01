/**
 * User.java
 * 
 * @author Imre Szekeres
 * */
package hu.bme.aut.tomeesample.model;

import java.util.*;

import javax.validation.constraints.*;

import java.io.Serializable;

import javax.persistence.*;


/**
 * Entity implementation class for Entity: User
 * @author Imre Szekeres
 * @version "%I%, %G%"
 */
@SuppressWarnings("serial")
@Entity
@NamedQueries({
	@NamedQuery(name="User.findAll", query="SELECT u FROM User u"),
	@NamedQuery(name="User.findById", query="SELECT u FROM User u "
										   +"WHERE u.id=:id"),
	@NamedQuery(name="User.findByName", query="SELECT u FROM User u "
										     +"WHERE u.username=:username"),
})
public class User implements Serializable {

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long id;
	
	@NotNull
	@Size(min=5, max=16)
	@Column(unique=true)
	private String username;
	
	@NotNull
	@Size(min=7, max=32)
	private String password;
	
	//TODO: refine the term ROLE ~ specify it in JAAS/Shiro
	@NotNull
	private Set<Role> roles;
	
	@OneToMany(mappedBy="user")
	private List<Comment> comments;
	
	@OneToMany(mappedBy="user")
	private Set<ProjectAssignment> projectAssignments;
	
	
	public User() {
		super();
	}
	
	public User(String username, String password, Role role){
		super();
		this.username = username;
		this.password = password;
		this.roles = new java.util.HashSet<>();
		this.roles.add(role);
		this.comments = new java.util.ArrayList<>();
		this.projectAssignments = new java.util.HashSet<>();
	}
	
	public User(String username, String password, Set<Role> roles){
		super();
		this.username = username;
		this.password = password;
		this.roles = roles;
		this.comments = new java.util.ArrayList<>();
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
	
	public void add(Comment comment){
		this.comments.add(comment);
	}
	
	public void add(Role role){
		this.roles.add(role);
	}
	
	public void add(ProjectAssignment assignment){
		this.projectAssignments.add(assignment);
	}
	
	public void setRoles(Set<Role> roles) {
		this.roles = roles;
	}
	
	public void setProjectAssignments(Set<ProjectAssignment> projectAssignments) {
		this.projectAssignments = projectAssignments;
	}
	
	public boolean remove(Comment comment){
		return comments.remove(comment);
	}
	
	public boolean remove(Role role){
		return roles.remove(role);
	}
	
	public boolean remove(ProjectAssignment assignment){
		return projectAssignments.remove(assignment);
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
