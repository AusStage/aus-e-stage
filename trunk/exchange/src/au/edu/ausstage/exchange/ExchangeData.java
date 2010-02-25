/*
 * This file is part of the AusStage Data Exchange Service
 *
 * AusStage Data Exchange Service is free software: you can redistribute
 * it and/or modify it under the terms of the GNU General Public License 
 * as published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * AusStage Data Exchange Service is distributed in the hope that it will 
 * be useful, but WITHOUT ANY WARRANTY; without even the implied warranty 
 * of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with AusStage Data Exchange Service.  
 * If not, see <http://www.gnu.org/licenses/>.
*/

package au.edu.ausstage.exchange;

// import additional classes
import java.io.*;
import java.sql.*;
import org.w3c.dom.*;
import javax.xml.parsers.*; 
import javax.xml.transform.*; 
import javax.xml.transform.dom.*; 
import javax.xml.transform.stream.*;

// import the json library
import org.json.simple.*;

/**
 * A class used to build the data sets used for the exchange data service
 */
public class ExchangeData {

	private DataManager dataManager;

	/**
	 * Constructor for this class
	 *
	 * @param dataManager the dataManager object used to access the database
	 */
	public ExchangeData(DataManager dataManager) {
		this.dataManager = dataManager;
	}

	/**
	 * A method to get information for an organisation
	 *
	 * @param ids		  the id number or numbers, of the organisation of interest
	 * @param type		  the output type
	 * @param limit		  the limit on the number of records returned
	 *
	 * @return			  the dataset for this organisation
	 */
	public String getOrganisationData(String[] ids, String type, String limit) throws javax.servlet.ServletException {
	
		// declare helper variables
		String results = null;
	
		// build the condition clause
		StringBuilder condition = new StringBuilder("WHERE (");
		
		// add a sufficient number of parameter place holders
		for(int i = 0; i < ids.length; i++) {
			condition.append(" organisationid = ? OR");
		}
		
		// finalise the condition clause 
		String conditionClause = condition.toString();
		conditionClause = conditionClause.substring(0, conditionClause.length() - 2);
		conditionClause += ") ";			
	
		// build the sql	
		String sql = "SELECT DISTINCT orgevlink.eventid, events.event_name, events.yyyyfirst_date, events.mmfirst_date, events.ddfirst_date, "
				   + "venue.venue_name, venue.suburb, states.state "
				   + "FROM orgevlink, events, venue, states "
				   + conditionClause
				   + "AND orgevlink.eventid = events.eventid "
				   + "AND events.venueid = venue.venueid "
				   + "AND venue.state = states.stateid "
				   + "ORDER BY events.yyyyfirst_date DESC, events.mmfirst_date DESC, events.ddfirst_date DESC";
				   
		// connect to the database
		dataManager.connect();
		
		// execute the query
		ResultSet resultSet = dataManager.executePreparedStatement(sql, ids);
		
		// build the required results
		if (type.equals("html") == true) {
			results = this.buildHtml(resultSet, limit);
		} else if(type.equals("json") == true) {
			results = this.buildJson(resultSet, limit);
		} else if(type.equals("xml") == true) {
			results = this.buildXml(resultSet, limit);
		} else if(type.equals("rss") == true) {
			results = this.buildRss(resultSet, limit);
		}
				
		// return the dataset
		return results;
	
	} // end getOrganisationData method
	
