package dbutils;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.SQLException;

import dataaccess.DataSource;
import dataaccess.PersistenceException;

public class CreateDatabase {

	public void createADSDerbyDB() throws FileNotFoundException, IOException, SQLException, PersistenceException {
		DataSource.INSTANCE.connect("jdbc:derby:data/derby/adsdb;create=true", "SaleSys", "");
		RunSQLScript.runScript(DataSource.INSTANCE.getConnection(), "data/scripts/createDDL-Derby.sql");
		RunSQLScript.runScript(DataSource.INSTANCE.getConnection(), "data/scripts/populateTables-Derby.sql");
		DataSource.INSTANCE.close();		
	}
	
	public static void main(String[] args) throws PersistenceException, FileNotFoundException, IOException, SQLException {
		new CreateDatabase().createADSDerbyDB();
	}

}
