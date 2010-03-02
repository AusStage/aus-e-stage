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
	 * @return true if, and only if, the section was added successfully
	 */
	private boolean addSection() {
	
		try{
			
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
	 * A method to get the current date and time to use in a timestamp
	 *
	 * @return  a string containing the timestamp
	 */
	public String getCurrentDateAndTime() {
	 
	 	java.util.GregorianCalendar calendar = new java.util.GregorianCalendar();
	 	java.text.DateFormat formatter = java.text.DateFormat.getDateTimeInstance(java.text.DateFormat.FULL, java.text.DateFormat.FULL);
	 	
		return formatter.format(calendar.getTime());
	 
	} // end getCurrentDateAndTime method

} // end class definition
