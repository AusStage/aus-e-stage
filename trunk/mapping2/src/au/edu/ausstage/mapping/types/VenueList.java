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
public class VenueList {

	// declare private variables
	private Set<Venue> venues;
	
	// declare public constants
	
	/**
	 * Sort venues by id
	 */
	public final static int VENUE_ID_SORT = 0;
	
	/**
	 * sort venues by name
	 */
	public final static int VENUE_NAME_SORT = 1;
	
	/**
	 * A constructor for this class
	 */
	public VenueList() {
		venues = new HashSet<Venue>();
	}
	
	/*
	 * Venue management
	 */
	
	/**
	 * A method to add a venue to this list of venues
	 *
	 * @param venue the new Venue
	 */
	public void addVenue(Venue venue) {
		// check on the parameter
		if(venue != null) {
			venues.add(venue);
		} else {
			throw new IllegalArgumentException("Venue cannot be null");
		}
	} // end addVenue method
	
	/**
	 * A method to check if this list has this venue already
	 *
	 * @param id the unique identifer of this venue
	 *
	 * @return       true if this list has this venue
	 */
	public boolean hasVenue(String id) {
	
		// check on the parameter
		if(InputUtils.isValidInt(id) == false) {
			throw new IllegalArgumentException("The id parameter must be a valid integer");
		}
		
		Venue venue = new Venue(id);
		
		return hasVenue(venue);
	}
	
	/**
	 * A method to check if this venue is in the list
	 *
	 * @param venue the vene we're looking for
	 *
	 * @return       true if this venue has this contributor
	 */
	public boolean hasVenue(Venue venue) {
		if(venue != null) {
			return venues.contains(venue);
		} else {
			throw new IllegalArgumentException("Venue cannot be null");
		}
	}
	
	/**
	 * A method to get a specific venue
	 *
	 * @param id the unique identifer of the venue
	 *
	 * @return   the venue if found, null if nothing is found
	 */
	public Venue getVenue(String id) {
	
		// check on the parameter
		if(InputUtils.isValidInt(id) == false) {
			throw new IllegalArgumentException("The id parameter must be a valid integer");
		}
		
		// get an iterator for this set
		Iterator iterator = venues.iterator();
		
		// loop through the list of events looking through 
		while(iterator.hasNext()) {
			// get the event at this place in the set
			Venue venue = (Venue)iterator.next();
			
			// compare ids
			if(venue.getId().equals(id) == true) {
				return venue;
			}
		}
		
		// if we get here, nothing was found
		return null;
	}
	
	/**
	 * A method to get the list of venues
	 *
	 * @return the list of venues
	 */
	public Set<Venue> getVenues() {
		return venues;
	} // end getEvents method
	
	/**
	 * A method to get the list of venues as an array
	 *
	 * @return the list of contributors
	 */
	public Venue[] getVenuesArray() {
		return venues.toArray(new Venue[0]);
	} // end getEvents method
	
	/**
	 * A method to get the sorted list of organisations for this venue
	 *
	 * @param sortType the type of sort to use on the list of organisations
	 *
	 * @return the sorted list of events
	 */
	public Set<Venue> getSortedVenues(int sortType) {
	
		// declare helper variables
		Set<Venue> sortedVenues;
	
		// determine what type of sort to do
		if(sortType == VENUE_ID_SORT) {
			sortedVenues = new TreeSet<Venue>(venues);
		} else if (sortType == VENUE_NAME_SORT) {
			sortedVenues = new TreeSet<Venue>(new VenueNameComparator());
			sortedVenues.addAll(venues);
		} else {
			throw new IllegalArgumentException("Unknown sort type specified");
		}
		
		return sortedVenues;
	
	}
	
	/**
	 * A method to get the sorted list of contributors for this venue as an array
	 *
	 * @param sortType the type of sort to use on the list of contributors
	 *
	 * @return the sorted list of events
	 */
	public Venue[] getSortedVenuesArray(int sortType) {
	
		// get the sorted events
		Set<Venue> sortedVenues = getSortedVenues(sortType);
		
		// convert to an array
		return sortedVenues.toArray(new Venue[0]);
	}

}
