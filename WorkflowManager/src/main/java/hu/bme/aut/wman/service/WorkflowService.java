package hu.bme.aut.wman.service;

import hu.bme.aut.wman.exceptions.EntityNotDeletableException;
import hu.bme.aut.wman.model.Project;
import hu.bme.aut.wman.model.Role;
import hu.bme.aut.wman.model.State;
import hu.bme.aut.wman.model.User;
import hu.bme.aut.wman.model.Workflow;
import hu.bme.aut.wman.model.graph.GraphNode;
import hu.bme.aut.wman.model.graph.StateGraph;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map.Entry;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.inject.Inject;

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

	@Inject
	private ProjectService projectService;
	@Inject
	private StateService stateService;
	@Inject
	private StateGraphService graphService;

	@Override
	public void save(Workflow entity) {
		super.save(entity);

		Workflow workflow = attach(entity);
		StateGraph graph = new StateGraph(workflow.getId());

		for (State state : workflow.getStates()) {
			GraphNode graphPoint = new GraphNode();
			graphPoint.setStateId(state.getId());
			graphPoint.setLabel(state.getName());
			graphPoint.setContent(state.getDescription());
			graphPoint.setInitial(state.isInitial());
			graphPoint.setX(-1);
			graphPoint.setY(-1);
			graphPoint.setGraph(graph);

			graph.getPoints().add(graphPoint);
		}

		graphService.save(graph);
	};

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
	public void removeState(Long workflowId, Long stateId) throws EntityNotDeletableException {
		State state = stateService.selectById(stateId);
		stateService.delete(state);

		Workflow workflow = selectById(workflowId);
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
		} else if (selectByName(workflow.getName()) != null) {
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
		List<Workflow> result = selectByParameters(parameterList);
		return result.isEmpty() ? null : result.get(0);
	}

	/**
	 * @return the initial state of the workflow
	 */
	public State getInitialState(Workflow workflow) {
		return stateService.selectInitialState(workflow.getId());
	}

	/**
	 * Determines whether the <code>User</code> specified by its name owns the required <code>Privilege</code> accounted
	 * as permission in the <code>Domain</code> that the <code>Workflow</code> specified by its id corresponds to.
	 * 
	 * @param username
	 * @param workflowID
	 * @param privilegeName
	 * @return whether the given {@link User} has permissions to execute operations on the given {@link Role}
	 * */
	public boolean hasPrivilege(String username, Long workflowID, String privilegeName) {
		List<Entry<String, Object>> parameters = new ArrayList<Entry<String, Object>>();
		parameters.add(new AbstractMap.SimpleEntry<String, Object>("username", username));
		parameters.add(new AbstractMap.SimpleEntry<String, Object>("privilegeName", privilegeName));
		parameters.add(new AbstractMap.SimpleEntry<String, Object>("workflowID", workflowID));
		List<? extends Number> count = callNamedQuery(Workflow.NQ_FIND_COUNT_BY_PRIVILEGE, parameters, Integer.class);
		return count.size() > 0 ? (count.get(0).intValue() > 0) : false;
	}

	/**
	 * Determines whether the <code>User</code> specified by its name owns the required <code>Privilege</code> accounted
	 * as permission in the <code>Domain</code> that the <code>Project</code> specified by its name corresponds to.
	 * 
	 * @param username
	 * @param workflowName
	 * @param privilegeName
	 * @return whether the given {@link User} has permissions to execute operations on the given {@link Role}
	 * */
	public boolean hasPrivilege(String username, String workflowName, String privilegeName) {
		List<Entry<String, Object>> parameters = new ArrayList<Entry<String, Object>>();
		parameters.add(new AbstractMap.SimpleEntry<String, Object>("username", username));
		parameters.add(new AbstractMap.SimpleEntry<String, Object>("privilegeName", privilegeName));
		parameters.add(new AbstractMap.SimpleEntry<String, Object>("workflowName", workflowName));
		List<? extends Number> count = callNamedQuery(Workflow.NQ_FIND_COUNT_BY_PRIVILEGE_AND_NAME, parameters, Integer.class);
		return count.size() > 0 ? (count.get(0).intValue() > 0) : false;
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
