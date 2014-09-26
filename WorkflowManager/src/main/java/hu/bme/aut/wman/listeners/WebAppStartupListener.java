/**
 * WebAppStartupListener.java
 */
package hu.bme.aut.wman.listeners;

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

	@EJB(mappedName="java:module/UserService")
	private UserService userService;
	
	@EJB(mappedName="java:module/RoleService")
	private RoleService roleService;
	
	
	@EJB(mappedName="java:module/PrivilegeService")
	private PrivilegeService privilegeService;
	
	
	@Override
	public void onApplicationEvent(ContextRefreshedEvent arg0) {
		// TODO:
		System.out.println("WebAppStartupListener.onApplicationEvent: start");
		Role role;
		if ((role = roleService.selectByName("System Administrator")) == null) {
			role = new Role("System Administrator");
			roleService.save(role);
			// TODO:
			System.out.println("WebAppStartupListener.onApplicationEvent: role " + role.getName() + " was created..");
		}
		
		User sudoer;
		if ((sudoer = userService.selectByName("sudoer")) == null) {
			sudoer = new User("sudoer", 
					   		  "sudoer7", 
					   		  "sudoer7@workflowmanager.org.com",
					   		  role,
					   		  "A humble administrator of the application to test and make it work, and mantain its functionality.");
			userService.save(sudoer);
			// TODO:
			System.out.println("WebAppStartupListener.onApplicationEvent: user " + sudoer.getUsername() + " was created..");
		}
		
		if ((role = roleService.selectByName("Reader")) == null) {
			role = new Role("Reader");
			roleService.save(role);
			// TODO:
			System.out.println("WebAppStartupListener.onApplicationEvent: role " + role.getName() + " was created..");
		}
	}
}
