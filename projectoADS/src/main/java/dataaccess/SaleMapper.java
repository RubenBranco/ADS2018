package dataaccess;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.LinkedList;

import business.Sale;
import business.SaleProduct;

/**
 * Este e' um exemplo do padrão Mapper (Fowler cap.18) que estabelece
 * a comunicação entre dois sistemas: a camada de negócio e a base de dados
 * disponibilizada via DataSource.
 * 
 * Este e' um padrao adequado para lidar com o domain model, dado que cada
 * conceito do domain model corresponde a uma classe Mapper. Por exemplo,
 * a classe Sale tem o SaleMapper, o ProductSpec tem o ProductMapper, etc.
 * 
 * Tem a desvantagem de precisar do acesso das classes da camada de negocio
 * 
 * A classe tem as operações CRUD (create, read, update, delete) em relacao
 * 'as vendas
 * 
 * Nesta classe foi incluido tambem uma cache, que permite reduzir o acesso
 * 'a base de dados.
 * 
 * Todos os metodos e atributos sao static. Estes Mappers poderiam ter sido
 * implementados como Singletons.
 * 
 * @author jpn
 * @version 1.0 (11/Jan/2016)
 */
public class SaleMapper {
	
	// the cache keeps all sales that were accessed during the current runtime
	static Map<Integer, Sale> cachedSales;
	
	static {
		// this code is initialized once per class
		cachedSales = new HashMap<Integer, Sale>();
	}
	
	/////////////////////////////////////////////////////////////////////////
	// SQL statement: inserts a new sale
	private static final String INSERT_SALE_SQL = 
		"INSERT INTO sale (id, date, total, status) VALUES (DEFAULT, ?, ?, '" + Sale.OPEN + "')";
	
	/**
	 * Inserts a new sale into the database
	 * @param date The sale's date
	 * @return the sale's id
	 */
	public static int insert(java.util.Date date) throws PersistenceException {	
		try (PreparedStatement statement =       // get new id
				DataSource.INSTANCE.prepareGetGenKey(INSERT_SALE_SQL)) {  
			// set statement arguments
			statement.setDate(1, new java.sql.Date(date.getTime()));
			statement.setDouble(2, 0.0); // total
			// execute SQL
			statement.executeUpdate();
			// get sale Id generated automatically by the database engine
			try (ResultSet rs = statement.getGeneratedKeys()) {
				rs.next(); 
				return rs.getInt(1);
			}
		} catch (SQLException e) {
			throw new PersistenceException ("Error inserting a new sale!", e);
		}
	}
	
	/////////////////////////////////////////////////////////////////////////
	// SQL statement: updates total and status from existing sale
	private static final String UPDATE_SALE_SQL = 
			"UPDATE sale SET total = ?, status = ? WHERE id = ?";
	
	/**
	 * Updates the sale's data in the database
	 * 
	 * @param sale_id The sale id to update
	 * @param total the new sale total
	 * @param status is the sale open or closed?
	 * @throws PersistenceException If an error occurs during the operation
	 */
	public static void update(int sale_id, double total, String status) throws PersistenceException {
		try (PreparedStatement statement = DataSource.INSTANCE.prepare(UPDATE_SALE_SQL)) {
			statement.setDouble(1, total);
			statement.setString(2, status);
			statement.setInt(3, sale_id);
			statement.executeUpdate();
		} catch (SQLException e) {
			throw new PersistenceException ("Internal error!", e);
		}
		
		cachedSales.remove(sale_id);  // sale was changed, remove from cache
	}
	
	/////////////////////////////////////////////////////////////////////////
	// SQL statement: deletes sale 
	private static final String DELETE_SALE_SQL = 
			"DELETE FROM sale WHERE id = ?";
	
