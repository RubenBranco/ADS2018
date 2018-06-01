package business;

import dataaccess.DataSource;
import dataaccess.PersistenceException;

/**
 * Includes operations regarding Rentals
 *
 * @author ADS08
 */
public class RentalSys {

    /**
     * Starts a connection with the database
     *
     * @throws ApplicationException
     */
    public void start() throws ApplicationException {
        // Connects to the database
        try {
            DataSource.INSTANCE.connect("jdbc:derby:data/derby/adsdb;create=false", "RentalSys", "");
        } catch (PersistenceException e) {
            throw new ApplicationException("Error connecting database", e);
        }
    }

    /**
     * Closes the database connection
     */
    public void stop() {
        // Closes the database connection
        DataSource.INSTANCE.close();
    }

}
