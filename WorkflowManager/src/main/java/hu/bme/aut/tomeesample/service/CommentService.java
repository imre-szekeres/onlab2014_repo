/**
 * ActionService.java
 */
package hu.bme.aut.tomeesample.service;

import hu.bme.aut.tomeesample.model.Comment;

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

	public Comment findByUserName(String userName) {
		try {
			TypedQuery<Comment> select = em.createNamedQuery("Comment.findByUser", Comment.class);
			select.setParameter("userName", userName);
			return select.getSingleResult();
		} catch (Exception e) {
			return null;
		}
	}
}
