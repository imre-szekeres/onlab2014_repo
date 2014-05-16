package hu.bme.aut.tomeesample.service;

import hu.bme.aut.tomeesample.model.Product;

import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Stateless
public class ProductService {

	@PersistenceContext
	EntityManager em;

	@SuppressWarnings("unchecked")
	public List<Product> getAllProducts() {

		return em.createNamedQuery("Product.findAll").getResultList();
	}

	public void createProduct(Product p) {
		em.persist(p);
	}

	public void save(Product p) {
		em.merge(p);
	}

	public void delete(Product p) {
		em.remove(em.merge(p));
	}

}
