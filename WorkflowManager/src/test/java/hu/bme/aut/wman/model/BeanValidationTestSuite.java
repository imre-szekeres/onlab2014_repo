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
	public static final String SUBJECT_NAME = "sudoer";
	public static final String DEFAULT_DESCRIPTION = "This is a subject User for testing the application via the JUnit testing framework. " +
            							             "His sole purpose is that to succeed in the test ahead.";
	private List<String> errorAttributes;
	
	@SuppressWarnings("rawtypes")
	public BeanValidationTestSuite(ValidationEngine engine, AbstractEntity invalid, AbstractEntity valid, List<String> errorAttributes) {
		this.validator = engine;
		this.invalid = invalid;
		this.valid = valid;
		this.errorAttributes = errorAttributes;
	}

	public final static User newValidUser(String username, String password) {
		return new User( username, 
				         password,  
                         "sudoer@workflowmanager.com",
                         DEFAULT_DESCRIPTION );
	}

	public static final User newValidUser() {
		return newValidUser(SUBJECT_NAME, "sudoer7");
	}

	@Parameters
	public static Collection<Object[]> engines() {
		User validUser = newValidUser();
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
		logMap(errors);
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
		logMap(errors);
		assertTrue(errors.size() <= 0);
	}
	
	private static final void logMap(Map<?, ?> map) {
		for(Object key : map.keySet())
			LOGGER.info(key.toString() + ": " + map.get(key).toString());
	}
	
	@SuppressWarnings("rawtypes")
	private ValidationEngine validator;
	private AbstractEntity invalid;
	private AbstractEntity valid;
	
}
