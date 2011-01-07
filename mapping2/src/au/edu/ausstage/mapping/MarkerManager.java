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
import java.util.*;
import org.json.simple.*;

/**
 * A class used to compile the marker data which is used to build
 * maps on web pages
 */
public class MarkerManager {

	// declare private class variables
	DbManager database;
	
	/**
	 * Constructor for this class
	 *
	 * @param database a valid DbManager object
	 */
	public MarkerManager(DbManager database) {
		this.database = database;
	}
	
	/**
	 * A public method to build the marker data for a state
	 * includes australia wide and international markers as well
	 *
	 * @param stateId the unique identifier of the region
	 *
	 * @return        json encoded data as a string
	 */
	public String getStateMarkers(String stateId) {
	
		// double check the parameters
		if(InputUtils.isValid(stateId, MarkerServlet.VALID_STATES) == false) {
			// no valid state id was found
			throw new IllegalArgumentException("Missing id parameter. Expected one of: " + InputUtils.arrayToString(MarkerServlet.VALID_STATES));
		}
		
		// build the sqlParameters
		String[] sqlParameters = new String[1];
		
		if(stateId.equals("99") == true || stateId.equals("999") == true) {
			// all australian states or all international venues
			sqlParameters[0] = "9";
		} else { 
			// valid state code
			sqlParameters[0] = stateId;
		}
		
		// build the SQL
		String sql = null;
		
		if(stateId.equals("99") == true) {
			// all australian states
			sql = "SELECT v.venueid, v.venue_name, v.street, v.suburb, s.state, v.postcode, v.latitude, v.longitude "
				+ "FROM venue v, states s "
				+ "WHERE v.state < ? "
				+ "AND v.state = s.stateid "
				+ "AND latitude IS NOT NULL";
		} else {
			// international venues
			sql = "SELECT v.venueid, v.venue_name, v.street, v.suburb, s.state, v.postcode, v.latitude, v.longitude "
				+ "FROM venue v, states s "
				+ "WHERE v.state = ? "
				+ "AND v.state = s.stateid "
				+ "AND latitude IS NOT NULL";
		}
		
		// get the data
		DbObjects results = database.executePreparedStatement(sql, sqlParameters);
		
		// check to see that data was returned
		if(results == null) {
			return getEmptyArray();
		}
		
		// build the dataset using internal objects
		// build a list of venues
		ResultSet resultSet = results.getResultSet();
		VenueList venues = buildVenueList(resultSet);
		
		// play nice and tidy up
		resultSet = null;
		results.tidyUp();
		results = null;		
		
		// check what was returned
		if(venues == null) {
			return getEmptyArray();
		}
		
		// build and return the JSON data
		return venueListToJson(venues);
	}
	
	/**
	 * A public method to build the marker data for a suburb
	 *
	 * @param suburbId the unique identifier of the suburb
	 *
	 * @return         json encoded data as a string
	 */
	public String getSuburbMarkers(String suburbId) {
	
		// declare helper variables
		String[] sqlParameters = null;
	
		// double check the parameters
		if(suburbId.indexOf("_") == -1) {
			throw new IllegalArgumentException("The id parameter is required to have a state code followed by a suburb name seperated by a \"_\" character");
		} else {
			sqlParameters = suburbId.split("_");
			if(sqlParameters.length > 2) {
				throw new IllegalArgumentException("The id parameter is required to have a state code followed by a suburb name seperated by a \"_\" character");
			} else {
				if(InputUtils.isValid(sqlParameters[0], LookupServlet.VALID_STATES) == false) {
					throw new IllegalArgumentException("Invalid state code. Expected one of: " + InputUtils.arrayToString(LookupServlet.VALID_STATES));
				}
			}
		}
		
		// tidy up the parameters
		sqlParameters[1] = sqlParameters[1].toLowerCase();
		
		// build the SQL
		String sql = "SELECT v.venueid, v.venue_name, v.street, v.suburb, s.state, v.postcode, v.latitude, v.longitude "
				   + "FROM venue v, states s "
				   + "WHERE v.state = ? "
				   + "AND LOWER(v.suburb) = ? "
				   + "AND v.state = s.stateid "
				   + "AND latitude IS NOT NULL ";
		
		// get the data
		DbObjects results = database.executePreparedStatement(sql, sqlParameters);
		
		// check to see that data was returned
		if(results == null) {
			return getEmptyArray();
		}
		
		// build the dataset using internal objects
		// build a list of venues
		ResultSet resultSet = results.getResultSet();
		VenueList venues = buildVenueList(resultSet);
		
		// play nice and tidy up
		resultSet = null;
		results.tidyUp();
		results = null;		
		
		// check what was returned
		if(venues == null) {
			return getEmptyArray();
		}
		
		// build and return the JSON data
		return venueListToJson(venues);
	}
	
