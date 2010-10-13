/*
 * This file is part of the AusStage Tweet Gatherer
 *
 * The AusStage Tweet Gatherer is free software: you can redistribute
 * it and/or modify it under the terms of the GNU General Public License 
 * as published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * The AusStage Twitter Gatherer is distributed in the hope that it will 
 * be useful, but WITHOUT ANY WARRANTY; without even the implied warranty 
 * of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with the AusStage Tweet Gatherer.  
 * If not, see <http://www.gnu.org/licenses/>.
*/

package au.edu.ausstage.tweetgatherer;

// import additional ausstage packages
import au.edu.ausstage.utils.*;

// import additional tweetStream4J packages
import com.crepezzi.tweetstream4j.*;
import com.crepezzi.tweetstream4j.types.*;

/**
 * Main driving class for the TweetGatherer application
 */
public class TweetGatherer {

	// Version information 
	private static final String VERSION    = "1.0.0";
	private static final String BUILD_DATE = "2010-10-13";
	private static final String INFO_URL   = "http://code.google.com/p/aus-e-stage/wiki/TweetGatherer";
	
	// default properties
	private static final String[] CONSUMER_PROPERTIES = {"consumer-key", "consumer-secret"};
	
	/**
	 * Main driving method for the TweetGatherer application
	 */
	public static void main(String args[]) {
	
		// output some basic information
		System.out.println("AusStage Tweet Gatherer - Gather Performance Feedback from Twitter");
		System.out.println("Version: " + VERSION + " Build Date: " + BUILD_DATE);
		System.out.println("More Info: " + INFO_URL + "\n");
		
		// output the date 	
	 	System.out.println("INFO: Application Started: " + DateUtils.getCurrentDateAndTime());
	 	
	 	// get the command line arguments
	 	CommandLineParser cli = new CommandLineParser(args);
	 	
	 	// process the list of arguments
	 	String propsPath = cli.getValue("properties");
	 	
	 	// double check the parameter
	 	if(propsPath == null) {
	 		System.err.println("ERROR: the following parameters are expected:");
	 		System.err.println("-properties the location of the properties file");
	 		System.exit(-1); 		
	 	}
	 	
	 	// load the properties
	 	PropertiesManager properties = new PropertiesManager();
	 	
	 	if(properties.loadFile(propsPath) == false) {
	 		System.err.println("ERROR: unable to open the specified properties file");
	 		System.exit(-1);
	 	}
	 	
	 	// check on the pin-code property if necessary
	 	if(cli.containsKey("getauthtoken") == false) {
		 	if(InputUtils.isValid(properties.getValue("oauth-token")) == false || InputUtils.isValid(properties.getValue("oauth-secret")) == false) {
		 		System.err.println("ERROR: the 'oauth-token' and 'oauth-secret' properties could not be found");
		 		System.err.println("       If necessary use the '-getauthtoken=yes' property to retrieve new tokens");
		 		System.err.println("       or contact the AusStage project manager.");
		 		System.exit(-1);
		 	}
		 } else {
		 
		 	// check on the required properties
		 	if(InputUtils.isValid(properties.getValue("consumer-key")) == false || InputUtils.isValid(properties.getValue("consumer-secret")) == false) {
		 		System.err.println("ERROR: the 'consumer-key' and 'consumer-secret' properties could not be found");
		 		System.exit(-1);
		 	}
		 	
		 	// we need to get a the OAuth tokens
		 	try{
			 	TwitterStreamConfiguration tws = new TwitterStreamConfiguration(properties.getValue("consumer-key"), properties.getValue("consumer-secret"));
				String url = tws.getAuthorizationUrl();
				System.out.println("INFO: Visit the following URL to retrieve the PIN");
				System.out.println("      " + url);
				System.out.println("      Enter the PIN code below:");
				
				// prepare to retrieve input from the user
				java.util.Scanner input = new java.util.Scanner(System.in);
				String pinCode = input.next();
				
				// use the pin code
				tws.authorize(pinCode);
				
				// get the user to store these details
				System.out.println("Store the following value as the 'oauth-token' property");
				System.out.println(tws.getToken());
				System.out.println("Store the following value as the 'oauth-secret' property");
				System.out.println(tws.getTokenSecret());
				System.exit(0);
				
			} catch (oauth.signpost.exception.OAuthException ex) {
				System.err.println("ERROR: An error occuring during an OAuth process. Details are:");
				System.err.println("      " + ex.toString());
				System.err.println("      If the problem persists, contact the AusStage team");
			}
		}

	 	
	 	
		
	} // end the main method
	
	
} // end class definition
