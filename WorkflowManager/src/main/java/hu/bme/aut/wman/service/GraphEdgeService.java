package hu.bme.aut.wman.service;

import hu.bme.aut.wman.model.graph.GraphEdge;

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
public class GraphEdgeService extends AbstractDataService<GraphEdge> {

	public List<GraphEdge> selectByStartId(Long id) {
		List<Entry<String, Object>> parameterList = new ArrayList<Entry<String, Object>>();
		parameterList.add(new AbstractMap.SimpleEntry<String, Object>("startId", id));
		return callNamedQuery(GraphEdge.NQ_FIND_BY_START_ID, parameterList);
	}

	public List<GraphEdge> selectByEndId(Long id) {
		List<Entry<String, Object>> parameterList = new ArrayList<Entry<String, Object>>();
		parameterList.add(new AbstractMap.SimpleEntry<String, Object>("endId", id));
		return callNamedQuery(GraphEdge.NQ_FIND_BY_END_ID, parameterList);
	}

	@Override
	protected Class<GraphEdge> getEntityClass() {
		return GraphEdge.class;
	}
}
