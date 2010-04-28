/*
 * This file is part of the AusStage Exchange Analytics application
 *
 * The AusStage Exchange Analytics application is free software: you can 
 * redistribute it and/or modify it under the terms of the GNU General 
 * Public License as published by the Free Software Foundation, either 
 * version 3 of the License, or (at your option) any later version.
 *
 * The AusStage Exchange Analytics application is distributed in the hope 
 * that it will be useful, but WITHOUT ANY WARRANTY; without even the 
 * implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  
 * See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with the AusStage Exchange Analytics application.  
 * If not, see <http://www.gnu.org/licenses/>.
*/

// import additional libraries
import java.io.*;
import java.sql.*;
import java.util.*;

import org.hsqldb.jdbc.jdbcDataSource;

/**
 * Main driving class for the AusStage Exchange Analytics application which is used
 * to parse the log files created by the AusStage Exchange Service and generate
 * an anlytics report which can be used to provide analytical information on the 
 * use of the service
 */
public class ExchangeAnalytics {

	/**
	 * Main method for the driving class 
	 */
	public static void main(String[] args) {
	
		// check on the command line arguments
		if(args.length != 3) {
			// output some helpful text
			System.out.println("ERROR: Expected three command line arguments.");
			System.out.println("- directory containing the log files");
			System.out.println("- name & location of the HSQLDB database file");
			System.out.println("- name & location of the XML report output file");
			System.out.println("Eg. java ExchangeAnalytics ./log-files ./analytics ./analytics-report.xml");
			System.exit(-1);
		}
		
		// check on the log files directory first
		File logFileDir = new File(args[0]);
		
		if(logFileDir.isDirectory() == false) { // is it a directory?
			// no
			System.out.println("ERROR: First command line argument expected to be a directory containing log files.");
			System.out.println("First command line argument is not a directory");
			System.out.println("Attempted to use: " + logFileDir);
			System.exit(-1);
		}
		
		if(logFileDir.canRead() == false) { // can we read this directory?
			// no
			System.out.println("ERROR: Unable to access directory containing the log files.");
			System.out.println("Attempted to use: " + logFileDir);
			System.exit(-1);
		}
		
		// get a list of files in the directory
		File[] logFiles = logFileDir.listFiles(new FileListFilter());
		
		// check to see what was returned
		if(logFiles == null || logFiles.length == 0) {
			System.out.println("ERROR: No valid log files found in the supplied directory.");
			System.out.println("Expected files with names that start with 'ausstage-exchange' & end with '.log'");
			System.exit(-1);
		} else {
			System.out.println("INFO: " + logFiles.length + " log files found.");
		}
		
		// check on the database parameter
		File dbFile = new File(args[1] + ".properties");
		
		if(dbFile.isFile() == false) {
			System.out.println("WARN: Unable to locate the database file, a new database file will be created.");
		} else {
			if(dbFile.canRead() == false || dbFile.canWrite() == false) {
				System.out.println("ERROR: Unable to access the database file.");
				System.exit(-1);
			}
		}
		
		// reset the dbFile variables
		dbFile = new File(args[1]);
		
		// check on the output file
		File outputFile = new File(args[2]);
		
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
		
		// instantiate database classes
		Connection database = null;
		jdbcDataSource dataSource = null;
		
		try {
		
			// get a new Data Source object
			dataSource = new jdbcDataSource();

			// set the database to the file specified
        	dataSource.setDatabase("jdbc:hsqldb:" + dbFile.getAbsolutePath());

			// get a standalone connection to the database
	        database = dataSource.getConnection("sa", "");
			
		} catch(java.sql.SQLException ex) {
			System.out.println("ERROR: Unable to connect to the database.\n" + ex);
			System.exit(-1);
		}
		
		// check to see if the tables are in the database
		Statement statement = null;
		ResultSet results   = null;
		
		// ensure the tables are present
		// processed files
		try {
		
			// if the query succeeds the table is present
			String sql = "SELECT MAX(file_name) FROM processed_files";
			
			// create a statement
			statement = database.createStatement();
			
			// get the results
			results = statement.executeQuery(sql);
			
			// play nice and close the statement
			statement.close();
			
		}  catch(java.sql.SQLException e) {
			// table appears to be missing
			try {
				String sql = "CREATE TABLE processed_files (file_name VARCHAR)";
			
				// create a statement
				statement = database.createStatement();
				
				// execute the statement
				int result = statement.executeUpdate(sql);
				
				// check on the result of the execute
				if(result == -1) {
					System.out.println("Unable to create the 'processed_files' table");
					System.exit(-1);
				}
			} catch(java.sql.SQLException ex) {
				System.out.println("ERROR: Unable to create the 'processed_files' table in the database.\n" + ex);
				System.exit(-1);
			}
		}
		
		// requests
		try {
		
			// if the query succeeds the table is present
			String sql = "SELECT MAX(date) FROM requests";
			
			// create a statement
			statement = database.createStatement();
			
			// get the results
			results = statement.executeQuery(sql);
			
			// play nice and close the statement
			statement.close();
			
		}  catch(java.sql.SQLException e) {
			// table appears to be missing
			try {
				String sql = "CREATE TABLE requests (date VARCHAR, request_type VARCHAR, id_value VARCHAR, output_type VARCHAR, record_limit VARCHAR, javascript VARCHAR, ip_address VARCHAR, referer VARCHAR)";
			
				// create a statement
				statement = database.createStatement();
				
				// execute the statement
				int result = statement.executeUpdate(sql);
				
				// check on the result of the execute
				if(result == -1) {
					System.out.println("ERROR: Unable to create the 'requests' table");
					System.exit(-1);
				}
			} catch(java.sql.SQLException ex) {
				System.out.println("ERROR: Unable to create the 'requests' table in the database.\n" + ex);
				System.exit(-1);
			}
		}
		
		// build a log parse object
		LogParser logParser = new LogParser(database);
		int recordCount = 0;
		int fileCount = 0;
		
		try {
		
			// prepare a statement for checking on the file and storing the file name
			PreparedStatement fileCheck  = null;
			PreparedStatement fileUpdate = null;
				
			// loop through the list of files and processes as appropriate
			for(File logFile : logFiles) {
				// check to see if the log file has been parsed before
				fileCheck = database.prepareStatement("SELECT * FROM processed_files WHERE file_name = ?");
				fileCheck.setString(1, logFile.getName());
				
				results = fileCheck.executeQuery();
				
				if(results.next() == false) {
					// play nice and close the result set
					results.close();
					fileCheck.close();
					
					// parse this file
					recordCount = logParser.parseLog(logFile);
					
					// check to see if an error occured
					if(recordCount == -1) {
						System.exit(-1);
					} else {
						// output number of lines added to the database
						System.out.println("INFO: " + recordCount + " new records added from " + logFile);
						
						// add the file name to the list of processed files
						fileUpdate = database.prepareStatement("INSERT INTO processed_files VALUES (?)");
						fileUpdate.setString(1, logFile.getName());
						int result = fileUpdate.executeUpdate();
						if(result == -1) {
							System.out.println("ERROR: Unable to update processed_files table");
						}
						fileUpdate.close();
						
						// increment the count
						fileCount++;
					}					
				} else {
					// play nice and close the result set
					results.close();
				}
			}
			
			// keep the user informed
			System.out.println("INFO: " + fileCount + " new files processed");
			
		} catch(java.sql.SQLException ex) {
			System.out.println("ERROR: Unable to execute file processed check or update SQL.\n" + ex);
			System.exit(-1);
		}
		
		// get an instance of the ReportGenerator class
		ReportGenerator report = new ReportGenerator(database);
		
		// generate the report
		boolean status = report.generate();
		
		// shutdown the database
		try {
			statement = database.createStatement();
			statement.execute("SHUTDOWN");
			database.close();
		} catch(java.sql.SQLException ex) {
			System.out.println("ERROR: Unable to close the database.\n" + ex);
			System.exit(-1);
		}
		
		if(status == true) {
			// write report file
			// debug code
			status = report.writeReportFile(outputFile);
			
			if(status == true) {
				// report success and quit
				System.out.println("INFO: Report file successfully written");
				System.exit(0);
			} else {
				System.out.println("ERROR: Unable to create report, check previous error message for details.");
				System.exit(-1);
			}
		} else {
			// report error and quit
			System.out.println("ERROR: Unable to generate report, check previous error message for details.");
			System.exit(-1);
		}
						
	} // end main method

} // end class definition

