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

// import additional packages
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * A class of methods useful when reading data from a properties file
 */
public class PropertiesManager {

	// declare class level variables
	private Properties props = new Properties();
	
	/**
	 * Load the properties from the file
	 *
	 * @param path the path to the properties file
	 *
	 * @return     true if, and only if, the file loaded successfully
	 */
	public boolean loadFile(String path) {
	
		if(InputUtils.isValid(path) == false) {
			throw new IllegalArgumentException("The path to the properties file cannot be null or an empty string");
		}		
		
		// load the data from the file
		try {
			props.load(new FileInputStream(path));
		} catch (IOException ex) {
			System.err.println("ERROR: Unable to load the properties file");
			System.err.println("       " + ex.getMessage());
			return false;
		}
		
		// if we get this far everything is ok
		return true;
	} // end the loadFile method
	
	/**
	 * A method to get the value of a parameter
	 *
	 * @param key the key for this parameter
	 *
	 * @return    the value of this parameter
	 */
	public String getProperty(String key) {
	
		if(InputUtils.isValid(key) == true) {
		
			// get the property value
			return props.getProperty(key);
		}
		
		// return null to indicate failure
		return null;
	
	} // end getProperty method
	
	/**
	 * A method to get the value of a parameter
	 * A method to get the value of a parameter
	 *
	 * @param key the key for this parameter
	 *
	 * @return    the value of this parameter
	 */
	public String getValue(String key) {
	
		if(InputUtils.isValid(key) == true) {
		
			// get the property value
			return props.getProperty(key);
		}
		
		// return null to indicate failure
		return null;
	
	} // end getValue method
	 
	 
	
} // end class definition
