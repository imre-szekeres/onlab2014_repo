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
		return "/register.xhtml";
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
		return "/auth/index.xhtml";
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
		return "/login.xhtml";
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
		return "/auth/man/add_user.xhtml";
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
		return "/auth/admin/add_role.xhtml";
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
		return "/auth/workflows.xhtml";
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
		return "/auth/add_project.xhtml";
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
		return "/auth/admin/actionTypes.xhtml";
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
		return "/auth/profile.xhtml";
	}

	private void clearConversation() {
		if (!this.conversation.isTransient())
			this.conversation.end();
	}
}
