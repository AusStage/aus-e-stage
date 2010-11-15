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

// import additional libraries
import java.util.Set;
import java.util.HashSet;
import java.util.TreeSet;
import java.util.Iterator;

/**
 * A class to store a number of PageView objects
 */
public class PageViews {

	// declare public constants
	
	/**
	 * Sort PageView objects by date
	 */
	 public final static int DATE_SORT = 0;
	 
	 /**
	  * Sort PageView objects by number of views
	  */
	 public final static int VIEWS_SORT = 1;
	 
	 // declare private class level variables
	 private Set<PageView> pageViews;
	 
	/**
	 * A constructor for this class
	 */
	public PageViews() {
		pageViews = new HashSet<PageView>();
	}
	
	/*
	 * page view management
	 */
	 
	/**
	 * A method to add a pageView object to the list
	 *
	 * @param pageView a valid PageView object
	 */
	public void add(PageView pageView) {
		
		if(pageView == null) {
			throw new IllegalArgumentException("The pageView parameter cannot be null");
		}
		
		pageViews.add(pageView);
	}
	 
	/**
	 * A method to add a pageView object to the list
	 *
	 * @param pageView a valid PageView object
	 */
	public void addPageView(PageView pageView) {
		
		if(pageView == null) {
			throw new IllegalArgumentException("The pageView parameter cannot be null");
		}
		
		pageViews.add(pageView);
	}
	
	/**
	 * A method to get the list of pageViews
	 *
	 * @return the list of pageViews
	 */
	public Set<PageView> getPageViews() {
		return pageViews;
	}
	
	/**
	 * A method to get the list of pageView objects in an array
	 *
	 * @return the list of PageView objects in an array
	 */
	public PageView[] getPageViewsAsArray() {
		return pageViews.toArray(new PageView[0]);
	}
	
	/**
	 * A method to return a list of sorted values
	 *
	 * @param sortType the type of sort algorithm to use
	 *
	 * @return a list of PageView objects sorted by the method indicated
	 */
	public Set<PageView> getSortedPageViews(int sortType) {
	
		Set<PageView> sortedPageViews;
		
		// check on the parameter
		if(sortType == DATE_SORT) {
			// sort by dates
			sortedPageViews = new TreeSet<PageView>(pageViews);
		} else if(sortType == VIEWS_SORT) {
			// sort by views
			sortedPageViews = new TreeSet<PageView>(new PageViewsComparator());
			sortedPageViews.addAll(pageViews);
		} else {
			throw new IllegalArgumentException("Unknown sort type specified");
		}
		
		// return the list of pageView objects
		return sortedPageViews;
	}
}
