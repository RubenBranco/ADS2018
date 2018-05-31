package use_cases;

import business.ApplicationException;
import business.CatalogRental;
import business.Rental;

public class HandlerProcessRental {

    private CatalogRental rentalCatalog;

    /**
     * Creates a handler for the process rental use case given
     * the rental, and product catalogs which contain the relevant
     * operators to execute this use case methods
     *
     * @param rentalCatalog    A rental's catalog
     */
    public HandlerProcessRental(CatalogRental rentalCatalog) {
        this.rentalCatalog = rentalCatalog;
    }

    /**
     * Creates a new rental
     *
     * @throws ApplicationException In case the rental fails to be created
     */
    public Rental newRental() throws ApplicationException {
        return rentalCatalog.newRental();
    }

    /**
     * Adds a product to a rental
     *
     * @param rental      The current rental
     * @param prod_code The product id to be added to the rental
     * @param qty       The quantity of the product sold
     * @throws ApplicationException When the rental is closed, the product code
     * is not part of the product's catalog, or when there is not enough stock
     * to proceed with the rental
     */
    public void addProductToRental (Rental rental, int prod_code, int qty) throws ApplicationException {
        rentalCatalog.addProductToRental(rental, prod_code, qty);
    }

    /**
     * Closes an open rental
     * @param rental   The current rental
     * @throws ApplicationException
     */
    public void closeRental(Rental rental) throws ApplicationException {
        rentalCatalog.closeRental(rental);
    }

    /**
     * Deletes rental
     * @param rental   The current rental
     * @throws ApplicationException
     */
    public void deleteRental(Rental rental) throws ApplicationException {
        rentalCatalog.deleteRental(rental);
    }
}
