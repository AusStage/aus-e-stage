/*
 * This file is part of the AusStage Navigating Networks Service
 *
 * The AusStage Navigating Networks Service is free software: you can redistribute
 * it and/or modify it under the terms of the GNU General private License 
 * as published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * The AusStage Navigating Networks Service is distributed in the hope that it will 
 * be useful, but WITHOUT ANY WARRANTY; without even the implied warranty 
 * of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General private License for more details.
 *
 * You should have received a copy of the GNU General private License
 * along with the AusStage Navigating Networks Service.  
 * If not, see <http://www.gnu.org/licenses/>.
*/

package au.edu.ausstage.networks.types;

// import additional libraries
import java.util.Comparator;
import au.edu.ausstage.utils.DateUtils;
import au.edu.ausstage.utils.InputUtils;

/**
 * A class to compare events different criteria
 */
public class EventComparator implements Comparator<Event> {

	// private variables ensure that these are updated
	// when new comparisons are implemented
	private static int MIN_TYPE = 1;
	private static int MAX_TYPE = 2;

	/**
	 * Use a comparison that supports chronological sorting
	 * based on the first date
	 */
	public static int FIRST_DATE_CHRONOLOGICAL = 1;
	
	/**
	 * Use a comparison that supports reverse chronological sorting
	 * based on the first date
	 */
	public static int FIRST_DATE_REVERSE_CHRONOLOGICAL = 2;
	
	// store the selected sort type
	private int sortType = 0;
	
	/**
	 * A constructor for this class; specify the sorting type
	 *
	 * @param sortType the type of sorting to undertake
	 */
	public EventComparator(int sortType) {
	
		if(sortType < MIN_TYPE || sortType > MAX_TYPE) {
			throw new IllegalArgumentException("ERROR: invalid sort type detected");
		}
		
		this.sortType = sortType;
	}
	
	/**
	 * Compare two events for sorting
	 *
	 * @param firstEvent  an event object for comparison
	 * @param secondEvent an event object for comparison
	 *
	 * @return the result of the comparison
	 */
	public int compare(Event firstEvent, Event secondEvent) {
	
		int returnValue = 0;
	
		// determine what type of sort to undertake
		if(sortType == FIRST_DATE_CHRONOLOGICAL) {
			returnValue = firstDateChronological(firstEvent, secondEvent);
		} else if(sortType == FIRST_DATE_REVERSE_CHRONOLOGICAL) {
			returnValue = firstDateReverseChronological(firstEvent, secondEvent);
		}
		
		return returnValue;
	}
	
	/**
	 * A private method to do reverse chronological ordering
	 *
	 * @param firstEvent  the first event in the comparison
	 * @param secondEvent the second event in the comparison
	 *
	 * @return the result of the comparison
	 */
	private int firstDateReverseChronological(Event firstEvent, Event secondEvent) {
	
		// get the dates
		String firstDate  = firstEvent.getFirstDate();
		String secondDate = secondEvent.getFirstDate();
		
		if(InputUtils.isValid(firstDate) == false || InputUtils.isValid(secondDate) == false) {
			throw new IllegalArgumentException("Error: both events in the comparison must have valid firstDates");
		}
		
		// get the integers for the date
		int firstInteger  = DateUtils.getIntegerFromDate(firstDate, "-");
		int secondInteger = DateUtils.getIntegerFromDate(secondDate, "-");
		int compareInt = 0;
		
		// compare the dates
		if(firstInteger < secondInteger) {
			// first date is before second
			compareInt = 1;
		} else if(firstInteger > secondInteger) {
			// first date is after second
			compareInt = -1;
		} else if(firstInteger == secondInteger) {
			// both dates are the same so sort by name
			String firstName = firstEvent.getName();
			String secondName = secondEvent.getName();
			
			compareInt = firstName.compareTo(secondName);
		}
		
		return compareInt;
	}
	
	/**
	 * A private method to do chronological ordering
	 *
	 * @param firstEvent  the first event in the comparison
	 * @param secondEvent the second event in the comparison
	 *
	 * @return the result of the comparison
	 */	
	private int firstDateChronological(Event firstEvent, Event secondEvent) {
		
		// get the dates
		String firstDate  = firstEvent.getFirstDate();
		String secondDate = secondEvent.getFirstDate();
		
		if(InputUtils.isValid(firstDate) == false || InputUtils.isValid(secondDate) == false) {
			throw new IllegalArgumentException("Error: both events in the comparison must have valid firstDates");
		}
		
		// get the integers for the date
		int firstInteger  = DateUtils.getIntegerFromDate(firstDate, "-");
		int secondInteger = DateUtils.getIntegerFromDate(secondDate, "-");
		int compareInt = 0;
		
		// compare the dates
		if(firstInteger < firstInteger) {
			// first date is after second
			compareInt = -1;
		} else if(firstInteger > firstInteger) {
			// first date is before second
			compareInt = 1;
		} else if(firstInteger == firstInteger) {
			// both dates are the same so sort by name
			String firstName = firstEvent.getName();
			String secondName = secondEvent.getName();
			
			compareInt = firstName.compareTo(secondName);
			if(Integer.signum(compareInt) == -1) {
				compareInt = 1;
			} else if(Integer.signum(compareInt) == 1) {
				compareInt =-1;
			} else {
				compareInt = 0;
			}
		}
		
		return compareInt;
	}	
}
