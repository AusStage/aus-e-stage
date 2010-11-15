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
 * A class used to manage access to account data,
 * including authentication and listing which profiles and tables can be accessed
 */
public class AccountData {

	/*
	 * private class level variables
	 */
	private String           lastError        = null;
	private AnalyticsService analyticsService = null;
	private AccountFeed      accountFeed      = null;

	/**
	 * Authenticate to the Google Analytics Service
	 * using the supplied credentials
	 *
	 * @param username the username used for authentication
	 * @param password the password used for authentication
	 *
	 * @return         true, if an only if, authentication is successful
	 */
	public boolean authenticateUser(String username, String password) {
	
		// check on the parameters
		if(InputUtils.isValid(username) == false || InputUtils.isValid(password) == false) {
			throw new IllegalArgumentException("All parameters to this method are required");
		}
		
		// reset the lastError variable
		lastError = null;
		
		try{					
			// Service Object to work with the Google Analytics Data Export API.
			analyticsService = new AnalyticsService(WebsiteAnalytics.APP_NAME.replace(" ", "-") + "-" + WebsiteAnalytics.VERSION);
			
			// authenticate using the credentials
			analyticsService.setUserCredentials(username, password);
		} catch (com.google.gdata.util.AuthenticationException ex) {
			lastError = "Authentication Error: " + ex.toString();
			analyticsService = null;
			return false;
		}

		// if we get this far authentication worked
		return true;	
	} 
	
	/**
	 * A method to get the details of the user
	 *
	 * @return a string containing details of the user
	 */
	public String getUserDetails() {
	
		// check to make sure the analytics Service object is valid
		if(analyticsService == null) {
			throw new RuntimeException("The authenticateUser() method must be called before this method");
		}
		
		// declare a variable to hold the data
		StringBuilder userDetails = new StringBuilder();
		
		// build the URL to request the data
		try {
			// build the query URL
			URL queryUrl = new URL("https://www.google.com/analytics/feeds/accounts/default?max-results=50");
			
			// get the account data
			accountFeed = analyticsService.getFeed(queryUrl, AccountFeed.class);
			
			// build the data
			for (AccountEntry entry : accountFeed.getEntries()) {
			
				userDetails.append("\nAccount Name  = " + entry.getProperty("ga:accountName"));
				userDetails.append("\nProfile Name  = " + entry.getTitle().getPlainText());
				userDetails.append("\nProfile Id    = " + entry.getProperty("ga:profileId"));
				userDetails.append("\nTable Id      = " + entry.getTableId().getValue());
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
		
		// debug code
		return userDetails.toString();
	}
	
	/**
	 * A method to get the Analytics Service object
	 *
	 * @return the Analytics Service object
	 */
	public AnalyticsService getAnalyticsService() {
		return analyticsService;
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
