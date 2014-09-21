package hu.bme.aut.wman.service;

import hu.bme.aut.wman.exceptions.EntityNotFoundException;
import hu.bme.aut.wman.model.StateNavigationEntry;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;

/**
 * Helps make operations with <code>StateNavigationEntry</code>.
 * 
 * @version "%I%, %G%"
 */
@Stateless
@LocalBean
public class StateNavigationEntryService extends AbstractDataService<StateNavigationEntry> {

	public List<StateNavigationEntry> selectByParentId(Long parentId) throws EntityNotFoundException {
		List<Entry<String, Object>> parameterList = new ArrayList<Entry<String, Object>>();
		parameterList.add(new AbstractMap.SimpleEntry<String, Object>("parentId", parentId));
		return callNamedQuery(StateNavigationEntry.NQ_FIND_BY_PARENT_ID, parameterList);
	}

	public List<StateNavigationEntry> selectByActionTypeId(Long typeId, Long parentId) throws EntityNotFoundException {
		List<Entry<String, Object>> parameterList = new ArrayList<Entry<String, Object>>();
		parameterList.add(new AbstractMap.SimpleEntry<String, Object>("typeId", typeId));
		parameterList.add(new AbstractMap.SimpleEntry<String, Object>("parentId", parentId));
		return callNamedQuery(StateNavigationEntry.NQ_FIND_BY_ACTIONTYPE_AND_PARENT_ID, parameterList);
	}

	@Override
	protected Class<StateNavigationEntry> getEntityClass() {
		return StateNavigationEntry.class;
	}
}
