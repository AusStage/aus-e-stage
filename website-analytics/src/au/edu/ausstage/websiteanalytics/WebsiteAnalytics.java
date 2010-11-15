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

// import additional java libraries
import java.util.ArrayList;
/**
 * Main driving class for the WebsiteAnalytics app
 */
public class WebsiteAnalytics {

	// Version information 
	public  static final String VERSION    = "1.0.0";
	private static final String BUILD_DATE = "2010-11-15";
	private static final String INFO_URL   = "http://code.google.com/p/aus-e-stage/wiki/WebsiteAnalytics";
	public  static final String APP_NAME   = "AusStage Website Analytics";
	
	// private helper constants
	private static final String[] REQD_PROPERTIES = {"config-dir", "username", "password"};
	private static final String[] TASK_TYPES      = {"account-data", "build-reports"};
	
	/**
	 * Main driving method for the  WebsiteAnalytics app
	 */
	public static void main(String args[]) {
	
		// output some basic information
		System.out.println(APP_NAME + " - Gather info from Google Analytics");
		System.out.println("Version: " + VERSION + " Build Date: " + BUILD_DATE);
		System.out.println("More Info: " + INFO_URL + "\n");
		
		// output the date 	
	 	System.out.println("INFO: Application Started: " + DateUtils.getCurrentDateAndTime());
	 	
	 	// get the command line arguments
	 	CommandLineParser cli = new CommandLineParser(args);
	 	
	 	// process the list of arguments
	 	String propsPath = cli.getValue("properties");
	 	String taskType  = cli.getValue("task");
	 	
	 	// double check the parameter
	 	if(InputUtils.isValid(propsPath) == false || InputUtils.isValid(taskType) == false) {
	 		System.err.println("ERROR: the following parameters are expected:");
	 		System.err.println("       -task       the task to perform");
	 		System.err.println("       -properties the location of the properties file");
	 		System.err.println("Valid task types are:");
	 		System.err.println(InputUtils.arrayToString(TASK_TYPES));
	 		System.exit(-1); 		
	 	}
	 	
	 	// double check the task parameter
	 	if(InputUtils.isValid(taskType, TASK_TYPES) == false) {
	 		System.err.println("ERROR: invalid task type specified, expected on of:");
	 		System.err.println(InputUtils.arrayToString(TASK_TYPES));
	 		System.exit(-1); 		
	 	}
	 	
	 	// load the properties
	 	PropertiesManager properties = new PropertiesManager();
	 	
	 	if(properties.loadFile(propsPath) == false) {
	 		System.err.println("ERROR: unable to open the specified properties file");
	 		System.exit(-1);
	 	}
	 	
	 	// check on the properties
	 	for(int i = 0; i < REQD_PROPERTIES.length; i++) {
	 		if(InputUtils.isValid(properties.getValue(REQD_PROPERTIES[i])) == false) {
	 			System.err.println("ERROR: unable to read the '" + REQD_PROPERTIES[i] + "' property");
	 			System.exit(-1);
	 		}
	 	}
	 	
	 	// check the config directory if required
	 	String configPath = null;
	 	
	 	if(taskType.equals("build-reports") == true) {
	 		// get the path to the config files
		 	configPath = properties.getValue("config-dir");
		 	
		 	if(FileUtils.doesDirExist(configPath) == false) {
		 		System.err.println("ERROR: unable to find the config directory");
		 		System.exit(-1);
		 	}
		 	
		 	// get a list of config files
		 	String[] configFiles = FileUtils.getFileNameListByExtension(configPath, ".xml");
		 	
		 	if(configFiles.length == 0) {
		 		System.err.println("ERROR: unable to find any '.xml' files in the config directory");
		 		System.exit(-1);
		 	}
		 	
		 	// parse each of the config files
		 	ArrayList<ReportConfig> reportConfigs = new ArrayList<ReportConfig>();
		 	ReportConfig      reportConfig        = null;
		 	ConfigReader      configReader        = new ConfigReader();
		 	
		 	for(int i = 0; i < configFiles.length; i++) {
		 		reportConfig = configReader.parseConfigFile(configFiles[i]);
		 		
		 		if(reportConfig == null) {
		 			System.err.println("ERROR: Unable to parse the config file at:\n" + configFiles[i]);
		 			System.err.println("Details: " + configReader.getLastError());
		 			System.exit(-1);
		 		} else {
		 			reportConfigs.add(reportConfig);
		 		}
		 	}
		 	
		 	//debug code
		 	for(int i = 0; i < reportConfigs.size(); i++) {
		 		reportConfig = reportConfigs.get(i);
		 		
		 		System.out.println("Report Config:");
		 		System.out.println("  table-id:    " + reportConfig.getTableId());
		 		System.out.println("  url-pattern: " + reportConfig.getUrlPattern());
		 		System.out.println("  title:       " + reportConfig.getReportTitle());
		 		System.out.println("Description:");
		 		System.out.println("  title: " + reportConfig.getDescriptionTitle());
		 		System.out.println("  text:  " + reportConfig.getDescription());
		 		System.out.println("Sections: ");
		 		System.out.println("  doCurrentMonth:  " + reportConfig.doCurrentMonthSection());
		 		System.out.println("  doPreviousMonth: " + reportConfig.doPreviousMonthSection());
		 		System.out.println("  doCurrentYear:   " + reportConfig.doCurrentYearSection());
		 		System.out.println("  doPreviousYear:  " + reportConfig.doPreviousYearSection());
		 	}
		 	

//			// authenticate using the supplied credentials
//			AccountData accountData = new AccountData();
//			
//			// Keep the user informed
//			System.out.println("INFO: Attempting Authentication to Google Analytics");
//			
//			if(accountData.authenticateUser(properties.getValue("username"), properties.getValue("password")) == false) {
//				System.err.println("ERROR: An unexpected error has occured");
//				System.err.println(accountData.getLastError());
//				System.exit(-1);
//			} else {
//				System.out.println("INFO: Authentication Successful");
//			}
//			
//			// debug code
//			// instantiate the Analytics data class
//			AnalyticsData analyticsData = new AnalyticsData(accountData.getAnalyticsService());
//			
//			// visits for a period by day
//			//PageViews pageViews = analyticsData.getVisitsByDateAndPattern("ga:25792128", "2010-11-01", "2010-11-15", "^/networks/");
//			
//			// visits for a month by day
//			//PageViews pageViews  = analyticsData.getVisitsForMonthByDayAndPattern("ga:25792128", "2010", "11", "^/networks/");
//			PageViews pageViews = analyticsData.getVisitsForYearByMonthAndPattern("ga:25792128", "2010", "^/networks/");
//			
//			java.util.Set<PageView> sortedPageViews = pageViews.getSortedPageViews(PageViews.DATE_SORT);
//			PageView[] views = sortedPageViews.toArray(new PageView[0]);
//			
//			for(int i = 0; i < views.length; i++) {
//				System.out.println(views[i]);
//			}
		 	
		} else if(taskType.equals("account-data") == true) {
			// get the account data for this user
			AccountData data = new AccountData();
			
			// Keep the user informed
			System.out.println("INFO: Attempting Authentication to Google Analytics");
			
			if(data.authenticateUser(properties.getValue("username"), properties.getValue("password")) == false) {
				System.err.println("ERROR: An unexpected error has occured");
				System.err.println(data.getLastError());
				System.exit(-1);
			} else {
				System.out.println("INFO: Authentication Successful");
			}
			
			String userDetails = data.getUserDetails();
			System.out.println("INFO: Account Data");
			
			if(userDetails == null) {
				System.err.println("ERROR: An unexpected error has occured");
				System.err.println(data.getLastError());
				System.exit(-1);
			} else {
				System.out.println(userDetails);
			}
		}	
	}
}
