/**
 * ProjectService.java 
 */
package hu.bme.aut.tomeesample.service;

import hu.bme.aut.tomeesample.model.Project;
import javax.persistence.*;
import java.util.List;
import javax.ejb.*;
/**
 * @author Imre Szekeres
 * @version "%I%, %G%"
 */
@Stateless
@LocalBean
public class ProjectService {

	@PersistenceContext
	EntityManager em;
	
	
	public void create(Project project) {
		em.persist(project);
	}
	
	public void update(Project project){
		em.merge(project);
	}
	
	public void remove(Project project){
		em.remove(project);
	}
	
	public List<Project> findAll(){
		return em.createNamedQuery("Project.findAll", Project.class).getResultList();
	}
	
	
	public List<Project> findAllByWorkflowName(String workflowName){
		TypedQuery<Project> selectAll = em.createNamedQuery("Project.findAllByWorkflowName", Project.class);
		selectAll.setParameter("name", workflowName);
		return selectAll.getResultList();
	}
	
	
	public Project findById(Long id){
		try{
			TypedQuery<Project> select = em.createNamedQuery("Project.findById", Project.class);
			select.setParameter("id", id);
			return select.getSingleResult();
		}catch(Exception e){
			return null;
		}
	}
	
	public Project findByName(String name){
		try{
			TypedQuery<Project> select = em.createNamedQuery("Project.findByName", Project.class);
			select.setParameter("name", name);
			return select.getSingleResult();
		}catch(Exception e){
			return null;
		}
	}
}
