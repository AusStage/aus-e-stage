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
 * A class to represent a venue
 */
public class Venue extends DataClasses implements Comparable<Venue>{

	// declare private variables
	private String id = null;
	private String name = null;
	private String suburb = null;
	private String state = null;
	private String postcode = null;
	private String latitude = null;
	private String longitude = null;
	private String url = null;
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
	
		try {
			this.id = filterString(id);
		} catch (IllegalArgumentException ex) {
			throw new IllegalArgumentException("Invalid Venue ID: " + ex.toString());
		}
		
		try {
			filterInteger(id);
		} catch (IllegalArgumentException ex) {
			throw new IllegalArgumentException("ID is expected to be an integer");
		}
		
		contributors = new HashSet<Contributor>();
		organisations = new HashSet<Organisation>();
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
		try {
			this.id        = filterString(id);
			this.name      = filterString(name);
			this.suburb    = filterString(suburb);
			this.state     = filterString(state);
			this.postcode  = postcode;
			this.latitude  = filterString(latitude);
			this.longitude = filterString(longitude);
			this.url       = filterString(url);
		} catch (IllegalArgumentException ex) {
			throw new IllegalArgumentException("All arguments must not be null or empty strings: " + ex.toString());
		}
		
		try {
			filterInteger(id);
		} catch (IllegalArgumentException ex) {
			throw new IllegalArgumentException("ID is expected to be an integer");
		}
		
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
	public void addNewContributor(Contributor contributor) {
		// check on the parameter
		if(contributor != null) {
			contributors.add(contributor);
		} else {
			throw new IllegalArgumentException("Contributor cannot be null");
		}
	} // end addNewContributor method
	
	/**
	 * A method to add a contributor to this venue
	 *
	 * @param contributor the new contributor
	 */
	public void addContributor(Contributor contributor) {
		addNewContributor(contributor);
	} // end addNewContributor method
	
	/**
	 * A method to check if this venue has contributor
	 *
	 * @param id the unique identifer of this contributor
	 *
	 * @return       true if this venue has this contributor
	 */
	public boolean hasContributor(String id) {
		// filter the parameter
		id = filterString(id);
		
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
	 * A method to get a specific event
	 *
	 * @param id the unique identifer of the event
	 *
	 * @return   the event if found, null if nothing is found
	 */
	public Contributor getContributor(String id) {
	
		// filter the parameter
		id = filterString(id);
		
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
	 * A method to get the list of contributors for this venue
	 *
	 * @return the list of contributors
	 */
	public Set<Contributor> getContributors() {
		return contributors;
	} // end getEvents method
	
	/**
	 * A method to get the list of contributors for this venue as an array
	 *
	 * @return the list of contributors
	 */
	public Contributor[] getContributorsArray() {
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
		} else {
			throw new IllegalArgumentException("Unknown sort type specified");
		}
		
		return sortedContributors;
	
	} // end getSortedEvents method
	
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
	public void addNewOrganisation(Organisation organisation) {
		// check on the parameter
		if(organisation != null) {
			organisations.add(organisation);
		} else {
			throw new IllegalArgumentException("Organisation cannot be null");
		}
	} // end addNewOrganisationmethod
	
	/**
	 * A method to add an organisation to this venue
	 *
	 * @param organisation the new organisation
	 */
	public void addOrganisation(Organisation organisation) {
		addNewOrganisation(organisation);
	} // end addNewContributor method
	
	/**
	 * A method to check if this venue has an organisation
	 *
	 * @param id the unique identifer of this organisation
	 *
	 * @return       true if this venue has this organisation
	 */
	public boolean hasOrganisation(String id) {
		// filter the parameter
		id = filterString(id);

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
	
		// filter the parameter
		id = filterString(id);
		
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
		this.id = filterString(value);
		filterInteger(id);
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String value) {
		this.name = filterString(value);
	}
	
	public String getState() {
		return state;
	}
	
	public void setState(String value) {
		this.state = filterString(value);
	}
	
	public String getSuburb() {
		return suburb;
	}
	
	public void setSuburb(String value) {
		this.suburb = filterString(value, true);
	}
	
	public String getPostcode() {
		return postcode;
	}
	
	public void setPostcode(String value) {
		this.postcode = value;
	}
	
	public String getLatitude() {
		return latitude;
	}
	
	public void setLatitude(String value) {
		this.latitude = filterString(value);
	}
	
	public String getLongitude() {
		return longitude;
	}
	
	public void setLongitude(String value) {
		this.longitude = filterString(value);
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
		int myId   = Integer.getInteger(id);
		int yourId = Integer.getInteger(v.getId());
		
		if(myId == yourId) {
			return 0;
		} else {
			return myId - yourId;
		}
		
	} // end compareTo method
	
} // end class definition