/**
 * A class used to filter the list of log files to those that 
 * we expect to find. Namely those starting with "ausstage-exchange" and
 * ending in .log
 */
class FileListFilter implements FilenameFilter {

	// declare helper variables
	private final String NAME = "ausstage-exchange";
	private final String EXT  = ".log";
	
	// get the current date as a string for file comparison
	// get an instance of the calendar
	private GregorianCalendar calendar = new GregorianCalendar();
	private String currentDate;
	
	/**
	 * Constructor for this class
	 */
	public FileListFilter() {
	
		// set it to the current time
	 	calendar.setTime(new java.util.Date());
	 	
		// get the current date
	 	currentDate = Integer.toString(calendar.get(java.util.Calendar.YEAR));
	 	currentDate += "-" + String.format("%02d", calendar.get(java.util.Calendar.MONTH) + 1);
	 	currentDate += "-" + String.format("%02d", calendar.get(java.util.Calendar.DAY_OF_MONTH));
	 }	
	
	/**
	 * Method to test if the specified file matches the predetermined
	 * name and extension requirements
	 *
	 * @param dir the directory in which the file was found.
	 * @param filename the name of the file
	 *
	 * @return true if and only if the file should be included
	 */
	public boolean accept(File dir, String filename) {
	
		if(filename != null) {
			if(filename.startsWith(NAME) && filename.endsWith(EXT) && filename.indexOf(currentDate) == -1) {
				return true;
			} else {
				return false;
			}		
		} else {
			return false;
		}
	}
}
