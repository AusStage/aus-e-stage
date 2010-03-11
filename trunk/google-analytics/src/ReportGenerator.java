/*
 * This file is part of the AusStage Exchange Analytics application
 *
 * The AusStage Google Analytics Report Generator application is free software: you can 
 * redistribute it and/or modify it under the terms of the GNU General 
 * Public License as published by the Free Software Foundation, either 
 * version 3 of the License, or (at your option) any later version.
 *
 * The AusStage Google Analytics Report Generator application is distributed in the hope 
 * that it will be useful, but WITHOUT ANY WARRANTY; without even the 
 * implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  
 * See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with the AusStage Google Analytics Report Generator application.  
 * If not, see <http://www.gnu.org/licenses/>.
*/

// import additional libraries
import java.io.*;
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
	private AnalyticsManager analytics;
	
	// private class level variables for XML generation
	private DocumentBuilderFactory factory;
	private	DocumentBuilder        builder;
	private DOMImplementation      domImpl;
	private Document               xmlDoc = null;
	private Element                rootElement = null;
	
	// report metadata
	private String reportTitle;
	private String reportDesc;
	private String reportId;
	
	// chart defaults
	private final int BAR_CHART_WIDTH  = 700;
	private final int BAR_CHART_HEIGHT = 125;

	private final int PIE_CHART_WIDTH  = 400;
	private final int PIE_CHART_HEIGHT = 125;

	/**
	 * Constructor for this class
	 *
	 * @param conn a valid connection to the database
	 */
	public ReportGenerator(AnalyticsManager analytics, String title, String description, String id) {
	
		// store these variables for later reuse
		this.analytics = analytics;
		reportTitle    = title;
		reportDesc     = description;
		reportId       = id;
	
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
		
		
		// add the report metadata
		status =  this.addReportMetadata();
		
		// check to see if it is OK to proceed
		if(status == false) {
			// an error has occured
			return false;
		}
		
		// add a section
		status = this.addSection("description", ReportConstants.DISPLAY_ALL, "About These Analytics", "<p>These analytics show the number of visits to the <a href=\"http://beta.ausstage.edu.au/mapping\" title=\"Mapping Service Homepage\">AusStage Mapping Service</a> service for 2010. The service is designed to allow members of the AusStage community to visualise data stored in the <a href=\"http://www.ausstage.edu.au\" title=\"AusStage Homepage\">AusStage</a> system using a variety of different maps.</p>");
		
		// check to see if it is OK to proceed
		if(status == false) {
			// an error has occured
			return false;
		}
		
		// output progress info
		System.out.println("INFO: Gathering data for current month");
		
		// get the chart for the current month
		chartURL = this.buildMonthChart(ReportConstants.CURRENT_MONTH, chart, this.BAR_CHART_WIDTH, this.BAR_CHART_HEIGHT);
		
		// check on what is returned
		if(chartURL == null) {
			System.out.println("An error occured while building a chart, see previous error message for details");
			return false;
		}
		
		// add the section for the current month chart
		status = this.addSectionWithChart("current-month-chart", ReportConstants.DISPLAY_DESKTOP, "Visits for the Current Month", "<p>This chart provides an overview of the number of visits to the Mapping Service for the current month</p>", chartURL, this.BAR_CHART_WIDTH, this.BAR_CHART_HEIGHT); 
		
		// check to see if it is OK to proceed
		if(status == false) {
			// an error has occured
			return false;
		}
		
		// output progress info
		System.out.println("INFO: Gathering data for previous month");
		
		// get the chart for the previous month
		chartURL = this.buildMonthChart(ReportConstants.PREVIOUS_MONTH, chart, this.BAR_CHART_WIDTH, this.BAR_CHART_HEIGHT);
		
		// check on what is returned
		if(chartURL == null) {
			System.out.println("An error occured while building a chart, see previous error message for details");
			return false;
		}
		
		// add the section for the current month chart
		status = this.addSectionWithChart("prev-month-chart", ReportConstants.DISPLAY_DESKTOP, "Requests for the Previous Month", "", chartURL, this.BAR_CHART_WIDTH, this.BAR_CHART_HEIGHT);
		
		// check to see if it is OK to proceed
		if(status == false) {
			// an error has occured
			return false;
		}
		
		// output progress info
		System.out.println("INFO: Gathering data for the year");
		
		// get the chart for the year
		chartURL = this.buildYearChart(this.getCurrentYear(), chart, this.BAR_CHART_WIDTH, this.BAR_CHART_HEIGHT);
		
		// check on what is returned
		if(chartURL == null) {
			System.out.println("An error occured while building a chart, see previous error message for details");
			return false;
		}
		
		// add the section for the current month chart
		status = this.addSectionWithChart("year-chart", ReportConstants.DISPLAY_DESKTOP, "Requests for the Year", "", chartURL, this.BAR_CHART_WIDTH, this.BAR_CHART_HEIGHT);
		
		// check to see if it is OK to proceed
		if(status == false) {
			// an error has occured
			return false;
		}

		// return true to indicate success		
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
		String startDate = null;
		String endDate   = null;
		int monthInt     = 0;
		String year      = null;
		
		// determine the month to be looking at
		if(chartType == ReportConstants.CURRENT_MONTH) {
			
			// determine the month and year
			monthInt = Integer.parseInt(this.getCurrentMonth());
			year     = this.getCurrentYear();
		
			// build the start and end dates
			startDate = this.getCurrentYear() + "-" + this.getCurrentMonth() + "-" + "01";
			endDate   = this.getCurrentYear() + "-" + this.getCurrentMonth() + "-" + getLastDay(this.getCurrentYear(), this.getCurrentMonth());
			
		} else if (chartType == ReportConstants.PREVIOUS_MONTH) {
		
			// determine the month
			monthInt = Integer.parseInt(this.getCurrentMonth()) - 1;
			
			// build the start and end dates
			if(monthInt != 0) {
				startDate = this.getCurrentYear() + "-" + String.format("%02d", monthInt) + "-" + "01";
				endDate   = this.getCurrentYear() + "-" + String.format("%02d", monthInt) + "-" + getLastDay(this.getCurrentYear(), String.format("%02d", monthInt));
				
				year = this.getCurrentYear();
				
			} else {
				monthInt = 12;
				year = Integer.toString(Integer.parseInt(this.getCurrentYear()) - 1);
				
				startDate  = year + "-" + String.format("%02d", monthInt) + "-" + "01";
				endDate    = year + "-" + String.format("%02d", monthInt) + "-" + getLastDay(year, String.format("%02d", monthInt));
			}
		}		

		// declare additional helper variables
		int maxVisits  = 0; // keep track of the maximum number of requests
		String[] data    = analytics.getVisitsForMonth(startDate, endDate);
		
		// determine the max visits
		if(data != null) {
			for(int i = 0; i < data.length; i++) {
				if(Integer.parseInt(data[i]) > maxVisits) {
					maxVisits = Integer.parseInt(data[i]);
				}
			}
		} else {
			System.out.println("ERROR: Unable to build chart, no data");
			return null;
		}
				
		// build the chart title
		String chartTitle = "Visits by day for " + this.lookupMonth(monthInt) + " " + year;
		
		return chart.buildBarChart(Integer.toString(chartWidth), Integer.toString(chartHeight), chart.simpleEncode(data, maxVisits), chartTitle, Integer.toString(maxVisits));	
	
	} // end buildMonthChart method
	
	/**
	 * A method to build a chart for a years worth of data
	 * 
	 * @param year        the year to build the chart for
	 * @param chart       a valid ChartManager object used to build this chart
	 * @param chartHeight the height of the chart
	 * @param chartWidth  the width of the chart
	 *
	 * @return          the URL for this chart
	 */
	private String buildYearChart(String year, ChartManager chart, int chartWidth, int chartHeight) {

		// declare additional helper variables
		int currentMonth   = 1; // the current day
		int maxVisits      = 0; // keep track of the maximum number of requests
		
		// get the data
		String[] data    = analytics.getVisitsForYear(year);
		
		// determine the max visits
		if(data != null) {
			for(int i = 0; i < data.length; i++) {
				if(Integer.parseInt(data[i]) > maxVisits) {
					maxVisits = Integer.parseInt(data[i]);
				}
			}
		} else {
			System.out.println("ERROR: Unable to build chart, no data");
			return null;
		}
		
		// build an array of x axis labels
		String[] xLabels = {"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sept", "Oct", "Nov", "Dec"};
		
		// build the chart title
		String chartTitle = "Requests by Month for " + year;
		
		return chart.buildBarChart(Integer.toString(chartWidth), Integer.toString(chartHeight), chart.simpleEncode(data, maxVisits), chartTitle, Integer.toString(maxVisits), xLabels);	
	
	} // end buildYearChart method
	
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
			currentElement.setTextContent(this.reportTitle);
			rootElement.appendChild(currentElement);
			
			// add the description
			currentElement = xmlDoc.createElement("description");
			currentElement.appendChild(xmlDoc.createCDATASection(this.reportDesc));
			rootElement.appendChild(currentElement);
			
			// add the analysis id
			currentElement = xmlDoc.createElement("analysis_id");
			currentElement.setTextContent(this.reportId);
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
