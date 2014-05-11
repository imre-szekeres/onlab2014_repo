/**
 * LoginManager.java
 */
package hu.bme.aut.tomeesample.web;

import hu.bme.aut.tomeesample.model.Comment;
import hu.bme.aut.tomeesample.model.ProjectAssignment;
import hu.bme.aut.tomeesample.model.Role;
import hu.bme.aut.tomeesample.model.User;
import hu.bme.aut.tomeesample.service.RoleService;
import hu.bme.aut.tomeesample.service.UserService;
import hu.bme.aut.tomeesample.utils.ManagingUtils;

import java.util.List;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
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
@RequestScoped
public class LoginManager {

	private static Logger logger = Logger.getLogger(LoginManager.class);

	@Inject
	private UserService userService;
	@Inject
	private RoleService roleService;

	private User subject;
	private String passwordAgain;
	private String oldPassword;
	private String newPassword;
	private String role;

	@PostConstruct
	public void init() {
		logger.debug("in init with loginManager: " + this.toString());
		logger.debug("\tand session.user as " + FacesContext.getCurrentInstance().getExternalContext()
				.getSessionMap().get("subject"));
		subject = new User();
		logger.debug("\tcreated new loginManager.subject as " + subject.toString());
		this.subject.setUsername("Username");
		this.subject.setDescription("Default description!");
	}

	/**
	 * Sets up this <code>LoginManager</code> instance for the requested profile
	 * page with the currently logged in <code>User</code>.
	 * 
	 * @return the id of the <code>User</code> subject of the profile page.
	 * */
	public void profileSetup() {
		subject = ManagingUtils.nullThenDefault(
				(User) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("subject"),
				new User()
				);
	}

	/**
	 * Fetches the users already registered/created into/in the application.
	 *
	 * @return a list of all the found users
	 * */
	public List<User> listUsers() {
		return this.userService.findAll();
	}

	/**
	 * Adds a new role to the user specified by user name.
	 *
	 * @return the string representation of the page to navigate to
	 * */
	public String addRoleFor() {
		try {
			User user = userService.findByName(subject.getUsername());
			Role uRole = roleService.findByName(role);
			userService.addRoleFor(user, uRole);
			logger.debug("user " + user.toString() + " and role " + uRole.toString() + " are created..");
			return "users";
		} catch (Exception e) {
			logger.error("in addRoleFor: ", e);
			return "add_role";
		}
	}

	/**
	 * Registers a new user with the previously set parameters then logs it in.
	 *
	 * @return the string representation of the page to navigate to
	 * */
	public String register() {
		try {
			// TODO: check whether the user added to the role is persisted!
			Role uRole = roleService.findByName(role == null ? "visitor" : role);
			subject.add(uRole);
			userService.create(subject);
			logger.debug("user " + subject.getUsername() + " is created");

			logPropsOf(subject);

			if (FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("subject") == null)
				return login();
		} catch (Exception e) {
			logger.error("in register: ", e);
		}
		return "add_user";
	}

	/**
	 * Checks whether the user was able to authenticate itself. It strongly
	 * depends on the validation mechanism to be called previously.
	 *
	 * @return the name of the page to navigate to
	 * */
	public String login() {
		FacesContext context = FacesContext.getCurrentInstance();
		try {
			context.getExternalContext().getSessionMap().put("subject", subject);
			logger.debug("user " + subject.getUsername() + " is logged in");
			logPropsOf(subject);

			infoMessage(context, "welcome, " + subject.getUsername());
			return "index";
		} catch (Exception e) {
			logger.error("in login: ", e);
			errorMessage(context, "invalid parameters");
			return "login";
		}
	}

