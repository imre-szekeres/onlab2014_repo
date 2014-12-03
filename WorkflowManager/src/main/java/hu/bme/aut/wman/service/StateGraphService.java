package hu.bme.aut.wman.service;

import hu.bme.aut.wman.exceptions.EntityNotDeletableException;
import hu.bme.aut.wman.model.AbstractEntity;
import hu.bme.aut.wman.model.graph.GraphEdge;
import hu.bme.aut.wman.model.graph.StateGraph;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.inject.Inject;

/**
 * Helps make operations with <code>BlobFile</code>.
 *
 * @version "%I%, %G%"
 */
@Stateless
@LocalBean
public class StateGraphService extends AbstractDataService<StateGraph> {

	@Inject
	GraphNodeService graphNodeService;
	@Inject
	GraphEdgeService graphEdgeService;

	/**
	 * @param workflowId
	 * @return states which are in the workflow
	 */
	public List<StateGraph> selectByWorkflowId(Long workflowId) {
		List<Entry<String, Object>> parameterList = new ArrayList<Entry<String, Object>>();
		parameterList.add(new AbstractMap.SimpleEntry<String, Object>(AbstractEntity.PR_ID, workflowId));
		return callNamedQuery(StateGraph.NQ_FIND_BY_WORKFLOW_ID, parameterList);
	}

	public Long getStateIdOfNode(Long nodeId) {
		return graphNodeService.selectById(nodeId).getStateId();
	}

	public Long getTransitionIdOfEdge(Long edgeId) {
		return graphEdgeService.selectById(edgeId).getTransitionId();
	}

	public void deleteNode(Long nodeId) throws EntityNotDeletableException {
		// Select those navigation entries where this state is a nextState, so we get its parents
		List<GraphEdge> starts = graphEdgeService.selectByEndId(nodeId);
		// Select those navigation entries where this state is a parentState, so we get its nexStates
		List<GraphEdge> ends = graphEdgeService.selectByStartId(nodeId);

		StateGraph graph = graphNodeService.getGraphByNodeId(nodeId);

		// Create new entries with every parent and every nextState
		for (GraphEdge start : starts) {
			for (GraphEdge end : ends) {
				if (start.getStart()==end.getEnd()) {
					continue;
				}
				GraphEdge edge = new GraphEdge();
				edge.setEnd(end.getEnd());
				edge.setStart(start.getStart());
				edge.setTransitionId(start.getTransitionId());
				edge.setLabel(start.getLabel());
				edge.setGraph(graph);
				graph.getEdges().add(edge);

			}
		}

		// Delete old entries first
		for (GraphEdge start : starts) {
			graph.getEdges().remove(start);
			graphEdgeService.delete(start);
		}

		for (GraphEdge end : ends) {
			graph.getEdges().remove(end);
			graphEdgeService.delete(end);
		}

		// We can delete that state finally
		graphNodeService.deleteById(nodeId);
	}

	public void deleteEdge(Long edgeId) throws EntityNotDeletableException {
		graphEdgeService.deleteById(edgeId);
	}

	@Override
	protected Class<StateGraph> getEntityClass() {
		return StateGraph.class;
	}
}
