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

// import from the standard java libraries
import java.util.concurrent.*;
import java.util.List;
import java.util.ListIterator;
import java.sql.ResultSet;

// import additional tweetStream4J packages
import com.crepezzi.tweetstream4j.*;
import com.crepezzi.tweetstream4j.types.*;

// import the Joda Time object
import org.joda.time.DateTime;
import org.joda.time.LocalDateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

// import the twitter-text-java library
import com.twitter.Extractor;

// import from the Google gson library
import com.google.gson.JsonObject;
import com.google.gson.JsonElement;

// import additional ausstage packages
import au.edu.ausstage.utils.*;

/**
 * A class to process Twitter messages by retrieving them from the queue populated
 * by the IncomingMessageHandler class
 */
public class MessageProcessor implements Runnable {

	// declare class level private variables
	private LinkedBlockingQueue<STweet> newTweets;
	private String logFiles;
	private DbManager database;
	private DateTimeFormatter dateTimeFormat;
	private DateTimeFormatter inputDateTimeFormat;
	private Extractor         extractHashTags;
	private EmailManager      emailManager;
	
	// declare class level constants
	private final int JSON_INDENT_LEVEL = 4;
	
	public final String SOURCE_ID = "1";
	
	private final String[] SANITISED_FIELDS = {"url", "profile_image_url", "screen_name", "profile_background_image_url", "name", "description"};
	
	private final String DB_DATE_TIME_FORMAT         = "DD-MON-YYYY HH24:MI:SS";
	private final String JODA_DATE_TIME_FORMAT       = "dd-MMM-YYYY HH:mm:ss";

	private final String JODA_INPUT_DATE_TIME_FORMAT = "E MMM dd HH:mm:ss Z YYYY";
	
	private final String EMAIL_SUBJECT = "[AusStage Tweet Gatherer] Message Processing Error";
	private final String EMAIL_MESSAGE = "Exception Report: The Tweet Gatherer was unable to locate a valid performance for this attached Twitter Message";
	
	/**
	 * A constructor for this class
	 *
	 * @param tweets   a blocking queue used to store new messages
	 * @param logDir   the directory to log files to
	 * @param manager  a valid DbManager object
	 *
	 */
	public MessageProcessor(LinkedBlockingQueue<STweet> tweets, String logDir, DbManager manager, EmailManager emailManager) {
	
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
		
		if(emailManager == null) {
			throw new IllegalArgumentException("The EmailManager parameter cannot be null");
		}
		
		// assign parrameters to local variables
		newTweets = tweets;
		logFiles = logDir;
		database = manager;
		this.emailManager = emailManager;
		
		// build the dateTimeFormat object
		dateTimeFormat      = DateTimeFormat.forPattern(JODA_DATE_TIME_FORMAT);
		inputDateTimeFormat = DateTimeFormat.forPattern(JODA_INPUT_DATE_TIME_FORMAT);
		
		// build the Extractor object
		extractHashTags = new Extractor();
	}
	
