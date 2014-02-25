package hu.bme.aut.tomeesample.service;

import static org.mockito.Mockito.*;
import hu.bme.aut.tomeesample.model.Product;

import javax.persistence.EntityManager;

import org.junit.Before;
import org.junit.Test;

public class ProductServiceTest {

	
	ProductService productService = new ProductService();	
	
	@Before
	public void setup(){
		productService.em = mock(EntityManager.class);
	}
	
	@Test
	public void createProduct(){		
		Product product = new Product("abc", 100.0);
		productService.createProduct(product);
		
		verify(productService.em, times(1)).persist(product);		
	}
	
	
}
