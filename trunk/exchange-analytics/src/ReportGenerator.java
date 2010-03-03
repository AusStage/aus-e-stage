/*
 * This file is part of the AusStage Exchange Analytics application
 *
 * The AusStage Exchange Analytics application is free software: you can 
 * redistribute it and/or modify it under the terms of the GNU General 
 * Public License as published by the Free Software Foundation, either 
 * version 3 of the License, or (at your option) any later version.
 *
 * The AusStage Exchange Analytics application is distributed in the hope 
 * that it will be useful, but WITHOUT ANY WARRANTY; without even the 
 * implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  
 * See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with the AusStage Exchange Analytics application.  
 * If not, see <http://www.gnu.org/licenses/>.
*/

// import additional libraries
import java.io.*;
import java.sql.*;
import org.w3c.dom.*;
import javax.xml.parsers.*; 
import javax.xml.transform.*; 
import javax.xml.transform.dom.*; 
import javax.xml.transform.stream.*; 

/**
 * A class used to parse log files and the relevant lines to the database
 */
 
public class ReportGenerator {

	// private class level variables
	private Connection database;
	
	// private class level variables for XML generation
	private DocumentBuilderFactory factory;
	private	DocumentBuilder        builder;
	private DOMImplementation      domImpl;
	private Document               xmlDoc = null;
	private Element                rootElement = null;
	
	// report metadata
	private final String REPORT_TITLE = "AusStage Data Exchange Service Analytics";
	private String description;
	private final String ANALYSIS_ID = "exchange-01";
	
	// chart defaults
	private final int BAR_CHART_WIDTH  = 750;
	private final int BAR_CHART_HEIGHT = 125;

	/**
	 * Constructor for this class
	 *
	 * @param conn a valid connection to the database
	 */
	public ReportGenerator(Connection conn) {
	
		// store the connection for later reuse
		database = conn;
	
	} // end constructor
	
	/**
	 * A method used to generate the report
	 *
	 * @return true if, and only if, the report is generated successfully and ready to be saved
	 */
	public boolean generate() {
	
		// declare helper variables
		boolean status = false;
		ChartManager chart = new ChartManager();
		String chartURL = null;
		
		// start the report
		status = this.startReport();
		
		// check to see if it is OK to proceed
		if(status == false) {
			// an error has occured
			return false;
		}
		
		// build the report description
		description = "<p>This report contains information about the usage of the <a href=\"http://beta.ausstage.edu.au/exchange/\" title=\"Direct link to the service\">AusStage Data Exchange Service</a></p>";
		
		// add the report metadata
		status =  this.addReportMetadata();
		
		// check to see if it is OK to proceed
		if(status == false) {
			// an error has occured
			return false;
		}
		
		// add a section
		status = this.addSection("description", ReportConstants.DISPLAY_ALL, "Test descriptive section", "<p>Hello World</p>");
		
		// check to see if it is OK to proceed
		if(status == false) {
			// an error has occured
			return false;
		}
		
		// get the chart for the current month
		chartURL = this.buildMonthChart(ReportConstants.CURRENT_MONTH, chart, this.BAR_CHART_WIDTH, this.BAR_CHART_HEIGHT);
		
		// check on what is returned
		if(chartURL == null) {
			System.out.println("An error occured while building a chart, see previous error message for details");
			return false;
		}
		
		// add the section for the current month chart
		status = this.addSectionWithChart("current-month-chart", ReportConstants.DISPLAY_DESKTOP, "Requests for the Current Month", "<p>This chart provides an overview of the number of successful requests to the Exchange Data Service for the current month</p>", chartURL, this.BAR_CHART_WIDTH, this.BAR_CHART_HEIGHT); 
		
		// get the chart for the previous month
		chartURL = this.buildMonthChart(ReportConstants.PREVIOUS_MONTH, chart, this.BAR_CHART_WIDTH, this.BAR_CHART_HEIGHT);
		
		// check on what is returned
		if(chartURL == null) {
			System.out.println("An error occured while building a chart, see previous error message for details");
			return false;
		}
		
		// add the section for the current month chart
		status = this.addSectionWithChart("prev-month-chart", ReportConstants.DISPLAY_DESKTOP, "Requests for the Previous Month", "", chartURL, this.BAR_CHART_WIDTH, this.BAR_CHART_HEIGHT); 

		// debug code
		return true;
	
	} // end generate method
	
