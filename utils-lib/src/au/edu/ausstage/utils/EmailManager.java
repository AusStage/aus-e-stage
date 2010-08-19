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
 * A class used to send email
 */
public class EmailManager {

	// declare private variables
	private EmailOptions options = null;
	
	/**
	 * Constructor for this class
	 *
	 * @param options a valid EmailOptions object
	 */
	public EmailManager(EmailOptions options) {
	
		if(options != null) {
			this.options = options;
		} else {
			throw new IllegalArgumentException("The EmailOptions object cannot be null");
		}
	
	} // end constructor


} // end class definition
