/* This file is part of the AusStage Exchange Analytics app
 *
 * The AusStage Exchange Analytics app is free software: you can redistribute
 * it and/or modify it under the terms of the GNU General Public License 
 * as published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * The AusStage Exchange Analytics app is distributed in the hope that it will 
 * be useful, but WITHOUT ANY WARRANTY; without even the implied warranty 
 * of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with the AusStage Exchange Analytics app.  
 * If not, see <http://www.gnu.org/licenses/>.
 */
 
package au.edu.ausstage.exchangeanalytics;

import au.edu.ausstage.utils.InputUtils;
import au.edu.ausstage.utils.DateUtils;
import au.edu.ausstage.utils.GoogleChartManager;

import java.io.*;
import java.sql.*;
import org.w3c.dom.*;
import javax.xml.parsers.*; 
import javax.xml.transform.*; 
import javax.xml.transform.dom.*; 
import javax.xml.transform.stream.*;

/**
 * generate the XML that represents this report
 */
public class ReportGenerator {

	// private class level variables for XML generation
	private DocumentBuilderFactory factory;
	private	DocumentBuilder        builder;
	private DOMImplementation      domImpl;
	private Document               xmlDoc = null;
	private Element                rootElement = null;
	
	// report metadata
	private final String REPORT_TITLE = "AusStage Data Exchange Service Analytics";
	private final String REPORT_DESCRIPTION = "<p>This report contains information about the usage of the <a href=\"http://beta.ausstage.edu.au/exchange/\" title=\"Direct link to the service\">AusStage Data Exchange Service</a></p>";
	private final String ANALYSIS_ID = "exchange-analytics";

	private final String DESCRIPTION_SECTION = "<p>These analytics show usage of the <a href=\"http://beta.ausstage.edu.au/exchange/\" title=\"Data Exchange Service homepage\">AusStage Data Exchange</a> service. The service allows <a href=\"http://www.ausstage.edu.au\" title=\"AusStage Website homepage\">AusStage</a> data to be retrieved in real time for display in external websites.</p><p><a href=\"http://beta.ausstage.edu.au/?tab=analytics&section=exchange\" title=\"Persistent link to this tab\">Persistent Link</a> to these analytics.</p>";
	private final String CURRENT_MONTH_SECTION = "<p>This chart provides an overview of the number of successful requests to the AusStage Data Exchange service for the current month.</p>";
	private final String PREVIOUS_MONTH_SECTION = "<p>This chart provides an overview of the number of successful requests to the AusStage Data Exchange service for the previous month.</p>";
	private final String CURRENT_YEAR_SECTION = "<p>This chart provides an overview of the number of successful requests to the AusStage Data Exchange service for the current year.</p>";
	private final String PREVIOUS_YEAR_SECTION = "<p>This chart provides an overview of the number of successful requests to the AusStage Data Exchange service for the previous year.</p>";
	
	private final String RECORD_TYPE_SECTION = "<p>This chart provides an overview of the requests by record type for the current year.<br/>Please note percentages may add up to more than 100% due to rounding for display purposes</p>";
	
	private final String EVENT_REQUEST_TYPE_SECTION = "<p>This chart provides an overview of the requests for event data by request type for the current year.<br/>Please note percentages may add up to more than 100% due to rounding for display purposes</p>";
	private final String RESOURCE_REQUEST_TYPE_SECTION = "<p>This chart provides an overview of the requests for resource data by request type for the current year.<br/>Please note percentages may add up to more than 100% due to rounding for display purposes</p>";
	
	private final String EVENT_OUTPUT_TYPE_SECTION = "<p>This chart provides an overview of the requests for event data by output type for the current year.<br/>Please note percentages may add up to more than 100% due to rounding for display purposes</p>";
	private final String RESOURCE_OUTPUT_TYPE_SECTION = "<p>This chart provides an overview of the requests for resource data by output type for the current year.<br/>Please note percentages may add up to more than 100% due to rounding for display purposes</p>";
	private final String FEEDBACK_OUTPUT_TYPE_SECTION = "<p>This chart provides an overview of the requests for feedback data by output type for the current year.<br/>Please note percentages may add up to more than 100% due to rounding for display purposes</p>";
	
	// chart defaults
	private final String BAR_CHART_WIDTH  = "700";
	private final String BAR_CHART_HEIGHT = "125";

