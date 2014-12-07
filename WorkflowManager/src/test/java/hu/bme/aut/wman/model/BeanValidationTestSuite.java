/**
 * BeanValidationTestSuite.java
 */
package hu.bme.aut.wman.model;

import static org.junit.Assert.assertTrue;
import hu.bme.aut.wman.service.validation.RoleValidator;
import hu.bme.aut.wman.service.validation.UserValidator;
import hu.bme.aut.wman.service.validation.ValidationEngine;

import java.util.Arrays;
import java.util.Collection;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
/**
 * @author Imre Szekeres
 * @version "%I%, %G%"
 */
@RunWith(Parameterized.class)
public class BeanValidationTestSuite {

	@SuppressWarnings("rawtypes")
	public BeanValidationTestSuite(ValidationEngine engine, AbstractEntity entity) {
		validator = engine;
		testSubject = entity;
	}
	
	@Parameters
	public static Collection<Object[]> engines() {
		@SuppressWarnings("deprecation")
		Object[][] engines = new Object[][] {
				{new UserValidator(), new User()},
				{new RoleValidator(), new Role()}
		};
		return Arrays.asList(engines);
	}
	
	
	@Test
	public void simpleValidation() {
		
		@SuppressWarnings("unchecked")
		Map<String, String> errors = validator.validate(testSubject);
		printMap(errors);
		assertTrue(errors.size() > 0);
	}
	
	private static final void printMap(Map<?, ?> map) {
		for(Object key : map.keySet())
			System.out.println(key.toString() + ": " + map.get(key).toString());
	}
	
	@SuppressWarnings("rawtypes")
	private ValidationEngine validator;
	private AbstractEntity testSubject;
}
