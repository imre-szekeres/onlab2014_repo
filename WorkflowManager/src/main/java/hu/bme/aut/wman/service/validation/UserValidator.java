/**
 * UserValidator.java
 */
package hu.bme.aut.wman.service.validation;

import hu.bme.aut.wman.model.User;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.validation.ConstraintViolation;
/**
 * @author Imre Szekeres
 * @version "%I%, %G%"
 */
public class UserValidator extends ValidationEngine<User> {

	@Override
	public Map<String, String> validate(User user) {
		Map<String, String> errors = new HashMap<String, String>(0);
		Set<ConstraintViolation<User>> violations;
		
		violations = validator.validateProperty(user, "username");
		if(violations.size() > 0) errors.put("username", violations.iterator().next().getMessage());
		
		violations = validator.validateProperty(user, "password");
		if(violations.size() > 0) errors.put("password", violations.iterator().next().getMessage());
		
		violations = validator.validateProperty(user, "email");
		if(violations.size() > 0) errors.put("email", violations.iterator().next().getMessage());
		
		violations = validator.validateProperty(user, "description");
		if(violations.size() > 0) errors.put("description", violations.iterator().next().getMessage());
		
		return errors;
	}
}