	private final String PIE_CHART_WIDTH  = "700";
	private final String PIE_CHART_HEIGHT = "125";
	
	private final String[] REQUEST_TYPES = {"contributor", "organisation", "venue", "secgenre", "contentindicator", "work"};
	private final String[] OUTPUT_TYPES  = {"html", "json", "xml", "rss"};
	private final String[] FEEDBACK_OUTPUT_TYPES = {"html", "json", "xml", "rss", "iframe"};
	
	private DbManager database;
	
	/**
	 * constructor for this class
	 *
	 * @param database the DbManager used to connect to the database
	 */
	public ReportGenerator(DbManager database) throws ParserConfigurationException, DOMException {
	
		if(database == null) {
			throw new IllegalArgumentException("the database parameter must be a valid object");
		}
		
		this.database = database;
		
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
		
		// add the title
		Element elem = xmlDoc.createElement("title");
		elem.setTextContent(REPORT_TITLE);
		rootElement.appendChild(elem);
		
		// add the description
		elem = xmlDoc.createElement("description");
		elem.appendChild(xmlDoc.createCDATASection(REPORT_DESCRIPTION));
		rootElement.appendChild(elem);
		
		// add the analysis id
		elem = xmlDoc.createElement("analysis_id");
		elem.setTextContent(ANALYSIS_ID);
		rootElement.appendChild(elem);
		
		// add the date and time
		elem = xmlDoc.createElement("generated");
		elem.setTextContent(DateUtils.getCurrentDateAndTime());
		rootElement.appendChild(elem);
		
		// add the description element
		addSection("description", "About These Analytics", DESCRIPTION_SECTION);
	}
	
	/**
	 * a method to build the chart
	 */
	public void buildReport() throws java.sql.SQLException, DOMException {
	
		// add the current month chart
		String chartUrl = buildCurrentMonthChart();
		addSectionWithChart("current-month-chart", "Requests for the Current Month", CURRENT_MONTH_SECTION, chartUrl, BAR_CHART_WIDTH, BAR_CHART_HEIGHT); 
		
		// add the previous month chart
		chartUrl = buildPreviousMonthChart();
		addSectionWithChart("previous-month-chart", "Requests for the Previous Month", PREVIOUS_MONTH_SECTION, chartUrl, BAR_CHART_WIDTH, BAR_CHART_HEIGHT); 
		
		// add the current year chart
		chartUrl = buildCurrentYearChart();
		addSectionWithChart("year-chart", "Requests for the Year", CURRENT_YEAR_SECTION, chartUrl, BAR_CHART_WIDTH, BAR_CHART_HEIGHT);
		
		// add the previous year chart 
		chartUrl = buildPreviousYearChart();
		
		if(chartUrl != null) {
			addSectionWithChart("previous-year-chart", "Requests for the Previous Year", CURRENT_YEAR_SECTION, chartUrl, BAR_CHART_WIDTH, BAR_CHART_HEIGHT);
		}
		
		String[] date = DateUtils.getCurrentDateAsArray();
		
		// add the requests by record type
		chartUrl = buildRecordTypeChart(date[0]);
		addSectionWithChart("record-type-chart", "Requests by Record Type", RECORD_TYPE_SECTION, chartUrl, PIE_CHART_WIDTH, PIE_CHART_HEIGHT);
		
		// add the requests by request type
		chartUrl = buildRequestTypeChart(date[0], "event_requests");
		addSectionWithChart("event-request-type-chart", "Requests for Event Data by Request Type", EVENT_REQUEST_TYPE_SECTION, chartUrl, PIE_CHART_WIDTH, PIE_CHART_HEIGHT);
		
		chartUrl = buildRequestTypeChart(date[0], "resource_requests");
		addSectionWithChart("resource-request-type-chart", "Requests for Resource Data by Request Type", RESOURCE_REQUEST_TYPE_SECTION, chartUrl, PIE_CHART_WIDTH, PIE_CHART_HEIGHT);
		
		// add the requests by output type
		chartUrl = buildOutputTypeChart(date[0], "event_requests");
		addSectionWithChart("event-output-type-chart", "Requests for Event Data by Output Type", EVENT_OUTPUT_TYPE_SECTION, chartUrl, PIE_CHART_WIDTH, PIE_CHART_HEIGHT);
		
		chartUrl = buildOutputTypeChart(date[0], "resource_requests");
		addSectionWithChart("resource-output-type-chart", "Requests for Resource Data by Output Type", RESOURCE_OUTPUT_TYPE_SECTION, chartUrl, PIE_CHART_WIDTH, PIE_CHART_HEIGHT);
		
		chartUrl = buildOutputTypeChart(date[0], "feedback_requests");
		addSectionWithChart("feedback-output-type-chart", "Requests for Feedback Data by Output Type", FEEDBACK_OUTPUT_TYPE_SECTION, chartUrl, PIE_CHART_WIDTH, PIE_CHART_HEIGHT);
	}
	
