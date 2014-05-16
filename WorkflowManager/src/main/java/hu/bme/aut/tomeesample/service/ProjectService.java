/**
 * ProjectService.java 
 */
package hu.bme.aut.tomeesample.service;

import hu.bme.aut.tomeesample.model.Project;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.validation.Validation;
import javax.validation.Validator;

/**
 * @author Imre Szekeres
 * @version "%I%, %G%"
 */
@Stateless
@LocalBean
public class ProjectService {

	@PersistenceContext
	EntityManager em;

	private Validator validator;

	/**
	 * Initialises the <code>Validator</code> for future use.
	 * */
	@PostConstruct
	public void init() {
		validator = Validation.buildDefaultValidatorFactory().getValidator();
	}

	/**
	 * Validates the given project name against the constraints given in the
	 * <code>Project</code> class.
	 *
	 * @param name
	 *            of the project that will be validated
	 * @return true only if the given project name corresponds to the
	 *         constraints given in the class <code>Project</code>
	 * */
	public boolean validateName(String name) {
		return validator.validateValue(Project.class, "name", name).size() == 0;
	}

	/**
	 * Validates the given description against the constraints given in the
	 * <code>Project</code> class.
	 *
	 * @param description
	 *            that will be validated
	 * @return true only if the given description corresponds to the constraints
	 *         given in the class <code>Project</code>
	 * */
	public boolean validateDescription(String description) {
		return validator.validateValue(Project.class, "description", description).size() == 0;
	}

	/**
	 *
	 * @param project
	 * @throws
	 * */
	public void create(Project project) throws Exception {
		em.persist(project);
	}

	/**
	 *
	 * @param project
	 * @return
	 * @throws
	 * */
	public Project update(Project project) throws Exception {
		return em.merge(project);
	}

	/**
	 *
	 * @param project
	 * @throws
	 * */
	public void remove(Project project) throws Exception {
		em.remove(project);
	}

	/**
	 *
	 * @return
	 * */
	public List<Project> findAll() {
		return em.createNamedQuery("Project.findAll", Project.class).getResultList();
	}

	/**
	 *
	 * @param workflowName
	 * @return
	 * */
	public List<Project> findAllByWorkflowName(String workflowName) {
		TypedQuery<Project> selectAll = em.createNamedQuery("Project.findAllByWorkflowName", Project.class);
		selectAll.setParameter("name", workflowName);
		return selectAll.getResultList();
	}

	/**
	 *
	 * @param id
	 * @return
	 * */
	public Project findById(Long id) {
		try {
			TypedQuery<Project> select = em.createNamedQuery("Project.findById", Project.class);
			select.setParameter("id", id);
			return select.getSingleResult();
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 *
	 * @param name
	 * @return
	 * */
	public Project findByName(String name) {
		try {
			TypedQuery<Project> select = em.createNamedQuery("Project.findByName", Project.class);
			select.setParameter("name", name);
			return select.getSingleResult();
		} catch (Exception e) {
			return null;
		}
	}
}
