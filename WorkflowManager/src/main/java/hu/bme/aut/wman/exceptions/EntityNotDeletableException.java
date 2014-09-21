package hu.bme.aut.wman.exceptions;

import hu.bme.aut.wman.model.AbstractEntity;

public class EntityNotDeletableException extends Exception {
	AbstractEntity entity;
	Class clazz;

	public EntityNotDeletableException(AbstractEntity entity, Class clazz) {
		this.entity = entity;
		this.clazz = clazz;
	}
}
