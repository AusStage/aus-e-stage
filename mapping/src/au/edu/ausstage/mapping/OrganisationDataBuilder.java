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

// import additional classes
import java.io.*;
import java.sql.*;
import java.util.*;
import org.w3c.dom.*;
import javax.xml.parsers.*; 
import javax.xml.transform.*; 
import javax.xml.transform.dom.*; 
import javax.xml.transform.stream.*; 

/**
 * A class used to build the data sets used for mapping organisation data
 */
public class OrganisationDataBuilder extends DataBuilder {

	/**
	 * Constructor for this class
	 *
	 * @param manager the DataManager object used to access the database
	 */
	public OrganisationDataBuilder(DataManager manager) {
		super(manager);
	}
	
	/**
	 * A method to search for all contributors matching the search term provided
	 *
	 * @param queryParameter the search term to be used in the query
	 *
	 * @return               the results of the search
	 */	
	public String doSearch(String queryParameter) throws javax.servlet.ServletException {
		return doSearch(queryParameter, "single");
	} // end doSearch method
	
	
	/**
	 * A method to search for all contributors matching the search term provided
	 *
	 * @param queryParameter the search term to be used in the query
	 * @param searchType     the type of search, either for single or multi contributor maps
	 *
	 * @return               the results of the search
	 */	
	public String doSearch(String queryParameter, String searchType) throws javax.servlet.ServletException {
	
		// define private variable to hold results
		StringBuilder results = new StringBuilder();
		
		int recordCount = 0;
		
		// try to connect to the database
		dataManager.connect(); // dataManager defined in parent object
		
		// declare helper variables
		String sql;
		String[] parameters;

		// define the sql
		sql = "SELECT organisationid, "
					 + "name, "
					 + "COUNT(eventid) as eventcount, "
					 + "COUNT(longitude) as pointcount "
					 + "FROM (SELECT DISTINCT search_organisation.organisationid, "
					 + "search_organisation.name, "
					 + "events.eventid, "
					 + "venue.longitude, "
					 + "venue.latitude "
					 + "FROM search_organisation, "
					 + "orgevlink, "
					 + "events, "
					 + "venue "
					 + "WHERE CONTAINS(search_organisation.combined_all, ?, 1) > 0 "
					 + "AND search_organisation.organisationid = orgevlink.organisationid "
					 + "AND events.eventid = orgevlink.eventid "
					 + "AND venue.venueid = events.venueid) "
					 + "GROUP BY organisationid, name "
					 + "ORDER BY name";
					 
		// define the paramaters
		parameters = new String[1];
		parameters[0] = queryParameter;
		
		// get the resultset
		ResultSet resultSet = this.dataManager.executePreparedStatement(sql, parameters);

		// get the contributor URL template
		String urlTemplate = dataManager.getContextParam("organisationURLTemplate");
		
		// start the table
		results.append("<form action=\"\" id=\"results_form\" name=\"results_form\">");
		results.append("<table class=\"searchResults\"><thead><tr><th>Organisation</th><th style=\"width: 65px; \">Events</th><th style=\"width: 65px; \">Mapped Events</th><th style=\"width: 10px; \">&nbsp;</th></tr></thead>");
		
		// add a table footer
		results.append("<tfoot>");
		results.append("<tr><td colspan=\"4\"><ul><li>Click the \"Show Map\" button to view an organisations map</li>");
		//results.append("<li>Alternatively select a contributor and add them to the contributor list to be mapped</li>");
		results.append("<li>Only events at venues that have geographic information in the database can be mapped</li>");
		results.append("<li>Where an organisation has had more than one role at an event, only one marker will be added to the map</li>");
		results.append("</ul></tfoot>");
		
		// start the table body
		results.append("<tbody>");
		
		try {
		
			// style odd rows
			int j = 2;
		
			// build the table by adding individual rows
			while (resultSet.next() && recordCount < 16) {
			
				// is this an odd or even rows
				if(recordCount % j == 1) {
					results.append("<tr class=\"odd\">");
				} else {
					results.append("<tr>");
				}

				// add the contributor page link
				results.append("<td><a href=\"" + urlTemplate.replace("[org-id]", resultSet.getString(1)) + "\" title=\"View record for " + resultSet.getString(2) + " in AusStage\" target=\"ausstage\">");
				results.append(resultSet.getString(2) + "</a></td>");	
				
				// add remaining data
				results.append("<td>" + resultSet.getString(3) + "</td>");
				results.append("<td>" + resultSet.getString(4) + "</td>");
				
				// add selection box
				if(Integer.parseInt(resultSet.getString(4)) > 0) {
					if(searchType.equals("single")) {
						// single map
						results.append("<td><input class=\"ui-state-default ui-corner-all button\" type=\"button\" onclick=\"showOrganisationMap('" + resultSet.getString(1) + "', '" + resultSet.getString(2) + "','" + urlTemplate.replace("[org-id]", resultSet.getString(1)) + "'); return false;\" value=\"Show Map\"/></td></tr>");
					} else {
						// multi map
						results.append("<td><input class=\"ui-state-default ui-corner-all button\" type=\"button\" onclick=\"addOrganisation('" + resultSet.getString(1) + "', '" + resultSet.getString(2) + "','" + urlTemplate.replace("[org-id]", resultSet.getString(1)) + "'); return false;\" value=\"Add\"/></td></tr>");
					}
				} else {
					results.append("<td>&nbsp;</td></tr>\n");
				}
				
				// increment counter
				recordCount++;
			}
			
			// check to see if record count reached
			if(recordCount == 16) {
				results.append("<tr><td colspan=\"4\"><strong>Note: </strong>Record limit of 15 records reached, please adjust your search terms to be more specific</td></tr>");
			}else if(recordCount == 0) {
				// no records were found
				results = new StringBuilder();
				results.append("<table class=\"searchResults\"><tbody><tr class=\"odd\"><td colspan=\"4\" style=\"text-align: center\">No organisations matching the specified search criteria were found.<br/>Please check your criteria and try again</td></tr></tbody></table>");
			}
			
		} catch(java.sql.SQLException ex) {
			throw new javax.servlet.ServletException("Unable to get search results", ex);
		}
		
		// finalise the table
		results.append("</tbody></table></form>");
		
		return results.toString();
		
	} // end doSearch method
	
