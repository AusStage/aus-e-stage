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
	
	/*
	 * organisation searching
	 */
	
	
	/** 
	 * A method to undertake a Organisation search
	 *
	 * @param searchType the type of search to undertake
	 * @param query      the search query to use
	 * @param formatType the type of data format for the results
	 * @param sortType   the way in which to sort the results
	 * @param limit      the number to limit the search results
	 */
	public String doOrganisationSearch(String searchType, String query, String formatType, String sortType, Integer limit) {
	
		// check on the parameters
		if(InputUtils.isValid(searchType) == false || InputUtils.isValid(query) == false) {
			throw new IllegalArgumentException("All of the parameters to this method are required");
		}
		
		// double check the parameter
		if(searchType.equals("id") != true) {
			if(InputUtils.isValidInt(limit, SearchServlet.MIN_LIMIT, SearchServlet.MAX_LIMIT) == false) {
				throw new IllegalArgumentException("Limit parameter must be between '" + SearchServlet.MIN_LIMIT + "' and '" + SearchServlet.MAX_LIMIT + "'");
			}
		}
		
		if(InputUtils.isValid(formatType) == false) {
			formatType = "json";
		} 
		
		if(InputUtils.isValid(sortType) == false) {
			sortType = "id";
		}
		
		// get the results of the search
		String data = null;
		
		if(searchType.equals("id") == true) {
			data = doOrganisationIdSearch(query, formatType);
		}	
	
		// return the data
		return data;
		
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
		
		
		// determine how to format the result
		String data = null;		
		if(formatType.equals("json") == true) {
			data = createJSONOutput(list, null);
		}
		
		// return the data
		return data;
			
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
	 *
	 * @param collaborators the list of organisations
	 * @param sortOrder     the order to sort the list of organisations as defined in the OrganisationList class
	 * @return              the JSON encoded string
	 */
	@SuppressWarnings("unchecked")
	private String createJSONOutput(OrganisationList orgList, Integer sortOrder) {
	
		Set<Organisation> organisations = null;
		
		if(sortOrder != null) {
			if(InputUtils.isValidInt(sortOrder, OrganisationList.ORGANISATION_ID_SORT, OrganisationList.ORGANISATION_NAME_SORT) != false) {
				if(sortOrder == OrganisationList.ORGANISATION_ID_SORT) {
					organisations = orgList.getSortedOrganisations(OrganisationList.ORGANISATION_ID_SORT);
				} else {
					organisations = orgList.getSortedOrganisations(OrganisationList.ORGANISATION_NAME_SORT);
				}
			} else {
				organisations = orgList.getOrganisations();
			}
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
	
	/*
	 * contributor searching
	 */
	
	
	/** 
	 * A method to undertake a Organisation search
	 *
	 * @param searchType the type of search to undertake
	 * @param query      the search query to use
	 * @param formatType the type of data format for the results
	 * @param sortType   the way in which to sort the results
	 * @param limit      the number to limit the search results
	 */
	public String doContributorSearch(String searchType, String query, String formatType, String sortType, Integer limit) {
	
		// check on the parameters
		if(InputUtils.isValid(searchType) == false || InputUtils.isValid(query) == false) {
			throw new IllegalArgumentException("All of the parameters to this method are required");
		}
		
		// double check the parameter
		if(searchType.equals("id") != true) {
			if(InputUtils.isValidInt(limit, SearchServlet.MIN_LIMIT, SearchServlet.MAX_LIMIT) == false) {
				throw new IllegalArgumentException("Limit parameter must be between '" + SearchServlet.MIN_LIMIT + "' and '" + SearchServlet.MAX_LIMIT + "'");
			}
		}
		
		if(InputUtils.isValid(formatType) == false) {
			formatType = "json";
		} 
		
		if(InputUtils.isValid(sortType) == false) {
			sortType = "id";
		}
		
		// get the results of the search
		String data = null;
		
		if(searchType.equals("id") == true) {
			data = doContributorIdSearch(query, formatType);
		}	
	
		// return the data
		return data;
		
	} // end the doOrganisationSearch method
	
	/**
	 * A private method to undertake a Organisation id search
	 *
	 * @param query      the search query to use
	 * @param formatType the type of data format for the results
	 *
	 * @return           the results of the search
	 */
	private String doContributorIdSearch(String query, String formatType) {
	
		// check the parameters
		if(InputUtils.isValid(query) == false || InputUtils.isValid(formatType) == false) {
			throw new IllegalArgumentException("All of the parameters to this method are required");
		}
		
		if(InputUtils.isValidInt(query) == false) {
			throw new IllegalArgumentException("The query parameter must be a valid integer");
		}
		
		// declare the SQL variables
		String sql = "SELECT contributorid, first_name, last_name, COUNT(eventid) as event_count, COUNT(longitude) as mapped_events "
				   + "FROM (SELECT c.contributorid, c.first_name, c.last_name, e.eventid, v.longitude "
				   + "      FROM contributor c, conevlink cel, events e, venue v "
				   + "      WHERE c.contributorid = ? "
				   + "      AND c.contributorid = cel.contributorid "
				   + "      AND cel.eventid = e.eventid "
				   + "      AND e.venueid = v.venueid) "
				   + "GROUP BY contributorid, first_name, last_name";
				   
		// define the paramaters
		String[] sqlParameters = {query};
		
		// declare additional helper variables
		ContributorList list         = new ContributorList();
		Contributor     contributor  = null;
		JSONArray       jsonList     = new JSONArray();
		
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
			
			contributor = new Contributor(resultSet.getString(1));
			contributor.setFirstName(resultSet.getString(2));
			contributor.setLastName(resultSet.getString(3));
			contributor.setUrl(LinksManager.getContributorLink(resultSet.getString(1)));
			contributor.setEventCount(resultSet.getString(4));
			contributor.setMappedEventCount(resultSet.getString(5));		
		
			// add the contrbutor to this venue
			list.addContributor(contributor);
		
		} catch (java.sql.SQLException ex) {
			// return an empty JSON Array
			return jsonList.toString();
		}
		 
		// play nice and tidy up
		resultSet = null;
		results.tidyUp();
		results = null;
		
		
		// determine how to format the result
		String data = null;		
		if(formatType.equals("json") == true) {
			data = createJSONOutput(list, null);
		}
		
		// return the data
		return data;
			
	} // end doOrganisationIdSearch method
	

	/**
	 * A method to take a group of collaborators and output JSON encoded text
	 *
	 * @param contribList the list of organisations
	 * @param sortOrder     the order to sort the list of organisations as defined in the OrganisationList class
	 * @return              the JSON encoded string
	 */
	@SuppressWarnings("unchecked")
	private String createJSONOutput(ContributorList contribList, Integer sortOrder) {
	
		Set<Contributor> contributors = null;
		
		if(sortOrder != null) {
			if(InputUtils.isValidInt(sortOrder, ContributorList.CONTRIBUTOR_ID_SORT, ContributorList.CONTRIBUTOR_NAME_SORT) != false) {
				if(sortOrder == ContributorList.CONTRIBUTOR_ID_SORT) {
					contributors = contribList.getSortedContributors(ContributorList.CONTRIBUTOR_ID_SORT);
				} else {
					contributors = contribList.getSortedContributors(ContributorList.CONTRIBUTOR_NAME_SORT);
				}
			} else {
				contributors = contribList.getContributors();
			}
		} else {
			contributors = contribList.getContributors();
		}		
	
		// assume that all sorting and ordering has already been carried out
		// loop through the list of organisations and add them to the new JSON objects
		Iterator iterator = contributors.iterator();
		
		// declare helper variables
		JSONArray  list = new JSONArray();
		JSONObject object = null;
		Contributor contributor = null;
		
		while(iterator.hasNext()) {
		
			// get the organisation
			contributor = (Contributor)iterator.next();
			
			// start a new JSON object
			object = new JSONObject();
			
			// build the object
			object.put("id", contributor.getId());
			object.put("url", contributor.getUrl());
			object.put("firstName", contributor.getFirstName());
			object.put("lastName", contributor.getLastName());
			object.put("totalEventCount", new Integer(contributor.getEventCount()));
			object.put("mapEventCount", new Integer(contributor.getMappedEventCount()));
			
			// add the new object to the array
			list.add(object);		
		}
		
		// return the JSON encoded string
		return list.toString();
			
	} // end the createJSONOutput method
	
	/** 
	 * A method to undertake a Organisation search
	 *
	 * @param searchType the type of search to undertake
	 * @param query      the search query to use
	 * @param formatType the type of data format for the results
	 * @param sortType   the way in which to sort the results
	 * @param limit      the number to limit the search results
	 */
	public String doVenueSearch(String searchType, String query, String formatType, String sortType, Integer limit) {
	
		// check on the parameters
		if(InputUtils.isValid(searchType) == false || InputUtils.isValid(query) == false) {
			throw new IllegalArgumentException("All of the parameters to this method are required");
		}
		
		// double check the parameter
		if(searchType.equals("id") != true) {
			if(InputUtils.isValidInt(limit, SearchServlet.MIN_LIMIT, SearchServlet.MAX_LIMIT) == false) {
				throw new IllegalArgumentException("Limit parameter must be between '" + SearchServlet.MIN_LIMIT + "' and '" + SearchServlet.MAX_LIMIT + "'");
			}
		}
		
		if(InputUtils.isValid(formatType) == false) {
			formatType = "json";
		} 
		
		if(InputUtils.isValid(sortType) == false) {
			sortType = "id";
		}
		
		// get the results of the search
		String data = null;
		
		if(searchType.equals("id") == true) {
			data = doVenueIdSearch(query, formatType);
		}	
	
		// return the data
		return data;
		
	} // end the doOrganisationSearch method
	
	/**
	 * A private method to undertake a Organisation id search
	 *
	 * @param query      the search query to use
	 * @param formatType the type of data format for the results
	 *
	 * @return           the results of the search
	 */
	private String doVenueIdSearch(String query, String formatType) {
	
		// check the parameters
		if(InputUtils.isValid(query) == false || InputUtils.isValid(formatType) == false) {
			throw new IllegalArgumentException("All of the parameters to this method are required");
		}
		
		if(InputUtils.isValidInt(query) == false) {
			throw new IllegalArgumentException("The query parameter must be a valid integer");
		}
		
		// declare the SQL variables
		String sql = "SELECT venueid, venue_name, street, suburb, state, postcode, latitude, longitude, COUNT(eventid) "
				   + "FROM (SELECT v.venueid, v.venue_name, v.street, v.suburb, s.state, v.postcode, v.latitude, v.longitude, e.eventid "
				   + "      FROM venue v, states s, events e "
				   + "      WHERE v.venueid = ? "
				   + "      AND v.state = s.stateid "
				   + "      AND v.venueid = e.venueid) "
				   + "GROUP BY venueid, venue_name, street, suburb, state, postcode, latitude, longitude";
				   
		// define the paramaters
		String[] sqlParameters = {query};
		
		// declare additional helper variables
		VenueList list     = new VenueList();
		Venue     venue    = null;
		JSONArray jsonList = new JSONArray();
		
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
			
			venue = new Venue(resultSet.getString(1));
			venue.setName(resultSet.getString(2));
			venue.setStreet(resultSet.getString(3));
			venue.setSuburb(resultSet.getString(4));
			venue.setState(resultSet.getString(5));
			venue.setPostcode(resultSet.getString(6));
			venue.setLatitude(resultSet.getString(7));
			venue.setLongitude(resultSet.getString(8));
			venue.setEventCount(resultSet.getString(9));
			venue.setUrl(LinksManager.getVenueLink(resultSet.getString(1)));
		
			// add the contrbutor to this venue
			list.addVenue(venue);
		
		} catch (java.sql.SQLException ex) {
			// return an empty JSON Array
			return jsonList.toString();
		}
		 
		// play nice and tidy up
		resultSet = null;
		results.tidyUp();
		results = null;		
		
		// determine how to format the result
		String data = null;		
		if(formatType.equals("json") == true) {
			data = createJSONOutput(list, null);
		}
		
		// return the data
		return data;
			
	} // end doOrganisationIdSearch method

	/**
	 * A method to take a group of venues and output JSON encoded text
	 *
	 * @param venueList the list of organisations
	 * @param sortOrder     the order to sort the list of organisations as defined in the OrganisationList class
	 *
	 * @return              the JSON encoded string
	 */
	@SuppressWarnings("unchecked")
	private String createJSONOutput(VenueList venueList, Integer sortOrder) {
	
		Set<Venue> venues = null;
		
		if(sortOrder != null) {
			if(InputUtils.isValidInt(sortOrder, VenueList.VENUE_ID_SORT, VenueList.VENUE_NAME_SORT) != false) {
				if(sortOrder == VenueList.VENUE_ID_SORT) {
					venues = venueList.getSortedVenues(VenueList.VENUE_ID_SORT);
				} else {
					venues = venueList.getSortedVenues(VenueList.VENUE_NAME_SORT);
				}
			} else {
				venues = venueList.getVenues();
			}
		} else {
			venues = venueList.getVenues();
		}		
	
		// assume that all sorting and ordering has already been carried out
		// loop through the list of organisations and add them to the new JSON objects
		Iterator iterator = venues.iterator();
		
		// declare helper variables
		JSONArray  list   = new JSONArray();
		JSONObject object = null;
		Venue      venue  = null;
		
		while(iterator.hasNext()) {
		
			// get the organisation
			venue = (Venue)iterator.next();
			
			// start a new JSON object
			object = new JSONObject();
			
			// build the object
			object.put("id", venue.getId());
			object.put("name", venue.getName());
			object.put("street", venue.getStreet());
			object.put("suburb", venue.getSuburb());
			object.put("postcode", venue.getPostcode());
			object.put("latitude", venue.getLatitude());
			object.put("longitude", venue.getLongitude());
			object.put("url", venue.getUrl());
			object.put("totalEventCount", new Integer(venue.getEventCount()));
			
			// add the new object to the array
			list.add(object);		
		}
		
		// return the JSON encoded string
		return list.toString();
			
	} // end the createJSONOutput method
	
} // end class definition