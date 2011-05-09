package au.edu.ausstage.utils;

//import additional libraries
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.ResultSet;

import oracle.jdbc.pool.OracleDataSource;

public class DatabaseManager {
	
	public String            connectionString = null;
	public OracleDataSource  dataSource = null;
	public Connection        connection = null;
	public ResultSet 		resultSet = null;
	public Statement 		statement = null;
	public PreparedStatement 		preparedstatement = null;	
	
	public DatabaseManager(String connectionString) {

		if(InputUtils.isValid(connectionString) == false) {
			throw new IllegalArgumentException("The connection string cannot be null or empty");

		} else {			
			this.connectionString = connectionString;							
		}
	} 
	
	public boolean connect() {
		
		try {
		
			// construct a new Oracle DataSource object
			dataSource = new OracleDataSource();
				
			// set the connection string
			dataSource.setURL(connectionString);
			
			// get a connection
			connection = dataSource.getConnection();
			//System.out.println("The driver is " + connection.getMetaData().getDriverVersion() );
			//System.out.println("The DBMS is " + connection.getMetaData().getDatabaseProductVersion() );
							
		} catch (java.sql.SQLException sqlEx) {
			System.out.println(sqlEx);
			// an error occured so return false and reset the objects
			dataSource = null;
			connection = null;
			return false;
		}
		
		// if we get here the connect worked
		return true;
	} // end connect Method


	public ResultSet exeStatement(String sqlQuery) {
		
		try {			
			// build a statement
			statement = connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
			
			// execute the statement and get the result set
			resultSet = statement.executeQuery(sqlQuery);
			
			return resultSet;
			
		} catch (java.sql.SQLException sqlEx) {
			System.out.println(sqlEx);
			return null;
		}					
	}
	
	public ResultSet exePreparedStatement(String sqlQuery, int[] param) {
		
		try {			
			preparedstatement = connection.prepareStatement(sqlQuery, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
			
			for(int i = 0; i < param.length; i++) {			
				// statements are indexed starting with 1
				// arrays are indexed starting with 0
				preparedstatement.setInt(i + 1, param[i]);
			}			
			// execute the statement and get the result set
			resultSet = preparedstatement.executeQuery();
			
			return resultSet;
			
		} catch (java.sql.SQLException sqlEx) {
			System.out.println(sqlEx);
			return null;
		}					
	}
	
	public ResultSet exePreparedINStatement(String sqlQuery, int[] param, int start, int batchLength) {
		
		try {
			preparedstatement = connection.prepareStatement(sqlQuery, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
			
			for(int i = 0; i < batchLength; i++) {				
				// statements are indexed starting with 1
				// arrays are indexed starting with 0
				preparedstatement.setInt(i + 1, param[start + i]);
			}			
			// execute the statement and get the result set
			resultSet = preparedstatement.executeQuery();
			
			return resultSet;
			
		} catch (java.sql.SQLException sqlEx) {
			System.out.println(sqlEx);
			return null;
		}					
	}
	
	public void finalize(){
		if (resultSet != null) {
			try {
				resultSet.close();
			} catch (SQLException ex) {
				System.out.println(ex);
			}

			resultSet = null;
		}

		if (statement != null) {
			try {
				statement.close();
			} catch (SQLException ex) {
				System.out.println(ex);
			}

			statement = null;
		}
		
		if (preparedstatement != null) {
			try {
				preparedstatement.close();
			} catch (SQLException ex) {
				System.out.println(ex);
			}

			preparedstatement = null;
		}

	}
	
	public void closeDB() throws Throwable {
		try {
			connection.close();
			dataSource = null;
		} catch(Exception e){}
	} 
	
}