	/**
	 * A method used to get the the Marker XML for an series of contributors
	 *
	 * @param queryParameter the parameter to determine which record(s) are of interest
	 *
	 * @return               the string representation of the Marker XML
	 */
	public String getMarkerXMLString(String queryParameter) throws javax.servlet.ServletException {
	
		// define private variables
		String sql = null;			// the sql to execute
		String[] parameters = null; // variable to hold sql parameters
		String[] ids = null;        // variable to hold individual contributor id numbers
		
		// get the persistent URL templates
		String eventURLTemplate        = dataManager.getContextParam("eventURLTemplate");        //[event-id]
		String organisationURLTemplate = dataManager.getContextParam("organisationURLTemplate"); //[org-id]
		String venueURLTemplate        = dataManager.getContextParam("venueURLTemplate");        //[venue-id]
		
		// venue, contributor and event variables
		VenueList        venues       = new VenueList();
		OrganisationList trajectories = new OrganisationList();
		Venue            venue        = null;
		Organisation     organisation = null;
		Event            event        = null;
		
		// date management variables
		int firstDate = Integer.MAX_VALUE;
		int lastDate  = Integer.MIN_VALUE;
		String firstDateAsString = "";
		String lastDateAsString  = "";
		
		// event count variables
		int eventCount = 0;
		
		// try to connect to the database
		dataManager.connect(); // dataManager defined in parent object
		
		// check to see if we need to add the contributor name to the event name
		if(queryParameter.indexOf(',') != -1) {
			// yes - so break out the ids into an array
			ids = queryParameter.split(",");
			
			// define the sql
			sql = "SELECT DISTINCT e.eventid, e.event_name, "
			    + "       e.yyyyfirst_date, e.mmfirst_date, e.ddfirst_date, "
				+ "       e.yyyylast_date, e.mmlast_date, e.ddlast_date, "
				+ "       v.venueid, v.venue_name, v.suburb, v.state, v.postcode, "
				+ "       v.latitude, v.longitude, o.organisationid, o.name "
				+ "FROM organisation o, events e, venue v, orgevlink ol "
				+ "WHERE o.organisationid = ANY (";

					   
				// add sufficient place holders for all of the ids
				for(int i = 0; i < ids.length; i++) {
					sql += "?,";
				}
			
				// tidy up the sql
				sql = sql.substring(0, sql.length() -1);
					   
					   
				// finish the sql					
				sql += ") AND o.organisationid = ol.organisationid "
				    + "AND ol.eventid = e.eventid "
				    + "AND e.venueid = v.venueid "
				    + "AND v.longitude IS NOT NULL ";
			
				// define the paramaters
				parameters = ids;

		} else {
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
				parameters = new String[1];
				parameters[0] = queryParameter;
		}
			
		// get the resultset
		ResultSet resultSet = dataManager.executePreparedStatement(sql, parameters);
		
		// build a list of venues, contributors and events
		try {
			// build the documnet by adding individual events
			while (resultSet.next()) {
				// build a list of venues, contributors and events
				
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
					venue.setUrl(venueURLTemplate.replace("[venue-id]", resultSet.getString(9)));
				
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
					organisation.setUrl(organisationURLTemplate.replace("[org-id]", resultSet.getString(16)));
				
					// add the contrbutor to this venue
					venue.addOrganisation(organisation);
				} else {
					organisation = venue.getOrganisation(resultSet.getString(16));
				}
				
				// see if this event has already been added
				if(organisation.hasEvent(resultSet.getString(1)) == false) {
					// add this event to this contributor
					event = new Event(resultSet.getString(1));
					event.setName(resultSet.getString(2));
					event.setFirstDate(buildDate(resultSet.getString(3), resultSet.getString(4), resultSet.getString(5)));
					event.setFirstDisplayDate(buildDisplayDate(resultSet.getString(3), resultSet.getString(4), resultSet.getString(5)));
					event.setUrl(eventURLTemplate.replace("[event-id]", resultSet.getString(1)));
					
					// add this event to this contributor
					organisation.addEvent(event);
				}
				
				// build trajectory data
				if(trajectories.hasOrganisation(resultSet.getString(16)) == false) {
					// make a new contributor
					organisation = new Organisation(resultSet.getString(16));
					organisation.setName(resultSet.getString(17));
					organisation.setUrl(organisationURLTemplate.replace("[org-id]", resultSet.getString(16)));
				
					// add the organisation to this trajectory
					trajectories.addOrganisation(organisation);
				} else {
					organisation = trajectories.getOrganisation(resultSet.getString(16));
				}
				
				// build the date
				String trajDate = buildDate(resultSet.getString(3), resultSet.getString(4), resultSet.getString(5));
				trajDate = trajDate.replace("-", "");
				
				// add the trajectory information
				organisation.addTrajectoryPoint(trajDate, resultSet.getString(14) + "," + resultSet.getString(15));
				
			} // end record set loop
			
			// close the resultset
			resultSet.close();
			
		}catch (java.sql.SQLException ex) {
			throw new javax.servlet.ServletException("Unable to build the list of venues, organisations and events", ex);
		}
		
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
									
