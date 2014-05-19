/**
 * ApplicationManager.java
 */
package hu.bme.aut.tomeesample.web;

import hu.bme.aut.tomeesample.model.Role;
import hu.bme.aut.tomeesample.model.User;
import hu.bme.aut.tomeesample.service.RoleService;
import hu.bme.aut.tomeesample.service.UserService;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

/**
 *
 * @author Imre Szekeres
 * @version "%I%, %G%"
 */
@Named
@ApplicationScoped
@SuppressWarnings("serial")
public class ApplicationManager implements java.io.Serializable {

	public static final String LOG_PROPERTIES = "resources/conf/log4j.properties";
	private static final Logger logger = Logger.getLogger(ApplicationManager.class);

	@Inject
	private RoleService roleService;
	@Inject
	private UserService userService;

	@PostConstruct
	public void init() {
		PropertyConfigurator.configure(LOG_PROPERTIES);
		FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("subject", null);
		logger.debug("logger configured..");

		Role admin = null;
		if ((admin = roleService.findByName("administrator")) == null) {
			admin = new Role("administrator");
			tryCreate(admin);
		}
		if (roleService.findByName("visitor") == null) {
			Role role = new Role("visitor");
			tryCreate(role);
		}
		if (userService.findByName("sudoer") == null) {
			User sudoer = new User("sudoer", "sudoer7", "sudoer@cvf.wm.com", admin,
					"A humble administrator for the (R) WorkflowManager application! "
							+ "He represents all that is good in the maintenance staff of a thick client and "
							+ "disributed around the world application.");
			tryCreate(sudoer);
		}
	}

	private void tryCreate(Role role) {
		try {
			roleService.create(role);
		} catch (Exception e) {
			logger.debug("failed to create role " + role.toString());
			logger.debug("ERROR in tryCreate ~ " + e.getClass() + ": " + e.getMessage());
		}
	}

	private void tryCreate(User user) {
		try {
			userService.create(user);
		} catch (Exception e) {
			logger.debug("failed to create user " + user.getUsername());
			logger.debug("ERROR in tryCreate ~ " + e.getClass() + ": " + e.getMessage());
		}
	}

	public String getLoggerName() {
		return logger.getName();
	}

	public void setLoggerName(String name) {
	}
}