	/**
	 * A public method to build marker data for a venue or list of venues
	 *
	 * @param venueId the unique venue ID or a list of venue ids
	 *
	 * @return        json encoded data as a string
	 */
	public String getVenueMarkers(String venueId) {
	
		// declare helper variables
		String[] sqlParameters = null;
	
		// check the parameter
		if(InputUtils.isValid(venueId) == true) {
			if(venueId.indexOf(',') == -1) {
				// a single id
				if(InputUtils.isValidInt(venueId) == false) {
					throw new IllegalArgumentException("The id parameter must be a valid integer");
				}
			} else {
				// multiple ids
				sqlParameters = venueId.split(",");
			
				if(InputUtils.isValidArrayInt(sqlParameters) == false) {
					throw new IllegalArgumentException("The id parameter must contain a list of valid integers seperated by commas only");
				}
			}
		} else {
			throw new IllegalArgumentException("Missing id parameter.");
		}
		
		// double check the parameters
		if(sqlParameters == null) {
			sqlParameters = new String[1];
			sqlParameters[0] = venueId;
		}
		
		// build the SQL
		String sql = null;
		
		if(sqlParameters.length == 1) {
			sql = "SELECT v.venueid, v.venue_name, v.street, v.suburb, s.state, v.postcode, v.latitude, v.longitude "
				+ "FROM venue v, states s "
				+ "WHERE v.venueid = ? "
				+ "AND v.state = s.stateid "
				+ "AND latitude IS NOT NULL ";
		} else {
			sql = "SELECT v.venueid, v.venue_name, v.street, v.suburb, s.state, v.postcode, v.latitude, v.longitude "
				+ "FROM venue v, states s "
				+ "WHERE v.venueid = ANY (";
					   
			// add sufficient place holders for all of the ids
			for(int i = 0; i < sqlParameters.length; i++) {
				sql += "?,";
			}
	
			// tidy up the sql
			sql = sql.substring(0, sql.length() -1);
			
			// finalise the sql string
			sql += ") "
				+ "AND v.state = s.stateid "
				+ "AND latitude IS NOT NULL ";
		}
		
		// get the data
		DbObjects results = database.executePreparedStatement(sql, sqlParameters);
		
		// check to see that data was returned
		if(results == null) {
			return getEmptyArray();
		}
		
		// build the dataset using internal objects
		// build a list of venues
		ResultSet resultSet = results.getResultSet();
		VenueList venues = buildVenueList(resultSet);
		
		// play nice and tidy up
		resultSet = null;
		results.tidyUp();
		results = null;	
		
		// check what was returned
		if(venues == null) {
			return getEmptyArray();
		}
		
		// build and return the JSON data
		return venueListToJson(venues);		
	}
	
