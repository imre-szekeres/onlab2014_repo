package hu.bme.aut.wman.service;

import hu.bme.aut.wman.model.ProjectAssignment;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.persistence.TypedQuery;

class ProjectAssignmentService extends AbstractDataService<ProjectAssignment> {

	@PostConstruct
	private void init() {
		this.setClass(ProjectAssignment.class);
	}

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

	// TODO rewrite this method
	// public ProjectAssignment createFor(Long projectID, Long userID) {
	// User user = em.find(User.class, userID);
	// Project project = em.find(Project.class, projectID);
	// ProjectAssignment pa = new ProjectAssignment(user, project);
	// em.persist(pa);
	// return pa;
	// }
}
