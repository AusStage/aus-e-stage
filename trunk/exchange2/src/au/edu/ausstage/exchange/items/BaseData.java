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
import au.edu.ausstage.exchange.types.*;
import au.edu.ausstage.exchange.builders.*;
import au.edu.ausstage.exchange.EventServlet;

import java.util.ArrayList;
import java.sql.ResultSet;

/**
 * The base class for all of the data collation classes
 */
public abstract class BaseData {

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
	public BaseData(DbManager database, String[] ids, String outputType, String recordLimit) {
	
		// validate the parameters
		if(database == null || ids == null || outputType == null || recordLimit == null) {
			throw new IllegalArgumentException("all parameters to this constructor are required");
		}
		
		if(InputUtils.isValid(outputType, EventServlet.VALID_OUTPUT_TYPES) == false) {
			// no valid type was found
			throw new IllegalArgumentException("Invalid output parameter. Expected one of: " + InputUtils.arrayToString(EventServlet.VALID_OUTPUT_TYPES));
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
	
	public final DbManager getDatabase() {
		return database;
	}
	
	public final String[] getIds() {
		return ids;
	}
	
	public final String getOutputType() {
		return outputType;
	}
	
	public final String getRecordLimit() {
		return recordLimit;
	}
	
	public final int getRecordLimitAsInt() {
		try {
			return Integer.parseInt(recordLimit);
		} catch (NumberFormatException ex) {
			return Integer.MAX_VALUE;
		}
	}
	
	/**
	 * compile the event data for the required data type
	 */
	public abstract String getEventData();
	
	/**
	 * compile the resource data for the required data type
	 */
	public abstract String getResourceData();
	
	/**
	 * Build the short version of the Venue Address
	 */
	public final String buildShortVenueAddress(String country, String street, String suburb, String state) {
	
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
