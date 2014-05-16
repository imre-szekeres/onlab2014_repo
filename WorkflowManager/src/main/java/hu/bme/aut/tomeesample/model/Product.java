package hu.bme.aut.tomeesample.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.validation.constraints.Min;

@Entity()
@NamedQueries({
	@NamedQuery(name="Product.findAll", query="SELECT p FROM Product p")
})
public class Product {
	
	@Id
	@GeneratedValue
	private Long id;
	
	private String name;
	
	@Min(value=50)
	private double price;
		
	public Product() {
	}

	public Product(String name, double price) {
		super();
		this.name = name;
		this.price = price;
	}
	
	public Long getId() {
		return id;
	}


	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public double getPrice() {
		return price;
	}

	public void setPrice(double price) {
		this.price = price;
	}		

}
