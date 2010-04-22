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
public class ContributorDataBuilder extends DataBuilder {

	/**
	 * Constructor for this class
	 *
	 * @param manager the DataManager object used to access the database
	 */
	public ContributorDataBuilder(DataManager manager) {
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
		return doSearch(queryParameter, null);
	} // end doSearch method
	
	
	/**
	 * A method to search for all contributors matching the search term provided
	 *
	 * @param queryParameter the search term to be used in the query
	 * @param stateLimit     the state code to limit the search to
	 *
	 * @return               the results of the search
	 */	
	public String doSearch(String queryParameter, String stateLimit) throws javax.servlet.ServletException {
	
/*
 * basic contributor search
 * copy stored here for reference
 * note, at most three functions will be listed, more than three and a ... is used
 *
SELECT DISTINCT search_contributor.contributorid,
       contrib_name as contributor_name,
       functions.function,
       events.event_count,
       DECODE(markers.marker_count, NULL, 0, markers.marker_count) as marker_count
FROM search_contributor, 
     (SELECT contributorid, 
             MAX(DECODE(val_number, 1, preferredterm, null)) ||
             MAX(DECODE(val_number, 2, ', ' || preferredterm, null)) ||
             MAX(DECODE(val_number, 3, ', ' || preferredterm, null)) ||
             MAX(DECODE(val_number, 4, '...', null)) as function
      FROM (SELECT contributor.contributorid, 
                   row_number() over (partition by contributor.contributorid order by preferredterm) as val_number, 
                   preferredterm
            FROM contributor,
                 contfunctlink,
                 contributorfunctpreferred
            WHERE contributor.contributorid = contfunctlink.contributorid
            AND contfunctlink.contributorfunctpreferredid = contributorfunctpreferred.contributorfunctpreferredid)
      GROUP BY contributorid) functions,
     (SELECT contributorid, count(*) over (partition by contributorid) event_count
      FROM conevlink) events,
     (SELECT contributorid, count(*) over (partition by contributorid) marker_count
      FROM conevlink,
           events,
           venue
      WHERE conevlink.eventid = events.eventid
      AND events.venueid = venue.venueid
      AND venue.latitude IS NOT NULL) markers
WHERE CONTAINS(search_contributor.combined_all, ?, 1) > 0
AND functions.contributorid = search_contributor.contributorid
AND events.contributorid = search_contributor.contributorid
AND markers.contributorid(+) = search_contributor.contributorid

*/
	
		// define private variable to hold results
		StringBuilder results = new StringBuilder();
		
		int recordCount = 0;
		
		// try to connect to the database
		this.dataManager.connect(); // dataManager defined in parent object
		
		// declare helper variables
		String sql;
		String[] parameters;
		
		if(stateLimit == null) {
		
			// define the sql
			sql = "SELECT DISTINCT search_contributor.contributorid, "
			    + "       contrib_name as contributor_name, "
   			    + "       functions.function, "
			    + "       events.event_count, "
			    + "       DECODE(markers.marker_count, NULL, 0, markers.marker_count) as marker_count, "
			    + "       search_contributor.last_name "
			    + "FROM search_contributor,  "
			    + "     (SELECT contributorid,  "
			    + "             MAX(DECODE(val_number, 1, preferredterm, null)) || "
			    + "             MAX(DECODE(val_number, 2, ', ' || preferredterm, null)) || "
			    + "             MAX(DECODE(val_number, 3, ', ' || preferredterm, null)) || "
			    + "             MAX(DECODE(val_number, 4, '...', null)) as function "
			    + "      FROM (SELECT contributor.contributorid,  "
			    + "                   row_number() over (partition by contributor.contributorid order by preferredterm) as val_number, "
			    + "                   preferredterm "
			    + "            FROM contributor, "
			    + "                 contfunctlink, "
			    + "                 contributorfunctpreferred "
			    + "            WHERE contributor.contributorid = contfunctlink.contributorid "
			    + "            AND contfunctlink.contributorfunctpreferredid = contributorfunctpreferred.contributorfunctpreferredid) "
			    + "      GROUP BY contributorid) functions, "
			    + "     (SELECT contributorid, COUNT(DISTINCT conevlink.eventid) over (partition by contributorid) event_count "
			    + "      FROM conevlink) events, "
			    + "     (SELECT contributorid, COUNT(DISTINCT conevlink.eventid) over (partition by contributorid) marker_count "
			    + "      FROM conevlink, "
			    + "           events, "
			    + "           venue "
			    + "      WHERE conevlink.eventid = events.eventid "
			    + "      AND events.venueid = venue.venueid "
			    + "      AND venue.latitude IS NOT NULL) markers "
			    + "WHERE CONTAINS(search_contributor.combined_all, ?, 1) > 0 "
			    + "AND functions.contributorid = search_contributor.contributorid "
			    + "AND events.contributorid = search_contributor.contributorid "
			    + "AND markers.contributorid(+) = search_contributor.contributorid "
			    + "ORDER BY search_contributor.last_name";
						 
			// define the paramaters
			parameters = new String[1];
			parameters[0] = queryParameter;
		
		} else {
		
			// define the sql
			sql = "SELECT DISTINCT search_contributor.contributorid, "
			    + "       contrib_name as contributor_name, "
   			    + "       functions.function, "
			    + "       events.event_count, "
			    + "       DECODE(markers.marker_count, NULL, 0, markers.marker_count) as marker_count , "
			    + "       search_contributor.last_name "
			    + "FROM search_contributor,  "
			    + "     (SELECT contributorid,  "
			    + "             MAX(DECODE(val_number, 1, preferredterm, null)) || "
			    + "             MAX(DECODE(val_number, 2, ', ' || preferredterm, null)) || "
			    + "             MAX(DECODE(val_number, 3, ', ' || preferredterm, null)) || "
			    + "             MAX(DECODE(val_number, 4, '...', null)) as function "
			    + "      FROM (SELECT contributor.contributorid,  "
			    + "                   row_number() over (partition by contributor.contributorid order by preferredterm) as val_number, "
			    + "                   preferredterm "
			    + "            FROM contributor, "
			    + "                 contfunctlink, "
			    + "                 contributorfunctpreferred "
			    + "            WHERE contributor.contributorid = contfunctlink.contributorid "
			    + "            AND contfunctlink.contributorfunctpreferredid = contributorfunctpreferred.contributorfunctpreferredid) "
			    + "      GROUP BY contributorid) functions, ";
			    
			    
			// determine state specific code to use
			if(stateLimit.equals("a")) {
				// add australia state limit clause
				sql += "(SELECT contributorid, COUNT(DISTINCT conevlink.eventid) over (partition by contributorid) event_count "
					+  " FROM conevlink, "
					+  "      events, "
					+  "      venue "
					+  " WHERE conevlink.eventid = events.eventid "
					+  " AND events.venueid = venue.venueid "
					+  " AND venue.state < 9 ) events, "
					+  "(SELECT contributorid, COUNT(DISTINCT conevlink.eventid) over (partition by contributorid) marker_count "
					+  " FROM conevlink, "
					+  "      events, "
 					+  "      venue "
					+  " WHERE conevlink.eventid = events.eventid "
					+  " AND events.venueid = venue.venueid "
					+  " AND venue.latitude IS NOT NULL "
					+  " AND venue.state < 9) markers ";
			} else {
				// add state specific limit clause 
				// includes overseas option
				sql += "(SELECT contributorid, COUNT(DISTINCT conevlink.eventid) over (partition by contributorid) event_count "
					+  " FROM conevlink, "
					+  "      events, "
					+  "      venue "
					+  " WHERE conevlink.eventid = events.eventid "
					+  " AND events.venueid = venue.venueid "
					+  " AND venue.state = ? ) events, "
					+  "(SELECT contributorid, COUNT(DISTINCT conevlink.eventid) over (partition by contributorid) marker_count "
					+  " FROM conevlink, "
					+  "      events, "
 					+  "      venue "
					+  " WHERE conevlink.eventid = events.eventid "
					+  " AND events.venueid = venue.venueid "
					+  " AND venue.latitude IS NOT NULL "
					+  " AND venue.state = ?) markers ";
			}
			
			// finalise the sql
			sql += "WHERE CONTAINS(search_contributor.combined_all, ?, 1) > 0 "
			    +  "AND functions.contributorid = search_contributor.contributorid "
			    +  "AND events.contributorid = search_contributor.contributorid "
			    +  "AND markers.contributorid(+) = search_contributor.contributorid "
			    +  "ORDER BY search_contributor.last_name";
			
			// define the paramaters
			if(stateLimit.equals("a")) {
				parameters = new String[1];
				parameters[0] = queryParameter;
			} else {
				parameters = new String[3];
				parameters[0] = stateLimit;
				parameters[1] = stateLimit;
				parameters[2] = queryParameter;
			}
		}
		
		// get the resultset
		ResultSet resultSet = this.dataManager.executePreparedStatement(sql, parameters);
		
		// start the table
		results.append("<form action=\"\" id=\"results_form\" name=\"results_form\">");
		results.append("<table class=\"searchResults\"><thead><tr><th>Contributor</th><th>Function(s)</th><th>Events in Database</th><th>Events to be Mapped</th><th>&nbsp;</th></tr></thead>");
		
		// add a table footer
		results.append("<tfoot>");
		results.append("<tr><td colspan=\"5\"><ul><li>Click the name of a contributor to view a map</li>");
		results.append("<li>Alternatively select a number of contributors and click the \"View Map\" button</li>");
		results.append("<li>Only events at venues that have geographic information in the database can be mapped</li>");
		results.append("<li>Where a contributor has had more than one role at an event, only one marker will be added to the map</li>");
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
				
				// enable selection of this contributor
				if(Integer.parseInt(resultSet.getString(5)) > 0) {
					// at least one event can be mapped
					results.append("<td><a href=\"#\" onclick=\"showContributorMap('" + resultSet.getString(1) + "'); return false;\">" + resultSet.getString(2) + "</a></td>");
				} else {
					// no events can be mapped
					results.append("<td>" + resultSet.getString(2) + "</td>");
				}				
				
				// add remaining data
				results.append("<td>" + resultSet.getString(3) + "</td>");
				results.append("<td>" + resultSet.getString(4) + "</td>");
				results.append("<td>" + resultSet.getString(5) + "</td>");
				
				// add selection box
				if(Integer.parseInt(resultSet.getString(5)) > 0) {
					results.append("<td><input type=\"checkbox\" name=\"contributor\" id=\"contributor\" value=\"" + resultSet.getString(1) + "\"/></td></tr>");
				} else {
					results.append("<td>&nbsp;</td></tr>\n");
				}
				
				// increment counter
				recordCount++;
			}
			
