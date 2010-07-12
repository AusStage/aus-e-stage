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
import java.util.Date;
import java.text.DateFormat;
import java.util.GregorianCalendar;

/**
 * Main driving class for the AusStage ABS Data Fix App
 */
public class RdfExport {

	// Version information 
	private static final String VERSION    = "1.0.0";
	private static final String BUILD_DATE = "2010-07-12";
	private static final String INFO_URL   = "http://code.google.com/p/aus-e-stage/wiki/RdfExport";
	
	// Valid tasks
	private static final String[] TASK_TYPES = {"build-network-data", "export-network-data", "run-query", "edge-list-export", "edge-list-export-no-dups"};
	
	// Valid data formats
	private static final String[] DATA_FORMATS = {"RDF/XML", "RDF/XML-ABBREV", "N-TRIPLE", "TURTLE", "N3"};
	
	/**
	 * Main driving method for the AusStage ABS Data Fix App
	 */
	public static void main(String args[]) {
	
		// output some basic information
		System.out.println("AusStage RdfExport - Export AusStage data into a variety of RDF related data formats");
		System.out.println("Version: " + VERSION + " Build Date: " + BUILD_DATE);
		System.out.println("More Info: " + INFO_URL + "\n");
		
		// get and store the current date
		GregorianCalendar calendar = new GregorianCalendar();
		Date startDate = calendar.getTime();
	 	DateFormat dateFormatter = DateFormat.getDateTimeInstance(DateFormat.FULL, DateFormat.FULL);
	 	
	 	// output the date 	
	 	System.out.println("INFO: Process Started: " + dateFormatter.format(startDate));
		
		// get the command line params
		SimpleCommandLineParser parser = new SimpleCommandLineParser(args); // use a Google class to do manage command line params
		
		// get the parameters
		String taskType   = parser.getValue("task", "tasktype");
		String propsPath  = parser.getValue("properties");
		String dataFormat = parser.getValue("format");
		String output     = parser.getValue("output");
		String query      = parser.getValue("query");
		
		// check on the parameters
		if(taskType == null || propsPath == null) {
			// output usage instructions
			System.err.println("ERROR: the following parameters are expected");
			System.err.println("-tasktype   the type of task to undertake");
			System.err.println("-properties the location of the properties file");
			System.err.println("-format     (optional) the data format used in an export task");
			System.err.println("-output     (optional) the output file to create for an export task");
			System.err.println("-query      (optional) the location of the query file to run");
			System.err.println("\nValid fix types are:");
			System.err.println(java.util.Arrays.toString(TASK_TYPES).replaceAll("[\\]\\[]", ""));
			System.err.println("\nValid data formats are:");
			System.err.println(java.util.Arrays.toString(DATA_FORMATS).replaceAll("[\\]\\[]", ""));
			System.exit(-1);
		}
		
		// check on the fix type
		boolean isValid = false;
		
		for(int i = 0; i < TASK_TYPES.length; i++) {
			if(TASK_TYPES[i].equals(taskType)) {
				isValid = true;
			}
		}
		
		if(isValid != true) {
			// no valid task type was found
			System.err.println("ERROR: the specified task type is invalid");
			System.err.println("Valid fix types are:");
			System.err.println(java.util.Arrays.toString(TASK_TYPES).replaceAll("[\\]\\[]", ""));
			System.exit(-1);
		}
		
		// check on the data format parameter if reqiured
		isValid = false;
		
		if(taskType.equals("export-network-data")) {
		
			if(dataFormat == null) {
				// format is missing so use a default
				System.out.println("INFO: No data format specified. Using 'RDF/XML' by default");
				dataFormat = "RDF/XML";
			} else { 
				// format specified, ensure it is valid
				for(int i = 0; i < DATA_FORMATS.length; i++) {
					if(DATA_FORMATS[i].equals(dataFormat)) {
						isValid = true;
					}
				}
				
				if(isValid == false) {
					System.err.println("ERROR: the specified data format is invalid");
					System.err.println("Valid data formats are:");
					System.err.println(java.util.Arrays.toString(DATA_FORMATS).replaceAll("[\\]\\[]", ""));
					System.exit(-1);
				}
			}
		}
		
		// declare helper variables
		boolean status;
		
		// try to instantiate the properties manager
		PropertiesManager properties = new PropertiesManager();
		
		// try to load the properties file
		status = properties.loadFile(propsPath);
		
		if(status == false) {
			System.err.println("ERROR: A fatal error has occured, see previous error message for details");
			System.exit(-1);
		}
		
		// declare database related variables
		DatabaseManager database = null;
		
		// try to connect to the database if required
		if(taskType.equals("build-network-data") == true) {
			// a database connection is required
			
			// instantiate the database classes
			database = new DatabaseManager();
		
			// get the connection string
			String connectionString = properties.getProperty("db-connection-string");
		
			if(connectionString == null) {
				System.err.println("ERROR: Unable to load the connection string property");
				System.err.println("       Check the db-connection-string value in the properties file");
				System.exit(-1);
			}
		
			System.out.println("INFO: Connecting to the database...");		
			// connect to the database
			status = database.connect(connectionString);
		
			if(status == true) {
				System.out.println("INFO: Connection established");
			} else {
				System.err.println("ERROR: Unable to connect to the database");
				System.exit(-1);
			}
		}

		// execute the appropriate task
		if(taskType.equals("build-network-data")) {
			// do the build-network-data task
			BuildNetworkData task = new BuildNetworkData(database, properties);
			
			// tidy up any existing TDB datastore infor
			status = task.doReset();
			
			if(status == false) {
				System.err.println("ERROR: A fatal error has occured, see previous error message for details");
				System.exit(-1);
			}
			
			status = task.doTask();
		} else if(taskType.equals("export-network-data")) {
			// do the export-network-data task
			ExportNetworkData task = new ExportNetworkData(properties);
			
			// check on the output file
			File outputFile = checkOutputPath(output);
			
			if(outputFile != null) {
				status = task.doTask(dataFormat, outputFile);
			} else {
				System.err.println("ERROR: A fatal error has occured, see previous error message for details");
				System.exit(-1);
			}
		} else if(taskType.equals("run-query")) {
		
			// do the run-query task
			RunQuery task = new RunQuery(properties);
			
			// check on the query parameter
			if(query == null) {
				System.err.println("ERROR: Location of query file not specified");
				System.exit(-1);
			}
			
			// check the query file
			File queryFile = checkPath(query);
			
			if(queryFile != null) {
				status = task.doTask(queryFile);
			} else {
				System.err.println("ERROR: A fatal error has occured, see previous error message for details");
				System.exit(-1);
			}
		
		} else if(taskType.equals("edge-list-export")) {
		
			// do the Edge List Export task
			EdgeListExport task = new EdgeListExport(properties);
			
			// check on the output file
			File outputFile = checkOutputPath(output);
			
			if(outputFile != null) {
				status = task.doTask(outputFile);
			} else {
				System.err.println("ERROR: A fatal error has occured, see previous error message for details");
				System.exit(-1);
			}
		} else if(taskType.equals("edge-list-export-no-dups")) {
		
			// do the Edge List Export task
			EdgeListExport task = new EdgeListExport(properties);
			
			// check on the output file
			File outputFile = checkOutputPath(output);
			
			if(outputFile != null) {
				status = task.doTask(outputFile, true);
			} else {
				System.err.println("ERROR: A fatal error has occured, see previous error message for details");
				System.exit(-1);
			}
		}	
		
		// determine how to finish
		if(status == false) {
			System.err.println("ERROR: An error has occured, see previous messages for details");
			System.exit(-1);
		} else {
			System.out.println("INFO: Task completed successfully");
			
			// output the finish date
			calendar = new GregorianCalendar();
			Date finishDate = calendar.getTime();
				 	
		 	// output the date 	
		 	System.out.println("INFO: Process Finished: " + dateFormatter.format(finishDate));
		 	
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
	
	/**
	 * A method to check the validity of the output parameter
	 *
	 * @param path the specified output path
	 *
	 * @return a valid File object on success, null on failure
	 */
	public static File checkOutputPath(String path) {
	
		// check the output file
		File outputFile = new File(path);
			
		if(outputFile.isFile() == true) {
			System.err.println("ERROR: Output file already exists, refusing to delete / overwrite");
			return null;
		} else {
			// try to create the file
			try {
				boolean stat = outputFile.createNewFile();
				if(stat == false) {
					System.err.println("ERROR: Unable to create the output file");
					return null;
				}
			} catch (java.io.IOException ex) {
				System.err.println("ERROR: Unable to create the output file");
				System.err.println("Exception details are: " + ex.toString());
				return null;
			}
		}
		
		// if we get this far everything is OK
		return outputFile;	
	}
	
	/**
	 * A method to check the validity of the output parameter
	 *
	 * @param path the specified output path
	 *
	 * @return a valid File object on success, null on failure
	 */
	public static File checkPath(String path) {
	
		// check the output file
		File file = new File(path);
			
		if(file.isFile() == false || file.canRead() == false) {
			System.err.println("ERROR: Unable to locate the specified file");
			return null;
		} else {
			// if we get this far everything is OK
			return file;			
		}		
	}
	
} // end the class definition
