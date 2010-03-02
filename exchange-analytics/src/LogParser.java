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
 * A class used to parse log files and the relevant lines to the database
 */
 
public class LogParser {

	// private class level variables
	private Connection database;

	/**
	 * Constructor for this class
	 *
	 * @param conn a valid connection to the database
	 */
	public LogParser(Connection conn) {
	
		// store the connection for later reuse
		database = conn;
	
	} // end constructor
	
	/**
	 * Method to parse a log file and add any lines found
	 * to the database
	 *
	 * @param logFile the log file to process
	 *
	 * @return        number of entries added to the database
	 */
	public int parseLog(File logFile) {
	
		// declare helper variables
		String logLine      = null;
		String previousLine = null;
		String[] tockens    = null; // tockens from the string
		String[] params     = null; // query parameters
		String[] param      = null; // parameter name and value
		String[] dateParts  = null; // parts of the date
		int  logLines       = 0;
		PreparedStatement statement = null;
		
		// sql string
		try {
			statement = database.prepareStatement("INSERT INTO requests VALUES (?, ?, ?, ?, ?, ?, ?, ?)");
		} catch (java.sql.SQLException ex) {
			System.out.println("ERROR: Unable to prepare SQL statement\n" + ex);
			return -1;
		}
	
		try {
			// open the log file
			BufferedReader input = new BufferedReader(new FileReader(logFile));
			
			// read in the file line by line
			while((logLine = input.readLine()) != null) {
			
				if(logLine.startsWith("INFO")) {
				
					// declare variables to store data
					String date    = null;
					String type    = null;
					String id      = null;
					String output  = null;
					String limit   = null;
					String script  = null;
					String ip      = null;
					String referer = null;				
				
					// get the date
					tockens = previousLine.split(" ");
					//date = tockens[0] + " " + tockens[1] + " " + tockens[2];
					dateParts = tockens[0].split("/");
					date = dateParts[2] + "-" + dateParts[1] + "-" + dateParts[0];
				
					// this is a line to parse
					tockens = logLine.split(" "); // tockens from the line
					params  = tockens[2].split("&"); // query parameters
					
					for(int i = 0; i < params.length; i++) {
						param = params[i].split("=");
					
						// tease out the params
						if(param[0].equals("type")) { // request type
							type = param[1];
						} else if(param[0].equals("id")) { // request id
							id = param[1];
						} else if(param[0].equals("output")) { // output type
							output = param[1];
						} else if(param[0].equals("limit")) { // limit
							limit = param[1];
						} else if(param[0].equals("script")) { // javascript output
							script = param[1];
						}
					}
					
					// double check the output type
					if(output == null) {
						output = "html";
					}
					
					// get the ip and referer
					if(tockens[4].equals("null")) {
						referer = tockens[6];
					} else {
						ip = tockens[5];
					}
					
					// add this data to the statement
					statement.setString(1, date);
					statement.setString(2, type);
					statement.setString(3, id);
					statement.setString(4, output);
					statement.setString(5, limit);
					statement.setString(6, script);
					statement.setString(7, ip);
					statement.setString(8, referer);
					
					// add this line to a batch
					statement.addBatch();
					
					// increment the count
					logLines++;
					
				} else {
					// store this line as it may contain a date for next INFO Line processing
					previousLine = logLine;
				}
			}
			
			// play nice and tidy up
			input.close();
			
		} catch (java.io.IOException ex) {
			System.out.println("ERROR: Unable to parse log file\n" + ex);
			return -1;
		} catch (java.sql.SQLException ex) {
			System.out.println("ERROR: Unable to compile SQL statement\n" + ex);
			return -1;
		}
		
		// add the data to the database
		try {
			database.setAutoCommit(false);
			statement.executeBatch();
			database.commit();
			database.setAutoCommit(true);
					
			// play nice and tidy up 
			statement.close();
		} catch (java.sql.SQLException ex) {
			System.out.println("ERROR: Unable to add data to the database\n" + ex);
			return -1;
		}
		
		// return number of lines processed
		return logLines;
	
	} // end parseLog method

} // end class definition