	/**
	 * A method to build a chart for a months worth of data
	 * 
	 * @param chartType one of the ReportConstants either CURRENT_MONTH or PREVIOUS_MONTH
	 * @param chart     a valid ChartManager object used to build this chart
	 * @param chartHeight the height of the chart
	 * @param chartWidth  the width of the chart
	 *
	 * @return          the URL for this chart
	 */
	private String buildMonthChart(int chartType, ChartManager chart, int chartWidth, int chartHeight) {
	
		// declare helper variables
		int monthInt       = 0;
		String monthString = null;
		String yearString  = null;
		int    yearInt     = 0;
		
		// determine the month to be looking at
		if(chartType == ReportConstants.CURRENT_MONTH) {
			
			// define month variables
			monthString = this.getCurrentMonth();
			monthInt    = Integer.parseInt(monthString);
			
			// define the year variables
			yearString = this.getCurrentYear();
			yearInt    = Integer.parseInt(yearString);
			
		} else if (chartType == ReportConstants.PREVIOUS_MONTH) {
		
			// define month variabls
			monthInt    = Integer.parseInt(this.getCurrentMonth()) - 1;
			monthString = String.format("%02d", monthInt);
			
			// check for rolling back too far
			if(monthInt == 0) {
			
				// set the month to Dec 
				monthInt = 12;
				monthString = "12";
				
				// go back one year
				yearInt    = Integer.parseInt(yearString) - 1;
				yearString = Integer.toString(yearInt);
			} else {
				// define the year variables
				yearString = this.getCurrentYear();
				yearInt    = Integer.parseInt(yearString);
			}
		}
		
		// determine number of days in this month
		int maxDays = Integer.parseInt(this.getLastDay(yearString, monthString));		

		// declare additional helper variables
		int currentDay   = 1; // the current day
		int maxRequests  = 0; // keep track of the maximum number of requests
		String[] data    = new String[maxDays]; // array of data values
		
		// database related helper variables
		PreparedStatement statement;
		ResultSet results;
		
		// prepare an SQL statement
		try {
			statement = database.prepareStatement("SELECT COUNT(date) FROM requests WHERE date = ?");
		} catch(java.sql.SQLException ex) {
			System.out.println("ERROR: Unable to prepare SQL statement in buildMonthChart method\n" + ex);
			return null;
		}
		
		try {
		
			// loop through the days getting the data
			while (currentDay <= maxDays) {
			
				// build the date for this query
				statement.setString(1, yearString + "-" + monthString + "-" + String.format("%02d", currentDay));
				
				// execute the query
				results = statement.executeQuery();
				
				if(results.next() == true) {
					data[currentDay -1] = results.getString(1);
					
					if(Integer.parseInt(results.getString(1)) > maxRequests) {
						maxRequests = Integer.parseInt(results.getString(1));
					}
					
				} else {
					data[currentDay -1] = "0";
				}

				// increment the currentDay count
				currentDay++;
				
				// play nice and close the resultset
				results.close();	
			} 
		} catch(java.sql.SQLException ex) {
			System.out.println("ERROR: Unable to execute SQL in buildMonthChart method\n" + ex);
			return null;
		}
		
		// build the chart title
		String chartTitle = "Requests by day for " + this.lookupMonth(monthInt) + " " + yearString;
		
		return chart.buildBarChart(Integer.toString(chartWidth), Integer.toString(chartHeight), chart.simpleEncode(data, maxRequests), chartTitle, Integer.toString(maxRequests));	
	
	} // end buildMonthChart method
	 
	
	/**
	 * A method to create the XML document ready for populating with data
	 * 
	 * @return true if, and only if, the report is ready to be populated
	 */
	private boolean startReport() {
	
		try {
			// create the xml document builder factory object
			factory = DocumentBuilderFactory.newInstance();
			
			// set the factory to be namespace aware
			factory.setNamespaceAware(true);
			
			// create the xml document builder object and get the DOMImplementation object			
			builder = factory.newDocumentBuilder();
			domImpl = builder.getDOMImplementation();
			
			// create a document with the appropriare default namespace
			xmlDoc = domImpl.createDocument("http://beta.ausstage.edu.au/xmlns/report", "report", null);
			
			// get the root element
			rootElement = xmlDoc.getDocumentElement();
			
			/*
			// add schema namespace to the root element
			rootElement.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns:xsi", "http://www.w3.org/2001/XMLSchema-instance");
			
			// add reference to the kml schema
			rootElement.setAttribute("xsi:schemaLocation", "http://www.opengis.net/kml/2.2 http://schemas.opengis.net/kml/2.2.0/ogckml22.xsd");	
			*/
		} catch(javax.xml.parsers.ParserConfigurationException ex) {
			System.out.println("ERROR: Unable to configure XML classes.\n" + ex);
			return false;
		}
		
		return true;
			
	} // end startReport method
	
