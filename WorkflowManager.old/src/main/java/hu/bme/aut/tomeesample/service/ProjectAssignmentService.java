/**
 * ActionService.java
 */
package hu.bme.aut.tomeesample.service;

import hu.bme.aut.tomeesample.model.Project;
import hu.bme.aut.tomeesample.model.ProjectAssignment;
import hu.bme.aut.tomeesample.model.User;

import java.util.List;

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

	public void removeDetached(ProjectAssignment projectAssignment) {
		ProjectAssignment managed = em.merge(projectAssignment);
		em.remove(managed);
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

	public List<ProjectAssignment> findByUserName(String userName) {
		TypedQuery<ProjectAssignment> select = em.createNamedQuery("ProjectAssignment.findByUser", ProjectAssignment.class);
		select.setParameter("userName", userName);
		return select.getResultList();
	}

	public List<ProjectAssignment> findByProjectId(Long projectId) {
		TypedQuery<ProjectAssignment> select = em.createNamedQuery("ProjectAssignment.findByProjectId", ProjectAssignment.class);
		select.setParameter("projectId", projectId);
		return select.getResultList();
	}

	public ProjectAssignment createFor(Long projectID, Long userID) {
		User user = em.find(User.class, userID);
		Project project = em.find(Project.class, projectID);
		ProjectAssignment pa = new ProjectAssignment(user, project);
		em.persist(pa);
		return pa;
	}
}
