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
			sql = "SELECT v.venueid, v.venue_name, v.suburb, s.state, v.postcode, v.latitude, v.longitude "
				+ "FROM venue v, states s "
				+ "WHERE v.state < ? "
				+ "AND v.state = s.stateid "
				+ "AND latitude IS NOT NULL";
		} else {
			// international venues
			sql = "SELECT v.venueid, v.venue_name, v.suburb, s.state, v.postcode, v.latitude, v.longitude "
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
		String sql = "SELECT v.venueid, v.venue_name, v.suburb, s.state, v.postcode, v.latitude, v.longitude "
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
			sql = "SELECT v.venueid, v.venue_name, v.suburb, s.state, v.postcode, v.latitude, v.longitude "
				+ "FROM venue v, states s "
				+ "WHERE v.venueid = ? "
				+ "AND v.state = s.stateid "
				+ "AND latitude IS NOT NULL ";
		} else {
			sql = "SELECT v.venueid, v.venue_name, v.suburb, s.state, v.postcode, v.latitude, v.longitude "
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
				venue.setSuburb(resultSet.getString(3));
				venue.setState(resultSet.getString(4));
				venue.setPostcode(resultSet.getString(5));
				venue.setLatitude(resultSet.getString(6));
				venue.setLongitude(resultSet.getString(7));
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
		return list.toString();
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
	
	
	
	
//	/**
//	 * A method to return the marker data for an organisation
//	 *
//	 * @param id the unique identifier for the organisation
//	 *
//	 * @return   the compiled marker data
//	 */
//	public String getOrganisationMarkers(String id) {
//		
//		// declare helper variables
//		String[] ids = null;	
//	
//		// check on the parameter
//		if(id.indexOf(',') == -1) {
//			// a single id
//			if(InputUtils.isValidInt(id) == false) {
//				throw new IllegalArgumentException("The id parameter must be a valid integer");
//			}
//		} else {
//			// multiple ids
//			ids = id.split(",");
//			
//			if(InputUtils.isValidArrayInt(ids) == false) {
//				throw new IllegalArgumentException("The id parameter must contain a list of valid integers seperated by commas only");
//			}
//		}		
//		
//		// declare helper variables
//		String   sql;
//		String[] sqlParameters;
//		
//		// venue organisation and event objects
//		VenueList        venues        = new VenueList();
//		OrganisationList orgList       = new OrganisationList();
//		Venue            venue         = null;
//		Organisation     organisation  = null;
//		Event            event         = null;
//		
//		// determine what type of SQL to build
//		if(ids == null) {
//			// only one id to use in the query
//		
//			// define the sql
//			sql = "SELECT DISTINCT e.eventid, e.event_name, "
//				+ "       e.yyyyfirst_date, e.mmfirst_date, e.ddfirst_date, "
//				+ "       e.yyyylast_date, e.mmlast_date, e.ddlast_date, "
//				+ "       v.venueid, v.venue_name, v.suburb, v.state, v.postcode, "
//				+ "       v.latitude, v.longitude, o.organisationid, o.name "
//				+ "FROM organisation o, events e, venue v, orgevlink ol "
//				+ "WHERE o.organisationid = ? "
//				+ "AND o.organisationid = ol.organisationid "
//				+ "AND ol.eventid = e.eventid "
//				+ "AND e.venueid = v.venueid "
//				+ "AND v.longitude IS NOT NULL ";
//								 
//			// define the paramaters
//			sqlParameters = new String[1];
//			sqlParameters[0] = id;
//			
//		} else {
//			// multiple ids to use in the query
//			
//			// define the sql
//			sql = "SELECT DISTINCT e.eventid, e.event_name, "
//			    + "       e.yyyyfirst_date, e.mmfirst_date, e.ddfirst_date, "
//				+ "       e.yyyylast_date, e.mmlast_date, e.ddlast_date, "
//				+ "       v.venueid, v.venue_name, v.suburb, v.state, v.postcode, "
//				+ "       v.latitude, v.longitude, o.organisationid, o.name "
//				+ "FROM organisation o, events e, venue v, orgevlink ol "
//				+ "WHERE o.organisationid = ANY (";
//					   
//			// add sufficient place holders for all of the ids
//			for(int i = 0; i < ids.length; i++) {
//				sql += "?,";
//			}
//		
//			// tidy up the sql
//			sql = sql.substring(0, sql.length() -1);
//				   
//				   
//			// finish the sql					
//			sql += ") AND o.organisationid = ol.organisationid "
//			    + "AND ol.eventid = e.eventid "
//			    + "AND e.venueid = v.venueid "
//			    + "AND v.longitude IS NOT NULL ";
//		
//			// define the paramaters
//			sqlParameters = ids;
//		}
//		
//		// get the data
//		DbObjects results = database.executePreparedStatement(sql, sqlParameters);
//		
//		// check to see that data was returned
//		if(results == null) {
//			return null;
//		}
//		
//		// build the dataset using internal objects
//		// build a list of venues, organisations and events
//		ResultSet resultSet = results.getResultSet();
//		
//		try {
//			// build the documnet by adding individual events
//			while (resultSet.next()) {
//				// build a list of venues, contributors and events
//				
//				// get the current venue, or make a new one
//				if(venues.hasVenue(resultSet.getString(9)) == false) {
//					// make a new venue
//					venue = new Venue(resultSet.getString(9));
//					venue.setName(resultSet.getString(10));
//					venue.setSuburb(resultSet.getString(11));
//					venue.setState(resultSet.getString(12));
//					venue.setPostcode(resultSet.getString(13));
//					venue.setLatitude(resultSet.getString(14));
//					venue.setLongitude(resultSet.getString(15));
//					venue.setUrl(LinksManager.getVenueLink(resultSet.getString(9)));
//				
//					// add the venue to the list
//					venues.addVenue(venue);

//				} else {
//					venue = venues.getVenue(resultSet.getString(9));
//				}
//				
//				// keep a list of organisations
//				if(orgList.hasOrganisation(resultSet.getString(16)) == false) {
//					// add a new organisation
//					// make a new organisation
//					organisation = new Organisation(resultSet.getString(16));
//					organisation.setName(resultSet.getString(17));
//					organisation.setUrl(LinksManager.getOrganisationLink(resultSet.getString(16)));
//				
//					// add the contrbutor to this venue
//					orgList.addOrganisation(organisation);
//				}
//				
//				// get this organisation for this venue or create a new one
//				if(venue.hasOrganisation(resultSet.getString(16)) == false) {

//					// make a new organisation
//					organisation = new Organisation(resultSet.getString(16));
//					organisation.setName(resultSet.getString(17));
//					organisation.setUrl(LinksManager.getOrganisationLink(resultSet.getString(16)));
//				
//					// add the contrbutor to this venue
//					venue.addOrganisation(organisation);
//				} else {
//					organisation = venue.getOrganisation(resultSet.getString(16));
//				}
//				
//				// see if this event has already been added
//				if(organisation.hasEvent(resultSet.getString(1)) == false) {
//					// add this event to this organisation
//					event = new Event(resultSet.getString(1));
//					event.setName(resultSet.getString(2));
//					event.setFirstDate(DateUtils.buildDate(resultSet.getString(3), resultSet.getString(4), resultSet.getString(5)));
//					event.setFirstDisplayDate(DateUtils.buildDisplayDate(resultSet.getString(3), resultSet.getString(4), resultSet.getString(5)));
//					event.setUrl(LinksManager.getEventLink(resultSet.getString(1)));
//					
//					// add this event to this contributor
//					organisation.addEvent(event);
//				}
//				
//			} // end record set loop
//			
//			// close the resultset
//			resultSet.close();
//			
//		}catch (java.sql.SQLException ex) {
//			return null;
//		}
//		
//		// declare remaining helper variables
//		int eventCount = 0;
//		int firstDate  = Integer.MAX_VALUE;
//		int lastDate   = Integer.MIN_VALUE;
//		Set<Organisation> organisations = null;
//		Iterator organisationIterator   = null;
//		
//		String firstDateAsString = null;
//		String lastDateAsString  = null;		
//	
//		// build the marker XML
//		try {
//			// create the xml document object
//			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
//			DocumentBuilder        builder = factory.newDocumentBuilder();
//			Document			   xmlDoc  = builder.newDocument();
//			
//			// add the root element
//			Element rootElement = xmlDoc.createElement("mapdata");
//			xmlDoc.appendChild(rootElement);
//			
//			// add the element for the markers
//			Element markers = xmlDoc.createElement("markers");
//			rootElement.appendChild(markers);
//			
//			// add the element for the names
//			Element entities = xmlDoc.createElement("entities");
//			rootElement.appendChild(entities);
//			
//			// get the list of venues
//			Set<Venue> venueList = venues.getVenues();
//			
//			// get the iterator for the list
//			Iterator venueIterator = venueList.iterator();
//			
//			// iterate over the venues
//			while(venueIterator.hasNext()) {
//				// get the current venue
//				venue = (Venue)venueIterator.next();
//				
//				// reset the event count variable
//				eventCount = 0;
//				
//				// reset the first and last date variables
//				firstDate = Integer.MAX_VALUE;
//				lastDate  = Integer.MIN_VALUE;
//				firstDateAsString = "";
//				lastDateAsString  = "";
//				
//				// add a marker to the xml
//				Element marker = xmlDoc.createElement("marker");
//				
//				// set the venue attributes
//				marker.setAttribute("name",     venue.getName());
//				marker.setAttribute("suburb",   venue.getSuburb());
//				marker.setAttribute("state",    venue.getState());
//				marker.setAttribute("postcode", venue.getPostcode());
//				marker.setAttribute("lat",      venue.getLatitude());
//				marker.setAttribute("lng",      venue.getLongitude());
//				
//				// build the description
//				StringBuilder description = new StringBuilder();
//				description.append("<p><a href=\"" + venue.getUrl() + "\" title= \"View the " + venue.getName()+ " record in AusStage\" target=\"ausstage\">" + venue.getName() + "</a></p><ul>");
//				
//				// get a list of organisations
//				organisations = venue.getSortedOrganisations(Venue.ORGANISATION_NAME_SORT);
//				
//				// get the iterator for the list of organisations
//				organisationIterator = organisations.iterator();
//				
//				// iterate over the organisations
//				while(organisationIterator.hasNext()) {
//					// get the current organisation
//					organisation = (Organisation)organisationIterator.next();					
//					
//					// start the description for this organisation
//					description.append("<li><a href=\"" + organisation.getUrl() + "\" title=\"View the " + organisation.getName() + " record in AusStage\" target=\"ausstage\">" + organisation.getName() + "</a><ul>");
//					
//					// get all of the events for this organisation
//					Set<Event> events = organisation.getSortedEvents(Organisation.EVENT_FIRST_DATE_SORT);
//					
//					// keep track of the number of events
//					eventCount += events.size();
//					
//					// get the iterator for the list of events
//					Iterator eventIterator = events.iterator();
//					
//					// iterate over the events
//					while(eventIterator.hasNext()) {
//						// get the current event
//						event = (Event)eventIterator.next();
//						
//						// add this event
//						description.append("<li><a href=\"" + event.getUrl() + "\" title=\"View the " +  event.getName() + " record in AusStage\" target=\"ausstage\">" + event.getName() + "</a>, " + event.getFirstDisplayDate() + "</li>");
//						
//						// update the date variables
//						if(firstDate > event.getFirstDateAsInt()) {
//							firstDate = event.getFirstDateAsInt();
//							firstDateAsString = event.getFirstDate();
//						}
//						
//						if(lastDate < event.getFirstDateAsInt()) {
//							lastDate = event.getFirstDateAsInt();
//							lastDateAsString = event.getFirstDate();
//						}						
//					}
//					
//					// finish the list of events & contributor
//					description.append("</ul></li>");
//				}
//				
//				// finish the list of contributors
//				description.append("</ul>");
//								
//				// add the description to the venue
//				//marker.appendChild(cdata);
//				marker.setTextContent(description.toString());
//				
//				// add the date attributes
//				marker.setAttribute("fdate", Integer.toString(firstDate));
//				marker.setAttribute("ldate", Integer.toString(lastDate));
//				marker.setAttribute("fdatestr", firstDateAsString);
//				marker.setAttribute("ldatestr", lastDateAsString);
//				
//				// add the event count attributes
//				marker.setAttribute("events", Integer.toString(eventCount));
//				
//				// add the marker to the XML
//				markers.appendChild(marker);
//			}
//			
//			// add the list of organisations
//			organisations = orgList.getSortedOrganisations(OrganisationList.ORGANISATION_NAME_SORT);
//			
//			organisationIterator = organisations.iterator();
//				
//			// iterate over the organisations
//			while(organisationIterator.hasNext()) {
//			
//				// get the current organisation
//				organisation = (Organisation)organisationIterator.next();
//			
//				Element entity = xmlDoc.createElement("entity");
//				entity.setAttribute("id",   organisation.getId());
//				entity.setAttribute("name", organisation.getName());
//				entity.setAttribute("url",  organisation.getUrl());
//				
//				entities.appendChild(entity);
//			}			
//			
//			// create a transformer 
//			TransformerFactory transFactory = TransformerFactory.newInstance();
//			Transformer        transformer  = transFactory.newTransformer();
//			
//			// set some options on the transformer
//			transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
//			transformer.setOutputProperty(OutputKeys.INDENT, "no");
//			
//			// get a transformer and supporting classes
//			StringWriter writer = new StringWriter();
//			StreamResult result = new StreamResult(writer);
//			DOMSource    source = new DOMSource(xmlDoc);
//			
//			// transform the xml document into a string
//			transformer.transform(source, result);
//			return writer.toString();			
//			
//		} catch(javax.xml.parsers.ParserConfigurationException ex) {
//			return null;
//		} catch(javax.xml.transform.TransformerException e) {
//			return null;
//		}
//	
//	} // end the getOrganisationMarkers method
//	
//	/**
//	 * A method to return the marker data for an organisation
//	 *
//	 * @param id the unique identifier for the organisation
//	 *
//	 * @return   the compiled marker data
//	 */
//	public String getContributorMarkers(String id) {
//		
//		// declare helper variables
//		String[] ids = null;	
//	
//		// check on the parameter
//		if(id.indexOf(',') == -1) {
//			// a single id
//			if(InputUtils.isValidInt(id) == false) {
//				throw new IllegalArgumentException("The id parameter must be a valid integer");
//			}
//		} else {
//			// multiple ids
//			ids = id.split(",");
//			
//			if(InputUtils.isValidArrayInt(ids) == false) {
//				throw new IllegalArgumentException("The id parameter must contain a list of valid integers seperated by commas only");
//			}
//		}		
//		
//		// declare helper variables
//		String   sql;
//		String[] sqlParameters;
//		
//		// venue organisation and event objects
//		VenueList        venues            = new VenueList();
//		ContributorList  contribList       = new ContributorList();
//		Venue            venue             = null;
//		Contributor      contributor       = null;
//		Event            event             = null;
//		
//		// determine what type of SQL to build
//		if(ids == null) {
//			// only one id to use in the query
//		
//			// define the sql
//			sql = "SELECT DISTINCT e.eventid, e.event_name, "
//				+ "       e.yyyyfirst_date, e.mmfirst_date, e.ddfirst_date, "
//				+ "       e.yyyylast_date, e.mmlast_date, e.ddlast_date, "
//				+ "       v.venueid, v.venue_name, v.suburb, v.state, v.postcode, "
//				+ "       v.latitude, v.longitude, c.contributorid, sc.contrib_name "
//				+ "FROM conevlink c, "
//				+ "     events e, "
//				+ "     venue v, "
//				+ "     search_contributor sc "
//				+ "WHERE c.contributorid = ? "
//				+ "AND c.contributorid = sc.contributorid "
//				+ "AND e.eventid = c.eventid "
//				+ "AND v.venueid = e.venueid "
//				+ "AND v.longitude IS NOT NULL";
//								 
//			// define the paramaters
//			sqlParameters = new String[1];
//			sqlParameters[0] = id;
//			
//		} else {
//			// multiple ids to use in the query
//			
//			//define the sql
//			sql = "SELECT DISTINCT e.eventid, e.event_name, "
//				+ "      e.yyyyfirst_date, e.mmfirst_date, e.ddfirst_date, "
//				+ "       e.yyyylast_date, e.mmlast_date, e.ddlast_date, "
//				+ "       v.venueid, v.venue_name, v.suburb, v.state, v.postcode, "
//				+ "       v.latitude, v.longitude, c.contributorid, sc.contrib_name "
//				+ "FROM conevlink c, "
//				+ "     events e, "
//				+ "     venue v, "
//				+ "     search_contributor sc "
//				+ "WHERE c.contributorid = ANY (";
//			
//				// add sufficient place holders for all of the ids
//				for(int i = 0; i < ids.length; i++) {
//					sql += "?,";
//				}
//			
//				// tidy up the sql
//				sql = sql.substring(0, sql.length() -1);
//			
//				// finish the sql					
//				sql += ") AND c.contributorid = sc.contributorid "
//				+ "AND e.eventid = c.eventid "
//				+ "AND v.venueid = e.venueid "
//				+ "AND v.longitude IS NOT NULL";
//		
//			// define the paramaters
//			sqlParameters = ids;
//		}
//		
//		// get the data
//		DbObjects results = database.executePreparedStatement(sql, sqlParameters);
//		
//		// check to see that data was returned
//		if(results == null) {
//			return null;
//		}
//		
//		// build the dataset using internal objects
//		// build a list of venues, organisations and events
//		ResultSet resultSet = results.getResultSet();
//		
//		try {
//			// build the documnet by adding individual events
//			while (resultSet.next()) {
//				// build a list of venues, contributors and events
//				
//				// get the current venue, or make a new one
//				if(venues.hasVenue(resultSet.getString(9)) == false) {
//					// make a new venue
//					venue = new Venue(resultSet.getString(9));
//					venue.setName(resultSet.getString(10));
//					venue.setSuburb(resultSet.getString(11));
//					venue.setState(resultSet.getString(12));
//					venue.setPostcode(resultSet.getString(13));
//					venue.setLatitude(resultSet.getString(14));
//					venue.setLongitude(resultSet.getString(15));
//					venue.setUrl(LinksManager.getVenueLink(resultSet.getString(9)));
//				
//					// add the venue to the list
//					venues.addVenue(venue);

//				} else {
//					venue = venues.getVenue(resultSet.getString(9));
//				}
//				
//				// keep a list of contributors
//				if(contribList.hasContributor(resultSet.getString(16)) == false) {
//					// add a new contributor
//					contributor = new Contributor(resultSet.getString(16));
//					contributor.setName(resultSet.getString(17));
//					contributor.setUrl(LinksManager.getContributorLink(resultSet.getString(16)));
//				
//					// add the contrbutor to this venue
//					contribList.addContributor(contributor);
//				}
//				
//				// get this organisation for this venue or create a new one
//				if(venue.hasContributor(resultSet.getString(16)) == false) {

//					// add a new contributor
//					contributor = new Contributor(resultSet.getString(16));
//					contributor.setName(resultSet.getString(17));
//					contributor.setUrl(LinksManager.getContributorLink(resultSet.getString(16)));
//				
//					// add the contrbutor to this venue
//					venue.addContributor(contributor);
//				} else {
//					contributor = venue.getContributor(resultSet.getString(16));
//				}
//				
//				// see if this event has already been added
//				if(contributor.hasEvent(resultSet.getString(1)) == false) {
//					// add this event to this organisation
//					event = new Event(resultSet.getString(1));
//					event.setName(resultSet.getString(2));
//					event.setFirstDate(DateUtils.buildDate(resultSet.getString(3), resultSet.getString(4), resultSet.getString(5)));
//					event.setFirstDisplayDate(DateUtils.buildDisplayDate(resultSet.getString(3), resultSet.getString(4), resultSet.getString(5)));
//					event.setUrl(LinksManager.getEventLink(resultSet.getString(1)));
//					
//					// add this event to this contributor
//					contributor.addEvent(event);
//				}
//				
//			} // end record set loop
//			
//			// close the resultset
//			resultSet.close();
//			
//		}catch (java.sql.SQLException ex) {
//			return null;
//		}
//		
//		// declare remaining helper variables
//		int eventCount = 0;
//		int firstDate  = 0;
//		int lastDate   = 0;
//		Set<Contributor> contributors  = null;
//		Iterator contributorIterator   = null;
//		
//		String firstDateAsString = null;
//		String lastDateAsString  = null;		
//	
//		// build the marker XML
//		try {
//			// create the xml document object
//			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
//			DocumentBuilder        builder = factory.newDocumentBuilder();
//			Document			   xmlDoc  = builder.newDocument();
//			
//			// add the root element
//			Element rootElement = xmlDoc.createElement("mapdata");
//			xmlDoc.appendChild(rootElement);
//			
//			// add the element for the markers
//			Element markers = xmlDoc.createElement("markers");
//			rootElement.appendChild(markers);
//			
//			// add the element for the names
//			Element entities = xmlDoc.createElement("entities");
//			rootElement.appendChild(entities);
//			
//			// get the list of venues
//			Set<Venue> venueList = venues.getVenues();
//			
//			// get the iterator for the list
//			Iterator venueIterator = venueList.iterator();
//			
//			// iterate over the venues
//			while(venueIterator.hasNext()) {
//				// get the current venue
//				venue = (Venue)venueIterator.next();
//				
//				// reset the event count variable
//				eventCount = 0;
//				
//				// reset the first and last date variables
//				firstDate = Integer.MAX_VALUE;
//				lastDate  = Integer.MIN_VALUE;
//				firstDateAsString = "";
//				lastDateAsString  = "";
//				
//				// add a marker to the xml
//				Element marker = xmlDoc.createElement("marker");
//				
//				// set the venue attributes
//				marker.setAttribute("name",     venue.getName());
//				marker.setAttribute("suburb",   venue.getSuburb());
//				marker.setAttribute("state",    venue.getState());
//				marker.setAttribute("postcode", venue.getPostcode());
//				marker.setAttribute("lat",      venue.getLatitude());
//				marker.setAttribute("lng",      venue.getLongitude());
//				
//				// build the description
//				StringBuilder description = new StringBuilder();
//				description.append("<p><a href=\"" + venue.getUrl() + "\" title= \"View the " + venue.getName()+ " record in AusStage\" target=\"ausstage\">" + venue.getName() + "</a></p><ul>");
//				
//				// get a list of organisations
//				contributors = venue.getSortedContributors(Venue.CONTRIBUTOR_NAME_SORT);
//				
//				// get the iterator for the list of organisations
//				contributorIterator = contributors.iterator();
//				
//				// iterate over the organisations
//				while(contributorIterator.hasNext()) {
//					// get the current organisation
//					contributor = (Contributor)contributorIterator.next();					
//					
//					// start the description for this organisation
//					description.append("<li><a href=\"" + contributor.getUrl() + "\" title=\"View the " + contributor.getName() + " record in AusStage\" target=\"ausstage\">" + contributor.getName() + "</a><ul>");
//					
//					// get all of the events for this organisation
//					Set<Event> events = contributor.getSortedEvents(Contributor.EVENT_FIRST_DATE_SORT);
//					
//					// keep track of the number of events
//					eventCount += events.size();
//					
//					// get the iterator for the list of events
//					Iterator eventIterator = events.iterator();
//					
//					// iterate over the events
//					while(eventIterator.hasNext()) {
//						// get the current event
//						event = (Event)eventIterator.next();
//						
//						// add this event
//						description.append("<li><a href=\"" + event.getUrl() + "\" title=\"View the " +  event.getName() + " record in AusStage\" target=\"ausstage\">" + event.getName() + "</a>, " + event.getFirstDisplayDate() + "</li>");
//						
//						// update the date variables
//						if(firstDate > event.getFirstDateAsInt()) {
//							firstDate = event.getFirstDateAsInt();
//							firstDateAsString = event.getFirstDate();
//						}
//						
//						if(lastDate < event.getFirstDateAsInt()) {
//							lastDate = event.getFirstDateAsInt();
//							lastDateAsString = event.getFirstDate();
//						}						
//					}
//					
//					// finish the list of events & contributor
//					description.append("</ul></li>");
//				}
//				
//				// finish the list of contributors
//				description.append("</ul>");
//								
//				// add the description to the venue
//				//marker.appendChild(cdata);
//				marker.setTextContent(description.toString());
//				
//				// add the date attributes
//				marker.setAttribute("fdate", Integer.toString(firstDate));
//				marker.setAttribute("ldate", Integer.toString(lastDate));
//				marker.setAttribute("fdatestr", firstDateAsString);
//				marker.setAttribute("ldatestr", lastDateAsString);
//				
//				// add the event count attributes
//				marker.setAttribute("events", Integer.toString(eventCount));
//				
//				// add the marker to the XML
//				markers.appendChild(marker);
//			}
//			
//			// add the list of organisations
//			contributors = contribList.getSortedContributors(ContributorList.CONTRIBUTOR_NAME_SORT);
//			
//			contributorIterator = contributors.iterator();
//				
//			// iterate over the organisations
//			while(contributorIterator.hasNext()) {
//			
//				// get the current organisation
//				contributor = (Contributor)contributorIterator.next();
//			
//				Element entity = xmlDoc.createElement("entity");
//				entity.setAttribute("id",   contributor.getId());
//				entity.setAttribute("name", contributor.getName());
//				entity.setAttribute("url",  contributor.getUrl());
//				
//				entities.appendChild(entity);
//			}			
//			
//			// create a transformer 
//			TransformerFactory transFactory = TransformerFactory.newInstance();
//			Transformer        transformer  = transFactory.newTransformer();
//			
//			// set some options on the transformer
//			transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
//			transformer.setOutputProperty(OutputKeys.INDENT, "no");
//			
//			// get a transformer and supporting classes
//			StringWriter writer = new StringWriter();
//			StreamResult result = new StreamResult(writer);
//			DOMSource    source = new DOMSource(xmlDoc);
//			
//			// transform the xml document into a string
//			transformer.transform(source, result);
//			return writer.toString();			
//			
//		} catch(javax.xml.parsers.ParserConfigurationException ex) {
//			return null;
//		} catch(javax.xml.transform.TransformerException e) {
//			return null;
//		}
//		
//	} // end the getContributorsMarkers method
	
} // end class definition
