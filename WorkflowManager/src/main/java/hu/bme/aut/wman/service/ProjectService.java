package hu.bme.aut.wman.service;

import hu.bme.aut.wman.model.Project;
import hu.bme.aut.wman.model.State;
import hu.bme.aut.wman.model.User;
import hu.bme.aut.wman.model.Workflow;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.TypedQuery;

/**
 * Helps make operations with <code>Project</code>.
 * 
 * @version "%I%, %G%"
 */
@Stateless
@LocalBean
public class ProjectService extends AbstractDataService<Project> {

	// private Validator validator;

	// @PostConstruct
	// private void init() {
	// validator = Validation.buildDefaultValidatorFactory().getValidator();
	// }

	// /**
	// * Validates the given project name against the constraints given in the <code>Project</code> class.
	// *
	// * @param name
	// * of the project that will be validated
	// * @return true only if the given project name corresponds to the
	// * constraints given in the class <code>Project</code>
	// * */
	// public boolean validateName(String name) {
	// return validator.validateValue(Project.class, "name", name).size() == 0;
	// }
	//
	// /**
	// * Validates the given description against the constraints given in the <code>Project</code> class.
	// *
	// * @param description
	// * that will be validated
	// * @return true only if the given description corresponds to the constraints
	// * given in the class <code>Project</code>
	// * */
	// public boolean validateDescription(String description) {
	// return validator.validateValue(Project.class, "description", description).size() == 0;
	// }

	public List<Project> selectAllByWorkflowName(String workflowName) {
		List<Entry<String, Object>> parameterList = new ArrayList<Entry<String, Object>>();
		parameterList.add(new AbstractMap.SimpleEntry<String, Object>(Workflow.PR_NAME, workflowName));
		return callNamedQuery(Project.NQ_FIND_BY_WORKFLOW_NAME, parameterList);
	}

	public List<Project> selectByName(String name) {
		ArrayList<Entry<String, Object>> parameterList = new ArrayList<Entry<String, Object>>();
		parameterList.add(new AbstractMap.SimpleEntry<String, Object>(Project.PR_NAME, name));
		return selectByParameters(parameterList);
	}

	public List<Project> selectByCurrentState(State state) {
		ArrayList<Entry<String, Object>> parameterList = new ArrayList<Entry<String, Object>>();
		parameterList.add(new AbstractMap.SimpleEntry<String, Object>(Project.PR_CURRENT_STATE, state));
		return selectByParameters(parameterList);
	}

	// TODO it should be in UserService
	/**
	 * Use findByParameters method instead
	 */
	@Deprecated
	public List<User> findUsersFor(Long projectID) {
		TypedQuery<User> selectFor = em.createQuery("SELECT u FROM User u, ProjectAssignment pa "
				+ "WHERE pa.user = u AND pa.project.id = :projectID", User.class);
		selectFor.setParameter("projectID", projectID);
		return selectFor.getResultList();
	}

	/**
	 * Use findByParameters method instead
	 */
	@Deprecated
	public List<Project> findProjectsFor(String username) {
		TypedQuery<Project> selectFor = em.createQuery("SELECT p FROM Project o, ProjectAssignment pa "
				+ "WHERE pa.u.username = :username AND pa.project = p", Project.class);
		selectFor.setParameter("username", username);
		return selectFor.getResultList();
	}

	@Override
	protected Class<Project> getEntityClass() {
		return Project.class;
	}
}
