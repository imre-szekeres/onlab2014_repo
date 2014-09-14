package hu.bme.aut.wman.service;

import hu.bme.aut.wman.model.HistoryEntry;

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

	public List<HistoryEntry> selectByUserName(String userName) {
		List<Entry<String, Object>> parameterList = new ArrayList<Entry<String, Object>>();
		parameterList.add(new AbstractMap.SimpleEntry<String, Object>("userName", userName));
		return callNamedQuery(HistoryEntry.NQ_FIND_BY_USER_NAME, parameterList);
	}

	public List<HistoryEntry> selectByStateId(Long stateId) {
		List<Entry<String, Object>> parameterList = new ArrayList<Entry<String, Object>>();
		parameterList.add(new AbstractMap.SimpleEntry<String, Object>("stateId", stateId));
		return callNamedQuery(HistoryEntry.NQ_FIND_BY_STATE_ID, parameterList);
	}

	public List<HistoryEntry> selectByProjectId(Long projectId) {
		List<Entry<String, Object>> parameterList = new ArrayList<Entry<String, Object>>();
		parameterList.add(new AbstractMap.SimpleEntry<String, Object>("projectId", projectId));
		return callNamedQuery(HistoryEntry.NQ_FIND_BY_PROJECT_ID, parameterList);
	}

	@Override
	protected Class<HistoryEntry> getEntityClass() {
		return HistoryEntry.class;
	}
}
