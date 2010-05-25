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

// import additional libraries
import java.util.*;

/**
 * A class to represent a peice of basic data
 */
public class DataClasses {

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
		if(nullAllowed == false) {
			// null values are not allowed
			if(value == null) {
				throw new IllegalArgumentException("Value cannot be null");
			}
		
			// trim the string
			value = value.trim();
		
			// check for nulls again
			if(value.equals("")) {
				throw new IllegalArgumentException("Value cannot be empty");
			}
		} else {
			// null values are allowed
			if(value == null) {
				value = "";
			}
			
			// trim the string
			value = value.trim();
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
	
	/**
	 * A method to merge an arbitary number of arrays
	 *
	 * Source: http://stackoverflow.com/questions/80476/how-to-concatenate-two-arrays-in-java
	 */
	public <T> T[] concatAll(T[] first, T[]... rest) {
		int totalLength = first.length;
			for (T[] array : rest) {
				totalLength += array.length;
			}
			
			T[] result = Arrays.copyOf(first, totalLength);
			int offset = first.length;
			
			for (T[] array : rest) {
				System.arraycopy(array, 0, result, offset, array.length);
				offset += array.length;
			}
		return result;
	}

		

} // end class definition
