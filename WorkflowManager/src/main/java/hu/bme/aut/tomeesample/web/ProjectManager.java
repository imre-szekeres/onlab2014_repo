/**
 * ProjectManager.java
 */
package hu.bme.aut.tomeesample.web;

import hu.bme.aut.tomeesample.model.ActionType;
import hu.bme.aut.tomeesample.model.Project;
import hu.bme.aut.tomeesample.model.ProjectAssignment;
import hu.bme.aut.tomeesample.model.State;
import hu.bme.aut.tomeesample.model.User;
import hu.bme.aut.tomeesample.model.Workflow;
import hu.bme.aut.tomeesample.service.ProjectAssignmentService;
import hu.bme.aut.tomeesample.service.ProjectService;
import hu.bme.aut.tomeesample.service.StateNavigationEntryService;
import hu.bme.aut.tomeesample.service.UserService;
import hu.bme.aut.tomeesample.service.WorkflowService;
import hu.bme.aut.tomeesample.utils.FacesMessageUtils;
import hu.bme.aut.tomeesample.utils.ManagingUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.enterprise.context.Conversation;
import javax.enterprise.context.ConversationScoped;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.event.ValueChangeEvent;
import javax.faces.validator.ValidatorException;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.log4j.Logger;

/**
 * 
 * @author Imre Szekeres
 * @version "%I%, %G%"
 */
@Named
@ConversationScoped
@SuppressWarnings("serial")
public class ProjectManager implements Serializable {

	private static final Logger logger = Logger.getLogger(ProjectManager.class);

	@Inject
	private Conversation conversation;

	@Inject
	private ProjectService projectService;

	@Inject
	private UserService userService;

	@Inject
	private ProjectAssignmentService assignmentService;

	@Inject
	private StateNavigationEntryService stateNavigationEntryService;

	@Inject
	private WorkflowService workflowService;

	private ActionType selectedActionType;
	private Project project = new Project();
	private String workflowName;
	private Long userID;

	/**
	 * Fetches the <code>Project</code>s already created in the application.
	 * 
	 * @return a list of all the found projects
	 * */
	public List<Project> listProjects() {
		return projectService.findAll();
	}

	/**
	 * Fetches the <code>User</code>s already assigned to the specified
	 * <code>Project</code>.
	 * 
	 * @return a list of all the found projects
	 * */
	public List<User> listAssignedUsers() {
		return project.getId() == null ? new ArrayList<User>() :
				projectService.findUsersFor(project.getId());
	}

	/**
	 * Fetches the <code>User</code>s already assigned to the specified
	 * <code>Project</code>.
	 * 
	 * @return a list of all the found projects
	 * */
	public List<User> filterAssigned(List<User> users) {
		if (project.getProjectAssignments() == null)
			return users;
		List<User> results = new ArrayList<User>();
		for (User user : users) {
			if (notAmong(user, project.getProjectAssignments()))
				results.add(user);
		}
		return results;
	}

	/**
	 * @param user
	 * @param assignments
	 * 
	 * @return a list containing all the <code>User</code>s not bound by the
	 *         given list of <code>ProjectAssignment</code>s.
	 * */
	private static boolean notAmong(User user, Set<ProjectAssignment> assignments) {
		for (ProjectAssignment assignment : assignments) {
			if (user.equals(assignment.getUser()))
				return false;
		}
		return true;
	}

	/**
	 * Fetches the <code>ProjectAssignment</code>s already assigned to the
	 * specified <code>Project</code>.
	 * 
	 * @return a list of all the found projects
	 * */
	public List<ProjectAssignment> listAssignments() {
		return project.getId() == null ? new ArrayList<ProjectAssignment>() :
				new ArrayList<ProjectAssignment>(project.getProjectAssignments());
	}

