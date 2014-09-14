package hu.bme.aut.wman.service;

import hu.bme.aut.wman.model.StateNavigationEntry;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.TypedQuery;

@Stateless
@LocalBean
public class StateNavigationEntryService extends AbstractDataService<StateNavigationEntry> {

	@PostConstruct
	private void init() {
		this.setClass(StateNavigationEntry.class);
	}

	/**
	 * Use findByParameters method instead
	 */
	@Deprecated
	public List<StateNavigationEntry> findByParentId(Long parentId) {
		try {
			TypedQuery<StateNavigationEntry> select = em.createNamedQuery("StateNavigationEntry.findByParentId", StateNavigationEntry.class);
			select.setParameter("parentId", parentId);
			return select.getResultList();
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * Use findByParameters method instead
	 */
	@Deprecated
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