	/* 
	 * a method to build the requests by output type for a specific record type chart
	 */
	private String buildOutputTypeChart(String year, String table) throws java.sql.SQLException {
	
		// declare helper variables
		String[] types;
		int maxRequests = 0;
		
		year = year + "%";
		
		if(table.equals("event_requests") || table.equals("resource_requests")) {
			types = OUTPUT_TYPES;
		} else {
			types = FEEDBACK_OUTPUT_TYPES;
		}
		
		String[] data = new String[types.length];
		
		// database related helper variables
		PreparedStatement statement;
		ResultSet results;
		
		// prepare an SQL statement
		statement = database.prepareStatement("SELECT COUNT(request_date) FROM " + table + " WHERE request_date LIKE ? AND output_type = ?");
		
		// loop through the request types
		for(int i = 0; i < types.length; i++) {

			statement.setString(1, year);
			statement.setString(2, types[i]);
			
			// execute the query
			results = statement.executeQuery();
			
			if(results.next() == true) {
				data[i] = results.getString(1);
				
				if(Integer.parseInt(results.getString(1)) > maxRequests) {
					maxRequests += Integer.parseInt(results.getString(1));
				}
				
			} else {
				data[i] = "0";
			}
		}
		
		// play nice and tidy up
		statement.close();
		
		// calculate the percentages
		for(int i = 0; i < types.length; i++) {
		
			data[i] = Integer.toString(Math.round((Float.valueOf(data[i]) / maxRequests) * 100));
		
		}
		
		String chartTitle = null;
		
		if(table.equals("event_requests")) {
			chartTitle = "Percentage of Requests for Event Data, by Output Type (" + year.substring(0, year.length() -1) + ")";
		} else if(table.equals("resource_requests")) {
			chartTitle = "Percentage of Requests for Resource Data, by Output Type (" + year.substring(0, year.length() -1) + ")";
		} else {
			chartTitle = "Percentage of Requests for Feedback Data, by Output Type (" + year.substring(0, year.length() -1) + ")";
		}
		
		String chartData  = GoogleChartManager.simpleEncode(data, 100);
		
		String[] chartLabels = new String[types.length];
		
		for(int i = 0; i < types.length; i++) {
			chartLabels[i] = types[i] + " - " + data[i] + "%";
		}
		
		return GoogleChartManager.buildPieChart(PIE_CHART_WIDTH, PIE_CHART_HEIGHT, chartData, chartTitle, chartLabels);	

	}
	
	/*
	 * a method to build the requests by request type for a specific record type chart
	 */
	private String buildRequestTypeChart(String year, String table) throws java.sql.SQLException {
	
		// declare helper variables
		String[] data = new String[REQUEST_TYPES.length];
		int maxRequests = 0;
		
		year = year + "%";
		
		// database related helper variables
		PreparedStatement statement;
		ResultSet results;
		
		// prepare an SQL statement
		statement = database.prepareStatement("SELECT COUNT(request_date) FROM " + table + " WHERE request_date LIKE ? AND request_type = ?");
		
		// loop through the request types
		for(int i = 0; i < REQUEST_TYPES.length; i++) {

			statement.setString(1, year);
			statement.setString(2, REQUEST_TYPES[i]);
			
			// execute the query
			results = statement.executeQuery();
			
			if(results.next() == true) {
				data[i] = results.getString(1);
				
				maxRequests += Integer.parseInt(results.getString(1));
				
			} else {
				data[i] = "0";
			}
		}
		
		// play nice and tidy up
		statement.close();
		
		// calculate the percentages
		for(int i = 0; i < REQUEST_TYPES.length; i++) {
		
			data[i] = Integer.toString(Math.round((Float.valueOf(data[i]) / maxRequests) * 100));
		
		}
		
		String chartTitle = null;
		
		if(table.equals("event_requests")) {
			chartTitle = "Percentage of Requests for Event Data, by Request Type (" + year.substring(0, year.length() -1) + ")";
		} else {
			chartTitle = "Percentage of Requests for Resource Data, by Request Type (" + year.substring(0, year.length() -1) + ")";
		}
		
		String chartData  = GoogleChartManager.simpleEncode(data, 100);
		
		String[] chartLabels = new String[REQUEST_TYPES.length];
		
		for(int i = 0; i < REQUEST_TYPES.length; i++) {
			chartLabels[i] = REQUEST_TYPES[i] + " - " + data[i] + "%";
		}
		
		return GoogleChartManager.buildPieChart(PIE_CHART_WIDTH, PIE_CHART_HEIGHT, chartData, chartTitle, chartLabels);	
	
	}
	
