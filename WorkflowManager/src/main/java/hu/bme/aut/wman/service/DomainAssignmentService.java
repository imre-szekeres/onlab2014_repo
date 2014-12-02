/**
 * DomainAssignmentService.java
 */
package hu.bme.aut.wman.service;

import hu.bme.aut.wman.model.Domain;
import hu.bme.aut.wman.model.DomainAssignment;
import hu.bme.aut.wman.model.Role;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;

/**
 * @author Imre Szekeres
 * @version "%I%, %G%"
 */
@LocalBean
@Stateless
public class DomainAssignmentService extends AbstractDataService<DomainAssignment> {

	public List<DomainAssignment> selectByDomainName(String domain) {
		List<Map.Entry<String, Object>> parameters = new ArrayList<>(1);
		parameters.add(new AbstractMap.SimpleEntry<String, Object>("domainName", domain));
		return callNamedQuery(DomainAssignment.NQ_FIND_BY_DOMAIN, parameters);
	}
	
	public List<DomainAssignment> selectByUserID(long userID) {
		List<Map.Entry<String, Object>> parameters = new ArrayList<>(1);
		parameters.add(new AbstractMap.SimpleEntry<String, Object>("userID", userID));
		return callNamedQuery(DomainAssignment.NQ_FIND_BY_USER_ID, parameters);
	}
	
	public DomainAssignment selectByDomainFor(long userID, String domain) {
		List<Map.Entry<String, Object>> parameters = new ArrayList<>(2);
		parameters.add(new AbstractMap.SimpleEntry<String, Object>("userID", userID));
		parameters.add(new AbstractMap.SimpleEntry<String, Object>("domainName", domain));
		List<DomainAssignment> assignments = callNamedQuery(DomainAssignment.NQ_FIND_BY_DOMAIN_FOR, parameters);
		
		return (assignments.size() > 0) ? assignments.get(0) : null;
	}
	
	public DomainAssignment selectByDomainFor(String username, String domain) {
		List<Map.Entry<String, Object>> parameters = new ArrayList<>(2);
		parameters.add(new AbstractMap.SimpleEntry<String, Object>("username", username));
		parameters.add(new AbstractMap.SimpleEntry<String, Object>("domainName", domain));
		List<DomainAssignment> assignments = callNamedQuery(DomainAssignment.NQ_FIND_BY_USERNAME_FOR, parameters);
		
		return (assignments.size() > 0) ? assignments.get(0) : null;
	}

	/**
	 * Attempts to delete the <code>DomainAssignment</code> given by its id without querying the whole entity
	 * into the memory.
	 * 
	 * @param assignmentId
	 * @return the rows affected
	 * */
	public int deleteAssignmentById(Long assignmentId) {
		List<Map.Entry<String, Object>> parameters = new ArrayList<>(2);
		parameters.add(new AbstractMap.SimpleEntry<String, Object>("id", assignmentId));
		return executeNamedQuery(DomainAssignment.NQ_DELETED_BY_ID, parameters);
	}

	/**
	 * Retrieves the <code>Domain</code> names and <code>DomainAssignment</code> ids as a <code>Map</code>
	 * using the domain name as a key.
	 * 
	 * @param userID
	 * @return a {@link Map} of <domainName, domainAssignmentID> pairs
	 * */
	public Map<String, Long> selectDomainsAndIds(Long userID) {
		List<Map.Entry<String, Object>> parameters = new ArrayList<>(2);
		parameters.add(new AbstractMap.SimpleEntry<String, Object>("userID", userID));
		return assignmentsMapOf(callNamedQuery(DomainAssignment.NQ_FIND_DOMAINS_AND_IDS, parameters, Object[].class));
	}

	/**
	 * Helper method for building a <code>Map</code> containing <domainName, domainAssignmentId> pairs
	 * from the result of calling the <code>NamedQuery</code> <code>NQ_FIND_DOMAINS_AND_IDS</code> in the 
	 * class <code>DomainAssignment</code>
	 * 
	 * @param entries
	 * @return a {@link Map}
	 * */
	protected Map<String, Long> assignmentsMapOf(List<Object[]> entries) {
		Map<String, Long> map = new HashMap<>(0);
		for(Object[] e : entries)
			map.put(e[0].toString(), (Long) e[1]);
		return map;
	}

	/**
	 * Obtains all the <code>Domain</code> names and all the <code>Role</code> names the <code>User</code> 
	 * specified by its user id has.
	 * 
	 * @param userID
	 * @return a {@link Map} containing its {@link Role}s ordered by the {@link Domain}s they are defined in
	 * */
	public Map<String, List<String>> assignmentsOf(long userID) {
		List<DomainAssignment> assignments = selectByUserID( userID );
		Map<String, List<String>> results = new HashMap<>();
		if (assignments.isEmpty())
			return results;
		
		List<String> roles;
		for(DomainAssignment da : assignments) {
			roles = new ArrayList<>();
			for(Role r : da.getUserRoles())
				roles.add( r.getName() );
			results.put(da.getDomain().getName(), roles);
		}
		return results;
	}

	/**
	 * @see {@link AbstractDataService#getClass()}
	 * */
	@Override
	protected Class<DomainAssignment> getEntityClass() {
		return DomainAssignment.class;
	}

}
