package dataaccess;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;


/**
 * Singleton that abstracts a database connection
 *
 * Remarks:
 * 1. Please notice how the Singleton is implemented. The usual form of implementing
 * a singleton (using the getInstance) is not thread safe (it will not work properly
 * in a concurrent setting) and correct implementations based to fix the problem are
 * more complex that the one I show here. This solution, besides being elegant, works
 * properly in a concurrent setting.
 * 
 * 2. The usage of Singleton classes should be avoided; it does not go well with 
 * unit testing, since there are explicit references to the class name instead of
 * a reference to the object of the class. This class serves to illustrate the
 * concept of DataSouce and is easy to understand. In version 2 I present an 
 * alternative implementation without a singleton implementation.
 *  
 * @author fmartins
 * @version 1.1 (18/02/2015)
 *
 */
public enum DataSource {
	INSTANCE;
	
	/**
	 * A connection to the database
	 */ 
	private Connection connection;

	// 1. Connections
	
	/**
	 * Constructs a database connection given the connection url, the username, and its password
	 * for the database engine.
	 * 
	 * @param url The database connection URL 
	 * @param username The username to login into the database 
	 * @param password The user's password 
	 * @return The data source.
	 * @throws PersistenceException In case the connection fails to establish
	 */
	public DataSource connect (String url, String username, String password) throws PersistenceException {
		try {
			connection = DriverManager.getConnection (url, username, password);
			return INSTANCE;
		} catch (SQLException e) {
			throw new PersistenceException("Cannot connect to database", e);
		}
	}
	
	/**
	 * @return The current database connection 
	 */
	public Connection getConnection () {
		return connection;
	}

	/**
	 * Close the database connection
	 */
	public void close () {
		try {
			connection.close();
		} catch (SQLException e) {
			// nothing that we can do about it...
		}
	}
	
	
	// 2. Prepare statements

	/**
	 * Prepare an SQL statement from an SQL string
	 * 
	 * @param sql The SQL text to prepare the command
	 * @return The prepared statement for the SQL text
	 * @throws PersistenceException In case the prepare statement 
	 * encounters an error.
	 */
	public PreparedStatement prepare (String sql) throws PersistenceException {
		try {
			return connection.prepareStatement(sql);
		} catch (SQLException e) {
			throw new PersistenceException("Error preparing comment", e);
		} 
	}
	
	/**
	 * Prepare an SQL statement from an SQL string and informs the underlying JDBC 
	 * layer to get the automatically generated database keys.
	 * 
	 * @param sql The SQL text to prepare the command
	 * @return The prepared statement for the SQL text
	 * @throws SQLException PersistenceException In case the prepare statement 
	 * encounters an error.
	 */
	public PreparedStatement prepareGetGenKey (String sql) throws SQLException {
		return connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS); 
	}
		
	
	// 3. Transactions
	
	/**
	 * Begins a database transaction
	 * 
	 * @throws PersistenceException In case the set commit flag cannot be set
	 */
	public void beginTransaction() throws PersistenceException {
		try {
			connection.setAutoCommit(false);
		} catch (SQLException e) {
			throw new PersistenceException("Error starting DB transaction", e);
		}
	}
	
	/**
	 * Commits a transaction 
	 * 
	 * @throws PersistenceException In case the commit transaction fails
	 */
	public void commit() throws PersistenceException {
		try {
			connection.commit();
		} catch (SQLException e) {
			throw new PersistenceException("Error on commit", e);
		}
		startAutoCommit();
	}

	/**
	 * Rolls back a transaction
	 * 
	 * @throws PersistenceException In case the rollback transaction fails
	 */
	public void rollback() throws PersistenceException {
		try {
			connection.rollback();
		} catch (SQLException e) {
			throw new PersistenceException("Error on rollback!", e);
		}
		startAutoCommit();
	}

	/**
	 * Disables commitment control
	 * 
	 * @throws PersistenceException In case the set commit flag cannot be set
	 */
	private void startAutoCommit() throws PersistenceException {
		try {
			connection.setAutoCommit(true);
		} catch (SQLException e) {
			throw new PersistenceException("Error starting auto commit", e);
		}
	}

}
