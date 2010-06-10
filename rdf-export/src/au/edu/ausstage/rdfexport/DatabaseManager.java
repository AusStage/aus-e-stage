/*
 * This file is part of the AusStage RDF Export App
 *
 * The AusStage RDF Export App is free software: you can 
 * redistribute it and/or modify it under the terms of the GNU General 
 * Public License as published by the Free Software Foundation, either 
 * version 3 of the License, or (at your option) any later version.
 *
 * The AusStage RDF Export App is distributed in the hope 
 * that it will be useful, but WITHOUT ANY WARRANTY; without even the 
 * implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  
 * See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with the AusStage RDF Export App.  
 * If not, see <http://www.gnu.org/licenses/>.
*/

// include the class in our package
package au.edu.ausstage.rdfexport;

// import additional packages
import java.sql.*;
import oracle.jdbc.pool.OracleDataSource;


/**
 * A class to manage access to the database
 */
public class DatabaseManager {

	// declare private variables
	private OracleDataSource  dataSource;
	private Connection        connection;
	private Statement         statement;
	private PreparedStatement preparedStatement;
	
	/**
	 * A method to connect to the database
	 *
	 * @param connectionString the string used to connect to the database
	 * 
	 * @return true if, and only if, the connection was successful
	 */
	public boolean connect(String connectionString) {
	
		// enclose code in a try block
		// return false if this doesn't work
		try {
		
			// do we need a new dataSource object
			if(this.dataSource == null) {
				// yes

				// construct a new Oracle DataSource object
				this.dataSource = new OracleDataSource();
				
				// set the connection string
				this.dataSource.setURL(connectionString);
			}
			
			// do we need a new connection?
			if(this.connection == null) {
			
				// get a connection
				this.connection = this.dataSource.getConnection();
				
			}
		} catch (java.sql.SQLException sqlEx) {
			// output some diagnostic information
			System.err.println("ERROR: Unable to connect to the database");
			System.err.println("       " + sqlEx.getMessage());
			return false;
		}
		
		// if we get here the connect worked
		return true;
	} // end connect Method
	
	/**
	 * A method to execute an SQL statement and return a resultset
	 * 
	 * @param sqlQuery the SQL query to execute
	 *
	 * @return         the result set built from executing this query
	 */
	public java.sql.ResultSet executeStatement(String sqlQuery) {
	
		// declare instance variables
		ResultSet resultSet;
	
		// enclose code in a try block
		// throw a more general exception if required
		try {
		
			// check on required objects
			if(dataSource == null || connection == null || connection.isValid(5) == false) {
				
				// report the error
				throw new java.sql.SQLException("A valid database connection was not available");
			}
			
			// build a statement
			this.statement = connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
			
			// execute the statement and get the result set
			resultSet = this.statement.executeQuery(sqlQuery);
			
		} catch (java.sql.SQLException sqlEx) {
			System.err.println("ERROR: Unable to execute an SQL Query");
			System.err.println("       " + sqlEx.getMessage());
			return null;
		}
	
		return resultSet;
	
	} // end executeStatement method
	
	/**
	 * A method to prepare and execute a prepared SQL statement and return a resultset
	 *
	 * @param sqlQuery   the SQL query to execute
	 * @param parameters an array of parameters, as strings, to pass into the parameter
	 *
	 * @return           the result set built from executing this query
	 */
	public java.sql.ResultSet executePreparedStatement(String sqlQuery, String[] parameters) {
	
		// declare instance variables
		ResultSet resultSet;
	
		// enclose code in a try block
		// throw a more general exception if required
		try {
		
			// check on required objects
			if(dataSource == null || connection == null || connection.isValid(5) == false) {
				
				// report the error
				throw new java.sql.SQLException("A valid database connection was not available");
			}
			
			// build the statement
			this.preparedStatement = connection.prepareStatement(sqlQuery, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
			
			// add the parameters
			for(int i = 0; i < parameters.length; i++) {
			
				// statements are indexed starting with 1
				// arrays are indexed starting with 0
				this.preparedStatement.setString(i + 1, parameters[i]);

			}
			
			// execute the statement and get the result set
			resultSet = this.preparedStatement.executeQuery();
			
		} catch (java.sql.SQLException sqlEx) {
			System.err.println("ERROR: Unable to execute an SQL Query");
			System.err.println("       " + sqlEx.getMessage());
			return null;
		}
	
		// if we get this far everything is ok
		return resultSet;
	
	} // end executePreparedStatement method
	
	/**
	 * Finalize method to be run when the object is destroyed
	 * plays nice and free up Oracle connection resources etc. 
	 */
	protected void finalize() throws Throwable {
		try {
			this.preparedStatement.close();
		} catch(Exception e){}
		
		try {
			this.statement.close();
		} catch(Exception e){}
		
		try {
			this.connection.close();
		} catch(Exception e){}
	} // end finalize method
	
} // end class definition
