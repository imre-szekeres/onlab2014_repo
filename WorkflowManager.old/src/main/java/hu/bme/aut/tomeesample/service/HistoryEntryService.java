/**
 * ActionService.java
 */
package hu.bme.aut.tomeesample.service;

import hu.bme.aut.tomeesample.model.HistoryEntry;

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
public class HistoryEntryService {

	@PersistenceContext
	EntityManager em;

	public void create(HistoryEntry historyEntry) {
		em.persist(historyEntry);
	}

	public void update(HistoryEntry historyEntry) {
		em.merge(historyEntry);
	}

	public void remove(HistoryEntry historyEntry) {
		em.remove(historyEntry);
	}

	public List<HistoryEntry> findAll() {
		return em.createNamedQuery("HistoryEntry.findAll", HistoryEntry.class).getResultList();
	}

	public HistoryEntry findById(Long id) {
		try {
			TypedQuery<HistoryEntry> select = em.createNamedQuery("HistoryEntry.findById", HistoryEntry.class);
			select.setParameter("id", id);
			return select.getSingleResult();
		} catch (Exception e) {
			return null;
		}
	}

	public HistoryEntry findByUserName(String userName) {
		try {
			TypedQuery<HistoryEntry> select = em.createNamedQuery("HistoryEntry.findByUser", HistoryEntry.class);
			select.setParameter("userName", userName);
			return select.getSingleResult();
		} catch (Exception e) {
			return null;
		}
	}

	public HistoryEntry findByStateId(Long stateId) {
		try {
			TypedQuery<HistoryEntry> select = em.createNamedQuery("HistoryEntry.findByState", HistoryEntry.class);
			select.setParameter("stateId", stateId);
			return select.getSingleResult();
		} catch (Exception e) {
			return null;
		}
	}

	public HistoryEntry findByProjectId(Long projectId) {
		try {
			TypedQuery<HistoryEntry> select = em.createNamedQuery("HistoryEntry.findByProject", HistoryEntry.class);
			select.setParameter("projectId", projectId);
			return select.getSingleResult();
		} catch (Exception e) {
			return null;
		}
	}
}
