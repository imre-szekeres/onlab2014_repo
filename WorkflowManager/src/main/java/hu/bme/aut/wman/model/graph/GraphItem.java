package hu.bme.aut.wman.model.graph;

import hu.bme.aut.wman.model.AbstractEntity;

import javax.persistence.MappedSuperclass;
import javax.validation.constraints.NotNull;

@SuppressWarnings("serial")
@MappedSuperclass
public class GraphItem extends AbstractEntity {
	public static final String PR_X = "x";
	public static final String PR_Y = "y";

	@NotNull
	protected Integer x;

	@NotNull
	protected Integer y;

	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + x;
		result = prime * result + y;
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
		GraphItem other = (GraphItem) obj;
		if (x != other.x) {
			return false;
		}
		if (y != other.y) {
			return false;
		}
		return true;
	}
}
