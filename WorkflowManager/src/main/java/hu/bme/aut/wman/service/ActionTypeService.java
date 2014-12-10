package hu.bme.aut.wman.service;

import hu.bme.aut.wman.exceptions.EntityNotDeletableException;
import hu.bme.aut.wman.model.ActionType;
import hu.bme.aut.wman.model.Domain;
import hu.bme.aut.wman.model.Transition;

import java.util.List;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.inject.Inject;


/**
 * Helps make operations with <code>ActionTypes</code>.
 *
 * @version "%I%, %G%"
 */
@Stateless
@LocalBean
public class ActionTypeService extends AbstractDataService<ActionType> {

	@Inject
	TransitionService transitionService;
	@Inject
	RoleService roleService;
	@Inject
	private DomainService domainService;

	@Override
	public void save(ActionType entity) {
		Domain domain = domainService.selectByName(entity.getDomain().getName());
		entity.setDomain(domain);
		domain.getActionTypes().add(entity);
		domainService.save(domain);
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
			throw new EntityNotDeletableException("There is " + relatedTransitions.size() + " action(s) using this action.");
		} else {
			super.delete(entity);
		}
	};

	/**
	 * Deletes the ActionType.
	 * You can not delete a ActionType, if there is at least one <b>active</b> project in with that ActionType.
	 *
	 * @param actionTypeId
	 *            the id of the ActionType to delete
	 * @throws EntityNotDeletableException
	 *             if you can not delete that ActionType for same reason
	 */
	@Override
	public void deleteById(Long actionTypeId) throws EntityNotDeletableException {
		delete(selectById(actionTypeId));
	}

	@Override
	protected Class<ActionType> getEntityClass() {
		return ActionType.class;
	}
}
