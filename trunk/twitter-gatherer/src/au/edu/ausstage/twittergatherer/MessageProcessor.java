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

// import the JSON object
import net.sf.json.JSONObject;
import net.sf.json.JSONNull;

// import the Joda Time object
import org.joda.time.DateTime;
import org.joda.time.LocalDateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

// import additional ausstage packages
import au.edu.ausstage.utils.*;

/**
 * A class to process Twitter messages by retrieving them from the queue populated
 * by the IncomingMessageHandler class
 */
public class MessageProcessor implements Runnable {

	// declare class level private variables
	LinkedBlockingQueue<STweet> newTweets;
	String logFiles;
	DbManager database;
	DateTimeFormatter dateTimeFormat;
	DateTimeFormatter inputDateTimeFormat;
	
	// declare class level constants
	private final int JSON_INDENT_LEVEL = 4;
	
	private final String SOURCE_ID = "1";
	
	private final String[] SANITISED_FIELDS = {"url", "profile_image_url", "screen_name", "profile_background_image_url", "name", "description"};
	
	private final String DB_DATE_TIME_FORMAT         = "DD-MON-YYYY HH24:MI:SS";
	private final String JODA_DATE_TIME_FORMAT       = "dd-MMM-YYYY HH:mm:ss";
	//Fri Aug 13 05:37:05 +0000 2010
	private final String JODA_INPUT_DATE_TIME_FORMAT = "E MMM dd hh:mm:ss Z YYYY";
	
