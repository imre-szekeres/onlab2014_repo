package hu.bme.aut.wman.service;

import hu.bme.aut.wman.model.AbstractEntity;
import hu.bme.aut.wman.model.ProjectAssignment;
import hu.bme.aut.wman.model.User;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;

/**
 * Helps make operations with <code>ProjectAssignment</code>.
 * 
 * @version "%I%, %G%"
 */
@Stateless
@LocalBean
class ProjectAssignmentService extends AbstractDataService<ProjectAssignment> {

	public List<ProjectAssignment> selectByUserName(String userName) {
		List<Entry<String, Object>> parameterList = new ArrayList<Entry<String, Object>>();
		parameterList.add(new AbstractMap.SimpleEntry<String, Object>(User.PR_NAME, userName));
		return callNamedQuery(ProjectAssignment.NQ_FIND_BY_USER_NAME, parameterList);
	}

	public List<ProjectAssignment> selectByProjectId(Long projectId) {
		List<Entry<String, Object>> parameterList = new ArrayList<Entry<String, Object>>();
		parameterList.add(new AbstractMap.SimpleEntry<String, Object>(AbstractEntity.PR_ID, projectId));
		return callNamedQuery(ProjectAssignment.NQ_FIND_BY_PROJECT_ID, parameterList);
	}

	@Override
	protected Class<ProjectAssignment> getEntityClass() {
		return ProjectAssignment.class;
	}

}
