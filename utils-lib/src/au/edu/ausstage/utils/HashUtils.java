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

// import additional classes
import org.apache.commons.codec.digest.DigestUtils;

/**
 * A class to provide convenience methods for hashing values.
 * Ensuring that the same hash technique is used consistently
 */
public class HashUtils {

	// declare private class level variables
	private static final int HASH_LENGTH = 64;
	private static final String HASH_REGEX = "[0-9a-f]{64,64}";

	/**
	 * A method to hash a string
	 * 
	 * @param data  the data to hash
	 *
	 * @return      the hashed data
	 */
	public static String hashValue(String data) {
	
		if(InputUtils.isValid(data) == false) {
			throw new IllegalArgumentException("The value to be hashed cannot be null or an empty string");
		}
		
		// return the hashed value
		return DigestUtils.sha256Hex(data);
		
	} // end hashValue method
	
	/**
	 * A method to check if the input looks like a hash
	 *
	 * @param hash the hash to check
	 *
	 * @return     true if, and only if, the hash passes the tests
	 */
	public static boolean isValid(String hash) {
	
		// check the input
		if(InputUtils.isValid(hash) == false) {
			return false;
		}
		
		// check the length of the string
		if(hash.length() == HASH_LENGTH) {
			// double check with the regex
			if(hash.matches(HASH_REGEX) == false) {
				return false;
			}
		} else {
			return false;
		}
	
		// if we get this far, validation has passed
		return true;
	} // end isValid method
	
	/**
	 * A method to check if two hashes are the same
	 *
	 * @param first  the first hash to compare
	 * @param second the second hash to compare
	 *
	 * @return       true, if an only if, the hashes match
	 */
	public static boolean compare(String first, String second) {
	
		// check to see if the hashes are valid
		if(isValid(first) == false) {
			throw new IllegalArgumentException("The first hash to compare is invalid");
		}
		
		if(isValid(second) == false) {
			throw new IllegalArgumentException("The second hash to compare is invalid");
		}
		
		return first.equals(second);
		
	} // end compare method

} // end class definition
