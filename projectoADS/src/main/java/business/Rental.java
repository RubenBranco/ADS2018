package business;

import java.util.*;

public class Rental {

    private int id;
    private Date date;
    private String status;
    private byte returnStatus;
    private List<RentalProduct> rentalProducts;

    public static final String OPEN   = "O";
    public static final String CLOSED = "C";
    public static final byte WAITING = 0;
    public static final byte RETURNED = 1;

    /**
     * Creates a new sale given the date it occurred and the customer that
     * made the purchase.
     *
     * @param date The date that the sale occurred
     */
    public Rental(int id, Date date) {
        this.id           = id;
        this.date         = date;
        this.status       = OPEN;
        this.rentalProducts = new LinkedList<RentalProduct>();
        this.returnStatus = WAITING;
    }

    public Date getDate() {
        return date;
    }

    public int getId() {
        return id;
    }

    public List<RentalProduct> getRentalProducts() {
        return rentalProducts;
    }
    /**
     * @return Whether the rent is open
     */
    public boolean isOpen() {
        return status.equals(OPEN);
    }

    public void close() {
        status = CLOSED;
    }

    public void open() {
        status = OPEN;
    }

    public void returnItems() {
        returnStatus = RETURNED;
    }

    public void unreturnItems() {
        returnStatus = WAITING;
    }

    public String getStatus() {
        return status;
    }

    public byte getReturnStatus() {
        return returnStatus;
    }

    /**
     * @return The rent's total
     */
    public double total() {
        double total = 0.0;
        for (RentalProduct sp : rentalProducts)
            total += sp.getSubTotal();
        return total;
    }

    /**
     * Adds a product to the sale
     *
     * @requires qty >= 0 (zero is useful for database tests)
     * @param product The product to rent
     * @param qty The amount of the product being rented
     * @throws ApplicationException
     */
    public void addProductToRental(ProductSpec product, int qty) {
        rentalProducts.add(new RentalProduct(product, qty));
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Rental @ " + date.toString() + "; " + (isOpen()?"open":"closed") + "; total of €" + total() + " with products:");
        for (RentalProduct sp : rentalProducts)
            sb.append(" [code " + sp.getProduct().getProductCode() + ", " + sp.getQty() + " units]");
        return sb.toString();
    }
}
