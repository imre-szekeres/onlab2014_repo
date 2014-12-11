package hu.bme.aut.wman.services.model;

import hu.bme.aut.wman.model.AbstractEntity;

import javax.persistence.Entity;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.validation.constraints.NotNull;

@Entity
@NamedQueries({
	@NamedQuery(name = "TestEntity.findByName", query = "SELECT te FROM TestEntity te WHERE te.name=:name")
})
public class TestEntity extends AbstractEntity {

	public static final String NQ_FIND_BY_NAME = "TestEntity.findByName";
	public static final String PR_NAME = "name";

	@NotNull
	private String name;

	public TestEntity() {
	}

	public TestEntity(String name) {
		this.setName(name);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((name == null) ? 0 : name.hashCode());
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
		TestEntity other = (TestEntity) obj;
		if (name == null) {
			if (other.name != null) {
				return false;
			}
		} else if (!name.equals(other.name)) {
			return false;
		}
		return true;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
