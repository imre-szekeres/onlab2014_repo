/**
 * BeanValidationTestSuite.java
 */
package hu.bme.aut.wman.model;

import static java.lang.String.format;
import static org.junit.Assert.assertTrue;
import hu.bme.aut.wman.service.validation.DomainValidator;
import hu.bme.aut.wman.service.validation.RoleValidator;
import hu.bme.aut.wman.service.validation.UserValidator;
import hu.bme.aut.wman.service.validation.ValidationEngine;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
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

	private static final Logger LOGGER = Logger.getLogger( BeanValidationTestSuite.class );
	private List<String> errorAttributes;
	
	@SuppressWarnings("rawtypes")
	public BeanValidationTestSuite(ValidationEngine engine, AbstractEntity invalid, AbstractEntity valid, List<String> errorAttributes) {
		this.validator = engine;
		this.invalid = invalid;
		this.valid = valid;
		this.errorAttributes = errorAttributes;
	}


	@Parameters
	public static Collection<Object[]> engines() {
		User validUser = new User( "sudoer", "sudoer7", 
				                   "sudoer@workflowmanager.com",
				                   "This is a subject User for testing the application via the JUnit testing framework. " +
				                   "Hes sole purpose is that to succeed in the test ahead." );
		List<String> nameErrorAttributes = Arrays.asList(new String[] {"name"});
		@SuppressWarnings("deprecation")
		Object[][] engines = new Object[][] {
				{new UserValidator(), new User(), validUser, Arrays.asList(new String[] {"username", "password", "email", "description"})},
				{new RoleValidator(), new Role(), new Role("System Viewer"), nameErrorAttributes},
				{new DomainValidator(), new Domain(), new Domain("System"), nameErrorAttributes}
		};
		return Arrays.asList(engines);
	}


	@Test
	public void testInvalidEntity() {
		@SuppressWarnings("unchecked")
		Map<String, String> errors = validator.validate(invalid);
		printMap(errors);
		assertTrue(errors.size() > 0);
		
		for(String attribute : errorAttributes) {
			LOGGER.info(format("validation error of %s", attribute));
			assertTrue(errors.get(attribute) != null);
		}
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
