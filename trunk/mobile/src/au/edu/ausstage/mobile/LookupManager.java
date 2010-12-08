/*
 * This file is part of the AusStage Navigating Networks Service
 *
 * The AusStage Navigating Networks Service is free software: you can redistribute
 * it and/or modify it under the terms of the GNU General Public License 
 * as published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * The AusStage Navigating Networks Service is distributed in the hope that it will 
 * be useful, but WITHOUT ANY WARRANTY; without even the implied warranty 
 * of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with the AusStage Navigating Networks Service.  
 * If not, see <http://www.gnu.org/licenses/>.
*/

package au.edu.ausstage.mobile;

// import additional AusStage libraries
import au.edu.ausstage.utils.*;

// import additional java packages / classes
import java.sql.ResultSet;
import org.json.simple.*;
import org.json.simple.parser.*;

/**
 * A class to manage the lookup of information
 */
public class LookupManager {

	// declare private class variables
	private DbManager database;
	
	/**
	 * Constructor for this class
	 *
	 * @param database a valid DbManager object
	 */
	public LookupManager(DbManager database) {
	
		// double check the parameter
		if(database == null) {
			throw new IllegalArgumentException("The database parameter cannot be null");
		}
		this.database = database;
	}
	
	/**
	 * A method to lookup the Feedback Source Types
	 *
	 * @param formatType the type of format used to represent the data
	 *
	 * @return a JSON encoded string containing details about the feedback source types
	 */
	@SuppressWarnings("unchecked")
	public String getFeedbackSourceTypes(String formatType) {
	
		// check the input parameter
		if(InputUtils.isValid(formatType) == false) {
			throw new IllegalArgumentException("The formatType parameter is required");
		} else {
			if(formatType.equals("json") == false) {
				throw new IllegalArgumentException("The only supported format at this time is json");
			}
		}
	
		// declare helper variables
		JSONObject object = null;
		JSONArray  list   = new JSONArray();
		
		// build the sql
		String sql = "SELECT source_type, source_name, source_description "
				   + "FROM mob_source_types "
				   + "ORDER BY source_type ";
				   
		// execute the sql
		DbObjects results = database.executeStatement(sql);
		ResultSet resultSet = results.getResultSet();
		
		//loop through the resultSet
		try {
			while(resultSet.next()) {
		
				// create a new JSONObject
				object = new JSONObject();
		
				// add data to the object
				object.put("id", resultSet.getString(1));
				object.put("name", resultSet.getString(2));
				object.put("description", resultSet.getString(3));
		
				// add the object to the array
				list.add(object);
			}
		} catch(java.sql.SQLException ex) {
			return new JSONArray().toString();
		}
		
		// play nice and tidy up
		results.tidyUp();
		results = null;
		
		// return the data
		return list.toString();
	
	} // end getFeedbackSourceTypes method
	
