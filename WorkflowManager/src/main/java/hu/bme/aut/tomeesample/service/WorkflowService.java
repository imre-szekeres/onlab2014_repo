/**
 * WorkflowService.java
 */
package hu.bme.aut.tomeesample.service;


import hu.bme.aut.tomeesample.model.Workflow;
import javax.persistence.*;
import java.util.List;
import javax.ejb.*;
/**
 * @author Imre Szekeres
 * @version "%I%, %G%"
 */
@Stateless
@LocalBean
public class WorkflowService {

	@PersistenceContext
	EntityManager em;
	
	/**
	 * Persists a newly created <code>Workflow</code>.
	 *  
	 * @param workflow the newly created worklow that is about to be persisted.
	 * */
	public void create(Workflow workflow) {
		em.persist(workflow);
	}
	
	public void update(Workflow workflow){
		em.merge(workflow);
	}
	
	public void remove(Workflow workflow){
		em.remove(workflow);
	}
	
	public List<Workflow> findAll(){
		return em.createNamedQuery("Workflow.findAll", Workflow.class).getResultList();
	}
	
	public Workflow findById(Long id){
		try{
			TypedQuery<Workflow> select = em.createNamedQuery("Workflow.findById", Workflow.class);
			select.setParameter("id", id);
			return select.getSingleResult();
		}catch(Exception e){
			return null;
		}
	}
	
	public Workflow findByName(String name){
		try{
			TypedQuery<Workflow> select = em.createNamedQuery("Workflow.findByName", Workflow.class);
			select.setParameter("name", name);
			return select.getSingleResult();
		}catch(Exception e){
			return null;
		}
	}
}
