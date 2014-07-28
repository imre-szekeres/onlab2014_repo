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
import hu.bme.aut.tomeesample.utils.FacesMessageUtils;
import hu.bme.aut.tomeesample.utils.ManagingUtils;

import java.util.Arrays;
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

import lombok.Getter;
import lombok.Setter;

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

	@Getter
	@Setter
	private User subject;
	@Getter
	@Setter
	private String passwordAgain;
	@Getter
	@Setter
	private String role;

	@PostConstruct
	public void init() {
		logger.debug("\tand session.user as " + FacesContext.getCurrentInstance().getExternalContext()
				.getSessionMap().get("subject"));
		subject = new User();
		this.subject.setUsername("Username");
		this.subject.setDescription("Default description!");
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
	 * Lists the available <code>Role</code> names in the system.
	 * 
	 * @return a list containing the names
	 * */
	public List<String> listRoleNames() {
		return Arrays.asList(roleService.findRoleNames());
	}

	/**
	 * Registers a new user with the previously set parameters then logs it in.
	 * 
	 * @return the string representation of the page to navigate to
	 * */
	public String register() {
		User subject = ManagingUtils.fetchSubjectFrom(FacesContext.getCurrentInstance());
		try {
			Role uRole = roleService.findByName(role == null ? "visitor" : role);
			subject.add(uRole);
			userService.create(subject);
			logger.debug(" user " + subject.getUsername() + " was created by " + subject == null ? null : subject.getUsername());

			if (FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("subject") == null) {
				return login();
			}
		} catch (Exception e) {
			logger.error(" in register: ", e);
		}
		return "/auth/man/add_user.xhtml";
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
			logger.debug(" user " + subject.getUsername() + " was logged in");

			FacesMessageUtils.infoMessage(context, "welcome, " + subject.getUsername());
			return "/auth/index.xhtml";
		} catch (Exception e) {
			logger.error(" in login: ", e);
			FacesMessageUtils.errorMessage(context, "invalid parameters");
			return "/login.xhtml";
		}
	}

	/**
	 * Performs the logout mechanism, resets the <code>User</code> instance in
	 * the session to default.
	 * 
	 * @return the page id to navigate to after the operation
	 * */
	public String logout() {
		FacesContext context = FacesContext.getCurrentInstance();
		User subject = ManagingUtils.fetchSubjectFrom(context);
		logger.debug(" " + subject.getUsername() + " logged out");
		context.getExternalContext().getSessionMap().put("subject", null);
		return "/login.xhtml?faces-redirect=true";
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
			throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_ERROR, "invalid username or password", "pasword or username is invalid"));
		}
		subject = user;
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
			if (!subject.getPassword().equals(password)) {
				throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_ERROR, "invalid username or password", "pasword or username is invalid"));
			}
		} catch (Exception e) {
			throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_ERROR, "invalid username or password", "pasword or username is invalid"), e);
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
		if (!userService.validateUsername((String) value)) {
			throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_ERROR, "username must be between 7 and 32 characters or already exists!", "username must be between 7 and 32 characters or already exists!"));
		}
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
		if (!userService.validatePassword((String) value)) {
			throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_ERROR, "invalid password!", "invalid password!"));
		}
		this.subject.setPassword((String) value);
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
		if (!userService.validatePasswords(this.subject.getPassword(), (String) value)) {
			throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_ERROR, "passwords do not match!", "passwords do not match!"));
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
	 * Removes the specified <code>User</code> from the application.
	 * 
	 * @param user
	 *            to be removed permanently
	 * */
	public String delete(User user) {
		FacesContext ctx = FacesContext.getCurrentInstance();
		User subject = ManagingUtils.fetchSubjectFrom(ctx);
		try {
			userService.removeDetached(user);
			FacesMessageUtils.infoMessage(ctx, "user " + user.getUsername() + " deleted!");
			logger.debug(" user " + user.getUsername() + " was removed by " + subject.getUsername());
		} catch (Exception e) {
			logger.error(" ERROR in delete: ", e);
			FacesMessageUtils.errorMessage(ctx, "error while attempting delete!");
		}
		return "/auth/man/add_user.xhtml";
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
		this.subject.setUsername(username);
	}

	public void setPassword(String password) {
		this.subject.setPassword(password);
	}

	public String getEmail() {
		return subject.getEmail();
	}

	public void setEmail(String email) {
		this.subject.setEmail(email);
	}

	public String getDescription() {
		return subject.getDescription();
	}

	public void setDescription(String description) {
		this.subject.setDescription(description);
	}

	@Override
	public String toString() {
		return ManagingUtils.fetchFrom(super.toString(), "LoginManager");
	}
}