package au.edu.ausstage.networks;

//import additional libraries
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import au.edu.ausstage.networks.types.Event;
import au.edu.ausstage.utils.InputUtils;

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
	
	//set parameters in sql, and return integer set such as eventID set or contributorId set
	public Set<Integer> getResultfromDB(String sql, int ID){
		Set<Integer> infoSet = new HashSet<Integer>();
		int[] param = {ID};
		
		//execute sql statement
		ResultSet results = exePreparedStatement(sql, param);				
		
		try {			
			// 	check to see that data was returned
			if (!results.last()){	
				tidyup();
				return null;
			}else
				results.beforeFirst();
		
			// 	build the list of contributors			
			while(results.next() == true) {
				infoSet.add(results.getInt(1));									
			}									
		} catch (java.sql.SQLException ex) {	
			System.out.println("Exception: " + ex.getMessage());
			results = null;
		}
		
		tidyup();
		return infoSet;
	}
	
	public List<Event> selectBatchingEventDetails(Set<Integer> set){
		int[] evtIDArray = new int[set.size()];
		int x = 0;
		for (Integer eID : set) evtIDArray[x++] = eID;

		int SINGLE_BATCH = 1;
		int SMALL_BATCH = 4;
		int MEDIUM_BATCH = 11;
		int LARGE_BATCH = 51;
		int start = 0;
		int totalNumberOfValuesLeftToBatch = set.size();
		Event evt = null;
		List<Event> evtList = new ArrayList<Event>();;
		int j = 0;
		
		while ( totalNumberOfValuesLeftToBatch > 0 ) {
			
			int batchSize = SINGLE_BATCH;
			if ( totalNumberOfValuesLeftToBatch >= LARGE_BATCH ) {
			  batchSize = LARGE_BATCH;
			} else if ( totalNumberOfValuesLeftToBatch >= MEDIUM_BATCH ) {
			  batchSize = MEDIUM_BATCH;
			} else if ( totalNumberOfValuesLeftToBatch >= SMALL_BATCH ) {
			  batchSize = SMALL_BATCH;
			}
			
			String inClause = new String("");			
			for (int i=0; i < batchSize; i++) {
				inClause = inClause + "? ,";
			}
			inClause = inClause.substring(0, inClause.length()-1);
			
			String sql = "SELECT DISTINCT e.eventid, e.event_name, e.first_date, v.venue_name, v.suburb, s.state, c.countryname "
				+ "FROM events e, venue v, states s, country c "
				+ "WHERE e.eventid in (" + inClause + ") "
				+ "AND e.venueid = v.venueid "
				+ "AND v.state = s.stateid (+) "
				+ "AND v.countryid = c.countryid (+) "	
				+ "ORDER BY e.first_date";
	
			ResultSet results = exePreparedINStatement(sql, evtIDArray, start, batchSize);
			
			try{
				if (!results.last()) {
					tidyup();
					return null;
				} else
				results.beforeFirst();
						
				//build the event object
				while (results.next() == true) {
					int e_id = results.getInt(1);
					String name = results.getString(2);
					String date = results.getDate(3).toString();
					String venue = results.getString(4);
					String suburb = results.getString(5);
					String state = results.getString(6);
					String country = results.getString(7);
					String venueDetail = venue;
					
					evt = new Event(Integer.toString(e_id));
					// evt.setId(resultSet.getString(1));
					evt.setMyName(name);
					evt.setMyFirstDate(date);	
					if (suburb != null && !suburb.isEmpty())
						venueDetail = venueDetail + ", " + suburb;
					if (country == null && state != null) 
						venueDetail = venueDetail + ", " + state;
					if (country != null && state != null && !state.isEmpty() && country.equalsIgnoreCase("Australia"))
						venueDetail = venueDetail + ", " + state;
					if (country != null && !country.isEmpty() && !country.equalsIgnoreCase("Australia"))
						venueDetail = venueDetail + ", " + country;
					
					evt.setVenue(venueDetail);
					evtList.add(evt);
					//evtId_evtObj_map.put(e_id, evt);
					j++;
				}
				
				tidyup();
			} catch (SQLException e) {
				results = null;
				tidyup();
				e.printStackTrace();
			}
			totalNumberOfValuesLeftToBatch -= batchSize; 			
			start += batchSize;
		}
					
		return evtList;
	}
	
	public void tidyup(){
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
			if (connection != null)
				connection.close();
			dataSource = null;
		} catch(Exception e){}
	} 
	
}
