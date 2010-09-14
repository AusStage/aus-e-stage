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

package au.edu.ausstage.networks;

// import additional classes
import java.util.TreeSet;
import org.json.simple.*;
import org.apache.commons.lang.StringEscapeUtils;

import au.edu.ausstage.utils.InputUtils;
import au.edu.ausstage.vocabularies.AusStageURI;

/**
 * A class to represent a Collaborator
 */
public class Collaborator implements Comparable<Collaborator>{

	// declare private class level variables
	private String id = null;
	private String url = null;
	private String givenName = null;
	private String familyName = null;
	private String name = null;
	private String function = null;
	private String gender = null;
	private String nationality = null;
	private String firstDate = null;
	private String lastDate = null;
	private String collaborations = null;
	private TreeSet<String> collaborators = null;
	
	/**
	 * Constructor for this class
	 * 
	 * @param id the id of the contributors
	 */
	public Collaborator(String id) {
		if(InputUtils.isValidInt(id)) {
			this.id = id;
			url = AusStageURI.getContributorURL(id);
		} else {
			throw new IllegalArgumentException("The id value must be a valid integer");
		}
	}
	
	/*
	 * declare getter and setter methods
	 */
	
	/**
	 * A method to set a new ID value
	 *
	 * @param value the new value
	 */
	public void setId(String value) {
		if(InputUtils.isValidInt(value)) {
			this.id = value;
		} else {
			throw new IllegalArgumentException("The value must be a valid integer");
		}
	}
	
	/**
	 * A method to get an ID value
	 *
	 * @return the requested value
	 */
	public String getId() {
		return id;
	}
	
	/**
	 * A method to set a new URL value
	 *
	 * @param value the new value
	 */	
	public void setUrl(String value) {
		if(InputUtils.isValid(value)) {
			url = value;
		} else {
			throw new IllegalArgumentException("Value cannot be null or an empty string");
		}
	}
	
	/**
	 * A method to get an URL value
	 *
	 * @return the requested value
	 */
	public String getUrl() {
		return url;
	}
	
	/**
	 * A method to set a new givenName value
	 *
	 * @param value the new value
	 */	
	public void setGivenName(String value) {
		if(InputUtils.isValid(value)) {
			givenName = value;
		} else {
			throw new IllegalArgumentException("Value cannot be null or an empty string");
		}
	}
	
	/**
	 * A method to set a new givenName value & automatically update the name value
	 *
	 * @param value  the new value
	 * @param update if the name value should also be updated
	 */
	public void setGivenName(String value, boolean update) {
		// call the other method
		setGivenName(value);
		
		if(update == true) {
			// update the name
			name = givenName + " " + familyName;
		}
	}
		
	
	/**
	 * A method to get thegivenName value
	 *
	 * @return the requested value
	 */
	public String getGivenName() {
		return givenName;
	}
	
	/**
	 * A method to set a new familyName value
	 *
	 * @param value the new value
	 */	
	public void setFamilyName(String value) {
		if(InputUtils.isValid(value)) {
			familyName = value;
		} else {
			throw new IllegalArgumentException("Value cannot be null or an empty string");
		}
	}
	
	/**
	 * A method to set a new familyName value & automatically update the name value
	 *
	 * @param value  the new value
	 * @param update if the name value should also be updated
	 */
	public void setFamilyName(String value, boolean update) {
		// call the other method
		setFamilyName(value);
		
		if(update == true) {
			// update the name
			name = givenName + " " + familyName;
		}
	}		
	
	/**
	 * A method to get the familyName value
	 *
	 * @return the requested value
	 */
	public String getFamilyName() {
		return familyName;
	}
	
	/**
	 * A method to set a new name value
	 *
	 * @param value the new value
	 */	
	public void setName(String value) {
		if(InputUtils.isValid(value)) {
			name = value;
		} else {
			throw new IllegalArgumentException("Value cannot be null or an empty string");
		}
	}
	
	/**
	 * A method to get an name value
	 *
	 * @return the requested value
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * A method to set a new function value
	 *
	 * @param value the new value
	 */	
	public void setFunction(String value) {
		if(InputUtils.isValid(value)) {
			if(function == null) {
				function = value;
			} else {
				function = function + " | " + value;
			}
		} else {
			throw new IllegalArgumentException("Value cannot be null or an empty string");
		}
	}
	
