package hu.bme.aut.tomeesample.web;

import static org.mockito.Mockito.*;
import static org.junit.Assert.*;
import java.util.Arrays;
import java.util.List;

import hu.bme.aut.tomeesample.model.Product;
import hu.bme.aut.tomeesample.service.ProductService;
import hu.bme.aut.tomeesample.web.ProductManager;
import org.junit.Before;
import org.junit.Test;


public class ProductManagerTest {
	
	ProductManager productManager = new ProductManager();	
	
	@Before
	public void setup(){
		productManager.productService = mock(ProductService.class);
	}
	
	@Test
	public void getAllProducts(){						
		List<Product> products = Arrays.asList(new Product[]{new Product("abc", 100.0), new Product("def", 200.0)});
		
		when(productManager.productService.getAllProducts()).thenReturn(products);
		List<Product> returnedProducts = productManager.getAllProducts();
		
		assertEquals(products, returnedProducts);		
	}
	
	
}
