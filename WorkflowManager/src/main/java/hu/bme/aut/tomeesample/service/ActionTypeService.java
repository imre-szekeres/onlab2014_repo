/**
 * ActionService.java
 */
package hu.bme.aut.tomeesample.service;

import hu.bme.aut.tomeesample.model.ActionType;

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
public class ActionTypeService {

	@PersistenceContext
	EntityManager em;

	public void create(ActionType actionType) {
		em.persist(actionType);
	}

	public void update(ActionType actionType) {
		em.merge(actionType);
	}

	public void remove(ActionType actionType) {
		em.remove(actionType);
	}

	public void removeDetached(ActionType actionType) {
		Object managed = em.merge(actionType);
		em.remove(managed);
	}

	public List<ActionType> findAll() {
		return em.createNamedQuery("ActionType.findAll", ActionType.class).getResultList();
	}

	public ActionType findById(Long id) {
		try {
			TypedQuery<ActionType> select = em.createNamedQuery("ActionType.findById", ActionType.class);
			select.setParameter("id", id);
			return select.getSingleResult();
		} catch (Exception e) {
			return null;
		}
	}
}
