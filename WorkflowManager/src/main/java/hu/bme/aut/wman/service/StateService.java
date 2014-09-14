package hu.bme.aut.wman.service;

import hu.bme.aut.wman.model.ActionType;
import hu.bme.aut.wman.model.State;
import hu.bme.aut.wman.model.StateNavigationEntry;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.TypedQuery;

/**
 * Helps make operations with <code>State</code>.
 * 
 * @version "%I%, %G%"
 */
@Stateless
@LocalBean
public class StateService extends AbstractDataService<State> {

	// private Validator validator;

	@Inject
	ActionTypeService actionTypeService;
	@Inject
	StateNavigationEntryService stateNavigationEntryService;

	// @PostConstruct
	// public void init() {
	// validator = Validation.buildDefaultValidatorFactory().getValidator();
	// }

	public void createWithParent(State parent, State child) {
		save(child);

		State managedParent = attach(parent);
		managedParent.addChild(child);
		save(managedParent);
	}

	public void addActionTypeToState(ActionType actionType, State nextState, State state) {

		// Create state navigation entry
		StateNavigationEntry stateNavigationEntry = new
				StateNavigationEntry(actionType, nextState, state);

		stateNavigationEntryService.save(stateNavigationEntry);

		// add them to the current state
		state.addNextState(stateNavigationEntry);

		// save changes
		save(state);
	}

	/**
	 * Use findByParameters method instead
	 */
	@Deprecated
	public List<State> findByWorkflowId(Long workflowId) {
		try {
			TypedQuery<State> select = em.createNamedQuery("State.findByWorkflowId", State.class);
			select.setParameter("workflowId", workflowId);
			return select.getResultList();
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * Use findByParameters method instead
	 */
	@Deprecated
	public List<State> findRootStatesByWorkflowId(Long workflowId) {
		try {
			TypedQuery<State> select = em.createNamedQuery("State.findRootStatesByWorkflowId", State.class);
			select.setParameter("workflowId", workflowId);
			return select.getResultList();
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * Use findByParameters method instead
	 */
	@Deprecated
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
		ArrayList<Entry<String, Object>> parameterList = new ArrayList<Entry<String, Object>>();
		parameterList.add(new AbstractMap.SimpleEntry<String, Object>("initial", true));
		// FIXME should check if has exactly one element
		return selectByParameters(parameterList).get(0);
	}

	@Override
	protected Class<State> getEntityClass() {
		return State.class;
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
