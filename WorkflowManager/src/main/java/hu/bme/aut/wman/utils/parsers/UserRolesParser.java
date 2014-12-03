/**
 * UserRolesParser.java
 */
package hu.bme.aut.wman.utils.parsers;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;

import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.JsonToken;

/**
 * Handles the transformation from JSON <code>String</code> to <code>Map</code> and
 * vice versa.
 * 
 * @author Imre Szekeres
 * @version "%I%, %G%"
 */
@LocalBean
@Stateless
public class UserRolesParser implements JsonParser<String, List<String>> {

	private static final JsonFactory FACTORY;
	
	static {
		FACTORY = new JsonFactory();
	}

	/**
	 * Builds a <code>Map</code> from the given JSON <code>String</code>. 
	 * 
	 * @param json
	 * @return the result {@link Map}
	 * @throws {@link Exception}
	 * 
	 * @see {@link hu.bme.aut.wman.utils.parsers.JsonParser#parse(String)}
	 * */
	@Override
	public Map<String, List<String>> parse(String json) throws Exception {
		Map<String, List<String>> results = new HashMap<>();
		try (org.codehaus.jackson.JsonParser parser = FACTORY.createJsonParser(json.getBytes())) {
			parser.nextToken();
			
			List<String> roles;
			String domainName;
			while (parser.nextToken() != JsonToken.END_OBJECT) {
				domainName = parser.getCurrentName();
				roles = new ArrayList<String>();
				
				parser.nextToken();
				while (parser.nextToken() != JsonToken.END_ARRAY) {
					roles.add( parser.getText() );
				}
				results.put(domainName, roles);
			}
			return results;
		} catch(Exception e) {
			throw e;
		}
	}

	/**
	 * Builds a JSON <code>String</code> from the given <code>Map</code> containing domainName: [ roleName1, roleName2, ..., roleNameN ] 
	 * entries.
	 * 
	 * @param map
	 * @return the result JSON {@link String}
	 * @throws {@link Exception}
	 * 
	 * @see {@link hu.bme.aut.wman.utils.parsers.JsonParser#parse(String)}
	 * */
	@Override
	public String stringify(Map<String, List<String>> map) throws Exception {
		String jsonResult;
		try (ByteArrayOutputStream byteArray = new ByteArrayOutputStream()) {
			try (JsonGenerator generator = FACTORY.createJsonGenerator( byteArray )) {
				
				generator.writeStartObject(); /* { */
				for(String domainName : map.keySet()) {
					generator.writeFieldName( domainName ); /* 'domain-i': */
					
					generator.writeStartArray(); /* [ */
					for(String roleName : map.get( domainName )) {
						generator.writeString(roleName);     /* 'role-i' */
					}
					generator.writeEndArray(); /* ] */
				}
				generator.writeEndObject(); /* } */
			}
			jsonResult = new String(byteArray.toByteArray(), "UTF-8");
		}
		return jsonResult;
	}
}
