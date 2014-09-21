package hu.bme.aut.wman.service;

import hu.bme.aut.wman.exceptions.EntityNotFoundException;
import hu.bme.aut.wman.model.AbstractEntity;
import hu.bme.aut.wman.model.HistoryEntry;
import hu.bme.aut.wman.model.User;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;

/**
 * Helps make operations with <code>HistoryEntry</code>.
 * 
 * @version "%I%, %G%"
 */
@Stateless
@LocalBean
public class HistoryEntryService extends AbstractDataService<HistoryEntry> {

	public List<HistoryEntry> selectByUserName(String userName) throws EntityNotFoundException {
		List<Entry<String, Object>> parameterList = new ArrayList<Entry<String, Object>>();
		parameterList.add(new AbstractMap.SimpleEntry<String, Object>(User.PR_NAME, userName));
		return callNamedQuery(HistoryEntry.NQ_FIND_BY_USER_NAME, parameterList);
	}

	public List<HistoryEntry> selectByStateId(Long stateId) throws EntityNotFoundException {
		List<Entry<String, Object>> parameterList = new ArrayList<Entry<String, Object>>();
		parameterList.add(new AbstractMap.SimpleEntry<String, Object>(AbstractEntity.PR_ID, stateId));
		return callNamedQuery(HistoryEntry.NQ_FIND_BY_STATE_ID, parameterList);
	}

	public List<HistoryEntry> selectByProjectId(Long projectId) throws EntityNotFoundException {
		List<Entry<String, Object>> parameterList = new ArrayList<Entry<String, Object>>();
		parameterList.add(new AbstractMap.SimpleEntry<String, Object>(AbstractEntity.PR_ID, projectId));
		return callNamedQuery(HistoryEntry.NQ_FIND_BY_PROJECT_ID, parameterList);
	}

	@Override
	protected Class<HistoryEntry> getEntityClass() {
		return HistoryEntry.class;
	}
}
