/**
 * JsonParserTester.java
 */
package hu.bme.aut.wman.utils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import hu.bme.aut.wman.utils.parsers.JsonParser;
import hu.bme.aut.wman.utils.parsers.UserRolesParser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
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
public class JsonParserTester {

	private static final JsonParser<String, List<String>> PARSER;
	private static final Logger LOGGER;

	static {
		PARSER = new UserRolesParser();
		LOGGER = Logger.getLogger( JsonParserTester.class );
	}

	private String json;
	private Map<String, List<String>> userRoles;

	public JsonParserTester(String json, Map<String, List<String>> userRoles) {
		this.json = json;
		this.userRoles = userRoles;
	}

	@Parameters
	public static final Collection<Object[]> newData() {
		List<Object[]> data = new ArrayList<>();
		data.add( createFirst() );
		data.add( createSecond() );
		return data;
	}

	private static final Object[] createFirst() {
		String json = "{'System': ['View User', 'Create Role']}".replace("'", "\"");
		Map<String, List<String>> userRoles = new HashMap<>();
		userRoles.put("System", Arrays.asList(new String[] {"View User", "Create Role"}));
		return new Object[] {json, userRoles};
	}

	private static final Object[] createSecond() {
		String json = ("{ " + 
	                      "'System': ['View User', 'Create Role'], " +
	                      "'BME.Aut': ['Create Domain', 'Assign Privilege', 'Assign Role'] " +
	                  " }").replace("'", "\"");
		Map<String, List<String>> userRoles = new HashMap<>();
		userRoles.put("System", Arrays.asList(new String[] {"View User", "Create Role"}));
		userRoles.put("BME.Aut", Arrays.asList(new String[] {"Create Domain", "Assign Privilege", "Assign Role"}));
		return new Object[] {json, userRoles};
	}

	@Test
	public void testParse() {
		try {
			assertEquals(PARSER.parse(json), userRoles);
		} catch(Exception e) {
			LOGGER.error(e);
			fail(String.format("testParse failed due to exception: %s", e));
		}
	}

	@Test
	public void testStringify() {
		try {
			String stringified = PARSER.stringify(userRoles);
			assertEquals(PARSER.parse(stringified), PARSER.parse(json));
		} catch(Exception e) {
			LOGGER.error(e);
			fail(String.format("testStringify failed due to exception: %s", e));
		}
	}
}
