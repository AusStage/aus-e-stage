/*
 * This file is part of the Aus-e-Stage Beta Website
 *
 * The Aus-e-Stage Beta Website is free software: you can redistribute
 * it and/or modify it under the terms of the GNU General Public License 
 * as published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * The Aus-e-Stage Beta Website is distributed in the hope that it will 
 * be useful, but WITHOUT ANY WARRANTY; without even the implied warranty 
 * of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with the AusStage Mapping Service.  
 * If not, see <http://www.gnu.org/licenses/>.
 */
package au.edu.ausstage.beta;

// import the ausstage utilities
import au.edu.ausstage.utils.DateUtils;
import au.edu.ausstage.utils.DbManager;
import au.edu.ausstage.utils.DbObjects;
import au.edu.ausstage.utils.FileUtils;
import au.edu.ausstage.utils.InputUtils;

// import additional java libraries
import java.io.*;
import javax.xml.transform.*;
import javax.xml.transform.stream.*;
import java.util.ArrayList;
 
 
/**
 * A class to manage the manipulation of the XML report into HTML using XSL
 *
 * @author corey.wallis@flinders.edu.au
 */
public class AnalyticsManager {

	/**
	 * AusStage tables used to provide record count analytics
	 */
	public static final String[] AUSSTAGE_TABLES = {"events", "venue", "contributor", "organisation", "item", "work"};
	
	/**
	 * A method to take convert the XML representation of the report into HTML using XSL
	 *
	 * @param reportDirectory  the directory where the report files live
	 * @param reportXSLFile    the name of the report XSL file, assumed to be in the reportDirectory
	 * @param reportXMLFile    the name of the report XML file, assumed to be in the reportDirectory
	 *
	 * @return                 the result of the transformation as a HTML encoded string
	 */
	public static String processXMLReport(String reportDirectory, String reportXSLFile, String reportXMLFile) {
	
		// double check the parameters
		if(InputUtils.isValid(reportDirectory) == false || InputUtils.isValid(reportXSLFile) == false || InputUtils.isValid(reportXMLFile) == false) {
			throw new IllegalArgumentException("All of the parameters are required");
		}
		
		if(FileUtils.doesDirExist(reportDirectory) == false) {
			throw new IllegalArgumentException("The specified reportDirectory path is invalid");
		}
		
		if(FileUtils.doesFileExist(reportDirectory + reportXSLFile) == false) {
			throw new IllegalArgumentException("The specified XSL file path is invalid");
		}
		
		if(FileUtils.doesFileExist(reportDirectory + reportXMLFile) == false) {
			throw new IllegalArgumentException("The specified XML file path is invalid");
		}
		
		// declare XML & XSLT related variables
		TransformerFactory factory;     // transformer factory
		Transformer        transformer; // transformer object to do the transforming
		Source 			   xslSource;   // object to hold the XSL
		Source 			   xmlSource;   // object to hold the XML
		StringWriter       writer;		// object to receive output of transformation
		StreamResult       result;		// object to receive output of transformation
		String             htmlOutput;  // object to hold the HTML output
		
		// open the XSL and XML files
		File xslFile = new File(reportDirectory + reportXSLFile);
		File xmlFile = new File(reportDirectory + reportXMLFile);
		
		// set up the source objects for the transform
		try {
			// get a transformer factory
			factory = TransformerFactory.newInstance();
			
			// load the XSLT source
			xslSource = new StreamSource(xslFile);
			
			// load the XML
			xmlSource = new StreamSource(xmlFile);
			
			// get a transformer using the XSL as a source of instructions
			transformer = factory.newTransformer(xslSource);
			
			// get objects to handle the output of the transformation
			writer = new StringWriter();
			result = new StreamResult(writer);
			
			// do the transformation
			transformer.transform(xmlSource, result);
			
			// get the results
			htmlOutput = writer.toString();
			
		} catch (javax.xml.transform.TransformerException ex) {
			throw new RuntimeException("The transformation of the XML using XSL failed");
		}
		
		// hack to fix wrong entities in the html output
		htmlOutput = htmlOutput.replace("&lt;", "<");
		htmlOutput = htmlOutput.replace("&gt;", ">");
		
		return htmlOutput;
	}
	
	/**
	 * A method to generate the record count analytics
	 *
	 * @param database an open connection to the database
	 *
	 * @return         the HTML that can be added to the page
	 */
	public static String getRecordCountAnalytics(DbManager database) {
	
		// check on the parameters
		if(database == null) {
			throw new IllegalArgumentException("the database parameter cannot be null");
		}
		
		String count = null;
		String name  = null;
		
		// start the html output
		StringBuilder htmlOutput = new StringBuilder();
		htmlOutput.append("<div class=\"report\" id=\"\">");
		htmlOutput.append("<h1>AusStage Database Record Counts</h1>");
		htmlOutput.append("<div id=\"generated\"><p>" + DateUtils.getCurrentDateAndTime() + "</p><div>");
		
		// add the description
		htmlOutput.append("<div class=\"report_section\" id=\"description\"><h2>About These Analytics</h2>");
		htmlOutput.append("<div class=\"report_section_content\"><p>These analytics show the number of records in a number of strategic table within the AusStage database at the time displayed above</p></div></div>");
		
		// start the output table
		htmlOutput.append("<div class=\"report_section\" id=\"record-count-table\"><h2>Number of Records in these Tables</h2>");
		htmlOutput.append("<table><thead><tr><th>Table Name</th><th class=\"numeric\">Record Count</th></tr></thead><tbody>");
		
		for(int i = 0; i < AUSSTAGE_TABLES.length; i++) {
		
			count = getRecordCount(database, AUSSTAGE_TABLES[i]);
			
			name = AUSSTAGE_TABLES[i];
			name = name.substring(0,1).toUpperCase() + name.substring(1);
			
			if(i % 2 == 1) {
				htmlOutput.append("<tr class=\"odd\">");
			} else {
				htmlOutput.append("<tr>");
			}
			
			htmlOutput.append("<td>" + name + "</td><td class=\"numeric\">" + count.format("%,d", new Integer(count))+ "</td></tr>");			
		}
		
		
		// finish the table
		htmlOutput.append("</tbody></table>");
		
		return htmlOutput.toString();
	
	}
	
	// private method to get the record count
	private static String getRecordCount(DbManager database, String table) {
	
		// declare sql variables
		String sql = "select count(*) from " + table;
		
		// get the count
		DbObjects results = database.executeStatement(sql);
		
		// check to see that data was returned
		if(results == null) {
			return null;
		}
		
		// get the count
		String count = results.getColumn(1).get(0);
		
		// play nice and tidy up
		results.tidyUp();
		results = null;	
		
		// return the count
		return count;	
	}
}