	/*
	 * a method to build the requests by record type chart
	 */
	private String buildRecordTypeChart(String year) throws java.sql.SQLException {
	
		// determine number of months to display
		int maxMonths = 12;

		// declare additional helper variables
		int currentMonth   = 1;
		
		String[] eventData    = new String[maxMonths];
		String[] resourceData = new String[maxMonths];
		String[] feedbackData = new String[maxMonths];
		
		int totalEvents    = 0;
		int totalResources = 0;
		int totalFeedback  = 0;
		
		// database related helper variables
		PreparedStatement statement;
		ResultSet results;
		
		// prepare an SQL statement
		statement = database.prepareStatement("SELECT COUNT(request_date) FROM event_requests WHERE request_date LIKE ?");

		// loop through the days getting the data
		while (currentMonth <= maxMonths) {
		
			// build the date for this query
			statement.setString(1, year + "-" + String.format("%02d", currentMonth) + "%");
			
			// execute the query
			results = statement.executeQuery();
			
			if(results.next() == true) {
				eventData[currentMonth -1] = results.getString(1);
				
			} else {
				eventData[currentMonth -1] = "0";
			}

			// increment the currentMonth count
			currentMonth++;
			
			// play nice and close the resultset
			results.close();	
		}
		
		// play nice and tidy up
		statement.close();
		currentMonth = 1;
		
		// prepare an SQL statement
		statement = database.prepareStatement("SELECT COUNT(request_date) FROM resource_requests WHERE request_date LIKE ?");

		// loop through the days getting the data
		while (currentMonth <= maxMonths) {
		
			// build the date for this query
			statement.setString(1, year + "-" + String.format("%02d", currentMonth) + "%");
			
			// execute the query
			results = statement.executeQuery();
			
			if(results.next() == true) {
				resourceData[currentMonth -1] = results.getString(1);
				
			} else {
				resourceData[currentMonth -1] = "0";
			}

			// increment the currentMonth count
			currentMonth++;
			
			// play nice and close the resultset
			results.close();	
		}
		
		// play nice and tidy up
		statement.close();
		currentMonth = 1;
		
		// prepare an SQL statement
		statement = database.prepareStatement("SELECT COUNT(request_date) FROM feedback_requests WHERE request_date LIKE ?");

		// loop through the days getting the data
		while (currentMonth <= maxMonths) {
		
			// build the date for this query
			statement.setString(1, year + "-" + String.format("%02d", currentMonth) + "%");
			
			// execute the query
			results = statement.executeQuery();
			
			if(results.next() == true) {
				feedbackData[currentMonth -1] = results.getString(1);
				
			} else {
				feedbackData[currentMonth -1] = "0";
			}

			// increment the currentMonth count
			currentMonth++;
			
			// play nice and close the resultset
			results.close();	
		}
		
		// play nice and tidy up
		statement.close();
		
		// calculate the totals
		
		for (int i = 0; i < eventData.length; i++) {
			totalEvents += Integer.parseInt(eventData[i]);
		}
		
		for (int i = 0; i< resourceData.length; i++) {
			totalResources += Integer.parseInt(resourceData[i]);
		}
		
		for (int i = 0; i < feedbackData.length; i++) {
			totalFeedback += Integer.parseInt(feedbackData[i]);
		}
		
		int totalRequests = totalEvents + totalResources + totalFeedback;
		
		// calculate the percentages
		totalEvents    = Math.round((new Float(totalEvents) / totalRequests) * 100);
		totalResources = Math.round((new Float(totalResources) / totalRequests) * 100);
		totalFeedback  = Math.round((new Float(totalFeedback) / totalRequests) * 100);
		
		
		String[] totals = new String[3];
		totals[0] = Integer.toString(totalEvents);
		totals[1] = Integer.toString(totalResources);
		totals[2] = Integer.toString(totalFeedback);
		
		String[] xLabels    = {"Events", "Resources", "Feedback"};
		
		for(int i = 0; i < totals.length; i++) {
			xLabels[i] = xLabels[i] + " - " + totals[i] + "%";
		}
		
		String   chartTitle = "Percentage of Requests by Record Type (" + year + ")";
		String   chartData  = GoogleChartManager.simpleEncode(totals, 100);
		
		return GoogleChartManager.buildPieChart(PIE_CHART_WIDTH, PIE_CHART_HEIGHT, chartData, chartTitle, xLabels);	
	}
	
