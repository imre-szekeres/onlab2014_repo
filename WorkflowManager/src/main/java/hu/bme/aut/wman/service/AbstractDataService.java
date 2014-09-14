package hu.bme.aut.wman.service;

import hu.bme.aut.wman.model.AbstractEntity;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.NoSuchElementException;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

/**
 * Abstract class, which implements and helps with the basic CRUD operations and queries.
 * 
 * @version "%I%, %G%"
 * 
 * @param <T>
 *            The Entity to use in the operations
 */
public abstract class AbstractDataService<T extends AbstractEntity> {

	// TODO make EntityManager private, remove deprecated methods from services
	@PersistenceContext
	protected EntityManager em;

	/**
	 * Saves the given entity. Persist if it is a new entity, merge if it is not.
	 * 
	 * @param entity
	 *            to save
	 */
	public void save(T entity) {
		if (isNew(entity)) {
			em.persist(entity);
		} else {
			em.merge(entity);
		}
	}

	/**
	 * Attaches an detached entity to the persistence context.
	 * 
	 * @param entity
	 *            to attach
	 * @return the attached entity
	 */
	public T attach(T entity) {
		return em.merge(entity);
	}

	/**
	 * Deletes the entity from the database.
	 * 
	 * @param entity
	 *            to delete
	 */
	public void delete(T entity) {
		if (isDetached(entity)) {
			em.remove(entity);
		} else {
			T managedEntity = em.merge(entity);
			em.remove(managedEntity);
		}
	}

	/**
	 * Returns all of the entities, which represented by the Entity class.
	 * 
	 * @return list of the result
	 */
	public List<T> selectAll() {
		CriteriaBuilder builder = em.getCriteriaBuilder();
		CriteriaQuery<T> buildedQuery = builder.createQuery(getEntityClass());

		Root<T> root = buildedQuery.from(getEntityClass());

		return em.createQuery(buildedQuery.select(root)).getResultList();
	}

	/**
	 * Returns the Entity which has the specified ID.
	 * 
	 * @param id
	 *            of the entity
	 * @return the result entity
	 */
	public T selectById(Long id) {
		ArrayList<Entry<String, Object>> parameterList = new ArrayList<Entry<String, Object>>();
		parameterList.add(new AbstractMap.SimpleEntry<String, Object>("id", id));
		Iterator<T> results = selectByParameters(parameterList).iterator();

		if (results.hasNext()) {
			return results.next();
		} else {
			throw new NoSuchElementException("There is not entity with id: " + id + "in class: " + getEntityClass().getSimpleName());
		}
	}

	/**
	 * Returns the result list of the query. The parameters will connected with <b>AND</b> in the query's WHERE part.
	 * 
	 * @param parameters
	 *            of the query, connected with AND
	 * @return result list of the query
	 */
	public List<T> selectByParameters(List<Entry<String, Object>> parameters) {
		CriteriaBuilder builder = em.getCriteriaBuilder();
		CriteriaQuery<T> buildedQuery = builder.createQuery(getEntityClass());

		Root<T> root = buildedQuery.from(getEntityClass());

		List<Predicate> predicates = new ArrayList<Predicate>();
		for (Entry<String, Object> parameter : parameters) {
			predicates.add(builder.equal(root.get(parameter.getKey()), parameter.getValue()));
		}

		buildedQuery.select(root).where(predicates.toArray(new Predicate[] {}));

		return em.createQuery(buildedQuery).getResultList();
	}

	/**
	 * @param entity
	 *            to examine
	 * @return if the entity is new or was persisted before.
	 */
	public boolean isNew(T entity) {
		return entity.getId() == null;
	}

	/**
	 * 
	 * @param entity
	 *            to examine
	 * @return if the entity is detached from the persistence context or not.
	 */
	public boolean isDetached(T entity) {
		return !em.contains(entity);
	}

	protected abstract Class<T> getEntityClass();
}
