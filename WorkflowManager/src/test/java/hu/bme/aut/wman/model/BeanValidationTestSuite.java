/**
 * BeanValidationTestSuite.java
 */
package hu.bme.aut.wman.model;

import static org.junit.Assert.assertTrue;
import hu.bme.aut.wman.service.validation.DomainValidator;
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
	public BeanValidationTestSuite(ValidationEngine engine, AbstractEntity invalid, AbstractEntity valid) {
		this.validator = engine;
		this.invalid = invalid;
		this.valid = valid;
	}


	@Parameters
	public static Collection<Object[]> engines() {
		User validUser = new User( "sudoer", "sudoer7", 
				                   "sudoer@workflowmanager.com",
				                   "This is a subject User for testing the application via the JUnit testing framework. " +
				                   "Hes sole purpose is that to succeed in the test ahead." );
		@SuppressWarnings("deprecation")
		Object[][] engines = new Object[][] {
				{new UserValidator(), new User(), validUser},
				{new RoleValidator(), new Role(), new Role("System Viewer")},
				{new DomainValidator(), new Domain(), new Domain("System")}
		};
		return Arrays.asList(engines);
	}


	@Test
	public void testInvalidEntity() {
		@SuppressWarnings("unchecked")
		Map<String, String> errors = validator.validate(invalid);
		printMap(errors);
		assertTrue(errors.size() > 0);
	}

	@Test
	public void testValidEntity() {
		@SuppressWarnings("unchecked")
		Map<String, String> errors = validator.validate(valid);
		printMap(errors);
		assertTrue(errors.size() <= 0);
	}
	
	private static final void printMap(Map<?, ?> map) {
		for(Object key : map.keySet())
			System.out.println(key.toString() + ": " + map.get(key).toString());
	}
	
	@SuppressWarnings("rawtypes")
	private ValidationEngine validator;
	private AbstractEntity invalid;
	private AbstractEntity valid;
	
}
