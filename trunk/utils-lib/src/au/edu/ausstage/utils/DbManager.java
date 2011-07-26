/*
 * This file is part of the AusStage Utilities Package
 *
 * The AusStage Utilities Package is free software: you can redistribute
 * it and/or modify it under the terms of the GNU General Public License 
 * as published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * The AusStage Utilities Package is distributed in the hope that it will 
 * be useful, but WITHOUT ANY WARRANTY; without even the implied warranty 
 * of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with the AusStage Utilities Package.  
 * If not, see <http://www.gnu.org/licenses/>.
*/

package au.edu.ausstage.utils;

// import additional libraries
import java.sql.Connection;
import java.sql.Statement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import oracle.jdbc.pool.OracleDataSource;
/**
 * A class of methods useful when interacting with a Database
 */
public class DbManager {

	// declare private variables
	private String            connectionString;
	private OracleDataSource  dataSource;
	private Connection        connection;
	
	/**
	 * Constructor for this class
	 *
	 * @param connectionString the string used to connect to the database
	 */
	public DbManager(String connectionString) {
		// validate this parameter
		if(InputUtils.isValid(connectionString) == false) {
			throw new IllegalArgumentException("The connection string cannot be null or empty");
		} else {
			this.connectionString = connectionString;
		}
	} // end the constructor
	
	/**
	 * A method to connect to the database
	 *
	 * @param reConnect restablish a connection if required
	 * 
	 * @return true if, and only if, the connection was successful
	 */
	public boolean connect(boolean reConnect) {
		
		// is this a reconnection
		if(reConnect == true) {
			dataSource = null;
			connection = null;
		}
		
		// enclose code in a try block
		// return false if this doesn't work
		try {
		
			// do we need a new dataSource object
			if(dataSource == null) {
				// yes

				// construct a new Oracle DataSource object
				dataSource = new OracleDataSource();
				
				// set the connection string
				dataSource.setURL(connectionString);
			}
			
			// do we need a new connection?
			if(connection == null) {
			
				// get a connection
				connection = dataSource.getConnection();
				
			}
		} catch (java.sql.SQLException sqlEx) {
			// an error occured so return false and reset the objects
			dataSource = null;
			connection = null;
			return false;
		}
		
		// if we get here the connect worked
		return true;
	} // end connect Method
	
	/**
	 * A method to connect to the database
	 * 
	 * @return true if, and only if, the connection was successful
	 */
	public boolean connect() {
		return connect(false);
	}
			
	/**
	 * A method to execute an SQL statement and return a resultset
	 * 
	 * @param sqlQuery the SQL query to execute
	 *
	 * @return         the result set built from executing this query
	 */
	public au.edu.ausstage.utils.DbObjects executeStatement(String sqlQuery) {
	
		// declare instance variables
		ResultSet resultSet;
		Statement statement;
	
		// enclose code in a try block
		// throw a more general exception if required
		try {
		
			// check on required objects
			if(dataSource == null || connection == null || connection.isValid(5) == false) {
				
				// try to reconnect to the database
				if(connect(true) == false) {
					// tried to do a reconnect, will attempt again next time method is called
					return null;
				}
			}
			
			// build a statement
			statement = connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
			
			// execute the statement and get the result set
			resultSet = statement.executeQuery(sqlQuery);
						
		} catch (java.sql.SQLException sqlEx) {
			return null;
		}
		
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
	public au.edu.ausstage.utils.DbObjects executePreparedStatement(String sqlQuery, String[] parameters) {
	
		// declare instance variables
		ResultSet resultSet;
		PreparedStatement statement;
	
		// enclose code in a try block
		// throw a more general exception if required
		try {
		
			// check on required objects
			if(dataSource == null || connection == null || connection.isValid(5) == false) {
				// try to reconnect to the database
				if(connect(true) == false) {
					// tried to do a reconnect, will attempt again next time method is called
					return null;
				}
			}
			
			// build the statement
			statement = connection.prepareStatement(sqlQuery, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
			
			// add the parameters
			for(int i = 0; i < parameters.length; i++) {
			
				// statements are indexed starting with 1
				// arrays are indexed starting with 0
				statement.setString(i + 1, parameters[i]);

			}
			
			// execute the statement and get the result set
			resultSet = statement.executeQuery();			
			
		} catch (java.sql.SQLException sqlEx) {
			return null;
		}
	
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
	public boolean executePreparedInsertStatement(String sqlQuery, String[] parameters) {
	
		// declare instance variables
		PreparedStatement statement;
	
		// enclose code in a try block
		// throw a more general exception if required
		try {
		
			// check on required objects
			if(dataSource == null || connection == null || connection.isValid(5) == false) {
				// try to reconnect to the database
				if(connect(true) == false) {
					// tried to do a reconnect, will attempt again next time method is called
					return false;
				}
			}
			
			// build the statement
			statement = connection.prepareStatement(sqlQuery, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
			
			// add the parameters
			for(int i = 0; i < parameters.length; i++) {
			
				// statements are indexed starting with 1
				// arrays are indexed starting with 0
				statement.setString(i + 1, parameters[i]);

			}
			
			// execute the statement and get the result set
			statement.executeUpdate();
			statement.close();
			
		} catch (java.sql.SQLException sqlEx) {
			return false;
		}
	
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
	public boolean executePreparedUpdateStatement(String sqlQuery, String[] parameters) {
	
		// declare instance variables
		PreparedStatement statement;
	
		// enclose code in a try block
		// throw a more general exception if required
		try {
		
			// check on required objects
			if(dataSource == null || connection == null || connection.isValid(5) == false) {
				// try to reconnect to the database
				if(connect(true) == false) {
					// tried to do a reconnect, will attempt again next time method is called
					return false;
				}
			}
			
			// build the statement
			statement = connection.prepareStatement(sqlQuery, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
			
			// add the parameters
			for(int i = 0; i < parameters.length; i++) {
			
				// statements are indexed starting with 1
				// arrays are indexed starting with 0
				statement.setString(i + 1, parameters[i]);

			}
			
			// execute the statement and get the result set
			statement.executeUpdate();
			statement.close();
			
		} catch (java.sql.SQLException sqlEx) {
			return false;
		}
	
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
	public PreparedStatement prepareStatement(String sqlQuery) {
	
		// enclose code in a try block
		// throw a more general exception if required
		try {
		
			// check on required objects
			if(dataSource == null || connection == null || connection.isValid(5) == false) {
				// try to reconnect to the database
				if(connect(true) == false) {
					// tried to do a reconnect, will attempt again next time method is called
					return null;
				}
			}
			
			// build the statement
			//return connection.prepareStatement(sqlQuery, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
			return connection.prepareStatement(sqlQuery);
			
		} catch (java.sql.SQLException sqlEx) {
			return null;
		}
	}
	
	/**
	 * Finalize method to be run when the object is destroyed
	 * plays nice and free up Oracle connection resources etc. 
	 */
	 protected void finalize() throws Throwable {
		try {
			connection.close();
			dataSource = null;
		} catch(Exception e){}
	} // end finalize method

	//release database resources as the garbage collector finalize() is not guaranteed to run at any specific time. 
	//In general it's best not to rely on finalize() to do any cleaning up etc.
	public void cleanup(){
		try {
			connection.close();
			dataSource = null;
		} catch(Exception e){}		
	}
	
} // end class definition
