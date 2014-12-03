/**
 * PasswordValidator.java
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
public class PasswordValidator extends ValidationEngine<User> {

	@Override
	public Map<String, String> validate(User user) {
		Map<String, String> errors = new HashMap<>();
		Set<ConstraintViolation<User>> violations = validator.validateProperty(user, "password");
		if (!violations.isEmpty())
			errors.put("password", violations.iterator().next().getMessage());
		return errors;
	}

}
