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

// import additional packages
import java.io.*;

/**
 * Main driving class for the AusStage ABS Data Fix App
 */
public class RdfExport {

	// Version information 
	private static final String VERSION    = "1.0.0";
	private static final String BUILD_DATE = "2010-06-01";
	private static final String INFO_URL   = "http://code.google.com/p/aus-e-stage/wiki/RdfExport";
	
	// Valid tasks
	private static final String[] TASK_TYPES = {"build-network-data"};
	
	/**
	 * Main driving method for the AusStage ABS Data Fix App
	 */
	public static void main(String args[]) {
	
		// output some basic information
		System.out.println("AusStage RdfExport - Export AusStage data into a variety of data formats");
		System.out.println("Version: " + VERSION + " Build Date: " + BUILD_DATE);
		System.out.println("More Info: " + INFO_URL + "\n");
		
		// get the command line params
		SimpleCommandLineParser parser = new SimpleCommandLineParser(args); // use a Google class to do manage command line params
		
		// get the parameters
		String taskType = parser.getValue("task", "tasktype");
		
		// check on the parameters
		if(taskType == null) {
			// output usage instructions
			System.err.println("ERROR: the following parameters are expected");
			System.err.println("-tasktype  the type of task to undertake");
			System.err.println("\nValid fix types are:");
			System.err.println(java.util.Arrays.toString(TASK_TYPES).replaceAll("[\\]\\[]", ""));
			System.exit(-1);
		}
		
		// check on the fix type
		boolean validTaskType = false;
		
		for(int i = 0; i < TASK_TYPES.length; i++) {
			if(TASK_TYPES[i].equals(taskType)) {
				validTaskType = true;
			}
		}
		
		if(validTaskType != true) {
			// no valid task type was found
			System.err.println("ERROR: the specified task type is invalid");
			System.err.println("Valid fix types are:");
			System.err.println(java.util.Arrays.toString(TASK_TYPES).replaceAll("[\\]\\[]", ""));
			System.exit(-1);
		}
		
		//debug code
		System.out.println("Everything OK So far");
		
	} // end main method
	
} // end the class definition
