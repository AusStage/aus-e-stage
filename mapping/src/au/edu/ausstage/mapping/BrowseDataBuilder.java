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
public class BrowseDataBuilder extends DataBuilder {

	/**
	 * Constructor for this class
	 *
	 * @param manager the DataManager object used to access the database
	 */
	public BrowseDataBuilder(DataManager manager) {
		super(manager);
	}
	
	/**
	 * A method used to get the String representation of the Marker XML
	 * 
	 *
	 * @return               the string representation of the Marker XML
	 */
	public String getMarkerXMLString() throws javax.servlet.ServletException {
	
		// declare helper variables
		String xmlString = null;
		int recordCount = 0;
	
		// try to connect to the database
		this.dataManager.connect(); // dataManager defined in parent object
		
		// define the sql
		String sql = "SELECT sq1.venueid, sq1.venue_name, sq1.latitude, sq1.longitude, sq1.event_count, " 
				   + "       sq2.first_year, sq2.last_year, sq1.state "
				   + "FROM (SELECT venue.venueid, venue.venue_name, state, latitude, longitude, COUNT(eventid) as event_count "
				   + "      FROM venue, events "
				   + "      WHERE longitude IS NOT NULL "
				   + "      AND events.venueid = venue.venueid "
				   + "      GROUP BY venue.venueid, venue.venue_name, state, latitude, longitude) sq1, "
				   + "     (SELECT venue.venueid, MAX(yyyylast_date) as last_year, MIN(yyyyfirst_date) as first_year "
				   + "      FROM venue, events "
				   + "      WHERE latitude IS NOT NULL "
				   + "      AND events.venueid = venue.venueid "
				   + "      GROUP BY venue.venueid) sq2 "
				   + "WHERE sq1.venueid = sq2.venueid";
		
		// get the data
		ResultSet resultSet = this.dataManager.executeStatement(sql);
		
		// build the xml document
		try {
			// create the xml document object
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder        builder = factory.newDocumentBuilder();
			Document			   xmlDoc  = builder.newDocument();
			
			// add the root element
			Element rootElement = xmlDoc.createElement("markers");
			xmlDoc.appendChild(rootElement);
		
			// build the documnet by adding individual events
			while (resultSet.next()) {
			
				// create a marker element
				Element marker = xmlDoc.createElement("marker");
				
				// add attributes to this element
				marker.setAttribute("id",    resultSet.getString(1)); // venue id
				marker.setAttribute("name",  resultSet.getString(2)); // venue name
				marker.setAttribute("lat",   resultSet.getString(3)); // latitude
				marker.setAttribute("lng",   resultSet.getString(4)); // longitude
				marker.setAttribute("cnt",   resultSet.getString(5)); // count
				marker.setAttribute("fyear", resultSet.getString(6)); // first year
				marker.setAttribute("lyear", resultSet.getString(7)); // last year
				marker.setAttribute("state", resultSet.getString(8)); // state
				
				// add this element to the document
				rootElement.appendChild(marker);
				
				// increment counter
				recordCount++;
			}
			
			// add a comment to help in debugging and testing
			rootElement.appendChild(xmlDoc.createComment("Number of markers created: " + recordCount));
			
			// get the maximum and minimum start date
			sql = "SELECT MAX(yyyyfirst_date) as last_date, "
				+ "       MIN(yyyyfirst_date) as first_date "
				+ "FROM venue, events "
				+ "WHERE latitude IS NOT NULL "
				+ "AND events.venueid = venue.venueid";
				
			resultSet = this.dataManager.executeStatement(sql);
			resultSet.next();
			
			Element date = xmlDoc.createElement("lastdate");
			date.setAttribute("value", resultSet.getString(1));
			rootElement.appendChild(date);
			
			date = xmlDoc.createElement("firstdate");
			date.setAttribute("value", resultSet.getString(2));
			rootElement.appendChild(date);
						
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
			xmlString = writer.toString();
			
		} catch(javax.xml.parsers.ParserConfigurationException ex) {
			throw new javax.servlet.ServletException("Unable to build marker xml", ex);
		} catch(Exception ex) {
			throw new javax.servlet.ServletException("Unable to build marker xml", ex);
		}
		
		// return the string
		return xmlString;
	
	} // end getMarkerXMLString
	
