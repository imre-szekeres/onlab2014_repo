/**
 * DomainService.java
 */
package hu.bme.aut.wman.service;

import hu.bme.aut.wman.exceptions.EntityNotDeletableException;
import hu.bme.aut.wman.model.Domain;
import hu.bme.aut.wman.model.DomainAssignment;
import hu.bme.aut.wman.service.validation.DomainValidator;
import hu.bme.aut.wman.service.validation.ValidationEngine;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.inject.Inject;
/**
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

	@PostConstruct
	public void setup() {
		validator = new DomainValidator();
	}
	
	public Map<String, String> validate(Domain domain) {
		Map<String, String> errors = validator.validate( domain );
		if (selectByName(domain.getName()) != null)
			errors.put("name", "Domain " + domain.getName() + " already exists!");
		return errors;
	}
	
	
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
	
	@Override
	public void delete(Domain domain) throws EntityNotDeletableException {
		List<DomainAssignment> assignments = domainAssignmentService.selectByDomainName(domain.getName());
		for(DomainAssignment da : assignments)
			domainAssignmentService.delete( da );
		super.delete( domain );
	}
}
