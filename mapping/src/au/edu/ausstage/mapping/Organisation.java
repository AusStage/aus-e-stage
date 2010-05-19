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
 * A class to represent an Organisation
 */
public class Organisation extends DataClasses implements Comparable<Organisation>{

	// declare private variables
	private String id = null;
	private String name = null;
	private String url  = null;
	private Set<Event> events;
	private TreeMap<String, String> trajectory;
	private String[] trajKeyDiff = {"a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z"};
	private int trajKeyDiffIndex = 0;
	
	// declare public constants
	/**
	 * Sort events by id
	 */
	public final static int EVENT_ID_SORT = 0;
	
	/**
	 * sort events by first date
	 */
	public final static int EVENT_FIRST_DATE_SORT = 1;
	
	
	/**
	 * Constructor for this class
	 *
	 * @param id the unique identifier for this contributor
	 */
	public Organisation(String id) {
	
		try {
			this.id = filterString(id);
			filterInteger(id);
		} catch (IllegalArgumentException ex) {
			throw new IllegalArgumentException("Contributor ID must not be null or an empty string");
		}
		
		events = new HashSet<Event>();
		trajectory = new TreeMap<String, String>();

	} // end constructor
	
	/**
	 * Constructor for this class
	 *
	 * @param id        the unique identifier for this contributor
	 * @param name      the name of this contributor
	 * @param url       the url for this contributor in AusStage
	 */
	public Organisation(String id, String name, String url) {
		
		// check the parameters
		try {
			this.id        = filterString(id);
			this.name      = filterString(name);
			this.url       = filterString(url);
		} catch (IllegalArgumentException ex) {
			throw new IllegalArgumentException("All arguments must not be null or empty strings: " + ex.toString());
		}
		
		try {
			filterInteger(id);
		} catch (IllegalArgumentException ex) {
			throw new IllegalArgumentException("ID is expected to be an integer");
		}
		
		events = new HashSet<Event>();
		trajectory = new TreeMap<String, String>();
		
	} // end constructor
	
	/*
	 * Event management
	 */
	
	/**
	 * A method to add an event for this organisation
	 *
	 * @param event the new event
	 */
	public void addNewEvent(Event event) {
		// check on the parameter
		if(event != null) {
			events.add(event);
		} else {
			throw new IllegalArgumentException("Event cannot be null");
		}
	} // end addNewEvent method
	
	/**
	 * A method to add an event for this organisation
	 *
	 * @param event the new event
	 */
	public void addEvent(Event event) {
		addNewEvent(event);
	} // end addNewEvent method
	
	/**
	 * A method to check if this organisation has an event 
	 *
	 * @param id the unique identifer of this event
	 *
	 * @return       true if this organisation has this event
	 */
	public boolean hasEvent(String id) {
		id = filterString(id);
		
		Event newEvent = new Event(id);
		
		return hasEvent(newEvent);
	}
	
	/**
	 * A method to check if this organisation has an event 
	 *
	 * @param event the event object to check
	 *
	 * @return      true if this organisation has this event
	 */
	public boolean hasEvent(Event event) {
		// check on the parameter
		if(event != null) {
			return events.contains(event);
		} else {
			throw new IllegalArgumentException("Event cannot be null");
		}
	}
	
	/**
	 * A method to get a specific event
	 *
	 * @param id the unique identifer of the event
	 *
	 * @return   the event if found, null if nothing is found
	 */
	public Event getEvent(String id) {
		
		// get an iterator for this set
		Iterator iterator = events.iterator();
		
		// loop through the list of events looking through 
		while(iterator.hasNext()) {
			// get the event at this place in the set
			Event event = (Event)iterator.next();
			
			// compare ids
			if(event.getId().equals(id) == true) {
				return event;
			}
		}
		
		// if we get here, nothing was found
		return null;
	}
	