	/**
	 * A public method to build marker data for a venue or list of venues
	 *
	 * @param organisationId the unique venue ID or a list of venue ids
	 *
	 * @return               json encoded data as a string
	 */
	public String getOrganisationMarkers(String organisationId) {
	
		// declare helper variables
		String[] sqlParameters = null;
	
		// check the parameter
		if(InputUtils.isValid(organisationId) == true) {
			if(organisationId.indexOf(',') == -1) {
				// a single id
				if(InputUtils.isValidInt(organisationId) == false) {
					throw new IllegalArgumentException("The id parameter must be a valid integer");
				}
			} else {
				// multiple ids
				sqlParameters = organisationId.split(",");
			
				if(InputUtils.isValidArrayInt(sqlParameters) == false) {
					throw new IllegalArgumentException("The id parameter must contain a list of valid integers seperated by commas only");
				}
			}
		} else {
			throw new IllegalArgumentException("Missing id parameter.");
		}
		
		// double check the parameters
		if(sqlParameters == null) {
			sqlParameters = new String[1];
			sqlParameters[0] = organisationId;
		}
		
		// build the SQL
		String sql = null;
		
		if(sqlParameters.length == 1) {
			sql = "SELECT DISTINCT o.organisationid, v.venueid, v.venue_name, v.street, v.suburb, s.state, v.postcode, v.latitude, v.longitude "
				+ "FROM organisation o, orgevlink oel, events e, venue v, states s "
				+ "WHERE o.organisationid = ?"
				+ "AND o.organisationid = oel.organisationid "
				+ "AND oel.eventid = e.eventid "
				+ "AND e.venueid = v.venueid "
				+ "AND v.state = s.stateid "
				+ "AND latitude IS NOT NULL ";
		} else {
			sql = "SELECT DISTINCT o.organisationid, v.venueid, v.venue_name, v.street, v.suburb, s.state, v.postcode, v.latitude, v.longitude "
				+ "FROM organisation o, orgevlink oel, events e, venue v, states s "
				+ "WHERE o.organisationid = ANY (";
					   
			// add sufficient place holders for all of the ids
			for(int i = 0; i < sqlParameters.length; i++) {
				sql += "?,";
			}
	
			// tidy up the sql
			sql = sql.substring(0, sql.length() -1);
			
			// finalise the sql string
			sql += ") "
				+ "AND o.organisationid = oel.organisationid "
				+ "AND oel.eventid = e.eventid "
				+ "AND e.venueid = v.venueid "
				+ "AND v.state = s.stateid "
				+ "AND latitude IS NOT NULL "
				+ "ORDER BY o.organisationid";
		}
		
		// get the data
		DbObjects results = database.executePreparedStatement(sql, sqlParameters);
		
		// check to see that data was returned
		if(results == null) {
			return getEmptyArray();
		}
		
		// build the dataset using internal objects
		ResultSet resultSet = results.getResultSet();
		HashMap<Integer, VenueList> venueListMap = buildVenueListMap(resultSet);
		
		// play nice and tidy up
		resultSet = null;
		results.tidyUp();
		results = null;	
		
		// check what was returned
		if(venueListMap.size() == 0) {
			return getEmptyArray();
		}		
		
		// return the list
		return buildVenueListMapJSONArray(venueListMap).toString();
	}
	
	/**
	 * A public method to build marker data for a venue or list of venues
	 *
	 * @param contributorId  the unique venue ID or a list of venue ids
	 *
	 * @return               json encoded data as a string
	 */
	public String getContributorMarkers(String contributorId) {
	
		// declare helper variables
		String[] sqlParameters = null;
	
		// check the parameter
		if(InputUtils.isValid(contributorId) == true) {
			if(contributorId.indexOf(',') == -1) {
				// a single id
				if(InputUtils.isValidInt(contributorId) == false) {
					throw new IllegalArgumentException("The id parameter must be a valid integer");
				}
			} else {
				// multiple ids
				sqlParameters = contributorId.split(",");
			
				if(InputUtils.isValidArrayInt(sqlParameters) == false) {
					throw new IllegalArgumentException("The id parameter must contain a list of valid integers seperated by commas only");
				}
			}
		} else {
			throw new IllegalArgumentException("Missing id parameter.");
		}
		
		// double check the parameters
		if(sqlParameters == null) {
			sqlParameters = new String[1];
			sqlParameters[0] = contributorId;
		}
		
		// build the SQL
		String sql = null;
		
		if(sqlParameters.length == 1) {
			sql = "SELECT DISTINCT c.contributorid, v.venueid, v.venue_name, v.street, v.suburb, s.state, v.postcode, v.latitude, v.longitude "
				+ "FROM contributor c, conevlink cel, events e, venue v, states s "
				+ "WHERE c.contributorid = ?"
				+ "AND c.contributorid = cel.contributorid "
				+ "AND cel.eventid = e.eventid "
				+ "AND e.venueid = v.venueid "
				+ "AND v.state = s.stateid "
				+ "AND latitude IS NOT NULL ";
		} else {
			sql = "SELECT DISTINCT c.contributorid, v.venueid, v.venue_name, v.street, v.suburb, s.state, v.postcode, v.latitude, v.longitude "
				+ "FROM contributor c, conevlink cel, events e, venue v, states s "
				+ "WHERE c.contributorid = ANY (";
					   
			// add sufficient place holders for all of the ids
			for(int i = 0; i < sqlParameters.length; i++) {
				sql += "?,";
			}
	
			// tidy up the sql
			sql = sql.substring(0, sql.length() -1);
			
			// finalise the sql string
			sql += ") "
				+ "AND c.contributorid = cel.contributorid "
				+ "AND cel.eventid = e.eventid "
				+ "AND e.venueid = v.venueid "
				+ "AND v.state = s.stateid "
				+ "AND latitude IS NOT NULL "
				+ "ORDER BY c.contributorid";
		}
		
		// get the data
		DbObjects results = database.executePreparedStatement(sql, sqlParameters);
		
		// check to see that data was returned
		if(results == null) {
			return getEmptyArray();
		}
		
		// build the dataset using internal objects
		ResultSet resultSet = results.getResultSet();
		HashMap<Integer, VenueList> venueListMap = buildVenueListMap(resultSet);
		
		// play nice and tidy up
		resultSet = null;
		results.tidyUp();
		results = null;	
		
		// check what was returned
		if(venueListMap.size() == 0) {
			return getEmptyArray();
		}		
		
		// return the list
		return buildVenueListMapJSONArray(venueListMap, "contributor").toString();
	}
	
