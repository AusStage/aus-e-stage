/*
 * This file is part of the AusStage Terminator Service
 *
 * The AusStage Terminator Service is free software: you can 
 * redistribute it and/or modify it under the terms of the GNU General 
 * Public License as published by the Free Software Foundation, either 
 * version 3 of the License, or (at your option) any later version.
 *
 * The AusStage Terminator Service is distributed in the hope 
 * that it will be useful, but WITHOUT ANY WARRANTY; without even the 
 * implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  
 * See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with the AusStage Terminator Service.  
 * If not, see <http://www.gnu.org/licenses/>.
*/ 

package au.edu.ausstage.terminator;

// import additional classes
import org.apache.commons.codec.digest.DigestUtils;

/**
 * A class to manage all aspects of authentication and authorisation
 */
public class AuthenticationManager {

	/**
	 * A method to check an authentication token
	 *
	 * @param authToken     the authentication token from the user
	 * @param securityToken the hashed security token
	 *
	 * @return              true, if an only if the hashed authentication token matches the security token
	 */
	public boolean checkAuthToken(String authToken, String securityToken) {
	
		// check the parameters
		if(authToken == null || securityToken == null) {
			throw new RuntimeException("Missing reqiured parameters");
		}
		
		// calculate a hash of the authToken
		String authHash = doHash(authToken);
		
		if(authHash.equals(securityToken)) {
			return true;
		} else {
			return false;
		}
	
	} // end checkAuthToken method
	
	/**
	 * A method to generate a hash and return a base64 encoded string
	 *
	 * @param toHash the string to hash
	 * 
	 * @return the base64 encoded string of the hash
	 */
	public String doHash(String toHash) {
	
		// check the parameters
		if(toHash == null) {
			throw new RuntimeException("Missing required parameter");
		}
		
		return DigestUtils.shaHex(toHash);
		
	} // end doHash

} // end class definition
