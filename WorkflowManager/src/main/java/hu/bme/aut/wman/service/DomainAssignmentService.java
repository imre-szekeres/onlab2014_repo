/**
 * DomainAssignmentService.java
 */
package hu.bme.aut.wman.service;

import hu.bme.aut.wman.model.DomainAssignment;

import java.util.AbstractMap;
import java.util.ArrayList;
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
	
	public DomainAssignment selectByDomainFor(long userID, String domain) {
		List<Map.Entry<String, Object>> parameters = new ArrayList<>(2);
		parameters.add(new AbstractMap.SimpleEntry<String, Object>("userID", userID));
		parameters.add(new AbstractMap.SimpleEntry<String, Object>("domainName", domain));
		List<DomainAssignment> assignments = callNamedQuery("DomainAssignment.findByDomainFor", parameters);
		
		return (assignments.size() > 0) ? assignments.get(0) : null;
	}
	
	@Override
	protected Class<DomainAssignment> getEntityClass() {
		return DomainAssignment.class;
	}

}
