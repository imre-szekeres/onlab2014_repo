package hu.bme.aut.wman.service;

import hu.bme.aut.wman.model.Comment;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;

/**
 * Helps make operations with <code>Comment</code>.
 * 
 * @version "%I%, %G%"
 */
@Stateless
@LocalBean
public class CommentService extends AbstractDataService<Comment> {

	// TODO rewrite these methods
	// public User createFor(Project project, User user, Comment comment) throws Exception {
	// Project managedp = em.merge(project);
	// User managedu = em.merge(user);
	// comment.setPostDate(new Date());
	// comment.setProject(managedp);
	// comment.setUser(managedu);
	// em.persist(comment);
	// return managedu;
	// }
	//
	// public User removeFrom(Project project, User user, Comment comment) throws Exception {
	// Project managedp = em.merge(project);
	// User managedu = em.merge(user);
	// Comment managedc = em.merge(comment);
	// managedp.removeComment(managedc);
	// managedu.removeComment(managedc);
	// em.remove(managedc);
	// return managedu;
	// }

	public List<Comment> selectByUserName(String userName) {
		List<Entry<String, Object>> parameterList = new ArrayList<Entry<String, Object>>();
		parameterList.add(new AbstractMap.SimpleEntry<String, Object>("userName", userName));
		return callNamedQuery(Comment.NQ_FIND_BY_USER_NAME, parameterList);
	}

	public List<Comment> selectByProjectId(String projectId) {
		List<Entry<String, Object>> parameterList = new ArrayList<Entry<String, Object>>();
		parameterList.add(new AbstractMap.SimpleEntry<String, Object>("id", projectId));
		return callNamedQuery(Comment.NQ_FIND_BY_PROJECT_ID, parameterList);
	}

	@Override
	protected Class<Comment> getEntityClass() {
		return Comment.class;
	}
}
