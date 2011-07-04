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

package au.edu.ausstage.exchange.items;

// import additional AusStage libraries
import au.edu.ausstage.utils.*;
import au.edu.ausstage.exchange.types.Feedback;
import au.edu.ausstage.exchange.builders.FeedbackDataBuilder;

import java.util.ArrayList;
import java.sql.ResultSet;

/**
 * The main driving class for the collation of event data using contributor ids
 */
public class FeedbackData extends BaseData {

	/**
	 * Constructor for this class
	 *
	 * @param database    the DbManager class used to connect to the database
	 * @param ids         the array of unique ids
	 * @param outputType  the output type
	 * @param recordLimit the record limit
	 *
	 * @throws IllegalArgumentException if any of the parameters are empty or do not pass validation
	 *
	 */
	public FeedbackData(DbManager database, String[] ids, String outputType, String recordLimit) {
		super(database, ids, outputType, recordLimit, null);
	}
	
	@Override
	public String getEventData() {
		
		throw new UnsupportedOperationException("This method is not valid in the context of Resource data");
	}
	
	@Override
	public String getResourceData() {
		throw new UnsupportedOperationException("This method is not valid in the context of Resource data");
	}
	
	/**
	 * get the performance data using the information supplied when constructed
	 *
	 * @return the required performance feedback data in the requested format
	 */
	public String getPerformanceData() {
	
		String sql;
		DbObjects results;
		Feedback feedback;
		
		ArrayList<Feedback> feedbackList = new ArrayList<Feedback>();
		
		String[] ids = getIds();
				
		sql = "SELECT feedback_id, short_content "
			+ "FROM mob_feedback "
			+ "WHERE performance_id = ? "
			+ "ORDER BY received_date_time DESC";
				
		// get the data
		results = getDatabase().executePreparedStatement(sql, ids);
	
		// check to see that data was returned
		if(results == null) {
			throw new RuntimeException("unable to lookup feedback data");
		}
		
		// build the list of contributors
		ResultSet resultSet = results.getResultSet();
		try {
			while (resultSet.next()) {
			
				// build a new feedback object and add it to the list
				feedback = new Feedback(ids[0], resultSet.getString(1), resultSet.getString(2));
				feedbackList.add(feedback);
				
			}
		} catch (java.sql.SQLException ex) {
			throw new  RuntimeException("unable to build list of feedback items: " + ex.toString());
		}
		
		// play nice and tidy up
		resultSet = null;
		results.tidyUp();
		results = null;
		
		// trim the arraylist if necessary
		if(getRecordLimit().equals("all") == false) {
			int limit = getRecordLimitAsInt();
			
			if(feedbackList.size() > limit) {
			
				ArrayList<Feedback> list = new ArrayList<Feedback>();
				
				for(int i = 0; i < limit; i++) {
					list.add(feedbackList.get(i));
				}
				
				feedbackList = list;
				list = null;
			}
		}
		
		String data = null;
		
		// build the output
		if(getOutputType().equals("html") == true) {
			data = FeedbackDataBuilder.buildHtml(feedbackList);
		} else if(getOutputType().equals("json")) {
			data = FeedbackDataBuilder.buildJson(feedbackList);
		} else if(getOutputType().equals("xml")) {
			data = FeedbackDataBuilder.buildXml(feedbackList);
		} else if(getOutputType().equals("rss") == true) {
			data = FeedbackDataBuilder.buildRss(feedbackList);
		} else if(getOutputType().equals("iframe") == true) {
			data = FeedbackDataBuilder.buildIframe(feedbackList);
		}
		
		return data;
	}
}
