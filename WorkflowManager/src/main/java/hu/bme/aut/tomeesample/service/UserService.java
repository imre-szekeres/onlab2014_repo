/**
 * UserService.java
 */
package hu.bme.aut.tomeesample.service;

import hu.bme.aut.tomeesample.model.User;
import javax.persistence.*;
import java.util.List;
import javax.ejb.*;
/**
 * @author Imre Szekeres
 *
 */
@Stateless
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
	
	public User findById(Long id){
		try{
			TypedQuery<User> select = em.createNamedQuery("User.findById", User.class);
			select.setParameter("id", id);
			return select.getSingleResult();
		}catch(Exception e){
			return null;
		}
	}
	
	public User findByName(String username){
		try{
			TypedQuery<User> select = em.createNamedQuery("User.findByName", User.class);
			select.setParameter("username", username);
			return select.getSingleResult();
		}catch(Exception e){
			return null;
		}
	}
}