	/**
	 * A private method to build a venue list map given a resultset
	 * Assumes the first column is the index to the map
	 *
	 * @param resultSet the result set to proces
	 *
	 * @return          a completed map
	 */
	private HashMap<Integer, VenueList> buildVenueListMap(ResultSet resultSet) {
	
		// declare helper variables
		HashMap<Integer, VenueList> venueListMap = new HashMap<Integer, VenueList>();
		VenueList  venues = null;
		Venue      venue  = null;
		
		try {
			// loop through the resultset
			while (resultSet.next()) {
			
				if(venueListMap.containsKey(Integer.parseInt(resultSet.getString(1))) == true) {
					venues = venueListMap.get(Integer.parseInt(resultSet.getString(1)));
				} else {
					venues = new VenueList();
					venueListMap.put(Integer.parseInt(resultSet.getString(1)), venues);
				}
			
				// build a new venue object
				venue = new Venue(resultSet.getString(2));
				
				// add the other information
				venue.setName(resultSet.getString(3));
				venue.setStreet(resultSet.getString(4));
				venue.setSuburb(resultSet.getString(5));
				venue.setState(resultSet.getString(6));
				venue.setPostcode(resultSet.getString(7));
				venue.setLatitude(resultSet.getString(8));
				venue.setLongitude(resultSet.getString(9));
				venue.setUrl(LinksManager.getVenueLink(resultSet.getString(2)));
				
				// add the venue to the list
				venues.addVenue(venue);			
			}
		
		} catch (java.sql.SQLException ex) {
			return null;
		}
		
		return venueListMap;	
	}
	
	/**
	 * A private method to take a venueListMap and convert it into a JSONArray of objects
	 *
	 * @param venueListMap the venue list map to process
	 *
	 * @return             the JSONArray representing the map
	 */
	@SuppressWarnings("unchecked") 
	private JSONArray buildVenueListMapJSONArray(HashMap<Integer, VenueList> venueListMap) {
	
		// declare helper variables
		Collection keys     = venueListMap.keySet();
		Iterator   iterator = keys.iterator();
		
		JSONArray list = new JSONArray();
		JSONObject object;
		Integer index;
		
		VenueList  venues = null;
		Venue      venue  = null;
		
		// loop through the map
		
		while(iterator.hasNext()) {
			
			// get the index
			index = (Integer)iterator.next();
			
			// get the venue list
			venues = (VenueList)venueListMap.get(index);
			
			// create a new object
			object = new JSONObject();
			
			// add the index and the venues
			object.put("id", index);
			object.put("venues", venueListToJsonArray(venues));
			
			// add this object to the list
			list.add(object);
		}
		
		return list;	
	}
	
	/**
	 * A private method to take a venueListMap and convert it into a JSONArray of objects
	 *
	 * @param venueListMap the venue list map to process
	 * @param extraData    a field to indicate the additional data is required
	 *
	 * @return             the JSONArray representing the map
	 */
	@SuppressWarnings("unchecked") 
	private JSONArray buildVenueListMapJSONArray(HashMap<Integer, VenueList> venueListMap, String extraData) {
	
		if(extraData == null) {
			return buildVenueListMapJSONArray(venueListMap);
		} else {
			// declare helper variables
			Collection keys     = venueListMap.keySet();
			Iterator   iterator = keys.iterator();
		
			JSONArray list = new JSONArray();
			JSONObject object;
			Integer index;
		
			VenueList  venues = null;
			Venue      venue  = null;
			
			LookupManager lookup     = new LookupManager(database);
			String        lookupData = null;
			Object        obj        = null;
		
			// loop through the map
		
			while(iterator.hasNext()) {
			
				// get the index
				index = (Integer)iterator.next();
			
				// get the venue list
				venues = (VenueList)venueListMap.get(index);
			
				// create a new object
				object = new JSONObject();
			
				// add the index and the venues
				object.put("id", index);
				
				// get the extra data
				if(extraData.equals("contributor") == true) {
					lookupData = lookup.getContributor(Integer.toString(index));
				}
				
				obj = JSONValue.parse(lookupData);
				object.put("extra", (JSONArray)obj);
				object.put("venues", venueListToJsonArray(venues));
			
				// add this object to the list
				list.add(object);
			}
		
			return list;
		}	
	} 
	