	/**
	 * A method to get information for a contributor
	 *
	 * @param ids		  the id number or numbers, of the organisation of interest
	 * @param type		  the output type
	 * @param limit		  the limit on the number of records returned
	 *
	 * @return			  the dataset for this organisation
	 */
	public String getContributorData(String[] ids, String type, String limit) throws javax.servlet.ServletException {
	
		// declare helper variables
		String results = null;
	
		// build the condition clause
		StringBuilder condition = new StringBuilder("WHERE (");
		
		// add a sufficient number of parameter place holders
		for(int i = 0; i < ids.length; i++) {
			condition.append(" contributorid = ? OR");
		}
		
		// finalise the condition clause 
		String conditionClause = condition.toString();
		conditionClause = conditionClause.substring(0, conditionClause.length() - 2);
		conditionClause += ") ";			
	
		// build the sql	
		String sql = "SELECT DISTINCT conevlink.eventid, events.event_name, events.yyyyfirst_date, events.mmfirst_date, events.ddfirst_date, "
				   + "venue.venue_name, venue.suburb, states.state "
				   + "FROM conevlink, events, venue, states "
				   + conditionClause
				   + "AND conevlink.eventid = events.eventid "
				   + "AND events.venueid = venue.venueid "
				   + "AND venue.state = states.stateid "
				   + "ORDER BY events.yyyyfirst_date DESC, events.mmfirst_date DESC, events.ddfirst_date DESC";
				   
		// connect to the database
		dataManager.connect();
		
		// execute the query
		ResultSet resultSet = dataManager.executePreparedStatement(sql, ids);
		
		// build the required results
		if (type.equals("html") == true) {
			results = this.buildHtml(resultSet, limit);
		} else if(type.equals("json") == true) {
			results = this.buildJson(resultSet, limit);
		} else if(type.equals("xml") == true) {
			results = this.buildXml(resultSet, limit);
		} else if(type.equals("rss") == true) {
			results = this.buildRss(resultSet, limit);
		}
				
		// return the dataset
		return results;
	
	} // end getContributorData method
	
	/**
	 * A method to get information for a venue
	 *
	 * @param ids		  the id number or numbers, of the organisation of interest
	 * @param type		  the output type
	 * @param limit		  the limit on the number of records returned
	 *
	 * @return			  the dataset for this organisation
	 */
	public String getVenueData(String[] ids, String type, String limit) throws javax.servlet.ServletException {
	
		// declare helper variables
		String results = null;
	
		// build the condition clause
		StringBuilder condition = new StringBuilder("WHERE (");
		
		// add a sufficient number of parameter place holders
		for(int i = 0; i < ids.length; i++) {
			condition.append(" venue.venueid = ? OR");
		}
		
		// finalise the condition clause 
		String conditionClause = condition.toString();
		conditionClause = conditionClause.substring(0, conditionClause.length() - 2);
		conditionClause += ") ";			
	
		// build the sql	
		String sql = "SELECT DISTINCT events.eventid, events.event_name, events.yyyyfirst_date, events.mmfirst_date, events.ddfirst_date, "
				   + "venue.venue_name, venue.suburb, states.state "
				   + "FROM events, venue, states "
				   + conditionClause
				   + "AND events.venueid = venue.venueid "
				   + "AND venue.state = states.stateid "
				   + "ORDER BY events.yyyyfirst_date DESC, events.mmfirst_date DESC, events.ddfirst_date DESC";
				   
		// connect to the database
		dataManager.connect();
		
		// execute the query
		ResultSet resultSet = dataManager.executePreparedStatement(sql, ids);
		
		// build the required results
		if (type.equals("html") == true) {
			results = this.buildHtml(resultSet, limit);
		} else if(type.equals("json") == true) {
			results = this.buildJson(resultSet, limit);
		} else if(type.equals("xml") == true) {
			results = this.buildXml(resultSet, limit);
		} else if(type.equals("rss") == true) {
			results = this.buildRss(resultSet, limit);
		}
				
		// return the dataset
		return results;
	
	} // end getVenueData method
	
	/**
	 * A method used to build a html result set
	 *
	 * @param resultSet the ResultSet object containing the data
	 * @param limit     the number of records to return
	 *
	 * @return          the dataset
	 */
	private String buildHtml(ResultSet resultSet, String limit) throws javax.servlet.ServletException {
	
		// declare helper variables
		int recordCount = 0;
		int recordLimit = 1;
		boolean limitedRecords = false;
		StringBuilder results = new StringBuilder("<ul class=\"ausstage_events\">");
		String eventURLTemplate = dataManager.getContextParam("eventURLTemplate");
	
		// determine the record limit
		if(limit.equals("all") == false) {
			recordLimit = Integer.parseInt(limit);
			limitedRecords = true;
		}
		
		// build the resultset
		try {
		
			while(resultSet.next() && recordCount < recordLimit) {
			
				// build the event url
				String url = eventURLTemplate.replace("[event-id]", resultSet.getString(1));  // replace the constant with the event id
				
				// add event name and link to AusStage record
				results.append("<li><a href=\"" + url + "\" title=\"More information in AusStage\">" + resultSet.getString(2).replaceAll("[\\r\\n]+", "") + "</a>, ");
				
				// add the venue name
				results.append(resultSet.getString(6));
				
				// add the venue suburb
				if(resultSet.getString(7) != null) {
					results.append(", " + resultSet.getString(7));
				}
				
				// add the venue state
				if(resultSet.getString(8) != null) {
					results.append(", " + resultSet.getString(8));
				}
				
				// add the start date
				results.append(", " + buildDisplayDate(resultSet.getString(3), resultSet.getString(4), resultSet.getString(5)));
				
				// finish this entry
				results.append("</li>");
				
				// do we need to keep track of record count?
				if(limitedRecords == true) {
					recordCount ++;
				}
			} // end while loop
			
			// finalise the html
			results.append("</ul>");
			
			// play nice and tidy up database resources
			resultSet.close();
			this.dataManager.closeStatement();
		
		} catch(Exception ex) {
			throw new javax.servlet.ServletException("Unable to build html dataset", ex);
		}	
		
		// return the dataset
		return results.toString();

	} // end buildHtml method
	