	/**
	 * A method to create add the report metadata
	 * 
	 * @return true if, and only if, the report is ready to be populated
	 */
	private boolean addReportMetadata() {
	
		try{
		
			// add the title
			Element currentElement = xmlDoc.createElement("title");
			currentElement.setTextContent(REPORT_TITLE);
			rootElement.appendChild(currentElement);
			
			// add the description
			currentElement = xmlDoc.createElement("description");
			currentElement.appendChild(xmlDoc.createCDATASection(description));
			rootElement.appendChild(currentElement);
			
			// add the analysis id
			currentElement = xmlDoc.createElement("analysis_id");
			currentElement.setTextContent(ANALYSIS_ID);
			rootElement.appendChild(currentElement);
			
			// add the date and time
			currentElement = xmlDoc.createElement("generated");
			currentElement.setTextContent(this.getCurrentDateAndTime());
			rootElement.appendChild(currentElement);
			
		} catch (org.w3c.dom.DOMException ex) {
			System.out.println("ERROR: Unable to create report metadata.\n" + ex);
			return false;
		}
		
		return true;

	} // end addReportMetadata method
	
	/**
	 * A method to add a section to the report
	 *
	 * @param id      the section identifier
	 * @param display display this section for mobile devices, desktop browsers or both
	 * @param title   title of this section
	 * @param content the html content of this section
	 *
	 * @return true if, and only if, the section was added successfully
	 */
	private boolean addSection(String id, String display, String title, String content) {
	
		try{
		
			//add the section
			Element sectionElement = xmlDoc.createElement("section");
			sectionElement.setAttribute("id", id);
			sectionElement.setAttribute("display", display);
			
			// add the title
			Element currentElement = xmlDoc.createElement("title");
			currentElement.setTextContent(title);
			sectionElement.appendChild(currentElement);
			
			// add the content
			currentElement = xmlDoc.createElement("content");
			currentElement.appendChild(xmlDoc.createCDATASection(content));
			sectionElement.appendChild(currentElement);
			
			// add the section to the document
			rootElement.appendChild(sectionElement);
			
		} catch (org.w3c.dom.DOMException ex) {
			System.out.println("ERROR: Unable to create report metadata.\n" + ex);
			return false;
		}
		
		return true;

	} // end addSection method
	
	/**
	 * A method to add a section to the report which includes a chart
	 *
	 * @param id      the section identifier
	 * @param display display this section for mobile devices, desktop browsers or both
	 * @param title   title of this section
	 * @param content the html content of this section
	 * @param chartURL the url for the chart
	 * @param chartHeight the height of the chart
	 * @param chartWidth   the width of the chart
	 *
	 * @return true if, and only if, the section was added successfully
	 */
	private boolean addSectionWithChart(String id, String display, String title, String content, String chartUrl, int chartWidth, int chartHeight) {
	
		try{
		
			//add the section
			Element sectionElement = xmlDoc.createElement("section");
			sectionElement.setAttribute("id", id);
			sectionElement.setAttribute("display", display);
			
			// add the title
			Element currentElement = xmlDoc.createElement("title");
			currentElement.setTextContent(title);
			sectionElement.appendChild(currentElement);
			
			// add the content
			currentElement = xmlDoc.createElement("content");
			currentElement.appendChild(xmlDoc.createCDATASection(content));
			sectionElement.appendChild(currentElement);
			
			// add the chart element
			currentElement = xmlDoc.createElement("chart");
			currentElement.setAttribute("height", Integer.toString(chartHeight));
			currentElement.setAttribute("width", Integer.toString(chartWidth));
			currentElement.setAttribute("href", chartUrl);
			sectionElement.appendChild(currentElement);
			
			// add the section to the document
			rootElement.appendChild(sectionElement);
			
		} catch (org.w3c.dom.DOMException ex) {
			System.out.println("ERROR: Unable to create report metadata.\n" + ex);
			return false;
		}
		
		return true;

	} // end addSectionWithChart method
	
