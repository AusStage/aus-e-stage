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

	// declare private variables
	private String orgIdCache;
	private String orgNameCache;

	/**
	 * Constructor for this class
	 *
	 * @param manager the DataManager object used to access the database
	 */
	public OrganisationDataBuilder(DataManager manager) {
		super(manager);
	}
	
	/**
	 * A method to search for all organisations matching the search term provided
	 *
	 * @param queryParameter the search term to be used in the query
	 *
	 * @return               the results of the search
	 */	
	public String doSearch(String queryParameter) throws javax.servlet.ServletException {
	
		// define private variable to hold results
		StringBuilder results = new StringBuilder();
		
		int recordCount = 0;
		
		// try to connect to the database
		this.dataManager.connect(); // dataManager defined in parent object
		
		// define the sql
		String sql = "SELECT organisationid, "
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
		String[] parameters = new String[1];
		parameters[0] = queryParameter;
					 
		// get the resultset
		ResultSet resultSet = this.dataManager.executePreparedStatement(sql, parameters);
		
		// start the table
		results.append("<table class=\"searchResults\"><thead><tr><th>Organisation</th><th>Events in Database</th><th>Events to be Mapped</th></tr></thead>");
		
		// add a table footer
		results.append("<tfoot><tr><td colspan=\"3\">");
		results.append("<ul><li>Click the name of the organisation to view a map</li>");
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
				
				// should we use this organisation to build a map?
				if(Integer.parseInt(resultSet.getString(4)) > 0) {
					// yes
					results.append("<td><a href=\"#\" onclick=\"return showOrgMap('" + resultSet.getString(1) + "'); return false;\">" + resultSet.getString(2) + "</a></td>");
				} else {
					// no
					results.append("<td>" + resultSet.getString(2) + "</td>");
				}
				
				// add remainind data
				results.append("<td>" + resultSet.getString(3) + "</td>");
				results.append("<td>" + resultSet.getString(4) + "</td></tr>");
				
				// increment counter
				recordCount++;
			}
			
			// check to see if record count reached
			if(recordCount == 16) {
				results.append("<tr><td colspan=\"3\"><strong>Note: </strong>Record limit of 15 records reached, please adjust your search terms to be more specific</td></tr>");
			}
			
		} catch(java.sql.SQLException ex) {
			throw new javax.servlet.ServletException("Unable to get search results", ex);
		}
		
		// finalise the table
		results.append("</tbody></table>");
		
		return results.toString();
	}
	
	/**
	 * A method used to get the Marker XML for an organisation
	 *
	 * @param queryParameter the parameter to determine which organisation is of interest
	 *
	 * @return               the string representation of the Marker XML
	 */
	public String getMarkerXMLString(String queryParameter) throws javax.servlet.ServletException {
		return getMarkerXMLString(queryParameter, null, null);
	}
	
	/**
	 * A method used to get the the Marker XML for an organisation restricted to a date range
	 * using the first date fields in the database
	 *
	 * @param queryParameter the parameter to determine which organisation is of interest
	 * @param startDate      the start date of the date range limit
	 * @param finishDate     the finish date of the date range limit
	 *
	 * @return               the string representation of the Marker XML
	 */
	public String getMarkerXMLString(String queryParameter, String startDate, String finishDate) throws javax.servlet.ServletException {
	
		// define private variables
		String xmlString;			// string to hold xml 
		int recordCount = 0;		// count number of markers created
		String sql = null;			// the sql to execute
		String[] parameters = null; // variable to hold sql parameters
				
		
		// get the persistent URL template for event
		String eventURLTemplate = this.dataManager.getContextParam("eventURLTemplate");
		
		// try to connect to the database
		this.dataManager.connect(); // dataManager defined in parent object
		
		// define the sql
		if(startDate == null || finishDate == null) {
			// define the standard sql
			sql = "SELECT DISTINCT orgevlink.eventid, events.event_name, events.yyyyfirst_date, events.mmfirst_date, events.ddfirst_date, "
					   + "events.yyyylast_date, events.mmlast_date, events.ddlast_date, "
					   + "venue.venue_name, venue.suburb, venue.latitude, venue.longitude "
					   + "FROM orgevlink, events, venue "
					   + "WHERE organisationid = ? "
					   + "AND orgevlink.eventid = events.eventid "
					   + "AND events.venueid = venue.venueid "
					   + "AND venue.longitude IS NOT NULL "
					   + "ORDER BY events.yyyyfirst_date DESC, events.mmfirst_date DESC, events.ddfirst_date DESC"; 
						 
			// define the paramaters
			parameters = new String[1];
			parameters[0] = queryParameter;
		} else {
			// define the date restricted sql
			sql = "SELECT DISTINCT orgevlink.eventid, events.event_name, events.yyyyfirst_date, events.mmfirst_date, events.ddfirst_date, "
					   + "events.yyyylast_date, events.mmlast_date, events.ddlast_date, "
					   + "venue.venue_name, venue.suburb, venue.latitude, venue.longitude "
					   + "FROM orgevlink, events, venue "
					   + "WHERE organisationid = ? "
					   + "AND (events.first_date >= to_date(?, 'yyyy-mm-dd')) "
					   + "AND (events.first_date <= to_date(?, 'yyyy-mm-dd')) "
					   + "AND orgevlink.eventid = events.eventid "
					   + "AND events.venueid = venue.venueid "
					   + "AND venue.longitude IS NOT NULL "
					   + "ORDER BY events.yyyyfirst_date DESC, events.mmfirst_date DESC, events.ddfirst_date DESC"; 
					   
			// manipulate the dates used in the comparison
			if(startDate.indexOf("-") != -1) {
				startDate = startDate + "-01";
			} else {
				startDate = startDate + "01-01";
			}
			
			// check for year only dates
			if(finishDate.indexOf("-") != -1) {
				finishDate = finishDate + "-" + this.getLastDay(finishDate.split("-")[0], finishDate.split("-")[1]);
			} else {
				finishDate = finishDate + "-12-31";
			}
						 
			// define the paramaters
			parameters = new String[3];
			parameters[0] = queryParameter;
			parameters[1] = startDate;
			parameters[2] = finishDate;
		}
		
		// get the resultset
		ResultSet resultSet = this.dataManager.executePreparedStatement(sql, parameters);
		
		// build the xml document
		try {
			// create the xml document object
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder        builder = factory.newDocumentBuilder();
			Document			   xmlDoc  = builder.newDocument();
			
			// add the root element
			Element rootElement = xmlDoc.createElement("markers");
			xmlDoc.appendChild(rootElement);
			
			// debug code
			//while (resultSet.next() && recordCount < 16) {
		
			// build the documnet by adding individual events
			while (resultSet.next()) {
			
				// create a marker element
				Element marker = xmlDoc.createElement("marker");
				
				// add attributes to this element
				marker.setAttribute("event", resultSet.getString(2)); // event name
				
				// build the event url
				String url = eventURLTemplate.replace("[event-id]", resultSet.getString(1));  // replace the constant with the event id
				marker.setAttribute("url", url);  // persistent URL to this event		
				
				// add start date
				String date = this.buildDisplayDate(resultSet.getString(3), resultSet.getString(4), resultSet.getString(5));
				marker.setAttribute("first", date);
				date = this.buildDisplayDate(resultSet.getString(6), resultSet.getString(7), resultSet.getString(8));
				marker.setAttribute("last", date);
				
				// add start and end date for program purposes
				date = this.buildDate(resultSet.getString(3), resultSet.getString(4), resultSet.getString(5)); 
				marker.setAttribute("startDate", date);
				
				date = this.buildDate(resultSet.getString(6), resultSet.getString(7), resultSet.getString(8)); 
				marker.setAttribute("finishDate", date);
				
				// add remaining attributes
				marker.setAttribute("venue",  resultSet.getString(9));  // venue name
				marker.setAttribute("suburb", resultSet.getString(10));  // venue name
				marker.setAttribute("lat",    resultSet.getString(11)); // latitude
				marker.setAttribute("lng",    resultSet.getString(12)); // longitude							
				
				// add this element to the document
				rootElement.appendChild(marker);
				
				// increment counter
				recordCount++;
			}
			
			// add a comment to help in debugging and testing
			rootElement.appendChild(xmlDoc.createComment("Number of markers created: " + recordCount));
			
			// create a transformer 
			TransformerFactory transFactory = TransformerFactory.newInstance();
			Transformer        transformer  = transFactory.newTransformer();
			
			// set some options on the transformer
			transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			
			// get a transformer and supporting classes
			StringWriter writer = new StringWriter();
			StreamResult result = new StreamResult(writer);
			DOMSource    source = new DOMSource(xmlDoc);
			
			// transform the xml document into a string
			transformer.transform(source, result);
			xmlString = writer.toString();
			
		} catch(javax.xml.parsers.ParserConfigurationException ex) {
			throw new javax.servlet.ServletException("Unable to build marker xml", ex);
		} catch(Exception ex) {
			throw new javax.servlet.ServletException("Unable to build marker xml", ex);
		}
		
		// return the string
		return xmlString;
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
	 
	 	// define private variables
		int recordCount = 0;		  // count number of markers created
		String previousCoords = null;   // used to store previous coordinates when linking placemarks
		int currentElevation = 0;	  // used to store the current elevation 
		
		// extract the options into primitive types for faster comparison
		boolean includeTimeSpanElements = false;
		boolean includeTrajectoryInfo   = false;
		boolean includeGroupedEventInfo = false;
		
		if(exportOptions.getOption("includeTimeSpanElements").equals("yes")) {
			includeTimeSpanElements = true;
		}
		
		if(exportOptions.getOption("includeTrajectoryInfo").equals("yes")) {
			includeTrajectoryInfo = true;
		}
		
		if(exportOptions.getOption("includeGroupedEventInfo").equals("yes")) {
			includeGroupedEventInfo = true;
		}
		
		// declare additional kml element variables
		Element trajectoryInfoDocument = null;
		Element groupedEventInfoDocument = null;
		
		// declare other helper variables
		Map<String, String[]> groupedEventInfo = null;
		
		// get the persisten URL template for event
		String eventURLTemplate = this.dataManager.getContextParam("eventURLTemplate");
		
		// try to connect to the database
		this.dataManager.connect(); // dataManager defined in parent object
		
		// define the sql
		String sql = "SELECT DISTINCT orgevlink.eventid, "
				   + "events.event_name, "
				   + "events.yyyyfirst_date, "
				   + "events.mmfirst_date, "
				   + "events.ddfirst_date, "
				   + "events.yyyylast_date, "
				   + "events.mmlast_date, "
				   + "events.ddlast_date, "
				   + "venue.venue_name, "
				   + "venue.street, "
				   + "venue.suburb, "
				   + "states.state, "
				   + "venue.postcode, "
				   + "country.countryname, "
				   + "venue.longitude, "
				   + "venue.latitude "
				   + "FROM orgevlink, "
				   + "events, "
				   + "venue, "
				   + "states, "
				   + "country "
				   + "WHERE organisationid = ? "
				   + "AND orgevlink.eventid = events.eventid "
				   + "AND events.venueid = venue.venueid "
				   + "AND venue.state = states.stateid "
				   + "AND venue.countryid = country.countryid "
				   + "AND venue.longitude IS NOT NULL "
				   + "ORDER BY events.yyyyfirst_date, events.mmfirst_date, events.yyyylast_date";
		
		// define the paramaters
		String[] parameters = new String[1];
		parameters[0] = queryParameter;
					 
		// get the resultset
		ResultSet resultSet = this.dataManager.executePreparedStatement(sql, parameters);
		
		// build the KML file
		try {
		
			// get a new KML file
			KMLExportFile exportFile = new KMLExportFile();
			
			// get the organisation name
			if(this.orgIdCache == null) {
				this.getOrgNameByID(queryParameter);
			}
			
			// add a folder to contain one or more documents
			Element folder = exportFile.addFolder(this.orgNameCache);
			
			// get information about the current system
			String systemName    = this.dataManager.getContextParam("systemName");
			String systemVersion = this.dataManager.getContextParam("systemVersion");
			String systemBuild   = this.dataManager.getContextParam("buildVersion");
			String systemUrl     = this.dataManager.getContextParam("systemUrl");
			
			// add some additional information
			exportFile.addComment("Map of events for: " + this.orgNameCache + " (" + queryParameter + ")");
			exportFile.addComment("Map data generated: " + this.getCurrentDateAndTime());	
			exportFile.addComment("Map generated by: " + systemName + " " + systemVersion + " " + systemBuild);
			
			// add author information to this folder
			exportFile.addAuthorInfo(folder, systemName, systemUrl + "maplinks.jsp?type=org&id=" + queryParameter);
			
			// add a description element to this folder
			exportFile.addDescription(folder, this.orgNameCache + "<br/>Information about " + this.orgNameCache + " exported from the AusStage system on " + this.getCurrentDate() + ".");
			
			// add a document element to the export file
			Element docElement = exportFile.addDocument(folder, "Events");
			
			// add a description element to this document
			exportFile.addDescription(docElement, "A map of events associated with " + this.orgNameCache + ".");
			
			// do we need to include a document for trajectory information?
			if(includeTrajectoryInfo) {
				trajectoryInfoDocument = exportFile.addDocument(folder, "Trajectory Info.");
				
				// add a description element to this document
				exportFile.addDescription(trajectoryInfoDocument, "A map of trajectories linking events associated with " + this.orgNameCache + ".");
			}
			
			// do we need to include grouped event information
			if(includeGroupedEventInfo) {
				groupedEventInfoDocument = exportFile.addDocument(folder, "Events Grouped by Venue");
				
				// add a description element to this document
				exportFile.addDescription(groupedEventInfoDocument, "A map of venues associated with " + this.orgNameCache + " with events grouped by venue.");
				
				// set up the map to use to aggregate the data together
				groupedEventInfo = new HashMap<String, String[]>();
			}
			
			// determine how many rows are in the resultset
			resultSet.last();
			int rowCount = resultSet.getRow();
			resultSet.first();
			
			// add line styles for trajectory info if required
			if(includeTrajectoryInfo) {
			
				if(rowCount > 255) {
					// add the maximum number of lines
					for(int i = 1; i <= 255; i++) {
						exportFile.addLineStyle("traj-" + i, exportFile.getGradientColour(i - 1, 255), "4");
					}
				} else {
			
					for(int i = 1; i <= rowCount; i++) {
					
						exportFile.addLineStyle("traj-" + i, exportFile.getGradientColour(i - 1, rowCount), "4");
					}
				}
			}
			
			// add basic pin style information
			exportFile.addIconStyle("basic-event", "http://chart.apis.google.com/chart?cht=mm&chs=32x32&chco=FFFFFF,CCBAD7,000000&ext=.png");					
			
			// loop through the dataset adding placemarks
			while (resultSet.next()) {
			
				// add a placemark to the document
				Element placemark = exportFile.createElement("Placemark");
				docElement.appendChild(placemark);
				
				// add the name to the placemark
				Element eventName = exportFile.createElement("name");
				placemark.appendChild(eventName);
				eventName.setTextContent(resultSet.getString(2));

				// build the event url
				String url = eventURLTemplate.replace("[event-id]", resultSet.getString(1));  // replace the constant with the event id
				
				// add a link element
				Element eventLink = exportFile.createElement("atom:link");
				eventLink.setAttribute("href", url);
				placemark.appendChild(eventLink);
				
//				// build the address
//				String venueAddress = "";
//				
//				if(resultSet.getString(10) != null) {
//					venueAddress += resultSet.getString(10) + ", ";
//				}
//				
//				if(resultSet.getString(11) != null) {
//					venueAddress += resultSet.getString(11) + ", ";
//				}
//				
//				if(resultSet.getString(12) != null) {
//					venueAddress += resultSet.getString(12) + ", ";
//				}
//				
//				if(resultSet.getString(13) != null) {
//					venueAddress += resultSet.getString(13) + ", ";
//				}
//				
//				if(resultSet.getString(14) != null) {
//					venueAddress += resultSet.getString(14);
//				}
//				
//				// build the description for the placemark
//				String html = "<ul><li><strong>Venue:</strong> " + resultSet.getString(9) + "</li>"
//							+ "<li><strong>Address:</strong> " + venueAddress + "</li>"
//							+ "<li><strong>First Date:</strong> " + this.buildDisplayDate(resultSet.getString(3), resultSet.getString(4), resultSet.getString(5)) + "</li>"
//							+ "<li><strong>Last Date:</strong> " + this.buildDisplayDate(resultSet.getString(6), resultSet.getString(7), resultSet.getString(8)) + "</li>"
//							+ "<li><a href=\"" + url + "\">More Information</a></li></ul>";

				String html = "";
				
				if (resultSet.getString(11) != null) {
					if(resultSet.getString(6) != null) {
						html = "<p>" + resultSet.getString(9) + ", " + resultSet.getString(11) + ", " + this.buildDisplayDate(resultSet.getString(3), resultSet.getString(4), resultSet.getString(5)) + " - " + this.buildDisplayDate(resultSet.getString(6), resultSet.getString(7), resultSet.getString(8));
					} else {
						html = "<p>" + resultSet.getString(9) + ", " + resultSet.getString(11) + ", " + this.buildDisplayDate(resultSet.getString(3), resultSet.getString(4), resultSet.getString(5));
					}
				} else {
					if(resultSet.getString(6) != null) {
						html = "<p>" + resultSet.getString(9) + ", " + this.buildDisplayDate(resultSet.getString(3), resultSet.getString(4), resultSet.getString(5)) + " - " + this.buildDisplayDate(resultSet.getString(6), resultSet.getString(7), resultSet.getString(8));
					} else {
						html = "<p>" + resultSet.getString(9) + ", " + this.buildDisplayDate(resultSet.getString(3), resultSet.getString(4), resultSet.getString(5));
					}
				}
				
				// add the more information link
				html += " <br/><a href=\"" + url + "\">More Information</a></p>";			
				
				// add the description to the placemark
				Element description = exportFile.createElement("description");
				placemark.appendChild(description);
				
				// create the CDATASection to hold the html
				CDATASection cdata = exportFile.createCDATASection(html);
				description.appendChild(cdata);
				
				// create the TimeSpan Element if required
				if(includeTimeSpanElements == true) {
				
					// get the dates
					String begin = this.buildDate(resultSet.getString(3), resultSet.getString(4), resultSet.getString(5));
					String end   = this.buildDate(resultSet.getString(6), resultSet.getString(7), resultSet.getString(8));
					
					if(!begin.equals("")) {
						
						// add the element to the tree
						placemark.appendChild(exportFile.createTimeSpanElement(begin, end));
					}
				} // end includeTimeSpanElements check
								
				// add the style information
				Element style = exportFile.createElement("styleUrl");
				placemark.appendChild(style);
				style.setTextContent("basic-event");
				
				// create the point element
				Element point = exportFile.createElement("Point");
				placemark.appendChild(point);
			
				// add the coordinates
				Element coordinates = exportFile.createElement("coordinates");
				point.appendChild(coordinates);
				coordinates.setTextContent(resultSet.getString(15) + "," + resultSet.getString(16));
				
				// add the trajectory information if required
				if(includeTrajectoryInfo == true) {
				
					// only draw a line between two places
					if(previousCoords != null) {
				
						//trajectoryInfoDocument
						// add a placemark to the document
						Element trajectoryPlacemark = exportFile.createElement("Placemark");
						trajectoryInfoDocument.appendChild(trajectoryPlacemark);
						
						// add the name to the placemark
						Element trajectoryEventName = exportFile.createElement("name");
						trajectoryPlacemark.appendChild(trajectoryEventName);
						trajectoryEventName.setTextContent(resultSet.getString(2));						
						
						// create the TimeSpan Element if required
						if(includeTimeSpanElements == true) {
						
							// get the dates
							String begin = this.buildDate(resultSet.getString(3), resultSet.getString(4), resultSet.getString(5));
							String end   = this.buildDate(resultSet.getString(6), resultSet.getString(7), resultSet.getString(8));
							
							if(!begin.equals("")) {
								
								// add the element to the tree
								trajectoryPlacemark.appendChild(exportFile.createTimeSpanElement(begin, end));
							}
						} // end includeTimeSpanElements check
						
						// add the style information
						if (rowCount <= 255) {
							// use actual row number if row count is less than or equal to 255
							Element styleUrl = exportFile.createElement("styleUrl");
							styleUrl.setTextContent("traj-" + Integer.toString(resultSet.getRow() - 2));
							trajectoryPlacemark.appendChild(styleUrl);
						} else {
							// if total row count is above 255 normalise row number before using it
							Element styleUrl = exportFile.createElement("styleUrl");
							styleUrl.setTextContent("traj-" + (exportFile.getNormalisedColourIndex(resultSet.getRow(), rowCount) - 1));
							trajectoryPlacemark.appendChild(styleUrl);
						}
						
						// add the lineString element and associated boilerplate
						Element lineString = exportFile.createElement("LineString");
							
						Element extrude   = exportFile.createElement("extrude");
						extrude.setTextContent("0");
						lineString.appendChild(extrude);
							
						Element tessellate = exportFile.createElement("tessellate");
						tessellate.setTextContent("1");
						lineString.appendChild(tessellate);
							
						Element altitudeMode = exportFile.createElement("altitudeMode");
						altitudeMode.setTextContent("clampToGround");
						lineString.appendChild(altitudeMode);
							
						// add the coordinates
						Element lineCoordinates = exportFile.createElement("coordinates");
						lineCoordinates.setTextContent(previousCoords + " " + resultSet.getString(15) + "," + resultSet.getString(16));
						lineString.appendChild(lineCoordinates);
							
						// add the lineString element to the placemark
						trajectoryPlacemark.appendChild(lineString);
							
						// store these coordinates for use later
						previousCoords = resultSet.getString(15) + "," + resultSet.getString(16);
					} else {
						// store these coordinates for use later
						previousCoords = resultSet.getString(15) + "," + resultSet.getString(16);
					}
				}

				// keep count of markers
				recordCount++;
				
				/*
				 * build the hashmap of events if required
				 */
				if(includeGroupedEventInfo) {
				
					// build the key for this venue
					String key = resultSet.getString(15) + resultSet.getString(16);
					key = key.replaceAll("\\.","");
					key = key.replaceAll(",", "");
					key = key.replaceAll("-","");
					
					if(groupedEventInfo.containsKey(key) == false) {
					
						// add a new entry for this venue
						String[] eventEntry = new String[6];
						
						// add the coordinates
						eventEntry[0] = resultSet.getString(15) + "," + resultSet.getString(16);
						
						// add the event name
						eventEntry[1] = resultSet.getString(9);
						
						// build the details for this event
//						String eventDetails = "<p>" + venueAddress + "</p> "
//											+ "<ul><li><a href=\"" + url + "\">" + resultSet.getString(2) + "</a>, "
//											+ "First Date: " + this.buildDisplayDate(resultSet.getString(3), resultSet.getString(4), resultSet.getString(5))
//											+ " Last Date: " + this.buildDisplayDate(resultSet.getString(6), resultSet.getString(7), resultSet.getString(8))
//											+ "</li>";

						String eventDetails = "<ul><li><a href=\"" + url + "\">" + resultSet.getString(2) + "</a>, ";
						
						if (resultSet.getString(11) != null) {
							eventDetails += resultSet.getString(9) + ", " + resultSet.getString(11) + ", " + this.buildDisplayDate(resultSet.getString(3), resultSet.getString(4), resultSet.getString(5)) + " - " + this.buildDisplayDate(resultSet.getString(6), resultSet.getString(7), resultSet.getString(8)) + "</li>";
						} else {
							eventDetails += resultSet.getString(9) + ", " + this.buildDisplayDate(resultSet.getString(3), resultSet.getString(4), resultSet.getString(5)) + " - " + this.buildDisplayDate(resultSet.getString(6), resultSet.getString(7), resultSet.getString(8)) + "</li>";
						}
											
						eventEntry[2] = eventDetails;
						
						// start a count
						eventEntry[3] = "1";
						
						// get the first date
						eventEntry[4] = this.buildDate(resultSet.getString(3), resultSet.getString(4), resultSet.getString(5));
						
						// get the last date
						eventEntry[5] = this.buildDate(resultSet.getString(6), resultSet.getString(7), resultSet.getString(8));
						
						// add this hashmap to the larger collection
						groupedEventInfo.put(key, eventEntry);
						
					} else {
						// use existing entry for this venue
						String[] eventEntry = groupedEventInfo.get(key);
						
						// get the existing details for this venue
						String eventDetails = eventEntry[2];
						
						// append this event to the list
//						eventDetails += "<li><a href=\"" + url + "\">" + resultSet.getString(2) + "</a>, "
//									 + "First Date: " + this.buildDisplayDate(resultSet.getString(3), resultSet.getString(4), resultSet.getString(5))
//									 + " Last Date: " + this.buildDisplayDate(resultSet.getString(6), resultSet.getString(7), resultSet.getString(8))
//									 + "</li>";
									 
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
						int i = Integer.parseInt(eventEntry[3]);
						i++;
						eventEntry[3] = Integer.toString(i);
						
						// update the last date
						eventEntry[5] = this.buildDate(resultSet.getString(6), resultSet.getString(7), resultSet.getString(8));
						
						// update this venue
						groupedEventInfo.put(key, eventEntry); 
					}
				
				} // end building of the hashmap
			
			} // end loop through dataset
			
			// add the grouped event info to the KML if required
			if(includeGroupedEventInfo) {
			
				// add the style information
				exportFile.addIconStyles();
				
				// loop through the hashmap using the array of keys
				for(Map.Entry<String, String[]> mapEntry : groupedEventInfo.entrySet()) {
				
					// get the details for this event
					String[] eventDetails = mapEntry.getValue();
					
					// build the placemark
					Element placemark = exportFile.createElement("Placemark");
					groupedEventInfoDocument.appendChild(placemark);
					
					// add the venue name
					Element eventName = exportFile.createElement("name");
					placemark.appendChild(eventName);
					eventName.setTextContent(eventDetails[1]);
					
					// add the description
					// add the description to the placemark
					Element description = exportFile.createElement("description");
					placemark.appendChild(description);
					
					String eventDescription = eventDetails[2];
					eventDescription += "</ul>";
					
					// create the CDATASection to hold the html
					CDATASection cdata = exportFile.createCDATASection(eventDescription);
					description.appendChild(cdata);
					
					// create the TimeSpan Element if required
					if(includeTimeSpanElements == true) {
					
						if(!eventDetails[4].equals("")) {
							
							// add the element to the tree
							placemark.appendChild(exportFile.createTimeSpanElement(eventDetails[4], eventDetails[5]));
						}
					} // end includeTimeSpanElements check
					
					// add the style information
					int eventCount = Integer.parseInt(eventDetails[3]);
					Element styleUrl = exportFile.createElement("styleUrl");
					
					// determine which url to use
					// matches IconStyle elements added earlier
					if (eventCount == 1) {
						styleUrl.setTextContent("grp-1-event");
					} else if(eventCount > 1 && eventCount < 6) {
						styleUrl.setTextContent("grp-2-5-event");
					} else if(eventCount > 5 && eventCount < 16) {
						styleUrl.setTextContent("grp-6-15-event");
					} else if(eventCount > 15 && eventCount < 31) {
						styleUrl.setTextContent("grp-16-30-event");
					} else {
						styleUrl.setTextContent("grp-30-plus-event");
					}
					
					// add the element
					placemark.appendChild(styleUrl);
					
					// create the point element
					Element point = exportFile.createElement("Point");
					placemark.appendChild(point);
					
					// add the coordinates
					Element coordinates = exportFile.createElement("coordinates");
					point.appendChild(coordinates);
					coordinates.setTextContent(eventDetails[0]);				
				}
			
			} // end adding the grouped event info		
			
			// add a comment to help in debugging and testing
			//exportFile.addComment("Number of Placemarks created: " + recordCount);
			
			// return the string
			return exportFile.toString();
			
		} catch(Exception ex) {
			throw new javax.servlet.ServletException("Unable to build KML xml", ex);
		}
	 
	 } // end getKMLString method
	 
	 /**
	  * A method to get the name of an organisation when using an organisation id
	  *
	  * @param id the id number for the organisation
	  *
	  * @return   a string containing the organisation name
	  */
	 public String getOrgNameByID(String id) throws javax.servlet.ServletException {
	 
	 	// check to see if we have the name already
	 	if(this.orgIdCache == null) {
	 
		 	// try to connect to the database
			this.dataManager.connect(); // dataManager defined in parent object
			
			// define the sql
			String sql = "SELECT name FROM organisation WHERE organisationid = ?";
						 
			// define the paramaters
			String[] parameters = new String[1];
			parameters[0] = id;
						 
			// get the resultset
			ResultSet resultSet = this.dataManager.executePreparedStatement(sql, parameters);
			
			try {
			
				// store the name
				resultSet.next();
				this.orgNameCache = resultSet.getString(1);
				this.orgIdCache = id;
				
				// return the name
				return this.orgNameCache;
			
			} catch(Exception ex) {
				throw new javax.servlet.ServletException("Unable to lookup organisation name", ex);
			}
		} else {
			// check to see if this request matches the cached copy
			if(id.equals(this.orgIdCache)) {
				return this.orgNameCache;
			} else {
				this.orgIdCache = null;
				return this.getOrgNameByID(id);
			}
		}
		 
	 } // end getOrgNameByID method
	 
	 /**
	  * A method to all of the start dates for events that can be mapped
	  *
	  * @param id the id number for the organisation
	  *
	  * @return   a string containing JSON encoded data
	  */
	public String getStartDatesForMap(String id) throws javax.servlet.ServletException {
	
		// declare helper variables
		StringBuilder jsonData = new StringBuilder("{");
	
		// try to connect to the database
		this.dataManager.connect(); // dataManager defined in parent object
		
		// define the sql
		String sql = "SELECT DISTINCT events.yyyyfirst_date, events.mmfirst_date "
				   + "FROM orgevlink, events, venue "
				   + "WHERE organisationid = ? "
				   + "AND orgevlink.eventid = events.eventid "
				   + "AND events.venueid = venue.venueid "
				   + "AND venue.longitude IS NOT NULL "
				   + "ORDER BY events.yyyyfirst_date ASC, events.mmfirst_date ASC"; 
					 
		// define the paramaters
		String[] parameters = new String[1];
		parameters[0] = id;
					 
		// get the resultset
		ResultSet resultSet = this.dataManager.executePreparedStatement(sql, parameters);
		
		// build the data
		try {
			
			// build the documnet by adding individual events
			while (resultSet.next()) {
			
				jsonData.append("\"" + this.buildDate(resultSet.getString(1), resultSet.getString(2), null) + "\" : ");
				jsonData.append("\"" + this.buildDisplayDate(resultSet.getString(1), resultSet.getString(2), null) + "\",");
				
			}
			
			// finalise the JSON data
			jsonData.replace(jsonData.length() - 1, jsonData.length(), "}");
			
			return jsonData.toString();

		} catch(Exception ex) {
			throw new javax.servlet.ServletException("Unable to build start date json", ex);
		}
	
	} // end getStartDatesForMap method
	
	/**
	 * A method declared in abstract class but not used in this class
	 */
	public String getMarkerXMLString() throws javax.servlet.ServletException, java.lang.NoSuchMethodException {
		throw new java.lang.NoSuchMethodException("Method not implemented");
	}
	
	/**
	 * A abstract method used to get the String representation of the KML document
	 * using the default options
	 *
	 * @return               a string containing the KML XML
	 */
	public String getKMLString() throws javax.servlet.ServletException, java.lang.NoSuchMethodException {
		throw new java.lang.NoSuchMethodException("Method not implemented");
	}

} // end class definition
