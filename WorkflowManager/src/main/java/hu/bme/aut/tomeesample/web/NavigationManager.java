/**
 * NavigationManager.java
 */
package hu.bme.aut.tomeesample.web;

import hu.bme.aut.tomeesample.model.User;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Conversation;
import javax.enterprise.context.RequestScoped;
import javax.faces.context.FacesContext;
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
public class NavigationManager {

	private static final Logger logger = Logger.getLogger(NavigationManager.class);
	private User subject;

	@Inject
	private Conversation conversation;

	@PostConstruct
	public void init() {
		subject =
				(User) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("subject");
		logger.debug(" subject is " + (subject == null ? null : subject.toString()) + " in " + this.toString());
	}

	/**
	 * Results the pageID to navigate to after the call, considers the status
	 * and <code>Role</code>s of the <code>User</code> referred as subject, and
	 * unauthorised access outcomes in a different pageID that was requested.
	 * 
	 * @return index or register page
	 * */
	public String register() {
		clearConversation();
		String pageID = "/register.xhtml";
		logger.debug("register() was requested with result: " + pageID);
		return pageID;
	}

	/**
	 * Results the pageID to navigate to after the call, considers the status
	 * and <code>Role</code>s of the <code>User</code> referred as subject, and
	 * unauthorised access outcomes in a different pageID that was requested.
	 * 
	 * @return index or register page
	 * */
	public String home() {
		clearConversation();
		String pageID = "/auth/index.xhtml";
		logger.debug("home() was requested with result: " + pageID);
		return pageID;
	}

	/**
	 * Results the pageID to navigate to after the call, considers the status
	 * and <code>Role</code>s of the <code>User</code> referred as subject, and
	 * unauthorised access outcomes in a different pageID that was requested.
	 * 
	 * @return login page
	 * */
	public String signIn() {
		clearConversation();
		String pageID = "/login.xhtml";
		logger.debug("signIn() was requested with result: " + pageID);
		return pageID;
	}

	/**
	 * Results the pageID to navigate to after the call, considers the status
	 * and <code>Role</code>s of the <code>User</code> referred as subject, and
	 * unauthorised access outcomes in a different pageID that was requested.
	 * 
	 * @return index or add_user page
	 * */
	public String addUser() {
		clearConversation();
		String pageID = "/auth/man/add_user.xhtml";
		logger.debug("addUser() was requested with result: " + pageID);
		return pageID;
	}

	/**
	 * Results the pageID to navigate to after the call, considers the status
	 * and <code>Role</code>s of the <code>User</code> referred as subject, and
	 * unauthorised access outcomes in a different pageID that was requested.
	 * 
	 * @return index or add_role page
	 * */
	public String addRole() {
		clearConversation();
		String pageID = "/auth/admin/add_role.xhtml";
		logger.debug("addRole() was requested with result: " + pageID);
		return pageID;
	}

	/**
	 * Results the pageID to navigate to after the call, considers the status
	 * and <code>Role</code>s of the <code>User</code> referred as subject, and
	 * unauthorised access outcomes in a different pageID that was requested.
	 * 
	 * @return index or workflows page
	 * */
	public String workflows() {
		clearConversation();
		String pageID = "/auth/workflows.xhtml";
		logger.debug("workflows() was requested with result: " + pageID);
		return pageID;
	}

	/**
	 * Results the pageID to navigate to after the call, considers the status
	 * and <code>Role</code>s of the <code>User</code> referred as subject, and
	 * unauthorised access outcomes in a different pageID that was requested.
	 * 
	 * @return index or add_project page
	 * */
	public String addProject() {
		clearConversation();
		String pageID = "/auth/add_project.xhtml";
		logger.debug("addProject() was requested with result: " + pageID);
		return pageID;
	}

	/**
	 * Results the pageID to navigate to after the call, considers the status
	 * and <code>Role</code>s of the <code>User</code> referred as subject, and
	 * unauthorised access outcomes in a different pageID that was requested.
	 * 
	 * @return index or actionTypes page
	 * */
	public String actionTypes() {
		clearConversation();
		String pageID = "/auth/man/actionTypes.xhtml";
		logger.debug("actionTypes() was requested with result: " + pageID);
		return pageID;
	}

	/**
	 * Results the pageID to navigate to after the call, considers the status
	 * and <code>Role</code>s of the <code>User</code> referred as subject, and
	 * unauthorised access outcomes in a different pageID that was requested.
	 * 
	 * @return login or profile page
	 * */
	public String profile() {
		clearConversation();
		String pageID = "/auth/profile.xhtml";
		logger.debug("profile() was requested with result: " + pageID);
		return pageID;
	}

	private void clearConversation() {
		if (!this.conversation.isTransient())
			this.conversation.end();
	}
}
