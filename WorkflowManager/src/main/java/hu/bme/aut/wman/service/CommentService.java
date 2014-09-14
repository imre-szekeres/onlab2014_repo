package hu.bme.aut.wman.service;

import hu.bme.aut.wman.model.Comment;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.TypedQuery;

@Stateless
@LocalBean
public class CommentService extends AbstractDataService<Comment> {

	@PostConstruct
	private void init() {
		this.setClass(Comment.class);
	}

	// TODO rewrite these methods
	// public User createFor(Project project, User user, Comment comment) throws Exception {
	// Project managedp = em.merge(project);
	// User managedu = em.merge(user);
	// comment.setPostDate(new Date());
	// comment.setProject(managedp);
	// comment.setUser(managedu);
	// em.persist(comment);
	// return managedu;
	// }
	//
	// public User removeFrom(Project project, User user, Comment comment) throws Exception {
	// Project managedp = em.merge(project);
	// User managedu = em.merge(user);
	// Comment managedc = em.merge(comment);
	// managedp.removeComment(managedc);
	// managedu.removeComment(managedc);
	// em.remove(managedc);
	// return managedu;
	// }

	/**
	 * Use findByParameters method instead
	 */
	@Deprecated
	public List<Comment> findByUserName(String userName) {
		TypedQuery<Comment> select = em.createNamedQuery("Comment.findByUser", Comment.class);
		select.setParameter("userName", userName);
		return select.getResultList();
	}

	/**
	 * Use findByParameters method instead
	 */
	@Deprecated
	public List<Comment> findByProjectName(String projectName) {
		TypedQuery<Comment> select = em.createNamedQuery("Comment.findByProject", Comment.class);
		select.setParameter("name", projectName);
		return select.getResultList();
	}
}
