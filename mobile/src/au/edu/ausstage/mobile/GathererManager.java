/*
 * This file is part of the AusStage Mobile Service
 *
 * The AusStage Mobile Service is free software: you can redistribute
 * it and/or modify it under the terms of the GNU General Public License 
 * as published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * The AusStage Mobile Service is distributed in the hope that it will 
 * be useful, but WITHOUT ANY WARRANTY; without even the implied warranty 
 * of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with the AusStage Mobile Service.  
 * If not, see <http://www.gnu.org/licenses/>.
 */
 
package au.edu.ausstage.mobile;

// import additional AusStage libraries
import au.edu.ausstage.utils.*;

// import the twitter-text-java library
import com.twitter.Extractor;

// import the Joda Time library
import org.joda.time.DateTime;
import org.joda.time.LocalDateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

// import other java libraries
import java.util.List;
import java.util.ListIterator;
import java.sql.ResultSet;
import javax.servlet.ServletConfig;

/**
 * A class used to compile the marker data which is used to build
 * maps on web pages
 */
public class GathererManager {

	// declare private class variables
	DbManager database;
	
	// declare class level constants
	public static final String SMS_SOURCE_ID = "2";
	
	// date related constants
	private final String DB_DATE_TIME_FORMAT = "DD-MON-YYYY HH24:MI:SS";
	private final String JODA_DATE_TIME_FORMAT = "dd-MMM-YYYY HH:mm:ss";
	private final String JODA_INPUT_DATE_TIME_FORMAT = "YY.MM.dd HH:mm:ss";
	
	// email related variables
	private EmailOptions emailOptions = null;
	private EmailManager emailManager = null;
	private final String EMAIL_SUBJECT = "[AusStage Feedback Gatherer] Message Processing Error";
	private final String EMAIL_MESSAGE = "Exception Report: The Feedback Gatherer was unable to locate a valid performance for the SMS Message below";
	
	/**
	 * Constructor for this class
	 *
	 * @param database a valid DbManager object
	 * @param servletConfig a valid ServletConfig object
	 */
	public GathererManager(DbManager database, ServletConfig servletConfig) {
	
		// double check the parameter
		if(database == null || servletConfig == null) {
			throw new IllegalArgumentException("All parameters to this constructor are required");
		}
		
		// store a reference to the database object locally
		this.database = database;
		
		// instantiate the email processing objects
		emailOptions = new EmailOptions();
		try {
		
			// set the options for sending email
			emailOptions.setHost(servletConfig.getServletContext().getInitParameter("mailHost"));
			
			if(InputUtils.isValid(servletConfig.getServletContext().getInitParameter("mailUser")) == true) {
				emailOptions.setUser(servletConfig.getServletContext().getInitParameter("mailUser"));
				emailOptions.setPassword(servletConfig.getServletContext().getInitParameter("mailPassword"));
			}
			
			// use security?
			if(InputUtils.isValid(servletConfig.getServletContext().getInitParameter("mailSsl")) == true) {
				// double check the value of this parameter
				if(servletConfig.getServletContext().getInitParameter("mailSsl").equals("yes") == true || servletConfig.getServletContext().getInitParameter("mailSsl").equals("true") == true) {
					emailOptions.setSSL(true);
				} else {
					emailOptions.setSSL(false);
				}
			}
			
			if(InputUtils.isValid(servletConfig.getServletContext().getInitParameter("mailTls")) == true) {
				// double check the value of this parameter
				if(servletConfig.getServletContext().getInitParameter("mailTls").equals("yes") == true || servletConfig.getServletContext().getInitParameter("mailTls").equals("true") == true) {
					emailOptions.setTLS(true);
				} else {
					emailOptions.setTLS(false);
				}
			}
			
			emailOptions.setPort(servletConfig.getServletContext().getInitParameter("mailPort"));
			emailOptions.setFromAddress(servletConfig.getServletContext().getInitParameter("mailFrom"));
			emailOptions.setToAddress(servletConfig.getServletContext().getInitParameter("mailTo"));
			
		
		} catch(IllegalArgumentException ex) {
			throw new IllegalArgumentException("Unable to construct the email options class", ex);
		}
		
		// instantiate the email manager class
		emailManager = new EmailManager(emailOptions);
	}
	