	/**
	 * A method to lookup the Feedback Source Types
	 *
	 * @param id         the unique identifier of a performance
	 * @param formatType the type of format used to represent the data
	 *
	 * @return a string containg details of a performance
	 */
	@SuppressWarnings("unchecked")
	public String getPerformanceDetails(String id, String formatType) {
	
		// check the input parameters
		if(InputUtils.isValid(formatType) == false) {
			throw new IllegalArgumentException("The formatType parameter is required");
		} else {
			if(formatType.equals("json") == false) {
				throw new IllegalArgumentException("The only supported format at this time is json");
			}
		}
		
		if(InputUtils.isValidInt(id) == false) {
			throw new IllegalArgumentException("The performance id is required");
		}
		
		// declare helper variables
		JSONObject object = null;
		
		// build the sql
		String sql = "SELECT e.event_name, e.eventid, mq.question, o.name, o.organisationid, v.venue_name, v.venueid, TO_CHAR(mp.start_date_time, 'DD-MON-YYYY HH24:MI:SS'), TO_CHAR(mp.end_date_time, 'DD-MON-YYYY HH24:MI:SS')"
				   + "FROM mob_performances mp, events e, mob_questions mq, orgevlink oe, organisation o, mob_organisations mo, venue v "
				   + "WHERE mp.performance_id = ? "
				   + "AND mp.event_id = e.eventid "
				   + "AND mp.question_id = mq.question_id "
				   + "AND e.eventid = oe.eventid "
				   + "AND o.organisationid = oe.organisationid "
				   + "AND mo.organisation_id = o.organisationid "
				   + "AND e.venueid = v.venueid ";
				   
		// build parameters
		String[] sqlParameters = new String[1];
		sqlParameters[0] = id;
				   
		// execute the sql
		DbObjects results = database.executePreparedStatement(sql, sqlParameters);
		ResultSet resultSet = results.getResultSet();
		
		//loop through the resultSet
		try {
			if(resultSet.next() == true) {
		
				// create a new JSONObject
				object = new JSONObject();
		
				// add data to the object
				object.put("id", Integer.parseInt(id));
				object.put("event"    , resultSet.getString(1));
				object.put("eventUrl", LinksManager.getEventLink(resultSet.getString(2)));
				object.put("question", resultSet.getString(3));
				object.put("organisation", resultSet.getString(4));
				object.put("organisationUrl", LinksManager.getOrganisationLink(resultSet.getString(5)));
				object.put("venue", resultSet.getString(6));
				object.put("venueUrl", LinksManager.getVenueLink(resultSet.getString(7)));
				object.put("startDateTime", resultSet.getString(8));
				object.put("endDateTime", resultSet.getString(9));

			} else {
				return new JSONObject().toString();
			}
		} catch(java.sql.SQLException ex) {
			return new JSONObject().toString();
		}
		
		// play nice and tidy up
		results.tidyUp();
		results = null;
		
		// return the data
		return object.toString();
	
	} // end getPerformanceDetails method
	
	/**
	 * A method to lookup the details of a question
	 *
	 * @param id         the unique identifier of a question
	 * @param formatType the type of format used to represent the data
	 *
	 * @return a string containing details of the question
	 */
	@SuppressWarnings("unchecked")
	public String getQuestionDetails(String id, String formatType) {
	
		// check the input parameters
		if(InputUtils.isValid(formatType) == false) {
			throw new IllegalArgumentException("The formatType parameter is required");
		} else {
			if(formatType.equals("json") == false) {
				throw new IllegalArgumentException("The only supported format at this time is json");
			}
		}
		
		if(InputUtils.isValidInt(id) == false) {
			throw new IllegalArgumentException("The performance id is required");
		}
		
		// declare helper variables
		JSONObject object = null;
		
		// build the sql
		String sql = "SELECT question_id, question, question_notes "
				   + "FROM mob_questions "
				   + "WHERE question_id = ?";
				   
		// build parameters
		String[] sqlParameters = new String[1];
		sqlParameters[0] = id;
				   
		// execute the sql
		DbObjects results = database.executePreparedStatement(sql, sqlParameters);
		ResultSet resultSet = results.getResultSet();
		
		//loop through the resultSet
		try {
			if(resultSet.next() == true) {
		
				// create a new JSONObject
				object = new JSONObject();
		
				// add data to the object
				object.put("id"   , Integer.parseInt(id));
				object.put("text" , resultSet.getString(2));
				object.put("notes", resultSet.getString(3));
				
			} else {
				return new JSONObject().toString();
			}
		} catch(java.sql.SQLException ex) {
			return new JSONObject().toString();
		}
		
		// play nice and tidy up
		results.tidyUp();
		results = null;
		
		// return the data
		return object.toString();
	
	} // end getPerformanceDetails method
	
