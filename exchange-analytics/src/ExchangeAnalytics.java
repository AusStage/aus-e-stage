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
			System.out.println("- name & location of the SQLite database file");
			System.out.println("- name & location of the XML report output file");
			System.out.println("Eg. java ExchangeAnalytics ./log-files ./analytics.db ./analytics-report.xml");
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
		File dbFile = new File(args[1]);
		
		if(dbFile.isFile() == false) {
			System.out.println("WARN: Unable to locate the database file, a new database file will be created.");			
		} else {
			if(dbFile.canRead() == false || dbFile.canWrite() == false) {
				System.out.println("ERROR: Unable to access the database file.");
				System.exit(-1);
			}
		}
		
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
		
		try {
		
			// load the database driver and get a connection to the database
			Class.forName("org.sqlite.JDBC");
				
			// connect to the database
			database = DriverManager.getConnection("jdbc:sqlite:" + dbFile.getAbsolutePath());
			
		} catch (java.lang.ClassNotFoundException ex) {
			System.out.println("ERROR: Unable to load the SQLite classes.\n" + ex);
			System.exit(-1);
		} catch(java.sql.SQLException ex) {
			System.out.println("ERROR: Unable to connect to the database.\n" + ex);
			System.exit(-1);
		}
		
		// check to see if the tables are in the database
		Statement statement = null;
		ResultSet results   = null;
		
		// processed files list
		try {
			String sql = "SELECT * FROM sqlite_master WHERE tbl_name = 'processed_files'";
			statement = database.createStatement();
			results = statement.executeQuery(sql);
			
			if(results.next() == false) {
				// table doesn't exist so create it
				sql = "CREATE TABLE processed_files (file_name)";
				statement.execute(sql);
			}
			
			// play nice and tidy up
			results.close();
			
		} catch(java.sql.SQLException ex) {
			System.out.println("ERROR: Unable to create the 'processed_files' table in the database.\n" + ex);
			System.exit(-1);
		}
		
		// data
		try {
			String sql = "SELECT * FROM sqlite_master WHERE tbl_name = 'requests'";
			statement = database.createStatement();
			results = statement.executeQuery(sql);
			
			if(results.next() == false) {
				// table doesn't exist so create it
				sql = "CREATE TABLE requests (date, request_type, id_value, output_type, record_limit, javascript, ip_address, referer)";
				statement.execute(sql);
			}
			
			// play nice and tidy up
			results.close();
			statement.close();
			
		} catch(java.sql.SQLException ex) {
			System.out.println("ERROR: Unable to create the 'requests' table in the database.\n" + ex);
			System.exit(-1);
		}
		
		// build a log parse object
		LogParser logParser = new LogParser(database);
		int recordCount = 0;
		int fileCount = 0;
		
		try {
		
			// prepare a statement for checking on the file and storing the file name
			PreparedStatement fileCheck  = database.prepareStatement("SELECT * FROM processed_files WHERE file_name = ?");
			PreparedStatement fileUpdate = database.prepareStatement("INSERT INTO processed_files VALUES (?)");
				
			// loop through the list of files and processes as appropriate
			for(File logFile : logFiles) {
				// check to see if the log file has been parsed before
				fileCheck.setString(1, logFile.getName());
				
				results = fileCheck.executeQuery();
				
				if(results.next() == false) {
					// play nice and close the result set
					results.close();
					
					// parse this file
					recordCount = logParser.parseLog(logFile);
					
					// check to see if an error occured
					if(recordCount == -1) {
						System.exit(-1);
					} else {
						// output number of lines added to the database
						System.out.println("INFO: " + recordCount + " new records added from " + logFile);
						
						// add the file name to the list of processed files
						fileUpdate.setString(1, logFile.getName());
						fileUpdate.executeUpdate();
						
						// increment the count
						fileCount++;
					}					
				} else {
					// play nice and close the result set
					results.close();
				}
			}
			
			// play nice and close the database
			database.close();
			System.out.println("INFO: " + fileCount + " new files processed");
			
		} catch(java.sql.SQLException ex) {
			System.out.println("ERROR: Unable to execute file processed check or update SQL.\n" + ex);
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
	private final String name = "ausstage-exchange";
	private final String ext  = ".log";
	
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
			if(filename.startsWith(name) && filename.endsWith(ext)) {
				return true;
			} else {
				return false;
			}		
		} else {
			return false;
		}
	}
}
