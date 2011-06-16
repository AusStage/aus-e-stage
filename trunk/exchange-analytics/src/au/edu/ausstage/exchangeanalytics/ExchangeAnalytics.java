/* This file is part of the AusStage Exchange Analytics app
 *
 * The AusStage Exchange Analytics app is free software: you can redistribute
 * it and/or modify it under the terms of the GNU General Public License 
 * as published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * The AusStage Exchange Analytics app is distributed in the hope that it will 
 * be useful, but WITHOUT ANY WARRANTY; without even the implied warranty 
 * of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with the AusStage Exchange Analytics app.  
 * If not, see <http://www.gnu.org/licenses/>.
 */
 
package au.edu.ausstage.exchangeanalytics;

// import additional ausstage packages
import au.edu.ausstage.utils.*;

/**
 * Main driving class for the Exchange Analytics app which is responsible
 * for compile the analytics report for the AusStage Data Exchange website
 */
public class ExchangeAnalytics {

	// Version information 
	public  static final String VERSION    = "1.0.0";
	private static final String BUILD_DATE = "2011-06-x";
	private static final String INFO_URL   = "http://code.google.com/p/aus-e-stage/wiki/ExchangeAnalytics";
	public  static final String APP_NAME   = "AusStage Exchange Analytics";
	
	// private helper constants
	private static final String[] REQD_PARAMETERS = {"properties"};
	//private static final String[] REQD_PROPERTIES = {"config-dir", "username", "password", "output-dir"};
	//private static final String[] TASK_TYPES      = {"account-data", "build-reports"};
	
	/**
	 * Main driving method for the  WebsiteAnalytics app
	 */
	public static void main(String args[]) {
	
		// output some basic information
		System.out.println(APP_NAME + " - Build the Exchange Service analytics report");
		System.out.println("Version: " + VERSION + " Build Date: " + BUILD_DATE);
		System.out.println("More Info: " + INFO_URL + "\n");
		
		// output the date 	
	 	System.out.println("INFO: Application Started: " + DateUtils.getCurrentDateAndTime());
	 	
	 	// get the command line arguments
	 	CommandLineParser cli = new CommandLineParser(args);
	 	
	 	// process the list of arguments
	 	String propsPath = cli.getValue("properties");
	 	
	 	// double check the parameter
	 	if(InputUtils.isValid(propsPath) == false) {
	 		System.err.println("ERROR: the following parameters are expected:");
	 		System.err.println("       -properties the location of the properties file");
	 		System.exit(-1); 		
	 	}
	}
}
