/**
 * Action.java
 * 
 * @author Imre Szekeres
 * */
package hu.bme.aut.tomeesample.model;

import java.io.Serializable;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.validation.constraints.NotNull;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * @author Gergely Varkonyi
 */
@SuppressWarnings("serial")
@Entity
@NoArgsConstructor
@EqualsAndHashCode
@ToString
@NamedQueries({
		@NamedQuery(name = "StateNavigationEntry.findAll", query = "SELECT sne FROM StateNavigationEntry sne"),
		@NamedQuery(name = "StateNavigationEntry.findById", query = "SELECT sne FROM StateNavigationEntry sne "
				+ "WHERE sne.id=:id"),
		@NamedQuery(name = "StateNavigationEntry.findByParentId", query = "SELECT sne FROM StateNavigationEntry sne "
				+ "WHERE sne.parent.id=:parentId"),
		@NamedQuery(name = "StateNavigationEntry.findByActionType", query = "SELECT sne FROM StateNavigationEntry sne "
				+ "WHERE sne.actionType.id=:typeId AND sne.parent.id=:parentId")
})
public class StateNavigationEntry implements Serializable {

	@Id
	@Getter
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Getter
	@Setter
	@ManyToOne(cascade = CascadeType.MERGE)
	@JoinColumn
	private State parent;

	@Getter
	@Setter
	@Column
	@ManyToOne(fetch = FetchType.EAGER)
	@NotNull
	private State nextState;

	@Getter
	@Setter
	@Column
	@ManyToOne(fetch = FetchType.EAGER)
	@NotNull
	private ActionType actionType;

	public StateNavigationEntry(ActionType actionType, State nextState, State parent) {
		this.actionType = actionType;
		this.nextState = nextState;
		this.parent = parent;
	}
}
