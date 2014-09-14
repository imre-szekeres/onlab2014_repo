package hu.bme.aut.wman.service;

import hu.bme.aut.wman.model.ProjectAssignment;

import java.util.List;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.TypedQuery;

/**
 * Helps make operations with <code>ProjectAssignment</code>.
 * 
 * @version "%I%, %G%"
 */
@Stateless
@LocalBean
class ProjectAssignmentService extends AbstractDataService<ProjectAssignment> {

	/**
	 * Use findByParameters method instead
	 */
	@Deprecated
	public List<ProjectAssignment> findByUserName(String userName) {
		TypedQuery<ProjectAssignment> select = em.createNamedQuery("ProjectAssignment.findByUser", ProjectAssignment.class);
		select.setParameter("userName", userName);
		return select.getResultList();
	}

	/**
	 * Use findByParameters method instead
	 */
	@Deprecated
	public List<ProjectAssignment> findByProjectId(Long projectId) {
		TypedQuery<ProjectAssignment> select = em.createNamedQuery("ProjectAssignment.findByProjectId", ProjectAssignment.class);
		select.setParameter("projectId", projectId);
		return select.getResultList();
	}

	@Override
	protected Class<ProjectAssignment> getEntityClass() {
		return ProjectAssignment.class;
	}

	// TODO rewrite this method
	// public ProjectAssignment createFor(Long projectID, Long userID) {
	// User user = em.find(User.class, userID);
	// Project project = em.find(Project.class, projectID);
	// ProjectAssignment pa = new ProjectAssignment(user, project);
	// em.persist(pa);
	// return pa;
	// }
}
