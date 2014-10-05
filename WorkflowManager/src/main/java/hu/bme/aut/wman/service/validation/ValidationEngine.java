/**
 * ValidationEngine.java
 */
package hu.bme.aut.wman.service.validation;

import hu.bme.aut.wman.model.AbstractEntity;

import java.util.Map;
import javax.validation.Validator;
import javax.validation.Validation;
/**
 * @author Imre Szekeres
 * @version "%I%, %G%"
 */
public abstract class ValidationEngine <T extends AbstractEntity> {

	protected ValidationEngine() {
		validator = Validation.buildDefaultValidatorFactory().getValidator();
	}
	
	public abstract Map<String, String> validate(T entity);
	
	protected Validator validator;
}