	/** 
	 * A method used to build a json result set
	 *
	 * @param resultSet the ResultSet object containing the data
	 * @param limit     the number of records to return
	 *
	 * @return          the dataset
	 */
	 
	 /*
	 * NOTE: using the json-simple library produces what appear to be harmless warnings about unchecked or unsafe operations
	 * these have been surpressed.
	 */
	
	@SuppressWarnings("unchecked")
	private String buildJson(ResultSet resultSet, String limit) throws javax.servlet.ServletException {
	
		// declare helper variables
		int recordCount = 0;
		int recordLimit = 1;
		boolean limitedRecords = false;
		
		// use org.json.simple classes
		JSONArray results = new JSONArray();

		// get the event url template
		String eventURLTemplate = dataManager.getContextParam("eventURLTemplate");
	
		// determine the record limit
		if(limit.equals("all") == false) {
			recordLimit = Integer.parseInt(limit);
			limitedRecords = true;
		}
		
		// build the resultset 
		try {
		
			while(resultSet.next() && recordCount < recordLimit) {
			
				// build the event url
				String url = eventURLTemplate.replace("[event-id]", resultSet.getString(1));  // replace the constant with the event id
				
				// create a JSONObject
				JSONObject object = new JSONObject();
				
				// add the event url
				object.put("url", url);
				object.put("event", resultSet.getString(2).replaceAll("[\\r\\n]+", ""));
				object.put("venue", resultSet.getString(6));
								
				// add the venue suburb
				if(resultSet.getString(7) != null) {
					object.put("suburb", resultSet.getString(7));
				} else {
					object.put("suburb", "");
				}
				
				// add the venue state
				if(resultSet.getString(8) != null) {
					object.put("state", resultSet.getString(8));
				} else {
					object.put("state", "");
				}
				
				// add the start date
				object.put("first", buildDisplayDate(resultSet.getString(3), resultSet.getString(4), resultSet.getString(5)));
				
				// add the object to the array
				results.add(object);
				
				// do we need to keep track of record count?
				if(limitedRecords == true) {
					recordCount ++;
				}
				
			} // end while loop		
			
			// play nice and tidy up database resources
			resultSet.close();
			this.dataManager.closeStatement();		
		
		} catch(Exception ex) {
			throw new javax.servlet.ServletException("Unable to build json dataset", ex);
		}
		
		// return the dataset
		return results.toString();
	
	} // end buildJson method
	
