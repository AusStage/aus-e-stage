/*
 * This file is part of the AusStage Website Analytics app
 *
 * The AusStage Website Analytics app is free software: you can redistribute
 * it and/or modify it under the terms of the GNU General Public License 
 * as published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * The AusStage Website Analytics app is distributed in the hope that it will 
 * be useful, but WITHOUT ANY WARRANTY; without even the implied warranty 
 * of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with the AAusStage Website Analytics app.  
 * If not, see <http://www.gnu.org/licenses/>.
*/

package au.edu.ausstage.websiteanalytics.types;

// import additional ausstage packages
import au.edu.ausstage.utils.*;

/**
 * A class to store details of page views for a set period
 */
public class PageView implements Comparable<PageView>{

	// private class variables
	public String  date  = null;
	public Long    views = null;
	
	/**
	 * Constructor for this class
	 *
	 * @param date  the date being tracked
	 * @param views the number of views
	 */
	public PageView(String date, Long views) {
		
		// check on the parameters
		if(InputUtils.isValid(date) == false) {
			throw new IllegalArgumentException("The date parameter must be a valid String object");
		}
		
		if(views == null) {
			throw new IllegalArgumentException("The views parameter must be a valid Long object");
		}
		
		// store the values
		
		// process the date
		if(date.length() == 8) {
			String year  = date.substring(0, 4);
			String month = date.substring(4, 6);
			String day   = date.substring(6);
			
			this.date  = year + "-" + month + "-" + day;
		} else {
			this.date = date;
		}
		
		this.views = views;
	}
	
	/*
	 * get and set methods
	 */
	/**
	 * A method to return the date
	 * 
	 * @return the date
	 */
	public String getDate() {
		return date;
	}
	
	/**
	 * A method to set the date
	 * 
	 * @param value the new value
	 */
	public void setDate(String value) {
		if(InputUtils.isValid(date) == false) {
			throw new IllegalArgumentException("The date parameter must be a valid String object");
		}
	
		date = value;
	}
	
	/**
	 * A method to return the number of views
	 *
	 * @return the number of views
	 */
	public Long getViews() {
		return views;
	}
	
	/**
	 * A method to set the number of views
	 *
	 * @param value, the new value
	 */
	public void setViews(Long value) {
		if(views == null) {
			throw new IllegalArgumentException("The views parameter must be a valid Long object");
		}
		
		views = value;
	}	
	
	/*
	 * override the toString method
	 */
	public String toString() {
		return date + " - " + views;
	}
	
	/*
	 * methods required for ordering in collections
	 * http://java.sun.com/docs/books/tutorial/collections/interfaces/order.html
	 */

	/**
	 * A method to determine if one object is the same as the other
	 *
	 * @param o the object to compare this one to
	 *
	 * @return  true if they are equal, false if they are not
	 */
	public boolean equals(Object o) {
		// check to make sure the object is of the right type
		if ((o instanceof PageView) == false) {
			// o is not of the right type
		 	return false;
		}
		
		// compare these two objects
		PageView p = (PageView)o;
		
		return date.equals(p.getDate());
		
	} // end equals method
	
	/**
	 * Overide the default hashcode method
	 * 
	 * @return a hashcode for this object
	 */
	public int hashCode() {
		return 31*date.hashCode();
	}
    
    /**
     * The compareTo method compares the receiving object with the specified object and returns a 
     * negative Long, 0, or a positive Long depending on whether the receiving object is 
     * less than, equal to, or greater than the specified object.
     *
     * @param o the object to compare this one to
     *
     * @return  an Long indicating comparison result
     */    
	public int compareTo(PageView o) {
	
		int me  = DateUtils.getIntegerFromDate(date);
		int you = DateUtils.getIntegerFromDate(o.date);
				
		if(me == you) {
			return 0;
		} else {
			return me - you;
		}
		
	} // end compareTo method
}
