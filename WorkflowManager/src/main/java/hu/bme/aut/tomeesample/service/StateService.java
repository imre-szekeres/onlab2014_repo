/**
 * ActionService.java
 */
package hu.bme.aut.tomeesample.service;

import hu.bme.aut.tomeesample.model.ActionType;
import hu.bme.aut.tomeesample.model.State;

import java.util.Collection;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.validation.Validation;
import javax.validation.Validator;

import org.apache.log4j.Logger;

/**
 * @author Gergely Várkonyi
 */
@Stateless
@LocalBean
public class StateService {

	@PersistenceContext
	EntityManager em;
	private Validator validator;
	@Inject
	ActionTypeService actionTypeService;

	private static final Logger logger = Logger.getLogger(StateService.class);

	/**
	 * Initialises the <code>Validator</code> for future use.
	 * */
	@PostConstruct
	public void init() {
		validator = Validation.buildDefaultValidatorFactory().getValidator();
	}

	public void create(State state) {
		em.persist(state);
	}

	public void update(State state) {
		em.merge(state);
	}

	public void updateAll(Collection<State> states) {
		for (State state : states) {
			em.merge(state);
		}
	}

	public void remove(State state) {
		em.remove(state);
	}

	public void removeDetached(State state) {
		Object managed = em.merge(state);
		em.remove(managed);
	}

	public void createWithParent(State parent, State child) {
		em.persist(child);
		parent.addChild(child);
		em.merge(parent);
	}

	public void addActionTypeToState(Long selectedActionTypeId, Long selectedNextStateId, State state) {
		ActionType actionType = actionTypeService.findById(selectedActionTypeId);

		logger.debug(actionType);

		State nextState = findById(selectedNextStateId);
		// nextState.setDescription("abcd");

		// logger.debug(nextState);
		//
		State managedState = em.merge(state);
		// // add them to the current state
		managedState.setDescription("asdf");
		managedState.addNextState(actionType, nextState);
		//
		// logger.debug(managedState);

		// save changes
		// em.merge(managedState);
	}

	public State getParent(State state) {
		return state.getParent();
	}

	public List<State> findAll() {
		return em.createNamedQuery("State.findAll", State.class).getResultList();
	}

	public State findById(Long id) {
		try {
			TypedQuery<State> select = em.createNamedQuery("State.findById", State.class);
			select.setParameter("id", id);
			return select.getSingleResult();
		} catch (Exception e) {
			return null;
		}
	}

	public List<State> findByWorkflowId(Long workflowId) {
		try {
			TypedQuery<State> select = em.createNamedQuery("State.findByWorkflowId", State.class);
			select.setParameter("workflowId", workflowId);
			return select.getResultList();
		} catch (Exception e) {
			return null;
		}
	}

	public List<State> findRootStatesByWorkflowId(Long workflowId) {
		try {
			TypedQuery<State> select = em.createNamedQuery("State.findRootStatesByWorkflowId", State.class);
			select.setParameter("workflowId", workflowId);
			return select.getResultList();
		} catch (Exception e) {
			return null;
		}
	}

	public List<State> findChildrenByParentId(Long parentId) {
		try {
			TypedQuery<State> select = em.createNamedQuery("State.findChildByParentId", State.class);
			select.setParameter("parentId", parentId);
			return select.getResultList();
		} catch (Exception e) {
			return null;
		}
	}

	public State findInitial() {
		try {
			TypedQuery<State> select = em.createNamedQuery("State.findByInitial", State.class);
			select.setParameter("initial", true);
			return select.getSingleResult();
		} catch (Exception e) {
			return null;
		}
	}

	// public boolean validateName(String name) {
	// return validator.validateValue(State.class, "name", name).size() == 0;
	// }
	//
	// public boolean validateDescription(String description) {
	// return validator.validateValue(State.class, "description",
	// description).size() == 0;
	// }
}