	/**
	 * A private method to build a venueList given a resultSet
	 *
	 * @param resultSet the result set to process
	 *
	 * @return          a completed venueList
	 */
	private VenueList buildVenueList(ResultSet resultSet) {
		
		// declare helper variables
		VenueList venues = new VenueList();
		Venue     venue  = null;
	
		try {
			// loop through the resultset
			while (resultSet.next()) {
			
				// build a new venue object
				venue = new Venue(resultSet.getString(1));
				
				// add the other information
				venue.setName(resultSet.getString(2));
				venue.setStreet(resultSet.getString(3));
				venue.setSuburb(resultSet.getString(4));
				venue.setState(resultSet.getString(5));
				venue.setPostcode(resultSet.getString(6));
				venue.setLatitude(resultSet.getString(7));
				venue.setLongitude(resultSet.getString(8));
				venue.setUrl(LinksManager.getVenueLink(resultSet.getString(1)));
				
				// add the venue to the list
				venues.addVenue(venue);			
			}
		
		} catch (java.sql.SQLException ex) {
			return null;
		}
		
		// if we get this far everything worked as expected
		return venues;	
	}
	
	/**
	 * A private method to build a JSON array of venue objects using a VenueList
	 *
	 * @param venues an instance of the VenueList object
	 *
	 * @return       the JSON encoded string 
	 */
	@SuppressWarnings("unchecked") 
	private String venueListToJson(VenueList venues) {
	
		// declare helper variables
		JSONArray  list   = venueListToJsonArray(venues);
		return list.toString();
	}
	
	/**
	 * A private method to build a JSON array of venue objects using a VenueList
	 *
	 * @param venues an instance of the VenueList object
	 *
	 * @return       the JSON encoded string 
	 */
	@SuppressWarnings("unchecked") 
	private JSONArray venueListToJsonArray(VenueList venues) {
	
		// declare helper variables
		JSONArray  list   = new JSONArray();
		JSONObject object = null;
		Venue      venue  = null;
		
		// get the iterator for the list
		Set<Venue> venueSet      = venues.getVenues();
		Iterator   venueIterator = venueSet.iterator();
		
		// iterate over the venues
		while(venueIterator.hasNext()) {
		
			// get the current venue
			venue = (Venue)venueIterator.next();		
			
			// add this object to the list
			list.add(venueToJson(venue));			
		}
		
		// return the JSON encoded string
		return list;
	}
	
	
	
	/**
	 * A private method to return the JSON version of a venue object
	 *
	 * @param venue a valid venue object
	 *
	 * @return      the JSON object
	 */
	@SuppressWarnings("unchecked")
	private JSONObject venueToJson(Venue venue) {
	
		JSONObject object = new JSONObject();
		
		object.put("id", venue.getId());
		object.put("name", venue.getName());
		object.put("street", venue.getStreet());
		object.put("suburb", venue.getSuburb());
		object.put("postcode", venue.getPostcode());
		object.put("latitude", venue.getLatitude());
		object.put("longitude", venue.getLongitude());
		object.put("url", venue.getUrl());
		
		return object;	
	}
	
	/**
	 * A private method to return an empty JSON array
	 *
	 * @return an empty JSON array as a string
	 */
	@SuppressWarnings("unchecked")
	private String getEmptyArray() {
		JSONArray list = new JSONArray();
		return list.toString();
	}
	
	/**
	 * A private method to return an empty JSON object
	 *
	 * @return an empty JSON object as a string
	 */
	@SuppressWarnings("unchecked")
	private String getEmptyObject() {
		JSONObject object = new JSONObject();
		return object.toString();
	}
	
} // end class definition