	/**
	 * A method to get the function value
	 *
	 * @return the requested value
	 */
	public String getFunction() {
		return function;
	}
	
	/**
	 * A method to get a list of functions as an array
	 *
	 * @return an array of functions
	 */
	public String[] getFunctionAsArray() {
		return function.split("\\|");
	}
	
	/**
	 * A method to set a new firstDate value
	 *
	 * @param value the new value
	 */	
	public void setFirstDate(String value) {
		if(InputUtils.isValid(value)) {
			firstDate = value;
		} else {
			throw new IllegalArgumentException("Value cannot be null or an empty string");
		}
	}
	
	/**
	 * A method to get the firstDate value
	 *
	 * @return the requested value
	 */
	public String getFirstDate() {
		return firstDate;
	}
	
	/**
	 * A method to set a new lastDate value
	 *
	 * @param value the new value
	 */	
	public void setLastDate(String value) {
		if(InputUtils.isValid(value)) {
			lastDate = value;
		} else {
			throw new IllegalArgumentException("Value cannot be null or an empty string");
		}
	}
	
	/**
	 * A method to get the lastDate value
	 *
	 * @return the requested value
	 */
	public String getLastDate() {
		return lastDate;
	}
	
	/**
	 * A method to set a new nationality
	 *
	 * @param value the new value
	 */	
	public void setNationality(String value) {
		if(InputUtils.isValid(value)) {
			nationality = value;
		} else {
			throw new IllegalArgumentException("Value cannot be null or an empty string");
		}
	}
	
	/**
	 * A method to get the lastDate value
	 *
	 * @return the requested value
	 */
	public String getNationality() {
		return nationality;
	}
	
	/**
	 * A method to set a new nationality
	 *
	 * @param value the new value
	 */	
	public void setGender(String value) {
		if(InputUtils.isValid(value)) {
			gender = value;
		} else {
			throw new IllegalArgumentException("Value cannot be null or an empty string");
		}
	}
	
	/**
	 * A method to get the lastDate value
	 *
	 * @return the requested value
	 */
	public String getGender() {
		return gender;
	}
	
	/**
	 * A method to set the number of collaborations
	 *
	 * @param value the new value
	 */
	public void setCollaborations(String value) {
		if(InputUtils.isValidInt(value)) {
			collaborations = value;
		} else {
			throw new IllegalArgumentException("The value must be a valid integer");
		}
	}
	
	/**
	 * A method to get the number of collaborations
	 *
	 * @return the requested value
	 */
	public String getCollaborations() {
		if(collaborations != null) {
			return collaborations;
		} else {
			return "0";
		}
	}
	
	/**
	 * A method to add a collaboration to a list of collaborations
	 *
	 * @param value the new collaborations
	 */
	public void addCollaborator(String value) {
		if(InputUtils.isValid(value)) {
		
			// instantiate a collaborations object if required
			// we do this late to save on resources
			if(collaborators == null) {
				collaborators = new TreeSet<String>();
			}
			
			// add to the list of collaborations			
			collaborators.add(value);
			
		} else {
			throw new IllegalArgumentException("The value cannot be null");
		}
	}
	
	/**
	 * A method to get the list of collaborations
	 *
	 * @return the list of collaborations as TeeSet
	 */
	public TreeSet getCollaborators() {
		return collaborators;
	}
	
	/**
	 * A method to get the list of collaborations
	 *
	 * @return an array of collaborations
	 */
	public String[] getCollaboratorsAsArray() {
		if(collaborators != null) {
			return collaborators.toArray(new String[0]);
		} else {
			return new String[0];
		}
	}
	
	/**
	 * A method to build a JSON representation of this collaborators data
	 *
	 * @return the JSON string for this collaborator
	 */

	public String toJson() {
	
		return toJsonObject().toString();
	}
	
