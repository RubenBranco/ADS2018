package business;

import dataaccess.DataSource;
import dataaccess.PersistenceException;

public class RentalSys {

    public void start() throws ApplicationException {
        // Connects to the database
        try {
            DataSource.INSTANCE.connect("jdbc:derby:data/derby/adsdb;create=false", "RentalSys", "");
        } catch (PersistenceException e) {
            throw new ApplicationException("Error connecting database", e);
        }
    }

    public void stop()  {
        // Closes the database connection
        DataSource.INSTANCE.close();
    }

}
