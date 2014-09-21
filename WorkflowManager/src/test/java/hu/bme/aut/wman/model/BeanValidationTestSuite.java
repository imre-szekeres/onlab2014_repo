/**
 * BeanValidationTestSuite.java
 */
package hu.bme.aut.wman.model;

import static org.junit.Assert.assertTrue;
import java.util.Arrays;

import hu.bme.aut.wman.service.validation.UserValidator;
import hu.bme.aut.wman.service.validation.ValidationEngine;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import static org.junit.runners.Parameterized.Parameters;

import java.util.Collection;
import java.util.Map;
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
		Object[][] engines = new Object[][] {
				{new UserValidator(), new User()}
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
