package hu.bme.aut.wman.service;

import hu.bme.aut.wman.model.ActionType;
import hu.bme.aut.wman.model.State;
import hu.bme.aut.wman.model.Transition;
import hu.bme.aut.wman.model.graph.GraphEdge;
import hu.bme.aut.wman.model.graph.GraphNode;
import hu.bme.aut.wman.model.graph.StateGraph;
import hu.bme.aut.wman.view.objects.NewTransitionVO;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map.Entry;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.inject.Inject;

import com.google.common.base.Function;
import com.google.common.collect.Collections2;

/**
 * Helps make operations with <code>StateNavigationEntry</code>.
 *
 * @version "%I%, %G%"
 */
@Stateless
@LocalBean
public class TransitionService extends AbstractDataService<Transition> {

	private static final long serialVersionUID = 6443637422553071946L;

	@Inject
	GraphNodeService nodeService;
	@Inject
	StateGraphService graphService;
	@Inject
	StateService stateService;
	@Inject
	ActionTypeService actionService;

	public void save(NewTransitionVO transitionVO, Long fromId, Long toId) throws IllegalArgumentException {

		GraphNode fromNode = nodeService.selectById(fromId);
		GraphNode toNode = nodeService.selectById(toId);
		State fromState = stateService.selectById(fromNode.getStateId());
		State toState = stateService.selectById(toNode.getStateId());
		ActionType action = actionService.selectById(transitionVO.getActionId());

		// Check if there is an other transition with this action from the same state
		List<Entry<String, Object>> parameterList = new ArrayList<Entry<String, Object>>();
		parameterList.add(new AbstractMap.SimpleEntry<String, Object>(Transition.PR_ACTION_TYPE, action));
		parameterList.add(new AbstractMap.SimpleEntry<String, Object>(Transition.PR_PARENT_STATE, fromState));
		List<Transition> sameTransition = selectByParameters(parameterList);

		if (sameTransition.size()>0) {
			throw new IllegalArgumentException("There is an other transition with this action from state: " + fromState.getName());
		}

		// create a new transaction
		//		Transition entity = new Transition(transitionVO.getAction(), toState, fromState);
		Transition transition = attach(new Transition(action, toState, fromState));

		// create graph edge
		StateGraph graph = graphService.selectByWorkflowId(transitionVO.getWorkflowId()).get(0);

		GraphEdge graphEdge = new GraphEdge();
		graphEdge.setEnd(toNode);
		graphEdge.setStart(fromNode);
		graphEdge.setGraph(graph);
		graphEdge.setLabel(action.getActionTypeName());
		graphEdge.setTransitionId(transition.getId());

		graph.getEdges().add(graphEdge);
	}

	public List<Transition> selectByParentId(Long parentId) {
		List<Entry<String, Object>> parameterList = new ArrayList<Entry<String, Object>>();
		parameterList.add(new AbstractMap.SimpleEntry<String, Object>("parentId", parentId));
		return callNamedQuery(Transition.NQ_FIND_BY_PARENT_ID, parameterList);
	}

	public List<Transition> selectByNextStateId(Long nextId) {
		List<Entry<String, Object>> parameterList = new ArrayList<Entry<String, Object>>();
		parameterList.add(new AbstractMap.SimpleEntry<String, Object>("nextId", nextId));
		return callNamedQuery(Transition.NQ_FIND_BY_NEXT_STATE_ID, parameterList);
	}

	public List<Transition> selectByActionTypeId(Long typeId) {
		List<Entry<String, Object>> parameterList = new ArrayList<Entry<String, Object>>();
		parameterList.add(new AbstractMap.SimpleEntry<String, Object>("typeId", typeId));
		return callNamedQuery(Transition.NQ_FIND_BY_ACTIONTYPE_ID, parameterList);
	}

	public Collection<State> selectNextStates(State state) {
		return Collections2.transform(selectByParentId(state.getId()),
				new Function<Transition, State>() {

			@Override
			public State apply(Transition navigationEntry) {
				return navigationEntry.getNextState();
			}
		});
	}

	public Collection<State> selectParents(State state) {
		return Collections2.transform(selectByNextStateId(state.getId()),
				new Function<Transition, State>() {

			@Override
			public State apply(Transition navigationEntry) {
				return navigationEntry.getParent();
			}
		});
	}

	@Override
	protected Class<Transition> getEntityClass() {
		return Transition.class;
	}
}
