/*
 * This file is part of the AusStage Website Analytics app
 *
 * The AusStage Website Analytics app is free software: you can redistribute
 * it and/or modify it under the terms of the GNU General Public License 
 * as published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * The AusStage Website Analytics app is distributed in the hope that it will 
 * be useful, but WITHOUT ANY WARRANTY; without even the implied warranty 
 * of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with the AusStage Website Analytics app.  
 * If not, see <http://www.gnu.org/licenses/>.
*/

package au.edu.ausstage.websiteanalytics;

// import additional ausstage packages
import au.edu.ausstage.utils.*;
import au.edu.ausstage.websiteanalytics.types.*;

// import additional java packages
import java.io.*;
import org.w3c.dom.*;
import javax.xml.parsers.*; 
import javax.xml.transform.*; 
import javax.xml.transform.dom.*; 
import javax.xml.transform.stream.*;

/**
 * A class to generate reports by using the details in a ReportConfig class and 
 * accessing data using the AnalyticsData class
 */
public class ReportGenerator {

	// declare private class level variables
	private AnalyticsData analyticsData   = null;
	private String        lastError       = null;
	private String        outputDirectory = null;
	
	// declare private class level constants
	private String CHART_WIDTH  = "700";
	private String CHART_HEIGHT = "125"; 

	/**
	 * Constructor for this class
	 *
	 * @param analytics a valid AnalyticsData object
	 * @param outputDir the directory to write the report files into
	 */
	public ReportGenerator(AnalyticsData analytics, String outputDir) {
		
		// check on the parameters
		if(analytics == null) {
			throw new IllegalArgumentException("The analytics parameter cannot be null");
		}
		
		if(InputUtils.isValid(outputDir) == false) {
			throw new IllegalArgumentException("The outputDir parameter must be a valid non zero length string");
		}
		
		analyticsData = analytics;
		outputDirectory = outputDir;
	}
	
