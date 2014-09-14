package hu.bme.aut.wman.service;

import hu.bme.aut.wman.model.ActionType;

import javax.annotation.PostConstruct;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;

@Stateless
@LocalBean
public class ActionTypeService extends AbstractDataService<ActionType> {

	@PostConstruct
	private void init() {
		this.setClass(ActionType.class);
	}
}
