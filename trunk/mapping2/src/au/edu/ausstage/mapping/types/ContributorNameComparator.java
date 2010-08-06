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

/**
 * A class to compare events using first dates for reverse chronological sorting
 */
public class ContributorNameComparator implements Comparator<Contributor>, java.io.Serializable {

	/**
	 * Compare two events for contributors
	 *
	 * @param firstContributor  a contributor object for comparison
	 * @param secondContributor a contributor object for comparison
	 *
	 * @return the result of the comparison
	 */
	public int compare(Contributor firstContributor, Contributor secondContributor) {
	
		// get the dates
		String firstName  = firstContributor.getName();
		String secondName = secondContributor.getName();
		
		return firstName.compareTo(secondName);
	
	} // end compare method

} // end class definition
