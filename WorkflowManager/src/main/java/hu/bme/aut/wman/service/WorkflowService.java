package hu.bme.aut.wman.service;

import hu.bme.aut.wman.exceptions.EntityNotDeletableException;
import hu.bme.aut.wman.model.Project;
import hu.bme.aut.wman.model.State;
import hu.bme.aut.wman.model.Workflow;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map.Entry;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;

import org.springframework.beans.factory.annotation.Autowired;

import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;

/**
 * Helps make operations with <code>Workflow</code>.
 * 
 * @version "%I%, %G%"
 */
@Stateless
@LocalBean
public class WorkflowService extends AbstractDataService<Workflow> {

	// private Validator validator;

	// @PostConstruct
	// public void init() {
	// validator = Validation.buildDefaultValidatorFactory().getValidator();
	// }

	@Autowired
	private ProjectService projectService;
	@Autowired
	private StateService stateService;

	/**
	 * Deletes the workflow.
	 * You can not delete a workflow, if there is at least one <b>active</b> project in with that workflow.
	 * 
	 * @param entity
	 *            the workflow to delete
	 * @throws EntityNotDeletableException
	 *             if you can not delete that workflow for same reason
	 */
	@Override
	public void delete(Workflow entity) throws EntityNotDeletableException {
		// Select only active related projects
		Collection<Project> relatedActiveProjects = Collections2.filter(projectService.selectAllByWorkflowName(entity.getName()), new Predicate<Project>() {

			@Override
			public boolean apply(Project project) {
				return project.isActive();
			}
		});

		if (relatedActiveProjects.size() > 0) {
			throw new EntityNotDeletableException("There are " + relatedActiveProjects.size() + " active project(s) using this workflow");
		} else {
			super.delete(entity);
		}
	}

	/**
	 * Adds the state to the workflow.
	 * 
	 * @param workflow
	 * @param state
	 */
	public void addState(Workflow workflow, State state) {
		addStates(workflow, Arrays.<State> asList(state));
	}

	/**
	 * Adds the states to the workflow.
	 * 
	 * @param workflow
	 * @param states
	 */
	public void addStates(Workflow workflow, List<State> states) {
		for (State state : states) {
			state.setWorkflow(workflow);
		}
		workflow.getStates().addAll(states);
		save(workflow);
	}

	/**
	 * Remove the state from the workflow, also deletes the state.
	 * 
	 * @param workflow
	 * @param state
	 * @throws EntityNotDeletableException
	 *             if the state was not deletable for some reason
	 */
	public void removeState(Workflow workflow, State state) throws EntityNotDeletableException {
		stateService.delete(state);
		workflow.getStates().remove(state);
		save(workflow);
	}

	/**
	 * Verifies if a workflow is valid or not.
	 * A workflow is valid, if it has at least one state and
	 * if it has only one state that should be the initial state.
	 * 
	 * @param workflow
	 * @return true if the workflow is valid
	 */
	public boolean verify(Workflow workflow) {
		if (workflow.getStates().size() == 0) {
			return false;
		} else if (workflow.getStates().size() == 1 && workflow.getStates().get(0) != workflow.getInitialState()) {
			return false;
		} else {
			return true;
		}
	}

	/**
	 * @param name
	 * @return the workflow, which has the given name
	 */
	public Workflow selectByName(String name) {
		ArrayList<Entry<String, Object>> parameterList = new ArrayList<Entry<String, Object>>();
		parameterList.add(new AbstractMap.SimpleEntry<String, Object>(Workflow.PR_NAME, name));
		return selectByParameters(parameterList).get(0);
	}

	/**
	 * @return the initial state of the workflow
	 */
	public State getInitialState(Workflow workflow) {
		return stateService.selectInitialState(workflow.getId());
	}

	@Override
	protected Class<Workflow> getEntityClass() {
		return Workflow.class;
	}

	// public boolean validateName(String name) {
	// return validator.validateValue(Workflow.class, "name", name).size() == 0;
	// }
	//
	// public boolean validateDescription(String description) {
	// return validator.validateValue(Workflow.class, "description", description).size() == 0;
	// }
}
