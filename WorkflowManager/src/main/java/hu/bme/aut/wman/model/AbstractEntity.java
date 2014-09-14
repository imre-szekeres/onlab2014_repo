package hu.bme.aut.wman.model;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

/**
 * An abstract Entity to use the AbstractDataService, every entity should extends this!
 * 
 * @version "%I%, %G%"
 */
@SuppressWarnings("serial")
@Entity
public abstract class AbstractEntity implements Serializable {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	protected Long id;

	public Long getId() {
		return id;
	}

}
