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
import java.sql.ResultSet;

// import additional java packages / classes
import org.json.simple.*;


/**
 * A class used to compile the marker data which is used to build
 * maps on web pages
 */
public class FeedbackManager {

	// declare private class variables
	DbManager database;
	
	/**
	 * Constructor for this class
	 *
	 * @param database a valid DbManager object
	 */
	public FeedbackManager(DbManager database) {
	
		// double check the parameter
		if(database == null) {
			throw new IllegalArgumentException("The database parameter cannot be null");
		}
		this.database = database;
	}
	
	/**
	 * A method to retrieve the initial batch of feedback
	 *
	 * @param performance the unique id number of the performance
	 *
	 * @return            initial performance data encoded as a JSON object
	 */
	@SuppressWarnings("unchecked")
	public String getInitialFeedback(String performance) {
	
		// check on the parameters
		if(InputUtils.isValidInt(performance) == false) {
			throw new IllegalArgumentException("The performance parameter is required to be a valid integer");
		}
		
		// declare helper variables
		String   sql;
		String[] sqlParameters;
		
		//JSONArray list = new JSONArray();
		JSONObject object = new JSONObject();
		JSONArray  list   = new JSONArray();
		
		// define the sql
		sql = "SELECT e.event_name, mq.question, o.name "
			+ "FROM mob_performances mp, events e, mob_questions mq, orgevlink oe, organisation o, mob_organisations mo "
			+ "WHERE mp.performance_id = ? "
			+ "AND mp.event_id = e.eventid "
			+ "AND mp.question_id = mq.question_id "
			+ "AND e.eventid = oe.eventid "
			+ "AND o.organisationid = oe.organisationid "
			+ "AND mo.organisation_id = o.organisationid ";
			
		// define the parameters
		sqlParameters = new String[1];
		sqlParameters[0] = performance;
			
		// get the data
		DbObjects results = database.executePreparedStatement(sql, sqlParameters);
		ResultSet resultSet = results.getResultSet();
		
		// check to see that data was returned
		if(results == null) {
		
			// return an empty object	
			object = new JSONObject();
			object.put("name", "");
			return object.toString();
		}
		
		// build the result object
		try {
			// build the documnet by adding individual events
			if (resultSet.next()) {
			
				// add the data
				object.put("name"    , resultSet.getString(1));
				object.put("question", resultSet.getString(2));
				object.put("organisation", resultSet.getString(3));
			} else {
				// add the data
				object.put("name", "");
			}
			
		}catch (java.sql.SQLException ex) {
			
			// return an empty object	
			object = new JSONObject();
			object.put("name", "");
			return object.toString();
		}
		
		// play nice and tidy up
		resultSet = null;
		results.tidyUp();		
		
		//get any feedback
		sql = "SELECT mf.short_content "
			+ "FROM mob_feedback mf, mob_performances mp "
			+ "WHERE mp.performance_id = ? "
			+ "AND mf.performance_id = mp.performance_id "
			+ "ORDER BY mf.received_date_time ASC ";
			
		// get the data
		results = database.executePreparedStatement(sql, sqlParameters);
		resultSet = results.getResultSet();
		
		try {
		
			while (resultSet.next()) {
		
				// add the feedback to the list
				list.add(resultSet.getString(1));
			}
		}catch (java.sql.SQLException ex) {}
		
		// play nice and tidy up
		resultSet = null;
		results.tidyUp();
		
		object.put("feedback", list);
		
		// return the json string
		return object.toString();
	
	} // end the getInitialFeedback method
	
	/**
	 * A method to retrieve updated performance data
	 *
	 * @param performance the unique id number of the performance
	 * @param lastId      the last feedback id that has already been sent to the page
	 *
	 * @return            performance data encoded as a JSON object
	 */
	@SuppressWarnings("unchecked")
	public String getUpdatedFeedback(String performance, String lastId) {
	
		return "";
	
	} // end the getUpdatedFeedback method
	
	
	

}
