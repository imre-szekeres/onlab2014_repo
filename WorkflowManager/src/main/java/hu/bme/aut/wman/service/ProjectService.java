package hu.bme.aut.wman.service;

import hu.bme.aut.wman.exceptions.EntityNotDeletableException;
import hu.bme.aut.wman.model.ActionType;
import hu.bme.aut.wman.model.Comment;
import hu.bme.aut.wman.model.Project;
import hu.bme.aut.wman.model.ProjectAssignment;
import hu.bme.aut.wman.model.Role;
import hu.bme.aut.wman.model.State;
import hu.bme.aut.wman.model.Transition;
import hu.bme.aut.wman.model.User;
import hu.bme.aut.wman.model.Workflow;
import hu.bme.aut.wman.view.objects.NewProjectVO;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map.Entry;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.inject.Inject;

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

	private static final long serialVersionUID = -113131268291497988L;

	@Inject
	private TransitionService transitionService;
	@Inject
	private ProjectAssignmentService projectAssignmentService;
	@Inject
	private UserService userService;
	@Inject
	private CommentService commentService;
	@Inject
	private WorkflowService workflowService;
	@Inject
	private ActionTypeService actionService;

	public void save(NewProjectVO projectVO) {
		Workflow workflow = workflowService.selectById(projectVO.getWorkflowId());
		Project project = new Project();
		project.setName(projectVO.getName());
		project.setWorkflow(workflow);
		project.setDescription(projectVO.getDescription());
		// TODO set current user
		project.setActive(true);

		super.save(project);
	}

	public void save(Long id, String name, String description) {
		Project project = selectById(id);

		project.setName(name);
		project.setDescription(description);
	}

	/**
	 * Closes the project.
	 *
	 * @param project
	 */
	public void close(Project project) {
		if (!project.isActive()) {
			return;
		} else {
			project = attach(project);
			project.setActive(false);
		}
	}

	/**
	 * Finds and closes the project by id.
	 *
	 * @param projectId
	 */
	public void closeById(Long projectId) {
		close(selectById(projectId));
	}

	/**
	 * Reopens the project. It means it will be active.
	 *
	 * @param project
	 * @throws Exception
	 */
	public void reopen(Project project) throws Exception {
		if (project.isActive()) {
			return;
		} else {
			project = attach(project);

			if (project.getWorkflow() == null) {
				throw new Exception("You can not reopen this project, because its workflow has been deleted.");
			} else {
				project.setActive(true);
			}
		}
	}

	/**
	 * Finds and opens the project by id.
	 *
	 * @param projectId
	 * @throws Exception
	 */
	public void reopenById(Long projectId) throws Exception {
		reopen(selectById(projectId));
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
	public void executeAction(Long projectId, Long actionId) {
		Project project = selectById(projectId);
		final ActionType action = actionService.selectById(actionId);

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
	public void commentOnProject(Long projectId, User user, String commentMessage) {
		Project project = selectById(projectId);

		Comment comment = new Comment(user, project, commentMessage);
		comment.setPostDate(new Date());

		commentService.save(comment);
		userService.save(user);
		project.addComment(comment);
		save(project);
	}

	public void assignUser(Long projectId, Long userId) throws IllegalArgumentException {
		Project project = selectById(projectId);
		User user = userService.selectById(userId);

		List<Entry<String, Object>> parameterList = new ArrayList<Entry<String, Object>>();
		parameterList.add(new AbstractMap.SimpleEntry<String, Object>(ProjectAssignment.PR_PROJECT, project));
		parameterList.add(new AbstractMap.SimpleEntry<String, Object>(ProjectAssignment.PR_USER, user));
		if (projectAssignmentService.selectByParameters(parameterList).size()>0) {
			throw new IllegalArgumentException(user.getUsername()+" is already assigned.");
		}

		ProjectAssignment assignment = new ProjectAssignment(user, project);

		projectAssignmentService.save(assignment);
	}

	public void unassignUser(Long projectId, Long userId) throws EntityNotDeletableException {
		Project project = selectById(projectId);
		User user = userService.selectById(userId);

		List<Entry<String, Object>> parameterList = new ArrayList<Entry<String, Object>>();
		parameterList.add(new AbstractMap.SimpleEntry<String, Object>(ProjectAssignment.PR_PROJECT, project));
		parameterList.add(new AbstractMap.SimpleEntry<String, Object>(ProjectAssignment.PR_USER, user));
		ProjectAssignment assignment = projectAssignmentService.selectByParameters(parameterList).get(0);

		projectAssignmentService.delete(assignment);
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
	public List<Project> selectProjectsForUser(String username) {
		List<Entry<String, Object>> parameterList = new ArrayList<Entry<String, Object>>();
		parameterList.add(new AbstractMap.SimpleEntry<String, Object>(User.PR_NAME, username));
		return callNamedQuery(Project.NQ_FIND_PROJECTS_FOR_USER, parameterList);
	}

	/**
	 * Determines whether the <code>User</code> specified by its name owns the required <code>Privilege</code> accounted
	 * as permission in the <code>Domain</code> that the <code>Project</code> specified by its id corresponds to.
	 *
	 * @param username
	 * @param projectID
	 * @param privilegeName
	 * @return whether the given {@link User} has permissions to execute operations on the given {@link Role}
	 * */
	public boolean hasPrivilege(String username, Long projectID, String privilegeName) {
		List<? extends Number> count = resultsOf(username, projectID, privilegeName);
		return count.size() > 0 ? (count.get(0).intValue() > 0) : false;
	}

	/**
	 * Accounts two Shadow <code>Privilege</code>s specific to the domain object <code>Project</code>, the one is
	 * 'Owns' and the other is 'Assigned To' which constraints these relationships to be with the given <code>User</code>, above all
	 * the existing privileges.
	 *
	 * @param username
	 * @param projectID
	 * @param privilegeName
	 * @return a {@link List} containing (at index 0) the number of entities found
	 * */
	private List<? extends Number> resultsOf(String username, Long projectID, String privilegeName) {
		String queryName = null;
		List<Entry<String, Object>> parameters = new ArrayList<Entry<String, Object>>();
		parameters.add(new AbstractMap.SimpleEntry<String, Object>("username", username));
		parameters.add(new AbstractMap.SimpleEntry<String, Object>("projectID", projectID));
		if ("Owns".equals( privilegeName )) {
			queryName = Project.NQ_FIND_COUNT_FOR_OWNER_BY_ID;
		} else if ("Assigned To".equals( privilegeName )) {
			queryName = Project.NQ_FIND_COUNT_FOR_ASSIGNMENT_BY_ID;
		} else {
			queryName = Project.NQ_FIND_COUNT_BY_PRIVILEGE;
			parameters.add(new AbstractMap.SimpleEntry<String, Object>("privilegeName", privilegeName));
		}
		return callNamedQuery(queryName, parameters, Integer.class);
	}


	@Override
	protected Class<Project> getEntityClass() {
		return Project.class;
	}

	public WorkflowService getTestWorkflowService() {
		return workflowService;
	}

	public void setTestWorkflowService(WorkflowService workflowService) {
		this.workflowService = workflowService;
	}
}
