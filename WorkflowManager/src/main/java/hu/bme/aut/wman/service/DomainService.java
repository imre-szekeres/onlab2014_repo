/**
 * DomainService.java
 */
package hu.bme.aut.wman.service;

import hu.bme.aut.wman.exceptions.EntityNotDeletableException;
import hu.bme.aut.wman.model.Domain;
import hu.bme.aut.wman.model.DomainAssignment;
import hu.bme.aut.wman.model.Privilege;
import hu.bme.aut.wman.service.validation.DomainValidator;
import hu.bme.aut.wman.service.validation.ValidationEngine;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.inject.Inject;
/**
 * Handles the database access regarding the <code>Domain</code> instances, the business logic
 * corresponding to the CRUD operations of them.
 * 
 * @author Imre Szekeres
 * @version "%I%, %G%"
 */
@LocalBean
@Stateless
public class DomainService extends AbstractDataService<Domain> {

	public static final String DEFAULT_DOMAIN = "System";
	public static final String DEFAULT_ROLE = "System Viewer";
	
	@Inject
	private DomainAssignmentService domainAssignmentService;
	private ValidationEngine<Domain> validator;

	/**
	 * Initializes the validation mechanism used.
	 * */
	@PostConstruct
	public void setup() {
		validator = new DomainValidator();
	}

	/**
	 * Validates the given <code>Domain</code> object against its given <code>ValidationConstraint</code>s 
	 * as Java annotation using Bean Validation mechanisms.
	 * 
	 * @param domain
	 * @return the {@link java.util.Map} representation of (<code>propertyName</code>, <code>errorMessage</code>) entries
	 * */
	public Map<String, String> validate(Domain domain) {
		Map<String, String> errors = validator.validate( domain );
		if (selectByName(domain.getName()) != null)
			errors.put("name", "Domain " + domain.getName() + " already exists!");
		return errors;
	}
	
	
	/**
	 * Retrieves all <code>Domain</code> names stored in the database.
	 * 
	 * @return a list of {@link Domain} names
	 * */
	public List<String> selectAllNames() {
		return callNamedQuery( Domain.NQ_FIND_ALL_NAMES,
				               new ArrayList<Map.Entry<String, Object>>(0),
				               String.class );
	}

	/**
	 * Retrieves an occurrence of a <code>Domain</code> specified by the given name.
	 * 
	 * @param domain
	 * @return the {@link Domain} instance corresponding to it
	 * */
	public Domain selectByName(String domain) {
		List<Map.Entry<String, Object>> parameters = new ArrayList<Map.Entry<String, Object>>(1);
		parameters.add(new AbstractMap.SimpleEntry<String, Object>("domainName", domain));
		
		List<Domain> domains = callNamedQuery("Domain.findByName", parameters);
		return (domains.size() > 0) ? domains.get(0) : null;
	}

	/**
	 * Retrieves an occurrence of a <code>Domain</code> specified by the given roleID.
	 * 
	 * @param roleID
	 * @return the {@link Domain} instance corresponding to the given roleID
	 * */
	public Domain selectByRoleID(long roleID) {
		List<Map.Entry<String, Object>> parameters = new ArrayList<Map.Entry<String, Object>>(1);
		parameters.add(new AbstractMap.SimpleEntry<String, Object>("roleID", roleID));
		
		List<Domain> domains = callNamedQuery("Domain.findByRoleID", parameters);
		return (domains.size() > 0) ? domains.get(0) : null;
	}

	/**
	 * Retrieves the <code>Domain</code> instances that the given <code>User</code> specified by its id
	 * was assigned to.
	 * 
	 * @param userID
	 * @return the {@link List} of {@link Domain}s
	 * */
	public List<Domain> domainsOf(Long userID) {
		List<Map.Entry<String, Object>> parameters = new ArrayList<Map.Entry<String, Object>>(1);
		parameters.add(new AbstractMap.SimpleEntry<String, Object>("userID", userID));
		return callNamedQuery(Domain.NQ_FIND_BY_USER_ID, parameters);
	}
	
