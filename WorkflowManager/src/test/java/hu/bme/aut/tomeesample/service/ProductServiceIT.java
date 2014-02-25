package hu.bme.aut.tomeesample.service;

import static org.junit.Assert.assertEquals;
import hu.bme.aut.tomeesample.model.Product;

import java.util.List;

import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class ProductServiceIT {
	
	private ProductService productService;
	private EntityTransaction tx;
	EntityManagerFactory emf = Persistence.createEntityManagerFactory("integrationTest");
	
	@Before
	public void setup(){
		productService = new ProductService();
		productService.em = emf.createEntityManager();
		tx = productService.em.getTransaction();
		tx.begin();
		// kezdeti DB tartalom létrehozása EntityManager műveletekkel 
		tx.commit();
	}
	
	@Test
	public void addProductIntoExistingCategory(){
		Product product = new Product("abc", 100.0);
		tx.begin();
		productService.createProduct(product);
		tx.commit();
		
		List<Product> products = productService.em.createQuery("SELECT p FROM Product p").getResultList();
		assertEquals(1, products.size());
		Product foundProduct = products.get(0);
		assertEquals(product.getName(), foundProduct.getName());
		assertEquals(product.getPrice(), foundProduct.getPrice(), 0.00001);		
	}
		
	
	@After
	public void destroy(){
		tx.begin();
		productService.em.createQuery("DELETE FROM Product").executeUpdate();
		productService.em.createQuery("DELETE FROM Category").executeUpdate();		
		tx.commit();
		productService.em.close();
		emf.close();
	}
	
}
