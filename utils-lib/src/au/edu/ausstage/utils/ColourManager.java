/*
 * This file is part of the AusStage Utilities Package
 *
 * The AusStage Utilities Package is free software: you can redistribute
 * it and/or modify it under the terms of the GNU General Public License 
 * as published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * The AusStage Utilities Package is distributed in the hope that it will 
 * be useful, but WITHOUT ANY WARRANTY; without even the implied warranty 
 * of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with the AusStage Utilities Package.  
 * If not, see <http://www.gnu.org/licenses/>.
*/

package au.edu.ausstage.utils;

// include java libraries
import java.util.ArrayList;

// include libraries for XML processing
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

/**
 * A class of methods useful when reading data from a properties file
 */
public class ColourManager {

	// declare class level variables
	String xmlPathName;
	ArrayList<String> colourList = null;

	/**
	 * Constructor for this class
	 *
	 * @param pathName the path to the xml file defining the colours
	 */
	public ColourManager(String pathName) {
	
		// check on the parameter
		if(InputUtils.isValid(pathName) == false) {
			throw new IllegalArgumentException("The pathName parameter is required");
		}
		
		if(FileUtils.doesFileExist(pathName) == false) {
			throw new IllegalArgumentException("The specified file does not exist");
		}
			
		xmlPathName = FileUtils.getCanonicalPath(pathName);
	}
	
	/**
	 * A method to load the colours from the XML file
	 *
	 * @return true, if an only if, the XML file was parsed successfully
	 */
	public boolean parseXML() {
	
		try {
			// declare the SAX related variables
			SAXParserFactory factory = SAXParserFactory.newInstance();
			SAXParser saxParser = factory.newSAXParser();
		
			// declare our own handler
			ColourHandler handler = new ColourHandler();
		
			saxParser.parse(xmlPathName, handler);
			
			// get the list of colours
			colourList = handler.getColourList();
			
		} catch (javax.xml.parsers.ParserConfigurationException ex) {return false;} 
		  catch (org.xml.sax.SAXException ex) {return false;}
		  catch (java.io.IOException ex) {return false;}
	
		// if we get this far everything went OK
		return true;	
	}
	
	/**
	 * A method to return the list of colours
	 *
	 * @return the list of colours retrieved from the XML file
	 */
	public ArrayList<String> getColourList() {
		return colourList;
	}
	
	/**
	 * A method to return the list of colours in a format
	 * that can be included directly into HTML markup
	 *
	 * @return the list of colours with a "#" in front of the value
	 */
	public ArrayList<String> getColourListForWeb() {
	
		// don't try to process an empty list
		if(colourList == null) {
			return null;
		}
	
		// declare helper variables
		String tmp;
	
		// make a copy of the existing list of colours
		ArrayList<String> webColours = new ArrayList<String>();
		
		// prepend hash onto the list of colours
		for(int i = 0; i < colourList.size(); i++) {
			tmp = (String)colourList.get(i);
			tmp = "#" + tmp;
			webColours.add(tmp);
		}
		
		return webColours;	
	}
	
} // end class definition

/**
 * A private class to manage the parsing the XML file
 */

class ColourHandler extends DefaultHandler {

	// declare helper variables
	ArrayList<String> colourList = new ArrayList<String>();

	/**
	 * Override the default startElement method
	 */
	public void startElement(String uri, String localName, String qName, Attributes atts) {
	
		// check to make sure we're processing a tag we expect
		if(localName.equals("colour") == true) {
			if(atts.getValue("hex") != null) {
				colourList.add(atts.getValue("hex"));
			}		
		}
	}
	
	/**
	 * A method to return the list of colours
	 */
	public ArrayList<String> getColourList() {
		return colourList;
	}
}