	/**
	 * Retrieves the names of the <code>Domain</code> instances that the given <code>User</code> specified by its id
	 * was assigned to.
	 * 
	 * @param userID
	 * @return the {@link List} of {@link Domain} names
	 * */
	public List<String> domainNamesOf(Long userID) {
		List<Map.Entry<String, Object>> parameters = new ArrayList<Map.Entry<String, Object>>(1);
		parameters.add(new AbstractMap.SimpleEntry<String, Object>("userID", userID));
		return callNamedQuery(Domain.NQ_FIND_NAMES_BY_USER_ID, parameters, String.class);
	}

	/**
	 * Retrieves the names of the <code>Domain</code> instances that the given <code>User</code> specified by its id
	 * was assigned to AND owns the <code>Privilege</code> specified by its name.
	 * 
	 * @param userID
	 * @param privilegeName
	 * @return the {@link List} of {@link Domain} names
	 * */
	public List<String> domainNamesOf(Long userID, String privilegeName) {
		List<Map.Entry<String, Object>> parameters = new ArrayList<Map.Entry<String, Object>>(1);
		parameters.add(new AbstractMap.SimpleEntry<String, Object>("userID", userID));
		parameters.add(new AbstractMap.SimpleEntry<String, Object>("privilegeName", privilegeName));
		return callNamedQuery(Domain.NQ_FIND_NAMES_BY_USER_ID_AND_PRIVILEGE, parameters, String.class);
	}

	/**
	 * Retrieves the names of the <code>Domain</code> instances that the given <code>User</code> specified by its id
	 * was assigned to AND owns all of the <code>Privilege</code>s specified by their names.
	 * 
	 * @param userID
	 * @param privilegeNames
	 * @return the {@link List} of {@link Domain} names
	 * */
	public List<String> domainNamesOf(Long userID, Collection<? extends String> privilegeNames) {
		List<Map.Entry<String, Object>> parameters = new ArrayList<Map.Entry<String, Object>>(1);
		parameters.add(new AbstractMap.SimpleEntry<String, Object>("userID", userID));
		parameters.add(new AbstractMap.SimpleEntry<String, Object>("privilegeNames", privilegeNames));
		parameters.add(new AbstractMap.SimpleEntry<String, Object>("count", privilegeNames.size()));
		return callNamedQuery(Domain.NQ_FIND_NAMES_BY_USER_ID_AND_PRIVILEGE_NAMES, parameters, String.class);
	}

	/**
	 * Retrieves the <code>Domain</code> instances that the given <code>User</code> specified by its id
	 * was assigned to AND owns all of the <code>Privilege</code>s specified by their names.
	 * 
	 * @param userID
	 * @param privilegeNames
	 * @return the {@link List} of {@link Domain} names
	 * */
	public List<Domain> domainsOf(Long userID, Collection<? extends String> privilegeNames) {
		List<Map.Entry<String, Object>> parameters = new ArrayList<Map.Entry<String, Object>>(1);
		parameters.add(new AbstractMap.SimpleEntry<String, Object>("userID", userID));
		parameters.add(new AbstractMap.SimpleEntry<String, Object>("privilegeNames", privilegeNames));
		parameters.add(new AbstractMap.SimpleEntry<String, Object>("count", privilegeNames.size()));
		return callNamedQuery(Domain.NQ_FIND_ALL_BY_USER_ID_AND_PRIVILEGE_NAMES, parameters);
	}

	/**
	 * Determines whether the <code>User</code> corresponding to the given userID has all the <code>Privilege</code>s
	 * passed as names in the given <code>Domain</code>.
	 * 
	 * @param userID
	 * @param domainName
	 * @param privilegeNames
	 * @return true if all the {@link Privilege}s are owned by the given {@link User} in the given {@link Domain}
	 * */
	public boolean hasPrivileges(Long userID, String domainName, Collection<? extends String> privilegeNames) {
		return domainNameOf(userID, domainName, privilegeNames).size() > 0;
	}

