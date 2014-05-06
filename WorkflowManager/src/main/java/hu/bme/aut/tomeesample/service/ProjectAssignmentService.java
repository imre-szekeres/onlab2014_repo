/**
 * ActionService.java
 */
package hu.bme.aut.tomeesample.service;

import hu.bme.aut.tomeesample.model.ProjectAssignment;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

/**
 * @author Gergely Várkonyi
 */
@Stateless
@LocalBean
public class ProjectAssignmentService {

	@PersistenceContext
	EntityManager em;

	public void create(ProjectAssignment projectAssignment) {
		em.persist(projectAssignment);
	}

	public void update(ProjectAssignment projectAssignment) {
		em.merge(projectAssignment);
	}

	public void remove(ProjectAssignment projectAssignment) {
		em.remove(projectAssignment);
	}

	public ProjectAssignment findById(Long id) {
		try {
			TypedQuery<ProjectAssignment> select = em.createNamedQuery("ProjectAssignment.findById", ProjectAssignment.class);
			select.setParameter("id", id);
			return select.getSingleResult();
		} catch (Exception e) {
			return null;
		}
	}

	public ProjectAssignment findByUserName(String userName) {
		try {
			TypedQuery<ProjectAssignment> select = em.createNamedQuery("ProjectAssignment.findByUser", ProjectAssignment.class);
			select.setParameter("userName", userName);
			return select.getSingleResult();
		} catch (Exception e) {
			return null;
		}
	}

	public ProjectAssignment findByProjectId(Long projectId) {
		try {
			TypedQuery<ProjectAssignment> select = em.createNamedQuery("ProjectAssignment.findByProjectId", ProjectAssignment.class);
			select.setParameter("projectId", projectId);
			return select.getSingleResult();
		} catch (Exception e) {
			return null;
		}
	}
}