	/*
	 * a method to build a year chart URL
	 */
	private String buildYearChart(String year) throws java.sql.SQLException {
		
		// determine number of months to display
		int maxMonths = 12;

		// declare additional helper variables
		int currentMonth   = 1;
		int maxRequests    = 0;
		
		// build an array of x axis labels
		String[] xLabels = {"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sept", "Oct", "Nov", "Dec"};
		
		String[] eventData    = new String[maxMonths];
		String[] resourceData = new String[maxMonths];
		String[] feedbackData = new String[maxMonths];
		String[] totalData    = new String[maxMonths];
		
		// database related helper variables
		PreparedStatement statement;
		ResultSet results;
		
		// prepare an SQL statement
		statement = database.prepareStatement("SELECT COUNT(request_date) FROM event_requests WHERE request_date LIKE ?");

		// loop through the days getting the data
		while (currentMonth <= maxMonths) {
		
			// build the date for this query
			statement.setString(1, year + "-" + String.format("%02d", currentMonth) + "%");
			
			// execute the query
			results = statement.executeQuery();
			
			if(results.next() == true) {
				eventData[currentMonth -1] = results.getString(1);
				
				if(Integer.parseInt(results.getString(1)) > maxRequests) {
					maxRequests = Integer.parseInt(results.getString(1));
				}
				
			} else {
				eventData[currentMonth -1] = "0";
			}

			// increment the currentMonth count
			currentMonth++;
			
			// play nice and close the resultset
			results.close();	
		}
		
		// play nice and tidy up
		statement.close();
		currentMonth = 1;
		
		// prepare an SQL statement
		statement = database.prepareStatement("SELECT COUNT(request_date) FROM resource_requests WHERE request_date LIKE ?");

		// loop through the days getting the data
		while (currentMonth <= maxMonths) {
		
			// build the date for this query
			statement.setString(1, year + "-" + String.format("%02d", currentMonth) + "%");
			
			// execute the query
			results = statement.executeQuery();
			
			if(results.next() == true) {
				resourceData[currentMonth -1] = results.getString(1);
				
				if(Integer.parseInt(results.getString(1)) > maxRequests) {
					maxRequests = Integer.parseInt(results.getString(1));
				}
				
			} else {
				resourceData[currentMonth -1] = "0";
			}

			// increment the currentMonth count
			currentMonth++;
			
			// play nice and close the resultset
			results.close();	
		}
		
		// play nice and tidy up
		statement.close();
		currentMonth = 1;
		
		// prepare an SQL statement
		statement = database.prepareStatement("SELECT COUNT(request_date) FROM feedback_requests WHERE request_date LIKE ?");

		// loop through the days getting the data
		while (currentMonth <= maxMonths) {
		
			// build the date for this query
			statement.setString(1, year + "-" + String.format("%02d", currentMonth) + "%");
			
			// execute the query
			results = statement.executeQuery();
			
			if(results.next() == true) {
				feedbackData[currentMonth -1] = results.getString(1);
				
				if(Integer.parseInt(results.getString(1)) > maxRequests) {
					maxRequests = Integer.parseInt(results.getString(1));
				}
				
			} else {
				feedbackData[currentMonth -1] = "0";
			}

			// increment the currentMonth count
			currentMonth++;
			
			// play nice and close the resultset
			results.close();	
		}
		
		// play nice and tidy up
		statement.close();
		
		// get the totals
		for (int i = 0; i < totalData.length; i++) {
		
			int a = Integer.parseInt(eventData[i]);
			int b = Integer.parseInt(resourceData[i]);
			int c = Integer.parseInt(feedbackData[i]);
			
			totalData[i] = Integer.toString((a + b + c));
		}
		
		// build the chart title
		String chartTitle = "Requests by Month for " + year;
		String chartData  = GoogleChartManager.simpleEncode(totalData, maxRequests);
		
		return GoogleChartManager.buildBarChart(BAR_CHART_WIDTH, BAR_CHART_HEIGHT, chartData, chartTitle, Integer.toString(maxRequests), xLabels);
	}
	
