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
public class ContributorList extends DataClasses {

	// declare private variables
	private Set<Contributor> contributors;
	
	/**
	 * A constructor for this class
	 */
	public ContributorList() {
		contributors = new HashSet<Contributor>();
	}
	
	/*
	 * Contributor management
	 */
	
	/**
	 * A method to add a contributor to this list of contributors
	 *
	 * @param contributor the new contributor object
	 */
	public void addNewContributor(Contributor contributor) {
		// check on the parameter
		if(contributor != null) {
			contributors.add(contributor);
		} else {
			throw new IllegalArgumentException("Contributor cannot be null");
		}
	} // end addNewContributor method
	
	/**
	 * A method to add a contributor to this list of contributors
	 *
	 * @param contributor the new contributor object
	 */
	public void addContributor(Contributor contributor) {
		addNewContributor(contributor);
	} // end addNewEvent method
	
	/**
	 * A method to check if this list has this contributor already
	 *
	 * @param id the unique identifer of this contributor
	 *
	 * @return       true if this list has this contributor
	 */
	public boolean hasContributor(String id) {
	
		id = filterString(id);
		Contributor contributor = new Contributor(id);
		
		return hasContributor(contributor);
	}
	
	/**
	 * A method to check if this list has this contributor already
	 *
	 * @param id the unique identifer of this contributor
	 *
	 * @return       true if this list has this contributor
	 */
	public boolean hasContributor(Contributor contributor) {
		if(contributor != null) {
			return contributors.contains(contributor);
		} else {
			throw new IllegalArgumentException("Contributor cannot be null");
		}
	}
	
	/**
	 * A method to get a specific contributor
	 *
	 * @param id the unique identifer of the contributor
	 *
	 * @return   the venue if found, null if nothing is found
	 */
	public Contributor getContributor(String id) {
		
		// get an iterator for this set
		Iterator iterator = contributors.iterator();
		
		// loop through the list of events looking through 
		while(iterator.hasNext()) {
			// get the event at this place in the set
			Contributor contributor = (Contributor)iterator.next();
			
			// compare ids
			if(contributor.getId().equals(id) == true) {
				return contributor;
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
	public Set<Contributor> getContributors() {
		return contributors;
	} // end getEvents method
	
	/**
	 * A method to get the list of venues as an array
	 *
	 * @return the list of contributors
	 */
	public Contributor[] getContributorArray() {
		return contributors.toArray(new Contributor[0]);
	} // end getEvents method

}
