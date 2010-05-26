/*
 * This file is part of the AusStage ABS Data Fix App
 *
 * The AusStage ABS Data Fix App is free software: you can 
 * redistribute it and/or modify it under the terms of the GNU General 
 * Public License as published by the Free Software Foundation, either 
 * version 3 of the License, or (at your option) any later version.
 *
 * The AusStage ABS Data Fix App is distributed in the hope 
 * that it will be useful, but WITHOUT ANY WARRANTY; without even the 
 * implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  
 * See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with the AusStage ABS Data Fix App.  
 * If not, see <http://www.gnu.org/licenses/>.
*/

// import additional packages
import java.io.*;
import java.util.Scanner;


/**
 * A class used to add the dublin core metadata to the overlays
 */
public class AddMetadata extends Tasks {

	/**
	 * Constructor for this class
	 *
	 * @param input  the input file
	 * @param output the output file
	 */
	public AddMetadata(File input, File output) {
		
		super(input, output);
		
	} // end constructor
	
	/**
	 * A method to append the basic dublin core metadata to the 
	 * overlay file
	 *
	 * @return true if, and only if, the task completes successfully
	 */
	public boolean doTask() {
	
		// declare helper variables
		String[] validStates = {"QLD", "NSW", "ACT", "VIC", "TAS", "NT", "SA", "WA"}; // list of valid states
		Scanner inputFromUser = new Scanner(System.in);                               // scanner to get input from user
		String datasetTitle = "";													  // title of this dataset
		String stateAbbr    = "";													  // 2 - 3 letter state abbreviation
		String moreInfoLink = "";													  // link to more information about the overlay
		String urlToTheFile = "";													  // url to the file
		String okToContinue = "n";													  // string to indicate it is ok to continue and use info collected
		
		// loop until it is okToContinue
		while (okToContinue.equals("n") == true) {
		
			// get the dataset title
			datasetTitle = getInfoFromUser(inputFromUser, "PROMPT: Enter the title of the ABS dataset used to construct the overlay:");
	
			// get the state
			while(stateAbbr.length() == 0) {
				stateAbbr    = getInfoFromUser(inputFromUser, "PROMPT: Enter the 2 - 3 letter State abbreviation:");
		
				// change the case of the abbreviation
				stateAbbr = stateAbbr.toUpperCase();
		
				// double check this value
				boolean validState = false;
		
				for(int i = 0; i < validStates.length; i++) {
					if(validStates[i].equals(stateAbbr) == true) {
						validState = true;
						i = validStates.length + 1;
					}
				}
		
				if(validState == false) {
					System.out.println("ERROR: The state abbreviation is not valid.");
					System.out.println("       Valid options are:");
					System.out.println("       " + java.util.Arrays.toString(validStates).replaceAll("[\\]\\[]", ""));
					stateAbbr = "";
				}
			}
	
			// get the link for more information
			moreInfoLink = getInfoFromUser(inputFromUser, "PROMPT: Enter the full URL for more information about this ABS overlay:");
	
			// get the link to this KML file
			urlToTheFile = getInfoFromUser(inputFromUser, "PROMPT: Enter the full URL that is a direct link to this KML:");
			
			// output the collected information
			System.out.println("\nINFO: Collected information is:");
			System.out.println("Dataset Title:       " + datasetTitle);
			System.out.println("State Abbr:          " + stateAbbr);
			System.out.println("More Info Link:      " + moreInfoLink);
			System.out.println("URL to the KML File: " + urlToTheFile);
			
			// prompt to ensure it is ok to continue
			okToContinue = getInfoFromUser(inputFromUser, "PROMPT: Is the above information correct? y / n / abort");
			okToContinue = okToContinue.toLowerCase();
			
			if(okToContinue.equals("y") == false) {
				if(okToContinue.equals("abort") == false) {
					datasetTitle = "";
					stateAbbr    = "";
					moreInfoLink = "";
					urlToTheFile = "";
					okToContinue = "n";
				} else {
					System.out.println("INFO: Aborting data fix");
					System.exit(0);
				}
			}
			
		} // end data gathering loop
		
		return false;
	}
	
	/**
	 * A method to gather information from the user via the command line and a prompt
	 *
	 * @param scanner     the scanner to use to get the input
	 * @param message     the message to display to the user
	 * @param nullAllowed set to true if the user isn't required to enter information
	 *
	 * @return            the filtered string entered by the user (new line characters removed etc.)
	 */
	private String getInfoFromUser(Scanner input, String message, boolean nullAllowed) {
	
		// double check the variables
		if(input == null) {
			throw new RuntimeException("A valid scanner object is required");
		}
		
		// a valid message is required
		message = filterString(message);
		
		// declare helper variables
		String fromUser = "";
		
		// loop until we get the input we want
		if(nullAllowed == true) {
		
			// just return whatever we get
			System.out.println(message);
			fromUser = input.nextLine();
			return fromUser.trim();
			
		} else {
			
			// loop until the input is valid
			while(fromUser.length() == 0) {
				// print the message
				System.out.println(message);

				// gather input
				fromUser = input.nextLine();

				// trim the string
				fromUser = fromUser.trim();

				// check on what we got from the user
				if(fromUser.length() == 0) {
					System.out.println("Error: Invalid input, empty strings are not allowed");
				}
			} // end loop gathering input

			// return what we got from the user
			return fromUser;
		}	
	} // end getInfoFromUser method
	
	/**
	 * A method to gather information from the user via the command line and a prompt
	 * empty strings are not allowed
	 *
	 * @param scanner     the scanner to use to get the input
	 * @param message     the message to display to the user
	 *
	 * @return            the filtered string entered by the user (new line characters removed etc.)
	 */
	private String getInfoFromUser(Scanner input, String message) {
	
		// double check the variables
		if(input == null) {
			throw new RuntimeException("A valid scanner object is required");
		}
		
		// a valid message is required
		message = filterString(message);
		
		// use the standard method just pass in false for the nullAllowed parameter
		return getInfoFromUser(input, message, false);
		
	} // end getInfoFromUser method
	
}
