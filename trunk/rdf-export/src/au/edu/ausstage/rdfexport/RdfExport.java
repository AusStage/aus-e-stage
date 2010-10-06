/*
 * This file is part of the AusStage RDF Export App
 *
 * The AusStage RDF Export App is free software: you can 
 * redistribute it and/or modify it under the terms of the GNU General 
 * Public License as published by the Free Software Foundation, either 
 * version 3 of the License, or (at your option) any later version.
 *
 * The AusStage RDF Export App is distributed in the hope 
 * that it will be useful, but WITHOUT ANY WARRANTY; without even the 
 * implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  
 * See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with the AusStage RDF Export App.  
 * If not, see <http://www.gnu.org/licenses/>.
*/

// include the class in our package
package au.edu.ausstage.rdfexport;

// import additional packages
import java.io.*;

// import additional AusStage related classes
import au.edu.ausstage.utils.*;

/**
 * Main driving class for the AusStage ABS Data Fix App
 */
public class RdfExport {

	// Version information 
	private static final String VERSION    = "1.2.0";
	private static final String BUILD_DATE = "2010-10-06";
	private static final String INFO_URL   = "http://code.google.com/p/aus-e-stage/wiki/RdfExport";
	
	// Valid tasks
	//private static final String[] TASK_TYPES = {"build-network-data", "export-network-data", "run-query", "edge-list-export", "edge-list-export-no-dups"};
	private static final String[] TASK_TYPES = {"build-network-data", "export-network-data"};
	
	// Valid data formats
	private static final String[] DATA_FORMATS = {"RDF/XML", "RDF/XML-ABBREV", "N-TRIPLE", "TURTLE", "N3"};
	
	/**
	 * Main driving method for the AusStage ABS Data Fix App
	 */
	public static void main(String args[]) {
	
		// output some basic information
		System.out.println("AusStage RdfExport - Export AusStage data into a variety of RDF related formats");
		System.out.println("Version: " + VERSION + " Build Date: " + BUILD_DATE);
		System.out.println("More Info: " + INFO_URL + "\n");
		
		java.util.GregorianCalendar calendar = new java.util.GregorianCalendar();
		java.util.Date startDate = calendar.getTime();
	 	
	 	// output the date 	
	 	System.out.println("INFO: Process Started: " + DateUtils.getCurrentDateAndTime());
		
		// get the command line params
		CommandLineParser parser = new CommandLineParser(args); 
		
		// get the parameters
		String taskType   = parser.getValue("task");
		String propsPath  = parser.getValue("properties");
		String dataFormat = parser.getValue("format");
		String output     = parser.getValue("output");
		
		// check on the parameters
		if(InputUtils.isValid(taskType, TASK_TYPES) == false || InputUtils.isValid(propsPath) == false) {
			// output usage instructions
			System.err.println("ERROR: the following parameters are expected");
			System.err.println("-tasktype   the type of task to undertake");
			System.err.println("-properties the location of the properties file");
			System.err.println("-format     (optional) the data format used in an export task");
			System.err.println("-output     (optional) the output file to create for an export task");
			System.err.println("\nValid task types are:");
			System.err.println(InputUtils.arrayToString(TASK_TYPES));
			System.err.println("\nValid data formats are:");
			System.err.println(InputUtils.arrayToString(DATA_FORMATS));
			System.exit(-1);
		}
		
		if(taskType.equals("export-network-data")) {
		
			if(InputUtils.isValid(dataFormat) == false) {
				// format is missing so use a default
				System.out.println("INFO: No data format specified. Using 'RDF/XML' by default");
				dataFormat = "RDF/XML";
			} else { 
				// check to make sure it is one of the supported options
				if(InputUtils.isValid(dataFormat, DATA_FORMATS) == false) {
					System.err.println("ERROR: the specified data format '" + dataFormat + "' is invalid");
					System.err.println("Valid data formats are:");
					System.err.println(InputUtils.arrayToString(DATA_FORMATS));
					System.exit(-1);
				}
			}
		}
		
		// declare helper variables
		boolean status = false;
		
		// try to instantiate the properties manager
		PropertiesManager properties = new PropertiesManager();
		
		if(properties.loadFile(propsPath) == false) {
	 		System.err.println("ERROR: unable to open the specified properties file");
	 		System.exit(-1);
	 	}
	 	
	 	// execute the appropriate task		
		if(taskType.equals("build-network-data") == true) {
				
			// instantiate the database classes
			DbManager database = new DbManager(properties.getProperty("db-connection-string"));
		
			System.out.println("INFO: Connecting to the database...");		
			// connect to the database
			if(database.connect() == false) {
		 		// connection to the database failed
		 		System.err.println("ERROR: a connection to the database could not be made");
		 		System.exit(-1);
	 		} else {
	 			System.out.println("INFO: Connection to the database established");
	 		}
	 		
	 		// do the build-network-data task
			BuildNetworkData task = new BuildNetworkData(database, properties);
			
			// tidy up any existing TDB datastore
			status = task.doReset();
			
			if(status == false) {
				System.err.println("ERROR: A fatal error has occured, see previous error message for details");
				System.exit(-1);
			}
			
			status = task.doTask();
	 		
		} 
		else if(taskType.equals("export-network-data")) {
			// do the export-network-data task
			ExportNetworkData task = new ExportNetworkData(properties);
			
			// check to ensure the file doesn't exist yet
			if(FileUtils.doesFileExist(output) == false) {
						
				File outputFile = new File(output);
				status = task.doTask(dataFormat, outputFile);
			} else {
				System.err.println("ERROR: Output file already exists, refusing to delete / overwrite");
				System.exit(-1);
			}
		}
		
		// determine how to finish
		if(status == false) {
			System.err.println("ERROR: An error has occured, see previous messages for details");
			System.exit(-1);
		} else {
			System.out.println("INFO: Task completed successfully");
				 	
		 	// output the date 	
		 	System.out.println("INFO: Process Finished: " + DateUtils.getCurrentDateAndTime());
		 	
		 	calendar = new java.util.GregorianCalendar();
			java.util.Date finishDate = calendar.getTime();
		 	
		 	// calculate the difference
		 	long startTime  = startDate.getTime();
		 	long finishTime = finishDate.getTime();
		 	
		 	// calculate the difference in minutes
			float minuteConstant = 60000;
		 	float timeDifference = finishTime - startTime;
		 	timeDifference = timeDifference / minuteConstant;
		 	
		 	if(timeDifference > 1 ) {
			 	System.out.format("INFO: Elapsed Time: %.2f minutes%n", timeDifference);
			} else {
				timeDifference = timeDifference * 60;
				System.out.format("INFO: Elapsed Time: %.2f seconds%n", timeDifference);
			}
			
			System.exit(0);
		}
		
	} // end main method
} // end the class definition
