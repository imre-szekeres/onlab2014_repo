package hu.bme.aut.wman.service;

import hu.bme.aut.wman.exceptions.EntityNotFoundException;
import hu.bme.aut.wman.exceptions.TooMuchElementException;
import hu.bme.aut.wman.model.AbstractEntity;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.NoSuchElementException;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
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
		CriteriaQuery<T> buildedCriteriaQuery = builder.createQuery(getEntityClass());

		Root<T> root = buildedCriteriaQuery.from(getEntityClass());

		return em.createQuery(buildedCriteriaQuery.select(root)).getResultList();
	}

	/**
	 * Returns the Entity which has the specified ID.
	 * 
	 * @param id
	 *            of the entity
	 * @return the result entity
	 * @throws EntityNotFoundException
	 */
	public T selectById(Long id) throws EntityNotFoundException {
		ArrayList<Entry<String, Object>> parameterList = new ArrayList<Entry<String, Object>>();
		parameterList.add(new AbstractMap.SimpleEntry<String, Object>(AbstractEntity.PR_ID, id));
		Iterator<T> results = selectByOwnProperties(parameterList).iterator();

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
	 * @return result list of the executed query
	 * @throws EntityNotFoundException
	 */
	public List<T> selectByOwnProperties(List<Entry<String, Object>> parameters) throws EntityNotFoundException {
		CriteriaQuery<T> buildedCriteriaQuery = buildCriteriaByParameters(parameters);

		return checkHasAnyResult(em.createQuery(buildedCriteriaQuery).getResultList());
	}

	/**
	 * Support calling named queries. You can find the named query's name in the entity classes
	 * 
	 * @param queryName
	 *            the name of the query in the specific entity class
	 * @param parameters
	 *            of the query
	 * @return result list of the executed query
	 * @throws EntityNotFoundException
	 */
	protected List<T> callNamedQuery(String queryName, List<Entry<String, Object>> parameters) throws EntityNotFoundException {
		TypedQuery<T> namedQuery = em.createNamedQuery(queryName, getEntityClass());
		for (Entry<String, Object> entry : parameters) {
			namedQuery.setParameter(entry.getKey(), entry.getValue());
		}

		return checkHasAnyResult(namedQuery.getResultList());
	}

	/**
	 * @param parameters
	 * @return a CriteriaQuery builded from the parameters
	 */
	private CriteriaQuery<T> buildCriteriaByParameters(List<Entry<String, Object>> parameters) {
		CriteriaBuilder builder = em.getCriteriaBuilder();
		CriteriaQuery<T> buildedCriteriaQuery = builder.createQuery(getEntityClass());

		Root<T> root = buildedCriteriaQuery.from(getEntityClass());

		List<Predicate> predicates = new ArrayList<Predicate>();
		for (Entry<String, Object> parameter : parameters) {
			predicates.add(builder.equal(root.get(parameter.getKey()), parameter.getValue()));
		}

		return buildedCriteriaQuery.select(root).where(predicates.toArray(new Predicate[] {}));
	}

	/**
	 * @throws TooMuchElementException
	 *             if the list has more than one element
	 * @param listToCheck
	 *            the list to check if has exactly one element.
	 * @return the same list it gets to support chaining
	 */
	protected List<T> checkHasExactlyOneElement(List<T> listToCheck) throws TooMuchElementException {
		int size = listToCheck.size();
		if (size > 1) {
			throw new TooMuchElementException("This query should return exactly one entity, but it returned " + size + "entitise.", size);
		} else {
			return listToCheck;
		}
	}

	/**
	 * @throws EntityNotFoundException
	 *             (not runtime)
	 *             if the list has not any element
	 * @param listToCheck
	 *            the list to check if has any element.
	 * @return the same list it gets to support chaining
	 */
	protected List<T> checkHasAnyResult(List<T> listToCheck) throws EntityNotFoundException {
		if (listToCheck.size() == 0) {
			throw new EntityNotFoundException();
		} else {
			return listToCheck;
		}
	}

	/**
	 * Gives back the CriteriaBuilder to the descendant classes to make more complex queries.
	 * 
	 * @return CriteriaBuilder
	 */
	protected CriteriaBuilder getCriteriaBuilder() {
		return em.getCriteriaBuilder();
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