	/*
	 * a method to build a current year chart URL
	 */
	private String buildCurrentYearChart() throws java.sql.SQLException {
		String[] date = DateUtils.getCurrentDateAsArray();
		return buildYearChart(date[0]);
	}
	
	/*
	 * a method to build a previous year chart URL
	 */
	private String buildPreviousYearChart() throws java.sql.SQLException {
		String[] date = DateUtils.getCurrentDateAsArray();
		
		Integer year  = Integer.parseInt(date[0]);
		
		year = year - 1;
		
		if(year >= 2011) {
			return buildYearChart(year.toString());
		} else {
			return null;
		}		
	}
	
	/*
	 * a method to build a month chart URL
	 */
	private String buildMonthChart(String month, String year) throws java.sql.SQLException {
	
		// determine the first and last days
		int maxDays = Integer.parseInt(DateUtils.getLastDay(year, month));
		
		// declare additional helper variables
		int currentDay   = 1;
		int maxRequests  = 0;
		
		String[] eventData    = new String[maxDays];
		String[] resourceData = new String[maxDays];
		String[] feedbackData = new String[maxDays];
		String[] totalData    = new String[maxDays];
		
		// database related helper variables
		PreparedStatement statement;
		ResultSet results;
		
		// prepare an SQL statement
		statement = database.prepareStatement("SELECT COUNT(request_date) FROM event_requests WHERE request_date = ?");

		// loop through the days getting the data
		while (currentDay <= maxDays) {
		
			// build the date for this query
			statement.setString(1, year + "-" + month + "-" + String.format("%02d", currentDay));
			
			// execute the query
			results = statement.executeQuery();
			
			if(results.next() == true) {
				eventData[currentDay - 1] = results.getString(1);
				
				if(Integer.parseInt(results.getString(1)) > maxRequests) {
					maxRequests = Integer.parseInt(results.getString(1));
				}
				
			} else {
				eventData[currentDay -1] = "0";
			}

			// increment the currentDay count
			currentDay++;
			
			// play nice and close the resultset
			results.close();	
		}
		
		// play nice and tidy up
		statement.close();
		currentDay = 1;
		
		// prepare an SQL statement
		statement = database.prepareStatement("SELECT COUNT(request_date) FROM resource_requests WHERE request_date = ?");

		// loop through the days getting the data
		while (currentDay <= maxDays) {
		
			// build the date for this query
			statement.setString(1, year + "-" + month + "-" + String.format("%02d", currentDay));
			
			// execute the query
			results = statement.executeQuery();
			
			if(results.next() == true) {
				resourceData[currentDay - 1] = results.getString(1);
				
				if(Integer.parseInt(results.getString(1)) > maxRequests) {
					maxRequests = Integer.parseInt(results.getString(1));
				}
				
			} else {
				resourceData[currentDay -1] = "0";
			}

			// increment the currentDay count
			currentDay++;
			
			// play nice and close the resultset
			results.close();	
		}
		
		// play nice and tidy up
		statement.close();
		currentDay = 1;
		
		// prepare an SQL statement
		statement = database.prepareStatement("SELECT COUNT(request_date) FROM feedback_requests WHERE request_date = ?");

		// loop through the days getting the data
		while (currentDay <= maxDays) {
		
			// build the date for this query
			statement.setString(1, year + "-" + month + "-" + String.format("%02d", currentDay));
			
			// execute the query
			results = statement.executeQuery();
			
			if(results.next() == true) {
				feedbackData[currentDay - 1] = results.getString(1);
				
				if(Integer.parseInt(results.getString(1)) > maxRequests) {
					maxRequests = Integer.parseInt(results.getString(1));
				}
				
			} else {
				feedbackData[currentDay -1] = "0";
			}

			// increment the currentDay count
			currentDay++;
			
			// play nice and close the resultset
			results.close();	
		}
		
		// play nice and tidy up
		statement.close();
		
//		// debug code
//		System.out.println(InputUtils.arrayToString(eventData));
//		System.out.println(InputUtils.arrayToString(resourceData));
//		System.out.println(InputUtils.arrayToString(feedbackData));
//		return null;
		
		// get the totals
		for (int i = 0; i < totalData.length; i++) {
		
			int a = Integer.parseInt(eventData[i]);
			int b = Integer.parseInt(resourceData[i]);
			int c = Integer.parseInt(feedbackData[i]);
			
			totalData[i] = Integer.toString((a + b + c));
		}
		
		// build the chart title
		String chartTitle = "Requests by day for " + DateUtils.lookupMonth(month) + " " + year;
		String chartData  = GoogleChartManager.simpleEncode(totalData, maxRequests);
		
		return GoogleChartManager.buildBarChart(BAR_CHART_WIDTH, BAR_CHART_HEIGHT, chartData, chartTitle, Integer.toString(maxRequests));	

	}
	
