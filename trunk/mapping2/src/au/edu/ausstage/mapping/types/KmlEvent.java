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

/**
 * class used to represent an event when constructing KML data for download
 */
public class KmlEvent implements Comparable<KmlEvent> {

	// private variables
	private String id   = null;
	private String name = null;
	private String url  = null;
	private String firstDate = null;
	private String sortFirstDate = null;
	private String sortLastDate = null;
	private String venueName = null;
	private String venueAddress = null;
	private String shortVenueAddress = null;
	private String venueUrl = null;
	private String latitude = null;
	private String longitude = null;
	
	/**
	 * constructor for this class
	 * 
	 * @param id the unique event id
	 
	 * @throws IllegalArgumentException if any of the parameters are null or empty strings
	 *
	 */
	public KmlEvent(String id) {
		if(InputUtils.isValid(id) == false) {
			throw new IllegalArgumentException("the id parameter is required");
		}
		
		this.id = id;
	}
	
	public String getId() {
		return id;
	}
	
	public String getName() {
		return name;
	}
	
	public String getUrl() {
		return url;
	}
	
	public String getAtomLink() {
	
		return url.replace("&amp;", "&");
	}
	
	public String getVenueUrl() {
		return venueUrl;
	}
	
	public String getFirstDate() {
		return firstDate;
	}
	
	public String getSortFirstDate() {
		return sortFirstDate;
	}
	
	public String getSortLastDate() {
		return sortLastDate;
	}
	
	public String getVenueName() {
		return venueName;
	}
	
	public String getVenueAddress() {
		return venueAddress;
	}
	
	public String getShortVenueAddress() {
		return shortVenueAddress;
	}
	
	public String getLatitude() {
		return latitude;
	}
	
	public String getLongitude() {
		return longitude;
	}
	
	public void setId(String value) {
		if(InputUtils.isValid(value) == false) {
			throw new IllegalArgumentException("value cannot be a null or empty string");
		} else {
			this.id = value;
		}
	}
	
	public void setName(String value) {
		if(InputUtils.isValid(value) == false) {
			throw new IllegalArgumentException("value cannot be a null or empty string");
		} else {
			this.name = value;
		}
	}
	
	public void setUrl(String value) {
		if(InputUtils.isValid(value) == false) {
			throw new IllegalArgumentException("value cannot be a null or empty string");
		} else {
			this.url = value;
		}
	}
	
	public void setVenueUrl(String value) {
		if(InputUtils.isValid(value) == false) {
			throw new IllegalArgumentException("value cannot be a null or empty string");
		} else {
			this.venueUrl = value;
		}
	}
	
	public void setFirstDate(String value) {
		if(InputUtils.isValid(value) == false) {
			throw new IllegalArgumentException("value cannot be a null or empty string");
		} else {
			this.firstDate = value;
		}
	}
	
	public void setSortFirstDate(String value) {
		if(InputUtils.isValid(value) == false) {
			throw new IllegalArgumentException("value cannot be a null or empty string");
		} else {
			this.sortFirstDate = value;
		}
	}
	
	public void setSortLastDate(String value) {
		if(InputUtils.isValid(value) == false) {
			throw new IllegalArgumentException("value cannot be a null or empty string");
		} else {
			this.sortLastDate = value;
		}
	}
	
	public void setVenueName(String value) {
		if(InputUtils.isValid(value) == false) {
			throw new IllegalArgumentException("value cannot be a null or empty string");
		} else {
			this.venueName = value;
		}
	}
	
	public void setVenueAddress(String value) {
		if(InputUtils.isValid(value) == false) {
			throw new IllegalArgumentException("value cannot be a null or empty string");
		} else {
			this.venueAddress = value;
		}
	}
	
	public void setShortVenueAddress(String value) {
		if(InputUtils.isValid(value) == false) {
			throw new IllegalArgumentException("value cannot be a null or empty string");
		} else {
			this.shortVenueAddress = value;
		}
	}
	
	public void setLatitude(String value) {
		if(InputUtils.isValid(value) == false) {
			throw new IllegalArgumentException("value cannot be a null or empty string");
		} else {
			this.latitude = value;
		}
	}
	
	public void setLongitude(String value) {
		if(InputUtils.isValid(value) == false) {
			throw new IllegalArgumentException("value cannot be a null or empty string");
		} else {
			this.longitude = value;
		}
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
		if ((o instanceof KmlEvent) == false) {
			// o is not an event object
		 	return false;
		}
		
		// compare these two events
		KmlEvent e = (KmlEvent)o;
		
		return id.equals(e.getId());
		
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
     * @param e the event to compare this one to
     *
     * @return a integer indicating comparison result
     */    
	public int compareTo(KmlEvent e) {
		int myId   = Integer.parseInt(id);
		int yourId = Integer.parseInt(e.getId());
		
		if(myId == yourId) {
			return 0;
		} else {
			return myId - yourId;
		}
		
	} // end compareTo method
}
