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
 * A class to represent a venue
 */
public class Venue implements Comparable<Venue>{

	// declare private variables
	private String id = null;
	private String name = null;
	private String street = null;
	private String suburb = null;
	private String state = null;
	private String postcode = null;
	private String latitude = null;
	private String longitude = null;
	private String url = null;
	private String eventCount = null;
	private Set<Contributor> contributors;
	private Set<Organisation> organisations;
	
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
	 * Sort organisations by id
	 */
	public final static int ORGANISATION_ID_SORT = 0;
	
	/**
	 * sort organisations by name
	 */
	public final static int ORGANISATION_NAME_SORT = 1;
	
	/**
	 * Constructor for this class
	 *
	 * @param id the unique identifier for this venue
	 */
	public Venue(String id) {
	
		// check on the parameter
		if(InputUtils.isValidInt(id) == false) {
			throw new IllegalArgumentException("The id parameter must be a valid integer");
		}
		
		this.id = id;
		
		// initialise the two collections		
		contributors = new HashSet<Contributor>();
		organisations = new TreeSet<Organisation>();
	} // end constructor
	
	/**
	 * Constructor for this class
	 *
	 * @param id        the unique identifier for this venue
	 * @param name      the name of this venue
	 * @param suburb    the suburb for this venue
	 * @param state     the state for this venue
	 * @param postcode  the postcode for this venue
	 * @param latitude  the latitude for this venue
	 * @param longitude the longitude for this venue
	 * @param url       the URL for this venue in AusStage
	 */
	public Venue(String id, String name, String suburb, String state, String postcode, String latitude, String longitude, String url) {
		
		// check the parameters
		if(InputUtils.isValid(id) == false || InputUtils.isValid(name) == false || InputUtils.isValid(state) == false) {
			throw new IllegalArgumentException("All method parameters are required");
		}
		
		if(InputUtils.isValid(latitude) == false || InputUtils.isValid(longitude) == false || InputUtils.isValid(url) == false) { 
			throw new IllegalArgumentException("All method parameters are required");
		}
		
		if(InputUtils.isValidInt(id) == false) {
			throw new IllegalArgumentException("The id parameter must be a valid integer");
		}
		
		this.id        = id;
		this.name      = name;
		this.suburb    = suburb;
		this.state     = state;
		this.postcode  = postcode;
		this.latitude  = latitude;
		this.longitude = longitude;
		this.url       = url;
		
		contributors = new HashSet<Contributor>();
		organisations = new HashSet<Organisation>();
		
	} // end constructor
	
	/*
	 * Contributor management
	 */
	
	/**
	 * A method to add a contributor to this venue
	 *
	 * @param contributor the new contributor
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
	 * A method to check if this venue has contributor
	 *
	 * @param id the unique identifer of this contributor
	 *
	 * @return       true if this venue has this contributor
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
	 * A method to check if this venue has contributor
	 *
	 * @param contributor the contributor we're looking for
	 *
	 * @return       true if this venue has this contributor
	 */
	public boolean hasContributor(Contributor contributor) {
		// check on the parameter
		if(contributor != null) {
			return contributors.contains(contributor);
		} else {
			throw new IllegalArgumentException("Contributor cannot be null");
		}
	}
	
