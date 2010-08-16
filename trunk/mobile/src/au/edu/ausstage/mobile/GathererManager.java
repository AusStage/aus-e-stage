/*
 * This file is part of the AusStage Mapping Service
 *
 * The AusStage Mapping Service is free software: you can redistribute
 * it and/or modify it under the terms of the GNU General Public License 
 * as published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * The AusStage Mapping Service is distributed in the hope that it will 
 * be useful, but WITHOUT ANY WARRANTY; without even the implied warranty 
 * of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with the AusStage Mapping Service.  
 * If not, see <http://www.gnu.org/licenses/>.
 */
 
package au.edu.ausstage.mobile;

// import additional AusStage libraries
import au.edu.ausstage.utils.*;

// import additional java packages / classes


/**
 * A class used to compile the marker data which is used to build
 * maps on web pages
 */
public class GathererManager {

	// declare private class variables
	DbManager database;
	
	/**
	 * Constructor for this class
	 *
	 * @param database a valid DbManager object
	 */
	public GathererManager(DbManager database) {
	
		// double check the parameter
		if(database == null) {
			throw new IllegalArgumentException("The database parameter cannot be null");
		}
		this.database = database;
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
	
		// TODO Have proper processing code
		StringBuilder builder = new StringBuilder("Caller ID: " + callerId + "\n");
		builder.append("Caller ID Hash: " + HashUtils.hashValue(callerId) + "\n");
		builder.append("Time: " + time + "\n");
		builder.append("Date: " + date + "\n");
		builder.append("Message: " + message + "\n");
		
		return builder.toString();	
	}

}
