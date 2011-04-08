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

// import other Java classes / libraries
import java.sql.ResultSet;
import java.util.Set;
import java.util.Iterator;
import org.json.simple.*;

/**
 * A class used to compile the marker data which is used to build
 * maps on web pages
 */
public class EventLookupManager {

	// declare private class variables
	DbManager database;
	
	/**
	 * Constructor for this class
	 *
	 * @param database a valid DbManager object
	 */
	public EventLookupManager(DbManager database) {
		this.database = database;
	}
	
	/**
	 * A method to get a list of events that occured at a venue
	 *
	 * @param id         the unique identifier of the venue
	 * @param formatType the type of format to use for encoding the data
	 *
	 * @return           the results of the lookup encoded in the specified format
	 */
	@SuppressWarnings("unchecked")
	public String getEventsByVenue(String id, String formatType) {
	
		// check the parameters
		if(InputUtils.isValid(id) == false) {
			throw new IllegalArgumentException("The id parameter must be a valid integer");
		}
		
		if(InputUtils.isValid(formatType, EventLookupServlet.FORMAT_TYPES) == false) {
			throw new IllegalArgumentException("The specified formatType is invalid. Expected one of: " + InputUtils.arrayToString(EventLookupServlet.FORMAT_TYPES));
		}
		
		// get the details of the venue
		JSONObject venue = getVenueObject(id);
		JSONArray  list  = new JSONArray();
		JSONObject object;
		
		// check on what was returned
		if(venue == null) {
			return new JSONObject().toString();
		}
		
		// get the events at this venue
		String sql = "SELECT eventid, event_name, yyyyfirst_date, mmfirst_date, ddfirst_date "
				   + "FROM events "
				   + "WHERE venueid = ? "
				   + "ORDER BY yyyyfirst_date DESC, mmfirst_date DESC, ddfirst_date DESC";
				   
		String[] sqlParameters = {id};
		
		// get the data
		DbObjects results = database.executePreparedStatement(sql, sqlParameters);
		
		// check to see that data was returned
		if(results == null) {
			// return an empty JSON Array
			return new JSONObject().toString();
		}
		
		// build the result data
		ResultSet resultSet = results.getResultSet();
		
		// build the list of results
		try {
		
			// loop through the resulset
			while(resultSet.next()) {
			
				// start a new JSON object
				object = new JSONObject();
				object.put("id", resultSet.getString(1));
				object.put("name", resultSet.getString(2));
				object.put("firstDate", DateUtils.buildDisplayDate(resultSet.getString(3), resultSet.getString(4), resultSet.getString(5)));
				object.put("filterDate", DateUtils.getIntegerFromDate(DateUtils.getSortDate(resultSet.getString(3), resultSet.getString(4), resultSet.getString(5))));
				object.put("url", LinksManager.getEventLink(resultSet.getString(1)));
				
				// add the object to the list
				list.add(object);
			}
		
		} catch (java.sql.SQLException ex) {
			// return an empty JSON Array
			return new JSONObject().toString();
		}
		
		// play nice and tidy up
		resultSet = null;
		results.tidyUp();
		results = null;
		
		// finalise the object
		venue.put("events", list);
		
		// return the data
		return venue.toString();
	}
	
	/**
	 * A private method to get the details of the venue from the search manager
	 *
	 * @param id the venue id
	 * @return a JSONObject containing details of the venue
	 */
	private JSONObject getVenueObject(String id) {

		// get the details of the venue
		SearchManager manager = new SearchManager(database);		
		String results = manager.doVenueSearch("id", id, null, null, null);
		
		// check on what was returned
		if(results.length() < 10) {
			return null;
		}
		
		// decode the JSON
		Object    obj   = JSONValue.parse(results);  // decode the JSON
		JSONArray array = (JSONArray)obj;		     // cast the object as an JSONArray
		JSONObject venue = (JSONObject)array.get(0); // case the first element in the array as JSONObject
		
		// return the details of the venue
		return venue;	
	}
	
