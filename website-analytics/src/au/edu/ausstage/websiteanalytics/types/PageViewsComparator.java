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
 * A class to implement different comparators in order to sort lists of PageView objects
 */
public class PageViewsComparator implements java.util.Comparator<PageView> {

	/**
	 * Compare two PageView objects sorting number of views
	 *
	 * @param first  a PageView object for comparison
	 * @param second a PageView object for comparison
	 *
	 * @return the result of the comparison
	 */
	public int compare(PageView first, PageView second) {
	
		/*
		 * have to use more than just the name as so many venues have the same name :(
		 */
		 
		Long firstValue  = first.getViews();
		Long secondValue = second.getViews();
		
		if(firstValue == secondValue) {
			secondValue = secondValue + 1;
		}
		
		return firstValue.compareTo(secondValue);
	
	} // end compare method

} // end class definition
