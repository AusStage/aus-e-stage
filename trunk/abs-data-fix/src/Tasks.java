/*
 * This file is part of the AusStage ABS Data Fix App
 *
 * The AusStage ABS Data Fix App is free software: you can 
 * redistribute it and/or modify it under the terms of the GNU General 
 * Public License as published by the Free Software Foundation, either 
 * version 3 of the License, or (at your option) any later version.
 *
 * The AusStage ABS Data Fix App is distributed in the hope 
 * that it will be useful, but WITHOUT ANY WARRANTY; without even the 
 * implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  
 * See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with the AusStage ABS Data Fix App.  
 * If not, see <http://www.gnu.org/licenses/>.
*/

import java.io.*;

/**
 * An abstract class used to store common methods & provide the basis for an API
 * for all of the different task classes
 */
public abstract class Tasks {

	// declare private variables
	File input;
	File output;
	
	// declare public constants
	public static final String ROOT_SNIPPET_ELEMENT_NAME = "ABSDataSnippet";
	public static final String DISTRICT_ELEMENT_NAME     = "district";
	public static final String ROOT_ELEMENT_NAME         = "ABSData";

	/**
	 * Constructor for this class
	 *
	 * @param input  the input file
	 * @param output the output file
	 */
	public Tasks(File input, File output) {
	
		// check on the parameters
		if(input == null || output == null) {
			throw new IllegalArgumentException("ERROR: This constructor requires valid file object as parameters");
		} else {
			this.input  = input;
			this.output = output;
		}
		
	} // end constructor
	
	/**
	 * A method used to undertake the required task
	 *
	 * @return true if, and only if, the task completes successfully
	 */
	public abstract boolean doTask();
	
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
	
	/**
	 * A method to filter a peice of string based input data
	 * and ensure it represents an integer
	 *
	 * @param value the value to filter
	 */
	public void filterInteger(String value) {
	
		try {
			Integer.getInteger(value);
		} catch(NumberFormatException ex) {
			throw new IllegalArgumentException("Value must represent an integer");
		}
	}

} // end class definition