	/**
	 * A method that is run in the thread
	 * Takes a tweet from the queue and processes it
	 */
	public void run() {
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
				
				// get the user id
				String userId = Long.toString(user.getUserId());
				
				// get a hash of the user id
				String userIdHash = HashUtils.hashValue(userId);
				
				// get the JSON value of the tweet
				JsonObject jsonTweetObject = sanitiseMessage(tweet.getJSON(), tweetIdHash);
				
				// get the JSON value of the user
				JsonObject jsonUserObject = sanitiseUser(user.getJSON(), userIdHash);
				
				// replace the user with the sanitised version
				jsonTweetObject.remove("user");
				jsonTweetObject.addProperty("user", "");
				
				/*
				 * Write the file
				 */
				
				if(FileUtils.writeNewFile(logFiles + "/" + tweetIdHash, jsonTweetObject.toString() + "\n" + jsonUserObject.toString()) == false) {
					System.err.println("ERROR: Unable to write file to the specified log file");
					System.err.println("       twitter message with id '" + tweetId + "' is now lost");
				} else {
					System.out.println("INFO: New Message written to the log directory:");
					System.out.println("      " + tweetIdHash);
				}
				
				/*
				 * get the hash tags from this message
				 */
				
				// get the list of hash tags from the tweet
				JsonElement elem = jsonTweetObject.get("text");
				List<String> hashtags = extractHashTags.extractHashtags(elem.getAsString());
				
				// convert all of the found hash tags to lower case
				ListIterator <String> hashtagsIterator = hashtags.listIterator();
		
				while(hashtagsIterator.hasNext() == true) {
		
					// get the next tage in the list
					String tmp = (String) hashtagsIterator.next();
			
					tmp = tmp.toLowerCase();
			
					// replace the value we retrieved with this updated one
					hashtagsIterator.set(tmp);
				}
				
				// see if this is a company performance
				if(isCompanyPerformance(hashtags, jsonTweetObject, jsonUserObject, tweetIdHash) == false) {
					// this isn't a company performance
					// is it using a performance specific id?
					if(isPerformance(hashtags, jsonTweetObject, jsonUserObject, tweetIdHash) == false) {
						//this isn't a valid performance
						System.err.println("ERROR: Unable to find a matching performance for this message");
						
						// send an exception report
						if(emailManager.sendMessageWithAttachment(EMAIL_SUBJECT, EMAIL_MESSAGE, logFiles + "/" + tweetIdHash) == false) {
							System.err.println("ERROR: Unable to send the exception report");
						}
					}
				}								
			}
		
		} catch (InterruptedException ex) {
			// thread has been interrupted
			System.out.println("INFO: The Message processing thread has stopped");
		}
	} // end the run method
	
	/**
	 * A private method to sanitise the message
	 *
	 * @param jsonTweetObject the original message object
	 * @param idHash          the hash of the message id
	 *
	 * @return                the sanitised message object
	 */
	private JsonObject sanitiseMessage(JsonObject jsonTweetObject, String idHash) {
	
		// remove the id and replace it with the hash
		jsonTweetObject.remove("id");
		jsonTweetObject.addProperty("id", idHash);
		
		// replace the "in_reply_to_user_id" field with a hash if present
		if(jsonTweetObject.has("in_reply_to_user_id") == true) {

			// get a hash of the value
			JsonElement elem = jsonTweetObject.get("in_reply_to_user_id");
			String      hash = HashUtils.hashValue(elem.getAsString());
			
			// update the value
			jsonTweetObject.remove("in_reply_to_user_id");
			jsonTweetObject.addProperty("in_reply_to_user_id", hash);
		}
		
		// replace the "in_reply_to_status_id" field with a hash if present
		if(jsonTweetObject.has("in_reply_to_status_id") == true) {

			// get a hash of the value
			JsonElement elem = jsonTweetObject.get("in_reply_to_status_id");
			String      hash = HashUtils.hashValue(elem.getAsString());
			
			// update the value
			jsonTweetObject.remove("in_reply_to_status_id");
			jsonTweetObject.addProperty("in_reply_to_status_id", hash);
		}
		
		// replace the "retweeted_status" field with a hash if present
		if(jsonTweetObject.has("retweeted_status") == true) {

			// get a hash of the value
			JsonElement elem = jsonTweetObject.get("retweeted_status");
			String      hash = HashUtils.hashValue(elem.getAsString());
			
			// update the value
			jsonTweetObject.remove("retweeted_status");
			jsonTweetObject.addProperty("retweeted_status", hash);
		}
			
		// return the sanitised object
		return jsonTweetObject;
	
	}
	
	/**
	 * A private method to sanitise the message
	 *
	 * @param jsonTweetObject the original user object
	 * @param idHash          the hash of the user id
	 *
	 * @return                the sanitised user object
	 */
	private JsonObject sanitiseUser(JsonObject jsonTweetObject, String idHash) {
	
		// remove the id and replace it with the hash
		jsonTweetObject.remove("id");
		jsonTweetObject.addProperty("id", idHash);
		
		// loop through the list of sanitised fields
		for(int i = 0; i < SANITISED_FIELDS.length; i++) {
			if(jsonTweetObject.has(SANITISED_FIELDS[i]) == true) {
				jsonTweetObject.remove(SANITISED_FIELDS[i]);
				jsonTweetObject.addProperty(SANITISED_FIELDS[i],"");
			}
		}
		
		// return the sanitised object
		return jsonTweetObject;	
	}
	
	/**
	 * A private method to process a message containing a company hash tag
	 *
	 * @param hashtags        a list of hashtags associated with this message
	 * @param jsonTweetObject the message
	 * @param jsonUserObject  the user object
	 * @param tweetIdHash     the hash of the message id
	 *
	 * @return                true if, and only if, the message was processed successfully
	 */
	private boolean isPerformance(List<String> hashtags, JsonObject jsonTweetObject, JsonObject jsonUserObject, String tweetIdHash) {
	
		return false;
	
	}
	
	/**
	 * A private method to process a message containing a company hash tag
	 *
	 * @param hashtags        a list of hashtags associated with this message
	 * @param jsonTweetObject the message
	 * @param jsonUserObject  the user object
	 * @param tweetIdHash     the hash of the message id
	 *
	 * @return                true if, and only if, the message was processed successfully
	 */
	private boolean isCompanyPerformance(List<String> hashtags, JsonObject jsonTweetObject, JsonObject jsonUserObject, String tweetIdHash) {
	
		/*
		 * get the date & time that the message was received
		 */
		JsonElement elem = jsonTweetObject.get("created_at");
		DateTime messageCreated = inputDateTimeFormat.parseDateTime(elem.getAsString());
		LocalDateTime localMessageCreated = messageCreated.toLocalDateTime();
		
		/*
		 * get the performance and question id
		 */
		String performanceId = null;
		String questionId    = null;
		
		// define the sql				
		String selectSql = "SELECT performance_id, question_id, SUBSTR(twitter_hash_tag, 2) "
						 + "FROM mob_performances, mob_organisations, orgevlink "
						 + "WHERE mob_performances.event_id = orgevlink.eventid "
						 + "AND mob_organisations.organisation_id = orgevlink.organisationid "
						 + "AND start_date_time < TO_DATE(?, '" + DB_DATE_TIME_FORMAT + "') "
						 + "AND end_date_time + 1/12 > TO_DATE(?, '" + DB_DATE_TIME_FORMAT + "') ";

		// define the parameters array
		String[] sqlParameters = new String[2];
		sqlParameters[0] = dateTimeFormat.print(messageCreated);
		sqlParameters[1] = dateTimeFormat.print(messageCreated);
		
		// get the data
		DbObjects results = database.executePreparedStatement(selectSql, sqlParameters);
		
		// double check the results
		if(results == null) {
			System.err.println("ERROR: Unable to execute the SQL to lookup performances");
			
			// send an exception report
			if(emailManager.sendMessageWithAttachment(EMAIL_SUBJECT, EMAIL_MESSAGE, logFiles + "/" + tweetIdHash) == false) {
				System.err.println("ERROR: Unable to send the exception report");
			}

		} else {
		
			// look for performances matching the hash tag from the message
			ResultSet resultSet = results.getResultSet();
			
			// loop through the resultset
			boolean found = false; // exit the loop early
			sqlParameters = new String[7]; // store the parameters
			
			try {
			
				while (resultSet.next() && found == false) {
			
					// see if the hashtags in the message match one in the DB
					if(hashtags.contains(resultSet.getString(3)) == true) {
						// yep
						sqlParameters[0] = resultSet.getString(1);
						sqlParameters[1] = resultSet.getString(2);
						found = true;
					}
				}
			}catch (java.sql.SQLException ex) {
				found = false;
			}
			
			if(found == false) {
				// no performance with a matching company id was found
				return false;
			} else {
				
				/*
				 * write the message to the database
				 */
				 
				// define the sql
				String insertSql = "INSERT INTO mob_feedback "
								 + "(performance_id, question_id, source_type, received_date_time, received_from, source_id, short_content) "
								 + "VALUES (?,?,?, TO_DATE(?, '" + DB_DATE_TIME_FORMAT + "'),?,?,?)";
	
				// use the source id from the constant
				sqlParameters[2] = SOURCE_ID;
	
				// add the date and time
				sqlParameters[3] = dateTimeFormat.print(messageCreated);
	
				// add the user id
				elem = jsonUserObject.get("id");
				sqlParameters[4] = elem.getAsString();
	
				// add the source id
				elem = jsonTweetObject.get("id");
				sqlParameters[5] = elem.getAsString();
	
				// add the message
				elem = jsonUserObject.get("text");
				sqlParameters[6] = elem.getAsString();
	
				// insert the data
				if(database.executePreparedInsertStatement(insertSql, sqlParameters) == false) {
					System.err.println("ERROR: Unable to add the message to the database");
					
					// send an exception report
					if(emailManager.sendMessageWithAttachment(EMAIL_SUBJECT, "Exception Report: The Tweet Gatherer was unable to store this message in the database", logFiles + "/" + tweetIdHash) == false) {
						System.err.println("ERROR: Unable to send the exception report");
					}
				
				} else {
					System.out.println("INFO: Successfully added the message to the database");
					return true;
				}	
			}
		} // end performance check
		
		// if we get this far, something bad happend
		return false;	
	}
}
