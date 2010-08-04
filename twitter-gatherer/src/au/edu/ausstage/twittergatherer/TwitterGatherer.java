/*
 * This file is part of the AusStage Twitter Gatherer
 *
 * The AusStage Twitter Gatherer is free software: you can redistribute
 * it and/or modify it under the terms of the GNU General Public License 
 * as published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * The AusStage Twitter Gatherer is distributed in the hope that it will 
 * be useful, but WITHOUT ANY WARRANTY; without even the implied warranty 
 * of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with the AusStage Twitter Gatherer.  
 * If not, see <http://www.gnu.org/licenses/>.
*/

package au.edu.ausstage.twittergatherer;

// import additional ausstage packages
import au.edu.ausstage.utils.*;

// import additional java packages
import java.io.*;
import java.util.Date;
import java.text.DateFormat;
import java.util.GregorianCalendar;

/**
 * Main driving class for the TwitterGatherer application
 */
public class TwitterGatherer {

	// Version information 
	private static final String VERSION    = "1.0.0";
	private static final String BUILD_DATE = "2010-08-03";
	private static final String INFO_URL   = "http://code.google.com/p/aus-e-stage/wiki/TwitterGatherer";
	
	// default properties
	private static final String[] DEFAULT_PROPERTIES = {"db-connection-string", "twitter-user", "twitter-password", "log-dir"};
	
	/**
	 * Main driving method for the AusStage ABS Data Fix App
	 */
	public static void main(String args[]) {
	
		// output some basic information
		System.out.println("AusStage Twitter Gatherer - Gather Performance Feedback from Twitter");
		System.out.println("Version: " + VERSION + " Build Date: " + BUILD_DATE);
		System.out.println("More Info: " + INFO_URL + "\n");
		
		// get and store the current date
		GregorianCalendar calendar = new GregorianCalendar();
		Date startDate = calendar.getTime();
	 	DateFormat dateFormatter = DateFormat.getDateTimeInstance(DateFormat.FULL, DateFormat.FULL);
	 	
	 	// output the date 	
	 	System.out.println("INFO: Application Started: " + dateFormatter.format(startDate));
	 	
	 	// get the command line arguments
	 	CommandLineParser cli = new CommandLineParser(args);
	 	
	 	// process the list of arguments
	 	String propsPath = cli.getValue("properties");
	 	
	 	// double check the parameter
	 	if(propsPath == null) {
	 		System.err.println("ERROR: the following parameters are expected:");
	 		System.err.println("-properties the location of the properties file");
	 		System.exit(-1); 		
	 	}
	 	
	 	// load the properties
	 	PropertiesManager properties = new PropertiesManager();
	 	
	 	if(properties.loadFile(propsPath) == false) {
	 		System.err.println("ERROR: unable to open the specified properties file");
	 		System.exit(-1);
	 	}
	 	
	 	// check on the properties 
	 	for(int i = 0; i < DEFAULT_PROPERTIES.length; i++) {
	 		if(properties.getValue(DEFAULT_PROPERTIES[i]) == null || properties.getValue(DEFAULT_PROPERTIES[i]).equals("") == true) {
	 			System.err.println("ERROR: unable to load the required property '" + DEFAULT_PROPERTIES[i] + "'");
	 			System.err.println("       check the properties file and try again");
	 			System.exit(-1);
	 		}
	 	}
	 	
	 	// check on the log directory
	 	if(FileUtils.doesDirExist(properties.getValue("log-dir")) == false) {
	 		// log directory is missing
	 		System.err.println("ERROR: the log directory cannot be found");
	 		System.err.println("       " + FileUtils.getCanonicalPath(properties.getValue("log-dir")));
	 		System.exit(-1);
	 	}
	 	
	 	// get a connection to the database
	 	System.out.println("INFO: Connecting to the database");
	 	
	 	DbManager database = new DbManager(properties.getValue("db-connection-string"));
	 	
	 	if(database.connect() == false) {
	 		// connection to the database failed
	 		System.err.println("ERROR: a connection to the database could not be made");
	 		System.exit(-1);
	 	}
	 	
	 	System.out.println("INFO: Connection established");
		
	} // end the main driving method



}
