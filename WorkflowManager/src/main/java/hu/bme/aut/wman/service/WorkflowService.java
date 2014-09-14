package hu.bme.aut.wman.service;

import hu.bme.aut.wman.model.State;
import hu.bme.aut.wman.model.Workflow;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Map.Entry;

import javax.annotation.PostConstruct;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;

@Stateless
@LocalBean
public class WorkflowService extends AbstractDataService<Workflow> {

	// private Validator validator;

	@PostConstruct
	public void init() {
		this.setClass(Workflow.class);
		// validator = Validation.buildDefaultValidatorFactory().getValidator();
	}

	public void setWorkflowToState(Workflow workflow, State state) {
		workflow.addState(state);
		save(workflow);
	}

	public Workflow findByName(String name) {
		ArrayList<Entry<String, Object>> parameterList = new ArrayList<Entry<String, Object>>();
		parameterList.add(new AbstractMap.SimpleEntry<String, Object>("name", name));
		// FIXME should check if has exactly one element
		return findByParameters(parameterList).get(0);
	}

	// public boolean validateName(String name) {
	// return validator.validateValue(Workflow.class, "name", name).size() == 0;
	// }
	//
	// public boolean validateDescription(String description) {
	// return validator.validateValue(Workflow.class, "description", description).size() == 0;
	// }
}
