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
	private String           email;
	private String           password;
	private AnalyticsService service;
	private AccountFeed      accountFeed;
	private DataFeed         dataFeed;
	private String           tableId = null;
	private String           urlPath = null;
	
	// declare private class constants
	private final String ACCOUNTS_URL     = "https://www.google.com/analytics/feeds/accounts/default";
	private final String BASE_DATA_URL    = "https://www.google.com/analytics/feeds/data";
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
	 * @return this list of accounts, ready to be printed to the screen
	 */
	public String getAccountList() {
	
		try {
			// build the url for the query
			URL queryUrl = new URL(ACCOUNTS_URL + "?max-results=50");
			
			// make a call to the api
			accountFeed = service.getFeed(queryUrl, AccountFeed.class);
		} catch(java.io.IOException ex) {
			System.out.println("ERROR: Unable to retrieve account information.\n" + ex.toString());
			return null;
		} catch(ServiceException ex) {
			System.out.println("ERROR: Unable to retrieve account information.\n" + ex.toString());
			return null;
		}
		
		return this.buildAccountList();
			
	} // end getAccountList method
	
	/**
	 * A method to build a list of available Google Analytics accounts
	 * that we can request data from
	 * 
	 * @return the list of accounts ready for output
	 */
	private String buildAccountList() {
	
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
	
	/**
	 * A method to set the table id parameter
	 *
	 * @param the unique table id in the Google Analytics system
	 */ 
	public void setTableId(String id) {
		this.tableId = id;
	}
	
	/**
	 * A method to get the table id parameter
	 *
	 * @return the unique table id in the Google Analytics system
	 */
	public String getTableId() {
		return this.tableId;
	}
	
	/**
	 * A method to set the url path parameter which is used to filter analytics data
	 *
	 * @param the url path as a regular expression
	 */ 
	public void setUrlPath(String path) {
		this.urlPath = path;
	}
	
	/**
	 * A method to get the table id parameter
	 *
	 * @return the unique table id in the Google Analytics system
	 */
	public String getUrlPath() {
		return this.urlPath;
	}
	
	/**
	 * A method to get the number of visits to a part of the site
	 *
	 * @param startDate the start of the required date range
	 * @param endDate   the end of the required date range
	 *
	 * @return an array containing the data
	 */
	public String[] getVisitsForMonth(String startDate, String endDate) {
		
		// determine the number of records to expect
		int recordCount = (Integer.parseInt(endDate.replace("-", "")) - Integer.parseInt(startDate.replace("-", ""))) + 1;
		String[] visits = new String[recordCount];
		int count = 0;
		DataFeed dataFeed;
		
		try {
		
			// build the URL to request the data
			DataQuery query = new DataQuery(new URL(BASE_DATA_URL));
			
			// set the parameters
			if(this.tableId != null) {
				query.setIds(tableId);
			} else {
				System.out.println("ERROR: The table id parameter must be set before trying to retrieve data.");
				return null;
			}
			
			query.setDimensions("ga:day");
			query.setMetrics("ga:visits");
			query.setSegment("gaid::-1");
			
			if (this.urlPath != null) {
				query.setFilters("ga:pagePath=~" + this.urlPath);
			}
			
			query.setSort("ga:day");
			query.setStartDate(startDate);
			query.setEndDate(endDate);
			
			// request the data
			dataFeed = service.getFeed(query.getUrl(), DataFeed.class);
			
		} catch(java.io.IOException ex) {
			System.out.println("ERROR: Unable to retrieve visit information.\n" + ex.toString());
			return null;
		} catch(ServiceException ex) {
			System.out.println("ERROR: Unable to retrieve visit information.\n" + ex.toString());
			return null;
		}
		
		// process the results
		for (DataEntry entry : dataFeed.getEntries()) {

			visits[count] = entry.stringValueOf("ga:visits");
			count++;
		}
		
		// debug data
		return visits;

	} // end getVisitsForMonth method
	
	/**
	 * A method to get the number of visits to a part of the site
	 *
	 * @param year the start of the required date range
	 *
	 * @return an array containing the data
	 */
	public String[] getVisitsForYear(String year) {
		
		// declare helper variables
		String[] visits = new String[12];
		int count = 0;
		DataFeed dataFeed;
		
		// determine the start and end dates
		String startDate = year + "-01-01";
		String endDate   = year + "-12-31";
		
		try {
		
			// build the URL to request the data
			DataQuery query = new DataQuery(new URL(BASE_DATA_URL));
			
			// set the parameters
			if(this.tableId != null) {
				query.setIds(tableId);
			} else {
				System.out.println("ERROR: The table id parameter must be set before trying to retrieve data.");
				return null;
			}
			
			query.setDimensions("ga:month");
			query.setMetrics("ga:visits");
			query.setSegment("gaid::-1");
			
			if (this.urlPath != null) {
				query.setFilters("ga:pagePath=~" + this.urlPath);
			}
			
			query.setSort("ga:month");
			query.setStartDate(startDate);
			query.setEndDate(endDate);
			
			// request the data
			dataFeed = service.getFeed(query.getUrl(), DataFeed.class);
			
		} catch(java.io.IOException ex) {
			System.out.println("ERROR: Unable to retrieve visit information.\n" + ex.toString());
			return null;
		} catch(ServiceException ex) {
			System.out.println("ERROR: Unable to retrieve visit information.\n" + ex.toString());
			return null;
		}
		
		// process the results
		for (DataEntry entry : dataFeed.getEntries()) {

			visits[count] = entry.stringValueOf("ga:visits");
			count++;
		}
		
		// debug data
		return visits;

	} // end getVisitsForYear method

} // end class definition