	/**
	 * Search the database for events at the specified venue
	 *
	 * @param queryParameter the venue id
	 *
	 * @return               HTML encoded result set
	 */	
	public String doSearch(String queryParameter) throws javax.servlet.ServletException {
	
		// declare helper variables
		StringBuilder data = new StringBuilder();
		String url = null;
		
		// get the persistent URL template for event
		String eventURLTemplate = this.dataManager.getContextParam("eventURLTemplate");
		String venueURLTemplate = this.dataManager.getContextParam("venueURLTemplate");
		
		// try to connect to the database
		this.dataManager.connect(); // dataManager defined in parent object
		
		// get info about the venue
		String sql = "SELECT venueid, venue_name, suburb FROM venue WHERE venueid = ?";
					 
		// define the paramaters
		String[] parameters = new String[1];
		parameters[0] = queryParameter;
					 
		// get the resultset
		ResultSet resultSet = this.dataManager.executePreparedStatement(sql, parameters);
		
		try {
		
			// move to the first record in the resultset
			resultSet.next();
			
			// build the venue url
			url = venueURLTemplate.replace("[venue-id]", queryParameter);
			
			// start to build the data
			if (resultSet.getString(2) != null) {
				data.append("<p><a href=\"" + url + "\" title=\"More information on Venue\" target=\"ausstage\">" + resultSet.getString(2) + "</a>, " + resultSet.getString(3) + "</p><ul>");
			} else {
				data.append("<p><a href=\"" + url + "\" title=\"More information on Venue\" target=\"ausstage\">" + resultSet.getString(2) + "</a></p><ul>");
			}
			
			// get the event information
			sql = "SELECT eventid, event_name, yyyyfirst_date, mmfirst_date, ddfirst_date, yyyylast_date, mmlast_date, ddlast_date "
				+ "FROM events, venue "
				+ "WHERE events.venueid = venue.venueid "
				+ "AND venue.venueid = ? "
				+ "ORDER BY yyyyfirst_date DESC, mmfirst_date DESC, ddfirst_date DESC";
			
			// get the resultset
			resultSet = this.dataManager.executePreparedStatement(sql, parameters);
			
			// build the documnet by adding individual events
			while (resultSet.next()) {
			
				// build the event url
				url = eventURLTemplate.replace("[event-id]", resultSet.getString(1));
				
				// add event information
				data.append("<li><a href=\"" + url + "\" title=\"More information on event\" target=\"ausstage\">" + resultSet.getString(2) + "</a>, ");
				if(resultSet.getString(6) != null) {
					data.append(this.buildDisplayDate(resultSet.getString(3), resultSet.getString(4), resultSet.getString(5)) + " - ");
					data.append(this.buildDisplayDate(resultSet.getString(6), resultSet.getString(7), resultSet.getString(8)) + "</li>");
				} else {	
					data.append(this.buildDisplayDate(resultSet.getString(3), resultSet.getString(4), resultSet.getString(5)) + "</li>");
				}
			}
			
			// finish the markup
			data.append("</ul>");
		
		} catch(Exception ex) {
			throw new javax.servlet.ServletException("Unable to build event data html", ex);
		}
		return data.toString();
		
	} // end doSearch method
	
