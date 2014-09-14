package hu.bme.aut.wman.service;

import hu.bme.aut.wman.model.Action;

import java.util.List;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

@Deprecated
@Stateless
@LocalBean
public class ActionService {

	@PersistenceContext
	EntityManager em;

	public void create(Action action) {
		em.persist(action);
	}

	public void update(Action action) {
		em.merge(action);
	}

	public void remove(Action action) {
		em.remove(action);
	}

	public List<Action> findAll() {
		return em.createNamedQuery("Action.findAll", Action.class).getResultList();
	}

	public Action findById(Long id) {
		try {
			TypedQuery<Action> select = em.createNamedQuery("Action.findById", Action.class);
			select.setParameter("id", id);
			return select.getSingleResult();
		} catch (Exception e) {
			return null;
		}
	}

	public Action findByType(Long typeId) {
		try {
			TypedQuery<Action> select = em.createNamedQuery("Action.findById", Action.class);
			select.setParameter("typeId", typeId);
			return select.getSingleResult();
		} catch (Exception e) {
			return null;
		}
	}
}
