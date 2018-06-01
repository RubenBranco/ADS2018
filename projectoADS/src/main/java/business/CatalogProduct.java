package business;

import dataaccess.PersistenceException;
import dataaccess.ProductMapper;

import java.util.List;

/**
 * Includes operations regarding Products
 *
 * @author jpn, ADS08
 */
public class CatalogProduct {

    /**
     * Returns an object ProductSpec that is identified by itemID
     *
     * @param itemID The product id
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

    /**
     * Produces a string containing the available stock of products from a rental
     *
     * @param rental is a rental object
     * @return A string containing the information.
     * @throws ApplicationException
     */
    public String getStocksOfRentalProducts(Rental rental) throws ApplicationException {
        StringBuilder sb = new StringBuilder();
        List<RentalProduct> rentalProducts = rental.getRentalProducts();
        for (RentalProduct rp : rentalProducts) {
            ProductSpec product = rp.getProduct();
            sb.append("There are " + product.getStock() + " units in stock of product with code " + product.getProductCode() + "; ");
        }

        return sb.toString();
    }
}
