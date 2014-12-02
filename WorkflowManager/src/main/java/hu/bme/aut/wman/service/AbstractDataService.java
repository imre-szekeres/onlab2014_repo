package hu.bme.aut.wman.service;

import hu.bme.aut.wman.exceptions.EntityNotDeletableException;
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

	@PersistenceContext
	private EntityManager em;

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
	 * @throws EntityNotDeletableException
	 */
	public void delete(T entity) throws EntityNotDeletableException {
		// TODO: investigate why it is not working... But it is not so importent now
		// if (isDetached(entity)) {
		// em.remove(entity);
		// } else {
		T managedEntity = em.merge(entity);
		em.remove(managedEntity);
		// }
	}

	/**
	 * Deletes the entity from the database by its ID.
	 *
	 * @param id
	 *            of the entity to delete
	 * @throws EntityNotDeletableException
	 */
	public void deleteById(Long id) throws EntityNotDeletableException {
		delete(selectById(id));
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
	 */
	public T selectById(long id) {
		ArrayList<Entry<String, Object>> parameterList = new ArrayList<Entry<String, Object>>();
		parameterList.add(new AbstractMap.SimpleEntry<String, Object>(AbstractEntity.PR_ID, id));
		Iterator<T> results = selectByParameters(parameterList).iterator();

		if (results.hasNext())
			return results.next();
		else
			throw new NoSuchElementException("There is not entity with id: " + id + "in class: " + getEntityClass().getSimpleName());
	}

	/**
	 * Returns the result list of the query. The parameters will connected with <b>AND</b> in the query's WHERE part.
	 *
	 * @param parameters
	 *            of the query, connected with AND
	 * @return result list of the executed query
	 */
	public List<T> selectByParameters(List<Entry<String, Object>> parameters) {
		CriteriaQuery<T> buildedCriteriaQuery = buildCriteriaByParameters(parameters);

		return em.createQuery(buildedCriteriaQuery).getResultList();
	}

	/**
	 * Support calling named queries. You can find the named query's name in the entity classes
	 *
	 * @param queryName
	 *            the name of the query in the specific entity class
	 * @param parameters
	 *            of the query
	 * @return result list of the executed query
	 */
	protected List<T> callNamedQuery(String queryName, List<Entry<String, Object>> parameters) {
		TypedQuery<T> namedQuery = em.createNamedQuery(queryName, getEntityClass());
		for (Entry<String, Object> entry : parameters) {
			namedQuery.setParameter(entry.getKey(), entry.getValue());
		}
		return namedQuery.getResultList();
	}

	/**
	 * Support calling <code>NamedQuery</code>s in a more flexible manner, for all kinds
	 * of types of results.
	 * 
	 * @param queryName
	 * @param parameters
	 * @param cls the expected type of result(s)
	 * 
	 * @return the list of results
	 * @see {@link AbstractDataService#callNamedQuery(String, List)}
	 * */
	protected <E> List<E> callNamedQuery(String queryName, List<Entry<String, Object>> parameters, Class<E> cls) {
		TypedQuery<E> namedQuery = em.createNamedQuery(queryName, cls);
		for (Entry<String, Object> entry : parameters) {
			namedQuery.setParameter(entry.getKey(), entry.getValue());
		}
		return namedQuery.getResultList();
	}

	/**
	 * Supports execution of non-SELECT <code>NamedQuery</code>s using the parameter <code>List</code> passed
	 * as argument.
	 *
	 * @param queryName
	 *            the name of the query in the specific entity class
	 * @param parameters
	 *          of the query
	 * @return the number of rows affected
	 * 
	 * @see {@link javax.persistence.Query#executeUpdate()}
	 * @see {@link javax.persistence.TypedQuery#executeUpdate()}
	 * @see {@link AbstractDataService#callNamedQuery(String, List)}
	 */
	protected int executeNamedQuery(String queryName, List<Entry<String, Object>> parameters) {
		TypedQuery<T> namedQuery = em.createNamedQuery(queryName, getEntityClass());
		for (Entry<String, Object> entry : parameters) {
			namedQuery.setParameter(entry.getKey(), entry.getValue());
		}
		return namedQuery.executeUpdate();
	}

	
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
	 * @param entity
	 *            to examine
	 * @return if the entity is new or was persisted before.
	 */
	public boolean isNew(T entity) {
		return entity.getId() == null && 
				(em.find(entity.getClass(), entity.getId()) == null);
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
