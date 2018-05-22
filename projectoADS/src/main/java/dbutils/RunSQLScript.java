package dbutils;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class RunSQLScript {

	public static void runScript (Connection connection, String scriptFilename) throws FileNotFoundException, IOException, SQLException {
		try (BufferedReader br = new BufferedReader(new FileReader(scriptFilename))) {
		    String command;
		    int i = 1;
		    while ((command = br.readLine()) != null) {
		        System.out.println(i + ": " + command);
		        i++;
		    	Statement statement = connection.createStatement();
		        statement.execute(command.toString());
		        statement.close();
		    }
		}
	}
	
}
