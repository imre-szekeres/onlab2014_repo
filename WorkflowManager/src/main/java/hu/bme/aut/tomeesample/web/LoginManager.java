/**
 * LoginManager.java
 */
package hu.bme.aut.tomeesample.web;

import hu.bme.aut.tomeesample.model.Role;
import hu.bme.aut.tomeesample.model.User;
import hu.bme.aut.tomeesample.service.RoleService;
import hu.bme.aut.tomeesample.service.UserService;

import java.util.List;

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

	private User subject = new User();
	private String passwordAgain;
	private String role;

	@PostConstruct
	public void init() {
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
			this.subject.add(uRole);
			userService.create(this.subject);
			logger.debug("user " + subject.getUsername() + " is created");
			return login();
		} catch (Exception e) {
			logger.error("in register: ", e);
			return "add_user";
		}
	}

	/**
	 * Checks whether the user was able to authenticate itself. It strongly
	 * depends on the validation mechanism to be called previously.
	 *
	 * @return the name of the page to navigate to
	 * */
	public String login() {
		try {
			FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("subject", subject);
			logger.debug("user " + subject.getUsername() + " is logged in");
			return "profile";
		} catch (Exception e) {
			logger.error("in login: ", e);
			return "login";
		}
	}

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
		subject = userService.findByName((String) value);
		if (subject == null) {
			throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_ERROR, "invalid username", "invalid username"));
		}
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
	 * Validates the submitted old password and if it differs from the one in
	 * the session then the authentication fails with error message.
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
		User subject = (User) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("subject");
		if (!subject.getPassword().equals(value))
			throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_ERROR, "invalid old password was given", "old password is invalid"));
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
		if (!subject.getPassword().equals(password))
			throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_ERROR, "invalid password", "invalid password"));
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
			throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_ERROR, "username already exists!", "username already exists!"));
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
					"description is too long! it msut be at most 1024 chars long!",
					"description is too long! it msut be at most 1024 chars long!"));
	}

	/**
	 * Updates the specified <code>User</code> from the application.
	 * 
	 * @param user
	 *            to be updated
	 * */
	public String modify(User user) {
		userService.update(user);
		logger.debug("user " + user.getUsername() + " was updated..");
		return "profile";
	}

	/**
	 * Removes the specified <code>User</code> from the application.
	 * 
	 * @param user
	 *            to be removed permanently
	 * */
	public String delete(User user) {
		try {
			userService.removeDetached(user);
			logger.debug("in delte: " + user.toString() + " removed");
		} catch (Exception e) {
			logger.error("ERROR in delete: ", e);
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
		this.subject = subject;
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
	}
}
