/**
 * WebAppStartupListener.java
 */
package hu.bme.aut.wman.listeners;


import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import hu.bme.aut.wman.model.Role;
import hu.bme.aut.wman.model.User;
import hu.bme.aut.wman.service.PrivilegeService;
import hu.bme.aut.wman.service.RoleService;
import hu.bme.aut.wman.service.UserService;

import javax.ejb.EJB;

import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
/**
 * @author Imre Szekeres
 * @version "%I%, %G%"
 */
public class WebAppStartupListener 
					implements ApplicationListener<ContextRefreshedEvent>{
	
	private static final Logger LOGGER = Logger.getLogger(WebAppStartupListener.class);
	public static final String LOGFILE_LOCATION = "WEB-INF/classes/log4j.properties";
	

	@EJB(mappedName="java:module/UserService")
	private UserService userService;
	
	@EJB(mappedName="java:module/RoleService")
	private RoleService roleService;
	
	
	@EJB(mappedName="java:module/PrivilegeService")
	private PrivilegeService privilegeService;
	
	
	static {
		PropertyConfigurator.configure(LOGFILE_LOCATION);
	}
	
	
	@Override
	public void onApplicationEvent(ContextRefreshedEvent arg0) {
		LOGGER.debug("WebAppStartupListener.onApplicationEvent: start");
		
		Role role;
		LOGGER.debug("role -- System Administrator check..");
		if ((role = roleService.selectByName("System Administrator")) == null) {
			role = new Role("System Administrator");
			roleService.save(role);
			
			LOGGER.debug("role -- " + role.getName() + " was created..");
		}
		
		User sudoer;
		LOGGER.debug("user -- sudoer check..");
		if ((sudoer = userService.selectByName("sudoer")) == null) {
			sudoer = new User("sudoer", 
					   		  "sudoer7", 
					   		  "sudoer7@workflowmanager.org.com",
					   		  role,
					   		  "A humble administrator of the application to test and make it work, and mantain its functionality.");
			userService.save(sudoer);
			
			role.addUser(sudoer);
			roleService.save(role);
			
			LOGGER.debug("user -- " + sudoer.getUsername() + " was created as a " + role.getName());
		}
		
		LOGGER.debug("role -- Reader check..");
		if ((role = roleService.selectByName("Reader")) == null) {
			role = new Role("Reader");
			roleService.save(role);
			
			LOGGER.debug("role -- " + role.getName() + " was created..");
		}
	}
}
