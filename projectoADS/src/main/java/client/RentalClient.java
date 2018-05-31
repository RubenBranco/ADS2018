package client;

import business.ApplicationException;
import business.CatalogRental;
import business.Rental;
import business.RentalSys;
import use_cases.HandlerProcessRental;

import java.sql.SQLException;
import java.util.List;

public class RentalClient {

    /**
     * A simple interaction with the application services
     *
     * @param args Command line parameters
     */
    public static void main(String[] args) {

        RentalSys app = new RentalSys();

        try {
            app.start();
        } catch (ApplicationException e) {
            System.out.println(e.getMessage());
            System.out.println("Application Message: " + e.getMessage());
            SQLException e1 = (SQLException) e.getCause().getCause();
            System.out.println("SQLException: " + e1.getMessage());
            System.out.println("SQLState: " + e1.getSQLState());
            System.out.println("VendorError: " + e1.getErrorCode());
            return;
        }

        // create catalog(s)
        CatalogRental rentalCatalog = new CatalogRental();

        // this client deals with the Process Sale use case
        HandlerProcessRental hpr = new HandlerProcessRental(rentalCatalog);


        try { // sample interaction


            System.out.println("\n-- Add rental and print it ----------------------------");

            // creates a new rental (returns it)
            Rental rental = hpr.newRental();

            // adds two products to the database
            hpr.addProductToRental(rental, 101, 10);
            hpr.addProductToRental(rental, 102, 25);

            // close rental
            hpr.closeRental(rental);

            //////////////////

            rental = rentalCatalog.getRental(rental.getId());
            System.out.println(rental);

            //////////////////


            System.out.println("\n-- Print all rentals ----------------------------------");

            System.out.println(rentalCatalog);

            //////////////////

            hpr.deleteRental(rental);

            System.out.println("\n-- Print all sales after delete ---------------------");

            System.out.println(rentalCatalog);


        } catch (ApplicationException e) {
            System.out.println("Error: " + e.getMessage());
            // for debugging purposes only. Typically, in the application this information
            // can be associated with a "details" button when the error message is displayed.
            if (e.getCause() != null)
                System.out.println("Cause: ");
            e.printStackTrace();
        }

        app.stop();
    }
}
