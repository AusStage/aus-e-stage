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
 * along with the AAusStage Website Analytics app.  
 * If not, see <http://www.gnu.org/licenses/>.
*/

package au.edu.ausstage.websiteanalytics;

// import additional ausstage packages
import au.edu.ausstage.utils.*;
import au.edu.ausstage.websiteanalytics.types.*;

// import additional java classes
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

/**
 * A class to read config xml files used with the AusStage Website Analytics app
 */
public class ConfigReader {

	// declare class level variables
	private SAXParserFactory factory   = null;
	private SAXParser        saxParser = null;
	private ConfigXMLHandler handler   = null;
	private String           lastError = null;
	
	/**
	 * Constructor for this class
	 */
	public ConfigReader() {
		// declare the SAX related variables
		try {
			factory = SAXParserFactory.newInstance();
			saxParser = factory.newSAXParser();
		}catch (javax.xml.parsers.ParserConfigurationException ex) {
			throw new RuntimeException("Unable to configure the SAX related classes: " + ex.toString());
		} catch (org.xml.sax.SAXException ex) {
			throw new RuntimeException("Unable to configure the SAX related classes: " + ex.toString());
		}
	}
		

	/**
	 * A method to parse a config file
	 *
	 * @param configFilePath the path to the config file
	 */
	public ReportConfig parseConfigFile(String configFilePath) {
	
		// check on the parameters
		if(InputUtils.isValid(configFilePath) == false) {
			throw new IllegalArgumentException("The configFilePath parameter is required");
		}
		
		if(FileUtils.doesFileExist(configFilePath) == false) {
			throw new IllegalArgumentException("The file specified by the configFilePath parameter cannot be found");
		}
		
		// make sure we have the full path to the config file
		configFilePath = FileUtils.getCanonicalPath(configFilePath);
		
		// declare a new ReportConfig variable
		ReportConfig reportConfig = new ReportConfig();
		
		// parse the XML file
		try {
		
			// re declare our own handler
			handler = new ConfigXMLHandler(reportConfig);
		
			saxParser.parse(configFilePath, handler);
			
			
		} catch (org.xml.sax.SAXException ex) {
			lastError = ex.toString();
			return null;
		}catch (java.io.IOException ex) {
			//debug code
			lastError = ex.toString();
			return null;
		}
	
		// if we get this far everything went OK
		return reportConfig;
	}
	
	/**
	 * A method to get the error message if an error occures
	 *
	 * @return the string of the last error message
	 */
	public String getLastError() {
		return lastError;
	}
	
	/**
	 * A class to handle the results of parsing the XML file
	 */
	private class ConfigXMLHandler extends DefaultHandler {
	
		// declare private class level variables
		boolean      isDescription  = false;
		ReportConfig reportConfig   = null;
		
		/**
		 * Constructor for this class
		 *
		 * @param config an empty ReportConfig variable that will be populated with items from the XML
		 */
		public ConfigXMLHandler(ReportConfig config) {
			reportConfig = config;
		}
	
		/**
		 * Override the default startElement method
		 */
		public void startElement(String uri, String localName, String qName, Attributes atts) {
			
			// determine which tag we're starting to parse
			if(qName.equals("config") == true) {
				// get the configuration parameters
				reportConfig.setTableId(atts.getValue("table-id"));
				reportConfig.setUrlPattern(atts.getValue("url-pattern"));
				reportConfig.setReportTitle(atts.getValue("title"));
			} else if(qName.equals("description") == true) {
				// get the title for the description section
				reportConfig.setDescriptionTitle(atts.getValue("title"));
				
				// set the isDescription flag
				isDescription = true;
			} else if(qName.equals("section") == true) {
				// determine which section this represent
				String tmp = atts.getValue("type");
				
				if(tmp.equals("current-month") == true) {
					reportConfig.setCurrentMonthFlag(true);
				} else if(tmp.equals("previous-month") == true) {
					reportConfig.setPreviousMonthFlag(true);
				} else if(tmp.equals("current-year") == true) {
					reportConfig.setCurrentYearFlag(true);
				} else if(tmp.equals("previous-year") == true) {
					reportConfig.setPreviousYearFlag(true);
				}
			}			
		}
		
		/**
		 * Override the default characters method
		 */
		public void characters(char[] ch, int start, int length) {
		
			// process the characters if this is the description tag
			if(isDescription == true) {
				reportConfig.appendCharacters(ch, start, length);
			}
		
		}
		
		/**
		 * Override the default endElement method
		 */
		public void endElement(String uri, String localName, String qName, Attributes atts) {
			
			// determine which tag we're ending on
			if(qName.equals("description") == true) {
				// set the isDescription flag
				isDescription = false;
			}
		}
	}
}