	/**
	 * generate a report based on the provided ReportConfig object
	 *
	 * @param reportConfig a ReportConfig object describing this report
	 *
	 * @return             true, if an only if, the report is generated successfully
	 */
	public boolean doReport(ReportConfig reportConfig) {
		
		// check on the parameter
		if(reportConfig == null) {
			throw new IllegalArgumentException("The reportConfig parameter cannot be null");
		}
		
		// declare helper variables
		PageViews currentMonth  = null;
		PageViews previousMonth = null;
		PageViews currentYear   = null;
		PageViews previousYear  = null;
		
		// get the current date
		String[] dateFields = DateUtils.getCurrentDateAsArray();
		
		// get the data for the report as required
		if(reportConfig.doCurrentMonthSection() == true) {
			currentMonth = analyticsData.getVisitsForMonthByDayAndPattern(reportConfig.getTableId(), dateFields[0], dateFields[1], reportConfig.getUrlPattern());
			
			// check on what was returned
			if(currentMonth == null) {
				lastError = "Unable to build the current month stats.\n" + analyticsData.getLastError();
				return false;
			}
		}
		
		if(reportConfig.doPreviousMonthSection() == true) {
			// build the previous month date
			int month = Integer.parseInt(dateFields[1]);
			int year  = Integer.parseInt(dateFields[0]);
			
			if(month > 1) {
				month = month - 1;
			} else {
				month = 12;
				year  = year - 1;
			}
			
			previousMonth = analyticsData.getVisitsForMonthByDayAndPattern(reportConfig.getTableId(), Integer.toString(year), String.format("%02d", month), reportConfig.getUrlPattern());
			
			// check on what was returned
			if(previousMonth == null) {
				lastError = "Unable to build the previous month stats.\n" + analyticsData.getLastError();
				return false;
			}
		}
		
		if(reportConfig.doCurrentYearSection() == true) {
			currentYear = analyticsData.getVisitsForYearByMonthAndPattern(reportConfig.getTableId(), dateFields[0], reportConfig.getUrlPattern());
			
			// check on what was returned
			if(currentYear == null) {
				lastError = "Unable to build the current year stats.\n" + analyticsData.getLastError();
				return false;
			}
		}
		
		if(reportConfig.doPreviousYearSection() == true) {
		
			int year  = Integer.parseInt(dateFields[0]);
			year  = year - 1;
			
			previousYear = analyticsData.getVisitsForYearByMonthAndPattern(reportConfig.getTableId(), Integer.toString(year), reportConfig.getUrlPattern());
			
			// check on what was returned
			if(currentYear == null) {
				lastError = "Unable to build the previous year stats.\n" + analyticsData.getLastError();
				return false;
			}
		}
		
		/*
		 * start building the XML of the report
		 */
		
		// declare helper variables
		Document     xmlDoc;
		Element      rootElement;
		Element      element;
		Element      parentElement;
		CDATASection cdata;
		
		// start the DOM
		try {
			// create the xml document builder factory object
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			
			// set the factory to be namespace aware
			factory.setNamespaceAware(true);
			
			// instantiate other supporting objects
			DocumentBuilder        builder = factory.newDocumentBuilder();
			DOMImplementation      domImpl = builder.getDOMImplementation();
			
			// create the xml document
			xmlDoc  = domImpl.createDocument("http://beta.ausstage.edu.au/xmlns/report", "report", null);
			
			// get the root element
			rootElement = xmlDoc.getDocumentElement();
			
		} catch(javax.xml.parsers.ParserConfigurationException ex) {
			lastError = "Unable to instantiate the XML objects\n" + ex.toString();
			return false;
		}
		
		// add the title
		element = xmlDoc.createElement("title");
		element.setTextContent(reportConfig.getReportTitle());
		rootElement.appendChild(element);
		
		// add the description
		element = xmlDoc.createElement("description");
		cdata   = xmlDoc.createCDATASection(reportConfig.getDescription());
		element.appendChild(cdata);
		rootElement.appendChild(element);
		
		// add the date that this was generated
		element = xmlDoc.createElement("generated");
		element.setTextContent(DateUtils.getCurrentDateAndTime());
		rootElement.appendChild(element);
		
		// add the description section
		element = xmlDoc.createElement("section");
		element.setAttribute("id", "description");
		parentElement = element;
		
		element = xmlDoc.createElement("title");
		element.setTextContent("About These Analytics");
		parentElement.appendChild(element);
		
		element = xmlDoc.createElement("content");
		cdata   = xmlDoc.createCDATASection(reportConfig.getDescription());
		element.appendChild(cdata);
		parentElement.appendChild(element);
		rootElement.appendChild(parentElement);
		
		// add the current month chart
		if(reportConfig.doCurrentMonthSection() == true) {
			element = xmlDoc.createElement("section");
			element.setAttribute("id", "current-month-chart");
			parentElement = element;
			
			element = xmlDoc.createElement("title");
			element.setTextContent("Visits for the Current Month");
			parentElement.appendChild(element);
			
			element = xmlDoc.createElement("content");
//			cdata   = xmlDoc.createCDATASection("<p>This chart provides an overview of the number of visits to the Mapping Service for the current month</p>");
//			element.appendChild(cdata);
			parentElement.appendChild(element);
			
			element = xmlDoc.createElement("chart");
			element.setAttribute("height", CHART_HEIGHT);
			element.setAttribute("width", CHART_WIDTH);
			element.setAttribute("href", buildChartUrl(currentMonth, "Visits by Day for " + DateUtils.lookupMonth(dateFields[1]) + " " + dateFields[0], "month"));
			parentElement.appendChild(element);
			
			rootElement.appendChild(parentElement);
		}
		
		if(reportConfig.doPreviousMonthSection() == true) {
			element = xmlDoc.createElement("section");
			element.setAttribute("id", "prev-month-chart");
			parentElement = element;
			
			element = xmlDoc.createElement("title");
			element.setTextContent("Visits for the Previous Month");
			parentElement.appendChild(element);
			
			element = xmlDoc.createElement("content");
//			cdata   = xmlDoc.createCDATASection("<p>This chart provides an overview of the number of visits to the Mapping Service for the previous month</p>");
//			element.appendChild(cdata);
			parentElement.appendChild(element);
			
			element = xmlDoc.createElement("chart");
			element.setAttribute("height", CHART_HEIGHT);
			element.setAttribute("width", CHART_WIDTH);
			
			if((Integer.parseInt(dateFields[1]) - 1) != 0) {
				element.setAttribute("href", buildChartUrl(previousMonth, "Visits by Day for " + DateUtils.lookupMonth(Integer.parseInt(dateFields[1]) - 1) + " " + dateFields[0], "month"));
			} else {
				element.setAttribute("href", buildChartUrl(previousMonth, "Visits by Day for " + DateUtils.lookupMonth(Integer.parseInt("12")) + " " + (Integer.parseInt(dateFields[0]) - 1), "month"));
			}
			
			parentElement.appendChild(element);
			
			rootElement.appendChild(parentElement);
		}
		
		if(reportConfig.doCurrentYearSection() == true) {
			element = xmlDoc.createElement("section");
			element.setAttribute("id", "current-year-chart");
			parentElement = element;
			
			element = xmlDoc.createElement("title");
			element.setTextContent("Visits for the Current Year");
			parentElement.appendChild(element);
			
			element = xmlDoc.createElement("content");
//			cdata   = xmlDoc.createCDATASection("<p>This chart provides an overview of the number of visits to the Mapping Service for the previous month</p>");
//			element.appendChild(cdata);
			parentElement.appendChild(element);
			
			element = xmlDoc.createElement("chart");
			element.setAttribute("height", CHART_HEIGHT);
			element.setAttribute("width", CHART_WIDTH);
			element.setAttribute("href", buildChartUrl(currentYear, "Visits by Month for " + dateFields[0], "year"));
			parentElement.appendChild(element);
			
			rootElement.appendChild(parentElement);
		}
		
		if(reportConfig.doPreviousYearSection() == true) {
			element = xmlDoc.createElement("section");
			element.setAttribute("id", "prev-year-chart");
			parentElement = element;
			
			element = xmlDoc.createElement("title");
			element.setTextContent("Visits for the Previous Year");
			parentElement.appendChild(element);
			
			element = xmlDoc.createElement("content");
//			cdata   = xmlDoc.createCDATASection("<p>This chart provides an overview of the number of visits to the Mapping Service for the previous month</p>");
//			element.appendChild(cdata);
			parentElement.appendChild(element);
			
			element = xmlDoc.createElement("chart");
			element.setAttribute("height", CHART_HEIGHT);
			element.setAttribute("width", CHART_WIDTH);
			element.setAttribute("href", buildChartUrl(previousYear, "Visits by Month for " + (Integer.parseInt(dateFields[0]) - 1), "year"));
			parentElement.appendChild(element);
			
			rootElement.appendChild(parentElement);
		}
			
		// output the new document
		try {
			// create a transformer 
			TransformerFactory transFactory = TransformerFactory.newInstance();
			Transformer        transformer  = transFactory.newTransformer();
			
			// set some options on the transformer
			transformer.setOutputProperty(OutputKeys.ENCODING, "utf-8");
			transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");

			// get a transformer and supporting classes
			StringWriter writer = new StringWriter();
			StreamResult result = new StreamResult(writer);
			DOMSource    source = new DOMSource(xmlDoc);
			
			// transform the xml document into a string
			transformer.transform(source, result);
			
			// open the output file
			FileWriter outputWriter = new FileWriter(outputDirectory + reportConfig.getFileName());
			outputWriter.write(writer.toString());
			outputWriter.close();
			
		} catch(javax.xml.transform.TransformerException e) {
			lastError = "Unable to transform xml for output\n" + e.toString();
			return false;
		}catch (java.io.IOException ex) {
			lastError = "Unable to write xml file\n" + ex.toString();
			return false;
		}
	
		// if we get this far, everything went ok
		return true;		
	}
	
