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
import au.edu.ausstage.exchangeanalytics.types.Request;

import java.util.ArrayList;

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
	}


}
