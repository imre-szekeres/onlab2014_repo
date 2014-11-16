/**
 * WebAppStartupListener.java
 */
package hu.bme.aut.wman.listeners;


import hu.bme.aut.wman.handlers.UserHandlerLocal;
import hu.bme.aut.wman.model.Domain;
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
@SuppressWarnings("all")
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
		
		/*Domain system = new Domain("System");
		domainService.save(system);
		LOGGER.debug("domain -- System was created..");
//		Role sysAdmin = new Role("System Administrator", system);
		Role sysMan = new Role("System Manager", system);
		roleService.save(sysMan);
		LOGGER.debug("role -- System Manager was created..");
		
		Role sysReader = new Role("System Reader", system);
		roleService.save(sysReader);
		LOGGER.debug("role -- System Reader was created..");
		
		system.addRole(sysMan);
		system.addRole(sysReader);
		domainService.save(system);*/
		
		/*try {
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
			/*	userService.delete(sudoer);	
				LOGGER.debug("user -- " + sudoer.getUsername() + " was removed..");
			}
			
			LOGGER.debug("role -- Reader check..");
			if ((role = roleService.selectByName("Reader")) != null) {
				roleService.delete(role);
				LOGGER.debug("role -- " + role.getName() + " was removed..");
			}
		} catch(Exception e) {
			LOGGER.fatal("ERROR during startup", e);
		}*/
	}
	
	private Domain createSystemDomain() {
		LOGGER.debug("domain -- System: initialization..");
		Domain system;
		if ((system = domainService.selectByName("System")) == null) {
			// TODO: inti the whole system...
			// LOGGER.debug("domain -- System was created..");
		}
		return system;
	}
	
	private User createSudoer(Domain system) {
		LOGGER.debug("user -- sudoer: initialization..");
		User sudoer;
		if ((sudoer = userService.selectByName("sudoer")) == null) {
			// TODO: init sudoer
			// LOGGER.debug("user -- sudoer was created..");
		}
		return null;
	}
}
