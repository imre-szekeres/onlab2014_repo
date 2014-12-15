/**
 * PrivilegeService.java
 */
package hu.bme.aut.wman.service;

import static java.lang.String.format;
import hu.bme.aut.wman.model.Privilege;
import hu.bme.aut.wman.service.validation.PrivilegeValidator;
import hu.bme.aut.wman.service.validation.ValidationEngine;

import java.io.Serializable;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityNotFoundException;
/**
 * Handles the CRUD operations and business logic of instances of class <code>Privilege</code>.
 * 
 * @author Imre Szekeres
 * @version "%I%, %G%"
 */
@LocalBean
@Stateless
public class PrivilegeService extends AbstractDataService<Privilege>
				implements Serializable {

	private static final long serialVersionUID = -8249165499277206990L;

	private ValidationEngine<Privilege> validationEngine;

	public PrivilegeService() {
		super();
	}

	/**
	 * Initializes the Service.
	 * */
	@PostConstruct
	public void setup() {
		validationEngine = new PrivilegeValidator();
	}

	/**
	 * Validates a <code>Privilege</code> against the Bean Validation constraints
	 * placed on the fields of the class.
	 * 
	 * @param privilege
	 * @return the {@link Map} containing constraint violation messages
	 * */
	public Map<String, String> validate(Privilege privilege) {
		Map<String, String> errors = validationEngine.validate(privilege);
		if(selectByName(privilege.getName()) != null)
			errors.put("name", "already exists");
		return errors;
	}

	/**
	 * Selects a <code>Privilege</code> corresponding to the given name.
	 * 
	 * @param name
	 * @return the {@link Privilege} corresponding to the given name
	 * */
	public Privilege selectByName(String name) {
		List<Entry<String, Object>> parameters = new ArrayList<Entry<String, Object>>();
		parameters.add(new AbstractMap.SimpleEntry<String, Object>(Privilege.PR_NAME, name));
		List<Privilege> results = selectByParameters(parameters);
		return (results.size() > 0) ? results.get(0) : null;
	}

	/**
	 * Obtains the <code>Privilege</code> names owned by the <code>User</code> specified by its username.
	 * 
	 * @param username
	 * @return a {@link List} of {@link Privilege} names
	 * */
	public List<String> privilegeNamesOf(String username) {
		List<Entry<String, Object>> parameters = new ArrayList<Entry<String, Object>>();
		parameters.add(new AbstractMap.SimpleEntry<String, Object>("username", username));
		return callNamedQuery(Privilege.NQ_FIND_ALL_NAMES_BY_USERNAME, parameters, String.class);
	}

	/**
	 * Obtains the <code>Privilege</code>s owned by the <code>User</code> specified by its username.
	 * 
	 * @param username
	 * @return a {@link List} of {@link Privilege}s
	 * */
	public List<Privilege> privilegesOf(String username) {
		List<Entry<String, Object>> parameters = new ArrayList<Entry<String, Object>>();
		parameters.add(new AbstractMap.SimpleEntry<String, Object>("username", username));
		return callNamedQuery(Privilege.NQ_FIND_ALL_BY_USERNAME, parameters);
	}

	/**
	 * Converts the given <code>Privilege</code> names to <code>Privilege</code> instances.
	 * 
	 * @param privilegeNames
	 * @return the {@link Privilege} instances corresponding to them
	 * @throws {@link EntityNotFoundException}
	 * */
	public Set<Privilege> privilegesOf(Set<String> privilegeNames) throws EntityNotFoundException {
		Set<Privilege> results = new HashSet<>();
		for(String name : privilegeNames) {
			Privilege p = selectByName(name);
			if (p == null)
				throw new EntityNotFoundException(format("Privilege %s was not found!", name));
			results.add( p );
		}
		return results;
	}

	/**
	 * @see {@link AbstractDataService}
	 * */
	@Override
	protected Class<Privilege> getEntityClass() {
		return Privilege.class;
	}
}