	/**
	 * Deletes the sale's data in the database.
	 * Notice that current product stocks are not changed!
	 * 
	 * @param sale_id The sale id to delete
	 * @throws PersistenceException If an error occurs during the operation
	 */
	public static void delete(int sale_id) throws PersistenceException {
		
		SaleProductMapper.delete(sale_id);  // first remove its sale products
		
		try (PreparedStatement statement = DataSource.INSTANCE.prepare(DELETE_SALE_SQL)) {
			statement.setDouble(1, sale_id);
			statement.executeUpdate();
		} catch (SQLException e) {
			throw new PersistenceException ("Internal error!", e);
		}
		
		cachedSales.remove(sale_id);  // sale was deleted, remove from cache
	}
	
	/////////////////////////////////////////////////////////////////////////
	// SQL statement: selects a sale by its id 
	private static final String GET_SALE_SQL = 
			"SELECT id, date, total, status FROM sale WHERE id = ?";
	
	/**
	 * Gets a sale by its id 
	 * 
	 * @param sale_id The sale id to search for
	 * @return The new object that represents an in-memory sale
	 * @throws PersistenceException In case there is an error accessing the database.
	 */
	public static Sale getSaleById(int sale_id) throws PersistenceException {
		
		if (cachedSales.containsKey(sale_id))  // perhaps this sale is cached?
			return cachedSales.get(sale_id);   //  yes, we don't need to query the database
		
		try (PreparedStatement statement = DataSource.INSTANCE.prepare(GET_SALE_SQL)) {		
			// set statement arguments
			statement.setInt(1, sale_id);		
			// execute SQL
			try (ResultSet rs = statement.executeQuery()) {
				rs.next();
				Sale sale = loadSale(rs);             // creates sale object from result set
				cachedSales.put(sale.getId(), sale);  // inserts it into cache
				return sale;
			}
		} catch (SQLException e) {
			throw new PersistenceException("Internal error getting sale " + sale_id, e);
		} 
	}
	
	/////////////////////////////////////////////////////////////////////////
	// SQL statement: get all sales 
	private static final String GET_ALL_SALES_SQL = "SELECT * FROM sale";
	
	/**
	 * Retrieve all sales kept on database
	 * @return A list with all the sales
	 * @throws PersistenceException
	 */
	public static List<Sale> getAllSales() throws PersistenceException {
		
		try (PreparedStatement statement = DataSource.INSTANCE.prepare(GET_ALL_SALES_SQL)) {		
			try (ResultSet rs = statement.executeQuery()) {

				List<Sale> sales = new LinkedList<Sale>();
				while(rs.next()) { // for each sale
					int sale_id = rs.getInt("id");          // get id of current sale
					if (cachedSales.containsKey(sale_id))   // check if it is cached
						sales.add(cachedSales.get(sale_id));
					else {
						Sale sale = loadSale(rs);           // if not, create a new sale object
						sales.add(sale);                    //  insert it to result list,
						cachedSales.put(sale_id, sale);     //  and cache it
					}
				}
				return sales;
			}
		} catch (SQLException e) {
			throw new PersistenceException("Unable to fetch all sales", e);
		} 
	}
	
	/**
	 * Creates a sale object from a result set retrieved from the database.
	 * 
	 * @requires rs.next() was already executed
	 * @param rs The result set with the information to create the sale.
	 * @return A new sale loaded from the database.
	 * @throws PersistenceException 
	 */
	private static Sale loadSale(ResultSet rs) throws PersistenceException {
		Sale sale;
		try {
			sale = new Sale(rs.getInt("id"), rs.getDate("date"));
			
			List<SaleProduct> saleProducts = SaleProductMapper.getSaleProducts(rs.getInt("id"));
			for(SaleProduct sp : saleProducts)
				sale.addProductToSale(sp.getProduct(), sp.getQty());
			
			if (rs.getString("status").equals(Sale.CLOSED))
				sale.close();
			
		} catch (SQLException e) {
			throw new RecordNotFoundException ("Sale does not exist	", e);
		}		
		return sale;
	}
}