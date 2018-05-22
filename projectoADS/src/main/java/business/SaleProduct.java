package business;

public class SaleProduct {
	
	private int         id;
	private ProductSpec product;
	private double      qty;
	
	/**
	 * Creates a product that is part of a sale. The qty is the quantity of items in the sale. 
	 * 
	 * @param product The product to be associated with the sale.
	 * @param qty     The number of products sold.
	 */
	public SaleProduct (ProductSpec produto, double qty) {
		this.product = produto;
		this.qty     = qty;
	}

	/**
	 * @return The product of the product sale
	 */
	public ProductSpec getProduct() {
		return product;
	}

	/**
	 * @return The quantity of the product sale
	 */
	public double getQty() {
		return qty;
	}

	/**
	 * @return The sub total of the product sale
	 */
	public double getSubTotal() {
		return qty * product.getPrice();
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
}