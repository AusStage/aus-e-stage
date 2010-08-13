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
import java.sql.Statement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.ArrayList;

/**
 * A class of methods to manage the database objects
 */
public class DbObjects {

	// declare private class variables
	private Statement         statement         = null;
	private PreparedStatement preparedStatement = null;
	private ResultSet         resultSet         = null;
	
	/**
	 * A constructor for this class
	 *
	 * @param statement the statement related to this result set
	 * @param resultSet the resultSet related to the query
	 */
	public DbObjects(Statement statement, ResultSet resultSet) {
		this.statement = statement;
		this.resultSet = resultSet;
	}
	
	/**
	 * A constructor this class
	 *
	 * @param statement the PreparedStatement related to this ResultSet
	 * @param resultSet         the resultSet related to the query
	 */
	public DbObjects(PreparedStatement statement, ResultSet resultSet) {
		this.preparedStatement = statement;
		this.resultSet = resultSet;
	}
	
	/**
	 * A method to get the Statement related to this query
	 *
	 * @return the Statement 
	 */
	public Statement getStatement() {
		return statement;
	}
	
	/**
	 * A method to get the PreparedStatement related to this query
	 *
	 * @return the PreparedStatement 
	 */
	public PreparedStatement getPreparedStatement() {
		return preparedStatement;
	}
	
	/**
	 * A method to get the Resulset related this query
	 *
	 * @return the ResultSet
	 */
	public ResultSet getResultSet() {
		return resultSet;
	}
	
	/**
	 * A method to get a column as an array list
	 *
	 * @param index the column index to use to populate the ArrayList
	 *
	 * @return      an array list containing all of the items in the column
	 */
	public ArrayList<String> getColumn(int index) {
	
		try {
	
			// get the metadata for this results
			ResultSetMetaData metadata = resultSet.getMetaData();
		
			// double check the index
			if(InputUtils.isValidInt(index, 1, metadata.getColumnCount()) == false) {
				return null;
			} else {
			
				// define a list to store the column
				ArrayList<String> list = new ArrayList<String>();
			
				// loop through the 
				while (resultSet.next()) {
					
					// add the value from the column to the list
					list.add(resultSet.getString(index));
				}
				
				// reset the cursor on the resultSet
				resultSet.first();
				
				// check on the array list
				if(list.isEmpty() == true) {
					return null;
				} else {
					return list;
				}
			}
			
		} catch (java.sql.SQLException sqlEx) {
			return null;
		}
	} // end the getColumn method
	
	/**
	 * A convenience method to play nice and tidy up any used resources
	 */
	public void tidyUp() {
		try {
			this.preparedStatement.close();
		} catch(Exception e){}
		
		try {
			this.statement.close();
		} catch(Exception e){}
		
		try {
			this.resultSet.close();
		} catch(Exception e){}
	}
	
	/**
	 * Finalize method to be run when the object is destroyed
	 * plays nice and free up Oracle connection resources etc. 
	 */
	protected void finalize() throws Throwable {
		this.tidyUp();
	}
}
