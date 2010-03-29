/*
 * This file is part of the AusStage Audience Participation Service
 *
 * The AusStage Audience Participation Service is free software: you can 
 * redistribute it and/or modify it under the terms of the GNU General 
 * Public License as published by the Free Software Foundation, either 
 * version 3 of the License, or (at your option) any later version.
 *
 * The AusStage Audience Participation Service is distributed in the hope 
 * that it will be useful, but WITHOUT ANY WARRANTY; without even the 
 * implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  
 * See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with the AusStage Audience Participation Service.  
 * If not, see <http://www.gnu.org/licenses/>.
*/

package au.edu.ausstage.mobile;

// import additional classes
import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;
import java.sql.*;
import org.w3c.dom.*;
import javax.xml.parsers.*; 
import javax.xml.transform.*; 
import javax.xml.transform.dom.*; 
import javax.xml.transform.stream.*; 

/**
 * A class to manage all lookup operations
 */
public class LookupManager {

	// declare private class variables
	private DataManager dataManager;

	/**
	 * Constructor for this class
	 *
	 * @param manager a valid DataManager object
	 */
	public LookupManager(DataManager manager) {
		this.dataManager = manager;
	} // end the constructor
	
	/**
	 * A method to lookup venues that are within the specified geographic boundaries
	 *
	 * @param northEast the coordinates of the north east corner of the boundary box
	 * @param southWest the coordinates of the south west corner of the boundary box
	 *
	 * @returns         a string containing the venue information in XML format for use in a map
	 */
	public String getVenuesByCoords(String northEast, String southWest) throws ServletException {
	
		// split the coordinates into their individual values
		String[] northCoords = northEast.split(",");
		String[] southCoords = southWest.split(",");
		
		// double check on the coordinates
		if(northCoords.length != 2 || southCoords.length != 2) {
			throw new ServletException("Unable to split coordinates into the component parts");
		}
		
		// get the data
		String sql = "SELECT venueid, venue_name, suburb, latitude, longitude "
				   + "FROM venue "
				   + "WHERE latitude >= ? "
				   + "AND longitude <= ? "
				   + "AND latitude <= ? "
				   + "AND longitude >= ? ";
				   
		// build the params array
		String[] params = new String[4];
		params[0] = northCoords[0];
		params[1] = northCoords[1];
		params[2] = southCoords[0];
		params[3] = southCoords[1];
		
		try {
			// execute the query
			ResultSet resultSet = dataManager.executePreparedStatement(sql, params);
			
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
				
				// add attributes
				marker.setAttribute("url",    "venuedetails.jsp?id=" + resultSet.getString(1)); // venue details url
				marker.setAttribute("venue",  resultSet.getString(2));  // venue name
				marker.setAttribute("suburb", resultSet.getString(3));  // venue name
				marker.setAttribute("lat",    resultSet.getString(4)); // latitude
				marker.setAttribute("lng",    resultSet.getString(5)); // longitude
				
				// add this element to the document
				rootElement.appendChild(marker);
			}
			
			// play nice and tidy up
			resultSet.close();
			dataManager.tidyUp();
			
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
			return writer.toString();
			
		} catch (java.sql.SQLException ex) {
			throw new ServletException("Unable to execute marker xml query", ex);
		} catch(javax.xml.parsers.ParserConfigurationException ex) {
			throw new javax.servlet.ServletException("Unable to build marker xml", ex);
		} catch(Exception ex) {
			throw new javax.servlet.ServletException("Unable to build marker xml", ex);
		}	
	
	} // end getVenuesByCoords method




} // end class defintion
