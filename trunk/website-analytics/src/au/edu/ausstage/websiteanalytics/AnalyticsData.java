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

package au.edu.ausstage.websiteanalytics;

// import additional ausstage packages
import au.edu.ausstage.utils.*;
import au.edu.ausstage.websiteanalytics.types.*;

// import additional Google Analytics classes;
import com.google.gdata.client.analytics.AnalyticsService;
import com.google.gdata.client.analytics.DataQuery;
import com.google.gdata.data.analytics.AccountEntry;
import com.google.gdata.data.analytics.AccountFeed;
import com.google.gdata.data.analytics.DataEntry;
import com.google.gdata.data.analytics.DataFeed;

import com.google.gdata.util.AuthenticationException;
import com.google.gdata.util.ServiceException;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * A class used to gather analytics data
 */
public class AnalyticsData {

	// private class variables
	AnalyticsService analyticsService = null;
	private String lastError = null;
	
	/**
	 * Constructor for this class
	 *
	 * @param analyticsService a valid AnalyticsService object from the AccountData class
	 */
	public AnalyticsData(AnalyticsService analyticsService) {
	
		// check on the parameter
		if(analyticsService == null) {
			throw new IllegalArgumentException("The parmeter must be a valid object");
		}
		
		this.analyticsService = analyticsService;	
	}
	
	/**
	 * A method to get vists for a period, by day, matching a specific url pattern
	 *
	 * @param tableId    the tableId as listed by the AccountData.getUserDetails() method
	 * @param startDate  the start of the period. Assumes the format yyyy-mm-dd where - is a delimiter
	 * @param endDate    the end of the period. Assumes the format yyyy-mm-dd where - is a delimiter
	 * @param urlPattern the URL pattern to match against
	 *
	 * @return the retrieved data or null on error
	 */
	public PageViews getVisitsByDateAndPattern(String tableId, String startDate, String endDate, String urlPattern) {
	
		// check on the parameters
		if(InputUtils.isValid(tableId) == false || InputUtils.isValid(startDate) == false || InputUtils.isValid(endDate) == false || InputUtils.isValid(urlPattern) == false) {
			throw new IllegalArgumentException("All of the parameters for this method are required");
		}
		
		// reset the last Error variable
		lastError = null;
		
		// declare other helper variables
		PageViews pageViews = new PageViews();
		
		try {
		
			// define a new Data Query object
			DataQuery query = new DataQuery(new URL("https://www.google.com/analytics/feeds/data"));
		
			// set the parameters
			query.setStartDate(startDate);
			query.setEndDate(endDate);
			query.setDimensions("ga:date");
			query.setMetrics("ga:visits");
			query.setSort("ga:date");
			//query.setMaxResults();
			query.setIds(tableId);
			query.setFilters("ga:pagePath=~" + urlPattern);
		
			// get the data
			DataFeed dataFeed = analyticsService.getFeed(query.getUrl(), DataFeed.class);
			
			// loop through and process the results
			for (DataEntry entry : dataFeed.getEntries()) {
				
				// declare a new pageView variable
				pageViews.add(new PageView(entry.stringValueOf("ga:date"), entry.longValueOf("ga:visits")));
			}				

		} catch (java.net.MalformedURLException ex) {
			lastError = "Query URL construction error: " + ex.toString();
			return null;
		} catch (java.io.IOException ex) {
			lastError = "Network error: " + ex.toString();
			return null;
		} catch (com.google.gdata.util.ServiceException ex) {
			lastError = "Google Data error: " + ex.toString();
			return null;
		}
		
		// if we get this far then everything worked as expected
		return pageViews;	
	}
	
	/**
	 * A method to get vists for a month, by day, matching a specific url pattern
	 *
	 * @param tableId    the tableId as listed by the AccountData.getUserDetails() method
	 * @param year       the year that is of interest
	 * @param month      the month that is of interest
	 * @param urlPattern the URL pattern to match against
	 *
	 * @return the retrieved data or null on error
	 */
	public PageViews getVisitsForMonthByDayAndPattern(String tableId, String year, String month, String urlPattern) {
		
		// check on the parameters
		if(InputUtils.isValid(tableId) == false || InputUtils.isValid(year) == false || InputUtils.isValid(month) == false || InputUtils.isValid(urlPattern) == false) {
			throw new IllegalArgumentException("All of the parameters for this method are required");
		}
		
		// determine the start and end years
		String startDate = year + "-" + month + "-01";
		String endDate   = year + "-" + month + "-" + DateUtils.getLastDay(year, month);
		
		return getVisitsByDateAndPattern(tableId, startDate, endDate, urlPattern);
	}
	
	/**
	 * A method to get visits for a year, by month, matching a specific url pattern
	 *
	 * @param tableId    the tableId as listed by the AccountData.getUserDetails() method
	 * @param year       the year that is of interest
	 * @param urlPattern the URL pattern to match against
	 *
	 * @return the retrieved data or null on error
	 */
	public PageViews getVisitsForYearByMonthAndPattern(String tableId, String year, String urlPattern) {
	
		// check on the parameters
		if(InputUtils.isValid(tableId) == false || InputUtils.isValid(year) == false || InputUtils.isValid(urlPattern) == false) {
			throw new IllegalArgumentException("All of the parameters for this method are required");
		}
		
		// reset the last Error variable
		lastError = null;
		
		// declare other helper variables
		PageViews pageViews = new PageViews();
		String startDate = year + "-01-01";
		String endDate   = year + "-12-31";
		
		try {
		
			// define a new Data Query object
			DataQuery query = new DataQuery(new URL("https://www.google.com/analytics/feeds/data"));
		
			// set the parameters
			query.setStartDate(startDate);
			query.setEndDate(endDate);
			query.setDimensions("ga:month");
			query.setMetrics("ga:visits");
			query.setSort("ga:month");
			//query.setMaxResults();
			query.setIds(tableId);
			query.setFilters("ga:pagePath=~" + urlPattern);
		
			// get the data
			DataFeed dataFeed = analyticsService.getFeed(query.getUrl(), DataFeed.class);
			
			// loop through and process the results
			for (DataEntry entry : dataFeed.getEntries()) {
				
				// declare a new pageView variable
				pageViews.add(new PageView(entry.stringValueOf("ga:month"), entry.longValueOf("ga:visits")));
			}				

		} catch (java.net.MalformedURLException ex) {
			lastError = "Query URL construction error: " + ex.toString();
			return null;
		} catch (java.io.IOException ex) {
			lastError = "Network error: " + ex.toString();
			return null;
		} catch (com.google.gdata.util.ServiceException ex) {
			lastError = "Google Data error: " + ex.toString();
			return null;
		}
		
		// if we get this far then everything worked as expected
		return pageViews;	
	}
		
	
	/**
	 * A method to get the error message if an error occures
	 *
	 * @return the string of the last error message
	 */
	public String getLastError() {
		return lastError;
	}


}
