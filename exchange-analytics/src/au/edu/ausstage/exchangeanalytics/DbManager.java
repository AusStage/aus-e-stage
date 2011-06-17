/* This file is part of the AusStage Exchange Analytics app
 *
 * The AusStage Exchange Analytics app is free software: you can redistribute
 * it and/or modify it under the terms of the GNU General Public License 
 * as published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * The AusStage Exchange Analytics app is distributed in the hope that it will 
 * be useful, but WITHOUT ANY WARRANTY; without even the implied warranty 
 * of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with the AusStage Exchange Analytics app.  
 * If not, see <http://www.gnu.org/licenses/>.
 */
 
package au.edu.ausstage.exchangeanalytics;

import au.edu.ausstage.utils.InputUtils;
import au.edu.ausstage.utils.DbObjects;

// import additional libraries
import java.sql.Connection;
import java.sql.Statement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.DriverManager;

/**
 * Manage access to the database
 * works similarly to the same class in the Utils package, 
 * only this one works with the Apache Derby database
 */
public class DbManager {

	// private class level variables
	Connection connection;
	String     path;

	/**
	 * constructor for this class
	 *
	 * @param databasePath the path to the database
	 */
	public DbManager(String databasePath) {
	
		if(InputUtils.isValid(databasePath) == false) {
			throw new IllegalArgumentException("the database path must be a valid string");
		}
		
		path = databasePath;
	}
	
	/**
	 * get a connection to the database
	 *
	 * @throws a SQLException if something bad happens
	 */
	public void getConnection() throws SQLException {
	
		String dbDriver = "org.apache.derby.jdbc.EmbeddedDriver";
		String dbUrl = "jdbc:derby:" + path +";create=true"; 

		try{
			Class.forName(dbDriver); 
		} catch(java.lang.ClassNotFoundException e) {
			throw new SQLException("unable to load the Apache Derby classes", e);
		}

		try {
			connection = DriverManager.getConnection(dbUrl); 
		} catch (SQLException e)  {
			throw new SQLException("unable to connect to the Apache Derby database");
		}
		
		
	}
	
	//private method to build the tables if necessary
	public void buildTables() throws SQLException {
	
		// see if the tables are there or if this is an empty database
		try {
			this.executeStatement("SELECT file_name FROM processed_files");
			return;
		} catch (SQLException e) {
			
			// create a table to keep track of the event requests files
			String sql = "CREATE TABLE event_requests (request_time TIMESTAMP NOT NULL, request_type VARCHAR(20) NOT NULL, id_value INT NOT NULL, output_type VARCHAR(10) NOT NULL, record_limit VARCHAR(10) NOT NULL, ip_address VARCHAR(16), referer VARCHAR(1024), PRIMARY KEY(request_time))";
		
			try {
				Statement statement = connection.createStatement();
				statement.execute(sql);
			} catch (SQLException ex) {
				throw new SQLException("unable to create the event_requests table", ex);
			}
			
			// create a table to keep track of the event requests files
			sql = "CREATE TABLE resource_requests (request_time TIMESTAMP NOT NULL, request_type VARCHAR(20) NOT NULL, id_value INT NOT NULL, output_type VARCHAR(10) NOT NULL, record_limit VARCHAR(10) NOT NULL, ip_address VARCHAR(16), referer VARCHAR(1024), PRIMARY KEY(request_time))";
		
			try {
				Statement statement = connection.createStatement();
				statement.execute(sql);
			} catch (SQLException ex) {
				throw new SQLException("unable to create the resource_requests table", ex);
			}
			
			// create a table to keep track of the event requests files
			sql = "CREATE TABLE feedback_requests (request_time TIMESTAMP NOT NULL, request_type VARCHAR(20) NOT NULL, id_value INT NOT NULL, output_type VARCHAR(10) NOT NULL, record_limit VARCHAR(10) NOT NULL, ip_address VARCHAR(16), referer VARCHAR(1024), PRIMARY KEY(request_time))";
		
			try {
				Statement statement = connection.createStatement();
				statement.execute(sql);
			} catch (SQLException ex) {
				throw new SQLException("unable to create the feedback_requests table", ex);
			}
		}
	}
	
