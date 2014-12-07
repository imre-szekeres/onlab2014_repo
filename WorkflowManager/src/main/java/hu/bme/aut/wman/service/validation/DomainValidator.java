/**
 * DomainValidator.java
 */
package hu.bme.aut.wman.service.validation;

import hu.bme.aut.wman.model.Domain;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.validation.ConstraintViolation;

/**
 * @author Imre Szekeres
 * @version "%I%, %G%"
 */
public class DomainValidator extends ValidationEngine<Domain> {

	@Override
	public Map<String, String> validate(Domain domain) {
		Map<String, String> errors = new HashMap<String, String>(0);
		Set<ConstraintViolation<Domain>> violations;
		
		violations = validator.validateProperty(domain, "name");
		if (violations.size() > 0) 
			errors.put("name", violations.iterator().next().getMessage());
		return errors;
	}

}
