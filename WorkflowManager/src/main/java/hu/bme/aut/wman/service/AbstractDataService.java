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

public abstract class AbstractDataService<T extends AbstractEntity> {

	// TODO make EntityManager private, remove deprecated methods from services
	@PersistenceContext
	protected EntityManager em;
	private Class<T> clazz;

	protected void setClass(Class<T> clazz) {
		this.clazz = clazz;
	}

	public void save(T entity) {
		if (isNew(entity)) {
			em.persist(entity);
		} else {
			em.merge(entity);
		}
	}

	public T attach(T entity) {
		return em.merge(entity);
	}

	public void delete(T entity) {
		if (isDetached(entity)) {
			em.remove(entity);
		} else {
			T managedEntity = em.merge(entity);
			em.remove(managedEntity);
		}
	}

	public List<T> getAll() {
		CriteriaBuilder builder = em.getCriteriaBuilder();
		CriteriaQuery<T> buildedQuery = builder.createQuery(clazz);

		Root<T> root = buildedQuery.from(clazz);

		return em.createQuery(buildedQuery.select(root)).getResultList();
	}

	public T get(Long id) {
		ArrayList<Entry<String, Object>> parameterList = new ArrayList<Entry<String, Object>>();
		parameterList.add(new AbstractMap.SimpleEntry<String, Object>("id", id));
		Iterator<T> results = findByParameters(parameterList).iterator();

		if (results.hasNext()) {
			return results.next();
		} else {
			throw new NoSuchElementException("There is not entity with id: " + id + "in class: " + clazz.getSimpleName());
		}
	}

	/**
	 * Returns the result of the query.
	 * 
	 * @param parameters
	 *            of the query, connected with AND
	 * @return result list of the query
	 */
	public List<T> findByParameters(List<Entry<String, Object>> parameters) {
		CriteriaBuilder builder = em.getCriteriaBuilder();
		CriteriaQuery<T> buildedQuery = builder.createQuery(clazz);

		Root<T> root = buildedQuery.from(clazz);

		List<Predicate> predicates = new ArrayList<Predicate>();
		for (Entry<String, Object> parameter : parameters) {
			predicates.add(builder.equal(root.get(parameter.getKey()), parameter.getValue()));
		}

		buildedQuery.select(root).where(predicates.toArray(new Predicate[] {}));

		return em.createQuery(buildedQuery).getResultList();
	}

	public boolean isNew(T entity) {
		return entity.getId() == null;
	}

	public boolean isDetached(T entity) {
		return em.contains(entity);
	}
}
