package dataaccess;

import static org.junit.Assert.*;

import java.util.GregorianCalendar;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import business.CatalogSale;
import business.ProductSpec;
import business.Sale;
import business.SaleSys;

/**
 * This class tests the SaleMapper.
 * 
 * This requires attention since we are testing the database access, and 
 * must not change anything in the database after the tests end
 * 
 * @author jpn
 *
 */
public class SaleMapperTests {

	private static SaleSys app;
	private static ProductSpec prod1, prod2, prod3;
	
	private CatalogSale saleCatalog;
	private Sale sale;
	
	private String initialReport;

	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		
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

	@Before
	public void setUp() throws Exception {
		
		saleCatalog = new CatalogSale();
		initialReport = saleCatalog.toString();
		
		int sale_id = SaleMapper.insert(new GregorianCalendar(2016,12,31).getTime());
		
		SaleProductMapper.insert(sale_id, prod1.getId(), 0);
		SaleProductMapper.insert(sale_id, prod2.getId(), 0);
		SaleProductMapper.insert(sale_id, prod3.getId(), 0);
		
		sale = SaleMapper.getSaleById(sale_id);
		sale.close();
		
		SaleMapper.update(sale.getId(), sale.total(), sale.getStatus());
	}

	@Test
	public void test_correctSavedId() {
		try {
			Sale savedSale = SaleMapper.getSaleById(sale.getId());
			
			assertEquals(sale.getId(), savedSale.getId());
		} catch (PersistenceException e) {
			fail("Saved sale could not be retrieved");
		}
	}

	@Test
	public void test_correctGetAll() {
		List<Sale> list = getSaleInList();               // uses getAllSales()

		assertTrue(list.size() == 1);                    // there's one and only one sale 
		assertTrue(list.get(0).getId() == sale.getId()); // and it has the correct id
	}

	@Test
	public void test_correctUpdate() {
		
		try {
		    sale.open();  // reopen sale and update
		    SaleMapper.update(sale.getId(), sale.total(), sale.getStatus());
		    
		    // recover sale	and check if it's open
		    Sale savedSale = SaleMapper.getSaleById(sale.getId());
		    assertTrue(savedSale.isOpen());
		    			
		} catch (PersistenceException e) {
			fail("Sale was not updated properly.");
		}
	}
	
	@Test
	public void test_correctDelete() {
		try {
			SaleMapper.delete(sale.getId());
			assertTrue(getSaleInList().isEmpty());
		} catch (PersistenceException e) {
			fail("Sale was not deleted");
		}
	}
	
	@Test
	public void test_correctDeleteUsingToString() {
		// compare catalog before sale was inserted and after sale was deleted
		try {
			SaleMapper.delete(sale.getId());
			
			String finalReport = saleCatalog.toString();
			
			assertEquals(initialReport, finalReport); 
		} catch (PersistenceException e) {
			fail("Sale reports are different");
		}
	}

	@After
	public void finish() {
		try {
			// if the sale was not deleted yet...
			if (!getSaleInList().isEmpty())   
				SaleMapper.delete(sale.getId());
		} catch (PersistenceException e) {
			fail("Sale could not be deleted");
		}
		
		// check if the database remains the same after each test
		String finalReport = saleCatalog.toString();
		assertEquals(initialReport, finalReport); 		
	}
	
	@AfterClass
	public static void finishAfterClass() {
		
		try {
			app.stop();
		} catch (Exception e) {
			fail("App unable to finish");
		}
	}

	/**
	 * Returns a list with a single element, the current sale retrieved from 
	 * the database. If the current sale is not in the database, the returned 
	 * list will be empty
	 * 
	 * @return list with the current sale retrieved from the database (empty, otherwise)
	 */
	private List<Sale> getSaleInList() {
		try {
			return SaleMapper.getAllSales()   // get all sales and filter for the sale id
					          .stream()
					          .filter(aSale -> aSale.getId() == sale.getId())
					          .collect(Collectors.toList());   // java 8 magic!
		} catch (PersistenceException e) {
			return null; // something wrong happened, prepare for Null Pointer Explosion
		}
	}
}