	/*
	 * a method to build a month chart for the current month
	 */
	private String buildCurrentMonthChart() throws java.sql.SQLException {
		String[] date = DateUtils.getCurrentDateAsArray();
		return buildMonthChart(date[1], date[0]);
	}
	
	/*
	 * a method to build a month for the previous month
	 */
	private String buildPreviousMonthChart() throws java.sql.SQLException {
	
		String[] date = DateUtils.getCurrentDateAsArray();
		
		Integer month = Integer.parseInt(date[1]);
		Integer year  = Integer.parseInt(date[0]);
		
		month = month - 1;
		
		if(month == 0) {
			month = 12;
			year = year - 1;
		}
		
		return buildMonthChart(String.format("%02d", month), String.format("%02d", year));		
	}
	
	/*
	 * a method to add a section to the report
	 */
	private void addSection(String id, String title, String content) throws org.w3c.dom.DOMException {
		
		//add the section
		Element sectionElement = xmlDoc.createElement("section");
		sectionElement.setAttribute("id", id);
		sectionElement.setAttribute("display", "all");
		
		// add the title
		Element elem = xmlDoc.createElement("title");
		elem.setTextContent(title);
		sectionElement.appendChild(elem);
		
		// add the content
		elem = xmlDoc.createElement("content");
		elem.appendChild(xmlDoc.createCDATASection(content));
		sectionElement.appendChild(elem);
		
		// add the section to the document
		rootElement.appendChild(sectionElement);

	} // end addSection method
	
	/**
	 * A method to add a section to the report which includes a chart
	 *
	 * @param id      the section identifier
	 * @param title   title of this section
	 * @param content the html content of this section
	 * @param chartURL the url for the chart
	 * @param chartHeight the height of the chart
	 * @param chartWidth   the width of the chart
	 *
	 * @return true if, and only if, the section was added successfully
	 */
	private void addSectionWithChart(String id, String title, String content, String chartUrl, String chartWidth, String chartHeight) throws DOMException {

		//add the section
		Element sectionElement = xmlDoc.createElement("section");
		sectionElement.setAttribute("id", id);
		sectionElement.setAttribute("display", "all");
		
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
		currentElement.setAttribute("height", chartHeight);
		currentElement.setAttribute("width", chartWidth);
		currentElement.setAttribute("href", chartUrl);
		sectionElement.appendChild(currentElement);
		
		// add the section to the document
		rootElement.appendChild(sectionElement);

	} // end addSectionWithChart method
	
	/**
	 * public method to print the XML of the report to the supplied printWriter
	 *
	 * @param printWriter the printWriter to use for the output
	 *
	 * @throws javax.xml.transform.TransformerException if something bad happens
	 */
	public void save(PrintWriter printWriter) throws javax.xml.transform.TransformerException {
	
		// create a transformer 
		TransformerFactory transFactory = TransformerFactory.newInstance();
		Transformer        transformer  = transFactory.newTransformer();
		
		// set some options on the transformer
		transformer.setOutputProperty(OutputKeys.ENCODING, "utf-8");
		transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
		transformer.setOutputProperty(OutputKeys.INDENT, "yes");
		transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
		
		// get a transformer and supporting classes
		StreamResult result = new StreamResult(printWriter);
		DOMSource    source = new DOMSource(xmlDoc);
		
		// transform the internal objects into XML and print it
		transformer.transform(source, result);
	}

}
