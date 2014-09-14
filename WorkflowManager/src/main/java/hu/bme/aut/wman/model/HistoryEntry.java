package hu.bme.aut.wman.model;

import java.io.Serializable;
import java.sql.Date;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToOne;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * Entity implementation class for Entity: HistoryEntry
 */
@SuppressWarnings("serial")
@Entity
@NamedQueries({
		@NamedQuery(name = "HistoryEntry.findAll", query = "SELECT he FROM HistoryEntry he"),
		@NamedQuery(name = "HistoryEntry.findById", query = "SELECT he FROM HistoryEntry he WHERE he.id=:id"),
		@NamedQuery(name = "HistoryEntry.findByUser", query = "SELECT he FROM HistoryEntry he WHERE he.userName=:userName"),
		@NamedQuery(name = "HistoryEntry.findByState", query = "SELECT he FROM HistoryEntry he WHERE he.state.id=:stateId"),
		@NamedQuery(name = "HistoryEntry.findByProject", query = "SELECT he FROM HistoryEntry he WHERE he.project.id= :projectId") })
public class HistoryEntry extends AbstractEntity implements Serializable {

	@Size(min = 10, max = 512)
	private Date when;

	@NotNull
	@Size(min = 5, max = 16)
	private String userName;

	@NotNull
	@ManyToOne
	private State state;

	@NotNull
	@Size(min = 2, max = 20)
	private String actionTypeName;

	@NotNull
	@OneToOne
	private Project project;

	public String getUser() {
		return userName;
	}

	public void setUser(String user) {
		this.userName = user;
	}

	public Project getProject() {
		return project;
	}

	public void setProject(Project project) {
		this.project = project;
	}

	public Date getWhen() {
		return when;
	}

	public void setWhen(Date when) {
		this.when = when;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public State getStateName() {
		return state;
	}

	public void setStateName(State state) {
		this.state = state;
	}

	public String getActionTypeName() {
		return actionTypeName;
	}

	public void setActionTypeName(String actionTypeName) {
		this.actionTypeName = actionTypeName;
	}

	@Override
	public boolean equals(Object o) {
		if (!(o instanceof HistoryEntry)) {
			return false;
		}
		return (((HistoryEntry) o).id).equals(this.id);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((actionTypeName == null) ? 0 : actionTypeName.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((project == null) ? 0 : project.hashCode());
		result = prime * result + ((state == null) ? 0 : state.hashCode());
		result = prime * result + ((userName == null) ? 0 : userName.hashCode());
		result = prime * result + ((when == null) ? 0 : when.hashCode());
		return result;
	}

}
