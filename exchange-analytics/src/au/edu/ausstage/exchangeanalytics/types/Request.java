/* This file is part of the AusStage Exchange Analytics app
 *
 * The AusStage Exchange Analytics app is free software: you can redistribute
 * it and/or modify it under the terms of the GNU General Public License 
 * as published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * The AusStage Exchange Analytics app is distributed in the hope that it will 
 * be useful, but WITHOUT ANY WARRANTY; without even the implied warranty 
 * of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with the AusStage Exchange Analytics app.  
 * If not, see <http://www.gnu.org/licenses/>.
 */
 
package au.edu.ausstage.exchangeanalytics.types;

import au.edu.ausstage.utils.InputUtils;

/**
 * a class used to represent a request to the Data Exchange service
 */
public class Request implements Comparable<Request> {

	// private class level variables
	String timestamp;
	String requestType;
	String id;
	String outputType;
	String recordLimit;
	String inetAddress;
	String referer;
	
	/**
	 * constructor for this class
	 *
	 * @param timestamp the time at which this request was made
	 *
	 * @throws IllegalArgumentException if the timestamp is empty
	 */
	public Request(String timestamp) {
	
		if(InputUtils.isValid(timestamp) == false) {
			throw new IllegalArgumentException("the timestamp parameter is required");
		}
		
		this.timestamp = timestamp;
	}
	
	/* get and set methods */
	public String getTimestamp() {
		return timestamp;
	}
	
	public void setRequestType(String value) {
		if(InputUtils.isValid(value) == false) {
			throw new IllegalArgumentException("the value parameter cannot be null or an empty string");
		}
		
		requestType = value;
	}
	
	public String getRequestType() {
		return requestType;
	}
	
	public void setId(String value) {
		if(InputUtils.isValid(value) == false) {
			throw new IllegalArgumentException("the value parameter cannot be null or an empty string");
		}
		
		id = value;
	}
	
	public String getId() {
		return id;
	}
	
	public void setOutputType(String value) {
		if(InputUtils.isValid(value) == false) {
			throw new IllegalArgumentException("the value parameter cannot be null or an empty string");
		}
		
		outputType = value;
	}
	
	public String getOutputType() {
		return outputType;
	}
	
	public void setRecordLimit(String value) {
		if(InputUtils.isValid(value) == false) {
			throw new IllegalArgumentException("the value parameter cannot be null or an empty string");
		}
		
		recordLimit = value;
	}
	
	public String getRecordLimit() {
		return recordLimit;
	}
	
	public void setInetAddress(String value) {
		if(InputUtils.isValid(value) == false) {
			throw new IllegalArgumentException("the value parameter cannot be null or an empty string");
		}
		
		inetAddress = value;
	}
	
	public String getInetAddress() {
		return inetAddress;
	}
	
	public void setReferer(String value) {
		if(InputUtils.isValid(value) == false) {
			throw new IllegalArgumentException("the value parameter cannot be null or an empty string");
		}
		
		referer = value;
	}
	
	public String getReferer() {
		return referer;
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
		if ((o instanceof Request) == false) {
			// o is not an event object
		 	return false;
		}
		
		// compare these two events
		Request r = (Request)o;
		
		return timestamp.equals(r.getTimestamp());
		
	} // end equals method
	
	/**
	 * Overide the default hashcode method
	 * 
	 * @return a hashcode for this object
	 */
	public int hashCode() {
		return 31*timestamp.hashCode();
	}
    
    /**
     * The compareTo method compares the receiving object with the specified object and returns a 
     * negative integer, 0, or a positive integer depending on whether the receiving object is 
     * less than, equal to, or greater than the specified object.
     *
     * @param e the event to compare this one to
     *
     * @return a integer indicating comparison result
     */    
	public int compareTo(Request r) {
		String myStamp   = timestamp;
		String yourStamp = r.getTimestamp();
		
		return myStamp.compareTo(yourStamp);
		
	} // end compareTo method
}
