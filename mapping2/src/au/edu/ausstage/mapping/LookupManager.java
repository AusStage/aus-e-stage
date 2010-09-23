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
 
package au.edu.ausstage.mapping;

// import additional AusStage libraries
import au.edu.ausstage.utils.*;
import au.edu.ausstage.mapping.types.*;

/**
 * A class used to compile the marker data which is used to build
 * maps on web pages
 */
public class LookupManager {

	// declare private class variables
	DbManager database;
	
	/**
	 * Constructor for this class
	 *
	 * @param database a valid DbManager object
	 */
	public LookupManager(DbManager database) {
		this.database = database;
	}
	
	/**
	 * A method to lookup all the possible valid state values for use in the UI
	 *
	 * @return a JSON encoded string containing the data
	 */
	public String getStateList() {
	
		// get the required sub manager
		LookupInterfaceElementsManager lookup = new LookupInterfaceElementsManager(database);
		
		// return the data
		return lookup.getStateList();
		
	} // end the getStateList method
	
	/**
	 * A method to lookup all of the suburbs given a particular state identifier
	 *
	 * @param stateId the unique identifier of the state
	 *
	 * @return a JSON encoded string containing the data
	 */
	public String getSuburbList(String stateId) {
	
		if(InputUtils.isValid(stateId, LookupServlet.VALID_STATES) == false) {
			throw new IllegalArgumentException("Missing id parameter. Expected one of: " + InputUtils.arrayToString(LookupServlet.VALID_STATES));
		}
	
		// get the required sub manager
		LookupInterfaceElementsManager lookup = new LookupInterfaceElementsManager(database);
		
		// return the data
		return lookup.getSuburbList(stateId);
	
	} // end the getSuburbList method
	
	/**
	 * A method to lookup all of the venues given a particular suburb
	 * 
	 * @param suburbName the name of the suburb
	 *
	 * @return a JSON encoded string containing the data
	 */
	public String getVenueListBySuburb(String suburbName) {
	
		// check the parameters
		if(InputUtils.isValid(suburbName) == false) {
			throw new IllegalArgumentException("The suburb name is a required parameter");
		}
		
		// get the required sub manager
		LookupInterfaceElementsManager lookup = new LookupInterfaceElementsManager(database);
		
		// return the data
		return lookup.getVenueListBySuburb(suburbName);
	} // end the getVenueListBySuburb method	 
	
} // end class definition
