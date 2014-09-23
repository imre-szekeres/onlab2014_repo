package hu.bme.aut.wman.service;

import hu.bme.aut.wman.exceptions.EntityNotDeletableException;
import hu.bme.aut.wman.model.AbstractEntity;
import hu.bme.aut.wman.model.ActionType;
import hu.bme.aut.wman.model.Project;
import hu.bme.aut.wman.model.State;
import hu.bme.aut.wman.model.Transition;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;

import org.springframework.beans.factory.annotation.Autowired;

/**
 * Helps make operations with <code>State</code>.
 * 
 * @version "%I%, %G%"
 */
@Stateless
@LocalBean
public class StateService extends AbstractDataService<State> {

	// private Validator validator;

	@Autowired
	ActionTypeService actionTypeService;
	@Autowired
	TransitionService transitionService;
	@Autowired
	private ProjectService projectService;

	// @PostConstruct
	// public void init() {
	// validator = Validation.buildDefaultValidatorFactory().getValidator();
	// }

	/**
	 * Deletes the state.
	 * You can not delete a state, if it is an initial state or there is at least one project in that state at the moment.
	 * (If you want delete a state from the client you should call WorkflowService.removeState)
	 * 
	 * @param entity
	 *            the state to delete
	 * @throws EntityNotDeletableException
	 *             if you can not delete that state for same reason
	 */
	@Override
	public void delete(State entity) throws EntityNotDeletableException {
		if (entity.isInitial()) {
			throw new EntityNotDeletableException("The initial state can not be deleted.");
		}

		List<Project> relatedProjects = projectService.selectByCurrentState(entity);
		if (relatedProjects.size() > 0) {
			throw new EntityNotDeletableException("There is " + relatedProjects.size() + " project(s), which are in this state.");
		}

		// Select those navigation entries where this state is a nextState, so we get its parents
		List<Transition> parents = transitionService.selectByNextStateId(entity.getId());
		// Select those navigation entries where this state is a parentState, so we get its nexStates
		List<Transition> nextStates = transitionService.selectByParentId(entity.getId());

		// Create new entries with every parent and every nextState
		List<Transition> transitions = new ArrayList<Transition>();
		for (Transition parent : parents) {
			for (Transition nextState : nextStates) {
				transitions.add(new Transition(parent.getActionType(), nextState.getNextState(), parent.getParent()));
			}
		}

		// TODO
		// I'm pretty sure we should do these in a transaction *****************************************

		// Delete old entries first
		for (Transition parent : parents) {
			transitionService.delete(parent);
		}

		for (Transition nextState : nextStates) {
			transitionService.delete(nextState);
		}

		// Save new entries
		for (Transition transition : transitions) {
			transitionService.save(transition);
		}

		// **********************************************************************************************

		// We can delete that state finally
		super.delete(entity);
	}

	/**
	 * Add a transition to the state.
	 * 
	 * @param state
	 *            the source state
	 * @param actionType
	 *            the action which should be performed
	 * @param nextState
	 *            the destination state
	 */
	public void addTransition(State state, ActionType actionType, State nextState) {
		// Create state navigation entry
		Transition transition = new Transition(actionType, nextState, state);
		transitionService.save(transition);
	}

	/**
	 * Deletes the transition. This equals TransitionService.delete(Transition).
	 * 
	 * @param transition
	 * @throws EntityNotDeletableException
	 *             if the transition is not deletable for some reason.
	 */
	public void removeTransition(Long transtionId) throws EntityNotDeletableException {
		transitionService.delete(transitionService.selectById(transtionId));
	}

	/**
	 * @param workflowId
	 * @return states which are in the workflow
	 */
	public List<State> selectByWorkflowId(Long workflowId) {
		List<Entry<String, Object>> parameterList = new ArrayList<Entry<String, Object>>();
		parameterList.add(new AbstractMap.SimpleEntry<String, Object>(AbstractEntity.PR_ID, workflowId));
		return callNamedQuery(State.NQ_FIND_BY_WORKFLOW_ID, parameterList);
	}

	/**
	 * @return the initial state of the workflow
	 */
	public State selectInitialState(Long workflowId) {
		List<Entry<String, Object>> parameterList = new ArrayList<Entry<String, Object>>();
		parameterList.add(new AbstractMap.SimpleEntry<String, Object>(AbstractEntity.PR_ID, workflowId));
		return callNamedQuery(State.NQ_FIND_INTIAL_IN_WORKFLOW, parameterList).get(0);
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
