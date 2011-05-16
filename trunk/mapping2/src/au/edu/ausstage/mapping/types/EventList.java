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

// import additional libraries
import java.util.Set;
import java.util.TreeSet;
import java.util.HashSet;
import java.util.Iterator;

/**
 * A class to represent a list of venues
 */
public class EventList {

	// declare private variables
	private Set<Event> events;
	
	// declare public constants
	
	/**
	 * Sort events by id
	 */
	public final static int EVENT_ID_SORT = 0;
	
	/**
	 * sort events by name
	 */
	public final static int EVENT_NAME_SORT = 1;
	
	/**
	 * sort events by first date
	 */
	public final static int EVENT_DATE_SORT = 2;
	
	
	/**
	 * A constructor for this class
	 */
	public EventList() {
		events = new HashSet<Event>();
	}
	
	/*
	 * Event management
	 */
	
	/**
	 * A method to add an event to the list of events
	 *
	 * @param value the new Event object
	 */
	public void addEvent(Event value) {
		if(value != null) {
			events.add(value);
		} else {
			throw new IllegalArgumentException("The value to the this method cannot be null");
		}
	}
	
	/**
	 * A method to check if this list has this event already
	 *
	 * @param value the unique identifier for the event
	 *
	 * @return      true if, and only if, the event is found
	 */
	public boolean hasEvent(String value) {
		
		// check on the parameter
		if(InputUtils.isValidInt(value) == false) {
			throw new IllegalArgumentException("The value to this method is expected to be a valid integer");
		}
		
		Event event = new Event(value);
		
		return hasEvent(event);
	}
	
	/**
	 * A method to check if this list has this event already
	 *
	 * @param value the event object we're looking for
	 *
	 * @return      true if, and only if, the event is found
	 */
	public boolean hasEvent(Event value) {
		if(value != null) {
			return events.contains(value);
		} else {
			throw new IllegalArgumentException("The value to this method cannot be null");
		}
	}
	
	
	/**
	 * A method to retrieve an event from the list
	 *
	 * @param value the unique identifier of the event
	 *
	 * @return      the event if found, null if nothing is found
	 */
	public Event getEvent(String value) {
	
		// check on the parameter
		if(InputUtils.isValidInt(value) == false) {
			throw new IllegalArgumentException("The value to this method is expected to be a valid integer");
		}
		
		// get an iterator for this set
		Iterator iterator = events.iterator();
		
		// declare other helper variables
		Event event = null;
		
		while(iterator.hasNext()) {
			
			// get the event at this place in the set
			event = (Event)iterator.next();
			
			// compare the ids
			if(event.getId().equals(value) == true) {
				return event;
			}
		}
		
		// if we get this far nothing was found
		return null;
	}
	
	
	/**
	 * A method to get the list of events as a set of event objects
	 *
	 * @return the list of events
	 */
	public Set<Event> getEvents() {
		return events;
	}
	
	/**
	 * A method to get the list of events as an array of event objects
	 *
	 * @return the list of events
	 */
	public Event[] getEventsArray() {
		return events.toArray(new Event[0]);
	}
	
	/**
	 * A method to get the sorted list of events
	 *
	 * @param sortType the type of sort to use to build the list of events
	 *
	 * @return the sorted list of events
	 */
	public Set<Event> getSortedEvents(int sortType) {
	
		// declare helper variables
		Set<Event> sortedEvents;
		
		// determine the tpe of sort
		if(sortType == EVENT_ID_SORT) {
			sortedEvents = new TreeSet<Event>(events);
		} else if(sortType == EVENT_NAME_SORT) {
			sortedEvents = new TreeSet<Event>(new EventNameComparator());
			sortedEvents.addAll(events);
		} else if(sortType == EVENT_DATE_SORT) {
			sortedEvents = new TreeSet<Event>(new EventDateComparator());
			sortedEvents.addAll(events);		
		} else {
			throw new IllegalArgumentException("Unknown sort type specified");
		}
	
		return sortedEvents;
	}
	
	/**
	 * A method to get the sorted list of events as an array
	 *
	 * @param sortType the type of sort to use to build the list of events
	 *
	 * @return the sorted list of events
	 */
	public Event[] getSortedEventsArray(int sortType) {
		
		Set<Event> sortedEvents = getSortedEvents(sortType);
		
		return sortedEvents.toArray(new Event[0]);
	}
}
