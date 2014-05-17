/**
 * WorkflowService.java
 */
package hu.bme.aut.tomeesample.service;

import hu.bme.aut.tomeesample.model.State;
import hu.bme.aut.tomeesample.model.Workflow;

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
public class WorkflowService {

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
	 * Persists a newly created <code>Workflow</code>.
	 * 
	 * @param workflow
	 *            the newly created worklow that is about to be persisted.
	 * */
	public void create(Workflow workflow) {
		em.persist(workflow);
	}

	public void update(Workflow workflow) {
		em.merge(workflow);
	}

	public void remove(Workflow workflow) {
		em.remove(workflow);
	}

	public void removeDetached(Workflow workflow) {
		Object managed = em.merge(workflow);
		em.remove(managed);
	}

	public void setWorkflowToState(Workflow workflow, State state) {
		workflow.addState(state);
		em.merge(workflow);
	}

	public List<Workflow> findAll() {
		return em.createNamedQuery("Workflow.findAll", Workflow.class).getResultList();
	}

	public Workflow findById(Long id) {
		try {
			TypedQuery<Workflow> select = em.createNamedQuery("Workflow.findById", Workflow.class);
			select.setParameter("id", id);
			return select.getSingleResult();
		} catch (Exception e) {
			return null;
		}
	}

	public Workflow findByName(String name) {
		try {
			TypedQuery<Workflow> select = em.createNamedQuery("Workflow.findByName", Workflow.class);
			select.setParameter("name", name);
			return select.getSingleResult();
		} catch (Exception e) {
			return null;
		}
	}

	public boolean validateName(String name) {
		return validator.validateValue(Workflow.class, "name", name).size() == 0;
	}

	public boolean validateDescription(String description) {
		return validator.validateValue(Workflow.class, "description", description).size() == 0;
	}
}
