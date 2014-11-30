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
		return callNamedQuery("DomainAssignment.findByDomain", parameters);
	}
	
	public List<DomainAssignment> selectByUserID(long userID) {
		List<Map.Entry<String, Object>> parameters = new ArrayList<>(1);
		parameters.add(new AbstractMap.SimpleEntry<String, Object>("userID", userID));
		return callNamedQuery("DomainAssignment.findByUserID", parameters);
	}
	
	public DomainAssignment selectByDomainFor(long userID, String domain) {
		List<Map.Entry<String, Object>> parameters = new ArrayList<>(2);
		parameters.add(new AbstractMap.SimpleEntry<String, Object>("userID", userID));
		parameters.add(new AbstractMap.SimpleEntry<String, Object>("domainName", domain));
		List<DomainAssignment> assignments = callNamedQuery("DomainAssignment.findByDomainFor", parameters);
		
		return (assignments.size() > 0) ? assignments.get(0) : null;
	}
	
	public DomainAssignment selectByDomainFor(String username, String domain) {
		List<Map.Entry<String, Object>> parameters = new ArrayList<>(2);
		parameters.add(new AbstractMap.SimpleEntry<String, Object>("username", username));
		parameters.add(new AbstractMap.SimpleEntry<String, Object>("domainName", domain));
		List<DomainAssignment> assignments = callNamedQuery("DomainAssignment.findByUsernameFor", parameters);
		
		return (assignments.size() > 0) ? assignments.get(0) : null;
	}

	/**
	 * Obtains all the <code>Domain</code> names and all the <code>Role</code> names the <code>User</code> 
	 * specified by its user id has.
	 * 
	 * @param userID
	 * @return a {@link Map} containing its {@link Role}s ordered by the {@link Domain}s they are defined in
	 * */
	public final Map<String, List<String>> assignmentsOf(long userID) {
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
	
	@Override
	protected Class<DomainAssignment> getEntityClass() {
		return DomainAssignment.class;
	}

}
