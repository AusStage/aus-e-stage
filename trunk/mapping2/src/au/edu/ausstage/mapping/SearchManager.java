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
import java.util.Set;
import java.util.Iterator;
import org.json.simple.*;

/**
 * A class used to compile the marker data which is used to build
 * maps on web pages
 */
public class SearchManager {

	// declare private class variables
	DbManager database;
	
	/**
	 * Constructor for this class
	 *
	 * @param database a valid DbManager object
	 */
	public SearchManager(DbManager database) {
		this.database = database;
	}
	
	/** 
	 * A method to undertake a Organisation search
	 *
	 * @param searchType the type of search to undertake
	 * @param query      the search query to use
	 * @param formatType the type of data format for the results
	 * @param sortType   the way in which to sort the results
	 * @param limit      the number to limit the search results
	 */
	public String doOrganisationSearch(String searchType, String query, String formatType, String sortType, int limit) {
	
		// check on the parameters
		if(InputUtils.isValid(searchType) == false || InputUtils.isValid(query) == false || InputUtils.isValid(formatType) == false || InputUtils.isValid(sortType) == false) {
			throw new IllegalArgumentException("All of the parameters to this method are required");
		}
		
		// double check the parameter
		if(InputUtils.isValidInt(limit, SearchServlet.MIN_LIMIT, SearchServlet.MAX_LIMIT) == false) {
			throw new IllegalArgumentException("Limit parameter must be between '" + SearchServlet.MIN_LIMIT + "' and '" + SearchServlet.MAX_LIMIT + "'");
		}
		
		if(searchType.equals("id") == true) {
			return doOrganisationIdSearch(query, formatType);
		}	
	
		//debug code
		return "";
		
	} // end the doOrganisationSearch method
	
	/**
	 * A private method to undertake a Organisation id search
	 *
	 * @param query      the search query to use
	 * @param formatType the type of data format for the results
	 *
	 * @return           the results of the search
	 */
	private String doOrganisationIdSearch(String query, String formatType) {
	
		// check the parameters
		if(InputUtils.isValid(query) == false || InputUtils.isValid(formatType) == false) {
			throw new IllegalArgumentException("All of the parameters to this method are required");
		}
		
		if(InputUtils.isValidInt(query) == false) {
			throw new IllegalArgumentException("The query parameter must be a valid integer");
		}
		
		// declare the SQL variables
		String sql = "SELECT organisationid, name, COUNT(eventid) as event_count, COUNT(longitude) as mapped_events "
				   + "FROM (SELECT DISTINCT o.organisationid, o.name, e.eventid, v.longitude "
				   + "      FROM organisation o, orgevlink oel, events e, venue v "
				   + "      WHERE o.organisationid = ? "
				   + "      AND o.organisationid = oel.organisationid "
				   + "      AND oel.eventid = e.eventid "
				   + "      AND e.venueid = v.venueid) "
				   + "GROUP BY organisationid, name";
				   
		// define the paramaters
		String[] sqlParameters = {query};
		
		// declare additional helper variables
		OrganisationList list          = new OrganisationList();
		Organisation     organisation  = null;
		JSONArray        jsonList      = new JSONArray();
		
		// get the data
		DbObjects results = database.executePreparedStatement(sql, sqlParameters);
		
		// check to see that data was returned
		if(results == null) {
			// return an empty JSON Array
			return jsonList.toString();
		}
		
		// build the result data
		ResultSet resultSet = results.getResultSet();
		
		try {
		
			// move to the result record
			resultSet.next();
			
			// build the organisation object
			organisation = new Organisation(resultSet.getString(1));
			organisation.setName(resultSet.getString(2));
			organisation.setUrl(LinksManager.getOrganisationLink(resultSet.getString(1)));
			organisation.setEventCount(resultSet.getString(3));
			organisation.setMappedEventCount(resultSet.getString(4));			
		
			// add the contrbutor to this venue
			list.addOrganisation(organisation);
		
		} catch (java.sql.SQLException ex) {
			// return an empty JSON Array
			return jsonList.toString();
		}
		 
		// play nice and tidy up
		resultSet = null;
		results.tidyUp();
		results = null;
		
		if(formatType.equals("json") == true) {
			return createJSONOutput(list, null);
		}
		
		// required by compiler
		return jsonList.toString();
			
	} // end doOrganisationIdSearch method
	
	/**
	 * A private method to turn an organisation list into a JSON Array
	 *
	 * @param orgList the OrganisationList object
	 *
	 * @return        the list of organisations as a JSON Array
	 */
	/**
	 * A method to take a group of collaborators and output JSON encoded text
	 * Unchecked warnings are suppressed due to internal issues with the org.json.simple package
	 *
	 * @param collaborators the list of organisations
	 * @param sortOrder     the order to sort the list of organisations
	 * @return              the JSON encoded string
	 */
	@SuppressWarnings("unchecked")
	private String createJSONOutput(OrganisationList orgList, String sortOrder) {
	
		Set<Organisation> organisations = null;
		
		if(sortOrder != null) {
			//TODO implement the search order
		} else {
			organisations = orgList.getOrganisations();
		}		
	
		// assume that all sorting and ordering has already been carried out
		// loop through the list of organisations and add them to the new JSON objects
		Iterator iterator = organisations.iterator();
		
		// declare helper variables
		JSONArray  list = new JSONArray();
		JSONObject object = null;
		Organisation organisation = null;
		
		while(iterator.hasNext()) {
		
			// get the organisation
			organisation = (Organisation)iterator.next();
			
			// start a new JSON object
			object = new JSONObject();
			
			// build the object
			object.put("id", organisation.getId());
			object.put("url", organisation.getUrl());
			object.put("name", organisation.getName());
			object.put("totalEventCount", new Integer(organisation.getEventCount()));
			object.put("mapEventCount", new Integer(organisation.getMappedEventCount()));
			
			// add the new object to the array
			list.add(object);		
		}
		
		// return the JSON encoded string
		return list.toString();
			
	} // end the createJSONOutput method
	
} // end class definition
