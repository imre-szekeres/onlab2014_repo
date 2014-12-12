/**
 * OrderedEntityRepository.java
 */
package hu.bme.aut.wman.repositories;

import static java.lang.String.format;
import hu.bme.aut.wman.model.AbstractEntity;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

/**
 * It acts as a bucket-hash, it can contain multiple values corresponding to one kay.
 * 
 * @author Imre Szekeres
 * @version "%I%, %G%"
 */
public abstract class MultiValuedEntityRepository <K, E extends AbstractEntity> 
				implements Repository<K, E> {
	
	private static final AtomicLong SEQ = new AtomicLong();
	private final Map<K, List<E>> repository;


	public MultiValuedEntityRepository() {
		repository = new HashMap<K, List<E>>();
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
		
			List<E> entities = entitiesOf( key );
			entities.add(entity);
			return entity;
		} catch(Exception e) {
			throw new RuntimeException(format("Unable to persist %s: %s", entity, e), e);
		}
	}

	/**
	 * 
	 * */
	protected abstract List<E> newList();

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
	    	List<E> entities = entitiesOf( key );
	    	if (entities.contains( entity ))
	    		entities.remove( entity );
	    	entities.add( entity );
	    	return entity;
	    }
	    repository.remove(key);
	    return insert(key, entity);
	}

	/**
	 * Use {@link OrderedEntityRepository#readAll(Object)} instead!
	 * 
	 * @throws {@link RuntimeException}
	 * */
	@Override
	public E read(K key) {
		throw new RuntimeException("Unsafe operation");
	}

	/**
	 * @see {@link Repository#read(Object)}
	 * */
	public List<E> readAll(K key) {
		return entitiesOf( key );
	}

	/**
	 * Removes the given entity from the <code>List</code> corresponding to the key specified.
	 * 
	 * @param key
	 * @param entity
	 * @return the entity removed
	 * */
	public E remove(K key, E entity) {
		List<E> entities = entitiesOf( key );
		entities.remove( entity );
		return entity;
	}

	/**
	 * Removes all entries corresponding form the given key.
	 * 
	 * @param key
	 * @return null
	 * */
	@Override
	public E remove(K key) {
		repository.remove(key);
		return null;
	}

	/**
	 * Retrieves the <code>List</code> of entities corresponding to the given key.
	 * 
	 * @param key
	 * @return the list of entities
	 * */
	private List<E> entitiesOf(K key) {
		List<E> entities = repository.get(key);
		if (entities == null) {
			entities = newList();
			repository.put(key, entities);
		}
		return entities;
	}
}
