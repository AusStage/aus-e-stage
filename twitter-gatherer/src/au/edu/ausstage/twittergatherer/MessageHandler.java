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

// import additional packages
import com.crepezzi.tweetstream4j.*;
import com.crepezzi.tweetstream4j.types.*;

// import additional ausstage packages
import au.edu.ausstage.utils.*;

/**
 * Custom TwitterStreamHandler for handling Twitter Messages
 */
public class MessageHandler implements TwitterStreamHandler {

	/**
	 * A method to process a new tweet message from the stream
	 *
	 * @param tweet the new Tweet
	 */
	public void addTweet(STweet tweet) {
	
	} // end addTweet Method

	/**
	 * A method to process a deletion message from the stream
	 *
	 * @param deletion Incoming deletion request
	 */
	public void addDeletion(SDeletion deletion) {
	
	
	} // end the addDeletion Method
	

	/**
	 * A method to process a new limit message from the stream
	 * @param limit Incoming limit message
	 */
	public void addLimit(SLimit limit) {
	
	
	}

	/**
	 * What to do when this handler has been request to stop
	 */
	public void stop() {

	}
	
} // end class definition