	/**
	 * A method to build the JSON representation of this collaborator
	 *
	 * @return the JSON object for this collaborator
	 */
	@SuppressWarnings("unchecked")
	public org.json.simple.JSONObject toJsonObject() {
	
		// declare helper variables
		JSONArray  list = new JSONArray();
		JSONObject object = new JSONObject();			
		
		// build the object
		object.put("id", Integer.parseInt(id));
		object.put("url", url);
		object.put("givenName", givenName);
		object.put("familyName", familyName);
		object.put("name", getName());
		object.put("collaborations", Integer.parseInt(getCollaborations()));
		
		String[] functions = getFunctionAsArray();
		
		for(int i = 0; i < functions.length; i++) {
			list.add(functions[i].trim());
		}
		
		object.put("functions", list);
		
		return object;	
	}
	
	/**
	 * A method to build an XML representation of this collaborator
	 *
	 * @return the XML string for this collaborator
	 */
	public String toXml() {
	
		StringBuilder xmlMarkup    = new StringBuilder("<?xml version=\"1.0\"?><collaborators>");
		
		xmlMarkup.append("<collaborator id=\"" + getId() + "\">");
		
		xmlMarkup.append("<url>" + StringEscapeUtils.escapeXml(getUrl()) + "</url>");
		xmlMarkup.append("<givenName>" + getGivenName() + "</givenName>");
		xmlMarkup.append("<familyName>" + getFamilyName() + "</familyName>");
		xmlMarkup.append("<name>" + getName() + "</name>");
		xmlMarkup.append("<functions>");
		
		String[] functions = getFunctionAsArray();
		
		for(int i = 0; i < functions.length; i++) {
			xmlMarkup.append("<function>" + functions[i].trim() + "</function>");
		}
		
		
		xmlMarkup.append("</functions>");
		xmlMarkup.append("<collaborations>" + getCollaborations() + "</collaborations>");
		xmlMarkup.append("</collaborator>");
		
		return xmlMarkup.toString();
	}
	
	/**
	 * A method to build an HTML representation of this collaborator
	 *
	 * @return the HTML string from this collaborator
	 */
	public String toHtml() {
	
		StringBuilder htmlMarkup    = new StringBuilder("<p>");
		
		htmlMarkup.append("<a href=\"" + StringEscapeUtils.escapeXml(getUrl()) + "\" title=\"View the record for " + getName() + " in AusStage\" target=\"ausstage\">" + getName() + "</a><br/>Functions: </p>");
		
		htmlMarkup.append("<ul>");
		
		String[] functions = getFunctionAsArray();
		
		for(int i = 0; i < functions.length; i++) {
			htmlMarkup.append("<li>" + functions[i].trim() + "</li>");
		}
		
		htmlMarkup.append("</ul><p>Collaborations: " + getCollaborations() + "</p>");
		
		return htmlMarkup.toString();
	}
	
	
	/*
	 * methods required for ordering in collections
	 * http://java.sun.com/docs/books/tutorial/collections/interfaces/order.html
	 */

	/**
	 * A method to determine if one event is the same as another
	 *
	 * @param o the object to compare this one to
	 *
	 * @return  true if they are equal, false if they are not
	 */
	public boolean equals(Object o) {
		// check to make sure the object is an event
		if ((o instanceof Collaborator) == false) {
			// o is not an event object
		 	return false;
		}
		
		// compare these two events
		Collaborator c = (Collaborator)o;
		
		return id.equals(c.getId());
		
	} // end equals method
	
	/**
	 * Overide the default hashcode method
	 * 
	 * @return a hashcode for this object
	 */
	public int hashCode() {
		return 31*id.hashCode();
	}
    
    /**
     * The compareTo method compares the receiving object with the specified object and returns a 
     * negative integer, 0, or a positive integer depending on whether the receiving object is 
     * less than, equal to, or greater than the specified object.
     *
     * @param c the event to compare this one to
     *
     * @return  an integer indicating comparison result
     */    
	public int compareTo(Collaborator c) {
		int myId   = Integer.parseInt(id);
		int yourId = Integer.parseInt(c.getId());
		
		if(myId == yourId) {
			return 0;
		} else {
			return myId - yourId;
		}
		
	} // end compareTo method

} // end class definition