	/**
	 * A method used to build a xml result set
	 *
	 * @param resultSet the ResultSet object containing the data
	 * @param limit     the number of records to return
	 *
	 * @return          the dataset
	 */
	private String buildXml(ResultSet resultSet, String limit) throws javax.servlet.ServletException {
	
		// declare helper variables
		int recordCount = 0;
		int recordLimit = 1;
		boolean limitedRecords = false;
		
		// get the event url template
		String eventURLTemplate = dataManager.getContextParam("eventURLTemplate");
	
		// determine the record limit
		if(limit.equals("all") == false) {
			recordLimit = Integer.parseInt(limit);
			limitedRecords = true;
		}
		
		// build the resultset
		try {
			
			// create the xml document object
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder        builder = factory.newDocumentBuilder();
			Document			   xmlDoc  = builder.newDocument();
			
			// add the root element
			Element rootElement = xmlDoc.createElement("events");
			xmlDoc.appendChild(rootElement);
		
			while(resultSet.next() && recordCount < recordLimit) {
			
				// create a event element
				Element event = xmlDoc.createElement("event");
			
				// build the event url
				String url = eventURLTemplate.replace("[event-id]", resultSet.getString(1));  // replace the constant with the event id
				
				// add the event url
				Element urlElement = xmlDoc.createElement("url");
				urlElement.setTextContent(url);
				event.appendChild(urlElement);
				
				// add the event name
				Element eventName = xmlDoc.createElement("name");
				eventName.setTextContent(resultSet.getString(2).replaceAll("[\\r\\n]+", ""));
				event.appendChild(eventName);
				
				// add the suburb
				Element suburb = xmlDoc.createElement("suburb");
								
				if(resultSet.getString(7) != null) {
					suburb.setTextContent(resultSet.getString(7));
				}
				event.appendChild(suburb);
				
				// add the venue state
				Element state = xmlDoc.createElement("state");
				
				if(resultSet.getString(8) != null) {
					state.setTextContent(resultSet.getString(8));
				}
				event.appendChild(state);
				
				// add the start date
				Element start = xmlDoc.createElement("first");
				start.setTextContent(buildDisplayDate(resultSet.getString(3), resultSet.getString(4), resultSet.getString(5)));
				event.appendChild(start);
				
				// add event element to the tree
				rootElement.appendChild(event);
				
				// do we need to keep track of record count?
				if(limitedRecords == true) {
					recordCount ++;
				}
				
			} // end while loop		
			
			// play nice and tidy up database resources
			resultSet.close();
			this.dataManager.closeStatement();		
			
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
		
		} catch(javax.xml.transform.TransformerException ex) {
			throw new javax.servlet.ServletException("Unable to build xml dataset", ex);
		}catch(java.sql.SQLException ex) {
			throw new javax.servlet.ServletException("Unable to build xml dataset", ex);
		}catch(javax.xml.parsers.ParserConfigurationException ex) {
			throw new javax.servlet.ServletException("Unable to build xml dataset", ex);
		}
	
	} // end buildXml method
	
