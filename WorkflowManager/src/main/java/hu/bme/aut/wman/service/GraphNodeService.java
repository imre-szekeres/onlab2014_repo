package hu.bme.aut.wman.service;

import hu.bme.aut.wman.model.graph.GraphNode;
import hu.bme.aut.wman.model.graph.StateGraph;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;

/**
 * Helps make operations with <code>BlobFile</code>.
 *
 * @version "%I%, %G%"
 */
@Stateless
@LocalBean
public class GraphNodeService extends AbstractDataService<GraphNode> {

	private static final long serialVersionUID = 5995723106439036263L;

	public StateGraph getGraphByNodeId(Long nodeId) {
		return selectById(nodeId).getGraph();
	}

	public GraphNode selectByStateId(Long stateId) {
		List<Entry<String, Object>> parameterList = new ArrayList<Entry<String, Object>>();
		parameterList.add(new AbstractMap.SimpleEntry<String, Object>(GraphNode.PR_STATE_ID, stateId));
		return selectByParameters(parameterList).get(0);
	}

	@Override
	protected Class<GraphNode> getEntityClass() {
		return GraphNode.class;
	}
}
