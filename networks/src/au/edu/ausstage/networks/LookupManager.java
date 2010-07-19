/*
 * This file is part of the AusStage Navigating Networks Service
 *
 * The AusStage Navigating Networks Service is free software: you can redistribute
 * it and/or modify it under the terms of the GNU General Public License 
 * as published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * The AusStage Navigating Networks Service is distributed in the hope that it will 
 * be useful, but WITHOUT ANY WARRANTY; without even the implied warranty 
 * of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with the AusStage Mapping Service.  
 * If not, see <http://www.gnu.org/licenses/>.
*/

package au.edu.ausstage.networks;

// import additional libraries
import com.hp.hpl.jena.query.*;
import java.util.*;
import com.google.gson.*;

// import the AusStage vocabularies package
import au.edu.ausstage.vocabularies.*;

/**
 * A class to manage the lookup of information
 */
public class LookupManager {

	// declare private class level variables
	DataManager database = null;

	/**
	 * Constructor for this class
	 */
	public LookupManager(DataManager database) {
	
		// store a reference to this DataManager for later
		this.database = database;
	} // end constructor
	
	
	/**
	 * A method to lookup the key collaborators for a contributor
	 *
	 * @param id         the unique id of the contributor
	 * @param formatType the required format of the data
	 * @param sortType   the required way in which the data is to be sorted
	 *
	 * @return           the results of the lookup
	 */
	public String getKeyCollaborators(String id, String formatType, String sortType) {
	
		// define a Tree Set to store the results
		java.util.TreeMap<Integer, Collaborator> collaborators = new java.util.TreeMap<Integer, Collaborator>();
		
		// define other helper variables
		QuerySolution row          = null;
		Collaborator  collaborator = null;
	
		// define the base sparql query
		String sparqlQuery = "PREFIX foaf:       <http://xmlns.com/foaf/0.1/> "
						   + "PREFIX ausestage:  <http://code.google.com/p/aus-e-stage/wiki/AuseStageOntology#> "
 						   + "SELECT ?collaborator ?collabGivenName ?collabFamilyName ?function ?firstDate ?lastDate ?collabCount "
						   + "WHERE {  "
						   + "       <ausstage:c:774> a foaf:Person ; "
						   + "                      ausestage:hasCollaboration ?collaboration. "
						   + "       ?collaboration ausestage:collaborator ?collaborator; "
						   + "                      ausestage:collaborationFirstDate ?firstDate; "
						   + "                      ausestage:collaborationLastDate ?lastDate; "
						   + "                      ausestage:collaborationCount ?collabCount. "
						   + "       ?collaborator  foaf:givenName ?collabGivenName; "
						   + "                      foaf:familyName ?collabFamilyName; "
						   + "                      ausestage:function ?function. "
						   + "       FILTER (?collaborator != @) "
						   + "}";
						   
		// do we need to sort by name?
		if(sortType.equals("name") == true) {
			sparqlQuery += " ORDER BY ?collabFamilyName ?collabGivenName";
		}
						   
		// build a URI from the id
		id = AusStageURI.getContributorURI(id);
		
		// add the contributor URI to the query
		sparqlQuery = sparqlQuery.replaceAll("@", "<" + id + ">");
		
		// execute the query
		com.hp.hpl.jena.query.ResultSet results = database.executeSparqlQuery(sparqlQuery);	
		
		// process the results
		if(sortType.equals("name") == false) {
			// use a numeric sort order
			while (results.hasNext()) {
				// loop though the resulset
				// get a new row of data
				row = results.nextSolution();
				
				// check to see if we've seen this collaborator before
				if(collaborators.containsKey(new Integer(AusStageURI.getId(row.get("collaborator").toString()))) == false) {
					// no we haven't
				
					// start a new collaborator
					collaborator = new Collaborator();
					
					// get the name
					collaborator.name = row.get("collabGivenName").toString() + " " + row.get("collabFamilyName").toString();	
					
					// get the dates
					collaborator.firstDate = row.get("firstDate").toString();
					collaborator.lastDate  = row.get("lastDate").toString();
			
					// get the collaboration count
					collaborator.collaborations = row.get("collabCount").toString();
				
					// add the url
					collaborator.url = AusStageURI.getURL(row.get("collaborator").toString());
				
					// add the id
					collaborator.id = AusStageURI.getId(row.get("collaborator").toString());
				
					// add the function
					collaborator.function = row.get("function").toString();
				
					// add this collaborator to the list
					collaborators.put(new Integer(collaborator.id), collaborator);
				} else {
					// yes we have
					// get the existing collaborator
					collaborator = collaborators.get(Integer.parseInt(AusStageURI.getId(row.get("collaborator").toString())));
					
					// update the function
					collaborator.function = collaborator.function + " | " + row.get("function").toString();
				}
			}
			
		} else {
			// process using name sort order
		}
		
		// play nice and tidy up
		database.tidyUp();
		
		// do we need to sort into collaboration count order?
		if(sortType.equals("count") == true) {
			// yes
			
			// define a new collection of collaborators
			java.util.TreeMap<Integer, Collaborator> collaboratorsToSort = new java.util.TreeMap<Integer, Collaborator>();
			
			// loop through the list of collaborators and add them to the new set
			Collection values = collaborators.values();
			Iterator   iterator = values.iterator();
			
			while(iterator.hasNext()) {
				collaborator = (Collaborator)iterator.next();
				collaboratorsToSort.put(new Integer(collaborator.collaborations), collaborator);
			}
			
			// sort them
			collaborators = reverseSortMapByKey(collaboratorsToSort);
		}
		
		//debug code
		Gson gson = new Gson();
		//Gson gson = new GsonBuilder().setPrettyPrinting().create();
		return gson.toJson(collaborators);
	}
	
	/**
	 * A method to sort a map in reverse order
	 * based on: http://forums.sun.com/thread.jspa?threadID=5152322
	 *
	 * @param inputMap the map to sort in reverse
	 * @return         the map sorted in reverse order
	 */
	private TreeMap<Integer, Collaborator> reverseSortMapByKey (TreeMap<Integer, Collaborator> inputMap) {
        Comparator < Integer > reverse = Collections.reverseOrder();
        TreeMap<Integer, Collaborator> result = new TreeMap<Integer, Collaborator>(reverse);
        result.putAll(inputMap);
        return result;
    } // end reverseSortMapByKey method
	
	/**
	 * A simple conveniens class used to represent a collaborator
	 */
	private class Collaborator {
	
		// declare public variables
		public String id;
		public String url;
		public String name;
		public String function;
		public String firstDate;
		public String lastDate;
		public String collaborations;	
	}

} // end class definition
