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

// import additional packages
import java.io.*;


/**
 * Main driving class for the AusStage Google Analytics Report Generator app
 */
public class GoogleAnalytics {

	/**
	 * Main driving method for the Google Analytics Report Generator app
	 */
	public static void main(String args[]) {
	
		// get the command line options
		SimpleCommandLineParser parser = new SimpleCommandLineParser(args); // use a Google class to do this
		
		// check on the parameters
		String email    = parser.getValue("email");
		String password = parser.getValue("password", "pass");
		String action   = parser.getValue("action");
		String table    = parser.getValue("table");
		String urlPath  = parser.getValue("path");
		String output   = parser.getValue("output");
		File outputFile = null;
		
		
		// check on the command line arguments
		if(email == null || password == null || action == null) {
			// output some helpful information
			System.out.println("ERROR: the following parameters are expected");
			System.out.println("-email    the email address of a google account");
			System.out.println("-password the password of the google account");
			System.out.println("-action   either \"list\" or \"report\"");
			System.out.println("          when set to \"list\" a list of available profiles will be returned");
			System.out.println("          when set to \"report\" a report will be generated");
			System.out.println("-table    unique id of the table in the Google Analytics Service used to generate the report");
			System.out.println("-output   the name & location of the report file to generate");
			System.exit(-1);
		}
		
		// check on the action parameter
		if(action.equals("list") == false && action.equals("report") == false) {
			System.out.println("ERROR: unexpected action parameter value");
			System.exit(-1);
		}
		
		// check on the report generation parameters if required
		if(action.equals("report") == true) {
			if(table == null) {
				System.out.println("ERROR: missing table parameter");
				System.exit(-1);
			}
			
			if(output == null) {
				System.out.println("ERROR: missing output parameter");
				System.exit(-1);
			}
			
			// check to see if the output file exists
			outputFile = new File(output);
			
			if(outputFile.isFile() == true) {
				System.out.println("ERROR: Output file already exists, refusing to delete / overwrite");
				System.exit(-1);
			} else {
				// try to create the file
				try {
					boolean stat = outputFile.createNewFile();
					if(stat == false) {
						System.out.println("ERROR: Unable to create the output file");
						System.exit(-1);
					}
				} catch (java.io.IOException ex) {
					System.out.println("ERROR: Unable to create the output file");
					System.out.println("Exception details are: " + ex.toString());
					System.exit(-1);
				}
			}
		}
		
		// set up the analytics manager object
		AnalyticsManager analytics = new AnalyticsManager(email, password);
		boolean status = false;
		
		// determine what task to do 
		if(action.equals("list") == true) {
			// list the profiles that can be accessed
			
			// connect to the analytics service
			status = analytics.connect();
			
			// check on the status 
			if(status == false) {
				System.out.println("ERROR: Unable to authenticate to the Google Analytics service, check previous error message for details.");
				System.exit(-1);
			} else {
				System.out.println("INFO: Successfully authenticated to Google Analytics service");
			}
			
			// get a list of the accounts we can use
			String accountList = analytics.getAccountList();
			
			// check on the status 
			if(accountList == null) {
				System.out.println("ERROR: Unable to retrieve list available accounts, check previous error message for details.");
				System.exit(-1);
			} else {
				// output the list of accounts
				System.out.println(accountList);
				System.exit(0);
			}
		
		} // end account list code
		
		if(action.equals("report") == true) {
		
			// connect to the analytics service
			status = analytics.connect();
			
			// check on the status 
			if(status == false) {
				System.out.println("ERROR: Unable to authenticate to the Google Analytics service, check previous error message for details.");
				System.exit(-1);
			} else {
				System.out.println("INFO: Successfully authenticated to Google Analytics service");
			}
			
			// start generating the Mapping service report
			ReportGenerator report = new ReportGenerator(analytics, "Visits to the AusStage Mapping Service", "<p>This report displays information about the usage of the <a href=\"http://beta.ausstage.edu.au/mapping\" title=\"AusStage Mapping Service Homepage\">AusStage Mapping Service</a> as measured using the <a href=\"http://www.google.com/analytics\" title=\"Google Analytics Homepage\">Google Analytics</a> service.", "mapping-service-01");
			
			// set some parameters for querying the analytics service
			analytics.setTableId(table);
			
			if(urlPath != null) {
				analytics.setUrlPath(urlPath);
			}
			
			// build the report
			status = report.generate();
			
			if(status == false) {
				System.out.println("ERROR: Unable to generate the report, see previous error messages for details");
				System.exit(-1);
			}
			
			// write the report filr
			status = report.writeReportFile(outputFile);
			
			if(status == false) {
				System.out.println("ERROR: Unable to write the report file, see previous error messages for details");
				System.exit(-1);
			} else {
				System.out.println("INFO: Report file created successfully");
				System.exit(0);
			}
			
			/*
			// debug code
			String[] data = analytics.getVisits("2010-02-01", "2010-02-28", "^/mapping", table);
			
			if(data == null) {
				System.out.println("ERROR: Unable to retrieve data, check previous error message for details.");
				System.exit(-1);
			}
			*/
		
		} // end report generating code	
	} // end main method

} // end class definition