	/**
	 * A method to get the data used to build a timeline in the XML format
	 *
	 * @param queryParameter the venue id
	 *
	 * @return               timeline data in the xml format
	 */	
	public String getTimelineXML(String queryParameter) throws javax.servlet.ServletException {
	
		// declare helper variables
		String startDate  = null;
		String finishDate = null;
		boolean isDuration = false;
		
		// get the persistent URL template for event
		String eventURLTemplate = this.dataManager.getContextParam("eventURLTemplate");
		
		// try to connect to the database
		this.dataManager.connect(); // dataManager defined in parent object
		
		// get info about the venue
		String sql = "SELECT eventid, event_name, yyyyfirst_date, mmfirst_date, ddfirst_date, yyyylast_date, mmlast_date, ddlast_date "
				   + "FROM events, venue "
				   + "WHERE events.venueid = venue.venueid "
				   + "AND venue.venueid = ? "
				   + "ORDER BY yyyyfirst_date DESC, mmfirst_date DESC, ddfirst_date DESC";
					 
		// define the paramaters
		String[] parameters = new String[1];
		parameters[0] = queryParameter;
		
		// get the resultset
		ResultSet resultSet = this.dataManager.executePreparedStatement(sql, parameters);
		
		try {
		
			// create the xml document object
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder        builder = factory.newDocumentBuilder();
			Document			   xmlDoc  = builder.newDocument();
			
			// add the root element
			Element rootElement = xmlDoc.createElement("data");
			xmlDoc.appendChild(rootElement);
			
			// add a series of events
			while (resultSet.next()) {

				// build the start date
				if (resultSet.getString(3) == null) {
					startDate = "";
				}
				else if(resultSet.getString(4) == null) {
					startDate = buildDisplayDate(resultSet.getString(3), "01", "01");
				}
				else if (resultSet.getString(5) == null) {
					startDate = buildDisplayDate(resultSet.getString(3), resultSet.getString(4), "01");
				} else {
					startDate = buildDisplayDate(resultSet.getString(3), resultSet.getString(4), resultSet.getString(5));
				}
				
				if(startDate.equals("") == false) {
					// only continue if we have at least a start date
					Element event = xmlDoc.createElement("event");
					
					// add the start date
					event.setAttribute("start", startDate + " 00:00:00 +0000");
					
					// build the finish date
					if (resultSet.getString(6) == null) {
						finishDate = "";
					}
					else if(resultSet.getString(7) == null) {
						finishDate = buildDisplayDate(resultSet.getString(6), "12", "31");
						isDuration = false;
					}
					else if (resultSet.getString(8) == null) {
						finishDate = buildDisplayDate(resultSet.getString(6), resultSet.getString(6), "31");
						isDuration = false;
					} else {
						finishDate = buildDisplayDate(resultSet.getString(6), resultSet.getString(7), resultSet.getString(8));
						isDuration = true;
					}
					
					// add the finish Date					
					if(finishDate.equals("") == false && (startDate.equals(finishDate) == false)) {
						if(isDuration == true) {
							event.setAttribute("end", finishDate + " 00:00:00 +0000");
							event.setAttribute("isDuration", "true");
						} else {
							event.setAttribute("end", finishDate + " 00:00:00 +0000");
						}
					}
					
					// add a title
					event.setAttribute("title", resultSet.getString(2));
					
					// add a link
					event.setAttribute("link", eventURLTemplate.replace("[event-id]", resultSet.getString(1)));
					
					// add the event
					rootElement.appendChild(event);				
				}
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
					
		}catch(javax.xml.parsers.ParserConfigurationException ex) {
			throw new javax.servlet.ServletException("Unable to build timeline xml", ex);
		} catch(Exception ex) {
			throw new javax.servlet.ServletException("Unable to build timeline xml", ex);
		}
	
	} // end getTimelineXML method
	
	/**
	 * A abstract method used to get the String representation of the KML document
	 * using the default options
	 *
	 * @return               a string containing the KML XML
	 */
	public String getKMLString() throws javax.servlet.ServletException {
	
		// declare helper variables
		String url = null;
		String firstDate = null;
		String lastDate = null;
		String[] explodedDate = null;
		int eventCount = 0;
		KMLExportFile exportFile = null;
		
		// get the persistent URL template for venues
		String venueURLTemplate = this.dataManager.getContextParam("venueURLTemplate");
		
		// try to connect to the database
		this.dataManager.connect(); // dataManager defined in parent object
		
		// get info about the venues
		String sql = "SELECT venue.venueid, venue.venue_name, venue.longitude, venue.latitude, sq1.event_count, sq2.min_first_date, sq3.max_last_date "
				   + "FROM venue, "
				   + "     (SELECT venue.venueid, COUNT(eventid) event_count "
      			   + "FROM venue, events "
				   + "      WHERE latitude IS NOT NULL "
				   + "      AND events.venueid = venue.venueid "
				   + "      GROUP BY venue.venueid "
				   + "     ) sq1, "
				   + "     (SELECT venue.venueid, MIN(CONCAT(yyyyfirst_date, CONCAT(mmfirst_date, ddfirst_date))) as min_first_date "
				   + "      FROM venue, events  "
				   + "      WHERE latitude IS NOT NULL  "
				   + "      AND events.venueid = venue.venueid "
				   + "      GROUP BY venue.venueid "
				   + "     ) sq2, "
				   + "     (SELECT venue.venueid, MAX(CONCAT(yyyylast_date, CONCAT(mmlast_date, ddlast_date))) as max_last_date "
				   + "      FROM venue, events "
				   + "      WHERE latitude IS NOT NULL "
				   + "      AND events.venueid = venue.venueid "
				   + "      GROUP BY venue.venueid "
				   + "     ) sq3 "
				   + "WHERE latitude IS NOT NULL "
				   + "AND venue.venueid = sq1.venueid "
				   + "AND venue.venueid = sq2.venueid "
				   + "AND venue.venueid = sq3.venueid "
				   + "ORDER BY venue.venueid";
					 
		// get the resultset
		ResultSet resultSet = this.dataManager.executeStatement(sql);
		
		try {
		
			// get a new KML file
			exportFile = new KMLExportFile();
			
			// get a folder Element
			Element folder = exportFile.addFolder("AusStage Venue Browse Data");
			
			// get information about the current system
			String systemName    = this.dataManager.getContextParam("systemName");
			String systemVersion = this.dataManager.getContextParam("systemVersion");
			String systemBuild   = this.dataManager.getContextParam("buildVersion");
			String systemUrl     = this.dataManager.getContextParam("systemUrl");
			
			// add some additional information
			exportFile.addComment("Map data generated: " + this.getCurrentDateAndTime());	
			exportFile.addComment("Map generated by: " + systemName + " " + systemVersion + " " + systemBuild);
			
			// add author information to this folder
			exportFile.addAuthorInfo(folder, systemName, systemUrl + "browse.jsp");
			
			// add a description element to this folder
			exportFile.addDescription(folder, "Browse venue data exported from the AusStage system on " + this.getCurrentDate() + ".");
			
			// add a document element to the export file
			Element docElement = exportFile.addDocument(folder, "Venues");
			
			// add a description element to this document
			exportFile.addDescription(docElement, "A map of venues exported from the AusStage system.");
			
			// centre the display of the map on Australia by default
			Element lookAt = exportFile.createElement("LookAt");
			
			// add the latitude and longitude
			Element lng = exportFile.createElement("longitude");
			lng.setTextContent("133.209639");
			lookAt.appendChild(lng);
			
			Element lat = exportFile.createElement("latitude");
			lat.setTextContent("-25.947028");
			lookAt.appendChild(lat);
			
			// add the heading element
			Element heading = exportFile.createElement("heading");
			heading.setTextContent("0");
			lookAt.appendChild(heading);
			
			// add the range element
			Element range = exportFile.createElement("range");
			range.setTextContent("6735030");
			lookAt.appendChild(range);
			
			// add the LookAt element to the document
			docElement.appendChild(lookAt);
			
			// add the basic icon styles
			exportFile.addIconStyles();
			
			// build the documnet by adding individual events
			while (resultSet.next()) {
			
				// add the venue to the KML export
				Element placemark = exportFile.createElement("Placemark");
				docElement.appendChild(placemark);
				
				// add the venue name
				Element venueName = exportFile.createElement("name");
				placemark.appendChild(venueName);
				venueName.setTextContent(resultSet.getString(2));
				
				// build the venue url
				url = venueURLTemplate.replace("[venue-id]", resultSet.getString(1));  // replace the constant with the event id
				
				// add a link element
				Element venueLink = exportFile.createElement("atom:link");
				venueLink.setAttribute("href", url);
				placemark.appendChild(venueLink);
				
				// add the description to the placemark
				Element description = exportFile.createElement("description");
				placemark.appendChild(description);
				
				// create the CDATASection to hold the html
				CDATASection cdata = exportFile.createCDATASection("<iframe width=\"450px\" height=\"400px\" scrolling=\"auto\" src=\"" + systemUrl + "browsedetail.jsp?id=" + resultSet.getString(1) + "\"</iframe>");
				description.appendChild(cdata);
				
				// build the dates
				explodedDate = getExplodedDate(resultSet.getString(6));
				firstDate    = buildDate(explodedDate[0], explodedDate[1], explodedDate[2]);
				explodedDate = getExplodedDate(resultSet.getString(7));
				lastDate     = buildDate(explodedDate[0], explodedDate[1], explodedDate[2]);
				
				// add the timespan elements
				placemark.appendChild(exportFile.createTimeSpanElement(firstDate, lastDate));				
				
				// determine which style url to use
				// matches IconStyle elements added earlier
				eventCount = Integer.parseInt(resultSet.getString(5));
				Element styleUrl = exportFile.createElement("styleUrl");
				
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
				coordinates.setTextContent(resultSet.getString(3) + "," + resultSet.getString(4));			
			}			
		
		} catch(Exception ex) {
			throw new javax.servlet.ServletException("Unable to build KML data for export", ex);
		}
		
		return exportFile.toString();
		
	} // end getKMLString method
	
	/**
	 * A method declared in the abstract class but not used in this class
	 */
	public String getMarkerXMLString(String queryParameter) throws javax.servlet.ServletException, java.lang.NoSuchMethodException {
		throw new java.lang.NoSuchMethodException("Method not implemented");
	}

	
	/**
	 * A method declared in the abstract class but not used in this class
	 */
	public String getKMLString(String queryParameter) throws javax.servlet.ServletException, java.lang.NoSuchMethodException {
		throw new java.lang.NoSuchMethodException("Method not implemented");
	}
	
	/**
	 * A method declared in the abstract class but not used in this class
	 */
	public String doKMLExport(String queryParameter, KMLExportOptions exportOptions) throws javax.servlet.ServletException, java.lang.NoSuchMethodException {
		throw new java.lang.NoSuchMethodException("Method not implemented");
	}

} // end class definition
