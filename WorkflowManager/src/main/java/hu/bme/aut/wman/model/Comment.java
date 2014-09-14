package hu.bme.aut.wman.model;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * Entity implementation class for Entity: Comment
 * 
 * @version "%I%, %G%"
 */
@SuppressWarnings("serial")
@Entity
@NamedQueries({
		@NamedQuery(name = "Comment.findAll", query = "SELECT c FROM Comment c"),
		@NamedQuery(name = "Comment.findById", query = "SELECT c FROM Comment c " + "WHERE c.id=:id"),
		@NamedQuery(name = "Comment.findByUser", query = "SELECT c FROM Comment c " + "WHERE c.user.username=:userName"),
		@NamedQuery(name = "Comment.findByProject", query = "SELECT c FROM Comment c " + "WHERE c.project.name=:name") })
public class Comment extends AbstractEntity {

	@Size(min = 1, max = 512)
	private String description;

	@NotNull
	@ManyToOne
	private User user;

	@NotNull
	@ManyToOne
	private Project project;

	@NotNull
	private Date postDate;

	public Comment() {
		super();
	}

	public Comment(User user, Project project, String description) {
		this.setUser(user);
		this.setProject(project);
		this.description = description;
		this.postDate = new Date();
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
		this.user.addComment(this);
	}

	public Project getProject() {
		return project;
	}

	public void setProject(Project project) {
		this.project = project;
		this.project.addComment(this);
	}

	/**
	 * @return the postDate
	 */
	public Date getPostDate() {
		return postDate;
	}

	/**
	 * @param postDate
	 *            the postDate to set
	 */
	public void setPostDate(Date postDate) {
		this.postDate = postDate;
	}

	@Override
	public boolean equals(Object o) {
		if (!(o instanceof Comment)) {
			return false;
		}
		return (((Comment) o).id).equals(this.id);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((description == null) ? 0 : description.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((project == null) ? 0 : project.hashCode());
		result = prime * result + ((user == null) ? 0 : user.hashCode());
		return result;
	}
}
