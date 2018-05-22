package business;

public class ProductSpec {

	private int    id;          // the database's id
	private int    itemID;      // the product's code
	private String description;
	private double price;       // value per unit
	private double qty;         // units in stock
	
	/**
	 * Creates a new product given its code, description, face value, 
	 * stock quantity, if it is eligible for discount, and its units.
	 * 
	 * @param code        The product code
	 * @param description The product description
	 * @param price       The value by which the product should be sold
	 * @param qty         The number of units available in stock
	 */
	public ProductSpec(int id, int code, String description, double price, double qty) {
		this.id          = id;
		this.itemID      = code;
		this.description = description;
		this.price       = price;
		this.qty         = qty;
	}
	
	public int getId() {
		return id;
	}
	
	/**
	 * Comment: there is a business rule to not allow product code changes.
	 * That is why there is no method for updating the product code.
	 * @return The code of the product.
	 */
	public int getProductCode() {
		return itemID;
	}

	/**
	 * @return The product's description
	 */
	public String getDescription() {
		return description;
	}
	
	/**
	 * @return The product's price
	 */
	public double getPrice() {
		return price;
	}

	/**
	 * @return The product's quantity
	 */
	public double getStock() {
		return qty;
	}

	/**
	 * Updates the product's stock quantity
	 * @param qty The new stock quantity
	 */
	public void setStock(double qty) {
		this.qty = qty;
	}
	
	public String toString() {
		return description + " [code " + itemID + " with unit price €" + price + " and stock " + qty + "]"; 
	}
}