	/**
	 * A method to get a specific Contributor
	 *
	 * @param id the unique identifer of the event
	 *
	 * @return   the event if found, null if nothing is found
	 */
	public Contributor getContributor(String id) {
	
		// check on the parameter
		if(InputUtils.isValidInt(id) == false) {
			throw new IllegalArgumentException("The id parameter must be a valid integer");
		}
		
		// get an iterator for this set
		Iterator iterator = contributors.iterator();
		
		// loop through the list of contributors
		while(iterator.hasNext()) {
			// get the next contributor
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
	 * A method to get the list of contributors for this venue
	 *
	 * @return the list of contributors
	 */
	public Set<Contributor> getContributors() {
		return contributors;
	}
	
	/**
	 * A method to get the list of contributors for this venue as an array
	 *
	 * @return the list of contributors
	 */
	public Contributor[] getContributorsArray() {
		return contributors.toArray(new Contributor[0]);
	}
	
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
	
	/*
	 * Organisation Management
	 */
	
	/**
	 * A method to add an organisation to this venue
	 *
	 * @param organisation the new organisation
	 */
	public void addOrganisation(Organisation organisation) {
		// check on the parameter
		if(organisation != null) {
			organisations.add(organisation);
		} else {
			throw new IllegalArgumentException("Organisation cannot be null");
		}
	} // end addNewOrganisationmethod
	
	/**
	 * A method to check if this venue has an organisation
	 *
	 * @param id the unique identifer of this organisation
	 *
	 * @return       true if this venue has this organisation
	 */
	public boolean hasOrganisation(String id) {
		// check on the parameter
		if(InputUtils.isValidInt(id) == false) {
			throw new IllegalArgumentException("The id parameter must be a valid integer");
		}

		Organisation organisation = new Organisation(id);
		return hasOrganisation(organisation);
	}
	
	/**
	 * A method to check if this venue has an organisation
	 *
	 * @param organisation the organisation object
	 *
	 * @return       true if this venue has this organisation
	 */
	public boolean hasOrganisation(Organisation organisation) {
	
		// check on the parameter
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
	 * @return   the event if found, null if nothing is found
	 */
	public Organisation getOrganisation(String id) {
	
		// check on the parameter
		if(InputUtils.isValidInt(id) == false) {
			throw new IllegalArgumentException("The id parameter must be a valid integer");
		}
		
		// get an iterator for this set
		Iterator iterator = organisations.iterator();
		
		// loop through the list of events looking through 
		while(iterator.hasNext()) {
			// get the organisation at this place in the set
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
	 * A method to get the list of organisations for this venue
	 *
	 * @return the list of organisations
	 */
	public Set<Organisation> getOrganisations() {
		return organisations;
	}
	
	/**
	 * A method to get the list of organisations for this venue as an array
	 *
	 * @return the list of organisations
	 */
	public Organisation[] getOrganisationsArray() {
		return organisations.toArray(new Organisation[0]);
	}
	
	/**
	 * A method to get the sorted list of organisations for this venue
	 *
	 * @param sortType the type of sort to use on the list of organisations
	 *
	 * @return the sorted list of events
	 */
	public Set<Organisation> getSortedOrganisations(int sortType) {
	
		// declare helper variables
		Set<Organisation> sortedOrganisations;
	
		// determine what type of sort to do
		if(sortType == ORGANISATION_ID_SORT) {
			sortedOrganisations = new TreeSet<Organisation>(organisations);
		} else if (sortType == ORGANISATION_NAME_SORT) {
			sortedOrganisations = new TreeSet<Organisation>(new OrganisationNameComparator());
			sortedOrganisations.addAll(organisations);
		} else {
			throw new IllegalArgumentException("Unknown sort type specified");
		}
		
		return sortedOrganisations;
	
	}
	
	/**
	 * A method to get the sorted list of contributors for this venue as an array
	 *
	 * @param sortType the type of sort to use on the list of contributors
	 *
	 * @return the sorted list of events
	 */
	public Organisation[] getSortedOrganisationsArray(int sortType) {
	
		// get the sorted events
		Set<Organisation> sortedOrganisations = getSortedOrganisations(sortType);
		
		// convert to an array
		return sortedOrganisations.toArray(new Organisation[0]);
	
	}
	
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
		
		this.id = value;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String value) {
		if(InputUtils.isValid(value) == false) {
			throw new IllegalArgumentException("The value cannot be null");
		}
		name = value;
	}
	
	public String getState() {
		return state;
	}
	
	public void setState(String value) {
		if(InputUtils.isValid(value) == false) {
			throw new IllegalArgumentException("The value cannot be null");
		}
		state = value;
	}
	
	public String getSuburb() {
		return suburb;
	}
	
	public void setSuburb(String value) {
		suburb = value;
	}
	
	public String getStreet() {
		return street;
	}
	
	public void setStreet(String value) {
		street = value;
	}
	
	public String getPostcode() {
		return postcode;
	}
	
	public void setPostcode(String value) {
		postcode = value;
	}
	
	public String getLatitude() {
		return latitude;
	}
	
	public void setLatitude(String value) {
//		if(InputUtils.isValid(value) == false) {
//			throw new IllegalArgumentException("The value cannot be null");
//		}
		latitude = value;
	}
	
	public String getLongitude() {
		return longitude;
	}
	
	public void setLongitude(String value) {
//		if(InputUtils.isValid(value) == false) {
//			throw new IllegalArgumentException("The value cannot be null");
//		}
		longitude = value;
	}
	
	public String getUrl() {
		return url;
	}
	
	public void setUrl(String value) {
		if(InputUtils.isValid(value) == false) {
			throw new IllegalArgumentException("The value cannot be null");
		}
		url = value;
	}
	
	public String getEventCount() {
		return eventCount;
	}
	
	public void setEventCount(String value) {
		// check on the parameter
		if(InputUtils.isValidInt(value) == false) {
			throw new IllegalArgumentException("The id parameter must be a valid integer");
		}
		
		this.eventCount = value;
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
		if ((o instanceof Venue) == false) {
			// o is not an event object
		 	return false;
		}
		
		// compare these two events
		Venue v = (Venue)o;
		
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
	public int compareTo(Venue v) {
		int myId   = Integer.parseInt(id);
		int yourId = Integer.parseInt(v.getId());
		
		if(myId == yourId) {
			return 0;
		} else {
			return myId - yourId;
		}
		
	} // end compareTo method
	
} // end class definition
