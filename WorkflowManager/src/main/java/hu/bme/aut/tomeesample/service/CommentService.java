/**
 * ActionService.java
 */
package hu.bme.aut.tomeesample.service;

import hu.bme.aut.tomeesample.model.Comment;
import hu.bme.aut.tomeesample.model.Project;
import hu.bme.aut.tomeesample.model.User;

import java.util.Date;
import java.util.List;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

/**
 * @author Gergely Várkonyi
 */
@Stateless
@LocalBean
public class CommentService {

	@PersistenceContext
	EntityManager em;

	public void create(Comment comment) {
		em.persist(comment);
	}

	public void createFor(Project project, User user, Comment comment) throws Exception {
		Project managedp = em.merge(project);
		User managedu = em.merge(user);
		comment.setPostDate(new Date());
		comment.setProject(managedp);
		comment.setUser(managedu);
		em.persist(comment);
	}

	public void update(Comment comment) {
		em.merge(comment);
	}

	public void remove(Comment comment) {
		em.remove(comment);
	}

	public List<Comment> findAll() {
		return em.createNamedQuery("Comment.findAll", Comment.class).getResultList();
	}

	public Comment findById(Long id) {
		try {
			TypedQuery<Comment> select = em.createNamedQuery("Comment.findById", Comment.class);
			select.setParameter("id", id);
			return select.getSingleResult();
		} catch (Exception e) {
			return null;
		}
	}

	public List<Comment> findByUserName(String userName) {
		TypedQuery<Comment> select = em.createNamedQuery("Comment.findByUser", Comment.class);
		select.setParameter("userName", userName);
		return select.getResultList();
	}

	public List<Comment> findByProjectName(String projectName) {
		TypedQuery<Comment> select = em.createNamedQuery("Comment.findByProject", Comment.class);
		select.setParameter("name", projectName);
		return select.getResultList();
	}
}
