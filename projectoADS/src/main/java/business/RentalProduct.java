package business;

/**
 * Includes operations regarding Rentals
 *
 * @author ADS08
 */
public class RentalProduct {

    private int id;
    private ProductSpec product;
    private int qty;

    /**
     * Creates a product that is part of a rental. The qty is the quantity of items in the rental.
     *
     * @param produto The product to be associated with the rental.
     * @param qty     The number of products sold.
     */
    public RentalProduct(ProductSpec produto, int qty) {
        this.product = produto;
        this.qty = qty;
    }

    /**
     * @return The product of the product rental
     */
    public ProductSpec getProduct() {
        return product;
    }

    /**
     * @return The quantity of the product rental
     */
    public double getQty() {
        return qty;
    }

    /**
     * @return The sub total of the product rental
     */
    public double getSubTotal() {
        return qty * product.getPrice() * 0.20;
    }

    /**
     * @return An int with the id of the rentalproduct
     */
    public int getId() {
        return id;
    }

    /**
     * Sets the id of the RentalProduct object.
     *
     * @param id is an int indicating the new id
     */
    public void setId(int id) {
        this.id = id;
    }

}
