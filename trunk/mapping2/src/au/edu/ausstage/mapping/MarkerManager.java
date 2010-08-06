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
import java.io.*;
import org.w3c.dom.*;
import javax.xml.parsers.*; 
import javax.xml.transform.*; 
import javax.xml.transform.dom.*; 
import javax.xml.transform.stream.*;

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
	 * A method to return the marker data for an organisation
	 *
	 * @param id the unique identifier for the organisation
	 *
	 * @return   the compiled marker data
	 */
	public String getOrganisationMarkers(String id) {
	
		// check on the parameter
		if(InputUtils.isValidInt(id) == false) {
			throw new IllegalArgumentException("The id parameter must be a valid integer");
		}
		
		// TODO - manage multiple IDs in the id variable
		
		// declare helper variables
		String   sql;
		String[] sqlParameters;
		
		// venue organisation and event objects
		VenueList        venues       = new VenueList();
		Venue            venue        = null;
		Organisation     organisation = null;
		Event            event        = null;
		
		// define the sql
		sql = "SELECT DISTINCT e.eventid, e.event_name, "
		    + "       e.yyyyfirst_date, e.mmfirst_date, e.ddfirst_date, "
			+ "       e.yyyylast_date, e.mmlast_date, e.ddlast_date, "
			+ "       v.venueid, v.venue_name, v.suburb, v.state, v.postcode, "
			+ "       v.latitude, v.longitude, o.organisationid, o.name "
			+ "FROM organisation o, events e, venue v, orgevlink ol "
			+ "WHERE o.organisationid = ? "
			+ "AND o.organisationid = ol.organisationid "
		    + "AND ol.eventid = e.eventid "
		    + "AND e.venueid = v.venueid "
		    + "AND v.longitude IS NOT NULL ";
							 
		// define the paramaters
		sqlParameters = new String[1];
		sqlParameters[0] = id;
		
		// get the data
		DbObjects results = database.executePreparedStatement(sql, sqlParameters);
		
		// check to see that data was returned
		if(results == null) {
			return null;
		}
		
		// build the dataset using internal objects
		// build a list of venues, organisations and events
		ResultSet resultSet = results.getResultSet();
		
		try {
			// build the documnet by adding individual events
			while (resultSet.next()) {
				// build a list of venues, contributors and events
				
				//debug code
				System.out.println("##" + resultSet.getString(9) + "##");
				
				// get the current venue, or make a new one
				if(venues.hasVenue(resultSet.getString(9)) == false) {
					// make a new venue
					venue = new Venue(resultSet.getString(9));
					venue.setName(resultSet.getString(10));
					venue.setSuburb(resultSet.getString(11));
					venue.setState(resultSet.getString(12));
					venue.setPostcode(resultSet.getString(13));
					venue.setLatitude(resultSet.getString(14));
					venue.setLongitude(resultSet.getString(15));
					venue.setUrl(LinksManager.getVenueLink(resultSet.getString(9)));
				
					// add the venue to the list
					venues.addVenue(venue);

				} else {
					venue = venues.getVenue(resultSet.getString(9));
				}
				
				// get this organisation for this venue or create a new one
				if(venue.hasOrganisation(resultSet.getString(16)) == false) {
					// make a new contributor
					organisation = new Organisation(resultSet.getString(16));
					organisation.setName(resultSet.getString(17));
					organisation.setUrl(LinksManager.getOrganisationLink(resultSet.getString(16)));
				
					// add the contrbutor to this venue
					venue.addOrganisation(organisation);
				} else {
					organisation = venue.getOrganisation(resultSet.getString(16));
				}
				
				// see if this event has already been added
				if(organisation.hasEvent(resultSet.getString(1)) == false) {
					// add this event to this organisation
					event = new Event(resultSet.getString(1));
					event.setName(resultSet.getString(2));
					event.setFirstDate(DateUtils.buildDate(resultSet.getString(3), resultSet.getString(4), resultSet.getString(5)));
					event.setFirstDisplayDate(DateUtils.buildDisplayDate(resultSet.getString(3), resultSet.getString(4), resultSet.getString(5)));
					event.setUrl(LinksManager.getEventLink(resultSet.getString(1)));
					
					// add this event to this contributor
					organisation.addEvent(event);
				}
				
			} // end record set loop
			
			// close the resultset
			resultSet.close();
			
		}catch (java.sql.SQLException ex) {
			return null;
		}
		
		//debug code
		return buildOrganisationMarkerXml(venues);
	
	} // end the getOrganisationMarkers method
	
	/**
	 * A method to return the marker data for an organisation
	 *
	 * @param id the unique identifier for the organisation
	 *
	 * @return   the compiled marker data
	 */
	public String getContributorMarkers(String id) {
		return "";
	} // end the getContributorsMarkers method
	
	/**
	 * A private method that takes a venue list and builds the marker XML
	 *
	 * @param venues   the list of venues to process
	 * @param listType the type of list that is being processed 
	 *
	 * @return the XML string representing the markers
	 */
	private String buildOrganisationMarkerXml(VenueList venues) {
	
		// check on the parameters
		if(venues == null) {
			throw new IllegalArgumentException("A list of venues to process must be specified");
		}
		
		// declare AusStage Type variables
		Organisation organisation = null;
		Venue        venue        = null;
		Event        event        = null;
		
		// declare other helper variables
		int eventCount = 0;
		int firstDate  = 0;
		int lastDate   = 0;
		
		String firstDateAsString = null;
		String lastDateAsString  = null;		
	
		// build the marker XML
		try {
			// create the xml document object
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder        builder = factory.newDocumentBuilder();
			Document			   xmlDoc  = builder.newDocument();
			
			// add the root element
			Element rootElement = xmlDoc.createElement("mapdata");
			xmlDoc.appendChild(rootElement);
			
			// add the element for the markers
			Element markers = xmlDoc.createElement("markers");
			rootElement.appendChild(markers);
			
			// get the list of venues
			Set<Venue> venueList = venues.getVenues();
			
			// get the iterator for the list
			Iterator venueIterator = venueList.iterator();
			
			// iterate over the venues
			while(venueIterator.hasNext()) {
				// get the current venue
				venue = (Venue)venueIterator.next();
				
				// add a marker to the xml
				Element marker = xmlDoc.createElement("marker");
				
				// set the venue attributes
				marker.setAttribute("name",     venue.getName());
				marker.setAttribute("suburb",   venue.getSuburb());
				marker.setAttribute("state",    venue.getState());
				marker.setAttribute("postcode", venue.getPostcode());
				marker.setAttribute("lat",      venue.getLatitude());
				marker.setAttribute("lng",      venue.getLongitude());
				
				// build the description
				StringBuilder description = new StringBuilder();
				description.append("<p><a href=\"" + venue.getUrl() + "\" title= \"View the " + venue.getName()+ " record in AusStage\" target=\"ausstage\">" + venue.getName() + "</a></p><ul>");
				
				// get a list of organisations
				Set<Organisation> organisations = venue.getSortedOrganisations(Venue.ORGANISATION_NAME_SORT);
				
				// get the iterator for the list of organisations
				Iterator organisationIterator = organisations.iterator();
				
				// iterate over the organisations
				while(organisationIterator.hasNext()) {
					// get the current organisation
					organisation = (Organisation)organisationIterator.next();
					
					// start the description for this organisation
					description.append("<li><a href=\"" + organisation.getUrl() + "\" target=\"ausstage\">" + organisation.getName() + "</a><ul>");
					
					// get all of the events for this organisation
					Set<Event> events = organisation.getSortedEvents(Organisation.EVENT_FIRST_DATE_SORT);
					
					// keep track of the number of events
					eventCount += events.size();
					
					// get the iterator for the list of events
					Iterator eventIterator = events.iterator();
					
					// iterate over the events
					while(eventIterator.hasNext()) {
						// get the current event
						event = (Event)eventIterator.next();
						
						// add this event
						description.append("<li><a href=\"" + event.getUrl() + "\" target=\"ausstage\">" + event.getName() + "</a>, " + event.getFirstDisplayDate() + "</li>");
						
						// update the date variables
						if(firstDate > event.getFirstDateAsInt()) {
							firstDate = event.getFirstDateAsInt();
							firstDateAsString = event.getFirstDate();
						}
						
						if(lastDate < event.getFirstDateAsInt()) {
							lastDate = event.getFirstDateAsInt();
							lastDateAsString = event.getFirstDate();
						}						
					}
					
					// finish the list of events & contributor
					description.append("</ul></li>");
				}
				
				// finish the list of contributors
				description.append("</ul>");
								
				// add the description to the venue
				//marker.appendChild(cdata);
				marker.setTextContent(description.toString());
				
				// add the date attributes
				marker.setAttribute("fdate", Integer.toString(firstDate));
				marker.setAttribute("ldate", Integer.toString(lastDate));
				marker.setAttribute("fdatestr", firstDateAsString);
				marker.setAttribute("ldatestr", lastDateAsString);
				
				// add the event count attributes
				marker.setAttribute("events", Integer.toString(eventCount));
				
				// add the marker to the XML
				markers.appendChild(marker);
			}
			
			// create a transformer 
			TransformerFactory transFactory = TransformerFactory.newInstance();
			Transformer        transformer  = transFactory.newTransformer();
			
			// set some options on the transformer
			transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
			transformer.setOutputProperty(OutputKeys.INDENT, "no");
			
			// get a transformer and supporting classes
			StringWriter writer = new StringWriter();
			StreamResult result = new StreamResult(writer);
			DOMSource    source = new DOMSource(xmlDoc);
			
			// transform the xml document into a string
			transformer.transform(source, result);
			return writer.toString();			
			
		} catch(javax.xml.parsers.ParserConfigurationException ex) {
			return null;
		} catch(javax.xml.transform.TransformerException e) {
			return null;
		}
	
	} // end buildMarkerXml method

} // end class definition