	/**
	 * A method to write the report to a file
	 *
	 * @return true if, and only if, the section was added successfully
	 */
	public boolean writeReportFile(File outputFile) {
	
		// check to make sure there is XML to work with
		if(xmlDoc == null) {
			return false;
		}
		
		// transform the XML DOM to a string and write it to a file	
		try{		
			// create a transformer 
			TransformerFactory transFactory = TransformerFactory.newInstance();
			Transformer        transformer  = transFactory.newTransformer();
			
			// set some options on the transformer
			transformer.setOutputProperty(OutputKeys.ENCODING, "utf-8");
			transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			
			// get a transformer and supporting classes
			StringWriter writer = new StringWriter();
			StreamResult result = new StreamResult(writer);
			DOMSource    source = new DOMSource(xmlDoc);
			
			// transform the xml document into a string
			transformer.transform(source, result);
			
			// open the output file
			FileWriter output = new FileWriter(outputFile);
			output.write(writer.toString());
			output.close();
			
		} catch (javax.xml.transform.TransformerException ex) {
			System.out.println("ERROR: Unable to write report.\n" + ex);
			return false;
		} catch (java.io.IOException ex) {
			System.out.println("ERROR: Unable to write report.\n" + ex);
			return false;
		}
		
		return true;

	} // end addSection method
	 
	
	/**
	 * A method to get the current date and time
	 *
	 * @return  a string containing the timestamp
	 */
	public String getCurrentDateAndTime() {
	 
	 	java.util.GregorianCalendar calendar = new java.util.GregorianCalendar();
	 	java.text.DateFormat formatter = java.text.DateFormat.getDateTimeInstance(java.text.DateFormat.FULL, java.text.DateFormat.FULL);
	 	
		return formatter.format(calendar.getTime());
	 
	} // end getCurrentDateAndTime method
	
	/**
	 * A method to get the current month
	 *
	 * @return a string containing the current month
	 */
	public String getCurrentMonth() {
		// get an instance of the calendar
		java.util.GregorianCalendar calendar = new java.util.GregorianCalendar();
		
		// set it to the current time
	 	calendar.setTime(new java.util.Date());
	 	
	 	// get the current month
	 	// zero based index so add 1
	 	return String.format("%02d", (calendar.get(java.util.Calendar.MONTH) + 1));
	 
	} // end getCurrentMonth method
	
	/**
	 * A method to get the current year
	 *
	 * @return a string containing the current month
	 */
	public String getCurrentYear() {
		// get an instance of the calendar
		java.util.GregorianCalendar calendar = new java.util.GregorianCalendar();
		
		// set it to the current time
	 	calendar.setTime(new java.util.Date());
	 	
	 	// get the current month
	 	// zero based index so add 1
	 	return Integer.toString(calendar.get(java.util.Calendar.YEAR));
	 
	} // end getCurrentYear method
	
	/**
	 * A method to get the last day of a month
	 *
	 * @param year  the four digit year
	 * @param month the two digit month
	 *
	 * @return      the last day of the specified month
	 */
	public String getLastDay(String year, String month) {
	
		// get a calendar object
		java.util.GregorianCalendar calendar = new java.util.GregorianCalendar();
		
		// convert the year and month to integers
		int yearInt = Integer.parseInt(year);
		int monthInt = Integer.parseInt(month);
		
		// adjust the month for a zero based index
		monthInt = monthInt - 1;
		
		// set the date of the calendar to the date provided
		calendar.set(yearInt, monthInt, 1);
		
		int dayInt = calendar.getActualMaximum(java.util.GregorianCalendar.DAY_OF_MONTH);
		
		return Integer.toString(dayInt);
	} // end getLastDay method
	
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
	
	/**
	 * A method used to lookup the name of a month based on its number
	 *
	 * @param month the month as a digit
	 *
	 * @return      a string containing the name of the month
	 */
	private String lookupMonth(int month) {
	 	
	 	switch (month) {
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

/**
 * A class used to define constants for Report Generation
 */
class ReportConstants {

	/**
	 * A constant that is used for sections that are to be displayed
	 * on mobile devices only
	 */
	public static final String DISPLAY_MOBILE  = "mobile";
	
	/**
	 * A constant that is used for sections that are to be displayed
	 * in desktop browsers only
	 */
	public static final String DISPLAY_DESKTOP = "desktop";
	
	/**
	 * A constant that is used for sections that are to be displayed
	 * on both mobile devices and desktop browsers
	 */
	public static final String DISPLAY_ALL     = "all";
	
	/**
	 * A constant that is used to build charts for the current month
	 */
	public static final int CURRENT_MONTH = 1;
	
	/**
	 * A constant that is used to build charts for the previous month
	 */
	public static final int PREVIOUS_MONTH = 2;
	

} // end class definition
