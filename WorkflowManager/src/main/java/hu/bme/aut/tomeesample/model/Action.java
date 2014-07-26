/**
 * Action.java
 * 
 * @author Imre Szekeres
 * */
package hu.bme.aut.tomeesample.model;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * Entity implementation class for Entity: Action
 * 
 * @author Imre Szekeres
 * @version "%I%, %G%"
 */
@SuppressWarnings("serial")
@Entity
@NoArgsConstructor
@EqualsAndHashCode
@ToString
@NamedQueries({
		@NamedQuery(name = "Action.findAll", query = "SELECT a FROM Action a"),
		@NamedQuery(name = "Action.findById", query = "SELECT a FROM Action a WHERE a.id=:id"),
		@NamedQuery(name = "Action.findByType", query = "SELECT a FROM Action a WHERE a.actionType.id=:typeId")
})
public class Action implements Serializable {

	@Id
	@Getter
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Getter
	@Setter
	@NotNull
	@ManyToOne
	private ActionType actionType;

	@Getter
	@Setter
	@Size(min = 10, max = 512)
	private String description;

	public Action(ActionType actionType, String description) {
		this.actionType = actionType;
		this.description = description;
	}
}