	/**
	 * Adds a new <code>FacesMessages.SEVERITY_INFO</code> message to the
	 * specified <code>FacesContext</code>.
	 * 
	 * @param context
	 * @param message
	 * */
	private void infoMessage(FacesContext context, String message) {
		context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, message, message));
	}

	/**
	 * Adds a new <code>FacesMessages.SEVERITY_ERROR</code> message to the
	 * specified <code>FacesContext</code>.
	 * 
	 * @param context
	 * @param message
	 * */
	private void errorMessage(FacesContext context, String message) {
		context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, message, message));
	}

	/**
	 * Performs the logout mechanism, resets the <code>User</code> instance in
	 * the session to default.
	 * 
	 * @return the page id to navigate to after the operation
	 * */
	public String logout() {
		FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("subject", null);
		return "index";
	}

	/**
	 * Validates the submitted user name and if it differs from the required
	 * then the authentication fails with error message.
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
	 *             when the submitted user name value differs from the required
	 * */
	public void validateUsername(FacesContext context, UIComponent component, Object value)
			throws ValidatorException {
		User user = userService.findByName((String) value);
		if (user == null) {
			throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_ERROR, "invalid username", "invalid username"));
		}
		subject = user;
		logger.debug("user <" + user.toString() + "> is found");
	}

	/**
	 * Validates the submitted password and if it differs from the required then
	 * the authentication fails with error message.
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
	public void validatePassword(FacesContext context, UIComponent component, Object value)
			throws ValidatorException {
		if (subject != null) {
			checkPassword((String) value);
		}
		logger.debug("password <" + value + "> equals to the users");
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
		if (!subject.getPassword().equals(value))
			throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_ERROR, "invalid old password was given", "old password is invalid"));
		logger.debug("old password <" + value + "> is valid");
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
		if (oldPassword == null || (oldPassword != null && oldPassword.isEmpty()))
			return;
		validatePassword(context, component, value);
	}

	/**
	 * Checks whether the given password equals to the password registered in
	 * the found user.
	 *
	 * @param password
	 *            to diff-against the fetched password
	 * @throws ValidatorException
	 *             only if the given password differs
	 * */
	private void checkPassword(String password) throws ValidatorException {
		try {
			if (!subject.getPassword().equals(password))
				throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_ERROR, "invalid password", "invalid password"));
		} catch (Exception e) {
			throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_ERROR, "invalid password", "invalid password"), e);
		}
	}

	/**
	 * Delegates the call to a <code>UserService</code> instance to ensure
	 * whether the given user name corresponds to the constraints defined on it.
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
	 * */
	public void validateUsernameIn(FacesContext context, UIComponent component, Object value)
			throws ValidatorException {
		if (!userService.validateUsername((String) value))
			throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_ERROR, "username must be between 7 and 32 characters or already exists!", "username must be between 7 and 32 characters or already exists!"));
		logger.debug("username <" + value + "> is valid");
	}

	/**
	 * Delegates the call to a <code>UserService</code> instance to ensure
	 * whether the given password corresponds to the constraints defined on it.
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
	 * */
	public void validatePasswordIn(FacesContext context, UIComponent component, Object value)
			throws ValidatorException {
		if (!userService.validatePassword((String) value))
			throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_ERROR, "invalid password!", "invalid password!"));
		this.subject.setPassword((String) value);
		logger.debug("password <" + value + "> is valid");
	}

	/**
	 * Delegates the call to a <code>UserService</code> instance to ensure
	 * whether the given passwords do match.
	 *
	 * @param context
	 *            representing the current JSF context
	 * @param component
	 *            the <code>UIComponent</code> from which the given value came
	 *            from
	 * @param value
	 *            the second password to ensure it was correctly given in the
	 *            first time
	 *
	 * @throws ValidatorException
	 * */
	public void validatePasswordAgainIn(FacesContext context, UIComponent component, Object value)
			throws ValidatorException {
		if (!userService.validatePasswords(this.subject.getPassword(), (String) value))
			throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_ERROR, "passwords do not match!", "passwords do not match!"));
		logger.debug("password again <" + value + "> matches prev password <" + subject.getPassword() + ">");
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
		if (!userService.validateEmail((String) value))
			throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_ERROR, "invalid email format!", "invalid email format!"));
		logger.debug("email <" + value + "> is valid");
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
		if (!userService.validateDescription(((String) value).trim()))
			throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_ERROR,
					"description length must be between 0 and 1024 characters!",
					"description length must be between 0 and 1024 characters!"));
		logger.debug("description <" + value + "> is valid");
	}

	/**
	 * Updates the specified <code>User</code> from the application.
	 * 
	 * */
	public String modify() {
		logger.debug("in modify ~ old password is: " + oldPassword);
		logger.debug("in modify ~ new password is: " + newPassword);
		if (oldPassword != null && !oldPassword.isEmpty()) {
			logger.debug("new pass: " + newPassword);
			subject.setPassword(newPassword);
		}

		logPropsOf(subject);

		userService.update(subject);
		FacesContext context = FacesContext.getCurrentInstance();
		context.getExternalContext().getSessionMap().put("subject", subject);

		String msgString = "user " + subject.getUsername() + " was updated..";
		logger.debug(msgString);
		context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, msgString, msgString));
		return "index";
	}

	// TODO: delete
	private void logPropsOf(User user) {
		try {
			logger.debug("\nloginManager: " + this.toString());
			logger.debug("user: " + user.toString());
			logger.debug("props:"
					+ "\nUsername: " + user.getUsername()
					+ "\nPassword: " + user.getPassword()
					+ "\nEmail: " + user.getEmail()
					+ "\nDescription: " + user.getDescription()
					+ "\nRoles: " + user.getRoles()
					+ "\nComments: " + user.getComments()
					+ "\nProjectAssignments: " + user.getProjectAssignments() + "\n");
		} catch (Exception e) {
			logger.error("ERROR in logProps ~ " + e.getClass() + ": " + e.getMessage());
			logger.error(e, e);
		}

	}

	/**
	 * Removes the specified <code>User</code> from the application.
	 * 
	 * @param user
	 *            to be removed permanently
	 * */
	public String delete(User user) {
		FacesContext ctx = FacesContext.getCurrentInstance();
		try {
			userService.removeDetached(user);
			logger.debug("in delte: " + user.toString() + " removed");
			infoMessage(ctx, "user " + user.getUsername() + " deleted!");
		} catch (Exception e) {
			logger.error("ERROR in delete: ", e);
			errorMessage(ctx, "error while attempting delete!");
		}
		return "add_user";
	}

	/**
	 * @return the subject
	 */
	public User getSubject() {
		return subject;
	}

	/**
	 * @param subject
	 *            the subject to set
	 */
	public void setSubject(User subject) {
		logger.debug("setting subject to: " + subject.toString());
		logPropsOf(subject);
		this.subject = subject;
	}

	/**
	 * Returns the id of the current subject.
	 * 
	 * @return id of the subject currently wrapped by <code>LoginManager</code>
	 * */
	public Long getSubjectId() {
		return subject.getId();
	}

	/**
	 * Sets the id of the current subject by fetching it from the database if
	 * necessary.
	 * 
	 * @param id
	 *            to look for
	 * */
	public void setSubjectId(Long id) {
		logger.debug("set subjectId was called..");
		logger.debug("subject is <" + subject + ">");
		if (subject.getId() == null) {
			User user = userService.findById(id);
			subject = ManagingUtils.nullThenDefault(user, subject);
			logger.debug("subject was set to <" + subject + ">");
		}
	}

	public String getUsername() {
		return subject.getUsername();
	}

	public String getPassword() {
		return subject.getPassword();
	}

	public List<Comment> getComments() {
		return subject.getComments();
	}

	public Set<ProjectAssignment> getProjectAssignments() {
		return subject.getProjectAssignments();
	}

	public Set<Role> getRoles() {
		return subject.getRoles();
	}

	public void setUsername(String username) {
		logger.debug("username is set to <" + username + ">");
		this.subject.setUsername(username);
	}

	public void setPassword(String password) {
		logger.debug("password is set to <" + password + ">");
		this.subject.setPassword(password);
	}

	public String getEmail() {
		return subject.getEmail();
	}

	public void setEmail(String email) {
		logger.debug("email is set to <" + email + ">");
		this.subject.setEmail(email);
	}

	public String getDescription() {
		return subject.getDescription();
	}

	public void setDescription(String description) {
		logger.debug("description is set to <" + description + ">");
		this.subject.setDescription(description);
		;
	}

	/**
	 * @return the passwordAgain
	 */
	public String getPasswordAgain() {
		return passwordAgain;
	}

	/**
	 * @param passwordAgain
	 *            the passwordAgain to set
	 */
	public void setPasswordAgain(String passwordAgain) {
		this.passwordAgain = passwordAgain;
	}

	/**
	 * @return the role
	 */
	public String getRole() {
		return role;
	}

	/**
	 * @param role
	 *            the role to set
	 */
	public void setRole(String role) {
		this.role = role;
		logger.debug("role was set to <" + role + ">");
	}

	/**
	 * @return the oldPassword
	 */
	public String getOldPassword() {
		return oldPassword;
	}

	/**
	 * @param oldPassword
	 *            the oldPassword to set
	 */
	public void setOldPassword(String oldPassword) {
		this.oldPassword = oldPassword;
		logger.debug("old pass was set to <" + oldPassword + ">");
	}

	/**
	 * @return the newPassword
	 */
	public String getNewPassword() {
		return newPassword;
	}

	/**
	 * @param newPassword
	 *            the newPassword to set
	 */
	public void setNewPassword(String newPassword) {
		this.newPassword = newPassword;
		logger.debug("new pass was set to <" + newPassword + ">");
	}

	@Override
	public String toString() {
		String reps = super.toString();
		return reps.substring(reps.indexOf("LoginManager"));
	}
}