	/**
	 * A private method to get the details of the organisation from the search manager
	 *
	 * @param id the organisation id
	 * @return a JSONObject containing details of the venue
	 */
	private JSONObject getOrganisationObject(String id) {

		// get the details of the venue
		SearchManager manager = new SearchManager(database);		
		String results = manager.doOrganisationSearch("id", id, null, null, null);
		
		// check on what was returned
		if(results.length() < 10) {
			return null;
		}
		
		// decode the JSON
		Object    obj   = JSONValue.parse(results);  // decode the JSON
		JSONArray array = (JSONArray)obj;		     // cast the object as an JSONArray
		JSONObject organisation = (JSONObject)array.get(0); // case the first element in the array as JSONObject
		
		// return the details of the venue
		return organisation;	
	}
	
	/**
	 * A private method to get the details of the contributor from the search manager
	 *
	 * @param id the organisation id
	 * @return a JSONObject containing details of the venue
	 */
	private JSONObject getContributorObject(String id) {

		// get the details of the venue
		SearchManager manager = new SearchManager(database);		
		String results = manager.doContributorSearch("id", id, null, null, null);
		
		// check on what was returned
		if(results.length() < 10) {
			return null;
		}
		
		// decode the JSON
		Object    obj   = JSONValue.parse(results);  // decode the JSON
		JSONArray array = (JSONArray)obj;		     // cast the object as an JSONArray
		JSONObject contributor = (JSONObject)array.get(0); // case the first element in the array as JSONObject
		
		// return the details of the venue
		return contributor;	
	}
	
	/**
	 * A method to get a list of events for an organisation at a venue
	 *
	 * @param id         the unique identifier of the organisation
	 * @param venueId    the unique identifier of the venue
	 * @param formatType the type of format to use for encoding the data
	 *
	 * @return           the results of the lookup encoded in the specified format
	 */
	@SuppressWarnings("unchecked")
	public String getEventsByOrganisation(String id, String venueId, String formatType) {
	
		// check the parameters
		if(InputUtils.isValid(id) == false) {
			throw new IllegalArgumentException("The id parameter must be a valid integer");
		}
		
		if(InputUtils.isValid(venueId) == false) {
			throw new IllegalArgumentException("The venue id parameter must be a valid integer");
		}
		
		if(InputUtils.isValid(formatType, EventLookupServlet.FORMAT_TYPES) == false) {
			throw new IllegalArgumentException("The specified formatType is invalid. Expected one of: " + InputUtils.arrayToString(EventLookupServlet.FORMAT_TYPES));
		}
		
		// get the details of the venue
		JSONObject venue        = getVenueObject(venueId);
		JSONObject organisation = getOrganisationObject(id);
		JSONArray  list  = new JSONArray();
		JSONObject object;
		
		// check on what was returned
		if(venue == null || organisation == null) {
			return new JSONObject().toString();
		}
		
		// get the events at this venue
		String sql = "SELECT DISTINCT events.eventid, event_name, yyyyfirst_date, mmfirst_date, ddfirst_date "
				   + "FROM events, orgevlink "
				   + "WHERE venueid = ? "
				   + "AND events.eventid = orgevlink.eventid "
				   + "AND orgevlink.organisationid = ? "
				   + "ORDER BY yyyyfirst_date DESC, mmfirst_date DESC, ddfirst_date DESC";
				   
		String[] sqlParameters = {venueId, id};
		
		// get the data
		DbObjects results = database.executePreparedStatement(sql, sqlParameters);
		
		// check to see that data was returned
		if(results == null) {
			// return an empty JSON Array
			return new JSONObject().toString();
		}
		
		// build the result data
		ResultSet resultSet = results.getResultSet();
		
		// build the list of results
		try {
		
			// loop through the resulset
			while(resultSet.next()) {
			
				// start a new JSON object
				object = new JSONObject();
				object.put("id", resultSet.getString(1));
				object.put("name", resultSet.getString(2));
				object.put("firstDate", DateUtils.buildDisplayDate(resultSet.getString(3), resultSet.getString(4), resultSet.getString(5)));
				object.put("filterDate", DateUtils.getIntegerFromDate(DateUtils.getSortDate(resultSet.getString(3), resultSet.getString(4), resultSet.getString(5))));
				object.put("url", LinksManager.getEventLink(resultSet.getString(1)));
				
				// add the object to the list
				list.add(object);
			}
		
		} catch (java.sql.SQLException ex) {
			// return an empty JSON Array
			return new JSONObject().toString();
		}
		
		// play nice and tidy up
		resultSet = null;
		results.tidyUp();
		results = null;
		
		// finalise the object
		venue.put("events", list);
		venue.put("organisation", organisation);
		
		// return the data
		return venue.toString();
	}
	
