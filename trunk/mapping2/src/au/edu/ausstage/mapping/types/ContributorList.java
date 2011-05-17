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
import java.util.HashSet;
import java.util.TreeSet;
import java.util.Iterator;


/**
 * A class to represent a list of venues
 */
public class ContributorList {

	// declare public constants
	/**
	 * Sort contributors by id
	 */
	public final static int CONTRIBUTOR_ID_SORT = 0;
	
	/**
	 * sort contributors by name
	 */
	public final static int CONTRIBUTOR_NAME_SORT = 1;
	
	/**
	 * sort contributors by last name, first name
	 */
	public final static int CONTRIBUTOR_ALT_NAME_SORT = 3;

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
	public void addContributor(Contributor contributor) {
		// check on the parameter
		if(contributor != null) {
			contributors.add(contributor);
		} else {
			throw new IllegalArgumentException("Contributor cannot be null");
		}
	} // end addNewContributor method
	
	/**
	 * A method to check if this list has this contributor already
	 *
	 * @param id the unique identifer of this contributor
	 *
	 * @return       true if this list has this contributor
	 */
	public boolean hasContributor(String id) {
	
		// check on the parameter
		if(InputUtils.isValidInt(id) == false) {
			throw new IllegalArgumentException("The id parameter must be a valid integer");
		}
		
		Contributor contributor = new Contributor(id);
		
		return hasContributor(contributor);
	}
	
	/**
	 * A method to check if this list has this contributor already
	 *
	 * @param contributor a contributor object to use for comparison
	 *
	 * @return            true if this list has this contributor
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
	
		// check on the parameter
		if(InputUtils.isValidInt(id) == false) {
			throw new IllegalArgumentException("The id parameter must be a valid integer");
		}
		
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
	
	/**
	 * A method to get the sorted list of contributors for this venue
	 *
	 * @param sortType the type of sort to use on the list of contributors
	 *
	 * @return the sorted list of events
	 */
	public Set<Contributor> getSortedContributors(int sortType) {
	
		// declare helper variables
		Set<Contributor> sortedContributors;
	
		// determine what type of sort to do
		if(sortType == CONTRIBUTOR_ID_SORT) {
			sortedContributors = new TreeSet<Contributor>(contributors);
		} else if (sortType == CONTRIBUTOR_NAME_SORT) {
			sortedContributors = new TreeSet<Contributor>(new ContributorNameComparator());
			sortedContributors.addAll(contributors);
		} else if (sortType == CONTRIBUTOR_ALT_NAME_SORT) {
			sortedContributors = new TreeSet<Contributor>(new ContributorAltNameComparator());
			sortedContributors.addAll(contributors);
		} else {
			throw new IllegalArgumentException("Unknown sort type specified");
		}
		
		return sortedContributors;
	}
	
	/**
	 * A method to get the sorted list of contributors for this venue as an array
	 *
	 * @param sortType the type of sort to use on the list of contributors
	 *
	 * @return the sorted list of events
	 */
	public Contributor[] getSortedContributorsArray(int sortType) {
	
		// get the sorted events
		Set<Contributor> sortedContributors = getSortedContributors(sortType);
		
		// convert to an array
		return sortedContributors.toArray(new Contributor[0]);
	
	} // end getSortedEvents method

}
