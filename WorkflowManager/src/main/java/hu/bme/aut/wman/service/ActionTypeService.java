package hu.bme.aut.wman.service;

import hu.bme.aut.wman.model.ActionType;

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

	@Override
	protected Class<ActionType> getEntityClass() {
		return ActionType.class;
	}
}
