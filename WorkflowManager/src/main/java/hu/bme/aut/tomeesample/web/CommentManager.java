/**
 * CommentManager.java
 */
package hu.bme.aut.tomeesample.web;

import hu.bme.aut.tomeesample.model.Comment;
import hu.bme.aut.tomeesample.model.Project;
import hu.bme.aut.tomeesample.model.User;
import hu.bme.aut.tomeesample.service.CommentService;
import hu.bme.aut.tomeesample.utils.FacesMessageUtils;
import hu.bme.aut.tomeesample.utils.ManagingUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.log4j.Logger;

/**
 * 
 * @author Imre Szekeres
 * @version "%I%, %G%"
 */
@Named
@RequestScoped
public class CommentManager {

	private static final Logger logger = Logger.getLogger(CommentManager.class);

	@Inject
	private CommentService commentService;
	private Comment comment;

	@PostConstruct
	public void init() {
		comment = new Comment();
	}

	/**
	 * Fetches the <code>Comment</code>s already posted by the given
	 * <code>User</code>.
	 *
	 * @return a list of all the found comments
	 * */
	public List<Comment> listCommentsFor(User user) {
		return (user == null || user.getId() == null) ?
				new ArrayList<Comment>() :
				user.getComments();
	}

	/**
	 * @param project
	 * @param subject
	 * @return the pageID to navigate to
	 * */
	public String createFor(Project project, User subject) {
		FacesContext context = FacesContext.getCurrentInstance();
		try {

			comment.setProject(project);
			comment.setUser(subject);
			comment.setPostDate(new Date());
			commentService.create(comment);

			String message = subject.getUsername() + "commented on " + project.getName();
			FacesMessageUtils.infoMessage(context, message);
			logger.debug(" " + message);
		} catch (Exception e) {
			FacesMessageUtils.errorMessage(context, "failed to comment on " + project.getName());
			logger.error(" " + subject.getUsername() + " failed to comment on " + project.getName(), e);
		}
		return null;
	}

	/**
	 * @return the comment
	 */
	public Comment getComment() {
		return comment;
	}

	/**
	 * @param comment
	 *            the comment to set
	 */
	public void setComment(Comment comment) {
		this.comment = comment;
	}

	@Override
	public String toString() {
		return ManagingUtils.fetchFrom(super.toString(), "CommentManager");
	}
}
