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
	
	public void create(User user) {
		em.persist(user);
	}
	
	public void update(User user){
		em.merge(user);
	}
	
	public void remove(User user){
		em.remove(user);
	}
	
	public List<User> findAll(){
		return em.createNamedQuery("User.findAll", User.class).getResultList();
	}
	
	public User findByID(Long id){
		try{
			return em.createNamedQuery("User.findById", User.class).getSingleResult();
		}catch(Exception e){
			return null;
		}
	}
}