	public String profileOf(Project project) {
		this.project = project;
		if (conversation.isTransient())
			conversation.begin();
		return "/auth/project_profile.xhtml";
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
	public void validateName(FacesContext context, UIComponent component, Object value) throws ValidatorException {
		if (!projectService.validateName((String) value))
			throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_ERROR, "must be between 5 and 16 characters long", "must be between 5 and 16 characters long"));
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
	public void validateDescription(FacesContext context, UIComponent component, Object value) throws ValidatorException {
		if (!projectService.validateDescription((String) value))
			throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_ERROR, "must be between 13 and 512 characters long", "must be between 13 and 512 characters long"));
	}

	/**
	 * Creates a brand new <code>Project</code> with the specified
	 * <code>Workflow</code> as a template.
	 * 
	 * @return the pageID to navigate to after the transaction
	 * */
	public String create() {
		FacesContext context = FacesContext.getCurrentInstance();
		try {
			Workflow workflow = workflowService.findByName(workflowName);
			project.setWorkflow(workflow);
			project.setCurrentState(workflow.getInitialState());
			projectService.create(project);

			if (!conversation.isTransient())
				conversation.end();

			String message = "project " + project.getName() + " created";
			FacesMessageUtils.infoMessage(context, message);
		} catch (Exception e) {
			FacesMessageUtils.errorMessage(context, "failed to create " + project.getName());
			logger.error(" in projectManager.create", e);
		}
		return "/auth/add_project.xhtml";
	}

	/**
	 * Creates a brand new <code>Project</code> with the specified
	 * <code>Workflow</code> as a template.
	 * 
	 * @return the pageID to navigate to after the transaction
	 * */
	public String remove(Project project) {
		FacesContext context = FacesContext.getCurrentInstance();
		try {

			projectService.removeDetached(project);
			if (!conversation.isTransient())
				conversation.end();

			String message = "project " + project.getName() + " was removed";
			FacesMessageUtils.infoMessage(context, message);
		} catch (Exception e) {
			FacesMessageUtils.errorMessage(context, "failed to remove " + project.getName());
			logger.error(" in projectManager.create", e);
		}
		return "/auth/add_project.xhtml";
	}

	/**
	 * Assigns a <code>User</code> to the specified <code>Project</code> by
	 * creating a new <code>ProjectAssignment</code> that binds a specific
	 * <code>User</code> to a specific <code>Project</code>.
	 * 
	 * @return the pageID to navigate to after the transaction
	 * */
	public String assign() {
		FacesContext context = FacesContext.getCurrentInstance();
		try {
			User user = userService.findById(userID);
			ProjectAssignment pa = new ProjectAssignment(user, project);

			assignmentService.create(pa);

			String message = "assignment to " + project.getName() + " was successful";
			FacesMessageUtils.infoMessage(context, message);
		} catch (Exception e) {
			FacesMessageUtils.infoMessage(context, "failed to create assignment to " + project.getName());
			logger.debug(" failed to create assignment to " + project.getName() + " due to ", e);
		}

		return "/auth/project_profile.xhtml";
	}

	/**
	 * Unassigns a <code>User</code> from the specified <code>Project</code> by
	 * removing the <code>ProjectAssignment</code> that binds a specific
	 * <code>User</code> to a specific <code>Project</code>.
	 * 
	 * @return the pageID to navigate to after the transaction
	 * */
	public String unassign(User user, ProjectAssignment assignment) {
		FacesContext context = FacesContext.getCurrentInstance();
		try {
			project.remove(assignment);
			user.remove(assignment);

			assignmentService.removeDetached(assignment);

			String message = "assignment to " + project.getName() + " was successful";
			FacesMessageUtils.infoMessage(context, message);
		} catch (Exception e) {
			FacesMessageUtils.infoMessage(context, "failed to create assignment to " + project.getName());
			logger.debug(" failed to create assignment to " + project.getName() + " due to ", e);
		}

		return "/auth/project_profile.xhtml";
	}

	public String doAction() {
		FacesContext context = FacesContext.getCurrentInstance();
		try {
			if (selectedActionType != null) {
				State nextState = stateNavigationEntryService.findByActionTypeId(selectedActionType.getId(),
						project.getCurrentState().getId()).getNextState();

				if (nextState == null) {
					FacesMessageUtils.infoMessage(context, "Next state was not found. :(");
				}

				project.setCurrentState(nextState);
				projectService.update(project);
				FacesMessageUtils.infoMessage(context, "Action done on " + project.getName());
			}
		} catch (Exception e) {
			FacesMessageUtils.infoMessage(context, "failed to do action on " + project.getName());
			logger.debug(" failed to do action on " + project.getName() + " due to ", e);
		}

		return "/auth/project_profile.xhtml";
	}

	public void selectedActionTypeChanged(ValueChangeEvent e) {
		selectedActionType = (ActionType) e.getNewValue();
	}

	/**
	 * @return the project
	 */
	public Project getProject() {
		return project;
	}

	/**
	 * @param project
	 *            the project to set
	 */
	public void setProject(Project project) {
		this.project = project;
	}

	/**
	 * @return the workflowName
	 */
	public String getWorkflowName() {
		return workflowName;
	}

	/**
	 * @param workflowName
	 *            the workflowName to set
	 */
	public void setWorkflowName(String workflowName) {
		this.workflowName = workflowName;
	}

	/**
	 * @return the userID
	 */
	public Long getUserId() {
		return userID;
	}

	/**
	 * @param username
	 *            the userID to set
	 */
	public void setUserId(Long id) {
		this.userID = id;
	}

	public ActionType getSelectedActionType() {
		return selectedActionType;
	}

	public void setSelectedActionType(ActionType selectedActionType) {
		this.selectedActionType = selectedActionType;
	}

	@Override
	public String toString() {
		return ManagingUtils.fetchFrom(super.toString(), "ProjectManager");
	}
}