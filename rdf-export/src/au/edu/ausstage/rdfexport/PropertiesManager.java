/*
 * This file is part of the AusStage RDF Export App
 *
 * The AusStage RDF Export App is free software: you can 
 * redistribute it and/or modify it under the terms of the GNU General 
 * Public License as published by the Free Software Foundation, either 
 * version 3 of the License, or (at your option) any later version.
 *
 * The AusStage RDF Export App is distributed in the hope 
 * that it will be useful, but WITHOUT ANY WARRANTY; without even the 
 * implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  
 * See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with the AusStage RDF Export App.  
 * If not, see <http://www.gnu.org/licenses/>.
*/

// import additional packages
import java.io.*;
import java.util.Properties;


/**
 * A class to manage access to the files stored in the 
 * properties file
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
		
		// filter the parameter
		if(path == null) {
			throw new IllegalArgumentException("The path to the properties file cannot be null");
		}
		
		path = path.trim();
		
		if(path.equals("")) {
			throw new IllegalArgumentException("The path to the properties file cannot be blank");
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
	} // end the constructor
	
	/**
	 * A method to get the value of a parameter
	 *
	 * @param key the key for this parameter
	 *
	 * @return    the value of this parameter
	 */
	public String getProperty(String key) {
	
		// filter the parameter
		if(key == null) {
			throw new IllegalArgumentException("The property key can not be null");
		}
		
		key = key.trim();
		
		if(key.equals("")) {
			throw new IllegalArgumentException("The property key can not be blank");
		}
		
		// get the property value
		return props.getProperty(key);
	
	} // end getParameter method
	
} // end class definition
