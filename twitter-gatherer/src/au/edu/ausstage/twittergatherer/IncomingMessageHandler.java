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
import java.util.concurrent.LinkedBlockingQueue;

// import additional tweetStream4J packages
import com.crepezzi.tweetstream4j.*;
import com.crepezzi.tweetstream4j.types.*;

// import additional ausstage packages
import au.edu.ausstage.utils.*;

/*
 * Official Twitter Docs 
 *
 * http://dev.twitter.com/pages/streaming_api_methods
 */

/**
 * Custom TwitterStreamHandler for handling Twitter Messages
 */
public class IncomingMessageHandler implements TwitterStreamHandler {

	// declare class level private variables
	private LinkedBlockingQueue<STweet> newTweets;
	private LinkedBlockingQueue<SDeletion> newDeletes;
	
	/**
	 * A constructor for this class
	 *
	 * @param tweets  a blocking queue used to store new messages
	 * @param deletes a blocking queue used to store new deletion requests
	 */
	public IncomingMessageHandler(LinkedBlockingQueue<STweet> tweets, LinkedBlockingQueue<SDeletion> deletes) {
		
		// assign parrameters to local variables
		newTweets  = tweets;
		newDeletes = deletes;
	}

	/**
	 * A method to process a new tweet message from the stream
	 *
	 * @param tweet the new Tweet
	 */
	public void addTweet(STweet tweet) {
	
		// try to add this tweet to the queue for processing
		try {
			// add this tweet to the queue
			newTweets.put(tweet);
		} catch (InterruptedException ex) {
			// inform user of error
			System.err.println("ERROR: An error occured while attempting to add a tweet to the queue");
			System.err.println("       " + tweet.toString());
		}
	
	} // end addTweet Method

	/**
	 * A method to process a deletion message from the stream
	 *
	 * @param deletion Incoming deletion request
	 */
	public void addDeletion(SDeletion deletion) {
	
		//debug code
		System.out.println("### New Deletion Request Arrived ###");
	
		// try to add this tweet to the queue for processing
		try {
			// add this tweet to the queue
			newDeletes.put(deletion);
		} catch (InterruptedException ex) {
			// inform user of error
			System.err.println("ERROR: An error occured while attempting to add a deletion request to the queue");
			System.err.println("       " + deletion.toString());
		}
	
	} // end the addDeletion Method
	

	/**
	 * A method to process a new limit message from the stream
	 *
	 * From the docs: http://dev.twitter.com/pages/streaming_api_concepts
	 * Track streams may also contain limitation notices, where the integer track is an enumeration of statuses that, since the start of the connection, matched the track predicate but were rate limited.
	 * @param limit Incoming limit message
	 */
	public void addLimit(SLimit limit) {
	
		// limit notice received
		System.out.println("New Limit Notice: " + limit.toString());	
	
	}

	/**
	 * What to do when this handler has been request to stop
	 */
	public void stop() {
	
		// output a message
		System.out.println("INFO: Twitter Message Handler Stopped.");

	}
	
} // end class definition
