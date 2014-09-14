package hu.bme.aut.wman.service;

import hu.bme.aut.wman.model.HistoryEntry;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.TypedQuery;

/**
 * Helps make operations with <code>HistoryEntry</code>.
 * 
 * @version "%I%, %G%"
 */
@Stateless
@LocalBean
public class HistoryEntryService extends AbstractDataService<HistoryEntry> {

	/**
	 * Use findByParameters method instead
	 */
	@Deprecated
	public HistoryEntry findByUserName(String userName) {
		try {
			TypedQuery<HistoryEntry> select = em.createNamedQuery("HistoryEntry.findByUser", HistoryEntry.class);
			select.setParameter("userName", userName);
			return select.getSingleResult();
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * Use findByParameters method instead
	 */
	@Deprecated
	public HistoryEntry findByStateId(Long stateId) {
		try {
			TypedQuery<HistoryEntry> select = em.createNamedQuery("HistoryEntry.findByState", HistoryEntry.class);
			select.setParameter("stateId", stateId);
			return select.getSingleResult();
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * Use findByParameters method instead
	 */
	@Deprecated
	public HistoryEntry findByProjectId(Long projectId) {
		try {
			TypedQuery<HistoryEntry> select = em.createNamedQuery("HistoryEntry.findByProject", HistoryEntry.class);
			select.setParameter("projectId", projectId);
			return select.getSingleResult();
		} catch (Exception e) {
			return null;
		}
	}

	@Override
	protected Class<HistoryEntry> getEntityClass() {
		return HistoryEntry.class;
	}
}
