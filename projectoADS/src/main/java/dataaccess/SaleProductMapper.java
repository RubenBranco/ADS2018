package dataaccess;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.util.LinkedList;
import java.util.List;

import business.ProductSpec;
import business.SaleProduct;

/**
 * Conferir SaleMapper para mais informacao
 * 
 * @author jpn
 */
public class SaleProductMapper {
	
	/////////////////////////////////////////////////////////////////////////
	// SQL statement: insert product in a sale 
	private static final String INSERT_PRODUCT_SALE_SQL = 
		"INSERT INTO saleproduct (id, sale_id, product_id, qty) VALUES (DEFAULT, ?, ?, ?)";
	
	/**
	 * Inserts the record in the sale products table 
	 * 
	 * @requires qty >= 0
	 * @param sale_id current sale's id
	 * @param prod_id current product's id
	 * @param qty the quantity
	 * @return the saleProduct's id
	 * @throws PersistenceException
	 */
	public static int insert (int sale_id, int prod_id, double qty) throws PersistenceException {
		try (PreparedStatement statement = DataSource.INSTANCE.prepareGetGenKey(INSERT_PRODUCT_SALE_SQL)) {
			statement.setInt(1, sale_id);    // set statement arguments
			statement.setInt(2, prod_id);
			statement.setDouble(3, qty);		
			statement.executeUpdate();      // execute SQL
			  // Gets sale product Id generated automatically by the database engine
			try (ResultSet rs = statement.getGeneratedKeys()) {
				rs.next(); 
				return rs.getInt(1);
			}
		} catch (SQLException e) {
			throw new PersistenceException ("Internal error inserting product " + prod_id + " into sale " + sale_id, e);
		}
	}
	
	/////////////////////////////////////////////////////////////////////////
	// SQL statement: deletes saleProducts of sale
	private static final String DELETE_SALEPRODUCT_SQL = 
			"DELETE FROM saleproduct WHERE sale_id = ?";
	
	/**
	 * Deletes the sale's data in the database.
	 * Notice that current product stocks are not changed!
	 * 
	 * @param sale_id The sale id to delete
	 * @throws PersistenceException If an error occurs during the operation
	 */
	public static void delete(int sale_id) throws PersistenceException {
		try (PreparedStatement statement = DataSource.INSTANCE.prepare(DELETE_SALEPRODUCT_SQL)) {
			statement.setDouble(1, sale_id);
			statement.executeUpdate();
		} catch (SQLException e) {
			throw new PersistenceException ("Internal error!", e);
		}
	}

	/////////////////////////////////////////////////////////////////////////
	// SQL statement: select the products of a sale by sale id 
	private static final String GET_SALE_PRODUCTS_SQL = 
			"SELECT id, sale_id, product_id, qty FROM saleproduct WHERE sale_id = ?";
		
	/**
	 * Gets the products of a sale by its sale id 
	 * 
	 * @param sale_id The sale id to get the products of
	 * @return The set of products that compose the sale
	 * @throws PersistenceException When there is an error obtaining the
	 *         information from the database.
	 */
	public static List<SaleProduct> getSaleProducts(int sale_id) throws PersistenceException {
		try (PreparedStatement statement = DataSource.INSTANCE.prepare(GET_SALE_PRODUCTS_SQL)) {
			// set statement arguments
			statement.setInt(1, sale_id);			
			// execute SQL
			try (ResultSet rs = statement.executeQuery()) {
				// creates the sale's products set with the data retrieved from the database
				return loadSaleProducts(rs);
			}
		} catch (SQLException e) {
			throw new PersistenceException("Internal error getting the products of sale " + sale_id, e);
		}
	}
		
	/**
	 * Creates the set of products of a sale from a result set retrieved from the database.
	 * 
	 * @param rs The result set with the information to create the set of products from a sale.
	 * @return The set of products of a sale loaded from the database.
	 * @throws SQLException When there is an error reading from the database.
	 * @throws PersistenceException 
	 */
	private static List<SaleProduct> loadSaleProducts(ResultSet rs) throws SQLException, PersistenceException {
		List<SaleProduct> result = new LinkedList<SaleProduct>();
		while (rs.next()) {
			ProductSpec product = ProductMapper.getProductById(rs.getInt("product_id"));
			SaleProduct newSaleProduct = new SaleProduct(product, rs.getDouble("qty"));
			newSaleProduct.setId(rs.getInt("id"));
			result.add(newSaleProduct);
		}
		return result;		
	}
}