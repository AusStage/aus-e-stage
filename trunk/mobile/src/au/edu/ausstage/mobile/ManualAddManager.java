/*
 * This file is part of the AusStage Navigating Networks Service
 *
 * The AusStage Navigating Networks Service is free software: you can redistribute
 * it and/or modify it under the terms of the GNU General Public License 
 * as published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * The AusStage Navigating Networks Service is distributed in the hope that it will 
 * be useful, but WITHOUT ANY WARRANTY; without even the implied warranty 
 * of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with the AusStage Navigating Networks Service.  
 * If not, see <http://www.gnu.org/licenses/>.
*/

package au.edu.ausstage.mobile;

// import additional AusStage libraries
import au.edu.ausstage.utils.*;

// import additional java packages / classes
import java.sql.ResultSet;
import org.json.simple.*;

/**
 * A class to manage the lookup of information
 */
public class ManualAddManager {

	// declare private class variables
	private DbManager database;
	private final String DB_DATE_TIME_FORMAT = "DD-MON-YYYY HH24:MI:SS";
	
	/**
	 * Constructor for this class
	 *
	 * @param database a valid DbManager object
	 */
	public ManualAddManager(DbManager database) {
	
		// double check the parameter
		if(database == null) {
			throw new IllegalArgumentException("The database parameter cannot be null");
		}
		this.database = database;
	}
	
	/**
	 * A method to manually add a piece of feedback to the database
	 *
	 * @param performance the unique performance identifier
	 * @param question    the unique question identifier
	 * @param sourceType  the type of source that this feedback came from
	 * @param date        the date that this feedback arrived
	 * @param time        the time that this feedback arrived
	 * @param from        the unique hashed identifier for the source of the feedback
	 * @param sourceId    the unique hashed identifier of the feedback in the source system
	 * @param content     the content of the feedback
	 *
	 * @return            a json encoded object indicating success or failure	 
	 */
	@SuppressWarnings("unchecked")
	public String addFeedback(String performance, String question, String sourceType, String date, String time, String from, String sourceId, String content) {
	
		// check the parameters
		if(InputUtils.isValidInt(performance) == false || InputUtils.isValidInt(question) == false || InputUtils.isValidInt(sourceType) == false) {
			throw new IllegalArgumentException("One of the performance, question and source_type parameters is invalid");
		}
		
		if(InputUtils.isValid(date) == false || InputUtils.isValid(time) == false) {
			throw new IllegalArgumentException("One of the date or time parameters is invalid");
		}
		
		if(InputUtils.isValid(from) == false) {
			throw new IllegalArgumentException("The from parameter is invalid");
		}
		
		if(InputUtils.isValid(content) == false) {
			throw new IllegalArgumentException("The content parameter is invalid");
		}
		
		// check the hashed parameters
		if(HashUtils.isValid(from) == false) {
			throw new IllegalArgumentException("The from parameter is not a valid hash");
		}
		
		if(InputUtils.isValid(sourceId) == true) {
			if(HashUtils.isValid(sourceId) == false) {
				throw new IllegalArgumentException("The source_id parameter is not a valid hash");
			}
		}
		
		// declare helper variables
		String   sql           = null;
		String[] sqlParameters = null;
		JSONObject object = new JSONObject();
		
		if(InputUtils.isValid(sourceId) == false) {
			sql = "INSERT INTO mob_feedback "
				+ "(performance_id, question_id, source_type, received_date_time, received_from, short_content, manual_add) "
				+ "VALUES (?,?,?, TO_DATE(?, '" + DB_DATE_TIME_FORMAT + "'),?,?, 'Y')";
				
			sqlParameters = new String[6];
			
		} else {
			sql = "INSERT INTO mob_feedback "
				+ "(performance_id, question_id, source_type, received_date_time, received_from, short_content, source_id, manual_add) "
				+ "VALUES (?,?,?, TO_DATE(?, '" + DB_DATE_TIME_FORMAT + "'),?,?,?, 'Y')";
				
			sqlParameters = new String[7];
		}
		
		// add the parameters
		sqlParameters[0] = performance;
		sqlParameters[1] = question;
		sqlParameters[2] = sourceType;
		sqlParameters[3] = date + " " + time;
		sqlParameters[4] = from;
		sqlParameters[5] = content;
		
		if(InputUtils.isValid(sourceId) == true) {
			sqlParameters[6] = sourceId;
		}
		
		// execute the sql
		if(database.executePreparedInsertStatement(sql, sqlParameters) == false) {
			object.put("status", "error");
		} else {
			object.put("status", "success");
		}
		
		//debug code
		return object.toString();
		
	} // end the addFeedback method	
	
} // end class definition