	/**
	 * Determines whether the <code>User</code> corresponding to the given username has all the <code>Privilege</code>s
	 * passed as names in the given <code>Domain</code>.
	 * 
	 * @param username
	 * @param domainName
	 * @param privilegeNames
	 * @return true if all the {@link Privilege}s are owned by the given {@link User} in the given {@link Domain}
	 * */
	public boolean hasPrivileges(String username, String domainName, Collection<? extends String> privilegeNames) {
		return domainNameOf(username, domainName, privilegeNames).size() > 0;
	}

	/**
	 * Determines whether the <code>User</code> corresponding to the given username owns the <code>Privilege</code>
	 * passed as its name in the given <code>Domain</code>.
	 * 
	 * @param username
	 * @param domainID
	 * @param privilegeName
	 * @return true the {@link Privilege} is owned by the given {@link User} in the given {@link Domain}
	 * */
	public boolean hasPrivilege(String username, Long domainID, String privilegeName) {
		return domainNameOf(username, domainID, privilegeName).size() > 0;
	}

	/**
	 * Retrieves the name of the <code>Domain</code> instance that the given <code>User</code> specified by its username
	 * was assigned to AND owns the <code>Privilege</code> specified by its name.
	 * 
	 * @param username
	 * @param domainID
	 * @param privilegeName
	 * @return the {@link List} of {@link Domain} names
	 * */
	private List<String> domainNameOf(String username, Long domainID, String privilegeName) {
		List<Map.Entry<String, Object>> parameters = new ArrayList<Map.Entry<String, Object>>(1);
		parameters.add(new AbstractMap.SimpleEntry<String, Object>("username", username));
		parameters.add(new AbstractMap.SimpleEntry<String, Object>("domainID", domainID));
		parameters.add(new AbstractMap.SimpleEntry<String, Object>("privilegeName", privilegeName));
		return callNamedQuery(Domain.NQ_FIND_NAMES_BY_USERNAME_AND_PRIVILEGE_AND_ID, parameters, String.class);
	}


	/**
	 * Determines whether the <code>User</code> corresponding to the given username owns the <code>Privilege</code>
	 * passed as its name in the given <code>Domain</code>.
	 * 
	 * @param username
	 * @param domainName
	 * @param privilegeName
	 * @return true if all the {@link Privilege}s are owned by the given {@link User} in the given {@link Domain}
	 * */
	public boolean hasPrivilege(String username, String domainName, String privilegeName) {
		return domainNameOf(username, domainName, privilegeName).size() > 0;
	}

	/**
	 * Retrieves the name of the <code>Domain</code> instance that the given <code>User</code> specified by its username
	 * was assigned to AND owns the <code>Privilege</code> specified by its name.
	 * 
	 * @param username
	 * @param domainName
	 * @param privilegeName
	 * @return the {@link List} of {@link Domain} names
	 * */
	private List<String> domainNameOf(String username, String domainName, String privilegeName) {
		List<Map.Entry<String, Object>> parameters = new ArrayList<Map.Entry<String, Object>>(1);
		parameters.add(new AbstractMap.SimpleEntry<String, Object>("username", username));
		parameters.add(new AbstractMap.SimpleEntry<String, Object>("domainName", domainName));
		parameters.add(new AbstractMap.SimpleEntry<String, Object>("privilegeName", privilegeName));
		return callNamedQuery(Domain.NQ_FIND_NAMES_BY_USERNAME_AND_PRIVILEGE, parameters, String.class);
	}

