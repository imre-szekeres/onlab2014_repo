/**
 * ProfileManager.java
 */
package hu.bme.aut.tomeesample.web;

import hu.bme.aut.tomeesample.model.ProjectAssignment;
import hu.bme.aut.tomeesample.model.Role;
import hu.bme.aut.tomeesample.model.User;
import hu.bme.aut.tomeesample.service.RoleService;
import hu.bme.aut.tomeesample.service.UserService;
import hu.bme.aut.tomeesample.utils.FacesMessageUtils;
import hu.bme.aut.tomeesample.utils.ManagingUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Conversation;
import javax.enterprise.context.ConversationScoped;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.ValidatorException;
import javax.inject.Inject;
import javax.inject.Named;

import lombok.Getter;
import lombok.Setter;

import org.apache.log4j.Logger;

/**
 * 
 * @author Imre Szekeres
 * @version "%I%, %G%"
 */
@Named
@ConversationScoped
@SuppressWarnings("serial")
public class ProfileManager implements Serializable {

	private static final Logger logger = Logger.getLogger(ProfileManager.class);
	@Inject
	private Conversation conversation;

	@Inject
	private UserService userService;
	@Inject
	private RoleService roleService;

	@Getter
	@Setter
	private User user;
	@Getter
	@Setter
	private String oldPassword;
	@Getter
	@Setter
	private String newPassword;
	@Getter
	@Setter
	private boolean isEditable;
	@Getter
	@Setter
	private String role;

	@PostConstruct
	public void init() {
		if (conversation.isTransient()) {
			conversation.begin();
		}
		user = (User) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("subject");
		isEditable = true;
	}

	public String profileOf(User user) {
		if (!conversation.isTransient()) {
			conversation.end();
		}
		this.user = user;
		User subject = ManagingUtils.fetchSubjectFrom(FacesContext.getCurrentInstance());
		if (!subject.equals(user)) {
			isEditable = false;
		}
		conversation.begin();
		logger.debug(" profile of " + user.getUsername() + " was visited by " + subject.getUsername());
		return "/auth/profile.xhtml";
	}

	/**
	 * Returns the <code>Role</code>s owned by the currently managed <code>User</code>.
	 * 
	 * @return a list containing the roles
	 * */
	public List<Role> listRoles() {
		return (user == null || user.getRoles() == null) ?
				new ArrayList<Role>() :
				new ArrayList<Role>(user.getRoles());
	}

	/**
	 * Returns the <code>Role</code>s owned by the currently managed <code>User</code>.
	 * 
	 * @return a list containing the roles
	 * */
	public List<ProjectAssignment> listAssignments() {
		return user == null ? new ArrayList<ProjectAssignment>() :
				new ArrayList<ProjectAssignment>(user.getProjectAssignments());
	}

	/**
	 * Validates the submitted old password and if it differs from the one in
	 * the session then error message is supplied.
	 * 
	 * @param context
	 *            representing the current JSF context
	 * @param component
	 *            the <code>UIComponent</code> from which the given value came
	 *            from
	 * @param value
	 *            that was submitted
	 * 
	 * @throws ValidatorException
	 *             when the submitted password value differs from the required
	 * */
	public void validateOldPassword(FacesContext context, UIComponent component, Object value)
			throws ValidatorException {
		if (value == null || (value != null && ((String) value).isEmpty())) {
			oldPassword = (String) value;
			return;
		}
		User subject = (User) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("subject");
		if (!subject.getPassword().equals(value)) {
			throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_ERROR, "invalid old password was given", "old password was invalid"));
		}
	}

	/**
	 * Validates the submitted new password and if it differs from the required
	 * error message is supplied.
	 * 
	 * @param context
	 *            representing the current JSF context
	 * @param component
	 *            the <code>UIComponent</code> from which the given value came
	 *            from
	 * @param value
	 *            that was submitted
	 * 
	 * @throws ValidatorException
	 *             when the submitted password value differs from the required
	 * */
	public void validateNewPassword(FacesContext context, UIComponent component, Object value)
			throws ValidatorException {
		if (oldPassword == null || (oldPassword != null && oldPassword.isEmpty())) {
			return;
		}
		if (!userService.validatePassword((String) value)) {
			throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_ERROR, "invalid password!", "invalid password!"));
		}
	}

	/**
	 * Delegates the call to a <code>UserService</code> instance to ensure
	 * whether the given email matches against a specified pattern.
	 * 
	 * @param context
	 *            representing the current JSF context
	 * @param component
	 *            the <code>UIComponent</code> from which the given value came
	 *            from
	 * @param value
	 *            representing an email
	 * 
	 * @throws ValidatorException
	 * */
	public void validateEmailIn(FacesContext context, UIComponent component, Object value)
			throws ValidatorException {
		if (!userService.validateEmail((String) value)) {
			throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_ERROR, "invalid email format!", "invalid email format!"));
		}
	}

	/**
	 * Delegates the call to a <code>UserService</code> instance to ensure
	 * whether the given description corresponds to the constraints defined on
	 * it.
	 * 
	 * @param context
	 *            representing the current JSF context
	 * @param component
	 *            the <code>UIComponent</code> from which the given value came
	 *            from
	 * @param value
	 *            representing a user description
	 * 
	 * @throws ValidatorException
	 * */
	public void validateDescriptionIn(FacesContext context, UIComponent component, Object value)
			throws ValidatorException {
		if (!userService.validateDescription(((String) value).trim())) {
			throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_ERROR,
					"description length must be between 0 and 1024 characters!",
					"description length must be between 0 and 1024 characters!"));
		}
	}

	/**
	 * Modifies the current <code>User</code>, which is the currently logged in <code>User</code> travelling in the session;
	 * 
	 * @return the pageID to navigate to after the transaction
	 * */
	public String modify() {
		FacesContext context = FacesContext.getCurrentInstance();
		try {
			if (oldPassword != null && !oldPassword.isEmpty()) {
				user.setPassword(newPassword);
			}

			userService.update(user);
			conversation.end();

			String message = "user " + user.getUsername() + " was updated";
			FacesMessageUtils.infoMessage(context, message);
			logger.debug(" " + message);
		} catch (Exception e) {
		}
		return "/auth/profile.xhtml";
	}

	/**
	 * Adds a choosen <code>Role</code> to the sepcified <code>User</code>.
	 * 
	 * @return the pageID to navigate to
	 * */
	public String addRole() {
		FacesContext context = FacesContext.getCurrentInstance();
		User subject = ManagingUtils.fetchSubjectFrom(FacesContext.getCurrentInstance());
		try {
			Role r = roleService.findByName(role);
			user.add(r);
			user = userService.update(user);

			conversation.end();
			String message = "role " + role + " was added to " + user.getUsername();
			FacesMessageUtils.infoMessage(context, message);
			logger.debug(" " + message + " by " + subject.getUsername());
		} catch (Exception e) {
			FacesMessageUtils.errorMessage(context, "failed to add role " + role);
			logger.debug(" ERROR in addRole: ", e);
		}
		return "/auth/man/add_user.xhtml";
	}

	/**
	 * Indicates the beginning of a conversation session, thus the beginning of
	 * the LifeCycle of this bean.
	 * */
	public void beginConversation() {
		this.conversation.begin();
	}

	/**
	 * Indicates the end of a conversation session, thus the end of the
	 * LifeCycle of this bean.
	 * */
	public void endConversation() {
		this.conversation.end();
	}

	@Override
	public String toString() {
		return ManagingUtils.fetchFrom(super.toString(), "ProfileManager");
	}
}