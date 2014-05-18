package hu.bme.aut.tomeesample.web;

import hu.bme.aut.tomeesample.model.Product;
import hu.bme.aut.tomeesample.service.ProductService;

import java.util.List;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;

@Named
@RequestScoped
public class ProductManager {

	@Inject
	ProductService productService;

	private Product newProduct = new Product();

	private List<Product> allProducts;

	public Product getNewProduct() {
		return newProduct;
	}

	public List<Product> getAllProducts() {
		if (allProducts == null) {
			allProducts = productService.getAllProducts();
		}

		return allProducts;
	}

	public String addProduct() {
		productService.save(newProduct);
		return "/auth/products.xhtml";
	}

}
