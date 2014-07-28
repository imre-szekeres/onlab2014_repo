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

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * Entity implementation class for Entity: ProjectAssignment
 * 
 */
@Entity
@SuppressWarnings("serial")
@NoArgsConstructor
@EqualsAndHashCode
@ToString
@Getter
@NamedQueries({
		@NamedQuery(name = "ProjectAssignment.findById", query = "SELECT pa FROM ProjectAssignment pa " + "WHERE pa.id=:id"),
		@NamedQuery(name = "ProjectAssignment.findByProjectId", query = "SELECT pa FROM ProjectAssignment pa WHERE pa.project.id=:projectId"),
		@NamedQuery(name = "ProjectAssignment.findByUser", query = "SELECT pa FROM ProjectAssignment pa " + "WHERE pa.user.username=:username") })
public class ProjectAssignment implements Serializable {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Setter
	@NotNull
	@ManyToOne
	private User user;

	@Setter
	@NotNull
	@ManyToOne
	private Project project;

	public ProjectAssignment(User user, Project project) {
		super();
		this.user = user;
		this.project = project;

		this.user.add(this);
		this.project.add(this);
	}
}
