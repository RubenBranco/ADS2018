package business;

import static org.junit.Assert.*;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Random;

import dataaccess.PersistenceException;
import javafx.application.Application;
import org.junit.AfterClass;  // cf. API at http://junit.org/junit4/javadoc/latest/
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import dataaccess.ProductMapper;
import use_cases.HandlerProcessRental;
import use_cases.HandlerProcessReturnRentedItems;

public class ReturnRentedItemsTest {
    private Rental rental;

    private static RentalSys app;
    private static ProductSpec prod1, prod2, prod3;
    private static HandlerProcessRental hpr;
    private static CatalogRental rentalCatalog;
    private static CatalogProduct productCatalog;
    private static HandlerProcessReturnRentedItems hprri;

    @BeforeClass
    public static void setUpBeforeClass() {
        try {
            app = new RentalSys();
            app.start();

            rentalCatalog = new CatalogRental();
            productCatalog = new CatalogProduct();
            hpr = new HandlerProcessRental(rentalCatalog);
            hprri = new HandlerProcessReturnRentedItems(rentalCatalog);
            prod1 = ProductMapper.getProductById(1002);
            prod2 = ProductMapper.getProductById(1003);
            prod3 = ProductMapper.getProductById(1004);

        } catch (Exception e) {
            fail("App didn't start or products could not be retrieved");
        }
    }

    @Before
    public void setup() throws ApplicationException {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, 10);
        rental = hpr.newRental(calendar.getTime());
        hpr.addProductToRental(rental, prod1.getProductCode(), 1);
        hpr.addProductToRental(rental, prod2.getProductCode(), 1);
        hpr.addProductToRental(rental, prod3.getProductCode(), 1);
        hpr.closeRental(rental);
    }

    @Test
    public void test_return_unreturn() throws PersistenceException, ApplicationException {
        assertFalse(rental.isReturned());
        rental.returnItems();
        assertTrue(rental.isReturned());
        rental.unreturnItems();
        assertFalse(rental.isReturned());

        hprri.returnProductFromRental(prod1.getProductCode(), 1);
        hprri.returnProductFromRental(prod2.getProductCode(), 1);
        hprri.returnProductFromRental(prod3.getProductCode(), 1);
        hprri.setRentalAsReturned(rental);
    }

    @Test
    public void test_return_items_on_time() throws PersistenceException, ApplicationException {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, 9);

        hprri.returnProductFromRental(prod1.getProductCode(), 1);
        hprri.returnProductFromRental(prod2.getProductCode(), 1);
        hprri.returnProductFromRental(prod3.getProductCode(), 1);
        hprri.setRentalAsReturned(rental);

        double penalty = rental.penalty(calendar.getTime());
        assertEquals(0.0, penalty, 0.001);
    }

    @Test
    public void test_return_items_miss_soft_limit() throws ApplicationException, PersistenceException {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, 11);

        hprri.returnProductFromRental(prod1.getProductCode(), 1);
        hprri.returnProductFromRental(prod2.getProductCode(), 1);
        hprri.returnProductFromRental(prod3.getProductCode(), 1);
        hprri.setRentalAsReturned(rental);

        double penalty = rental.penalty(calendar.getTime());
        assertEquals(1310.0, penalty, 0.001);
    }

    @Test
    public void test_return_items_miss_hard_limit() throws ApplicationException, PersistenceException {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, 20);

        hprri.returnProductFromRental(prod1.getProductCode(), 1);
        hprri.returnProductFromRental(prod2.getProductCode(), 1);
        hprri.returnProductFromRental(prod3.getProductCode(), 1);
        hprri.setRentalAsReturned(rental);

        double penalty = rental.penalty(calendar.getTime());
        assertEquals(10480.0, penalty,0.001);
    }

    @Test
    public void test_inventory_update() throws ApplicationException, PersistenceException {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, 9);
        rental = rentalCatalog.getRental(rental.getId());

        ProductSpec prod1Before = productCatalog.getProduct(prod1.getProductCode());
        ProductSpec prod2Before = productCatalog.getProduct(prod2.getProductCode());
        ProductSpec prod3Before = productCatalog.getProduct(prod3.getProductCode());

        hprri.returnProductFromRental(prod1.getProductCode(), 1);
        hprri.returnProductFromRental(prod2.getProductCode(), 1);
        hprri.returnProductFromRental(prod3.getProductCode(), 1);
        hprri.setRentalAsReturned(rental);

        ProductSpec prod1After = productCatalog.getProduct(prod1.getProductCode());
        ProductSpec prod2After = productCatalog.getProduct(prod2.getProductCode());
        ProductSpec prod3After = productCatalog.getProduct(prod3.getProductCode());

        assertEquals(prod1Before.getStock() + 1, prod1After.getStock());
        assertEquals(prod2Before.getStock()+ 1, prod2After.getStock());
        assertEquals(prod3Before.getStock()+ 1, prod3After.getStock());

    }

    @AfterClass
    public static void finishAfterClass() {
        try {
            app.stop();
        } catch (Exception e) {
            fail("App unable to finish");
        }
    }
}
