/**
 * ActionService.java
 */
package hu.bme.aut.tomeesample.service;

import hu.bme.aut.tomeesample.model.State;

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
public class StateService {

	@PersistenceContext
	EntityManager em;

	public void create(State state) {
		em.persist(state);
	}

	public void update(State state) {
		em.merge(state);
	}

	public void remove(State state) {
		em.remove(state);
	}

	public List<State> findAll() {
		return em.createNamedQuery("State.findAll", State.class).getResultList();
	}

	public State findById(Long id) {
		try {
			TypedQuery<State> select = em.createNamedQuery("State.findById", State.class);
			select.setParameter("id", id);
			return select.getSingleResult();
		} catch (Exception e) {
			return null;
		}
	}

	public State findByWorkflowId(Long workflowId) {
		try {
			TypedQuery<State> select = em.createNamedQuery("State.findByWorkflowId", State.class);
			select.setParameter("workflowId", workflowId);
			return select.getSingleResult();
		} catch (Exception e) {
			return null;
		}
	}
}