	/**
	 * A method to get the list of events for this contributor
	 *
	 * @return the list of events
	 */
	public Set getEvents() {
		return events;
	} // end getEvents method
	
	/**
	 * A method to get the list of events for this contributor as an array
	 *
	 * @return the list of events
	 */
	public Event[] getEventsArray() {
		return events.toArray(new Event[0]);
	} // end getEvents method
	
	/**
	 * A method to get the sorted list of events for this contributor
	 *
	 * @param sortType the type of sort to use on the list of events
	 *
	 * @return the sorted list of events
	 */
	public Set<Event> getSortedEvents(int sortType) {
	
		// declare helper variables
		Set<Event> sortedEvents;
	
		// determine what type of sort to do
		if(sortType == EVENT_ID_SORT) {
			sortedEvents = new TreeSet<Event>(events);
		} else if (sortType == EVENT_FIRST_DATE_SORT) {
			sortedEvents = new TreeSet<Event>(new EventFirstDateComparator());
			sortedEvents.addAll(events);
		} else {
			throw new IllegalArgumentException("Unknown sort type specified");
		}
		
		return sortedEvents;
	
	} // end getSortedEvents method
	
	/**
	 * A method to get the sorted list of events for this contributor as an array
	 *
	 * @param sortType the type of sort to use on the list of events
	 *
	 * @return the sorted list of events
	 */
	public Event[] getSortedEventsArray(int sortType) {
	
		// get the sorted events
		Set<Event> sortedEvents = getSortedEvents(sortType);
		
		// convert to an array
		return sortedEvents.toArray(new Event[0]);
	
	} // end getSortedEvents method
	
	/**
	 * A method to get the number of events for this contributor
	 *
	 * @return the number of evens for this contributor
	 */
	public int getEventCount() {
		return events.size();
	} // end getEventCount method
	
	/*
	 * methods to manage a trajectory
	 */
	 
	/**
	 * A method to add a coordinate to the list for the trajectory
	 *
	 * @param key    the date that this event occured
	 * @param coords the coordinates of where the event occured
	 *
	 */
	public void addTrajectoryPoint(String key, String coords) {
	
		// filter the input
		key    = filterString(key);
		coords = filterString(coords);
		
		// double check the key
		if(key.length() == 4) {
			key += "0101";
		} else if(key.length() == 6) {
			key += "01";
		}
		
		// check to see if this key has been used before
		if(trajectory.containsKey(key) == true) {
			key = key + trajKeyDiff[trajKeyDiffIndex];
			trajKeyDiffIndex++;
		}
		
		// add this coordinate to the hash
		trajectory.put(key, coords);
		
	} // end add trajectory point method
	
	/**
	 * A method to return the list of coordinates to build a trajectory
	 *
	 * @return the list of coordinates
	 */
	public Collection<String> getTrajectory() {
	
		// get the collection of values
		return trajectory.values();
	
	} // end get trajectory method
	
	/**
	 * A method to return the list of coordinates to build a trajectory
	 *
	 * @return the list of coordinates
	 */
	public String[] getTrajectoryArray() {
	
		// get the collection of values
		Collection<String> coords = getTrajectory();
		
		// return the collection as an array
		return coords.toArray(new String[0]);
	
	} // end getTrajectoryArray
	
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
	
	public String getUrl() {
		return url;
	}
	
	public void setUrl(String value) {
		this.url = filterString(value);
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
		if ((o instanceof Organisation) == false) {
			// o is not an event object
		 	return false;
		}
		
		// compare these two events
		Organisation org = (Organisation)o;
		
		return id.equals(org.getId());
		
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
     * @param c the organisation to compare this one to
     *
     * @return  an integer indicating comparison result
     */    
	public int compareTo(Organisation o) {
		int myId   = Integer.getInteger(id);
		int yourId = Integer.getInteger(o.getId());
		
		if(myId == yourId) {
			return 0;
		} else {
			return myId - yourId;
		}
		
	} // end compareTo method


} // end class definition
