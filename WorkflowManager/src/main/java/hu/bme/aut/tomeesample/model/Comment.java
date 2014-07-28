/**
 * Comment.java
 * 
 * @author Imre Szekeres
 * */
package hu.bme.aut.tomeesample.model;

import hu.bme.aut.tomeesample.utils.ManagingUtils;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Entity implementation class for Entity: Comment
 * 
 * @author Gergely Várkonyi
 */
@SuppressWarnings("serial")
@Entity
@NoArgsConstructor
@EqualsAndHashCode
@Getter
@NamedQueries({
		@NamedQuery(name = "Comment.findAll", query = "SELECT c FROM Comment c"),
		@NamedQuery(name = "Comment.findById", query = "SELECT c FROM Comment c " + "WHERE c.id=:id"),
		@NamedQuery(name = "Comment.findByUser", query = "SELECT c FROM Comment c " + "WHERE c.user.username=:userName"),
		@NamedQuery(name = "Comment.findByProject", query = "SELECT c FROM Comment c " + "WHERE c.project.name=:name") })
public class Comment implements Serializable {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Setter
	@Size(min = 1, max = 512)
	private String description;

	@NotNull
	@ManyToOne
	private User user;

	@NotNull
	@ManyToOne
	private Project project;

	@Setter
	@NotNull
	private Date postDate;

	public Comment(User user, Project project, String description) {
		this.setUser(user);
		this.setProject(project);
		this.description = description;
		this.postDate = new Date();
	}

	public void setUser(User user) {
		this.user = user;
		this.user.add(this);
	}

	public void setProject(Project project) {
		this.project = project;
		this.project.add(this);
	}

	@Override
	public String toString() {
		return ManagingUtils.fetchFrom(super.toString(), "Comment");
	}
}
