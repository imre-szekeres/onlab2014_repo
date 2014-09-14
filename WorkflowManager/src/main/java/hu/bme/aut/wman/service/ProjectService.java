package hu.bme.aut.wman.service;

import hu.bme.aut.wman.model.Project;
import hu.bme.aut.wman.model.User;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import javax.annotation.PostConstruct;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.TypedQuery;

@Stateless
@LocalBean
public class ProjectService extends AbstractDataService<Project> {

	// private Validator validator;

	@PostConstruct
	private void init() {
		this.setClass(Project.class);
		// validator = Validation.buildDefaultValidatorFactory().getValidator();
	}

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

	/**
	 * Use findByParameters method instead
	 */
	@Deprecated
	public List<Project> findAllByWorkflowName(String workflowName) {
		TypedQuery<Project> selectAll = em.createNamedQuery("Project.findAllByWorkflowName", Project.class);
		selectAll.setParameter("name", workflowName);
		return selectAll.getResultList();
	}

	public List<Project> findByName(String name) {
		ArrayList<Entry<String, Object>> parameterList = new ArrayList<Entry<String, Object>>();
		parameterList.add(new AbstractMap.SimpleEntry<String, Object>("name", name));
		return findByParameters(parameterList);
	}

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
}
