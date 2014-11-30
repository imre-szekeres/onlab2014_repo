/**
 * UserRolesParser.java
 */
package hu.bme.aut.wman.utils.parsers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Imre Szekeres
 * @version "%I%, %G%"
 */
public class UserRolesParser implements JsonParser<String, List<String>> {

	/*private static final JsonFactory FACTORY;
	
	static {
		
	}*/
	
	@Override
	public Map<String, List<String>> parse(String json) throws Exception {
		Map<String, List<String>> results = new HashMap<>();
		return null;
	}

	@Override
	public String stringify(Map<String, List<String>> map) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

}
