package use_cases;

import business.ApplicationException;
import business.CatalogSale;
import business.Sale;

/**
 * Handles use case process sale. Version with two operations: 
 * newSale followed by an arbitrary number of addProductToSale
 * 
 * @author fmartins, jpn
 * @version 1.3 (06/01/2017)
 */
public class HandlerProcessSale {
	
	private CatalogSale    saleCatalog;
	
	/**
	 * Creates a handler for the process sale use case given 
	 * the sale, and product catalogs which contain the relevant
	 * operators to execute this use case methods
	 * 
	 * @param saleCatalog    A sale's catalog
	 * @param productCatalog A product's catalog
	 */
	public HandlerProcessSale(CatalogSale saleCatalog) {
		this.saleCatalog = saleCatalog;
	}

	/**
	 * Creates a new sale
	 * 
	 * @throws ApplicationException In case the sale fails to be created
	 */
	public Sale newSale() throws ApplicationException {
		return saleCatalog.newSale();
	}

	/**
	 * Adds a product to a sale
	 * 
	 * @param sale      The current sale 
	 * @param prod_code The product id to be added to the sale 
	 * @param qty       The quantity of the product sold
	 * @throws ApplicationException When the sale is closed, the product code
	 * is not part of the product's catalog, or when there is not enough stock
	 * to proceed with the sale
	 */
	public void addProductToSale (Sale sale, int prod_code, double qty) throws ApplicationException {
		saleCatalog.addProductToSale(sale, prod_code, qty);
	}

    /**
     * Closes an open sale
     * @param sale   The current sale 
     * @throws ApplicationException 
     */
	public void closeSale(Sale sale) throws ApplicationException {
		saleCatalog.closeSale(sale);		
	}

    /**
     * Deletes sale
     * @param sale   The current sale 
     * @throws ApplicationException 
     */
	public void deleteSale(Sale sale) throws ApplicationException {
		saleCatalog.deleteSale(sale);		
	}
}