	/**
	 * A method to execute an SQL statement and return a resultset
	 * 
	 * @param sqlQuery the SQL query to execute
	 *
	 * @return         the result set built from executing this query
	 */
	public au.edu.ausstage.utils.DbObjects executeStatement(String sqlQuery) throws SQLException {
	
		// declare instance variables
		ResultSet resultSet;
		Statement statement;
			
		// build a statement
		statement = connection.createStatement();
			
		// execute the statement and get the result set
		resultSet = statement.executeQuery(sqlQuery);
			
		return new au.edu.ausstage.utils.DbObjects(statement, resultSet);
	
	} // end executeStatement method
	
	/**
	 * A method to prepare and execute a prepared SQL statement and return a resultset
	 *
	 * @param sqlQuery   the SQL query to execute
	 * @param parameters an array of parameters, as strings, to pass into the parameter
	 *
	 * @return           the result set built from executing this query
	 */
	public au.edu.ausstage.utils.DbObjects executePreparedStatement(String sqlQuery, String[] parameters) throws SQLException {
	
		// declare instance variables
		ResultSet resultSet;
		PreparedStatement statement;
			
		// build the statement
		statement = connection.prepareStatement(sqlQuery);
		
		// add the parameters
		for(int i = 0; i < parameters.length; i++) {
		
			// statements are indexed starting with 1
			// arrays are indexed starting with 0
			statement.setString(i + 1, parameters[i]);

		}
		
		// execute the statement and get the result set
		resultSet = statement.executeQuery();
	
		// if we get this far everything is ok
		return new au.edu.ausstage.utils.DbObjects(statement, resultSet);
	
	} // end executePreparedStatement method
	
	/**
	 * A method to prepare and execute a prepared SQL statement to insert data
	 *
	 * @param sqlQuery   the SQL query to execute
	 * @param parameters an array of parameters, as strings, to pass into the parameter
	 *
	 * @return           true, if and only if, the insert worked
	 */
	public boolean executePreparedInsertStatement(String sqlQuery, String[] parameters) throws SQLException {
	
		// declare instance variables
		PreparedStatement statement;
			
		// build the statement
		statement = connection.prepareStatement(sqlQuery);
		
		// add the parameters
		for(int i = 0; i < parameters.length; i++) {
		
			// statements are indexed starting with 1
			// arrays are indexed starting with 0
			statement.setString(i + 1, parameters[i]);

		}
		
		// execute the statement and get the result set
		statement.executeUpdate();
		statement.close();

		// if we get this far everything is ok
		return true;
	
	} // end executePreparedStatement method
	
	/**
	 * A method to prepare and execute a prepared SQL statement to update data
	 *
	 * @param sqlQuery   the SQL query to execute
	 * @param parameters an array of parameters, as strings, to pass into the parameter
	 *
	 * @return           true, if and only if, the insert worked
	 */
	public boolean executePreparedUpdateStatement(String sqlQuery, String[] parameters) throws SQLException {
	
		// declare instance variables
		PreparedStatement statement;
	
		// build the statement
		statement = connection.prepareStatement(sqlQuery);
		
		// add the parameters
		for(int i = 0; i < parameters.length; i++) {
		
			// statements are indexed starting with 1
			// arrays are indexed starting with 0
			statement.setString(i + 1, parameters[i]);

		}
		
		// execute the statement and get the result set
		statement.executeUpdate();
		statement.close();
	
		// if we get this far everything is ok
		return true;
	
	} // end executePreparedStatement method
	
	/**
	 * A method to prepare and return a statement
	 *
	 * @param sqlQuery   the SQL query to prepare
	 *
	 * @return           the prepared statement
	 */
	public PreparedStatement prepareStatement(String sqlQuery) throws SQLException {
	
		return connection.prepareStatement(sqlQuery);
			
	}
	
	/**
	 * Finalize method to be run when the object is destroyed
	 * plays nice and free up Oracle connection resources etc. 
	 */
	protected void finalize() throws Throwable {
		try {
			connection.close();
		} catch(Exception e){}
	} // end finalize method
}
