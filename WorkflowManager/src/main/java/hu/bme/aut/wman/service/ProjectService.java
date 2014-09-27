package hu.bme.aut.wman.service;

import hu.bme.aut.wman.exceptions.EntityNotDeletableException;
import hu.bme.aut.wman.model.ActionType;
import hu.bme.aut.wman.model.Comment;
import hu.bme.aut.wman.model.Project;
import hu.bme.aut.wman.model.ProjectAssignment;
import hu.bme.aut.wman.model.State;
import hu.bme.aut.wman.model.Transition;
import hu.bme.aut.wman.model.User;
import hu.bme.aut.wman.model.Workflow;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map.Entry;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;

import org.springframework.beans.factory.annotation.Autowired;

import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;

/**
 * Helps make operations with <code>Project</code>.
 * 
 * @version "%I%, %G%"
 */
@Stateless
@LocalBean
public class ProjectService extends AbstractDataService<Project> {

	@EJB(mappedName = "java:module/TransitionService")
	TransitionService transitionService;
	@EJB(mappedName = "java:module/ProjectAssignmentService")
	ProjectAssignmentService projectAssignmentService;
	@EJB(mappedName = "java:module/UserService")
	UserService userService;
	@EJB(mappedName = "java:module/CommentService")
	CommentService commentService;

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

	/**
	 * You can not delete projects, only closing is allowed.
	 * NOTE: If you call this method, it will not delete the project either!
	 */
	@Override
	@Deprecated
	public void delete(Project entity) throws EntityNotDeletableException {
	};

	/**
	 * Closes the project.
	 * 
	 * @param project
	 */
	public void close(Project project) {
		if (!project.isActive()) {
			return;
		} else {
			if (isDetached(project)) {
				project = attach(project);
			}
			project.setActive(false);
		}
	}

	/**
	 * Reopens the project. It means it will be active.
	 * 
	 * @param project
	 */
	public void reopen(Project project) {
		if (project.isActive()) {
			return;
		} else {
			if (isDetached(project)) {
				project = attach(project);
			}
			project.setActive(true);
		}
	}

	/**
	 * Sets the owner on the project.
	 * 
	 * @param project
	 * @param newOwner
	 */
	public void setOwnerOnProject(Project project, User newOwner) {
		if (project.getOwner() == newOwner) {
			return;
		} else {
			if (isDetached(project)) {
				project = attach(project);
			}
			project.setOwner(newOwner);
		}
	}

	/**
	 * Executes the given action on the project. The project will go from the current state into an other.
	 * 
	 * @param project
	 * @param action
	 *            to execute
	 */
	public void executeAction(Project project, final ActionType action) {
		State currentState = project.getCurrentState();

		Collection<Transition> transitions = Collections2.filter(transitionService.selectByParentId(currentState.getId()), new Predicate<Transition>() {

			@Override
			public boolean apply(Transition transition) {
				return transition.getActionType() == action;
			}
		});

		if (!transitions.isEmpty()) {
			project.setCurrentState(transitions.iterator().next().getNextState());
			save(project);
		} else {
			throw new IllegalArgumentException("There is no transition with this " + action + " from the " + currentState + " on " + project + ".");
		}
	}

	/**
	 * Adds a user to the given project.
	 * 
	 * @param projectID
	 * @param userID
	 */
	public void addUser(Long projectID, User user) {
		Project project = selectById(projectID);
		ProjectAssignment projectAssignment = new ProjectAssignment(user, project);

		projectAssignmentService.save(projectAssignment);
	}

	/**
	 * Removes a user from the given project.
	 * 
	 * @param projectId
	 * @param userID
	 * @throws EntityNotDeletableException
	 *             if the assignment is not deletable for some reason
	 */
	public void removeUser(Long projectId, final User user) throws EntityNotDeletableException {
		Collection<ProjectAssignment> assignments = Collections2.filter(projectAssignmentService.selectByProjectId(projectId), new Predicate<ProjectAssignment>() {

			@Override
			public boolean apply(ProjectAssignment projectAssignment) {
				return projectAssignment.getUser() == user;
			}
		});

		if (!assignments.isEmpty()) {
			projectAssignmentService.delete(assignments.iterator().next());
		}
	}

	/**
	 * Creates a comment on the project by the user with the message.
	 * 
	 * @param project
	 * @param user
	 * @param commentMessage
	 */
	public void commentOnProject(Project project, User user, String commentMessage) {
		Comment comment = new Comment(user, project, commentMessage);
		comment.setPostDate(new Date());

		commentService.save(comment);
		userService.save(user);
		save(project);
	}

	/**
	 * @param workflowName
	 * @return the projects which have the given workflow
	 */
	public List<Project> selectAllByWorkflowName(String workflowName) {
		List<Entry<String, Object>> parameterList = new ArrayList<Entry<String, Object>>();
		parameterList.add(new AbstractMap.SimpleEntry<String, Object>(Workflow.PR_NAME, workflowName));
		return callNamedQuery(Project.NQ_FIND_BY_WORKFLOW_NAME, parameterList);
	}

	/**
	 * @param workflowName
	 * @return the projects which have the given name
	 */
	public List<Project> selectByName(String name) {
		ArrayList<Entry<String, Object>> parameterList = new ArrayList<Entry<String, Object>>();
		parameterList.add(new AbstractMap.SimpleEntry<String, Object>(Project.PR_NAME, name));
		return selectByParameters(parameterList);
	}

	/**
	 * @param workflowName
	 * @return the projects which are in the given state
	 */
	public List<Project> selectByCurrentState(State state) {
		ArrayList<Entry<String, Object>> parameterList = new ArrayList<Entry<String, Object>>();
		parameterList.add(new AbstractMap.SimpleEntry<String, Object>(Project.PR_CURRENT_STATE, state));
		return selectByParameters(parameterList);
	}

	/**
	 * @param workflowName
	 * @return the projects which have the given user assigned to it
	 */
	public List<Project> findProjectsForUser(String username) {
		List<Entry<String, Object>> parameterList = new ArrayList<Entry<String, Object>>();
		parameterList.add(new AbstractMap.SimpleEntry<String, Object>(User.PR_NAME, username));
		return callNamedQuery(Project.NQ_FIND_PROJECTS_FOR_USER, parameterList);
	}

	@Override
	protected Class<Project> getEntityClass() {
		return Project.class;
	}
}
