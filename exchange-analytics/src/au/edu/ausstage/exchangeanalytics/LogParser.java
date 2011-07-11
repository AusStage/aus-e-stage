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

import au.edu.ausstage.utils.InputUtils;
import au.edu.ausstage.utils.FileUtils;
import au.edu.ausstage.exchangeanalytics.types.Request;

import java.util.ArrayList;
import java.io.*;

/**
 * parse a directory of log files building a list of records
 */
public class LogParser {

	// private class level variables
	private String[] logFiles;
	
	ArrayList<Request> eventRequests;
	ArrayList<Request> resourceRequests;
	ArrayList<Request> feedbackRequests;
	
	/**
	 * constructor for this class
	 *
	 * @param logFiles an array of log files to process
	 *
	 * @throws IllegalArgumentException if the array is null or empty
	 */
	public LogParser(String[] logFiles) {
	
		if(logFiles == null) {
			throw new IllegalArgumentException("the logFiles parameter must not by null");
		}
		
		if(logFiles.length == 0) {
			throw new IllegalArgumentException("the logFiles array must contain at least one element");
		}
		
		this.logFiles = logFiles;
		
		eventRequests    = new ArrayList<Request>();
		resourceRequests = new ArrayList<Request>();
		feedbackRequests = new ArrayList<Request>();
	}
	
	/**
	 * public method to parse any log files
	 */
	public void parseLogs() throws IOException {
	
		// declare helper variables
		String logFilePath = null;
		String previousLine = null;
		String logLine = null;
		
		String[] tokens = null;
		String[] params = null;
		String[] param  = null;
		
		BufferedReader input = null;
		
		Request request = null;
		
		int lineCount = 0;
	
		// loop through each of the logfiles in turn
		for(int i = 0; i < logFiles.length; i++) {
			if(FileUtils.doesFileExist(logFiles[i]) == false) {
				throw new IOException("unable to open the file at '" + logFiles[i] + "'");
			}
			
			// output some info to the user
			System.out.println("INFO: processing file: " + logFiles[i]);
			
			// open the file
			input = new BufferedReader(new FileReader(logFiles[i]));
			
			// read the file
			while((logLine = input.readLine()) != null) {
			
				lineCount++;
			
				if(logLine.startsWith("INFO: ")) {
					// this is a line of interest
					
					try {
						// build the date
						tokens = previousLine.split(" ");
						tokens = tokens[0].split("/");
					
						// create a new request object
						request = new Request(tokens[2] + "-" + tokens[1] + "-" + tokens[0]);
						
					} catch (ArrayIndexOutOfBoundsException e) {
						System.err.println("ERROR: unable to parse the date on line '" + lineCount + "'");
						continue;
					}
					
					// get the data from the actual line
					tokens = logLine.split(" ");
					
					// get the params
					params = tokens[4].split("&");
					
					for(int x = 0; x < params.length; x++) {
						param = params[x].split("=");
					
						// get the individual parameters
						if(param[0].equals("type")) { // request type
							request.setRequestType(param[1]);
						} else if(param[0].equals("id")) { // request id
							request.setId(param[1]);
						} else if(param[0].equals("output")) { // output type
							request.setOutputType(param[1]);
						} else if(param[0].equals("limit")) { // limit
							request.setRecordLimit(param[1]);
						} else if(param[0].equals("callback")) {
							request.setCallback("y");
						}
					}
					
					// get the ip address and referer
					if(tokens[6].equals("null") == false) {
						request.setInetAddress(tokens[6]);
					} else {
						request.setReferer(tokens[8]);
					}
					
					// determine which type of data request it was
					if(tokens[2].equals("events") == true) {
						eventRequests.add(request);
					} else if(tokens[2].equals("resources") == true) {
						resourceRequests.add(request);
					} else {
						feedbackRequests.add(request);
					}
					
				} else {
					// store this line as it may contain a date for next INFO Line processing
					previousLine = logLine;
				}
			
			}
			
			// rename the file
			input.close();
		}
	}
	
	/**
	 * return the number of new event requests
	 */
	public int getEventRequestCount() {
		return eventRequests.size();
	}
	
	/**
	 * return the number of new resource requests
	 */
	public int getResourceRequestCount() {
		return resourceRequests.size();
	}
	
	/**
	 * return the number of new feedback requests
	 */
	public int getFeedbackRequestCount() {
		return feedbackRequests.size();
	}
	
	public ArrayList<Request> getEventRequests() {
		return eventRequests;
	}
	
	public ArrayList<Request> getResourceRequests() {
		return resourceRequests;
	}
	
	public ArrayList<Request> getFeedbackRequests() {
		return feedbackRequests;
	}
	
	/**
	 * rename any new log files
	 */
	public void renameOldFiles() throws IOException {
		for (int i = 0; i < logFiles.length; i++) {
			FileUtils.renameFile(logFiles[i], logFiles[i] + ".done");
		}
	}
	 
}
