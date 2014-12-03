/**
 * PrivilegeService.java
 */
package hu.bme.aut.wman.service;

import hu.bme.aut.wman.model.Privilege;
import hu.bme.aut.wman.service.validation.PrivilegeValidator;
import hu.bme.aut.wman.service.validation.ValidationEngine;

import java.io.Serializable;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.annotation.PostConstruct;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
/**
 * Handles the CRUD operations and business logic of instances of class <code>Privilege</code>.
 * 
 * @author Imre Szekeres
 * @version "%I%, %G%"
 */
@LocalBean
@Stateless
@SuppressWarnings("serial")
public class PrivilegeService extends AbstractDataService<Privilege> implements Serializable {

	private ValidationEngine<Privilege> validationEngine;
	
	public PrivilegeService() {
		super();
	}
	
	@PostConstruct
	public void setup() {
		validationEngine = new PrivilegeValidator();
	}
	
	public Map<String, String> validate(Privilege privilege) {
		Map<String, String> errors = validationEngine.validate(privilege);
		if(selectByName(privilege.getName()) != null)
			errors.put("name", "already exists");
		return errors;
	}
	
	public Privilege selectByName(String name) {
		List<Entry<String, Object>> parameters = new ArrayList<Entry<String, Object>>();
		parameters.add(new AbstractMap.SimpleEntry<String, Object>(Privilege.PR_NAME, name));
		List<Privilege> results = selectByParameters(parameters);
		return (results.size() > 0) ? results.get(0) : null;
	}
	
	@Override
	protected Class<Privilege> getEntityClass() {
		return Privilege.class;
	}
}
