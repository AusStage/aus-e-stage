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
 * along with the AusStage Navigating Networks Service.  
 * If not, see <http://www.gnu.org/licenses/>.
*/

package au.edu.ausstage.networks;

// import additional libraries
import com.hp.hpl.jena.query.*;
import java.util.*;
import org.json.simple.*;
import org.apache.commons.lang.StringEscapeUtils;

// import AusStage related packages
import au.edu.ausstage.vocabularies.*;
import au.edu.ausstage.utils.DateUtils;
import au.edu.ausstage.utils.InputUtils;
import au.edu.ausstage.networks.types.*;

/**
 * A class to manage the searches for information
 */
public class SearchManager {

	// declare private class level variables
	private DataManager database = null;

	/**
	 * Constructor for this class
	 */
	public SearchManager(DataManager database) {
	
		// store a reference to this DataManager for later
		this.database = database;
	} // end constructor
	
	/**
	 * A method to undertake a collaborator search
	 *
	 * @param query      the name of the collaborator
	 * @param formatType the type of data format for the response
	 * @param sortType   the type of sort to undertake on the data
	 * @param limit      the maximum number of collaborators to return
	 *
	 * @return           the results of the search
	 */
	public String doCollaboratorSearch(String query, String formatType, String sortType, int limit) {
	
		// double check the parameters
		if(InputUtils.isValid(query) == false || InputUtils.isValid(formatType) == false || InputUtils.isValid(sortType) == false) {
			throw new IllegalArgumentException("All of the parameters for this method are required");
		}
		
		// define a Tree Set to store the results
		java.util.LinkedList<Collaborator> collaborators = new java.util.LinkedList<Collaborator>();
		
		// define other helper variables
		QuerySolution row          = null;
		Collaborator  collaborator = null;
		boolean       underLimit   = true;
		int           resultCount  = 0;
	
		// define the base sparql query
		String sparqlQuery = "PREFIX foaf:       <" + FOAF.NS + ">"
						   + "PREFIX ausestage:  <" + AuseStage.NS + "> "
 						   + "SELECT ?collaborator ?familyName ?givenName ?function ?collaboratorCount "
						   + "WHERE {   "
						   + "       ?collaborator a foaf:Person; "
						   + "                     foaf:familyName ?familyName; "
						   + "                     foaf:givenName  ?givenName; "
						   + "                     foaf:name       ?name; "
						   + "                     ausestage:collaboratorCount ?collaboratorCount;  "
						   + "                     ausestage:function ?function. "
						   + "       FILTER regex(?name, \"" + query + "\", \"i\") "
						   + "}";
						   
		// define the ordering of the result set
		if(sortType.equals("name") == true) {
			sparqlQuery += " ORDER BY ?familyName ?givenName ";
		} else if (sortType.equals("id") == true) {
			// TODO implement the use of a custom function for ordering
		}
		
		//debug code
		System.out.println("###" + sparqlQuery + "###");
								   
		// execute the query
		ResultSet results = database.executeSparqlQuery(sparqlQuery);
		
		// build the dataset
		// use a numeric sort order
		while (results.hasNext() && underLimit) {
			// loop though the resulset
			// get a new row of data
			row = results.nextSolution();
			
			// instantiate a collaborator object
			collaborator = new Collaborator(AusStageURI.getId(row.get("collaborator").toString()));
			
			// check to see if the list contains this collaborator
			if(collaborators.indexOf(collaborator) != -1) {
				// collaborator is already in the list
				collaborator = collaborators.get(collaborators.indexOf(collaborator));
				
				// update the function
				collaborator.setFunction(row.get("function").toString());
				
			} else {
				// collaborator is not on the list
				
				// have we reached the limit yet
				if(resultCount <= limit) {
					
					// increment the count
					resultCount++;
					
					// get the name
					collaborator.setGivenName(row.get("givenName").toString());
					collaborator.setFamilyName(row.get("familyName").toString(), true);
							
					// get the collaboration count
					collaborator.setCollaborations(Integer.toString(row.get("collaboratorCount").asLiteral().getInt()));
		
					// add the url
					collaborator.setUrl(AusStageURI.getURL(row.get("collaborator").toString()));
		
					// add the function
					collaborator.setFunction(row.get("function").toString());
			
					collaborators.add(collaborator);
				} else {
					// limit reached so stop the loop
					underLimit = false;
				}
			}
		}
		
		// play nice and tidy up
		database.tidyUp();
		
		// define a variable to store the data
		String dataString = null;
		
		if(formatType.equals("html") == true) {
			dataString = createHTMLOutput(collaborators);
		} else if(formatType.equals("xml") == true) {
			dataString = createXMLOutput(collaborators);
		} else if(formatType.equals("json") == true) {
			dataString = createJSONOutput(collaborators);
		}
		
		// return the data
		return dataString;	
		
	} // end the doCollaboratorSearch method
	
	/**
	 * A method to take a group of collaborators and output JSON encoded text
	 * Unchecked warnings are suppressed due to internal issues with the org.json.simple package
	 *
	 * @param collaborators the list of collaborators
	 * @return              the JSON encoded string
	 */
	@SuppressWarnings("unchecked")
	private String createJSONOutput(LinkedList<Collaborator> collaborators) {
	
		// assume that all sorting and ordering has already been carried out
		// loop through the list of collaborators and add them to the new JSON objects
		ListIterator iterator = collaborators.listIterator(0);
		
		// declare helper variables
		JSONArray  list = new JSONArray();
		Collaborator collaborator = null;
		
		while(iterator.hasNext()) {
		
			// get the collaborator
			collaborator = (Collaborator)iterator.next();
			
			// add the Json object to the array
			list.add(collaborator.toJsonObject());		
		}
		
		// return the JSON encoded string
		return list.toString();
			
	} // end the createJSONOutput method
	
	/**
	 * A method to take a group of collaborators and output XML encoded text
	 *
	 * @param collaborators the list of collaborators
	 * @return              the HTML encoded string
	 */
	private String createXMLOutput(java.util.LinkedList<Collaborator> collaborators) {
		return "";
	}
	
	/**
	 * A method to take a group of collaborators and output HTML encoded text
	 *
	 * @param collaborators the list of collaborators
	 * @return              the HTML encoded string
	 */
	private String createHTMLOutput(LinkedList<Collaborator> collaborators) {
		return "";
	}
	
	


} // end class definition
