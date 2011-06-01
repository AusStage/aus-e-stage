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

// import additional AusStage libraries
import au.edu.ausstage.utils.*;
import au.edu.ausstage.exchange.types.*;

import java.util.ArrayList;
import java.sql.ResultSet;

/**
 * The main driving class for the collation of event data using contributor ids
 */
public class ContributorData {

	// private class level variables
	private DbManager database;
	private String[]  ids;
	private String    outputType;
	private String    recordLimit;

	/**
	 * Constructor for this class
	 *
	 * @param database    the DbManager class used to connect to the database
	 * @param ids         the array of unique contributor ids
	 * @param outputType  the output type
	 * @param recordLimit the record limit
	 *
	 * @throws IllegalArgumentException if any of the parameters are empty or do not pass validation
	 *
	 */
	public ContributorData(DbManager database, String[] ids, String outputType, String recordLimit) {
	
		// validate the parameters
		if(database == null || ids == null || outputType == null || recordLimit == null) {
			throw new IllegalArgumentException("all parameters to this constructor are required");
		}
		
		if(InputUtils.isValid(outputType, ExchangeServlet.VALID_OUTPUT_TYPES) == false) {
			// no valid type was found
			throw new IllegalArgumentException("Invalid output parameter. Expected one of: " + InputUtils.arrayToString(ExchangeServlet.VALID_OUTPUT_TYPES));
		}
		
		// ensure the ids are numeric
		for(int i = 0; i < ids.length; i++) {
			try {
				Integer.parseInt(ids[i]);
			} catch(Exception ex) {
				throw new IllegalArgumentException("The ids parameter must contain numeric values");
			}
		}
		
		// impose sensible limit on id numbers
		if(ids.length > 10) {
			throw new IllegalArgumentException("The ids parameter must contain no more than 10 numbers");
		}
		
		if(recordLimit.equals("all") == false) {
			try {
				Integer.parseInt(recordLimit);	
			} catch(Exception ex) {
				throw new IllegalArgumentException("The limit parameter must be 'all' or a numeric value");
			}
		}
		
		this.database = database;
		this.ids = ids;
		this.outputType = outputType;
		this.recordLimit = recordLimit;
	}
	
	/**
	 * method to get and return the data
	 *
	 * @return the compiled data in the requested format
	 */
	public String getData() {
		
		String sql;
		DbObjects results;
		Event event;
		
		String venue;
		String firstDate;
		
		ArrayList<Event> eventList = new ArrayList<Event>();
		
		if(ids.length == 1) {
		
			sql = "SELECT e.eventid, e.event_name, e.yyyyfirst_date, e.mmfirst_date, e.ddfirst_date, "
				+ "       v.venueid, v.venue_name, v.street, v.suburb, s.state, v.postcode, "
				+ "       c.countryname "
				+ "FROM events e, conevlink cl, venue v, country c, states s "
				+ "WHERE cl.contributorid = ? "
				+ "AND cl.eventid = e.eventid "
				+ "AND e.venueid = v.venueid "
				+ "AND v.countryid = c.countryid (+) "
				+ "AND v.state = s.stateid (+) "
				+ "ORDER BY e.yyyyfirst_date DESC, e.mmfirst_date DESC, e.ddfirst_date DESC";
			
		} else {
		
			sql = "SELECT e.eventid, e.event_name, e.yyyyfirst_date, e.mmfirst_date, e.ddfirst_date, "
				+ "       v.venueid, v.venue_name, v.street, v.suburb, s.state, v.postcode, "
				+ "       c.countryname "
				+ "FROM events e, conevlink cl, venue v, country c, states s "
				+ "WHERE cl.contributorid = ANY (";
			    
			    // add sufficient place holders for all of the ids
				for(int i = 0; i < ids.length; i++) {
					sql += "?,";
				}

				// tidy up the sql
				sql = sql.substring(0, sql.length() -1);
				
				// finalise the sql string
				sql += ") "
				+ "AND cl.eventid = e.eventid "
				+ "AND e.venueid = v.venueid "
				+ "AND v.countryid = c.countryid (+) "
				+ "AND v.state = s.stateid (+) "
				+ "ORDER BY e.yyyyfirst_date DESC, e.mmfirst_date DESC, e.ddfirst_date DESC";
		}
		
		// get the data
		results = database.executePreparedStatement(sql, ids);
	
		// check to see that data was returned
		if(results == null) {
			throw new RuntimeException("unable to lookup contributor data");
		}
		
		// build the list of contributors
		ResultSet resultSet = results.getResultSet();
		try {
			while (resultSet.next()) {
			
				// get the venue and first date
				venue = buildShortVenueAddress(resultSet.getString(12), resultSet.getString(8), resultSet.getString(9), resultSet.getString(10));
				venue = resultSet.getString(7) + ", " + venue;
				
				firstDate = DateUtils.buildDisplayDate(resultSet.getString(3), resultSet.getString(4), resultSet.getString(5));
				
				// build a new event and add it to the list
				event = new Event(resultSet.getString(1), resultSet.getString(2), venue, firstDate);
				eventList.add(event);
				
			}
		} catch (java.sql.SQLException ex) {
			throw new  RuntimeException("unable to build list of events: " + ex.toString());
		}
		
		// play nice and tidy up
		resultSet = null;
		results.tidyUp();
		results = null;
		
		// trim the arraylist if necessary
		if(recordLimit.equals("all") == false) {
			int limit = Integer.parseInt(recordLimit);
			
			if(eventList.size() > limit) {
			
				ArrayList<Event> list = new ArrayList<Event>();
				
				for(int i = 0; i < limit; i++) {
					list.add(eventList.get(i));
				}
				
				eventList = list;
				list = null;
			}
		}
		
		String data = null;
		
		// build the output
		if(outputType.equals("html") == true) {
			data =  DataBuilder.buildHtml(eventList);
		} else if(outputType.equals("json")) {
			data = DataBuilder.buildJson(eventList);
		} else if(outputType.equals("xml")) {
			data = DataBuilder.buildXml(eventList);
		} else if(outputType.equals("rss") == true) {
			data = DataBuilder.buildRss(eventList);
		}
		
		return data;
	}
	
	// private function to build the venue address
	private String buildShortVenueAddress(String country, String street, String suburb, String state) {
	
		String address = "";
		
		if(InputUtils.isValid(country) && country.equals("Australia")) {
		
			if(InputUtils.isValid(suburb) == true) {
				address += suburb + ", ";
			}
			
			if(InputUtils.isValid(state) == true) {
				address += state;
			} else {
				address = address.substring(0, address.length() - 2);
			}
			
		} else {
		
			if(InputUtils.isValid(suburb) == true) {
				address += suburb + ", ";
			}
			
			if(InputUtils.isValid(country) == true) {
				address += country;
			} else {
				address = address.substring(0, address.length() - 2);
			}
		}
		
		return address;
	}
}
