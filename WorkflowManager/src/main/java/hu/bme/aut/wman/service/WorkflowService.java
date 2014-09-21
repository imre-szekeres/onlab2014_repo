package hu.bme.aut.wman.service;

import hu.bme.aut.wman.exceptions.EntityNotFoundException;
import hu.bme.aut.wman.exceptions.TooMuchElementException;
import hu.bme.aut.wman.model.State;
import hu.bme.aut.wman.model.Workflow;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Map.Entry;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;

/**
 * Helps make operations with <code>Workflow</code>.
 * 
 * @version "%I%, %G%"
 */
@Stateless
@LocalBean
public class WorkflowService extends AbstractDataService<Workflow> {

	// private Validator validator;

	// @PostConstruct
	// public void init() {
	// validator = Validation.buildDefaultValidatorFactory().getValidator();
	// }

	public void setWorkflowToState(Workflow workflow, State state) {
		workflow.addState(state);
		save(workflow);
	}

	public Workflow selectByName(String name) throws TooMuchElementException, EntityNotFoundException {
		ArrayList<Entry<String, Object>> parameterList = new ArrayList<Entry<String, Object>>();
		parameterList.add(new AbstractMap.SimpleEntry<String, Object>(Workflow.PR_NAME, name));

		return checkHasExactlyOneElement(selectByOwnProperties(parameterList)).get(0);
	}

	@Override
	protected Class<Workflow> getEntityClass() {
		return Workflow.class;
	}

	// public boolean validateName(String name) {
	// return validator.validateValue(Workflow.class, "name", name).size() == 0;
	// }
	//
	// public boolean validateDescription(String description) {
	// return validator.validateValue(Workflow.class, "description", description).size() == 0;
	// }
}
