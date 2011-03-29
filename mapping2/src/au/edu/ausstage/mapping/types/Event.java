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

// import additional AusStage libraries
import au.edu.ausstage.utils.InputUtils;

/**
 * A class to represent an Event
 */
public class Event implements Comparable<Event> {

	// declare private variables
	private String id = null;
	private String name = null;
	private String firstDate = null;
	private String firstDisplayDate = null;
	private String url = null;
	private String venueId = null;
	private String venue = null;
	private String latitude = null;
	private String longitude = null;
	private String sortFirstDate = null;
	private String sortLastDate = null;
	
	/**
	 * Constructor for this class
	 *
	 * @param id the unique identifier for this Event
	 */
	public Event(String id) {
	
		// check on the parameter
		if(InputUtils.isValidInt(id) == false) {
			throw new IllegalArgumentException("The id parameter must be a valid integer");
		}
		
		this.id = id;
		
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
	
		// check on the parameter
		if(InputUtils.isValidInt(id) == false) {
			throw new IllegalArgumentException("The id parameter must be a valid integer");
		}
		
		if(InputUtils.isValid(name) == false || InputUtils.isValid(firstDate) == false || InputUtils.isValid(firstDisplayDate) == false || InputUtils.isValid(url) == false) {
			throw new IllegalArgumentException("All parameters of this method are required.");
		}
		
		this.id               = id;
		this.name             = name;
		this.firstDate        = firstDate;
		this.firstDisplayDate = firstDisplayDate;
		this.url              = url;
		
	} // end constructor
	
	/*
	 * getter and setter methods
	 */
	public String getId() {
		return id;
	}
	
	public void setId(String value) {
		// check on the parameter
		if(InputUtils.isValidInt(value) == false) {
			throw new IllegalArgumentException("The id parameter must be a valid integer");
		}
		
		this.id = (value);
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String value) {
		// check on the parameter
		if(InputUtils.isValid(value) == false) {
			throw new IllegalArgumentException("The parameter must be not be null or empty");
		}
		this.name = value.trim();
	}
	
	public String getFirstDate() {
		return firstDate;
	}
	
	public void setFirstDate(String value) {
		// check on the parameter
		if(InputUtils.isValid(value) == false) {
			throw new IllegalArgumentException("The parameter must be not be null or empty");
		}
		firstDate = value.trim();
	}
	
	public String getFirstDisplayDate() {
		return firstDisplayDate;
	}
	
	public void setFirstDisplayDate(String value) {
		// check on the parameter
		if(InputUtils.isValid(value) == false) {
			throw new IllegalArgumentException("The parameter must be not be null or empty");
		}
		firstDisplayDate = value.trim();
	}
	
	public String getUrl() {
		return url;
	}
	
	public void setUrl(String value) {
		// check on the parameter
		if(InputUtils.isValid(value) == false) {
			throw new IllegalArgumentException("The parameter must be not be null or empty");
		}
		url = value.trim();
	}
	
	public int getFirstDateAsInt() {
		String date = firstDate.replace("-", "");
		return Integer.valueOf(date);
	}
	
	public void setVenueId(String value) {
		// check on the parameter
		if(InputUtils.isValid(value) == false) {
			throw new IllegalArgumentException("The parameter must be not be null or empty");
		}
		venueId = value.trim();
	}
	
	public String getVenueId() {
		return venueId;
	}
	
	public void setVenue(String value) {
		// check on the parameter
		if(InputUtils.isValid(value) == false) {
			throw new IllegalArgumentException("The parameter must be not be null or empty");
		}
		venue = value.trim();
	}
	
	public String getVenue() {
		return venue;
	}
	
	public void setLatitude(String value) {
		// check on the parameter
//		if(InputUtils.isValid(value) == false) {
//			throw new IllegalArgumentException("The parameter must be not be null or empty");
//		}
		if (value != null) {
			latitude = value.trim();
		} else {
			latitude = null;
		}
	}
	
	public String getLatitude() {
		return latitude;
	}
	
	public void setLongitude(String value) {
		// check on the parameter
//		if(InputUtils.isValid(value) == false) {
//			throw new IllegalArgumentException("The parameter must be not be null or empty");
//		}
		
		if (value != null) {
			longitude = value.trim();
		} else {
			longitude = null;
		}
	}
	
	public String getLongitude() {
		return longitude;
	}
	
	public void setSortFirstDate(String value) {
		sortFirstDate = value;
	}
	
	public String getSortFirstDate() {
		return sortFirstDate;
	}
	
	public void setSortLastDate(String value) {
		sortLastDate = value;
	}
	
	public String getSortLastDate() {
		return sortLastDate;
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
		int myId   = Integer.parseInt(id);
		int yourId = Integer.parseInt(e.getId());
		
		if(myId == yourId) {
			return 0;
		} else {
			return myId - yourId;
		}
		
	} // end compareTo method

} // end class definition
