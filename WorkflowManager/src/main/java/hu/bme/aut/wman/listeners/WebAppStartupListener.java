/**
 * WebAppStartupListener.java
 */
package hu.bme.aut.wman.listeners;


import hu.bme.aut.wman.handlers.UserHandlerLocal;
import hu.bme.aut.wman.model.Role;
import hu.bme.aut.wman.model.User;
import hu.bme.aut.wman.service.DomainService;
import hu.bme.aut.wman.service.PrivilegeService;
import hu.bme.aut.wman.service.RoleService;
import hu.bme.aut.wman.service.UserService;

import javax.ejb.EJB;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
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
	

	@EJB(mappedName = "java:module/UserService")
	private UserService userService;
	
	@EJB(mappedName = "java:module/RoleService")
	private RoleService roleService;
	
	
	@EJB(mappedName = "java:module/PrivilegeService")
	private PrivilegeService privilegeService;
	
	@EJB(mappedName = "java:module/DomainService")
	private DomainService domainService;
	
	@EJB(mappedName = "java:module/UserHandlerImpl")
	private UserHandlerLocal userHandler;
	
	
	static {
		PropertyConfigurator.configure(LOGFILE_LOCATION);
	}
	
	
	@Override
	public void onApplicationEvent(ContextRefreshedEvent arg0) {
		LOGGER.debug("WebAppStartupListener.onApplicationEvent: start");
		
		try {
			Role role;
			LOGGER.debug("role -- System Administrator check..");
			if ((role = roleService.selectByName("System Administrator")) != null) {
				roleService.delete(role);
				
				LOGGER.debug("role -- " + role.getName() + " was removed..");
			}
			
			User sudoer;
			LOGGER.debug("user -- sudoer check..");
			if ((sudoer = userService.selectByName("sudoer")) != null) {
				/*sudoer = new User("sudoer", 
						   		  "sudoer7", 
						   		  "sudoer7@workflowmanager.org.com",
						   		  "A humble administrator of the application to test and make it work, and mantain its functionality.");*/
				userService.delete(sudoer);	
				LOGGER.debug("user -- " + sudoer.getUsername() + " was removed..");
			}
			
			LOGGER.debug("role -- Reader check..");
			if ((role = roleService.selectByName("Reader")) != null) {
				roleService.delete(role);
				LOGGER.debug("role -- " + role.getName() + " was removed..");
			}
		} catch(Exception e) {
			LOGGER.fatal("ERROR during startup", e);
		}
	}
}
