/**
 * RoleValidator.java
 */
package hu.bme.aut.wman.service.validation;

import hu.bme.aut.wman.model.Role;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.validation.ConstraintViolation;


/**
 * @author Imre Szekeres
 * @version "%I%, %G%"
 */
public class RoleValidator extends ValidationEngine<Role> {

	@Override
	public Map<String, String> validate(Role role) {
		Map<String, String> errors = new HashMap<String, String>(0);
		Set<ConstraintViolation<Role>> violations;
		
		violations = validator.validateProperty(role, "name");
		if(violations.size() > 0) 
			errors.put("name", violations.iterator().next().getMessage());
		return errors;
	}

}