	/**
	 * A method to get a list of performances at venues within a specified distance
	 *
	 * @param latitude  a latitude coordinate in decimal notation
	 * @param longitude a longitude coordinate in decimal notation
	 * @param distance  the distance to add in metres
	 *
	 * @return          a string containing the details of any found performances
	 */
	@SuppressWarnings("unchecked")
	public String getPerformanceByLocation(String latitude, String longitude, String distance) {
	
		// first round of validation
		if(InputUtils.isValid(latitude) == false || InputUtils.isValid(longitude) == false || InputUtils.isValid(distance) == false) {
			throw new IllegalArgumentException("Missing parameters, expect lat, lng and distance");
		} else {
			// second round validation
			if(CoordinateManager.isValidLatitude(Float.valueOf(latitude)) == false) {
				throw new IllegalArgumentException("The value of the latitude parameter did not pass validation as defined by the CoordinateManager class");
			}
			
			if(CoordinateManager.isValidLongitude(Float.valueOf(longitude)) == false) {
				throw new IllegalArgumentException("The value of the longitude parameter did not pass validation as defined by the CoordinateManager class");
			}
			
			if(InputUtils.isValidInt(distance) == false) {
				throw new IllegalArgumentException("The value of the distance parameter must be a valid integer");
			}
		}
		
		// convert the values to their required formats
		float lat  = Float.valueOf(latitude);
		float lng  = Float.valueOf(longitude);
		int   dist = Integer.valueOf(distance);
		
		// declare helper variables
		JSONArray  list   = new JSONArray();
		JSONObject obj    = new JSONObject();
		JSONParser parser = new JSONParser();
		String     json   = null;
		Float      value  = null;
		
		// get the bounding box hashMap
		java.util.HashMap<String, Coordinate> boundingBox = CoordinateManager.getBoundingBox(lat, lng, dist);
		
		// define the sql
		String sql = "SELECT mp.performance_id "
				   + "FROM venue v, events e, mob_performances mp "
				   + "WHERE TO_NUMBER(REGEXP_REPLACE(v.latitude, '[^0-9\\.\\-]+', '')) < TO_NUMBER(?) "
				   + "AND TO_NUMBER(REGEXP_REPLACE(v.latitude, '[^0-9\\.\\-]+', '')) > TO_NUMBER(?) "
				   + "AND TO_NUMBER(REGEXP_REPLACE(v.longitude, '[^0-9\\.\\-]+', '')) < TO_NUMBER(?) "
				   + "AND TO_NUMBER(REGEXP_REPLACE(v.longitude, '[^0-9\\.\\-]+', '')) > TO_NUMBER(?) "
				   + "AND v.venueid = e.venueid "
				   + "AND e.eventid = mp.event_id "
				   + "AND TO_DATE(start_date_time, 'DD-MON-YYYY') >= TO_DATE(sysdate, 'DD-MON-YYYY')";
				   
		// define the sql parameters
		String[] sqlParameters = new String[4];
		
		sqlParameters[0] = boundingBox.get("NE").getLatitudeAsString();
		sqlParameters[1] = boundingBox.get("SE").getLatitudeAsString();
		sqlParameters[2] = boundingBox.get("NE").getLongitudeAsString();
		sqlParameters[3] = boundingBox.get("SW").getLongitudeAsString();
		
		// execute the sql
		DbObjects results = database.executePreparedStatement(sql, sqlParameters);
		ResultSet resultSet = results.getResultSet();
		
		//loop through the resultSet
		try {
			if(resultSet.next() == true) {
			
				// get the json for this performance
				json = getPerformanceDetails(resultSet.getString(1), "json");
				
				// convert the json into an object
				obj = (JSONObject) parser.parse(json);
				
				// add the object to the array
				list.add(obj);				
				
			} else {
				return new JSONArray().toString();
			}
		} catch(java.sql.SQLException ex) {
			return new JSONArray().toString();
		} catch(org.json.simple.parser.ParseException ex) {
			return new JSONArray().toString();
		}
		
		return list.toString();
	}

} // end class definition
