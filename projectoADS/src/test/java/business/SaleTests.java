package business;

import static org.junit.Assert.*;

import java.util.GregorianCalendar;
import java.util.Random;

import org.junit.AfterClass;  // cf. API at http://junit.org/junit4/javadoc/latest/
import org.junit.Before; 
import org.junit.BeforeClass;
import org.junit.Test;

import dataaccess.ProductMapper;

public class SaleTests {
	
	private Sale sale;

	private static SaleSys app;
	private static ProductSpec prod1, prod2, prod3;
	
	@BeforeClass   // run once
	public static void setUpBeforeClass() {
		
		try {
			app = new SaleSys();
			app.start();
			
			prod1 = ProductMapper.getProductById(1001); // assumes they are already there
			prod2 = ProductMapper.getProductById(1002);
			prod3 = ProductMapper.getProductById(1003);
		} catch (Exception e) {
			fail("App didn't start or products could not be retrieved");
		}
	}
	
	@Before   // run before each test
	public void setup() {
		
		sale = new Sale(1, new GregorianCalendar(2016,12,31).getTime());
	}
	
	@Test
	public void test_emptySaleTotal() {
		assertEquals(sale.total(), 0.0, 0.001);
	}

	@Test
	public void test_emptySaleStatus() {
		assertEquals(sale.getStatus(), Sale.OPEN);
	}
	
	@Test
	public void test_emptySaleProducts() {
		assertTrue(sale.getSaleProducts().isEmpty());
	}
	
	@Test
	public void test_close_open() {
		assertTrue(sale.isOpen());
		sale.close();
		assertFalse(sale.isOpen());
		sale.open();
		assertTrue(sale.isOpen());
	}
	
	@Test
	public void test_saleWithProducts_length() {
		
		double qty1 = 100, qty2 = 100;
		sale.addProductToSale(prod1, qty1);
		sale.addProductToSale(prod2, qty2);
		
		assertEquals(sale.getSaleProducts().size(), 2);
	}
	
	@Test
	public void test_saleWithProducts_budget() {
		
		double qty1 = 100, qty2 = 100;
		sale.addProductToSale(prod1, qty1);
		sale.addProductToSale(prod2, qty2);
		
		// compute sale total price
		double expectedSalePrice = qty1 * prod1.getPrice() + qty2 * prod2.getPrice();
		
		assertEquals(sale.total(), expectedSalePrice, 0.001);
	}
	
	/**
	 * This is an eg of a random generated test, useful for heavy testing
	 */
	@Test
	public void test_saleWithProducts_budget_large() {
		
		Random random = new Random();
		
		long sizeTest = 1000;  // number of products to insert into the sale
		int maxAmount = 500;
		
		ProductSpec[] prodArray = new ProductSpec[] {prod1, prod2, prod3};  // available products
		
		// generate random quantities
		double [] qtys  = random.doubles().limit(sizeTest).map(n -> Math.abs(n) % maxAmount).toArray();
		
		// generate valid indexes for prodArray in order to pick products at random
		int [] prods = random.ints().limit(sizeTest).map(n -> Math.abs(n) % prodArray.length).toArray(); 

		double expectedSalePrice = 0;
		for(int i=0; i<sizeTest; i++) { 
			sale.addProductToSale(prodArray[prods[i]], qtys[i]); 
			expectedSalePrice += prodArray[prods[i]].getPrice() * qtys[i];
		}
		
		assertEquals(sale.total(), expectedSalePrice, 0.001);  // budget ok?
		assertEquals(sale.getSaleProducts().size(), sizeTest); // size ok?
		assertTrue(sale.isOpen());
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
