/**
 * PrivilegeValidator.java
 */
package hu.bme.aut.wman.service.validation;

import java.util.HashMap;
import java.util.Map;

import hu.bme.aut.wman.model.Privilege;
/**
 * @author Imre Szekeres
 * @version "%I%, %G%"
 */
public class PrivilegeValidator extends ValidationEngine<Privilege>{

	@Override
	public Map<String, String> validate(Privilege entity) {
		return new HashMap<String, String>(0);
	}

}
