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

// import additional libraries
import org.json.simple.*;

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
	
		// validate the input
		if(InputUtils.isValid(suburbName) == false) {
			throw new IllegalArgumentException("The suburbName parameter is required");
		} else if(suburbName.indexOf("_") == -1) {
			throw new IllegalArgumentException("The suburbName parameter is required to have a state code followed by a suburb name seperated by a \"_\" character");
		} else {
			String[] tmp = suburbName.split("_");
			if(tmp.length > 2) {
				throw new IllegalArgumentException("The suburbName parameter is required to have a state code followed by a suburb name seperated by a \"_\" character");
			} else {
				if(InputUtils.isValid(tmp[0], LookupServlet.VALID_STATES) == false) {
					throw new IllegalArgumentException("Invalid state code. Expected one of: " + InputUtils.arrayToString(LookupServlet.VALID_STATES));
				}
			}
		}
		
		// get the required sub manager
		LookupInterfaceElementsManager lookup = new LookupInterfaceElementsManager(database);
		
		// return the data
		return lookup.getVenueListBySuburb(suburbName);
	} // end the getVenueListBySuburb method
	
	/**
	 * A method to lookup the details of an organisation
	 *
	 * @param id the unique identifier of the organisation
	 *
	 * @return a JSON encoded string containing the data
	 */
	public String getOrganisation(String id) {
	
		// validate the input
		if(InputUtils.isValidInt(id) == false) {
			throw new IllegalArgumentException("The id parameter is required to be a valid integer");
		}
		
		SearchManager search = new SearchManager(database);
		
		return search.doOrganisationSearch("id", id, null, null, null);
	}
	
	/**
	 * A method to lookup the details of a contributor
	 *
	 * @param id the unique identifier of the contributor
	 *
	 * @return a JSON encoded string containing the data
	 */
	public String getContributor(String id) {
	
		// validate the input
		if(InputUtils.isValidInt(id) == false) {
			throw new IllegalArgumentException("The id parameter is required to be a valid integer");
		}
		
		SearchManager search = new SearchManager(database);
		
		return search.doContributorSearch("id", id, null, null, null);
	}
	
	/**
	 * A method to lookup the details of a venue
	 *
	 * @param id the unique identifier of the venue
	 *
	 * @return a JSON encoded string containing the data
	 */
	public String getVenue(String id) {
	
		// validate the input
		if(InputUtils.isValidInt(id) == false) {
			throw new IllegalArgumentException("The id parameter is required to be a valid integer");
		}
		
		SearchManager search = new SearchManager(database);
		
		return search.doVenueSearch("id", id, null, null, null);
	}
	
	/**
	 * A method to return the list of colours that can be used for 
	 * elements displayed on a map
	 *
	 * @param xmlFilePath the path to the XML file that specifies the colours
	 * 
	 * @return a JSON encoded array of colour attributes
	 */	
	@SuppressWarnings("unchecked")	
	public static String getMapElementColourList(String xmlFilePath) {
	
		// double check on the parameter
		if(InputUtils.isValid(xmlFilePath) == false) {
			throw new IllegalArgumentException("The xmlFilePath parameter is required");
		}
		
		ColourManager colours = new ColourManager(xmlFilePath);
		
		if(colours.parseXML() == false) {
			return new JSONArray().toString();
		}
		
		// get the list of colours
		JSONArray list = new JSONArray();
		list.addAll(colours.getColourListForWeb());
		return list.toString();
	}
	
} // end class definition