	/**
	 * A method to process an SMS message
	 *
	 * @param callerId the id of the caller, typically the mobile device / mobile phone number
	 * @param time     the time that the message was sent
	 * @param date     the date that the message was sent
	 * @param message  the content of the message 
	 *
	 * @return         the message in response to the input
	 */
	public String processSMS(String callerId, String time, String date, String message) {
	
		// check on the parameters
		if(InputUtils.isValid(callerId) == false || InputUtils.isValid(time) == false || InputUtils.isValid(date) == false || InputUtils.isValid(message) == false) {
			return null;
		}
		
		// define helper variables
		Extractor extractHashTags = new Extractor();
		DateTimeFormatter dateTimeFormat      = DateTimeFormat.forPattern(JODA_DATE_TIME_FORMAT);
		DateTimeFormatter inputDateTimeFormat = DateTimeFormat.forPattern(JODA_INPUT_DATE_TIME_FORMAT);
		
		// get a santised version of the callerId
		callerId = HashUtils.hashValue(callerId);
		
		// get the list of hash tags from the tweet
		List<String> hashtags = extractHashTags.extractHashtags(message);
		
		// check on what was returned
		if(hashtags.isEmpty() == true) {
			// no hashtags were found
			// send an exception report
			emailManager.sendSimpleMessage(EMAIL_SUBJECT, EMAIL_MESSAGE + buildException(callerId, date, time, message));
			return "Error";
		}
		
		// convert all of the found hash tags to lower case
		ListIterator <String> hashtagsIterator = hashtags.listIterator();
		
		while(hashtagsIterator.hasNext() == true) {
		
			// get the next tage in the list
			String tmp = (String) hashtagsIterator.next();
			
			tmp = tmp.toLowerCase();
			
			// replace the value we retrieved with this updated one
			hashtagsIterator.set(tmp);
		}
		
		// get the date and time that the message was recieved
		time = time.split(" ")[0];
		DateTime messageReceived = inputDateTimeFormat.parseDateTime(date + " " + time);
		
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
		sqlParameters[0] = dateTimeFormat.print(messageReceived);
		sqlParameters[1] = dateTimeFormat.print(messageReceived);
		
		// get the data
		DbObjects results = database.executePreparedStatement(selectSql, sqlParameters);
		
		// check on what is returned
		if(results == null) {
			emailManager.sendSimpleMessage(EMAIL_SUBJECT, EMAIL_MESSAGE + "\nDetails: Unable to lookup performance information\n" + buildException(callerId, date, time, message));
			return "Error";
		} else {
		
			// look for performances matching the hash tag from the message
			ResultSet resultSet = results.getResultSet();
			
			// loop through the resultset
			boolean found = false; // exit the loop early
			
			// reset the sql parameters
			sqlParameters = new String[6];
			
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
			
			if(found == true) {
				
				/*
				 * write the message to the database
				 */
				 
				// define the sql
				String insertSql = "INSERT INTO mob_feedback "
								 + "(performance_id, question_id, source_type, received_date_time, received_from, short_content) "
								 + "VALUES (?,?,?, TO_DATE(?, '" + DB_DATE_TIME_FORMAT + "'),?,?)";
	
				// build the remaining parameters
	
				// use the source id from the constant
				sqlParameters[2] = SMS_SOURCE_ID;
	
				// add the date and time
				sqlParameters[3] = dateTimeFormat.print(messageReceived);
	
				// add the user id
				sqlParameters[4] = callerId;
		
				// add the message
				sqlParameters[5] = message;
	
				// insert the data
				if(database.executePreparedInsertStatement(insertSql, sqlParameters) == false) {
					emailManager.sendSimpleMessage(EMAIL_SUBJECT, EMAIL_MESSAGE + "\nDetails: Unable to add information to the database\n" + buildException(callerId, date, time, message));
					return "Error";
				}	
			} else {
				emailManager.sendSimpleMessage(EMAIL_SUBJECT, EMAIL_MESSAGE + "\nDetails: Unable to find a matching performance\n" + buildException(callerId, date, time, message));
				return "Error";
			}
		} // end performance check	
		
		
		// if we get this far everything is ok
		return "OK";

	}
	
	/**
	 * A private method to build an error message
	 * 
	 * @param callerId the callerId
	 * @param date     the date of the message
	 * @param time     the time of the message
	 * @param message  the feedback meessage
	 *
	 * @return         a string representation of the message to use in the exception report
	 */
	private String buildException(String callerId, String date, String time, String message) {
	
		StringBuilder builder = new StringBuilder("\n******************************************\n");
		builder.append("Caller ID: " + callerId + "\n");
		builder.append("Date: " + date + "\n");
		builder.append("Time: " + time + "\n");
		builder.append("Message: " + message + "\n");
		
		return builder.toString();
	}
}
