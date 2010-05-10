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
 * A class to represent a list of venues
 */
public class VenueList extends DataClasses {

	// declare private variables
	private Set<Venue> venues;
	
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
	public void addNewVenue(Venue venue) {
		// check on the parameter
		if(venue != null) {
			venues.add(venue);
		} else {
			throw new IllegalArgumentException("Venue cannot be null");
		}
	} // end addNewVenue method
	
	/**
	 * A method to add a venue to this list of venues
	 *
	 * @param venue the new Venue
	 */
	public void addVenue(Venue venue) {
		addNewVenue(venue);
	} // end addNewEvent method
	
	/**
	 * A method to check if this list has this venue already
	 *
	 * @param id the unique identifer of this venue
	 *
	 * @return       true if this list has this venue
	 */
	public boolean hasVenue(String id) {
	
		id = filterString(id);
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

}
