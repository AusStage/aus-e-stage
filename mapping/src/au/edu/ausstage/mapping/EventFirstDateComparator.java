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
import java.util.Comparator;

/**
 * A class to compare events using first dates for reverse chronological sorting
 */
public class EventFirstDateComparator implements Comparator<Event>, java.io.Serializable{

	/**
	 * Compare two events for sorting by date in reverse chronological order
	 *
	 * @param firstEvent  an event object for comparison
	 * @param secondEvent an event object for comparison
	 *
	 * @return the result of the comparison
	 */
	public int compare(Event firstEvent, Event secondEvent) {
	
		// get the dates
		String firstDate  = firstEvent.getFirstDate();
		String secondDate = secondEvent.getFirstDate();
		
		// remove dashes
		firstDate  = firstDate.replace("-", "");
		secondDate = secondDate.replace("-", "");
		
		// double check the dates
		if(firstDate == null || firstDate.equals("")) {
			firstDate = "0";
		}
		
		if(secondDate == null || secondDate.equals("")) {
			secondDate = "0";
		}
		
		// convert to integers
		int first  = Integer.valueOf(firstDate);
		int second = Integer.valueOf(secondDate);
		
		// compare the dates
		if(first < second) {
			// first date is before second
			return 1;
		} else if (first == second) {

			// both dates are the same so sort by name
			String firstName = firstEvent.getName();
			String secondName = secondEvent.getName();
			
			return firstName.compareTo(secondName);
			
		} else {
			// first date is after second
			return -1;
		}	
	} // end compare method

} // end class definition