	/**
	 * Determines whether the <code>User</code> corresponding to the given username has all the <code>Privilege</code>s
	 * passed as names in the given <code>Domain</code>.
	 * 
	 * @param username
	 * @param domainID
	 * @param privilegeNames
	 * @return true if all the {@link Privilege}s are owned by the given {@link User} in the given {@link Domain}
	 * */
	public boolean hasPrivileges(String username, Long domainID, Collection<? extends String> privilegeNames) {
		return domainNameOf(username, domainID, privilegeNames).size() > 0;
	}

	
	/**
	 * Retrieves the name of the <code>Domain</code> instance that the given <code>User</code> specified by its username
	 * was assigned to AND owns all of the <code>Privilege</code>s specified by their names.
	 * 
	 * @param username
	 * @param domainID
	 * @param privilegeNames
	 * @return the {@link List} of {@link Domain} names
	 * */
	private List<String> domainNameOf(String username, Long domainID, Collection<? extends String> privilegeNames) {
		List<Map.Entry<String, Object>> parameters = new ArrayList<Map.Entry<String, Object>>(1);
		parameters.add(new AbstractMap.SimpleEntry<String, Object>("username", username));
		parameters.add(new AbstractMap.SimpleEntry<String, Object>("domainID", domainID));
		parameters.add(new AbstractMap.SimpleEntry<String, Object>("privilegeNames", privilegeNames));
		parameters.add(new AbstractMap.SimpleEntry<String, Object>("count", privilegeNames.size()));
		return callNamedQuery(Domain.NQ_FIND_NAME_BY_USERNAME_AND_PRIVILEGE_NAMES_AND_ID, parameters, String.class);
	}

	/**
	 * Retrieves the name of the <code>Domain</code> instance that the given <code>User</code> specified by its id
	 * was assigned to AND owns all of the <code>Privilege</code>s specified by their names.
	 * 
	 * @param userID
	 * @param domainName
	 * @param privilegeNames
	 * @return the {@link List} of {@link Domain} names
	 * */
	private List<String> domainNameOf(Long userID, String domainName, Collection<? extends String> privilegeNames) {
		List<Map.Entry<String, Object>> parameters = new ArrayList<Map.Entry<String, Object>>(1);
		parameters.add(new AbstractMap.SimpleEntry<String, Object>("userID", userID));
		parameters.add(new AbstractMap.SimpleEntry<String, Object>("domainName", domainName));
		parameters.add(new AbstractMap.SimpleEntry<String, Object>("privilegeNames", privilegeNames));
		parameters.add(new AbstractMap.SimpleEntry<String, Object>("count", privilegeNames.size()));
		return callNamedQuery(Domain.NQ_FIND_NAME_BY_USER_ID_AND_PRIVILEGE_NAMES, parameters, String.class);
	}

	/**
	 * Retrieves the name of the <code>Domain</code> instance that the given <code>User</code> specified by its username
	 * was assigned to AND owns all of the <code>Privilege</code>s specified by their names.
	 * 
	 * @param username
	 * @param domainName
	 * @param privilegeNames
	 * @return the {@link List} of {@link Domain} names
	 * */
	private List<String> domainNameOf(String username, String domainName, Collection<? extends String> privilegeNames) {
		List<Map.Entry<String, Object>> parameters = new ArrayList<Map.Entry<String, Object>>(1);
		parameters.add(new AbstractMap.SimpleEntry<String, Object>("username", username));
		parameters.add(new AbstractMap.SimpleEntry<String, Object>("domainName", domainName));
		parameters.add(new AbstractMap.SimpleEntry<String, Object>("privilegeNames", privilegeNames));
		parameters.add(new AbstractMap.SimpleEntry<String, Object>("count", privilegeNames.size()));
		return callNamedQuery(Domain.NQ_FIND_NAME_BY_USERNAME_AND_PRIVILEGE_NAMES, parameters, String.class);
	}

	/**
	 * @see {@link AbstractDataService#getEntityClass()}
	 * */
	@Override
	protected Class<Domain> getEntityClass() {
		return Domain.class;
	}

	/**
	 * @see {@link AbstractDataService#delete(hu.bme.aut.wman.model.AbstractEntity)}
	 * */
	@Override
	public void delete(Domain domain) throws EntityNotDeletableException {
		List<DomainAssignment> assignments = domainAssignmentService.selectByDomainName(domain.getName());
		for(DomainAssignment da : assignments)
			domainAssignmentService.delete( da );
		super.delete( domain );
	}

	/**
	 * Only for testing purposes!
	 * 
	 * @param domainAssignmentService
	 * */
	public void set(DomainAssignmentService domainAssignmentService) {
		this.domainAssignmentService = domainAssignmentService;
	}
}
