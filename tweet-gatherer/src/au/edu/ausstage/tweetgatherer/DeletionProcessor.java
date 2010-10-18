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
import java.sql.ResultSet;

// import additional tweetStream4J packages
import com.crepezzi.tweetstream4j.*;
import com.crepezzi.tweetstream4j.types.*;

// import additional ausstage packages
import au.edu.ausstage.utils.*;

/**
 * A class to process Twitter deletions requests by marking them as non public in the database and
 * ammending the file name in the log directory by retrieving them from the queue populated by the
 * IncomingMessageHandler class
 */
public class DeletionProcessor implements Runnable {

	// declare class level private variables
	private LinkedBlockingQueue<SDeletion> newDeletes;
	private String                         logFiles;
	private DbManager                      database;
	private EmailManager                   emailManager;
	
	// declare class level constants
	
	public final String SOURCE_ID = "1";
	
	private final String EMAIL_SUBJECT = "[AusStage Twitter Gatherer] Deletion Processing Error";
	private final String EMAIL_MESSAGE = "Exception Report: The Twitter Gatherer was unable to process the deletion request for message with id: ";
	
	/**
	 * A constructor for this class
	 *
	 * @param deletes      a blocking queue used to store new messages
	 * @param logDir       the directory to log files to
	 * @param manager      a valid DbManager object
	 * @param emailManager a valid EmailManager object
	 *
	 */
	public DeletionProcessor(LinkedBlockingQueue<SDeletion> deletes, String logDir, DbManager manager, EmailManager emailManager) {
	
		// check on the parameters
		if(deletes == null) {
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
		newDeletes = deletes;
		logFiles = logDir;
		database = manager;
		this.emailManager = emailManager;
	}
	
	/**
	 * A method that is run in the thread
	 * Takes a tweet from the queue and processes it
	 */
	public void run() {
	
		// declare helper variables
		SDeletion delete = null;
		
		try {
			// infinite loop to keep the thread running
			while(true) {
				
				// take a tweet from the queue
				delete = newDeletes.take();
				
				// get the tweet id number
				String tweetId = Long.toString(delete.getStatusId());
				
				// get a hash of the tweet id
				String tweetIdHash = HashUtils.hashValue(tweetId);
				
				// keep the user informed
				System.out.println("INFO: Received a deletion request for message with id: " + tweetIdHash);
				
				/*
				 * rename the log file
				 */
				if(FileUtils.doesFileExist(logFiles + "/" + tweetIdHash) == true) {
					// move the file to a new file name
					if(FileUtils.renameFile(logFiles + "/" + tweetIdHash, logFiles + "/" + tweetIdHash + "-non-public") == false) {
						// report the error
						System.err.println("ERROR: Unable to mark as deleted the file twitter message with id'" + tweetIdHash + "' as the rename operation failed");
					
						// send an exception report
						if(emailManager.sendSimpleMessage(EMAIL_SUBJECT, EMAIL_MESSAGE + tweetIdHash) == false) {
							System.err.println("ERROR: Unable to send the exception report");
						}
					}
						
				} else {
					// report the error
					System.err.println("ERROR: Unable to mark as deleted the file twitter message with id'" + tweetIdHash + "' as it could not be found");
					
					// send an exception report
					if(emailManager.sendSimpleMessage(EMAIL_SUBJECT, EMAIL_MESSAGE + tweetIdHash) == false) {
						System.err.println("ERROR: Unable to send the exception report");
					}
				}
				
				/*
				 * mark the message as private in the database
				 */
				
				// define the sql				
				String updateSql = "UPDATE mob_feedback "
								 + "SET public_display = 'N' "
								 + "WHERE source_id = ? "
								 + "AND source_type = ?";

				// define the parameters array
				String[] sqlParameters = new String[2];
				sqlParameters[0] = tweetIdHash;
				sqlParameters[1] = SOURCE_ID;
				
				// update the data
				if(database.executePreparedInsertStatement(updateSql, sqlParameters) == false) {
					System.err.println("ERROR: Unable to mark the message as private in the database");
					
					// send an exception report
					if(emailManager.sendSimpleMessage(EMAIL_SUBJECT, EMAIL_MESSAGE + tweetIdHash) == false) {
						System.err.println("ERROR: Unable to send the exception report");
					}
					
				} else {
					System.out.println("INFO: Successfully marked the message as private in the database");
				}
			}
		} catch (InterruptedException ex) {
			// thread has been interrupted
			System.out.println("INFO: The Deletion Request message processing thread has stopped");
		}
	} // end the run method

}
