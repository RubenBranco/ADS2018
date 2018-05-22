package business;

import dataaccess.PersistenceException;
import dataaccess.ProductMapper;

/** 
 * Includes operations regarding Products
 * 
 * @author jpn
 *
 */
public class CatalogProduct {

	/**
	 * Returns an object ProductSpec that is identified by itemID 
	 * 
	 * @param itemID  The product id
	 * @return The object with this id
	 * @throws ApplicationException If the product does not exist in the database
	 */
	public ProductSpec getProduct(int itemID) throws ApplicationException {
		
		try {
			return ProductMapper.getProductByProdCod(itemID);
		} catch (PersistenceException e) {
			throw new ApplicationException("Unable to retrieve product with itemID " + itemID);
		}
	}
}