	/**
	 * A method used to build a rss result set
	 * Always outputs a maximum of 10 records
	 *
	 * @param resultSet the ResultSet object containing the data
	 *
	 * @return          the dataset
	 */
	private String buildRss(ResultSet resultSet, String limit) throws javax.servlet.ServletException {
	
		// declare helper variables
		int recordCount = 0;
		
		int recordLimit = 1;
		boolean limitedRecords = false;
		
		// get the event url template
		String eventURLTemplate = dataManager.getContextParam("eventURLTemplate");
	
		// determine the record limit
		if(limit.equals("all") == false) {
			recordLimit = Integer.parseInt(limit);
			limitedRecords = true;
		}
		
		// build the resultset
		try {
			
			// create the xml document object
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder        builder = factory.newDocumentBuilder();
			Document			   xmlDoc  = builder.newDocument();
			
			// add the root element
			Element rootElement = xmlDoc.createElement("rss");
			rootElement.setAttribute("version", "2.0");
			xmlDoc.appendChild(rootElement);
			
			// add the channel element
			Element channelElement = xmlDoc.createElement("channel");
			rootElement.appendChild(channelElement);
			
			// add required channel elements
			Element channelTitle = xmlDoc.createElement("title"); // title
			channelTitle.setTextContent("AusStage - Data Exchange Service");
			channelElement.appendChild(channelTitle);
			
			Element channelLink = xmlDoc.createElement("link"); // link
			channelLink.setTextContent("http://www.ausstage.edu.au/");
			channelElement.appendChild(channelLink);
			
			Element channelDescription = xmlDoc.createElement("description"); // description
			channelDescription.setTextContent("Event information sourced from the AusStage website");
			channelElement.appendChild(channelDescription);
			
			// get information about this system
			String systemName    = dataManager.getContextParam("systemName");
			String systemVersion = dataManager.getContextParam("systemVersion");
			String systemBuild   = dataManager.getContextParam("buildVersion");
			String systemUrl     = dataManager.getContextParam("systemUrl");
			
			Element channelGenerator = xmlDoc.createElement("generator"); // generator
			channelGenerator.setTextContent(systemName + " " + systemVersion + " " + systemBuild + " " + systemUrl);
			channelElement.appendChild(channelGenerator);
		
			while(resultSet.next() && recordCount < recordLimit) {
			
				// create an item element
				Element item = xmlDoc.createElement("item");
			
				// build the event url
				String url = eventURLTemplate.replace("[event-id]", resultSet.getString(1));  // replace the constant with the event id
				
				// add the event url
				Element link = xmlDoc.createElement("link");
				link.setTextContent(url);
				item.appendChild(link);
				
				// add a guid element
				Element guid = xmlDoc.createElement("guid");
				guid.setTextContent(url);
				guid.setAttribute("isPermaLink", "true");
				item.appendChild(guid);
				
				// add the title
				Element title = xmlDoc.createElement("title");
				title.setTextContent(resultSet.getString(2).replaceAll("[\\r\\n]+", ""));
				item.appendChild(title);
				
				// build the description
				StringBuilder descriptionContent = new StringBuilder("Venue: ");
				descriptionContent.append(resultSet.getString(6)); // venue name
				
				// add the venue suburb
				if(resultSet.getString(7) != null) {
					descriptionContent.append(", " + resultSet.getString(7));
				}
				
				// add the venue state
				if(resultSet.getString(8) != null) {
					descriptionContent.append(", " + resultSet.getString(8));
				}
				
				// add the start date
				descriptionContent.append(", First Date: " + buildDisplayDate(resultSet.getString(3), resultSet.getString(4), resultSet.getString(5)));
				
				// add the description element
				Element description = xmlDoc.createElement("description");
				description.setTextContent(descriptionContent.toString());
				item.appendChild(description);
				
				// add item element to the tree
				channelElement.appendChild(item);
				
				// do we need to keep track of record count?
				if(limitedRecords == true) {
					recordCount ++;
				}
				
			} // end while loop		
			
			// play nice and tidy up database resources
			resultSet.close();
			this.dataManager.closeStatement();		
			
			// create a transformer 
			TransformerFactory transFactory = TransformerFactory.newInstance();
			Transformer        transformer  = transFactory.newTransformer();
			
			// set some options on the transformer
			transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			
			// get a transformer and supporting classes
			StringWriter writer = new StringWriter();
			StreamResult result = new StreamResult(writer);
			DOMSource    source = new DOMSource(xmlDoc);
			
			// transform the xml document into a string
			transformer.transform(source, result);
			return writer.toString();
		
		} catch(javax.xml.transform.TransformerException ex) {
			throw new javax.servlet.ServletException("Unable to build rss dataset", ex);
		}catch(java.sql.SQLException ex) {
			throw new javax.servlet.ServletException("Unable to build rss dataset", ex);
		}catch(javax.xml.parsers.ParserConfigurationException ex) {
			throw new javax.servlet.ServletException("Unable to build rss dataset", ex);
		}
	
	} // end buildRss method
	
	/**
	 * A method used to build a date for display in line with existing formatting rules
	 *
	 * @param year  the year component of the date
	 * @param month the month component of the date
	 * @param day   the day component of the month
	 *
	 * @return      a string containing the finalised date
	 */
	public String buildDisplayDate(String year, String month, String day) {
	 
	 	String date = day + " " + this.lookupMonth(month) + " " + year;
	 	date = date.trim();
	 	date = date.replace("null","");		
		return date;
	 
	} // end buildDisplayDate method
	 
	/**
	 * A method used to lookup the name of a month based on its number
	 *
	 * @param month the month as a digit
	 *
	 * @return      a string containing the name of the month
	 */
	private String lookupMonth(String month) {
	 
		// check on the month parameter
	 	if(month == null || month.equals("")) {
	 		return "";
	 	}
	 
	 	// prepare the month
	 	month = month.trim();
	 	
	 	// double check the month parameter
	 	if(month == null || month.equals("")) {
	 		return "";
	 	}
	 
	 	// convert the string to an int
	 	int i = Integer.parseInt(month);
	 	
	 	switch (i) {
	 		case 1:  return "January";
	 		case 2:  return "February";
	 		case 3:  return "March";
	 		case 4:  return "April";
	 		case 5:  return "May";
	 		case 6:  return "June";
	 		case 7:  return "July";
	 		case 8:  return "August";
	 		case 9:  return "September";
	 		case 10: return "October";
	 		case 11: return "November";
	 		case 12: return "December";
	 		default: return "";
	 	}
	} // end lookupMonth method

} // end class definition
