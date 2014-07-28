/**
 * HistoryEntry.java
 * 
 * @author Imre Szekeres
 * */
package hu.bme.aut.tomeesample.model;

import java.io.Serializable;
import java.sql.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToOne;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * Entity implementation class for Entity: HistoryEntry
 * 
 */
@SuppressWarnings("serial")
@Entity
@NoArgsConstructor
@EqualsAndHashCode
@ToString
@Getter
@NamedQueries({
		@NamedQuery(name = "HistoryEntry.findAll", query = "SELECT he FROM HistoryEntry he"),
		@NamedQuery(name = "HistoryEntry.findById", query = "SELECT he FROM HistoryEntry he WHERE he.id=:id"),
		@NamedQuery(name = "HistoryEntry.findByUser", query = "SELECT he FROM HistoryEntry he WHERE he.userName=:userName"),
		@NamedQuery(name = "HistoryEntry.findByState", query = "SELECT he FROM HistoryEntry he WHERE he.state.id=:stateId"),
		@NamedQuery(name = "HistoryEntry.findByProject", query = "SELECT he FROM HistoryEntry he WHERE he.project.id= :projectId") })
public class HistoryEntry implements Serializable {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Setter
	@Size(min = 10, max = 512)
	private Date when;

	@Setter
	@NotNull
	@Size(min = 5, max = 16)
	private String userName;

	@Setter
	@NotNull
	@ManyToOne
	private State state;

	@Setter
	@NotNull
	@Size(min = 2, max = 20)
	private String actionTypeName;

	@Setter
	@NotNull
	@OneToOne
	private Project project;

}
