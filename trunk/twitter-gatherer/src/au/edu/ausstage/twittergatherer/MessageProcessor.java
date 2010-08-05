/*
 * This file is part of the AusStage Twitter Gatherer
 *
 * The AusStage Twitter Gatherer is free software: you can redistribute
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
 * along with the AusStage Twitter Gatherer.  
 * If not, see <http://www.gnu.org/licenses/>.
*/

package au.edu.ausstage.twittergatherer;

// import from the standard java libraries
import java.util.concurrent.*;

// import additional tweetStream4J packages
import com.crepezzi.tweetstream4j.*;
import com.crepezzi.tweetstream4j.types.*;

// import additional ausstage packages
import au.edu.ausstage.utils.*;

/**
 * A class to process Twitter messages by retrieving them from the queue populated
 * by the IncomingMessageHandler class
 */
public class MessageProcessor implements Runnable {

	// declare class level private variables
	LinkedBlockingQueue<STweet> newTweets;
	
	/**
	 * A constructor for this class
	 *
	 * @param tweets a blocking queue used to store new messages
	 */
	public MessageProcessor(LinkedBlockingQueue<STweet> tweets) {
		
		// assign parrameters to local variables
		newTweets = tweets;
	}
	
	/**
	 * A method that is run in the thread
	 * Takes a tweet from the queue and processes it
	 */
	public void run() {
	
		// declare helper variables
		STweet tweet = null;
		
		try {
			// infinite loop to keep the thread running
			while(true) {
				tweet = newTweets.take();
				
				//debug code
				System.out.println("INFO: New Tweet Recieved ");
				System.out.println("      " + tweet.toString());
			}
		} catch (InterruptedException ex) {
			// inform user of error
			System.err.println("ERROR: An error occured while attempting to take a tweet from the queue");
			System.err.println("       " + tweet.toString());
		}
	}
}
