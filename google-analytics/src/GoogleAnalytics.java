/*
 * This file is part of the AusStage Google Analytics Report Generator
 *
 * The AusStage Google Analytics Report Generator is free software: you can 
 * redistribute it and/or modify it under the terms of the GNU General 
 * Public License as published by the Free Software Foundation, either 
 * version 3 of the License, or (at your option) any later version.
 *
 * The AusStage Google Analytics Report Generator is distributed in the hope 
 * that it will be useful, but WITHOUT ANY WARRANTY; without even the 
 * implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  
 * See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with the AusStage Google Analytics Report Generator.  
 * If not, see <http://www.gnu.org/licenses/>.
*/

/**
 * Main driving class for the AusStage Google Analytics Report Generator app
 */
public class GoogleAnalytics {

	/**
	 * Main driving method for the Google Analytics Report Generator app
	 */
	public static void main(String args[]) {
	
		// get the command line options
		SimpleCommandLineParser parser = new SimpleCommandLineParser(args); // use a Google class to do this
		
		// check on the parameters
		String email    = parser.getValue("email");
		String password = parser.getValue("password", "pass");
		String action   = parser.getValue("action");
		
		if(email == null || password == null || action == null) {
			// output some helpful information
			System.out.println("ERROR: the following parameters are expected");
			System.out.println("-email    the email address of a google account");
			System.out.println("-password the password of the google account");
			System.out.println("-action   either \"list\" or \"report\"");
			System.out.println("          when set to \"list\" a list of available profiles will be returned");
			System.out.println("          when set to \"report\" a report will be generated");
			System.out.println("-profile  unique id of the profile used to generate the report");
			System.out.println("-output   the name & location of the report file to generate");
			System.exit(-1);
		}
	
	} // end main method

} // end class definition