			// check to see if record count reached
			if(recordCount == 16) {
				results.append("<tr><td colspan=\"5\"><strong>Note: </strong>Record limit of 15 records reached, please adjust your search terms to be more specific</td></tr>");
			}else if(recordCount == 0) {
				results.append("<tr class=\"odd\"><td colspan=\"5\" style=\"text-align: center\">No contributors matching the specified search criteria were found.<br/>Please check your criteria and try again</td></tr>");
			}
			
		} catch(java.sql.SQLException ex) {
			throw new javax.servlet.ServletException("Unable to get search results", ex);
		}
		
		// finalise the table
		if(stateLimit != null && recordCount != 0) {
			results.append("<tr><td colspan=\"5\"><strong>Note: </strong>If a contributor is not listed they may not be associated with a venue in the specified region</td></tr>");
		}
		
		if(recordCount != 0) {
			results.append("<tr style=\"border: 1px solid #333333;\"><td colspan=\"5\"><input class=\"ui-state-default ui-corner-all button\" id=\"multi_contrib\" type=\"submit\" name=\"submit\" value=\"Map Contributors\"/></td></tr>");
		}
		
		results.append("</tbody></table></form>");
		
		return results.toString();
		
	}
	
	/**
	 * A method used to get the Marker XML for contributors
	 *
	 * @param queryParameter the parameter to determine which record(s) are of interest
	 *
	 * @return               the string representation of the Marker XML
	 */
	public String getMarkerXMLString(String queryParameter) throws javax.servlet.ServletException {
		return getMarkerXMLString(queryParameter, null);
	}
	
	/**
	 * A method used to get the the Marker XML for an organisation restricted to a date range
	 * using the first date fields in the database
	 *
	 * @param queryParameter the parameter to determine which record(s) are of interest
	 * @param stateLimit     the state id that venues must be in to be part of the dataset
	 *
	 * @return               the string representation of the Marker XML
	 */
	public String getMarkerXMLString(String queryParameter, String stateLimit) throws javax.servlet.ServletException {
	
		// define private variables
		String xmlString;			// string to hold xml 
		int recordCount = 0;		// count number of markers created
		String sql = null;			// the sql to execute
		String[] parameters = null; // variable to hold sql parameters
		String[] ids = null;        // variable to hold individual contributor id numbers
		
		// get the persistent URL template for event
		String eventURLTemplate = this.dataManager.getContextParam("eventURLTemplate");
		
		// try to connect to the database
		this.dataManager.connect(); // dataManager defined in parent object
		
		// check to see if multiple contributors are specified
		if(queryParameter.indexOf(',') != -1) {
			// yes - so break out the ids into an array
			ids = queryParameter.split(",");
		}

		if(stateLimit == null || stateLimit.equals("nolimit")) {
			// all venues
			
			// check to see if we need to add the contributor name to the event name
			if(queryParameter.indexOf(',') != -1) {
				sql = "SELECT DISTINCT e.eventid, e.event_name || ' (' || sc.contrib_name || ')', "
					+ "      e.yyyyfirst_date, e.mmfirst_date, e.ddfirst_date, "
					+ "       e.yyyylast_date, e.mmlast_date, e.ddlast_date, "
					+ "       v.venue_name, v.suburb, v.latitude, v.longitude "
					+ "FROM conevlink c, "
					+ "     events e, "
					+ "     venue v, "
					+ "     search_contributor sc "
					+ "WHERE c.contributorid = ANY (";
					
					// add sufficient place holders for all of the ids
					for(int i = 0; i < ids.length; i++) {
						sql += "?,";
					}
					
					// tidy up the sql
					sql = sql.substring(0, sql.length() -1);
					
					// finish the sql					
					sql += ") AND c.contributorid = sc.contributorid "
					+ "AND e.eventid = c.eventid "
					+ "AND v.venueid = e.venueid "
					+ "AND v.longitude IS NOT NULL "
					+ "ORDER BY e.yyyyfirst_date DESC, e.mmfirst_date DESC, e.ddfirst_date DESC ";
					
					// define the paramaters
					parameters = ids;

			} else {
				sql = "SELECT DISTINCT e.eventid, e.event_name, "
					+ "       e.yyyyfirst_date, e.mmfirst_date, e.ddfirst_date, "
					+ "       e.yyyylast_date, e.mmlast_date, e.ddlast_date, "
					+ "       v.venue_name, v.suburb, v.latitude, v.longitude "
					+ "FROM conevlink c, "
					+ "     events e, "
					+ "     venue v, "
					+ "     search_contributor sc "
					+ "WHERE c.contributorid = ? "
					+ "AND c.contributorid = sc.contributorid "
					+ "AND e.eventid = c.eventid "
					+ "AND v.venueid = e.venueid "
					+ "AND v.longitude IS NOT NULL "
					+ "ORDER BY e.yyyyfirst_date DESC, e.mmfirst_date DESC, e.ddfirst_date DESC ";
					
					// define the paramaters
					parameters = new String[1];
					parameters[0] = queryParameter;
			}
			
		} else {
		
			// check to see if we need to add the contributor name to the event name
			if(queryParameter.indexOf(',') != -1) {
				sql = "SELECT DISTINCT e.eventid, e.event_name || ' (' || sc.contrib_name || ')', "
					+ "      e.yyyyfirst_date, e.mmfirst_date, e.ddfirst_date, "
					+ "       e.yyyylast_date, e.mmlast_date, e.ddlast_date, "
					+ "       v.venue_name, v.suburb, v.latitude, v.longitude "
					+ "FROM conevlink c, "
					+ "     events e, "
					+ "     venue v, "
					+ "     search_contributor sc "
					+ "WHERE c.contributorid = ANY (";
					
					// add sufficient place holders for all of the ids
					for(int i = 0; i < ids.length; i++) {
						sql += "?,";
					}
					
					// tidy up the sql
					sql = sql.substring(0, sql.length() -1);
					
					// finish the sql segment					
					sql += ") AND c.contributorid = sc.contributorid "
					+ "AND e.eventid = c.eventid "
					+ "AND v.venueid = e.venueid ";

			} else {
				sql = "SELECT DISTINCT e.eventid, e.event_name, "
					+ "       e.yyyyfirst_date, e.mmfirst_date, e.ddfirst_date, "
					+ "       e.yyyylast_date, e.mmlast_date, e.ddlast_date, "
					+ "       v.venue_name, v.suburb, v.latitude, v.longitude "
					+ "FROM conevlink c, "
					+ "     events e, "
					+ "     venue v, "
					+ "     search_contributor sc "
					+ "WHERE c.contributorid = ? "
					+ "AND c.contributorid = sc.contributorid "
					+ "AND e.eventid = c.eventid "
					+ "AND v.venueid = e.venueid ";
			}
				
			if(stateLimit.equals("a")) {
				// add australia state limit clause
				sql += "AND v.state < 9 ";
				
			} else {
				// add state specific limit clause 
				// includes overseas option
				sql += "AND v.state = ? ";	
			}
			
			// finalise the sql
			sql += "AND v.longitude IS NOT NULL "
				+  "ORDER BY e.yyyyfirst_date DESC, e.mmfirst_date DESC, e.ddfirst_date DESC ";
				
			// define the parameters
			if (queryParameter.indexOf(',') != -1) {
				// must deal with multiple ids
				if (stateLimit.equals("a")) {
					// no need for a state limit parameter
					parameters = ids;
				} else {
					// must add the state limit parameter
					parameters = new String[ids.length + 1];
					
					// add is to the list of parameters
					for(int i = 0; i < ids.length; i++) {
						parameters[i] = ids[i];
					}
					
					// add the state parameter
					parameters[ids.length] = stateLimit;
				}
			
			} else {
				// only one id
				if (stateLimit.equals("a")) {
					// no need for a state limit parameter
					// define the paramaters
					parameters = new String[1];
					parameters[0] = queryParameter;
				} else {
					// define the paramaters
					parameters = new String[2];
					parameters[0] = queryParameter;
					parameters[1] = stateLimit;
				}				
			}
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
				
				// add start and end date for program purposes
				date = this.buildDate(resultSet.getString(3), resultSet.getString(4), null); 
				marker.setAttribute("sliderDate", date);
				
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
			for(int i = 0; i < ids.length; i++) {
			
				// define the query parameters
				parameters[0] = ids[i];
				
				// store the current contributor name
				currentContributor = getNameByID(ids[i]);
				
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
						// add a document for the standard map
						document = exportFile.addDocument(contribFolder, "Events with TimeSpan");
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
					
						// need to add a document that includes timespan elements
						// add a document for the standard map
						document = exportFile.addDocument(contribFolder, "Events with Trajectory");
						exportFile.addDescriptionElement(document, "One place marker for each event, linked with trajectory information. Note: Trajectory information links venues based on the earliest event to occur at that venue and does not indicate a tour.");
						
						// get the number of records
						resultSet.last();
						int rowCount = resultSet.getRow();
						resultSet.first();
						
						// add the right number of line style elements
						if(rowCount > 255) {
							// add the maximum number of lines
							for(int x = 1; x <= 255; x++) {
								exportFile.addLineStyle("traj-" + x, exportFile.getGradientColour(x - 1, 255), "4");
							}
						} else {
							// add the require number of lines
							for(int x = 1; x <= rowCount; x++) {
								exportFile.addLineStyle("traj-" + x, exportFile.getGradientColour(x - 1, rowCount), "4");
							}
						}
												
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
								if (rowCount <= 255) {
									styleId = "traj-" + Integer.toString(resultSet.getRow() - 2);
								} else {
									styleId = "traj-" + (exportFile.getNormalisedColourIndex(resultSet.getRow(), rowCount) - 1);
								}
								
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
						}
											
					} // end adding trajectory elements
				
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