	/**
	 * A method to get a list of events for a contributor at a venue
	 *
	 * @param id         the unique identifier of the contributor
	 * @param venueId    the unique identifier of the venue
	 * @param formatType the type of format to use for encoding the data
	 *
	 * @return           the results of the lookup encoded in the specified format
	 */
	@SuppressWarnings("unchecked")
	public String getEventsByContributor(String id, String venueId, String formatType) {
	
		// check the parameters
		if(InputUtils.isValid(id) == false) {
			throw new IllegalArgumentException("The id parameter must be a valid integer");
		}
		
		if(InputUtils.isValid(venueId) == false) {
			throw new IllegalArgumentException("The venue id parameter must be a valid integer");
		}
		
		if(InputUtils.isValid(formatType, EventLookupServlet.FORMAT_TYPES) == false) {
			throw new IllegalArgumentException("The specified formatType is invalid. Expected one of: " + InputUtils.arrayToString(EventLookupServlet.FORMAT_TYPES));
		}
		
		// get the details of the venue
		JSONObject venue       = getVenueObject(venueId);
		JSONObject contributor = getContributorObject(id);
		JSONArray  list  = new JSONArray();
		JSONObject object;
		
		// check on what was returned
		if(venue == null || contributor == null) {
			return new JSONObject().toString();
		}
		
		// get the events at this venue
		String sql = "SELECT DISTINCT events.eventid, event_name, yyyyfirst_date, mmfirst_date, ddfirst_date "
				   + "FROM events, conevlink "
				   + "WHERE venueid = ? "
				   + "AND events.eventid = conevlink.eventid "
				   + "AND conevlink.contributorid = ? "
				   + "ORDER BY yyyyfirst_date DESC, mmfirst_date DESC, ddfirst_date DESC";
				   
		String[] sqlParameters = {venueId, id};
		
		// get the data
		DbObjects results = database.executePreparedStatement(sql, sqlParameters);
		
		// check to see that data was returned
		if(results == null) {
			// return an empty JSON Array
			return new JSONObject().toString();
		}
		
		// build the result data
		ResultSet resultSet = results.getResultSet();
		
		// build the list of results
		try {
		
			// loop through the resulset
			while(resultSet.next()) {
			
				// start a new JSON object
				object = new JSONObject();
				object.put("id", resultSet.getString(1));
				object.put("name", resultSet.getString(2));
				object.put("firstDate", DateUtils.buildDisplayDate(resultSet.getString(3), resultSet.getString(4), resultSet.getString(5)));
				object.put("filterDate", DateUtils.getIntegerFromDate(DateUtils.getSortDate(resultSet.getString(3), resultSet.getString(4), resultSet.getString(5))));
				object.put("url", LinksManager.getEventLink(resultSet.getString(1)));
				
				// add the object to the list
				list.add(object);
			}
		
		} catch (java.sql.SQLException ex) {
			// return an empty JSON Array
			return new JSONObject().toString();
		}
		
		// play nice and tidy up
		resultSet = null;
		results.tidyUp();
		results = null;
		
		// finalise the object
		venue.put("events", list);
		venue.put("contributor", contributor);
		
		// return the data
		return venue.toString();
	}	
	
} // end class definition
