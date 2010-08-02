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
 * A class of methods useful when validating input to methods
 */
public class InputUtils {

		/**
	 * check to ensure a parameter value is valid
	 *
	 * @param value the parameter value
	 *
	 * @return      true if, and only if, the parameter is valid
	 */
	public static boolean isValid(String value) {
	
		// check on the parameter value
		if(value == null) {
			return false;
		} else {
			return true;
		}
	} // end the isValid method
	
	/**
	 * check to ensure a parameter is valid and is from a list
	 *
	 * @param value the parameter value
	 * @param list  the list of allowed values
	 *
	 * @return      true if, and only if, the parameter is valid
	 */
	public static boolean isValid(String value, String[] list) {
	
		// check if it is not null first
		if(isValid(value) == false) {
			return false;
		}
		
		// check the value against the list
		boolean isValid = false;
		
		for(int i = 0; i < list.length; i++) {
			if(list[i].equals(value)) {
				isValid = true;
			}
		}
		
		return isValid;	
	} // end the isValid method with list
	
	/**
	 * check to ensure a parameter value is valid
	 *
	 * @param value the parameter value
	 *
	 * @return      true if, and only if, the parameter is valid
	 */
	public static boolean isValidInt(String value) {
	
		if(isValid(value) == false) {
			return false;
		}
	
		// can we parse the value as a int?
		try {
			Integer.parseInt(value);
		} catch (java.lang.NumberFormatException ex) {
			// nope
			return false;
		}
		
		// if we get this far everything is OK
		return true;
	
	} // end the isValid method
	
	/**
	 * check to ensure a parameter value is valid
	 *
	 * @param value   the parameter value
	 * @param minimum the minimum allowed value
	 *
	 * @return      true if, and only if, the parameter is valid
	 */
	public static boolean isValidInt(int value, int minimum) {
		
		if(value >= minimum) {
			return false;
		} else {
			return true;
		}
			
	} // end the isValid method
	
	/**
	 * check to ensure a parameter value is valid
	 *
	 * @param value    the parameter value
	 * @param minimum  the minimum allowed value
	 * @param maximum the maximum allowed value
	 *
	 * @return      true if, and only if, the parameter is valid
	 */
	public static boolean isValidInt(int value, int minimum, int maximum) {
		
		if(value >= minimum && value <= maximum) {
			return true;
		} else {
			return false;
		}			
	} // end the isValid method
	
	


} // end class definition
