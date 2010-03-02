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
	private final String title = "AusStage Data Exchange Service Analytics";
	private String description;
	private final String analysisID = "exchange-01";

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
		
		/*
		 * get the data for this month
		 */

		/*
		 * debug code bring the month back to feb
		 */
		//int maxDays = Integer.parseInt(this.getLastDay(this.getCurrentYear(), this.getCurrentMonth())); // maximum number of days to search for data
		int maxDays = Integer.parseInt(this.getLastDay(this.getCurrentYear(), "02")); // maximum number of days to search for data
		int currentDay = 1; // the current day
		int maxRequests = 0; // keep track of the maximum number of requests
		String[] data    = new String[maxDays]; // array of data values
		String[] xLabels = new String[maxDays]; // array of x axis labels
		PreparedStatement statement;
		ResultSet results;
		
		// prepare an SQL statement
		try {
			statement = database.prepareStatement("SELECT COUNT(date) FROM requests WHERE date = ?");
		} catch(java.sql.SQLException ex) {
			System.out.println("ERROR: Unable to prepare current month SQL statement\n" + ex);
			return false;
		}
		
		try {
		
			// loop through the days getting the data
			while (currentDay <= maxDays) {
			
				// set the dates
				//statement.setString(1, this.getCurrentYear() + "-" + this.getCurrentMonth() + "-" + String.format("%02d", currentDay);
				statement.setString(1, this.getCurrentYear() + "-" + "02" + "-" + String.format("%02d", currentDay)); // remember to use real month
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
			System.out.println("ERROR: Unable to execute current month SQL\n" + ex);
			return false;
		}
		
		// build the x labels array
		for(int i = 0; i < maxDays; i++) {
			xLabels[i] = String.format("%02d", i + 1);
		}
		
		//buildBarChart(String width, String height, String data, String title, String[] xLabels, String yMax) {
		String chart_url = chart.buildBarChart("750", "125", chart.simpleEncode(data, maxRequests), "Requests by day for February 2010", xLabels, Integer.toString(maxRequests));
		
		System.out.println(chart_url);

		// debug code
		return true;
	
	} // end generate method
	
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
			currentElement.setTextContent(title);
			rootElement.appendChild(currentElement);
			
			// add the description
			currentElement = xmlDoc.createElement("description");
			currentElement.appendChild(xmlDoc.createCDATASection(description));
			rootElement.appendChild(currentElement);
			
			// add the analysis id
			currentElement = xmlDoc.createElement("analysis_id");
			currentElement.setTextContent(analysisID);
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
	 * @return true if, and only if, the section was added successfully
	 */
	private boolean addSectionWithChart() {
	
		try{
			
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

} // end class definition

/**
 * A class used to define constants for Report Generation
 */
class ReportConstants {

	/**
	 * A constant that is used for sections that are to be displayed
	 * on mobile devices only
	 */
	public static final String MOBILE_DISPLAY  = "mobile";
	
	/**
	 * A constant that is used for sections that are to be displayed
	 * in desktop browsers only
	 */
	public static final String DESKTOP_DISPLAY = "desktop";
	
	/**
	 * A constant that is used for sections that are to be displayed
	 * on both mobile devices and desktop browsers
	 */
	public static final String DISPLAY_ALL     = "all";

} // end class definition
