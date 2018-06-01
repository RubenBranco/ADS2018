package business;

import java.util.Date;
import java.util.List;

import dataaccess.*;
import javafx.application.Application;

/**
 * Includes operations regarding Rentals
 *
 * @author jpn, ADS08
 */
public class CatalogRental {
    /**
     * Creates a new rental, initially it open, and has a total of zero
     *
     * @return
     * @throws ApplicationException
     */
    public Rental newRental(Date returnDate) throws ApplicationException {

        try {
            int rental_id = RentalMapper.insert(new Date(), returnDate);  // create new entry in the database
            return RentalMapper.getRentalById(rental_id);
        } catch (PersistenceException e) {
            throw new ApplicationException("Unable to create new rental", e);
        }
    }

    /**
     * Add a product to an open rental
     *
     * @param rental  The current rental (must be open)
     * @param prod_id The product id to add (must exist)
     * @param qty     The quantity sold (must not be higher than the current stock)
     * @throws ApplicationException If some of these assumptions does not hold
     */
    public void addProductToRental(Rental rental, int prod_id, int qty)
            throws ApplicationException {

        if (!rental.isOpen())    // check if it's open
            throw new ApplicationException("Rental " + rental.getId() + " is already closed!");

        if (qty < 0)
            throw new ApplicationException("Negative amount (" + qty + " units of product " + prod_id + ") for rental" + rental.getId());

        if (qty > 1) {
            throw new ApplicationException("Cannot rent more than one item of the same itemID at the same time");
        }

        ProductSpec product;

        // check if product exists and the stock is enough, if so update stock
        try {
            product = ProductMapper.getProductByProdCod(prod_id);

            if (product.getStock() < qty)   // not enough units?
                throw new ApplicationException("Current stock is not enough to sell " + qty +
                        " units of product " + product.getId());

            // otherwise, update stock
            product.setStock(product.getStock() - qty);
            ProductMapper.updateStockValue(product.getId(), product.getStock());

        } catch (PersistenceException e) {
            throw new ApplicationException("Product " + prod_id + " does not exist!", e);
        }

        rental.addProductToRental(product, qty);  // add it to the object rental

        try {
            RentalProductMapper.insert(rental.getId(), product.getId(), qty);  // add it to the database
        } catch (PersistenceException e) {
            throw new ApplicationException("Unable to add " + product.getProductCode() +
                    " to sale id " + rental.getId(), e);
        }
    }

    /**
     * Return a product from a rental
     *
     * @param prod_id The product id to update
     * @param qty     quantity of the product
     * @throws ApplicationException
     */
    public void returnProductFromRental(int prod_id, int qty) throws ApplicationException {
        ProductSpec product;

        try {
            product = ProductMapper.getProductByProdCod(prod_id);
            product.setStock(product.getStock() + qty);
            ProductMapper.updateStockValue(product.getId(), product.getStock());
        } catch (PersistenceException e) {
            throw new ApplicationException("Product " + prod_id + " does not exist!", e);
        }
    }

    /**
     * Close rental, updating the total and its status.
     * If the rental was already closed, nothing happens.
     *
     * @param rental the rental to be closed
     * @throws ApplicationException
     */
    public void closeRental(Rental rental) throws ApplicationException {

        if (rental.isOpen()) {
            try {
                rental.close();
                RentalMapper.update(rental.getId(), rental.total(), rental.getStatus());
            } catch (PersistenceException e) {
                throw new ApplicationException("Unable to close " + rental.getId() +
                        ", or unable to find it", e);
            }
        }
    }

    /**
     * delete rental and its rental products
     *
     * @param rental the rental to be deleted
     * @throws ApplicationException
     */
    public void deleteRental(Rental rental) throws ApplicationException {
        try {
            RentalMapper.delete(rental.getId());
        } catch (PersistenceException e) {
            throw new ApplicationException("Unable to delete rental " + rental.getId(), e);
        }
    }

    /**
     * Gets an existing rental
     *
     * @param rental_id id from the existing rental
     * @return A rental object
     * @throws ApplicationException
     */
    public Rental getRental(int rental_id) throws ApplicationException {
        try {
            return RentalMapper.getRentalById(rental_id);
        } catch (PersistenceException e) {
            throw new ApplicationException("Unable to retrieve rental " + rental_id, e);
        }
    }

    /**
     * Setting an existing rental as returned, as in, its items have been returned
     *
     * @param rental is a rental object
     * @throws ApplicationException
     */
    public void setRentalAsReturned(Rental rental) throws ApplicationException {
        try {
            Byte rentalStatus = 1;
            RentalMapper.updateRentalStatus(rental.getId(), rentalStatus);
        } catch (PersistenceException e) {
            throw new ApplicationException("Unable to update rental " + rental.getId(), e);
        }
    }

    /**
     * Gets all existing rentals.
     *
     * @return a list of rentals.
     * @throws ApplicationException
     */
    public List<Rental> getAllRentals() throws ApplicationException {
        try {
            return RentalMapper.getAllRentals();
        } catch (PersistenceException e) {
            throw new ApplicationException("Unable to retrieve all rental.", e);
        }
    }

    /**
     * String representation of all rentals (attention: might produce a quite large output)
     */
    public String toString() {
        try {
            List<Rental> rentals = RentalMapper.getAllRentals();
            StringBuffer sb = new StringBuffer();

            for (Rental rental : rentals) {
                sb.append(rental);
                sb.append("\n");
            }

            return sb.toString();
        } catch (PersistenceException e) {
            System.out.println(e);
            return "N/A"; // something went wrong
        }
    }
}
