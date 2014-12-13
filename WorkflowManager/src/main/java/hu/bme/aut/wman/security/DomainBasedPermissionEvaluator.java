/**
 * DomainBasedPermissionEvaluator.java
 */
package hu.bme.aut.wman.security;

import hu.bme.aut.wman.model.Domain;
import hu.bme.aut.wman.model.Privilege;
import hu.bme.aut.wman.model.Workflow;
import hu.bme.aut.wman.service.DomainService;
import hu.bme.aut.wman.service.ProjectService;
import hu.bme.aut.wman.service.RoleService;
import hu.bme.aut.wman.service.UserService;
import hu.bme.aut.wman.service.WorkflowService;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

import javax.ejb.EJB;

import org.apache.log4j.Logger;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
/**
 * @author Imre Szekeres
 * @version "%I%, %G%"
 */
public class DomainBasedPermissionEvaluator implements PermissionEvaluator {

	private static final Logger LOGGER;
	private static final ObjectMapper MAPPER;
	
	static {
		MAPPER = new ObjectMapper();
		LOGGER = Logger.getLogger(DomainBasedPermissionEvaluator.class);
	}

	@EJB(mappedName = "java:module/DomainService")
	private DomainService domainService;
	@EJB(mappedName = "java:module/RoleService")
	private RoleService roleService;
	@EJB(mappedName = "java:module/UserService")
	private UserService userService;
	@EJB(mappedName = "java:module/ProjectService")
	private ProjectService projectService;
	@EJB(mappedName = "java:module/WorkflowService")
	private WorkflowService workflowService;

	/**
	 * Responsible for handling the hasPermission(targetDomainObject, permission) like expression.
	 * <p>
	 * It returns false since it is not yet supported in WorkflowManage 2.1
	 * 
	 * @param authentication implicit object that is passed by the Spring Security framework
	 * @param targetDomainObject
	 * @param permission
	 * @return the decision made
	 * @see {@link PermissionEvaluator#hasPermission(Authentication, Object, Object)}
	 * */
	@Override
	public boolean hasPermission(Authentication authentication, Object targetDomainObject, Object permission) {
		return false;
	}

	/**
	 * Responsible for handling the hasPermission(targetId, targetType, permission) like expression.
	 * 
	 * @param authentication implicit object that is passed by the Spring Security framework
	 * @param targetId
	 * @param targetType
	 * @param permission
	 * @return the decision made
	 * @see {@link PermissionEvaluator#hasPermission(Authentication, Serializable, String, Object)}
	 * */
	@Override
	public boolean hasPermission(Authentication authentication, Serializable targetId, String targetType, Object permission) {
		String username = ((User) authentication.getPrincipal()).getUsername();
		if ("Role".equals( targetType ))
			return roleService.hasPrivilege(username, (Long) targetId, (String) permission);
		
		else if ("Domain".equals( targetType ))
			return hasDomainPermissions(username, targetId, (String) permission);
		
		else if ("User".equals( targetType ))
			return hasUserPermissions(username, targetId, (String) permission);
		
		else if ("Project".equals( targetType ))
			return hasProjectPermissions(username, targetId, (String) permission);
		
		else if ("Workflow".equals( targetType ))
			return hasWorkflowPermissions(username, targetId, (String) permission);
		return false;
	}

	/**
	 * Handles the case when it is to be decided whether the given <code>User</code> has the given <code>Privilege</code>
	 * (specified by its name) in the <code>Domain</code> given by its name or id.
	 * 
	 * @param username
	 * @param targetId
	 * @param permission
	 * @return true when the {@link User} has any {@link Role} that owns the {@link Privilege} specified in the given {@link Domain}
	 * */
	private boolean hasDomainPermissions(String username, Object targetId, String permission) {
		if (targetId instanceof Long) 
			return domainService.hasPrivilege(username, (Long) targetId, permission);
		return domainService.hasPrivilege(username, (String) targetId, permission);
	}

	/**
	 * Handles the case when it is to be decided whether the given <code>User</code> has the given <code>Privilege</code>
	 * (specified by its name) in ANY of the <code>Domain</code>s the given <code>User</code> is in.
	 * 
	 * @param username
	 * @param targetId
	 * @param permission
	 * @return true when the {@link User} has any {@link Role} that owns the {@link Privilege} specified in the given {@link Domain}
	 *         in which the given {@link User} (as its identifier) is
	 * */
	private boolean hasUserPermissions(String username, Object targetId, String permission) {
		if (targetId instanceof Long) 
			return userService.hasPrivilege(username, (Long) targetId, permission);
		return userService.hasPrivilege(username, (String) targetId, permission);
	}

	/**
	 * Handles the case when it is to be decided whether the given <code>User</code> has the given <code>Privilege</code>
	 * (specified by its name) in the the <code>Domain</code> the given <code>Project</code> is in.
	 * 
	 * @param username
	 * @param targetId
	 * @param permission
	 * @return true when the {@link User} has any {@link Role} that owns the {@link Privilege} specified in the given {@link Domain}
	 *         in which the given {@link Project} (as its identifier) is
	 * */
	private boolean hasProjectPermissions(String username, Object targetId, String permission) {
		if (targetId instanceof Long) 
			return projectService.hasPrivilege(username, (Long) targetId, permission);
		return projectService.hasPrivilege(username, (String) targetId, permission);
	}

	/**
	 * Handles the case when it is to be decided whether the given <code>User</code> has the given <code>Privilege</code>
	 * (specified by its name) in the the <code>Domain</code> the given <code>Workflow</code> is in.
	 * 
	 * @param username
	 * @param targetId
	 * @param permission
	 * @return true when the {@link User} has any {@link Role} that owns the {@link Privilege} specified in the given {@link Domain}
	 *         in which the given {@link Workflow} (as its identifier) is
	 * */
	private boolean hasWorkflowPermissions(String username, Object targetId, String permission) {
		if (targetId instanceof Long) 
			return workflowService.hasPrivilege(username, (Long) targetId, permission);
		return workflowService.hasPrivilege(username, (String) targetId, permission);
	}

	/**
	 * Handles the given permission Spring EL <code>String</code>, transforms it into a <code>List</code> of
	 * <code>String</code>s containing the permission/<code>Privilege</code> names.
	 * 
	 * @param permission
	 * @return the {@link List} of names
	 * */
	public static final List<String> authoritiesOf(String permission) {
		if (permission.trim().startsWith("[")) {
			return asJsonArray(permission);
		}
		return Arrays.asList(new String[] {permission});
	}

	/**
	 * Parses a <code>String</code> in an expected format of ['v1', 'v2', ..., 'vN'] as a JSON <code>String</code>
	 * and returns a <code>List</code> of <code>String</code>s.
	 * 
	 * @param json
	 * @return a {@link List}
	 * */
	private static final List<String> asJsonArray(String json) {
		try {
			return MAPPER.readValue( json, 
					                 MAPPER.getTypeFactory().constructCollectionType(List.class, String.class) );
		} catch(Exception e) {
			LOGGER.warn(String.format("Undable to parse %s in %s", json, "DomainBasedPermissionEvaluator.asJsonArray"), e);
			return Arrays.asList(new String[0]);
		}
	}
}
