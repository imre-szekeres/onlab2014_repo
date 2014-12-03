package hu.bme.aut.wman.model.graph;

import hu.bme.aut.wman.model.AbstractEntity;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.validation.constraints.NotNull;

import org.codehaus.jackson.annotate.JsonBackReference;

@SuppressWarnings("serial")
@Entity
@NamedQueries({
	@NamedQuery(name = "GraphEdge.findByStartId", query = "SELECT ge FROM GraphEdge ge WHERE ge.start.id=:startId"),
	@NamedQuery(name = "GraphEdge.findByEndId", query = "SELECT ge FROM GraphEdge ge WHERE ge.end.id=:endId")
})
public class GraphEdge extends AbstractEntity {
	public static final String NQ_FIND_BY_START_ID = "GraphEdge.findByStartId";
	public static final String NQ_FIND_BY_END_ID = "GraphEdge.findByEndId";

	public static final String PR_START = "start";
	public static final String PR_END = "end";
	public static final String PR_TRANSITION_ID = "transitionId";
	public static final String PR_LABEL = "label";

	@NotNull
	@ManyToOne
	private GraphNode start;

	@NotNull
	@ManyToOne
	private GraphNode end;

	@NotNull
	private Long transitionId;

	@NotNull
	private String label;

	@NotNull
	@ManyToOne
	@JsonBackReference
	private StateGraph graph;

	public GraphNode getStart() {
		return start;
	}

	public void setStart(GraphNode start) {
		this.start = start;
	}

	public GraphNode getEnd() {
		return end;
	}

	public void setEnd(GraphNode end) {
		this.end = end;
	}

	public Long getTransitionId() {
		return transitionId;
	}

	public void setTransitionId(Long transitionId) {
		this.transitionId = transitionId;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((end == null) ? 0 : end.hashCode());
		result = prime * result + ((label == null) ? 0 : label.hashCode());
		result = prime * result + ((start == null) ? 0 : start.hashCode());
		result = prime * result
				+ ((transitionId == null) ? 0 : transitionId.hashCode());
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
		GraphEdge other = (GraphEdge) obj;
		if (end == null) {
			if (other.end != null) {
				return false;
			}
		} else if (!end.equals(other.end)) {
			return false;
		}
		if (label == null) {
			if (other.label != null) {
				return false;
			}
		} else if (!label.equals(other.label)) {
			return false;
		}
		if (start == null) {
			if (other.start != null) {
				return false;
			}
		} else if (!start.equals(other.start)) {
			return false;
		}
		if (transitionId == null) {
			if (other.transitionId != null) {
				return false;
			}
		} else if (!transitionId.equals(other.transitionId)) {
			return false;
		}
		return true;
	}

	public StateGraph getGraph() {
		return graph;
	}

	public void setGraph(StateGraph graph) {
		this.graph = graph;
	}
}
