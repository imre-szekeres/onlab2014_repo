/**
 * ActionService.java
 */
package hu.bme.aut.tomeesample.service;

import hu.bme.aut.tomeesample.model.StateNavigationEntry;

import java.util.List;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

/**
 * @author Gergely Varkonyi
 */
@Stateless
@LocalBean
public class StateNavigationEntryService {

	@PersistenceContext
	EntityManager em;

	public void create(StateNavigationEntry stateNavigationEntry) {
		em.persist(stateNavigationEntry);
	}

	public void update(StateNavigationEntry stateNavigationEntry) {
		em.merge(stateNavigationEntry);
	}

	public void remove(StateNavigationEntry stateNavigationEntry) {
		em.remove(stateNavigationEntry);
	}

	public void removeDetached(StateNavigationEntry stateNavigationEntry) {
		Object managed = em.merge(stateNavigationEntry);
		em.remove(managed);
	}

	public List<StateNavigationEntry> findAll() {
		return em.createNamedQuery("StateNavigationEntry.findAll", StateNavigationEntry.class).getResultList();
	}

	public StateNavigationEntry findById(Long id) {
		try {
			TypedQuery<StateNavigationEntry> select = em.createNamedQuery("StateNavigationEntry.findById", StateNavigationEntry.class);
			select.setParameter("id", id);
			return select.getSingleResult();
		} catch (Exception e) {
			return null;
		}
	}

	public List<StateNavigationEntry> findByParentId(Long parentId) {
		try {
			TypedQuery<StateNavigationEntry> select = em.createNamedQuery("StateNavigationEntry.findByParentId", StateNavigationEntry.class);
			select.setParameter("parentId", parentId);
			return select.getResultList();
		} catch (Exception e) {
			return null;
		}
	}

	public StateNavigationEntry findByActionTypeId(Long typeId, Long parentId) {
		try {
			TypedQuery<StateNavigationEntry> select = em.createNamedQuery("StateNavigationEntry.findByActionType", StateNavigationEntry.class);
			select.setParameter("typeId", typeId);
			select.setParameter("parentId", parentId);
			StateNavigationEntry ret = select.getSingleResult();
			return ret;
		} catch (Exception e) {
			return null;
		}
	}
}
