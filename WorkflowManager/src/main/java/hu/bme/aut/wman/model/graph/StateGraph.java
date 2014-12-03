package hu.bme.aut.wman.model.graph;

import hu.bme.aut.wman.model.AbstractEntity;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.validation.constraints.NotNull;

import org.codehaus.jackson.annotate.JsonManagedReference;

@SuppressWarnings("serial")
@Entity
@NamedQueries({
	@NamedQuery(name = "StateGraph.findByWorkflowId", query = "SELECT sg FROM StateGraph sg WHERE sg.workflowId=:id"),
})
public class StateGraph extends AbstractEntity {
	public static final String NQ_FIND_BY_WORKFLOW_ID = "StateGraph.findByWorkflowId";

	public static final String PR_WORKFLOW = "workflow";
	public static final String PR_POINTS = "points";
	public static final String PR_EDGES = "edges";

	@NotNull
	private Long workflowId;

	@OneToMany(mappedBy = "graph", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	@JsonManagedReference
	private List<GraphNode> points = new ArrayList<GraphNode>();

	@OneToMany(mappedBy = "graph", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	@JsonManagedReference
	private List<GraphEdge> edges = new ArrayList<GraphEdge>();

	public StateGraph() {};

	public StateGraph(Long workflowId) {
		this.setWorkflowId(workflowId);
	}

	public List<GraphNode> getPoints() {
		return points;
	}

	public void setPoints(List<GraphNode> points) {
		this.points = points;
	}

	public List<GraphEdge> getEdges() {
		return edges;
	}

	public void setEdges(List<GraphEdge> edges) {
		this.edges = edges;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((edges == null) ? 0 : edges.hashCode());
		result = prime * result + ((points == null) ? 0 : points.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!super.equals(obj)) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		StateGraph other = (StateGraph) obj;
		if (edges == null) {
			if (other.edges != null) {
				return false;
			}
		} else if (!edges.equals(other.edges)) {
			return false;
		}
		if (points == null) {
			if (other.points != null) {
				return false;
			}
		} else if (!points.equals(other.points)) {
			return false;
		}
		return true;
	}

	public Long getWorkflowId() {
		return workflowId;
	}

	public void setWorkflowId(Long workflowId) {
		this.workflowId = workflowId;
	}

}
