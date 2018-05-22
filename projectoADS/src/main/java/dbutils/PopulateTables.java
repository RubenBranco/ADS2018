package dbutils;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.SQLException;

import dataaccess.DataSource;
import dataaccess.PersistenceException;

public class PopulateTables {

	public void populateADSDerbyDB() throws FileNotFoundException, IOException, SQLException, PersistenceException {
		DataSource.INSTANCE.connect("jdbc:derby:data/derby/adsdb;create=false", "SaleSys", "");
		RunSQLScript.runScript(DataSource.INSTANCE.getConnection(), "data/scripts/resetTables-Derby.sql");
		RunSQLScript.runScript(DataSource.INSTANCE.getConnection(), "data/scripts/populateTables-Derby.sql");
		DataSource.INSTANCE.close();		
	}
	
	public static void main(String[] args) throws PersistenceException, FileNotFoundException, IOException, SQLException {
		new PopulateTables().populateADSDerbyDB();
	}

}