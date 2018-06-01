package use_cases;

import business.ApplicationException;
import business.CatalogRental;
import business.Rental;
import dataaccess.PersistenceException;

/**
 * Includes operations regarding  Use Case (UC1)
 *
 * @author ADS08
 */
public class HandlerProcessReturnRentedItems {

    private CatalogRental rentalCatalog;

    public HandlerProcessReturnRentedItems(CatalogRental rentalCatalog) {
        this.rentalCatalog = rentalCatalog;
    }

    public Rental getRental(int rentalId) throws ApplicationException {
        return rentalCatalog.getRental(rentalId);
    }

    public void setRentalAsReturned(Rental rental) throws ApplicationException {
        rentalCatalog.setRentalAsReturned(rental);
    }

    /**
     * Deletes rental
     * @param rental   The current rental
     * @throws ApplicationException
     */
    public void deleteRental(Rental rental) throws ApplicationException {
        rentalCatalog.deleteRental(rental);
    }

    public void returnProductFromRental(int prod_id, int qty) throws ApplicationException, PersistenceException {
        rentalCatalog.returnProductFromRental(prod_id, qty);
    }
}
