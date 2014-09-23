package hu.bme.aut.wman.service;

import hu.bme.aut.wman.exceptions.EntityNotDeletableException;
import hu.bme.aut.wman.model.AbstractEntity;
import hu.bme.aut.wman.model.Comment;
import hu.bme.aut.wman.model.User;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;

import org.springframework.beans.factory.annotation.Autowired;

/**
 * Helps make operations with <code>Comment</code>.
 * 
 * @version "%I%, %G%"
 */
@Stateless
@LocalBean
public class CommentService extends AbstractDataService<Comment> {

	@Autowired
	UserService userService;
	@Autowired
	ProjectService projectService;

	/**
	 * @param entity
	 *            the comment to delete
	 */
	@Override
	public void delete(Comment entity) {
		entity.getProject().removeComment(entity);
		entity.getUser().removeComment(entity);

		userService.save(entity.getUser());
		projectService.save(entity.getProject());
		try {
			super.delete(entity);
		} catch (EntityNotDeletableException e) {
			// it should not happen
		}
	}

	/**
	 * @param userName
	 * @return the comments which was write by the given user
	 */
	public List<Comment> selectByUserName(String userName) {
		List<Entry<String, Object>> parameterList = new ArrayList<Entry<String, Object>>();
		parameterList.add(new AbstractMap.SimpleEntry<String, Object>(User.PR_NAME, userName));
		return callNamedQuery(Comment.NQ_FIND_BY_USER_NAME, parameterList);
	}

	/**
	 * @param userName
	 * @return the comments which are on the given project
	 */
	public List<Comment> selectByProjectId(String projectId) {
		List<Entry<String, Object>> parameterList = new ArrayList<Entry<String, Object>>();
		parameterList.add(new AbstractMap.SimpleEntry<String, Object>(AbstractEntity.PR_ID, projectId));
		return callNamedQuery(Comment.NQ_FIND_BY_PROJECT_ID, parameterList);
	}

	@Override
	protected Class<Comment> getEntityClass() {
		return Comment.class;
	}
}
