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
import au.edu.ausstage.utils.InputUtils;

// import additional java libraries
import org.json.simple.JSONObject; 
 
/**
 * A class to manage the transformation of CSS into the required formats
 *
 * @author corey.wallis@flinders.edu.au
 */
public class TransformManager {

	/**
	 * A method to transform the CSS into the required format
	 *
	 * @param source the source css
	 * 
	 * @return       JSON string object containing the transformed source
	 */
	@SuppressWarnings("unchecked") 
	public static String transform(String source) {
	
	
		// double check the parameters
		if(InputUtils.isValid(source) == false) {
			throw new IllegalArgumentException("The source parameter is required");
		}
		
		// instantiate the TransformCSS class
		TransformCSS transformer = new TransformCSS();
		
		// instantiate the json object
		JSONObject obj = new JSONObject();
		
		// populate the object
		obj.put("foreground", transformer.getForegroundCSS(source));
		obj.put("background", transformer.getBackgroundCSS(source));
		obj.put("kml",        transformer.getKML(source));

		// return the string		
		return obj.toString();	
	}	
}
