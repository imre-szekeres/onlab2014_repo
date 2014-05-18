/**
 * ProjectManager.java
 */
package hu.bme.aut.tomeesample.web;

import hu.bme.aut.tomeesample.model.Project;
import hu.bme.aut.tomeesample.model.User;
import hu.bme.aut.tomeesample.model.Workflow;
import hu.bme.aut.tomeesample.service.ProjectAssignmentService;
import hu.bme.aut.tomeesample.service.ProjectService;
import hu.bme.aut.tomeesample.service.UserService;
import hu.bme.aut.tomeesample.service.WorkflowService;
import hu.bme.aut.tomeesample.utils.FacesMessageUtils;
import hu.bme.aut.tomeesample.utils.ManagingUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.enterprise.context.Conversation;
import javax.enterprise.context.ConversationScoped;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
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
	private ProjectAssignmentService assignmentService;

	@Inject
	private UserService userService;

	@Inject
	private WorkflowService workflowService;
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

	public String profileOf(Project project) {
		this.project = project;
		if (conversation.isTransient())
			conversation.begin();
		return "project_profile";
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
		// TODO: check
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
			logger.debug(" " + message);
		} catch (Exception e) {
			FacesMessageUtils.errorMessage(context, "failed to create " + project.getName());
			logger.error(" in projectManager.create", e);
		}
		return "add_projects";
	}

	/***/
	public String assign() {
		FacesContext context = FacesContext.getCurrentInstance();
		try {

			/*
			 * User user = userService.findByName(username);
			 * 
			 * logger.debug(user); logger.debug(project);
			 * 
			 * logger.debug(" pas: " + user.getProjectAssignments());
			 * user.setProjectAssignments(new HashSet<ProjectAssignment>());
			 * logger.debug(" pas: " + user.getProjectAssignments());
			 * 
			 * ProjectAssignment pa = new ProjectAssignment(); logger.debug(pa);
			 * 
			 * pa.setUser(user); pa.setProject(project);
			 * 
			 * assignmentService.create(pa);
			 */

			// TODO:
			assignmentService.createFor(userID, project.getId());

			String message = "assignment to " + project.getName() + " was successful";
			FacesMessageUtils.infoMessage(context, message);
			logger.debug(" " + message);
		} catch (Exception e) {
			FacesMessageUtils.infoMessage(context, "failed to create assignment to " + project.getName());
			logger.debug(" failed to create assignment to " + project.getName() + " due to ", e);
		}

		return "project_profile";
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

	@Override
	public String toString() {
		return ManagingUtils.fetchFrom(super.toString(), "ProjectManager");
	}
}
