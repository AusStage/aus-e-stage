/*
 * This file is part of the AusStage Data Exchange Service
 *
 * AusStage Data Exchange Service is free software: you can redistribute
 * it and/or modify it under the terms of the GNU General Public License 
 * as published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * AusStage Data Exchange Service is distributed in the hope that it will 
 * be useful, but WITHOUT ANY WARRANTY; without even the implied warranty 
 * of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with AusStage Data Exchange Service.  
 * If not, see <http://www.gnu.org/licenses/>.
*/

package au.edu.ausstage.exchange;

// import additional classes
import org.json.simple.JSONObject;
import org.json.simple.JSONArray;

// import additional AusStage libraries
import au.edu.ausstage.utils.*;
import java.sql.ResultSet;

/**
 * A class to manage the lookup of secondary genres and content indicators
 */
public class LookupManager {

	// private class level variables
	private DbManager database;

	/**
	 * Constructor for this class
	 *
	 * @param database    the DbManager class used to connect to the database
	 *
	 * @throws IllegalArgumentException if any of the parameters are empty or do not pass validation
	 *
	 */
	public LookupManager(DbManager database) {
	
		// validate the parameters
		if(database == null) {
			throw new IllegalArgumentException("all parameters to this constructor are required");
		}
		
		this.database = database;
	}
	
	/**
	 * A method to build the list of secondary genres
	 *
	 * @return a JSON string containing secondary genres
	 */
	@SuppressWarnings("unchecked")
	public String getSecondaryGenreIdentifiers() {
	
		String sql;
		DbObjects results;
		
		JSONArray list = new JSONArray();
		JSONObject obj;
		
		sql = "SELECT s.secgenrepreferredid, s.preferredterm, DECODE(event_count, NULL, 0, event_count), DECODE(item_count, NULL, 0, item_count) "
			+ "FROM (SELECT s.secgenrepreferredid, COUNT(sl.eventid) AS event_count "
			+ "       FROM secgenrepreferred s, secgenreclasslink sl "
			+ "       WHERE s.secgenrepreferredid = sl.secgenrepreferredid "
			+ "       GROUP BY s.secgenrepreferredid, s.preferredterm) a, "
			+ "      (SELECT s.secgenrepreferredid, COUNT(il.itemid) AS item_count "
			+ "       FROM secgenrepreferred s, itemsecgenrelink il "
			+ "       WHERE s.secgenrepreferredid = il.secgenrepreferredid "
			+ "       GROUP BY s.secgenrepreferredid, s.preferredterm) b, "
			+ "      secgenrepreferred s "
			+ "WHERE s.secgenrepreferredid = a.secgenrepreferredid (+) "
			+ "AND s.secgenrepreferredid = b.secgenrepreferredid (+) "
			+ "ORDER BY s.preferredterm ASC";
			
		// get the data
		results = database.executeStatement(sql);
	
		// check to see that data was returned
		if(results == null) {
			throw new RuntimeException("unable to lookup secondary genre data");
		}
		
		// build the list of contributors
		ResultSet resultSet = results.getResultSet();
		try {
			while (resultSet.next()) {
			
				obj = new JSONObject();
				
				obj.put("id", resultSet.getString(1));
				obj.put("term", resultSet.getString(2));
				obj.put("events", resultSet.getString(3));
				obj.put("items", resultSet.getString(4));
				
				list.add(obj);				
			}
		} catch (java.sql.SQLException ex) {
			throw new  RuntimeException("unable to build list of secondary genres: " + ex.toString());
		}
		
		// play nice and tidy up
		resultSet = null;
		results.tidyUp();
		results = null;
		
		return list.toString();	
	}
	
	/**
	 * A method to build the list of content indicators
	 *
	 * @return a JSON string containing a list of content indicators
	 */
	@SuppressWarnings("unchecked")
	public String getContentIndicatorIdentifiers() {
		
		String sql;
		DbObjects results;
		
		JSONArray list = new JSONArray();
		JSONObject obj;
		
		sql = "SELECT c.contentindicatorid, c.contentindicator, DECODE(event_count, NULL, 0, event_count), DECODE(item_count, NULL, 0, item_count) "
			+ "FROM (SELECT c.contentindicatorid, COUNT(icl.itemid) AS item_count "
			+ "      FROM contentindicator c, itemcontentindlink icl "
			+ "      WHERE c.contentindicatorid = icl.contentindicatorid "
			+ "      GROUP BY c.contentindicatorid) a, "
			+ "     (SELECT c.contentindicatorid, COUNT(e.eventid) AS event_count "
			+ "      FROM contentindicator c, events e "
			+ "      WHERE c.contentindicatorid = e.content_indicator "
			+ "      GROUP BY c.contentindicatorid) b, "
			+ "    contentindicator c "
			+ "WHERE c.contentindicatorid = a.contentindicatorid (+) "
			+ "AND   c.contentindicatorid = b.contentindicatorid (+) "
			+ "ORDER BY c.contentindicator";
			
		// get the data
		results = database.executeStatement(sql);
	
		// check to see that data was returned
		if(results == null) {
			throw new RuntimeException("unable to lookup content indicator data");
		}
		
		// build the list of contributors
		ResultSet resultSet = results.getResultSet();
		try {
			while (resultSet.next()) {
			
				obj = new JSONObject();
				
				obj.put("id", resultSet.getString(1));
				obj.put("term", resultSet.getString(2));
				obj.put("events", resultSet.getString(3));
				obj.put("items", resultSet.getString(4));
				
				list.add(obj);				
			}
		} catch (java.sql.SQLException ex) {
			throw new  RuntimeException("unable to build list of content indicators: " + ex.toString());
		}
		
		// play nice and tidy up
		resultSet = null;
		results.tidyUp();
		results = null;
		
		return list.toString();	
	}
}
