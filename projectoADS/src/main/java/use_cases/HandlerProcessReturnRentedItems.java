package use_cases;

import business.ApplicationException;
import business.CatalogRental;
import business.Rental;
import dataaccess.PersistenceException;

/**
 * Includes operations regarding Return Rented Items Use Case (UC2)
 *
 * @author ADS08
 */
public class HandlerProcessReturnRentedItems {

    private CatalogRental rentalCatalog;

    public HandlerProcessReturnRentedItems(CatalogRental rentalCatalog) {
        this.rentalCatalog = rentalCatalog;
    }

    /**
     * Gets an existing rental object with a given id
     *
     * @param rentalId the id of the rental object
     * @return A rental object
     * @throws ApplicationException
     */
    public Rental getRental(int rentalId) throws ApplicationException {
        return rentalCatalog.getRental(rentalId);
    }

    /**
     * Sets a an existing rental as having had its items returned (status)
     *
     * @param rental rental object
     * @throws ApplicationException
     */
    public void setRentalAsReturned(Rental rental) throws ApplicationException {
        rentalCatalog.setRentalAsReturned(rental);
    }

    /**
     * Deletes rental
     *
     * @param rental The current rental
     * @throws ApplicationException
     */
    public void deleteRental(Rental rental) throws ApplicationException {
        rentalCatalog.deleteRental(rental);
    }

    /**
     * Returns a single Product belonging to a rental
     *
     * @param prod_id id of the product
     * @param qty     quantity of said product
     * @throws ApplicationException
     * @throws PersistenceException
     */
    public void returnProductFromRental(int prod_id, int qty) throws ApplicationException, PersistenceException {
        rentalCatalog.returnProductFromRental(prod_id, qty);
    }
}
