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

package au.edu.ausstage.mapping.types;

// import additional libraries
import java.util.Comparator;
import au.edu.ausstage.utils.InputUtils;

/**
 * A class to compare contributors based on their full name
 */
public class ContributorAltNameComparator implements Comparator<Contributor>, java.io.Serializable {

	/**
	 * Compare two events for contributors
	 *
	 * @param firstContributor  a contributor object for comparison
	 * @param secondContributor a contributor object for comparison
	 *
	 * @return the result of the comparison
	 */
	public int compare(Contributor firstContributor, Contributor secondContributor) {
	
		String firstName  = firstContributor.getLastName() + " " + firstContributor.getFirstName();
		String secondName = secondContributor.getLastName() + " " + secondContributor.getFirstName();
		
		if(InputUtils.isValid(firstName) == false || InputUtils.isValid(secondName) == false) {
			firstName  = firstContributor.getName();
			secondName = secondContributor.getName();
		}	
		
		// check for legitimate duplication
		if(firstName.equals(secondName) == true) {
			if(firstContributor.getId().equals(secondContributor.getId()) == true) {
				return firstName.compareTo(secondName);
			} else {
				firstName += firstContributor.getId();
				secondName += secondContributor.getId();
			}
		}
		
		return firstName.compareTo(secondName);
	
	} // end compare method

} // end class definition
