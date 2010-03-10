/*
 * This file is part of the AusStage Google Analytics Report Generator
 *
 * The AusStage Google Analytics Report Generator is free software: you can 
 * redistribute it and/or modify it under the terms of the GNU General 
 * Public License as published by the Free Software Foundation, either 
 * version 3 of the License, or (at your option) any later version.
 *
 * The AusStage Google Analytics Report Generator is distributed in the hope 
 * that it will be useful, but WITHOUT ANY WARRANTY; without even the 
 * implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  
 * See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with the AusStage Google Analytics Report Generator.  
 * If not, see <http://www.gnu.org/licenses/>.
*/

// import additional libraries
import com.google.gdata.client.analytics.AnalyticsService;
import com.google.gdata.client.analytics.DataQuery;
import com.google.gdata.data.analytics.AccountEntry;
import com.google.gdata.data.analytics.AccountFeed;
import com.google.gdata.data.analytics.DataEntry;
import com.google.gdata.data.analytics.DataFeed;
import com.google.gdata.util.ServiceException;
import com.google.gdata.util.AuthenticationException;

import java.io.IOException;
import java.net.URL;
import java.net.MalformedURLException;

/**
 * A class to manage all aspects of interacting with the Google Analytics API
 */
public class AnalyticsManager {

	// declare private class variables
	private String email;
	private String password;
	private AnalyticsService service;
	private AccountFeed accountFeed;
	
	// declare private class constants
	private final String ACCOUNTS_URL     = "https://www.google.com/analytics/feeds/accounts/default";
	private final String DATA_URL         = "https://www.google.com/analytics/feeds/data";
	private final String APPLICATION_NAME = "AusStage_Analytics";
	
	/**
	 * Constructor for this class
	 *
	 * @param email    google account email address
	 * @param password google account password
	 */
	public AnalyticsManager(String email, String password) {
	
		// save the parameters
		this.email = email;
		this.password = password;
	} // end constructor
	
	/**
	 * Method to connect to the Analytics service
	 *
	 * @return true if authentication succeeded
	 */
	public boolean connect() {
	
		try {
			service = new AnalyticsService(APPLICATION_NAME);
			service.setUserCredentials(email, password);
		} catch(AuthenticationException ex) {
			System.out.println("ERROR: Authentication to Google service failed.\n" + ex.toString());
			return false;
		}
	
		return true;
	} // end connect method
	
	/**
	 * A method to get a list of available Google Analytics accounts
	 * that we can request data from
	 *
	 * @return true if the request succeeded
	 */
	public boolean getAccountList() {
	
		try {
			// build the url for the query
			URL queryUrl = new URL(ACCOUNTS_URL + "?max-results=50");
			
			// make a call to the api
			accountFeed = service.getFeed(queryUrl, AccountFeed.class);
		} catch(java.io.IOException ex) {
			System.out.println("ERROR: Unable to retrieve account information.\n" + ex.toString());
			return false;
		} catch(ServiceException ex) {
			System.out.println("ERROR: Unable to retrieve account information.\n" + ex.toString());
			return false;
		}
		
		return true;
			
	} // end getAccountList method
	
	/**
	 * A method to build a list of available Google Analytics accounts
	 * that we can request data from
	 * 
	 * @return the list of accounts ready for output
	 */
	public String buildAccountList() {
	
		// declare helper variables
		StringBuilder list = new StringBuilder("---------- Available Accounts ----------\n");
		
		// loop through all of the available accounts
		for (AccountEntry entry : accountFeed.getEntries()) {

			list.append("Account Name: " + entry.getProperty("ga:accountName") + "\n");
			list.append("Profile Name: " + entry.getTitle().getPlainText() + "\n");
			list.append("Profile ID:   " + entry.getProperty("ga:profileId") + "\n");
			list.append("Table ID:     " + entry.getTableId().getValue() + "\n\n");
		}
		
		return list.toString();
	
	} // end buildAccountList method

} // end class definition
