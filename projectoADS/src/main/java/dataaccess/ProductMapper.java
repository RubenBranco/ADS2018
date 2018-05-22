package dataaccess;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import business.ProductSpec;

/**
 * Conferir SaleMapper para mais informacao
 * 
 * @author jpn
 */
public class ProductMapper {
	
	// SQL statement: select product with given id (the database id)
	private static final String GET_PRODUCT_BY_PROD_ID_SQL = 
		    "SELECT id, itemID, description, price, qty FROM product WHERE id = ?";
		
	/**
	 * Gets a product given its codProd 
	 * 
	 * @param item_id The code of the product to search for
	 * @return The in-memory representation of the product
	 * @throws PersistenceException 
	 */
	public static ProductSpec getProductById (int id) throws PersistenceException {
		try (PreparedStatement Statement = DataSource.INSTANCE.prepare(GET_PRODUCT_BY_PROD_ID_SQL)) {
			// set statement arguments
			Statement.setInt(1, id);
			// execute SQL
			try (ResultSet rs = Statement.executeQuery()) {
				// creates a new product with the data retrieved from the database
				return loadProduct(rs);	
			}
		} catch (SQLException e) {
			throw new PersistenceException("Internal error getting product with id " + id, e);
		}
	}
	
	// SQL statement: select product with given code (called itemID)
	private static final String GET_PRODUCT_BY_PROD_COD_SQL = 
		    "SELECT id, itemID, description, price, qty FROM product WHERE itemID = ?";
		
	/**
	 * Gets a product given its codProd 
	 * 
	 * @param item_id The code of the product to search for
	 * @return The in-memory representation of the product
	 * @throws PersistenceException 
	 */
	public static ProductSpec getProductByProdCod (int item_id) throws PersistenceException {
		try (PreparedStatement Statement = DataSource.INSTANCE.prepare(GET_PRODUCT_BY_PROD_COD_SQL)) {
			// set statement arguments
			Statement.setInt(1, item_id);
			// execute SQL
			try (ResultSet rs = Statement.executeQuery()) {
				// creates a new product with the data retrieved from the database
				return loadProduct(rs);	
			}
		} catch (SQLException e) {
			throw new PersistenceException("Internal error getting product with id " + item_id, e);
		}
	}
	
	/**
	 * Creates a product from a result set retrieved from the database.
	 * 
	 * @param rs The result set with the information to create the product.
	 * @return A new product loaded from the database.
	 * @throws RecordNotFoundException In case the result set is empty.
	 */
	private static ProductSpec loadProduct(ResultSet rs) throws RecordNotFoundException {
		try {
			rs.next();
			ProductSpec product = new ProductSpec(rs.getInt("id"), 
					                              rs.getInt("itemID"),
					                              rs.getString("description"),
					                              rs.getDouble("price"),
					                              rs.getDouble("qty"));
			return product;
		} catch (SQLException e) {
			throw new RecordNotFoundException ("Product not found! ", e);
		}
	}
	
	// SQL statement: update product stock
	private static final String	UPDATE_STOCK_SQL =
			"UPDATE product SET qty = ? WHERE id = ?";
	
	/**
	 * Updates the product quantity
	 * 
	 * @throws PersistenceException
	 */
	public static void updateStockValue(int prod_id, double qty) throws PersistenceException {
		try (PreparedStatement statement = DataSource.INSTANCE.prepare(UPDATE_STOCK_SQL)){
			// set statement arguments
			statement.setDouble(1, qty);
			statement.setInt(2, prod_id);
			// execute SQL
			statement.executeUpdate();
		} catch (SQLException e) {
			throw new PersistenceException("Internal error updating product " + prod_id + " stock amount. ", e);
		}
	}
}
