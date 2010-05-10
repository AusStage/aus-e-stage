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
import java.util.*;

/**
 * A class to represent an Event
 */
public class Event extends DataClasses implements Comparable<Event> {

	// declare private variables
	private String id = null;
	private String name = null;
	private String firstDate = null;
	private String firstDisplayDate = null;
	private String url = null;
	
	/**
	 * Constructor for this class
	 *
	 * @param id the unique identifier for this Event
	 */
	public Event(String id) {
	
		try {
			this.id = filterString(id);
			filterInteger(id);
		} catch (IllegalArgumentException ex) {
			throw new IllegalArgumentException("Event ID must not be null or an empty string");
		}
	} // end constructor
	
	/**
	 * Constructor for this class
	 *
	 * @param id               the unique identifier for this contributor
	 * @param name             the name of this contributor
	 * @param firstDate        the firstDate of this event
	 * @param firstDisplayDate the firstDate of this event (used for display)
	 * @param url              the url for this event in AusStage
	 */
	public Event(String id, String name, String firstDate, String firstDisplayDate, String url) {
		
		// check the parameters
		try {
			this.id        = filterString(id);
			this.name      = filterString(name);
			this.firstDate = filterString(firstDate);
			this.firstDisplayDate = filterString(firstDisplayDate);
			this.url       = filterString(url);
		} catch (IllegalArgumentException ex) {
			throw new IllegalArgumentException("All arguments must not be null or empty strings: " + ex.toString());
		}
		
		try {
			filterInteger(id);
		} catch (IllegalArgumentException ex) {
			throw new IllegalArgumentException("ID is expected to be an integer");
		}
		
	} // end constructor
	
	/*
	 * getter and setter methods
	 */
	public String getId() {
		return id;
	}
	
	public void setId(String value) {
		this.id = filterString(value);
		filterInteger(id);
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String value) {
		this.name = filterString(value);
	}
	
	public String getFirstDate() {
		return firstDate;
	}
	
	public void setFirstDate(String value) {
		this.firstDate = filterString(value);
	}
	
	public String getFirstDisplayDate() {
		return firstDisplayDate;
	}
	
	public void setFirstDisplayDate(String value) {
		this.firstDisplayDate = filterString(value);
	}
	
	public String getUrl() {
		return name;
	}
	
	public void setUrl(String value) {
		this.url = filterString(value);
	}
	
	public int getFirstDateAsInt() {
		String date = firstDate.replace("-", "");
		return Integer.valueOf(date);
	}

	/*
	 * methods required for ordering in collections
	 * http://java.sun.com/docs/books/tutorial/collections/interfaces/order.html
	 */

	/**
	 * A method to determine if one event is the same as another
	 *
	 * @param o the object to compare this one to
	 *
	 * @return  true if they are equal, false if they are not
	 */
	public boolean equals(Object o) {
		// check to make sure the object is an event
		if ((o instanceof Event) == false) {
			// o is not an event object
		 	return false;
		}
		
		// compare these two events
		Event e = (Event)o;
		
		return id.equals(e.getId());
		
	} // end equals method
	
	/**
	 * Overide the default hashcode method
	 * 
	 * @return a hashcode for this object
	 */
	public int hashCode() {
		return 31*id.hashCode();
	}
    
    /**
     * The compareTo method compares the receiving object with the specified object and returns a 
     * negative integer, 0, or a positive integer depending on whether the receiving object is 
     * less than, equal to, or greater than the specified object.
     *
     * @param e the event to compare this one to
     *
     * @return a integer indicating comparison result
     */    
	public int compareTo(Event e) {
		int myId   = Integer.getInteger(id);
		int yourId = Integer.getInteger(e.getId());
		
		if(myId == yourId) {
			return 0;
		} else {
			return myId - yourId;
		}
		
	} // end compareTo method

} // end class definition
