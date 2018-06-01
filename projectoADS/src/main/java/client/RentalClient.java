package client;

import business.*;
import dataaccess.PersistenceException;
import use_cases.HandlerProcessRental;
import use_cases.HandlerProcessReturnRentedItems;

import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Scanner;

/**
 * A client to do operations with rental use cases
 *
 * @author ADS08
 */
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
        CatalogProduct productCatalog = new CatalogProduct();

        // this client deals with the Process Sale use case
        HandlerProcessRental hpr = new HandlerProcessRental(rentalCatalog);

        HandlerProcessReturnRentedItems hprri = new HandlerProcessReturnRentedItems(rentalCatalog);


        try { // sample interaction

            System.out.println("\n-- Add rental and print it ----------------------------");
            System.out.println("\n-- How many days do you want to rent the items for?");

            Scanner in = new Scanner(System.in);
            while (!in.hasNextInt()) {
                in.next();
            }

            int rentalDays = in.nextInt();

            Date now = new Date();
            Calendar calendar = Calendar.getInstance();

            calendar.setTime(now);

            calendar.add(Calendar.DAY_OF_YEAR, rentalDays);

            Date returnDate = calendar.getTime();

            // creates a new rental (returns it)
            Rental rental = hpr.newRental(returnDate);

            // adds two products to the database
            hpr.addProductToRental(rental, 101, 1);
            hpr.addProductToRental(rental, 102, 1);

            // close rental
            hpr.closeRental(rental);

            //////////////////

            rental = rentalCatalog.getRental(rental.getId());
            System.out.println(rental);

            //////////////////


            System.out.println("\n-- Print all rentals ----------------------------------");

            System.out.println(rentalCatalog);

            //////////////////

            System.out.println("\n-- Return products ------------------------------------");

            System.out.println("\n-- Rental before returning and its products");

            System.out.println(rental);
            System.out.println(productCatalog.getStocksOfRentalProducts(rental));

            System.out.println("\n-- What day are you returning your products? (YYYY-MM-DD)");

            String clientReturnDateStr = in.next();
            DateFormat dateFormat = new SimpleDateFormat("y-M-d");
            Date clientReturnDate = dateFormat.parse(clientReturnDateStr);

            hprri.returnProductFromRental(101, 1);
            hprri.returnProductFromRental(102,1);

            hprri.setRentalAsReturned(rental);

            System.out.println("Penalty fee: " + rental.penalty(clientReturnDate) + "€");

            System.out.println("\n-- Rental after returning items and its products");
            rental = rentalCatalog.getRental(rental.getId());

            System.out.println(rental);
            System.out.println(productCatalog.getStocksOfRentalProducts(rental));
            //////////////////

            hpr.deleteRental(rental);

            System.out.println("\n-- Print all rentals after delete ---------------------");

            System.out.println(rentalCatalog);




        } catch (ApplicationException | java.text.ParseException | PersistenceException e) {
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
