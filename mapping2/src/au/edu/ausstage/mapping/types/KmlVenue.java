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
import au.edu.ausstage.utils.*;
import java.util.Set;
import java.util.Iterator;


/**
 * A class used to represent a venue when building KML data
 */
public class KmlVenue implements Comparable<KmlVenue> {

	// private level variables
	private Integer id;
	private String  name;
	private String  address;
	private String  shortAddress;
	private String  latitude;
	private String  longitude;
	private EventList events;
	
	/**
	 * Constructor for this class
	 * all fields are are required
	 *
	 * @param id the unique identifier for this venue
	 * @param name the name of this venue
	 * @param address the address of this venue
	 * @param latitude the latitude for this venue
	 * @param longitude the longitude for this venue
	 *
	 * @throws IllegalArgumentException if any of the parameters are invalid
	 */
	public KmlVenue(String id, String name, String address, String shortAddress, String latitude, String longitude) {
	
		// check the parameters
		if(InputUtils.isValidInt(id) == false) {
			throw new IllegalArgumentException("the id field must be a valid integer");
		}
		
		if(InputUtils.isValid(name) == false || InputUtils.isValid(address) == false || InputUtils.isValid(shortAddress) == false || InputUtils.isValid(latitude) == false || InputUtils.isValid(longitude) == false) {
			throw new IllegalArgumentException("all fields to this constructor are required");
		}
		
		// store values for later
		this.id = Integer.parseInt(id);
		this.name = name;
		this.address = address;
		this.latitude = latitude;
		this.longitude = longitude;
		this.shortAddress = shortAddress;
		
		events = new EventList();
	}
	
	public Integer getId() {
		return id;
	}
	
	public String getName() {
		return name;
	}
	
	public String getAddress() {
		return address;
	}
	
	public String getShortAddress() {
		return shortAddress;
	}
	
	public String getLatitude() {
		return latitude;
	}
	
	public String getLongitude() {
		return longitude;
	}
	
	public String getUrl() {
		return LinksManager.getVenueLink(id.toString());
	}
	
	public void addEvent(Event event) {
		if(event == null) {
			throw new IllegalArgumentException("the event field must not be null");
		} else {
			events.addEvent(event);
		}
	}
	
	public Set<Event> getEvents() {
		return events.getSortedEvents(events.EVENT_DATE_SORT);
	}
	
	public String[] getTimespanValues() {
	
		if(events == null) {
			return null;
		}
		
		int min = Integer.MAX_VALUE;
		int max = Integer.MIN_VALUE;
		
		String[] values = new String[2];
		
		Set<Event> list = events.getEvents();
		Iterator iterator = list.iterator();
		
		while(iterator.hasNext()) {
			Event event = (Event)iterator.next();
			
			if(DateUtils.getIntegerFromDate(event.getSortFirstDate()) < min) {
				min = DateUtils.getIntegerFromDate(event.getSortFirstDate());
				values[0] = event.getSortFirstDate();
			}
			
			if(DateUtils.getIntegerFromDate(event.getSortLastDate()) > max) {
				max = DateUtils.getIntegerFromDate(event.getSortLastDate());
				values[1] = event.getSortLastDate();
			}
		}
		
		return values;	
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
		if ((o instanceof KmlVenue) == false) {
			// o is not an event object
		 	return false;
		}
		
		// compare these two events
		KmlVenue v = (KmlVenue)o;
		
		return id.equals(v.getId());
		
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
     * @param v the venue to compare this one to
     *
     * @return a integer indicating comparison result
     */    
	public int compareTo(KmlVenue v) {
		int myId   = id;
		int yourId = v.getId();
		
		if(myId == yourId) {
			return 0;
		} else {
			return myId - yourId;
		}
		
	} // end compareTo method
}
