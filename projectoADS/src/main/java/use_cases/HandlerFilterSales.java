package use_cases;

import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import business.ApplicationException;
import business.CatalogSale;
import business.Sale;

public class HandlerFilterSales {
	
	private CatalogSale saleCatalog;
	
	/**
	 * Creates a handler for the filter sales use case
	 * 
	 * @param saleCatalog    A sale's catalog
	 * @param productCatalog A product's catalog
	 */
	public HandlerFilterSales(CatalogSale saleCatalog) {
		this.saleCatalog = saleCatalog;
	}
	
	/**
	 * Filter all the sales accordingly to a given predicate
	 * 
	 * @param p The predicate used in the filter
	 * @return The list of sales satisfying p
	 * @throws ApplicationException
	 */
	public List<Sale> filterSales(Predicate<Sale> p) throws ApplicationException {
		
		try {
			return saleCatalog.getAllSales().stream().filter(p).collect(Collectors.toList());
		} catch (ApplicationException e) {
			throw new ApplicationException("Unable to apply filter to current sales", e);
		}
		
	}

}
