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

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Main driving class for the Exchange Analytics app which is responsible
 * for compile the analytics report for the AusStage Data Exchange website
 */
public class ExchangeAnalytics {

	// version information 
	public static final String VERSION    = "1.0.0";
	public static final String BUILD_DATE = "2011-06-x";
	public static final String INFO_URL   = "http://code.google.com/p/aus-e-stage/wiki/ExchangeAnalytics";
	public static final String APP_NAME   = "AusStage Exchange Analytics";
	
	// other public constants
	public static final String DERBY_DB = "exchange.derby";
	public static final String REPORT_FILENAME = "exchange-analytics.xml";
	
	// private helper constants
	private static final String[] REQD_PROPERTIES = {"input-dir", "database-dir", "output-dir"};
	
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
			} else {
				if(FileUtils.doesDirExist(properties.getValue(REQD_PROPERTIES[i])) == false) {
					System.err.println("ERROR: unable to find the directory specified by the '" + REQD_PROPERTIES[i] + "' property");
					System.err.println("       was looking for '" + FileUtils.getAbsolutePath(properties.getValue(REQD_PROPERTIES[i])) + "'");
					System.exit(-1);
				}
			}
		}
		
		// check on the output
		if(FileUtils.doesFileExist(properties.getValue("output-dir") + REPORT_FILENAME) == true) {
			System.out.println("INFO: Deleting the old '" + REPORT_FILENAME + "' report");
			
			if(FileUtils.deleteFile(properties.getValue("output-dir") + REPORT_FILENAME) == false) {
				System.err.println("ERROR: Unable to delete the old '" + REPORT_FILENAME + "' report");
				System.exit(-1);
			}
		}
		
		// check on the log files
		String[] logFiles = FileUtils.getFileNameListByExtension(properties.getValue("input-dir"), ".log");
		
		if(logFiles.length == 0) {
			System.err.println("ERROR: unable to locate any log files in the 'input-dir' directory");
			System.exit(-1);
		} else {
			System.out.println("INFO: Found " + logFiles.length + " log files");
		}
		
		
		// try to open a connection to the database
		String dbName = properties.getValue("database-dir") + DERBY_DB;
	
		// check the database 
		if(FileUtils.doesDirExist(dbName) == false) {
			System.out.println("INFO: No existing database found, a new one will be created");
		} else {
			System.out.println("INFO: Using existing database");
		}
		
		DbManager database = new DbManager(dbName);
		
		try {
			database.getConnection();
		} catch (SQLException e) {
			System.err.println("ERROR: unable to connect to the database");
			System.err.println("      " + e.toString());
			System.exit(-1);
		}
		
		try {
			database.buildTables();
		} catch (SQLException e) {
			System.err.println("ERROR: unable to build the required tables");
			System.err.println("      " + e.toString());
			e.printStackTrace();
			System.exit(-1);
		}
	}
}