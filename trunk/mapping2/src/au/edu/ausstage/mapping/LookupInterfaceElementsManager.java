/*
 * This file is part of the AusStage Mapping Service
 *
 * The AusStage Mapping Service is free software: you can redistribute
 * it and/or modify it under the terms of the GNU General Public License 
 * as published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * The AusStage Mapping Service is distributed in the hope that it will 
 * be useful, but WITHOUT ANY WARRANTY; without even the implied warranty 
 * of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with the AusStage Mapping Service.  
 * If not, see <http://www.gnu.org/licenses/>.
 */
 
package au.edu.ausstage.mapping;

// import additional AusStage libraries
import au.edu.ausstage.utils.*;
import au.edu.ausstage.mapping.types.*;

// import additional java packages / classes
import java.sql.ResultSet;
import org.json.simple.*;

/**
 * A class used to lookup data used for user interface elements 
 */
public class LookupInterfaceElementsManager {

	// declare private class variables
	DbManager database;
	
	/**
	 * Constructor for this class
	 *
	 * @param database a valid DbManager object
	 */
	public LookupInterfaceElementsManager(DbManager database) {
		this.database = database;
	}
	
	/**
	 * A method to lookup all the possible valid state values for use in the UI
	 *
	 * @return a JSON encoded string containing the data
	 */
	@SuppressWarnings("unchecked")
	public String getStateList() {
	
		// define some helper variables
		JSONArray list    = new JSONArray();
		JSONObject object = new JSONObject();
		
		// build the list of states
		object.put("id", 99);
		object.put("name", "Australia");
		list.add(object);
		
		object = new JSONObject();
		object.put("id", 7);
		object.put("name", "Australian Capital Territory");
		list.add(object);
		
		object = new JSONObject();
		object.put("id", 3);
		object.put("name", "New South Wales");
		list.add(object);
		
		object = new JSONObject();
		object.put("id", 8);
		object.put("name", "Northern Territory");
		list.add(object);
		
		object = new JSONObject();
		object.put("id", 4);
		object.put("name", "Queensland");
		list.add(object);
		
		object = new JSONObject();
		object.put("id", 1);
		object.put("name", "South Australia");
		list.add(object);
		
		object = new JSONObject();
		object.put("id", 5);
		object.put("name", "Tasmania");
		list.add(object);
		
		object = new JSONObject();
		object.put("id", 6);
		object.put("name", "Victoria");
		list.add(object);
		
		object = new JSONObject();
		object.put("id", 2);
		object.put("name", "Western Australia");
		list.add(object);
		
		object = new JSONObject();
		object.put("id", 999);
		object.put("name", "International");
		list.add(object);
		
		// return the JSON data
		return list.toString();		
		
	} // end the getStateList method
	
	/**
	 * A method to lookup all of the suburbs given a particular state identifier
	 *
	 * @param stateId the unique identifier of the state
	 *
	 * @return a JSON encoded string containing the data
	 */
	@SuppressWarnings("unchecked")
	public String getSuburbList(String stateId) {
	
		// double check the parameters
		if(InputUtils.isValid(stateId, LookupServlet.VALID_STATES) == false) {
			throw new IllegalArgumentException("Missing id parameter. Expected one of: " + InputUtils.arrayToString(LookupServlet.VALID_STATES));
		}
		
		// declare helper variables
		JSONArray list    = new JSONArray();
		JSONObject object = null;
		
		// define the sql
		String sql = "SELECT TRIM(suburb), COUNT(suburb) as venue_count, COUNT(latitude) as can_be_mapped "
				   + "FROM venue "
				   + "WHERE state = ? "
				   + "AND suburb IS NOT NULL "
				   + "GROUP BY TRIM(suburb) "
				   + "ORDER BY TRIM(suburb) ";
		
		String[] sqlParameters = {stateId};
		
		// get the data
		DbObjects results = database.executePreparedStatement(sql, sqlParameters);
		
		// check to see that data was returned
		if(results == null) {
			// return an empty JSON Array
			return list.toString();
		}
		
		// build the result data
		ResultSet resultSet = results.getResultSet();
		
		try {
		
			// loop through the dataset
			while(resultSet.next() == true) {
			
				// declare a new object
				object = new JSONObject();
				
				// add the data
				object.put("name", resultSet.getString(1));
				object.put("venueCount", resultSet.getString(2));
				object.put("mapVenueCount", resultSet.getString(3));
				
				// add the object to the list
				list.add(object);			
			}
		
		} catch (java.sql.SQLException ex) {
			// return an empty JSON Array
			return list.toString();
		}
		
		// play nice and tidy up
		resultSet = null;
		results.tidyUp();
		results = null;
		
		// return the data
		return list.toString();
	
	} // end the getSuburbList method
	
	/**
	 * A method to lookup all of the venues given a particular suburb
	 * 
	 * @param suburbName the name of the suburb
	 *
	 * @return a JSON encoded string containing the data
	 */
	@SuppressWarnings("unchecked")
	public String getVenueListBySuburb(String suburbName) {
	
		// declare helper variables
		String[] sqlParameters = null;
	
		// validate the input
		if(InputUtils.isValid(suburbName) == false) {
			throw new IllegalArgumentException("The suburbName parameter is required");
		} else if(suburbName.indexOf("_") == -1) {
			throw new IllegalArgumentException("The suburbName parameter is required to have a state code followed by a suburb name seperated by a \"_\" character");
		} else {
			sqlParameters = suburbName.split("_");
			if(sqlParameters.length > 2) {
				throw new IllegalArgumentException("The suburbName parameter is required to have a state code followed by a suburb name seperated by a \"_\" character");
			} else {
				if(InputUtils.isValid(sqlParameters[0], LookupServlet.VALID_STATES) == false) {
					throw new IllegalArgumentException("Invalid state code. Expected one of: " + InputUtils.arrayToString(LookupServlet.VALID_STATES));
				}
			}
		}
		
		// declare helper variables
		JSONArray list    = new JSONArray();
		JSONObject object = null;
		
		// define the sql
		String sql = "SELECT venue.venueid, venue_name, latitude, COUNT(events.eventid) "
				   + "FROM venue, events "
				   + "WHERE state = ? "
				   + "AND TRIM(suburb) = ? "
				   + "AND venue.venueid = events.venueid "
				   + "GROUP BY venue.venueid, venue_name, latitude "
				   + "ORDER BY venue_name ";
		
		// get the data
		DbObjects results = database.executePreparedStatement(sql, sqlParameters);
		
		// check to see that data was returned
		if(results == null) {
			// return an empty JSON Array
			return list.toString();
		}
		
		// build the result data
		ResultSet resultSet = results.getResultSet();
		
		try {
		
			// loop through the dataset
			while(resultSet.next() == true) {
			
				// declare a new object
				object = new JSONObject();
				
				// add the data
				object.put("id", resultSet.getString(1));
				object.put("name", resultSet.getString(2));
				object.put("venueUrl", LinksManager.getVenueLink(resultSet.getString(1)));
				object.put("eventCount", resultSet.getString(4));
				
				if(resultSet.getString(3) == null) {
					object.put("mapEventCount", "0");
				} else {
					object.put("mapEventCount", resultSet.getString(4));
				}
				
				// add the object to the list
				list.add(object);			
			}
		
		} catch (java.sql.SQLException ex) {
			// return an empty JSON Array
			return list.toString();
		}
		
		// play nice and tidy up
		resultSet = null;
		results.tidyUp();
		results = null;
		
		// return the data
		return list.toString();
		
	} // end the getVenueListBySuburb method	 
	
} // end class definition