				// set the first and last date variables
				firstDate = Integer.MAX_VALUE;
				lastDate  = Integer.MIN_VALUE;
				firstDateAsString = "";
				lastDateAsString  = "";
				
				// reset the event count variable
				eventCount = 0;
				
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
				description.append("<p><a href=\"" + venue.getUrl() + "\" target=\"ausstage\">" + venue.getName() + "</a></p><ul>");
				
				// get a list of contributors
				Set<Organisation> organisations = venue.getSortedOrganisations(Venue.ORGANISATION_NAME_SORT);
				
				// get the iterator for the list of contributors
				Iterator organisationIterator = organisations.iterator();
				
				// iterate over the contributors
				while(organisationIterator.hasNext()) {
					// get the current contributor
					organisation = (Organisation)organisationIterator.next();
					
					// start the description for this contributor
					description.append("<li><a href=\"" + organisation.getUrl() + "\" target=\"ausstage\">" + organisation.getName() + "</a><ul>");
					
					// get all of the events for this contributor
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
				//CDATASection cdata = xmlDoc.createCDATASection(description.toString());
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
			
			/*
			 * build the trajectories 
			 */
			// get the list of venues
			Set<Organisation> trajectoryList = trajectories.getOrganisations();
			
			// get the iterator for the list
			Iterator trajectoriesIterator = trajectoryList.iterator();
			
			// add an element to hold the trajectories
			Element trajectoriesElement = xmlDoc.createElement("trajectories");
			rootElement.appendChild(trajectoriesElement);
			
			// iterate over the contributors
			while(trajectoriesIterator.hasNext()) {
			
				// get the current contributor
				organisation = (Organisation)trajectoriesIterator.next();
				
				// create an element for this contributor
				Element trajectory = xmlDoc.createElement("trajectory");
				trajectoriesElement.appendChild(trajectory);
				
				// add the id to this trajectory
				trajectory.setAttribute("id", organisation.getId());
				
				// get the list of coordinates
				Collection<String> organisationTrajectory = organisation.getTrajectory();
				Iterator organisationTrajectoryIterator   = organisationTrajectory.iterator();
				
				// declare helper variables
				String previousCoords = null;
				String coords         = null;
				
				// loop through the list of coordinates
				while(organisationTrajectoryIterator.hasNext()) {
				
					// get the current coordinates
					coords = (String)organisationTrajectoryIterator.next();
					
					// check to see if the previous coords are available
					if(previousCoords == null) {
						// no they aren't - must be first time through the loop
						previousCoords = coords;
					} else {
						// yes the are - must be the nth time through the loop
						
						// build the xml element to hold the coordinates
						Element trajectoryCoords = xmlDoc.createElement("coords");
						trajectoryCoords.setTextContent(previousCoords + " " + coords);
						
						// add the element to the coordinates
						trajectory.appendChild(trajectoryCoords);
						
						// store these coordinates for the next loop
						previousCoords = coords;
					}
				} // end contributorTrajectoryIterator loop
				
				// add the trajectory to the list of trajectories
				trajectoriesElement.appendChild(trajectory);
				
			} // end trajectoriesIterator loop
			
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
			throw new javax.servlet.ServletException("Unable to build marker xml", ex);
		} catch(javax.xml.transform.TransformerException e) {
			throw new javax.servlet.ServletException("Unable to build marker xml", e);
		}

	} // end getMarkerXMLString method
	
	/**
	 * A abstract method used to get the String representation of the KML document
	 * using the default options
	 *
	 * @param queryParameter the parameter to determine what is of interest
	 *
	 * @return               a string containing the KML XML
	 */
	public String getKMLString(String queryParameter) throws javax.servlet.ServletException {
	 	
		// get the default options
	 	KMLExportOptions exportOptions = new KMLExportOptions();
	 	
	 	// do a KML export with the default options
	 	return doKMLExport(queryParameter, exportOptions);
	 	
	} // end getKMLString function
	
	/**
	  * A method used to get the String representation of the KML document
	  *
	  * @param queryParameter the parameter to determine what is of interest
	  * @param exportOptions  the options used to control this export
	  *
	  * @return               a string containing the KML XML
	  */
	 public String doKMLExport(String queryParameter, KMLExportOptions exportOptions) throws javax.servlet.ServletException {
	 
	 	// define helper variables
	 	String currentContributor = null;
		
		// get the persisten URL template for event
		String eventURLTemplate = dataManager.getContextParam("eventURLTemplate");
		
		// try to connect to the database
		dataManager.connect(); // dataManager defined in parent object
		
		// define the sql
		String sql = "SELECT DISTINCT e.eventid, e.event_name, e.yyyyfirst_date, e.mmfirst_date, e.ddfirst_date, e.yyyylast_date, e.mmlast_date, e.ddlast_date, "
				   + "                v.venue_name, v.street, v.suburb, s.state, v.postcode, c.countryname, "
				   + "                v.longitude, v.latitude "
				   + "FROM conevlink ce, events e, venue v, states s, country c "
				   + "WHERE ce.contributorid = ? "
				   + "AND ce.eventid = e.eventid "
				   + "AND e.venueid = v.venueid "
				   + "AND v.state = s.stateid "
				   + "AND v.countryid = c.countryid "
				   + "AND v.latitude IS NOT NULL "
				   + "ORDER BY e.yyyyfirst_date, e.mmfirst_date, e.ddfirst_date ";
				   
		// build an array of contributor ids
		String[] ids = null;
		String[] parameters = new String[1];
		
		if(queryParameter.indexOf(',') != -1) {
			ids = queryParameter.split(",");
		} else {
			ids = new String[1];
			ids[0] = queryParameter;
		}
		
		try {
			// start a new KML file	
			KMLBuilder exportFile = new KMLBuilder();
			Element firstFolder = exportFile.addFolder("Maps of events by contributor"); // add a folder to hold all of the maps
			
			// add some additional information
			exportFile.addComment("Map data generated: " + getCurrentDateAndTime());	
			exportFile.addComment("Map generated by: " + dataManager.getContextParam("systemName") + " " + dataManager.getContextParam("systemVersion") + " " + dataManager.getContextParam("buildVersion"));
			
			// add author information to this folder
			exportFile.addAuthorElement(firstFolder, dataManager.getContextParam("systemName"), dataManager.getContextParam("systemUrl"));
				
			// add a description element to this folder
			exportFile.addDescriptionElement(firstFolder, "Maps of events by contributor exported from the AusStage system on " + getCurrentDate() + ".");
		
			// add the style
			exportFile.addIconStyle(firstFolder, "basic-event", "http://chart.apis.google.com/chart?cht=mm&chs=32x32&chco=FFFFFF,CCBAD7,000000&ext=.png"); // basic event marker
		
			// loop through each of the contributors 
			for(int contribCount = 0; contribCount < ids.length; contribCount++) {
			
				// define the query parameters
				parameters[0] = ids[contribCount];
				
				// store the current contributor name
				currentContributor = getNameByID(ids[contribCount]);
				
				try {
					// execute the sql
					ResultSet resultSet = dataManager.executePreparedStatement(sql, parameters);
					
					// add a folder for this contributor
					Element contribFolder = exportFile.addFolder(firstFolder, currentContributor); // add a folder to hold all of the maps
					exportFile.addDescriptionElement(contribFolder, "Maps of events associated with: " + currentContributor);
					
					// add a document for the standard map
					Element document = exportFile.addDocument(contribFolder, "Events");
					exportFile.addDescriptionElement(document, "One place marker for each event");
					
					// loop through the dataset adding placemarks to the basic doc
					while (resultSet.next()) {
					
						// build the event url
						String url = eventURLTemplate.replace("[event-id]", resultSet.getString(1));  // replace the constant with the event id
						
						// build the description						
						String html = "";
				
						if (resultSet.getString(11) != null) {
							if(resultSet.getString(6) != null) {
								html = "<p>" + resultSet.getString(9) + ", " + resultSet.getString(11) + ", " + buildDisplayDate(resultSet.getString(3), resultSet.getString(4), resultSet.getString(5)) + " - " + buildDisplayDate(resultSet.getString(6), resultSet.getString(7), resultSet.getString(8));
							} else {
								html = "<p>" + resultSet.getString(9) + ", " + resultSet.getString(11) + ", " + buildDisplayDate(resultSet.getString(3), resultSet.getString(4), resultSet.getString(5));
							}
						} else {
							if(resultSet.getString(6) != null) {
								html = "<p>" + resultSet.getString(9) + ", " + buildDisplayDate(resultSet.getString(3), resultSet.getString(4), resultSet.getString(5)) + " - " + buildDisplayDate(resultSet.getString(6), resultSet.getString(7), resultSet.getString(8));
							} else {
								html = "<p>" + resultSet.getString(9) + ", " + buildDisplayDate(resultSet.getString(3), resultSet.getString(4), resultSet.getString(5));
							}
						}
						
						// add the more information link
						html += " <br/><a href=\"" + url + "\">More Information</a></p>";
						
						// add the placemark
						exportFile.addPlacemark(document, resultSet.getString(2), url, html, "basic-event", resultSet.getString(15), resultSet.getString(16));				
					} // end basic document
					
					// add additional documents as necessary
					if (exportOptions.getOption("includeTimeSpanElements").equals("yes")) {
						// need to add a document that includes timespan elements
						document = exportFile.addDocument(contribFolder, "Events with TimeSpan", false);
						exportFile.addDescriptionElement(document, "One place marker for each event, with time span information");
						
						// rewind the resultset
						resultSet.first();
						
						// loop through the dataset adding placemarks to the basic doc
						while (resultSet.next()) {
						
							// build the event url
							String url = eventURLTemplate.replace("[event-id]", resultSet.getString(1));  // replace the constant with the event id
							
							// build the description						
							String html = "";
					
							if (resultSet.getString(11) != null) {
								if(resultSet.getString(6) != null) {
									html = "<p>" + resultSet.getString(9) + ", " + resultSet.getString(11) + ", " + buildDisplayDate(resultSet.getString(3), resultSet.getString(4), resultSet.getString(5)) + " - " + buildDisplayDate(resultSet.getString(6), resultSet.getString(7), resultSet.getString(8));
								} else {
									html = "<p>" + resultSet.getString(9) + ", " + resultSet.getString(11) + ", " + buildDisplayDate(resultSet.getString(3), resultSet.getString(4), resultSet.getString(5));
								}
							} else {
								if(resultSet.getString(6) != null) {
									html = "<p>" + resultSet.getString(9) + ", " + buildDisplayDate(resultSet.getString(3), resultSet.getString(4), resultSet.getString(5)) + " - " + buildDisplayDate(resultSet.getString(6), resultSet.getString(7), resultSet.getString(8));
								} else {
									html = "<p>" + resultSet.getString(9) + ", " + buildDisplayDate(resultSet.getString(3), resultSet.getString(4), resultSet.getString(5));
								}
							}
							
							// add the more information link
							html += " <br/><a href=\"" + url + "\">More Information</a></p>";
							
							// add the placemark
							Element placemark = exportFile.addPlacemark(document, resultSet.getString(2), url, html, "basic-event", resultSet.getString(15), resultSet.getString(16));
							
							// add the timspan element
							// build the dates
							String begin = this.buildDate(resultSet.getString(3), resultSet.getString(4), resultSet.getString(5));
							String end   = this.buildDate(resultSet.getString(6), resultSet.getString(7), resultSet.getString(8));
							
							// double check the date
							if(!begin.equals("")) {
								// we have at least a beginning date therefore add the element to the placemark
								exportFile.addTimeSpan(placemark, begin, end);
							}				
						}						
					} // end adding timespan elements
					
					// add additional documents as necessary
					if (exportOptions.getOption("includeTrajectoryInfo").equals("yes")) {
					
						// declare additional helper variables
						boolean addTimeSpan = false;
						
						if(exportOptions.getOption("includeTimeSpanElements").equals("yes")) {
							addTimeSpan = true;
						}
						
						String previousCoords = null;
						String previousStart  = null;
						String start          = null;
						String finish         = null;
					
						// need to add a document that includes trajectory info
						document = exportFile.addDocument(contribFolder, "Events with Trajectory", false);
						exportFile.addDescriptionElement(document, "One place marker for each event, linked with trajectory information. Note: Trajectory information links venues based on the earliest event to occur at that venue and does not indicate a tour.");
						
						// rewind the result set
						resultSet.last();
						int rowCount = resultSet.getRow();
						resultSet.first();
						
						if (contribCount == 0) {
							// add the maximum number of line styles
							for(int i = 1; i <= 255; i++) {
								exportFile.addLineStyle("traj-" + i, exportFile.getGradientColour(i - 1, 255), "4");
							}
						}
												
						// loop through the dataset adding placemarks
						while (resultSet.next()) {
						
							// build the event url
							String url = eventURLTemplate.replace("[event-id]", resultSet.getString(1));  // replace the constant with the event id
							
							// build the description						
							String html = "";
					
							if (resultSet.getString(11) != null) {
								if(resultSet.getString(6) != null) {
									html = "<p>" + resultSet.getString(9) + ", " + resultSet.getString(11) + ", " + buildDisplayDate(resultSet.getString(3), resultSet.getString(4), resultSet.getString(5)) + " - " + buildDisplayDate(resultSet.getString(6), resultSet.getString(7), resultSet.getString(8));
								} else {
									html = "<p>" + resultSet.getString(9) + ", " + resultSet.getString(11) + ", " + buildDisplayDate(resultSet.getString(3), resultSet.getString(4), resultSet.getString(5));
								}
							} else {
								if(resultSet.getString(6) != null) {
									html = "<p>" + resultSet.getString(9) + ", " + buildDisplayDate(resultSet.getString(3), resultSet.getString(4), resultSet.getString(5)) + " - " + buildDisplayDate(resultSet.getString(6), resultSet.getString(7), resultSet.getString(8));
								} else {
									html = "<p>" + resultSet.getString(9) + ", " + buildDisplayDate(resultSet.getString(3), resultSet.getString(4), resultSet.getString(5));
								}
							}
							
							// add the more information link
							html += " <br/><a href=\"" + url + "\">More Information</a></p>";
							
							// add the placemark
							Element placemark = exportFile.addPlacemark(document, resultSet.getString(2), url, html, "basic-event", resultSet.getString(15), resultSet.getString(16));
							
							// add the timspan element if required
							if(addTimeSpan == true) {
								// build the dates
								start  = buildDate(resultSet.getString(3), resultSet.getString(4), resultSet.getString(5));
								finish = buildDate(resultSet.getString(6), resultSet.getString(7), resultSet.getString(8));
								
								// double check the date
								if(start.equals("") == false) {
									// we have at least a beginning date therefore add the element to the placemark
									exportFile.addTimeSpan(placemark, start, finish);
								}
							}
							
							// add the trajectory information
							if(previousCoords != null) {
								
								String name    = "trajectory-" + Integer.toString(resultSet.getRow() - 2);
								String styleId = null;
								
								// calculate the style ID
								styleId = "traj-" + (exportFile.getNormalisedColourIndex(resultSet.getRow(), rowCount) - 1);
								
								// create the trajectory element
								Element trajectory = exportFile.addTrajectory(document, name, styleId, previousCoords, resultSet.getString(15) + "," + resultSet.getString(16));
								
								// add the timspan element if required
								if(addTimeSpan == true) {
								
									// build the dates
									String end   = buildDate(resultSet.getString(6), resultSet.getString(7), resultSet.getString(8));
								
									// double check the date
									if(previousStart.equals("") == false) {
										// we have at least a beginning date therefore add the element to the placemark
										if(finish.equals("") == false) {
											exportFile.addTimeSpan(trajectory, previousStart, finish);
										} else {
											exportFile.addTimeSpan(trajectory, previousStart, start);
										}
									}
								}
							}
							
							// store details for next loop iteration
							previousCoords = resultSet.getString(15) + "," + resultSet.getString(16);
							previousStart  = start;
						} // end adding trajectory information
											
					} // end adding trajectory elements
					
					// add events grouped by venue if required
					if (exportOptions.getOption("includeGroupedEventInfo").equals("yes")) {
					
						// need to add a document that includes trajectory info
						document = exportFile.addDocument(contribFolder, "Events Grouped by Venue", false);
						exportFile.addDescriptionElement(document, "One place marker for each venue, with all events that occured at that venue associated with it.");
						
						// add the grouped venue styles
						if (contribCount == 0) {
							exportFile.addGroupedEventIconStyles();
						}
						
						// declare other helper variables
						Map<String, String[]> groupedEventInfo = new HashMap<String, String[]>();
						
						// declare additional helper variables
						boolean addTimeSpan = false;
						
						if(exportOptions.getOption("includeTimeSpanElements").equals("yes")) {
							addTimeSpan = true;
						}
						
						// rewind the result set
						resultSet.first();
						
						// loop through the dataset compiling a list of placemarks
						while (resultSet.next()) {
						
							// build the key for this venue
							String key = resultSet.getString(15) + resultSet.getString(16);
							key = key.replaceAll("\\.","");
							key = key.replaceAll(",", "");
							key = key.replaceAll("-","");
							
							// build the event url
							String url = eventURLTemplate.replace("[event-id]", resultSet.getString(1));  // replace the constant with the event id
							
							// is this venue in the group already
							if(groupedEventInfo.containsKey(key) == false) {
							
								// add a new entry for this venue
								String[] eventEntry = new String[6];
								
								// add the coordinates
								eventEntry[0] = resultSet.getString(15) + "," + resultSet.getString(16);
								
								// add the event name
								eventEntry[1] = resultSet.getString(9);
								
								// build the description for this event								
								String eventDetails = "<ul><li><a href=\"" + url + "\">" + resultSet.getString(2) + "</a>, ";
						
								if (resultSet.getString(11) != null) {
									eventDetails += resultSet.getString(9) + ", " + resultSet.getString(11) + ", " + this.buildDisplayDate(resultSet.getString(3), resultSet.getString(4), resultSet.getString(5)) + " - " + this.buildDisplayDate(resultSet.getString(6), resultSet.getString(7), resultSet.getString(8)) + "</li>";
								} else {
									eventDetails += resultSet.getString(9) + ", " + this.buildDisplayDate(resultSet.getString(3), resultSet.getString(4), resultSet.getString(5)) + " - " + this.buildDisplayDate(resultSet.getString(6), resultSet.getString(7), resultSet.getString(8)) + "</li>";
								}
								
								// add the details for the event to the array
								eventEntry[2] = eventDetails;
								
								// start a count
								eventEntry[3] = "1";
								
								// get the first date
								eventEntry[4] = this.buildDate(resultSet.getString(3), resultSet.getString(4), resultSet.getString(5));
								
								// get the last date
								eventEntry[5] = this.buildDate(resultSet.getString(6), resultSet.getString(7), resultSet.getString(8));
								
								// add this array to the larger collection
								groupedEventInfo.put(key, eventEntry);							
							} else {
							
								// use existing entry for this venue
								String[] eventEntry = groupedEventInfo.get(key);
								
								// get the existing details for this venue
								String eventDetails = eventEntry[2];
											 
								eventDetails += "<li><a href=\"" + url + "\">" + resultSet.getString(2) + "</a>, ";
								
								if (resultSet.getString(11) != null) {
									if(resultSet.getString(6) != null) {
										eventDetails += resultSet.getString(9) + ", " + resultSet.getString(11) + ", " + this.buildDisplayDate(resultSet.getString(3), resultSet.getString(4), resultSet.getString(5)) + " - " + this.buildDisplayDate(resultSet.getString(6), resultSet.getString(7), resultSet.getString(8)) + "</li>";
									} else {
										eventDetails += resultSet.getString(9) + ", " + resultSet.getString(11) + ", " + this.buildDisplayDate(resultSet.getString(3), resultSet.getString(4), resultSet.getString(5)) + "</li>";
									}
								} else {
									if(resultSet.getString(6) != null) {
										eventDetails += resultSet.getString(9) + ", " + this.buildDisplayDate(resultSet.getString(3), resultSet.getString(4), resultSet.getString(5)) + " - " + this.buildDisplayDate(resultSet.getString(6), resultSet.getString(7), resultSet.getString(8)) + "</li>";
									} else {
										eventDetails += resultSet.getString(9) + ", " + this.buildDisplayDate(resultSet.getString(3), resultSet.getString(4), resultSet.getString(5)) + "</li>";
									}
								}
											 
								// update the details back
								eventEntry[2] = eventDetails;
								
								// update the count
								int count = Integer.parseInt(eventEntry[3]);
								count++;
								eventEntry[3] = Integer.toString(count);
								
								// update the last date
								eventEntry[5] = this.buildDate(resultSet.getString(6), resultSet.getString(7), resultSet.getString(8));
								
								// update this venue
								groupedEventInfo.put(key, eventEntry); 
							}
						
						} // end loop
						
						// loop through the hashmap of all of the venues
						for(Map.Entry<String, String[]> mapEntry : groupedEventInfo.entrySet()) {
						
							// get the details for this event
							String[] eventDetails = mapEntry.getValue();
							
							String[] coords = eventDetails[0].split(",");
							
							// determine which style to use
							String style = null;
							int eventCount = Integer.parseInt(eventDetails[3]);
							
							// add the placemark
							if (eventCount == 1) {
								style = "grp-1-event";
							} else if(eventCount > 1 && eventCount < 6) {
								style = "grp-2-5-event";
							} else if(eventCount > 5 && eventCount < 16) {
								style = "grp-6-15-event";
							} else if(eventCount > 15 && eventCount < 31) {
								style = "grp-16-30-event";
							} else {
								style = "grp-30-plus-event";
							}
							
							// add the placemark
							Element placemark = exportFile.addPlacemark(document, eventDetails[1], null, eventDetails[2] + "</ul>", style, coords[0], coords[1]);
							
							// add the time span element if necessary
							if (addTimeSpan == true) {
							
								exportFile.addTimeSpan(placemark, eventDetails[4], eventDetails[5]);							
							}
							
						} // end hashmap loop
					
					}// end adding grouped event info
				
				} catch (java.sql.SQLException e) {
					throw new javax.servlet.ServletException("Unable to build KML xml for contributor: " + currentContributor, e);
				}		
			}
			
			// return the XML
			return exportFile.toString();
			
		} catch (java.lang.Exception e) {
			throw new javax.servlet.ServletException("Unable to build KML xml for contributor: " + currentContributor, e);
		}
		
	 } // end doKMLExport function
	
	/**
	 * Function to get the name of the contributor
	 *
	 * @param queryParameter the parameter to determine which contributor to lookup
	 *
	 * @return               a string containing the name of the contributor
	 */
	public String getNameByID(String queryParameter) throws javax.servlet.ServletException {
	
		// build the sql
		String sql = "SELECT contrib_name FROM search_contributor WHERE contributorid = ?";
		
		// define the parameters
		String[] parameters = new String[1];
		parameters[0] = queryParameter;
		
		// try to connect to the database
		this.dataManager.connect(); // dataManager defined in parent object
		
		// get the resultset
		ResultSet resultSet = this.dataManager.executePreparedStatement(sql, parameters);
		
		try {			
			// move to the first record in the recordset
			resultSet.next();
			
			// return the name
			return resultSet.getString(1);
		} catch(Exception ex) {
				throw new javax.servlet.ServletException("Unable to lookup contributor name", ex);
		}
	} // get the full name of a contributor

} // end class definition
