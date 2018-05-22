package business;

import java.util.Date;
import java.util.List;

import dataaccess.PersistenceException;
import dataaccess.ProductMapper;
import dataaccess.SaleMapper;
import dataaccess.SaleProductMapper;

/** 
 * Includes operations regarding Sales
 * 
 * @author jpn
 *
 */
public class CatalogSale {
	
	/**
	 * Creates a new sale, initially it open, and has a total of zero 
	 * @return
	 * @throws ApplicationException
	 */
	public Sale newSale() throws ApplicationException {

		try {
			int sale_id = SaleMapper.insert(new Date());  // create new entry in the database
			return SaleMapper.getSaleById(sale_id);       
		} catch (PersistenceException e) {
			throw new ApplicationException("Unable to create new sale", e);
		}
	}
	
	/**
	 * Add a product to an open sale
	 * 
	 * @param sale    The current sale (must be open)
	 * @param prod_id The product id to add (must exist)
	 * @param qty     The quantity sold (must not be higher than the current stock)
	 * @throws ApplicationException If some of these assumptions does not hold
	 */
	public void addProductToSale(Sale sale, int prod_id, double qty) 
			throws ApplicationException {
		
		if (!sale.isOpen())    // check if it's open
			throw new ApplicationException("Sale " + sale.getId() + " is already closed!");
		    
		if (qty<0)
			throw new ApplicationException("Negative amount (" + qty + " units of product " + prod_id + ") for sale" + sale.getId());
		
		ProductSpec product;
		
		// check if product exists and the stock is enough, if so update stock
		try {
			product = ProductMapper.getProductByProdCod(prod_id);
			
			if (product.getStock() < qty)   // not enough units?
				throw new ApplicationException("Current stock is not enough to sell " + qty + 
						" units of product " + product.getId());
			
			// otherwise, update stock
			product.setStock(product.getStock() - qty);
			ProductMapper.updateStockValue(product.getId(), product.getStock());
			
		} catch (PersistenceException e) {
			throw new ApplicationException("Product " + prod_id + " does not exist!", e);
		}

		sale.addProductToSale(product, qty);  // add it to the object sale
		
		try {
			SaleProductMapper.insert(sale.getId(), product.getId(), qty);  // add it to the database
		} catch (PersistenceException e) {
			throw new ApplicationException("Unable to add " + product.getProductCode() + 
					" to sale id " + sale.getId(), e);
		}
	}

	/**
	 * Close sale, updating the total and its status.
	 * If the sale was already closed, nothing happens.
	 * 
	 * @param sale the sale to be closed
	 * @throws ApplicationException 
	 */
	public void closeSale(Sale sale) throws ApplicationException {
		
		if (sale.isOpen()) {
			try {
				sale.close();
				SaleMapper.update(sale.getId(), sale.total(), sale.getStatus());
			} catch (PersistenceException e) {
				throw new ApplicationException("Unable to close " + sale.getId() + 
						", or unable to find it", e);
			}
		}
	}
	
	/**
	 * delete sale and its sale products
	 * 
	 * @param sale the sale to be deleted
	 * @throws ApplicationException
	 */
	public void deleteSale(Sale sale) throws ApplicationException {
		try {
			SaleMapper.delete(sale.getId());
		} catch (PersistenceException e) {
			throw new ApplicationException("Unable to delete sale " + sale.getId(), e);
		}
	}

	public Sale getSale(int sale_id) throws ApplicationException {
		try {
			return SaleMapper.getSaleById(sale_id);
		} catch (PersistenceException e) {
			throw new ApplicationException("Unable to retrieve sale " + sale_id, e);
		}
	}
	
	public List<Sale> getAllSales() throws ApplicationException {
		try {
			return SaleMapper.getAllSales();
		} catch (PersistenceException e) {
			throw new ApplicationException("Unable to retrieve all sales.", e);
		}
	}
	
	/**
	 * String representation of all sales (attention: might produce a quite large output)
	 */
	public String toString() {
		try {
			List<Sale> sales = SaleMapper.getAllSales();
			StringBuffer sb = new StringBuffer();

			for(Sale sale : sales) {
			  sb.append(sale);
			  sb.append("\n");
			}
			
			return sb.toString();			
		} catch (PersistenceException e) {
			System.out.println(e);
			return "N/A"; // something went wrong
		}
	}
}
