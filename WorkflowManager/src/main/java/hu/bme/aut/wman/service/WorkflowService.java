package hu.bme.aut.wman.service;

import hu.bme.aut.wman.exceptions.EntityNotDeletableException;
import hu.bme.aut.wman.model.Project;
import hu.bme.aut.wman.model.State;
import hu.bme.aut.wman.model.Workflow;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map.Entry;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;

import org.springframework.beans.factory.annotation.Autowired;

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

	@Autowired
	private ProjectService projectService;
	private StateService stateService;

	@Override
	public void delete(Workflow entity) throws EntityNotDeletableException {
		List<Project> relatedProjects = projectService.selectAllByWorkflowName(entity.getName());

		if (relatedProjects.size() > 0) {
			throw new EntityNotDeletableException(entity, getClass());
		} else {
			super.delete(entity);
		}
	}

	public void addState(Workflow workflow, State state) {
		addStates(workflow, Arrays.<State> asList(state));
	}

	public void addStates(Workflow workflow, List<State> states) {
		for (State state : states) {
			state.setWorkflow(workflow);
		}
		workflow.getStates().addAll(states);
		save(workflow);
	}

	public boolean removeState(Workflow workflow, State state) {
		try {
			stateService.delete(state);
			workflow.getStates().remove(state);
			save(workflow);
			return true;
		} catch (EntityNotDeletableException e) {
			return false;
		}
	}

	public boolean verify(Workflow workflow) {
		if (workflow.getInitialState() != null) {
			return true;
		} else {
			return false;
		}
	}

	public Workflow selectByName(String name) {
		ArrayList<Entry<String, Object>> parameterList = new ArrayList<Entry<String, Object>>();
		parameterList.add(new AbstractMap.SimpleEntry<String, Object>(Workflow.PR_NAME, name));
		return selectByParameters(parameterList).get(0);
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