	/**
	 * A constructor for this class
	 *
	 * @param tweets   a blocking queue used to store new messages
	 * @param logDir   the directory to log files to
	 * @param manager  a valid DbManager object
	 *
	 */
	public MessageProcessor(LinkedBlockingQueue<STweet> tweets, String logDir, DbManager manager) {
	
		// check on the parameters
		if(tweets == null) {
			throw new IllegalArgumentException("The tweets parameter cannot be null");
		}
		
		if(InputUtils.isValid(logDir) == false) {
			throw new IllegalArgumentException("The log directory parameter cannot be null or empty");
		}
		
		if(FileUtils.doesDirExist(logDir) == false) {
			throw new IllegalArgumentException("The specified log directory is not valid");
		}
		
		if(manager == null) {
			throw new IllegalArgumentException("The database parameter cannot be null");
		}
		
		// assign parrameters to local variables
		newTweets = tweets;
		logFiles = logDir;
		database = manager;
		
		// build the dateTimeFormat object
		dateTimeFormat      = DateTimeFormat.forPattern(JODA_DATE_TIME_FORMAT);
		inputDateTimeFormat = DateTimeFormat.forPattern(JODA_INPUT_DATE_TIME_FORMAT);
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
				
				// take a tweet from the queue
				tweet = newTweets.take();
				
				// get the tweet id number
				String tweetId = Long.toString(tweet.getStatusId());
				
				// get a hash of the tweet id
				String tweetIdHash = HashUtils.hashValue(tweetId);
				
				// get the user
				STweetUser user = tweet.getUser();
				
				// get the user id numbers
				String userId = Long.toString(user.getUserId());
				
				// get a hash of the user id
				String userIdHash = HashUtils.hashValue(userId);
				
				// get the JSON value of the tweet
				JSONObject jsonTweetObject = tweet.getJSON();
				
				// get the JSON value of the user
				JSONObject jsonUserObject = user.getJSON();
				
				/*
				 * Sanitise the message
				 */
				jsonTweetObject = jsonTweetObject.discard("id");
				jsonTweetObject = jsonTweetObject.element("id", tweetIdHash);
				
				//"in_reply_to_status_id"
				
				// replace the "in_reply_to_user_id" field with a hash if it is present
				try {
					// if it isn't present an exception is thrown
					String replyId = Long.toString(jsonTweetObject.getLong("in_reply_to_user_id"));
					jsonTweetObject = jsonTweetObject.discard("in_reply_to_user_id");
					jsonTweetObject = jsonTweetObject.element("in_reply_to_user_id", HashUtils.hashValue(replyId));
									
				} catch (net.sf.json.JSONException ex) {}
				
				// replace the "in_reply_to_status_id" field with a hash if it is present
				try {
					// if it isn't present an exception is thrown
					String replyId = Long.toString(jsonTweetObject.getLong("in_reply_to_status_id"));
					jsonTweetObject = jsonTweetObject.discard("in_reply_to_status_id");
					jsonTweetObject = jsonTweetObject.element("in_reply_to_status_id", HashUtils.hashValue(replyId));
									
				} catch (net.sf.json.JSONException ex) {}
				
				// remove the "retweeted_status" field if present
				try {
					// if it isn't present an exception is thrown
					JSONObject retweet = jsonTweetObject.getJSONObject("retweeted_status");
					jsonTweetObject = jsonTweetObject.discard("retweeted_status");
					
					// get the id of the retweet
					String retweetId = Long.toString(retweet.getLong("id"));
					
					// add a hash of the id back into the object
					jsonTweetObject = jsonTweetObject.element("retweeted_status", HashUtils.hashValue(retweetId));
									
				} catch (net.sf.json.JSONException ex) {}
				
				/*
				 * Sanitise the user
				 */
				 
				// loop through the list of sanitised fields
				for(int i = 0; i < SANITISED_FIELDS.length; i++) {
					jsonUserObject = jsonUserObject.discard(SANITISED_FIELDS[i]);
					jsonUserObject = jsonUserObject.element(SANITISED_FIELDS[i], JSONNull.getInstance());
				}
				
				// fix the user id
				jsonUserObject = jsonUserObject.discard("id");
				jsonUserObject = jsonUserObject.element("id", userIdHash);
				
				// replace the existing user with the new one
				jsonTweetObject = jsonTweetObject.discard("user");
				jsonTweetObject = jsonTweetObject.element("user", jsonUserObject);				
				
				
				/*
				 * Write the file
				 */
				
				if(FileUtils.writeNewFile(logFiles + "/" + tweetIdHash, jsonTweetObject.toString(JSON_INDENT_LEVEL)) == false) {
					System.err.println("ERROR: Unable to write file to the specified log file");
					System.err.println("       twitter message with id '" + tweetId + "' is now lost");
				} else {
					System.out.println("INFO: New Message written to the log directory:");
					System.out.println("      " + tweetIdHash);
				}
				
				/*
				 * TODO: determine which performance this message is about
				 */
				 
				/*
				 * get the date & time that the messages was received
				 */
				DateTime messageCreated = inputDateTimeFormat.parseDateTime(jsonTweetObject.getString("created_at"));
				LocalDateTime localMessageCreated = messageCreated.toLocalDateTime();
				 
				/*
				 * write the message to the database
				 */
				 
				// define the sql
				String insertSql = "INSERT INTO mob_feedback "
								 + "(performance_id, question_id, source_type, received_date_time, received_from, source_id, short_content) "
								 + "VALUES (?,?,?, TO_DATE(?, '" + DB_DATE_TIME_FORMAT + "'),?,?,?)";
								 
				// define the parameter array
				String[] sqlParameters = new String[7];
				
				// build the parameters
				// TODO: determine valid performance and question ids

				//debug code
				sqlParameters[0] = "1";
				sqlParameters[1] = "1";
				
				// use the sourvce id from the constant
				sqlParameters[2] = SOURCE_ID;
				
				// add the date and time
				sqlParameters[3] = dateTimeFormat.print(messageCreated);
				
				// add the user id
				sqlParameters[4] = jsonUserObject.getString("id");
				
				// add the source id
				sqlParameters[5] = jsonTweetObject.getString("id");
				
				// add the message
				sqlParameters[6] = jsonTweetObject.getString("text");
				
				// insert the data
				if(database.executePreparedInsertStatement(insertSql, sqlParameters) == false) {
					System.err.println("ERROR: Unable to add the message to the database");
				} else {
					System.out.println("INFO: Successfully added the message to the database");
				}						
			}
		} catch (InterruptedException ex) {
			// thread has been interrupted
			System.out.println("INFO: The Message processing thread has stopped");
		}
	} // end the run method

}
