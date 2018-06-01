package business;

import java.util.*;

/**
 * Defines a rental
 *
 * @author ADS08
 */
public class Rental {

    private int id;
    private Date date;
    private Date returnDate;
    private String status;
    private byte returnStatus;
    private List<RentalProduct> rentalProducts;

    public static final String OPEN = "O";
    public static final String CLOSED = "C";
    public static final byte WAITING = 0;
    public static final byte RETURNED = 1;

    /**
     * Creates a new rental given the date it occurred and the customer that
     * made the rental.
     *
     * @param date The date that the rental occurred
     */
    public Rental(int id, Date date, Date returnDate) {
        this.id = id;
        this.date = date;
        this.returnDate = returnDate;
        this.status = OPEN;
        this.rentalProducts = new LinkedList<RentalProduct>();
        this.returnStatus = WAITING;
    }

    /**
     * @return Date object belonging to the rental
     */
    public Date getDate() {
        return date;
    }

    /**
     * @return Date object in which the rental items must be returned
     */
    public Date getReturnDate() {
        return returnDate;
    }

    /**
     * @return The id from the rental object
     */
    public int getId() {
        return id;
    }

    /**
     * Gets all the rental products from the rental object
     *
     * @return A list of RentalProducts
     */
    public List<RentalProduct> getRentalProducts() {
        return rentalProducts;
    }

    /**
     * @return Whether the rent is open
     */
    public boolean isOpen() {
        return status.equals(OPEN);
    }

    /**
     * @return Whether the items from a rental have been returned
     */
    public boolean isReturned() {
        return returnStatus == RETURNED;
    }

    /**
     * Closes the rental
     */
    public void close() {
        status = CLOSED;
    }

    /**
     * Opens the rental
     */
    public void open() {
        status = OPEN;
    }

    /**
     * Sets the rental items as having been returned fully
     */
    public void returnItems() {
        returnStatus = RETURNED;
    }

    /**
     * Sets the rental items as not having been returned
     */
    public void unreturnItems() {
        returnStatus = WAITING;
    }

    /**
     * @return A string indicating the status of the rental
     */
    public String getStatus() {
        return status;
    }

    /**
     * @return A byte indicating the return status of the rental; 1 - Returned, 0 - Unreturned
     */
    public byte getReturnStatus() {
        return returnStatus;
    }

    /**
     * @return The rental's total
     */
    public double total() {
        double total = 0.0;
        for (RentalProduct rp : rentalProducts)
            total += rp.getSubTotal();
        return total;
    }

    /**
     * @return The rental's penalty
     */
    public double penalty(Date now) {
        double total = 0.0;
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(returnDate);
        calendar.add(Calendar.DAY_OF_YEAR, 7);

        Date hardLimit = calendar.getTime();

        if (now.after(returnDate)) {
            for (RentalProduct rp : rentalProducts) {
                if (now.after(hardLimit)) {
                    ProductSpec product = rp.getProduct();
                    total += product.getPrice() * rp.getQty() - rp.getSubTotal();
                } else {
                    total += rp.getSubTotal() * 0.5;
                }
            }
        }
        return total;
    }

    /**
     * Adds a product to the rental
     *
     * @param product The product to rent
     * @param qty     The amount of the product being rented
     * @throws ApplicationException
     * @requires qty >= 0 (zero is useful for database tests)
     */
    public void addProductToRental(ProductSpec product, int qty) {
        rentalProducts.add(new RentalProduct(product, qty));
    }

    /**
     * @return A string with the representation of the rental.
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Rental @ " + date.toString() + "; " + (isOpen() ? "open" : "closed") + "; " + (isReturned() ? "returned" : "unreturned") + "; total of €" + total() + " with products:");
        for (RentalProduct rp : rentalProducts)
            sb.append(" [code " + rp.getProduct().getProductCode() + ", " + rp.getQty() + " units]");
        sb.append(" Return date is " + returnDate.toString());
        return sb.toString();
    }
}
