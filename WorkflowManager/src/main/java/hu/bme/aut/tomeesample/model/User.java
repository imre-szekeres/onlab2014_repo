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
 *
 */
@SuppressWarnings("serial")
@Entity
@NamedQueries({
	@NamedQuery(name="User.findAll", query="SELECT u FROM User u"),
	@NamedQuery(name="User.findById", query="SELECT u FROM User u "
										   +"WHERE u.id=:id"),
	@NamedQuery(name="User.findByName", query="SELECT u FROM User u "
										     +"WHERE u.username=:username"),
	@NamedQuery(name="User.findCommentsByName", query="SELECT c FROM Comment c "
										     	     +"WHERE c.user.username=:username"),
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
		return projectAssignment.remove(assignment);
	}
	
	@Override
	public boolean equals(Object o){
		if(!(o instanceof User)) return false;
		return ((User)o).id.equals(this.id);
	}
	
	@Override
	public int hashCode(){
		int hash = 0;
		hash = 31*hash + id.hashCode();
		hash = 31*hash + username.hashCode();
		hash = 31*hash + password.hashCode();
		hash = 31*hash + roles.hashCode();
		hash = 31*hash + comments.hashCode();
		hash = 31*hash + projectAssignments.hashCode();
		return hash;
	}
}
