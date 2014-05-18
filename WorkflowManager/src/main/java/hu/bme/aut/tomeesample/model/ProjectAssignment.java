/**
 * ProjectAssignment.java
 * */
package hu.bme.aut.tomeesample.model;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.validation.constraints.NotNull;

/**
 * Entity implementation class for Entity: ProjectAssignment
 *
 */
@Entity
@SuppressWarnings("serial")
@NamedQueries({
		@NamedQuery(name = "ProjectAssignment.findById", query = "SELECT pa FROM ProjectAssignment pa " + "WHERE pa.id=:id"),
		@NamedQuery(name = "ProjectAssignment.findByProjectId", query = "SELECT pa FROM ProjectAssignment pa WHERE pa.project.id=:projectId"),
		@NamedQuery(name = "ProjectAssignment.findByUser", query = "SELECT pa FROM ProjectAssignment pa " + "WHERE pa.user.username=:username") })
public class ProjectAssignment implements Serializable {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@NotNull
	@ManyToOne
	private User user;

	@NotNull
	@ManyToOne
	private Project project;

	public ProjectAssignment() {
		super();
	}

	public ProjectAssignment(User user, Project project) {
		super();
		this.user = user;
		this.project = project;

		this.user.add(this);
		this.project.add(this);
	}

	public Long getId() {
		return id;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
		this.user.add(this);
	}

	public Project getProject() {
		return project;
	}

	public void setProject(Project project) {
		this.project = project;
		this.project.add(this);
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
		if (!(obj instanceof ProjectAssignment)) {
			return false;
		}
		ProjectAssignment other = (ProjectAssignment) obj;
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
