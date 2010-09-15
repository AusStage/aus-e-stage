/*
 * This file is part of the AusStage Navigating Networks Service
 *
 * The AusStage Navigating Networks Service is free software: you can redistribute
 * it and/or modify it under the terms of the GNU General private License 
 * as published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * The AusStage Navigating Networks Service is distributed in the hope that it will 
 * be useful, but WITHOUT ANY WARRANTY; without even the implied warranty 
 * of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General private License for more details.
 *
 * You should have received a copy of the GNU General private License
 * along with the AusStage Navigating Networks Service.  
 * If not, see <http://www.gnu.org/licenses/>.
*/

package au.edu.ausstage.networks.types;

// import java classes
import java.util.*;

// import additional classes
import au.edu.ausstage.utils.InputUtils;

/**
 * A class used to maintain a list of collaborations for a contributor
 */
public class CollaborationList {

	// declare private class variables
	private String collaborator = null;
	private HashMap<Integer, Collaboration> collaborations;
	
	/**
	 * Constructor for this class
	 *
	 * @param id the unique collaborator id that is associated with this list of collaborations
	 */
	public CollaborationList(String id) {
		// check the input
		if(InputUtils.isValidInt(id) == false) {
			throw new IllegalArgumentException("The id parameters cannot be null");
		}
		
		// store the id an initialise other variables
		collaborator = id;
		collaborations = new HashMap<Integer, Collaboration>();
	}
	
	/*
	 * get and set methods
	 */
	/**
	 * get the id of the collaborator that is associated with this list of collaborations
	 *
	 * @return the unique collaborator identifier
	 */
	public String getCollaboratorId() {
		return collaborator;
	}
	
	/**
	 * set the id of the collaborator that is associated with this list of collaborations
	 *
	 * @param value the new collaborator id
	 */
	public void setCollaboratorId(String value) {
	
		// check the input
		if(InputUtils.isValidInt(value) == false) {
			throw new IllegalArgumentException("The id parameters cannot be null");
		}
		
		collaborator = value;
	}
	
	/*
	 * collaboration management methods
	 */
	
	/**
	 * Add a collaboration to the list
	 *
	 * @param value the new collaboration
	 */
	public void addCollaboration(Collaboration value) {
		// check the input
		if(value == null) {
			throw new IllegalArgumentException("The parameter cannot be null");
		}
		
		// add the collaboration to the list
		collaborations.put(Integer.parseInt(value.getPartner()), value);
	}
	
	/**
	 * Retrieve a collaboration from the list
	 *
	 * @param value the id of the partner of the collaboration with this collaborator
	 */
	public Collaboration getCollaboration(String value) {
	
		// check the input
		if(InputUtils.isValidInt(value) == false) {
			throw new IllegalArgumentException("The parameter cannot be null");
		}
		
		return collaborations.get(Integer.parseInt(value));	
	}
	
	/**
	 * Retrieve a collaboration from the list
	 *
	 * @param value the id of the partner of the collaboration with this collaborator
	 */
	public Collaboration getCollaboration(Integer value) {
	
		// check the input
		if(value == null) {
			throw new IllegalArgumentException("The parameter cannot be null");
		}
		
		return collaborations.get(value);	
	}

} // end class definition
