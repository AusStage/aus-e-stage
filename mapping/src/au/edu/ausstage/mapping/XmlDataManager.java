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
import javax.xml.xpath.*;
import org.xml.sax.InputSource;

/**
 * A class used to manage access to data stored in XML files
 * such as the data used to populate the infoWindows in the ABS
 * data overlays
 */
public class XmlDataManager {

	// declare private string variables
	private String      xmlDataStorePath;
	private String      xmlFilePath = null;
	private File        xmlFile     = null;
	private InputSource xmlData     = null;

	/**
	 * Constructor for this class
	 *
	 * @param xmlDataStore the root directory of where the XML data files are stored
	 */
	public XmlDataManager(String xmlDataStore) {
	
		xmlDataStorePath = filterString(xmlDataStore);
	
	} // end constructor
	
	/**
	 * A method to validate the path to the XML file
	 *
	 * @param the XML file that is expected to be valid
	 */
	public boolean validatePath(String fileName) {

		// construct the full path
		xmlFilePath = filterString(fileName);
		xmlFilePath = xmlDataStorePath + xmlFilePath;
		
		// check to see if the file is valid
		xmlFile = new File(xmlFilePath);
		
		if(xmlFile.isFile() != true) {
			return false;
		} else if(xmlFile.canRead() == false) {
			return false;
		} else {
			try {
				xmlData = new InputSource(new FileInputStream(xmlFile));
			} catch (java.io.FileNotFoundException ex) {
				xmlData = null;
				return false;
			}
			return true;
		}
	} // end validatePath method
	
	/**
	 * A method to access an ABS data file and return
	 * the HTML to populate an infoWindow
	 *
	 * @param id  the collection district identifier
	 * @return    the HTML for this collection district
	 */
	public String getAbsData(String id) {
	
		// get the district id
		String districtId = filterString(id);
		
		// check on the xmlData object
		if(xmlData == null) {
			return null;
		}
		
		// build the xPath Query 
		String xPathQuery = "/ABSData/district[@id = \"" + id + "\"]/html";
		
		try {
			// create xpath related objects
			XPathFactory factory = XPathFactory.newInstance();
			XPath        xpath = factory.newXPath();
		
			// compile the XPath expression
			XPathExpression xpathExpr = xpath.compile(xPathQuery);

			// Evaluate the XPath expression against the input document
			return (String)xpathExpr.evaluate(xmlData, XPathConstants.STRING);		
		} catch(javax.xml.xpath.XPathExpressionException ex) {
			return null;
		}		
	} // end getAbsData method
	
	
	/*
	 * Common Methods
	 */
	 
	/**
	 * A method to filter a peice of string based input data
	 *
	 * @param value       the value to filter
	 * @param nullAllowed if true a null value is allowed
	 *
	 * @return            the filtered value
	 */
	public String filterString(String value, boolean nullAllowed) {
		
		// check for nulls	
		if(nullAllowed == false && value == null) {
			throw new IllegalArgumentException("Value cannot be null");
		}
		
		// trim the string
		value = value.trim();
		
		// check for nulls again
		if(nullAllowed == false && value.equals("")) {
			throw new IllegalArgumentException("Value cannot be empty");
		}
	
		// return the filtered value
		return value;	
	
	} // end filterString method
	
	/**
	 * A method to filter a peice of string based input data
	 * by default null values are not allowed
	 *
	 * @param value       the value to filter
	 *
	 * @return            the filtered value
	 */
	public String filterString(String value) {
		
		// return the filtered value
		return filterString(value, false);	
	
	} // end filterString method



} // end class definition
