package hu.bme.aut.wman.model;


import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * Entity implementation class for Entity: HistoryEntry
 *
 * @version "%I%, %G%"
 */
@SuppressWarnings("serial")
@Entity
@NamedQueries({
	@NamedQuery(name = "HistoryEntry.findByUser", query = "SELECT he FROM HistoryEntry he WHERE he.userName=:userName"),
	@NamedQuery(name = "HistoryEntry.findByProject", query = "SELECT he FROM HistoryEntry he WHERE he.project.id= :id") })
public class HistoryEntry extends AbstractEntity {

	public static final String NQ_FIND_BY_USER_NAME = "HistoryEntry.findByUser";
	public static final String NQ_FIND_BY_PROJECT_ID = "HistoryEntry.findByProject";

	public static final String PR_USER_NAME = "userName";
	public static final String PR_WHEN = "when";
	public static final String PR_STATE = "state";
	public static final String PR_EVENT = "event";
	public static final String PR_PROJECT = "project";
	public static final String PR_MESSAGE = "message";

	@Size(min = 10, max = 512)
	private Date when;

	@NotNull
	@Size(min = 5, max = 16)
	private String userName;

	@NotNull
	private String state;

	@NotNull
	@Size(min = 2, max = 20)
	private HistoryEntryEventType event;

	@NotNull
	private String message;

	@NotNull
	@ManyToOne
	private Project project;

	public HistoryEntry() {
		super();
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


	@Override
	public boolean equals(Object o) {
		if (!(o instanceof HistoryEntry)) {
			return false;
		}
		return (((HistoryEntry) o).id).equals(id);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((project == null) ? 0 : project.hashCode());
		result = prime * result + ((getState() == null) ? 0 : getState().hashCode());
		result = prime * result + ((userName == null) ? 0 : userName.hashCode());
		result = prime * result + ((when == null) ? 0 : when.hashCode());
		return result;
	}

	public HistoryEntryEventType getEvent() {
		return event;
	}

	public void setEvent(HistoryEntryEventType event) {
		this.event = event;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

}
