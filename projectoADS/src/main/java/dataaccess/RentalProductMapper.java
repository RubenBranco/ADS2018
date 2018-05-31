package dataaccess;

import business.ProductSpec;
import business.RentalProduct;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

public class RentalProductMapper {

    /////////////////////////////////////////////////////////////////////////
    // SQL statement: insert product in a rental
    private static final String INSERT_PRODUCT_RENTAL_SQL =
            "INSERT INTO rentalproduct (id, rental_id, product_id, qty) VALUES (DEFAULT, ?, ?, ?)";

    /**
     * Inserts the record in the rental products table
     *
     * @requires qty >= 0
     * @param rental_id current sale's id
     * @param prod_id current product's id
     * @param qty the quantity
     * @return the rentalProduct's id
     * @throws PersistenceException
     */
    public static int insert (int rental_id, int prod_id, int qty) throws PersistenceException {
        try (PreparedStatement statement = DataSource.INSTANCE.prepareGetGenKey(INSERT_PRODUCT_RENTAL_SQL)) {
            statement.setInt(1, rental_id);    // set statement arguments
            statement.setInt(2, prod_id);
            statement.setDouble(3, qty);
            statement.executeUpdate();      // execute SQL
            // Gets rental product Id generated automatically by the database engine
            try (ResultSet rs = statement.getGeneratedKeys()) {
                rs.next();
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            throw new PersistenceException ("Internal error inserting product " + prod_id + " into rental " + rental_id, e);
        }
    }

    /////////////////////////////////////////////////////////////////////////
    // SQL statement: deletes rentalProducts of sale
    private static final String DELETE_RENTALPRODUCT_SQL =
            "DELETE FROM rentalproduct WHERE rental_id = ?";

    /**
     * Deletes the rental's data in the database.
     * Notice that current product stocks are not changed!
     *
     * @param rental_id The rental id to delete
     * @throws PersistenceException If an error occurs during the operation
     */
    public static void delete(int rental_id) throws PersistenceException {
        try (PreparedStatement statement = DataSource.INSTANCE.prepare(DELETE_RENTALPRODUCT_SQL)) {
            statement.setDouble(1, rental_id);
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new PersistenceException ("Internal error!", e);
        }
    }

    /////////////////////////////////////////////////////////////////////////
    // SQL statement: select the products of a rental by rental id
    private static final String GET_RENTAL_PRODUCTS_SQL =
            "SELECT id, rental_id, product_id, qty FROM rentalproduct WHERE rental_id = ?";

    /**
     * Gets the products of a rental by its rental id
     *
     * @param rental_id The rental id to get the products of
     * @return The set of products that compose the rental
     * @throws PersistenceException When there is an error obtaining the
     *         information from the database.
     */
    public static List<RentalProduct> getRentalProducts(int rental_id) throws PersistenceException {
        try (PreparedStatement statement = DataSource.INSTANCE.prepare(GET_RENTAL_PRODUCTS_SQL)) {
            // set statement arguments
            statement.setInt(1, rental_id);
            // execute SQL
            try (ResultSet rs = statement.executeQuery()) {
                // creates the rental's products set with the data retrieved from the database
                return loadRentalProducts(rs);
            }
        } catch (SQLException e) {
            throw new PersistenceException("Internal error getting the products of rental " + rental_id, e);
        }
    }

    /**
     * Creates the set of products of a rental from a result set retrieved from the database.
     *
     * @param rs The result set with the information to create the set of products from a rental.
     * @return The set of products of a rental loaded from the database.
     * @throws SQLException When there is an error reading from the database.
     * @throws PersistenceException
     */
    private static List<RentalProduct> loadRentalProducts(ResultSet rs) throws SQLException, PersistenceException {
        List<RentalProduct> result = new LinkedList<RentalProduct>();
        while (rs.next()) {
            ProductSpec product = ProductMapper.getProductById(rs.getInt("product_id"));
            RentalProduct newRentalProduct = new RentalProduct(product, rs.getInt("qty"));
            newRentalProduct.setId(rs.getInt("id"));
            result.add(newRentalProduct);
        }
        return result;
    }

}
