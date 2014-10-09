package hu.bme.aut.wman.service;

import hu.bme.aut.wman.exceptions.EntityNotDeletableException;
import hu.bme.aut.wman.model.ActionType;
import hu.bme.aut.wman.model.Transition;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;

/**
 * Helps make operations with <code>ActionTypes</code>.
 * 
 * @version "%I%, %G%"
 */
@Stateless
@LocalBean
public class ActionTypeService extends AbstractDataService<ActionType> {

	@EJB(mappedName = "java:module/TransitionService")
	TransitionService transitionService;
	@EJB(mappedName = "java:module/RoleService")
	RoleService roleService;

	// TODO we marked it as complex, but I don't remember why :(
	@Override
	public void save(ActionType entity) {
		super.save(entity);
	};

	/**
	 * Deletes the ActionType.
	 * You can not delete an ActionType, if there is at least one transition which using this ActionType.
	 * 
	 * @param entity
	 *            the ActionType to delete
	 * @throws EntityNotDeletableException
	 *             if you can not delete that ActionType for same reason
	 */
	@Override
	public void delete(ActionType entity) throws EntityNotDeletableException {
		List<Transition> relatedTransitions = transitionService.selectByActionTypeId(entity.getId());
		if (relatedTransitions.size() > 0) {
			throw new EntityNotDeletableException("There is " + relatedTransitions.size() + " project(s), which are in this state.");
		} else {
			super.delete(entity);
		}
	};

	@Override
	protected Class<ActionType> getEntityClass() {
		return ActionType.class;
	}
}
