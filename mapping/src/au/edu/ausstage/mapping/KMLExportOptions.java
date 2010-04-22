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

// import libraries
import java.util.*;

/**
 * A class used to manage options related to the export of KML Data
 */
public class KMLExportOptions {

	// define private instance variables
	Map<String, String> optionsMap = new HashMap<String, String>();

	/**
	 * Constructor for this class, by default all options are set to false
	 *
	 */
	public KMLExportOptions() {
		// set the default options
		this.optionsMap.put("includeTimeSpanElements", "no");
		this.optionsMap.put("includeTrajectoryInfo", "no");
		this.optionsMap.put("includeElevationInfo", "no");
		this.optionsMap.put("includeGroupedEventInfo", "no");
	}
	
	/**
	 * Method to set one of the options
	 *
	 * @param optionName  the name of the option to set
	 * @param optionValue the value of the option
	 */
	public void setOption(String optionName, String optionValue) throws IllegalArgumentException {
	
		// determine if this is a legal option name
		if(this.optionsMap.containsKey(optionName) == false) {
			throw new IllegalArgumentException("Unknown option name");
		} else {
			this.optionsMap.put(optionName, optionValue);
		}
				
	} // end setOption method
	
	/**
	 * Method to get one of the options
	 *
	 * @param optionName the name of the option to get
	 *
	 * @return the value of the option
	 */
	public String getOption(String optionName) throws IllegalArgumentException {
	
		// determine if this is a legal option name
		if(this.optionsMap.containsKey(optionName) == false) {
			throw new IllegalArgumentException("Unknown option name");
		} else {
			return this.optionsMap.get(optionName).toLowerCase();
		}
			
	} // end getOption method
	
	/**
	 * Method to list all of the allowed option names
	 *
	 * @return an array of strings listing the allowed option names
	 */
	public String[] getOptionNames() {
	
		// declare helper variables
		String[] names = new String[optionsMap.size()];
		int count = 0;
		
		// get the option names
		Set optionNames = this.optionsMap.keySet();
		
		for (Object name : optionNames) {
		
			names[count] = name.toString();
			count++;
		}
		
		return names;
	} // end getOptionNames method

} // end class definition
