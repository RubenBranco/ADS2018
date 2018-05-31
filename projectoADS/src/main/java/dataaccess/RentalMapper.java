package dataaccess;

import business.Rental;
import business.RentalProduct;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class RentalMapper {

    // the cache keeps all rentals that were accessed during the current runtime
    static Map<Integer, Rental> cachedRentals;

    static {
        // this code is initialized once per class
        cachedRentals = new HashMap<Integer, Rental>();
    }

    /////////////////////////////////////////////////////////////////////////
    // SQL statement: inserts a new rental
    private static final String INSERT_RENTAL_SQL =
            "INSERT INTO rental (id, date, total, status) VALUES (DEFAULT, ?, ?, '" + Rental.OPEN + "')";

    /**
     * Inserts a new rental into the database
     * @param date The rental's date
     * @return the rental's id
     */
    public static int insert(java.util.Date date) throws PersistenceException {
        try (PreparedStatement statement =       // get new id
                     DataSource.INSTANCE.prepareGetGenKey(INSERT_RENTAL_SQL)) {
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
            throw new PersistenceException ("Error inserting a new rental!", e);
        }
    }

    /////////////////////////////////////////////////////////////////////////
    // SQL statement: updates total and status from existing rental
    private static final String UPDATE_RENTAL_SQL =
            "UPDATE rental SET total = ?, status = ? WHERE id = ?";

    /**
     * Updates the rental's data in the database
     *
     * @param rental_id The rental id to update
     * @param total the new rental total
     * @param status is the rental open or closed?
     * @throws PersistenceException If an error occurs during the operation
     */
    public static void update(int rental_id, double total, String status) throws PersistenceException {
        try (PreparedStatement statement = DataSource.INSTANCE.prepare(UPDATE_RENTAL_SQL)) {
            statement.setDouble(1, total);
            statement.setString(2, status);
            statement.setInt(3, rental_id);
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new PersistenceException ("Internal error!", e);
        }

        cachedRentals.remove(rental_id);  // rental was changed, remove from cache
    }

    /////////////////////////////////////////////////////////////////////////
    // SQL statement: deletes rental
    private static final String DELETE_RENTAL_SQL =
            "DELETE FROM rental WHERE id = ?";

    /**
     * Deletes the rental's data in the database.
     * Notice that current product stocks are not changed!
     *
     * @param rental_id The rental id to delete
     * @throws PersistenceException If an error occurs during the operation
     */
    public static void delete(int rental_id) throws PersistenceException {

        RentalProductMapper.delete(rental_id);  // first remove its rental products

        try (PreparedStatement statement = DataSource.INSTANCE.prepare(DELETE_RENTAL_SQL)) {
            statement.setDouble(1, rental_id);
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new PersistenceException ("Internal error!", e);
        }

        cachedRentals.remove(rental_id);  // rental was deleted, remove from cache
    }

    /////////////////////////////////////////////////////////////////////////
    // SQL statement: selects a rental by its id
    private static final String GET_RENTAL_SQL =
            "SELECT id, date, total, status FROM rental WHERE id = ?";

    /**
     * Gets a rental by its id
     *
     * @param rental_id The rental id to search for
     * @return The new object that represents an in-memory rental
     * @throws PersistenceException In case there is an error accessing the database.
     */
    public static Rental getRentalById(int rental_id) throws PersistenceException {

        if (cachedRentals.containsKey(rental_id))  // perhaps this rental is cached?
            return cachedRentals.get(rental_id);   //  yes, we don't need to query the database

        try (PreparedStatement statement = DataSource.INSTANCE.prepare(GET_RENTAL_SQL)) {
            // set statement arguments
            statement.setInt(1, rental_id);
            // execute SQL
            try (ResultSet rs = statement.executeQuery()) {
                rs.next();
                Rental rental = loadRental(rs);             // creates rental object from result set
                cachedRentals.put(rental.getId(), rental);  // inserts it into cache
                return rental;
            }
        } catch (SQLException e) {
            throw new PersistenceException("Internal error getting sale " + rental_id, e);
        }
    }

    /////////////////////////////////////////////////////////////////////////
    // SQL statement: get all rentals
    private static final String GET_ALL_RENTALS_SQL = "SELECT * FROM rental";

    /**
     * Retrieve all rentals kept on database
     * @return A list with all the rentals
     * @throws PersistenceException
     */
    public static List<Rental> getAllRentals() throws PersistenceException {

        try (PreparedStatement statement = DataSource.INSTANCE.prepare(GET_ALL_RENTALS_SQL)) {
            try (ResultSet rs = statement.executeQuery()) {

                List<Rental> rentals = new LinkedList<Rental>();
                while(rs.next()) { // for each rental
                    int rental_id = rs.getInt("id");          // get id of current rental
                    if (cachedRentals.containsKey(rental_id))   // check if it is cached
                        rentals.add(cachedRentals.get(rental_id));
                    else {
                        Rental rental = loadRental(rs);           // if not, create a new rental object
                        rentals.add(rental);                    //  insert it to result list,
                        cachedRentals.put(rental_id, rental);     //  and cache it
                    }
                }
                return rentals;
            }
        } catch (SQLException e) {
            throw new PersistenceException("Unable to fetch all rentals", e);
        }
    }

    /**
     * Creates a rental object from a result set retrieved from the database.
     *
     * @requires rs.next() was already executed
     * @param rs The result set with the information to create the rental.
     * @return A new rental loaded from the database.
     * @throws PersistenceException
     */
    private static Rental loadRental(ResultSet rs) throws PersistenceException {
        Rental rental;
        try {
            rental = new Rental(rs.getInt("id"), rs.getDate("date"));

            List<RentalProduct> rentalProducts = RentalProductMapper.getRentalProducts(rs.getInt("id"));
            for(RentalProduct rp : rentalProducts)
                rental.addProductToRental(rp.getProduct(), (int) rp.getQty());

            if (rs.getString("status").equals(Rental.CLOSED))
                rental.close();

        } catch (SQLException e) {
            throw new RecordNotFoundException ("Rental does not exist	", e);
        }
        return rental;
    }
}
