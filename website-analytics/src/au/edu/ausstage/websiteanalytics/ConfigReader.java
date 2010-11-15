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

	/**
	 * A method to parse a config file
	 *
	 * @param configFilePath the path to the config file
	 */
	public void parseConfigFile(String configFilePath) {
	
		// check on the parameters
		if(InputUtils.isValid(configFilePath) == false) {
			throw new IllegalArgumentException("The configFilePath parameter is required");
		}
		
		if(FileUtils.doesFileExist(configFilePath) == false) {
			throw new IllegalArgumentException("The file specified by the configFilePath parameter cannot be found");
		}	
	}
	
	/**
	 * A class to handle the results of parsing the XML file
	 */
	private class ConfigXMLHandler extends DefaultHandler {
	
		/**
		 * Override the default startElement method
		 */
		public void startElement(String uri, String localName, String qName, Attributes atts) {
	
			
		}
		
		/**
		 * Override the default characters method
		 */
		public void characters(char[] ch, int start, int length) {
		
		}
	}
}
