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
 * A class to represent a list of organisations
 */
public class OrganisationList extends DataClasses {

	// declare private variables
	private Set<Organisation> organisations;
	
	/**
	 * A constructor for this class
	 */
	public OrganisationList() {
		organisations = new HashSet<Organisation>();
	}
	
	/*
	 * Organisation management
	 */
	
	/**
	 * A method to add an organisation to the list of organisations
	 *
	 * @param organisation the new organisation object
	 */
	public void addNewOrganisation(Organisation organisation) {
		// check on the parameter
		if(organisation != null) {
			organisations.add(organisation);
		} else {
			throw new IllegalArgumentException("Organisation cannot be null");
		}
	} // end addNewOrganisation method
	
	/**
	 * A method to add an organisation to the list of organisations
	 *
	 * @param organisation the new organisation object
	 */
	public void addOrganisation(Organisation organisation) {
		addNewOrganisation(organisation);
	} // end addNewEvent method
	
	/**
	 * A method to check if this list has this organisation already
	 *
	 * @param id the unique identifer of this organisation
	 *
	 * @return       true if this list has this organisation
	 */
	public boolean hasOrganisation(String id) {
	
		id = filterString(id);
		Organisation organisation = new Organisation(id);
		
		return hasOrganisation(organisation);
	}
	
	/**
	 * A method to check if this list has this organisation already
	 *
	 * @param id the unique identifer of this organisation
	 *
	 * @return       true if this list has this organisation
	 */
	public boolean hasOrganisation(Organisation organisation) {
		if(organisation != null) {
			return organisations.contains(organisation);
		} else {
			throw new IllegalArgumentException("Organisation cannot be null");
		}
	}
	
	/**
	 * A method to get a specific organisation
	 *
	 * @param id the unique identifer of the organisation
	 *
	 * @return   the organisation if found, null if nothing is found
	 */
	public Organisation getOrganisation(String id) {
		
		// get an iterator for this set
		Iterator iterator = organisations.iterator();
		
		// loop through the list of events looking through 
		while(iterator.hasNext()) {
			// get the event at this place in the set
			Organisation organisation = (Organisation)iterator.next();
			
			// compare ids
			if(organisation.getId().equals(id) == true) {
				return organisation;
			}
		}
		
		// if we get here, nothing was found
		return null;
	}
	
	/**
	 * A method to get the list of organisations
	 *
	 * @return the list of organisations
	 */
	public Set<Organisation> getOrganisations() {
		return organisations;
	} // end getOrganisations method
	
	/**
	 * A method to get the list of venues as an array
	 *
	 * @return the list of contributors
	 */
	public Organisation[] getOrganisationArray() {
		return organisations.toArray(new Organisation[0]);
	} // end getOrganisationArray

}