	/**
	 * A method to build the URL for a chart
	 *
	 * @param pageViews a pageViews object containing valid PageView objects
	 * @param title     the title of the chart
	 * @param scale     the scale of this chart
	 *
	 * @return          the URL for the chart
	 */
	private String buildChartUrl(PageViews pageViews, String title, String scale) {
	
		// sort the PageView objects
		java.util.Set<PageView> sortedPageViews = pageViews.getSortedPageViews(PageViews.DATE_SORT);
		PageView[] views = sortedPageViews.toArray(new PageView[0]);
		
		// build a list of values and labels
		String[] values   = new String[views.length];
		String[] labels   = new String[views.length];
		Long     maxViews = new Long("-1");
		
		for(int i = 0; i < views.length; i++) {
			labels[i] = views[i].getDate();			
			values[i] = Long.toString(views[i].getViews());
			
			if(views[i].getViews() > maxViews) {
				maxViews = views[i].getViews();
			}
		}
		
		// process the labels
		if(scale.equals("month") == true) {
			for(int i = 0; i < labels.length; i++) {
				labels[i] = DateUtils.getExplodedDate(labels[i], "-")[2];
			}
		} 
				
		// build the list of chart values
		String chartValues = GoogleChartManager.simpleEncode(values, maxViews.intValue());
		return GoogleChartManager.buildBarChart(CHART_WIDTH, CHART_HEIGHT, chartValues, title, Integer.toString(maxViews.intValue()), labels);
	
	}
	
	/**
	 * A method to get the error message if an error occures
	 *
	 * @return the string of the last error message
	 */
	public String getLastError() {
		return lastError;
	}
}
