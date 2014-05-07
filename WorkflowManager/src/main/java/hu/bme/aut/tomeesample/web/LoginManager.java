/**
 * LoginManager.java
 */
package hu.bme.aut.tomeesample.web;

import hu.bme.aut.tomeesample.model.Role;
import hu.bme.aut.tomeesample.model.User;
import hu.bme.aut.tomeesample.service.RoleService;
import hu.bme.aut.tomeesample.service.UserService;

import java.util.List;

import javax.enterprise.context.RequestScoped;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.ValidatorException;
import javax.inject.Inject;
import javax.inject.Named;

/**
 *
 * @author Imre Szekeres
 * @version "%I%, %G%"
 */
@Named
@RequestScoped
public class LoginManager {

	/*
	 * static { PropertyConfigurator.configure(System.getProperty("user.dir") +
	 * "/src/main/java/log4j.properties"); }
	 *
	 * private static Logger logger = Logger.getLogger(LoginManager.class);
	 */

	@Inject
	private UserService userService;
	@Inject
	private RoleService roleService;
	private String description;

	private User subject;

	private String username = "Username";
	private String password;
	private String passwordAgain;
	private String email;
	private String role;

	public LoginManager() {
		super();
	}

	/**
	 * Fetches the users already registered/created into/in the application.
	 *
	 * @return a list of all the found users
	 * */
	public List<User> listUsers() {
		List<User> all = userService.findAll();
		System.out.println(all);
		return all;
	}

	/**
	 * Adds a new role to the user specified by user name.
	 *
	 * @return the string representation of the page to navigate to
	 * */
	public String addRoleFor() {
		try {
			User user = userService.findByName(username);
			Role uRole = roleService.findByName(role);
			userService.addRoleFor(user, uRole);
			return "users";
		} catch (Exception e) {
			// TODO: logger.error("in addRoleFor: ", e);
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
			Role uRole = roleService.findByName(role == null ? "visitor" : role);
			this.subject = new User(username, password, email, uRole, description);
			userService.create(this.subject);
			return login();
		} catch (Exception e) {
			// TODO: logger.error("in register: ", e);
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
			return "profile";
		} catch (Exception e) {
			return "login";
		}
	}

	public String logout() {
		FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("subject", new User());
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
		this.password = (String) value;
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
		if (!userService.validatePasswords(password, (String) value))
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
	 * @return the username
	 */
	public String getUsername() {
		return username;
	}

	/**
	 * @param username
	 *            the username to set
	 */
	public void setUsername(String username) {
		this.username = username;
	}

	/**
	 * @return the password
	 */
	public String getPassword() {
		return password;
	}

	/**
	 * @param password
	 *            the password to set
	 */
	public void setPassword(String password) {
		this.password = password;
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
	 * @return the balance
	 */
	public String getEmail() {
		return email;
	}

	/**
	 * @param balance
	 *            the balance to set
	 */
	public void setEmail(String email) {
		this.email = email;
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

	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * @param description
	 *            the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}
}
