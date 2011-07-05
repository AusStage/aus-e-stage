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
import au.edu.ausstage.utils.DateUtils;
import java.util.Comparator;

/**
 * A class to compare events using first dates for reverse chronological sorting
 */
public class KmlEventDateComparator implements Comparator<KmlEvent> {

	/**
	 * Compare two events for sorting by date in reverse chronological order
	 *
	 * @param firstEvent  an event object for comparison
	 * @param secondEvent an event object for comparison
	 *
	 * @return the result of the comparison
	 */
	public int compare(KmlEvent firstEvent, KmlEvent secondEvent) {
	
		int firstDate  = DateUtils.getIntegerFromDate(firstEvent.getSortFirstDate());
		int secondDate = DateUtils.getIntegerFromDate(secondEvent.getSortFirstDate());
		
		Integer first  = new Integer(firstDate);
		Integer second = new Integer(secondDate);
		
		if(first == second) {
			first  = Integer.parseInt(firstDate  + "" + firstEvent.getId());
			second = Integer.parseInt(secondDate + "" + secondEvent.getId());
		}
		
		return first.compareTo(second);

	} // end compare method

} // end class definition
