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

/**
 * A class of methods useful when outputing data in JSON format in response to a JSONP request
 */
public class JSONPManager {

	/**
	 * A convenience method to wrap a JSON string in a callback function name 
	 * used to support JSONP requests.
	 * More information: http://en.wikipedia.org/wiki/JSON#JSONP
	 *
	 * @param json     the json string
	 * @param callback the name of the callback method
	 */
	public static String wrapJSON(String json, String callback) {
	
		// check on the input parameters
		if(InputUtils.isValid(json) == false || InputUtils.isValid(callback) == false) {
			throw new IllegalArgumentException("All parameters to this method are required");
		}
	
		// could do other more interesting things here, but for the moment let's keep it simple	
		return callback + "(" + json + ");";	
	
	} // end the wrapJSON method
	
	/**
	 * A convenience method to wrap a JSON string in a callback function name
	 * used to support JSONP requests.
	 * In this version the wrapped JSON is sent directly to the user via the PrintWriter
	 * More information: http://en.wikipedia.org/wiki/JSON#JSONP
	 *
	 * @param json     the json string
	 * @param callback the name of the callback method
	 * @param writer   the PrintWriter to use to output the JSONP
	 */
	public static void wrapJSON(String json, String callback, java.io.PrintWriter writer) {
	
		// check on the input parameters
		if(InputUtils.isValid(json) == false || InputUtils.isValid(callback) == false || writer == null) {
			throw new IllegalArgumentException("All parameters to this method are required");
		}
	
		// could do other more interesting things here, but for the moment let's keep it simple	
		writer.print(callback + "(" + json + ");");	
	
	} // end the wrapJSON method
	
} // end class definition
