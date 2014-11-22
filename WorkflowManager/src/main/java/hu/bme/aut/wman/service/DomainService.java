/**
 * DomainService.java
 */
package hu.bme.aut.wman.service;

import hu.bme.aut.wman.model.Domain;

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
public class DomainService extends AbstractDataService<Domain> {

	public Domain selectByName(String domain) {
		List<Map.Entry<String, Object>> parameters = new ArrayList<Map.Entry<String, Object>>(1);
		parameters.add(new AbstractMap.SimpleEntry<String, Object>("domainName", domain));
		
		List<Domain> domains = callNamedQuery("Domain.findByName", parameters);
		return (domains.size() > 0) ? domains.get(0) : null;
	}
	
	@Override
	protected Class<Domain> getEntityClass() {
		return Domain.class;
	}

}
