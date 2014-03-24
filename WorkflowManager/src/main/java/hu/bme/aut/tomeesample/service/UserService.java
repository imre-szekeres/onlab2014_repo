/**
 * UserService.java
 */
package hu.bme.aut.tomeesample.service;

import hu.bme.aut.tomeesample.model.*;
import javax.persistence.*;
import java.util.*;
import javax.ejb.*;
/**
 * @author Imre Szekeres
 *
 */
@LocalBean
public class UserService {

	@PersistenceContext
	EntityManager em;
	
	public void createUser(Map<String, Object> properties) {
		User user = new User(
				(String)properties.get("username"),
				(String)properties.get("password"),
				(Set<Role>)properties.get("roles")
		);
		em.persist(user);
	}
}
