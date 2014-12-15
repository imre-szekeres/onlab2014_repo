package hu.bme.aut.wman.service;

import hu.bme.aut.wman.model.AbstractEntity;
import hu.bme.aut.wman.model.HistoryEntry;
import hu.bme.aut.wman.model.HistoryEntryEventType;
import hu.bme.aut.wman.model.Project;
import hu.bme.aut.wman.model.User;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map.Entry;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.inject.Inject;

/**
 * Helps make operations with <code>HistoryEntry</code>.
 *
 * @version "%I%, %G%"
 */
@Stateless
@LocalBean
public class HistoryEntryService extends AbstractDataService<HistoryEntry> {

	private static final long serialVersionUID = -6436062508521145745L;

	@Inject
	ProjectService projectService;

	public void log(String username, Date when,	HistoryEntryEventType event, String message, Long projectId) {
		Project project = projectService.selectById(projectId);
		log(username, when, event, message, project, project.getCurrentState().getName());
	}

	public void log(String username, Date when, HistoryEntryEventType event, String message, Project project, String stateName) {
		HistoryEntry historyEntry = new HistoryEntry();
		historyEntry.setUserName(username);
		historyEntry.setWhen(when);
		historyEntry.setEvent(event);
		historyEntry.setMessage(message);
		historyEntry.setProject(project);
		historyEntry.setState(stateName);

		save(historyEntry);
	}

	public List<HistoryEntry> selectByUserName(String userName) {
		List<Entry<String, Object>> parameterList = new ArrayList<Entry<String, Object>>();
		parameterList.add(new AbstractMap.SimpleEntry<String, Object>(User.PR_NAME, userName));
		return callNamedQuery(HistoryEntry.NQ_FIND_BY_USER_NAME, parameterList);
	}
	//
	//	public List<HistoryEntry> selectByStateId(Long stateId) {
	//		List<Entry<String, Object>> parameterList = new ArrayList<Entry<String, Object>>();
	//		parameterList.add(new AbstractMap.SimpleEntry<String, Object>(AbstractEntity.PR_ID, stateId));
	//		return callNamedQuery(HistoryEntry.NQ_FIND_BY_STATE_ID, parameterList);
	//	}

	public List<HistoryEntry> selectByProjectId(Long projectId) {
		List<Entry<String, Object>> parameterList = new ArrayList<Entry<String, Object>>();
		parameterList.add(new AbstractMap.SimpleEntry<String, Object>(AbstractEntity.PR_ID, projectId));
		return callNamedQuery(HistoryEntry.NQ_FIND_BY_PROJECT_ID, parameterList);
	}

	@Override
	protected Class<HistoryEntry> getEntityClass() {
		return HistoryEntry.class;
	}

}
