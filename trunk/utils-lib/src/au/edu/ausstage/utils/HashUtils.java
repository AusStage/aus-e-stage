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



} // end class definition
