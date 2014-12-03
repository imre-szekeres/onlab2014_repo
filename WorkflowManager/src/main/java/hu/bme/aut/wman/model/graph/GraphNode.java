package hu.bme.aut.wman.model.graph;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;

import org.codehaus.jackson.annotate.JsonBackReference;

@SuppressWarnings("serial")
@Entity
public class GraphNode extends GraphItem {
	public static final String PR_STATE_ID = "stateId";
	public static final String PR_LABEL = "label";
	public static final String PR_CONTENT = "content";
	public static final String PR_INITIAL = "initial";

	@NotNull
	private Long stateId;

	@NotNull
	private String label;

	private String content;

	private boolean initial;

	@NotNull
	@ManyToOne
	@JsonBackReference
	private StateGraph graph;

	public Long getStateId() {
		return stateId;
	}

	public void setStateId(Long stateId) {
		this.stateId = stateId;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((stateId == null) ? 0 : stateId.hashCode());
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
		GraphNode other = (GraphNode) obj;
		if (stateId == null) {
			if (other.stateId != null) {
				return false;
			}
		} else if (!stateId.equals(other.stateId)) {
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

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public boolean isInitial() {
		return initial;
	}

	public void setInitial(boolean initial) {
		this.initial = initial;
	}
}
