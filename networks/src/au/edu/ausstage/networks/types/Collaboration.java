/*
 * This file is part of the AusStage Navigating Networks Service
 *
 * The AusStage Navigating Networks Service is free software: you can redistribute
 * it and/or modify it under the terms of the GNU General private License 
 * as published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * The AusStage Navigating Networks Service is distributed in the hope that it will 
 * be useful, but WITHOUT ANY WARRANTY; without even the implied warranty 
 * of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General private License for more details.
 *
 * You should have received a copy of the GNU General private License
 * along with the AusStage Navigating Networks Service.  
 * If not, see <http://www.gnu.org/licenses/>.
*/

package au.edu.ausstage.networks.types;

// import additional classes
import au.edu.ausstage.utils.InputUtils;

/**
 * A class to represent a Collaboration
 */
public class Collaboration implements Comparable<Collaboration>{

	// declare private class level variables
	private String  collaborator = null;
	private String  partner      = null;
	private Integer count        = null;
	private String  firstDate    = null;
	private String  lastDate     = null;
	
	/**
	 * Constuctor for this class
	 *
	 * @param collaborator the first collaborator in this collaboration
	 * @param partner      the second collaborator in this collaboration
	 * @param count        the number of times this collaboration has occured
	 * @param firstDate    the first date of a collaboration between these two collaborators
	 * @param lastDate     the last date of a collaboration between these two collaborators
	 */
	public Collaboration(String collaborator, String partner, Integer count, String firstDate, String lastDate) {
	
		// check the input parameters
		if(InputUtils.isValid(collaborator) == false || InputUtils.isValid(partner) == false || count == null || InputUtils.isValid(firstDate) == false || InputUtils.isValid(lastDate) == false) {
			throw new IllegalArgumentException("All parameters to this constructor are required");
		}
		
		if(InputUtils.isValidInt(collaborator) == false || InputUtils.isValidInt(partner) == false) {
			throw new IllegalArgumentException("The collaborator and partner parameters must be valid integers");
		}
		
		// assign parameters to class variables
		this.collaborator = collaborator;
		this.partner      = partner;
		this.count        = count;
		this.firstDate    = firstDate;
		this.lastDate     = lastDate;	
	
	} // end constructor
	
	/*
	 * get and set methods
	 */
	/**
	 * A method to get the collaborator in this collaboration
	 *
	 * @return the collaborator id
	 */
	public String getCollaborator() {
		return collaborator;
	}
	
	/**
	 * A method to set the collaborator in this collaboration
	 *
	 * @param value the new collaborator id
	 */
	public void setCollaborator(String value) {
		if(InputUtils.isValidInt(value) == false) {
			throw new IllegalArgumentException("The value parameter must be a valid integer");
		} else {
			collaborator = value;
		}
	}
	
	/**
	 * A method to get the partner in this collaboration
	 *
	 * @return the partner id
	 */
	public String getPartner() {
		return partner;
	}
	
	/**
	 * A method to set the partner in this collaboration
	 *
	 * @param value the new partner id
	 */
	public void setPartner(String value) {
		if(InputUtils.isValidInt(value) == false) {
			throw new IllegalArgumentException("The value parameter must be a valid integer");
		} else {
			partner = value;
		}
	}
	
	/**
	 * A method to get the collaboration count
	 *
	 * @return the collaboration count
	 */
	public Integer getCollaborationCount() {
		return count;
	}
	
	/**
	 * A method to set the collaboration count
	 *
	 * @param value the new collaboration count
	 */
	public void setCollaborationCount(Integer value) {
		if(value == null) {
			throw new IllegalArgumentException("The value parameter must not be null");
		} else {
			count = value;
		}
	}	
	
	/**
	 * A method to get the first date of this collaboration
	 *
	 * @return the first date
	 */
	public String getFirstDate() {
		return firstDate;
	}
	
	/**
	 * A method to set the first Date of this collaborator
	 *
	 * @param value the new date value
	 */
	public void setFirstDate(String value) {
		if(InputUtils.isValid(value) == false) {
			throw new IllegalArgumentException("The value parameter must not be null");
		} else {
			firstDate = value;
		}
	}
	
	/**
	 * A method to get the last date of this collaboration
	 *
	 * @return the first date
	 */
	public String getLastDate() {
		return lastDate;
	}
	
	/**
	 * A method to set the first Date of this collaborator
	 *
	 * @param value the new date value
	 */
	public void setlastDate(String value) {
		lastDate = value;
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
		if ((o instanceof Collaboration) == false) {
			// o is not an event object
		 	return false;
		}
		
		// compare these two objects
		Collaboration c = (Collaboration)o;
		
		String tmp = collaborator + partner;
		
		return tmp.equals(c.getCollaborator() + c.getPartner());
		
	} // end equals method
	
	/**
	 * Overide the default hashcode method
	 * 
	 * @return a hashcode for this object
	 */
	public int hashCode() {
		String tmp = collaborator + partner;
		return 31*tmp.hashCode();
	}
    
    /**
     * The compareTo method compares the receiving object with the specified object and returns a 
     * negative integer, 0, or a positive integer depending on whether the receiving object is 
     * less than, equal to, or greater than the specified object.
     *
     * @param c the event to compare this one to
     *
     * @return  an integer indicating comparison result
     */    
	public int compareTo(Collaboration c) {
		
		long myCompare   = Long.parseLong(collaborator + partner);
		long yourCompare = Long.parseLong(c.getCollaborator() + c.getPartner());
		
		if(myCompare == yourCompare) {
			return 0;
		} else {
			return (int)myCompare - (int)yourCompare;
		}
		
	} // end compareTo method
	
} // end the class definition
