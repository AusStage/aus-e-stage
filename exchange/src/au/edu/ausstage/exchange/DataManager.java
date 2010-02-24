/*
 * This file is part of AusStage Data Exchange Service
 *
 * AusStage Data Exchange Service is free software: you can redistribute
 * it and/or modify it under the terms of the GNU General Public License 
 * as published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * AusStage Data Exchange Service is distributed in the hope that it will 
 * be useful, but WITHOUT ANY WARRANTY; without even the implied warranty 
 * of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with AusStage Data Exchange Service.  
 * If not, see <http://www.gnu.org/licenses/>.
*/
 
package au.edu.ausstage.exchange;

// import additional classes
import java.sql.*;
import oracle.jdbc.pool.OracleDataSource;
import javax.servlet.*;

/**
 * A class used to Manage access to the underlying database
 */

public class DataManager {

	// declare private variables
	private String            connectionString;
	private OracleDataSource  dataSource;
	private Connection        connection;
	private Statement         statement;
	private PreparedStatement preparedStatement;
	private ServletConfig     servletConfig;
	
	/**
	 * Constructor for this class
	 *
	 * @param config the servlet configuration object for the running servlet
	 */
	public DataManager(ServletConfig config) {
	
		// store a reference to this object
		this.servletConfig = config;
	
		// store connection string for later use
		//connectionString = this.servletConfig.getInitParameter("databaseConnectionString");
		connectionString = config.getServletContext().getInitParameter("databaseConnectionString");
	}
	
	/**
	 * A method to create a connection to the database
	 */
	public void connect() throws javax.servlet.ServletException {
	
		// enclose code in a try block
		// throw a more general exception if required
		try {
		
			// do we need a new dataSource object
			if(this.dataSource == null) {
				// yes

				// construct a new Oracle DataSource object
				this.dataSource = new OracleDataSource();
				
				// set the connection string
				this.dataSource.setURL(this.connectionString);
			}
			
			// do we need a new connection?
			if(this.connection == null) {
			
				// get a connection
				this.connection = this.dataSource.getConnection();
				
			}
		} catch (java.sql.SQLException sqlEx) {
			throw new javax.servlet.ServletException("Unable to connect to Database", sqlEx);
		}	
	} // end connect Method
	
	/**
	 * A method to execute an SQL statement and return a resultset
	 * 
	 * @param sqlQuery the SQL query to execute
	 *
	 * @return         the result set built from executing this query
	 */
	public java.sql.ResultSet executeStatement(String sqlQuery) throws javax.servlet.ServletException {
	
		// declare instance variables
		ResultSet resultSet;
	
		// enclose code in a try block
		// throw a more general exception if required
		try {
		
			// check on required objects
			if(dataSource == null || connection == null) {
				
				// get a connection
				this.connect();
			}
			
			// build a statement
			this.statement = connection.createStatement();
			
			// execute the statement and get the result set
			resultSet = this.statement.executeQuery(sqlQuery);
			
		} catch (java.sql.SQLException sqlEx) {
			throw new javax.servlet.ServletException("Unable to execute an SQL Query", sqlEx);
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
	public java.sql.ResultSet executePreparedStatement(String sqlQuery, String[] parameters) throws javax.servlet.ServletException {
	
		// declare instance variables
		ResultSet resultSet;
	
		// enclose code in a try block
		// throw a more general exception if required
		try {
		
			// check on required objects
			if(dataSource == null || connection == null) {
				
				// get a connection
				this.connect();
			}
			
			// build the statement
			this.preparedStatement = connection.prepareStatement(sqlQuery);
			
			// add the parameters
			for(int i = 0; i < parameters.length; i++) {
			
				// statements are indexed starting with 1
				// arrays are indexed starting with 0
				this.preparedStatement.setString(i + 1, parameters[i]);

			}
			
			// execute the statement and get the result set
			resultSet = this.preparedStatement.executeQuery();
			
		} catch (java.sql.SQLException sqlEx) {
			throw new javax.servlet.ServletException("Unable to execute an SQL Query", sqlEx);
		}
	
		return resultSet;
	
	} // end executePreparedStatement method
	
	/**
	 * A method to close the currently open Statement and PreparedStatement objects held by this object
	 */
	public void closeStatement() {
	
		try{
			
			if(this.statement != null) {
				this.statement.close();
			}
			
			if(this.preparedStatement != null) {
				this.preparedStatement.close();
			}
		} catch (java.sql.SQLException ex) {
			// don't do anything as when execution stops objects will be closed / destroyed anyway
			// just playing nice by explicitly closing resources when we don't need them
		}
	
	} // end closeStatement method
	
	/**
	 * A method used to get the value of a parameter from the ServletConfig object
	 * of the current servlet
	 *
	 * @param name the name of the parameter
	 *
	 * @return     value of the parameter as a string
	 */
	public String getContextParam(String name) {
	
		return this.servletConfig.getServletContext().getInitParameter(name);
	}
	 

} // end class definition
