/**
 * EntityRepository.java
 */
package hu.bme.aut.wman.repositories;

import static java.lang.String.format;
import hu.bme.aut.wman.model.AbstractEntity;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author Imre Szekeres
 * @version "%I%, %G%"
 */
public class EntityRepository <K, E extends AbstractEntity> 
				implements Repository<K, E> {

	private static final AtomicLong SEQ = new AtomicLong();
	private final Map<K, E> repository;
	
	public EntityRepository() {
		repository = new HashMap<K, E>();
	}

	/**
	 * Upon insertion it sets the id field (of the <code>AbstractEntity</code> passed) to the value generated 
	 * by the <code>AtomicLong<code> instance.
	 * 
	 * @param key
	 * @param entity
	 * @return the entity inserted
	 * */
	@Override
	public synchronized E create(K key, E entity) {
		if (repository.containsKey( key ))
			throw new javax.persistence.EntityExistsException(format("Entity %s already exists.", entity));
		return insert(key, entity);
	}

	/**
	 * Insertion mechanism that places the <code>AbstractEntity</code> subclass to the repository and also 
	 * sets its id field.
	 * 
	 * @param key
	 * @param entity
	 * @return the entity stored
	 * */
	protected E insert(K key, E entity) {
		try {
			Field idField = entity.getClass().getSuperclass().getDeclaredField("id");
			idField.setAccessible( true );
			idField.set(entity, SEQ.incrementAndGet());
			idField.setAccessible( false );
		
			repository.put(key, entity);
			return entity;
		} catch(Exception e) {
			throw new RuntimeException(format("Unable to persist %s: %s", entity, e), e);
		}
	}

	/**
	 * Updates the given <code>AbstractEntity</code> corresponding to the given key. If the entity passed has a non-null id, then 
	 * resets the value, otherwise removes the previous one and inserts the new one. 
	 * 
	 * @param key
	 * @param entity
	 * @return the updated value
	 * */
	@Override
	public E update(K key, E entity) {
	    if (entity.getId() != null) {
	    	repository.put(key, entity);
	    	return entity;
	    }
	    repository.remove(key);
	    return insert(key, entity);
	}

	/**
	 * @see {@link Repository#read(Object)}
	 * */
	@Override
	public E read(K key) {
		return repository.get( key );
	}

	/**
	 * @see {@link Repository#remove(Object)}
	 * */
	@Override
	public E remove(K key) {
		return repository.remove( key );
	}
}
