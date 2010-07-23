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

// import the AusStage vocabularies package
import au.edu.ausstage.vocabularies.*;
import au.edu.ausstage.utils.DateUtils;

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
						   + "       @ a foaf:Person ; "
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
		
		// build the dataset
		// use a numeric sort order
		while (results.hasNext()) {
			// loop though the resulset
			// get a new row of data
			row = results.nextSolution();
			
			// check to see if we've seen this collaborator before
			if(collaborators.containsKey(new Integer(AusStageURI.getId(row.get("collaborator").toString()))) == false) {
				// no we haven't
			
				// start a new collaborator
				collaborator = new Collaborator(AusStageURI.getId(row.get("collaborator").toString()));
				
				// get the name
				collaborator.setGivenName(row.get("collabGivenName").toString());
				collaborator.setFamilyName(row.get("collabFamilyName").toString(), true);
								
				// get the dates
				collaborator.setFirstDate(row.get("firstDate").toString());
				collaborator.setLastDate(row.get("lastDate").toString());
		
				// get the collaboration count
				collaborator.setCollaborations(row.get("collabCount").toString());
			
				// add the url
				collaborator.setUrl(AusStageURI.getURL(row.get("collaborator").toString()));
			
				// add the function
				collaborator.setFunction(row.get("function").toString());
			
				// add this collaborator to the list
				collaborators.put(new Integer(collaborator.getId()), collaborator);
			} else {
				// yes we have
				// get the existing collaborator
				collaborator = collaborators.get(Integer.parseInt(AusStageURI.getId(row.get("collaborator").toString())));
				
				// update the function
				collaborator.setFunction(row.get("function").toString());
			}
		}
		
		// play nice and tidy up
		database.tidyUp();
		
		// do we need to sort the list of collaborators
		if(sortType.equals("count") == true) {
			// yes - in order of collaboration count
			
			// define a new collection of collaborators
			java.util.TreeMap<Integer, Collaborator> collaboratorsToSort = new java.util.TreeMap<Integer, Collaborator>();
			
			// loop through the list of collaborators and add them to the new set
			Collection values   = collaborators.values();
			Iterator   iterator = values.iterator();
			
			while(iterator.hasNext()) {
				collaborator = (Collaborator)iterator.next();
				collaboratorsToSort.put(new Integer(collaborator.getCollaborations()), collaborator);
			}
			
			// sort them
			collaborators = reverseSortMapByKey(collaboratorsToSort);
			
			collaboratorsToSort = null;
			
		} else if(sortType.equals("name") == true) {
			// yes - by name			
			java.util.TreeMap<String, Collaborator> collaboratorsToSort = new java.util.TreeMap<String, Collaborator>();
			
			// loop through the list of collaborators and add them to the new set
			Collection values   = collaborators.values();
			Iterator   iterator = values.iterator();
			String     index    = null;
			
			while(iterator.hasNext()) {
				collaborator = (Collaborator)iterator.next();
				
				// derive an index value from the name
				index = collaborator.getFamilyName();
				index = index + collaborator.getGivenName();
				index = index.toLowerCase();
				index = index.replaceAll(" ", "");
				
				// use the index in the new collection
				collaboratorsToSort.put(index, collaborator);				
			}
			
			// rebuild the collaborators collection
			values   = collaboratorsToSort.values();
			iterator = values.iterator();
			int newIndex = 0;
			
			collaborators.clear();
			
			while(iterator.hasNext()) {
				collaborator = (Collaborator)iterator.next();
				
				collaborators.put(newIndex, collaborator);
				
				newIndex++;
			}

			collaboratorsToSort = null;
		}
		
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
	}
	
	/**
	 * A method to take a group of collaborators and output JSON encoded text
	 *
	 * @param collaborators the list of collaborators
	 * @return              the JSON encoded string
	 */
	@SuppressWarnings("unchecked")
	private String createJSONOutput(TreeMap<Integer, Collaborator> collaborators) {
	
		// assume that all sorting and ordering has already been carried out
		// loop through the list of collaborators and add them to the new JSON objects
		Collection values = collaborators.values();
		Iterator   iterator = values.iterator();
		
		// declare helper variables
		JSONArray  list = new JSONArray();
		JSONObject object = null;
		Collaborator collaborator = null;
		
		while(iterator.hasNext()) {
		
			// get the collaborator
			collaborator = (Collaborator)iterator.next();
			
			// start a new JSON object
			object = new JSONObject();
			
			// build the object
			object.put("id", collaborator.getId());
			object.put("url", collaborator.getUrl());
			object.put("givenName", collaborator.getGivenName());
			object.put("familyName", collaborator.getFamilyName());
			object.put("name", collaborator.getName());
			object.put("function", collaborator.getFunction());
			object.put("firstDate", collaborator.getFirstDate());
			object.put("lastDate", collaborator.getLastDate());
			object.put("collaborations", new Integer(collaborator.getCollaborations()));
			
			// add the new object to the array
			list.add(object);		
		}
		
		// return the JSON encoded string
		return list.toString();
			
	} // end the createJSONOutput method
	
	/**
	 * A method to take a group of collaborators and output HTML encoded text
	 *
	 * @param collaborators the list of collaborators
	 * @return              the HTML encoded string
	 */
	private String createHTMLOutput(TreeMap<Integer, Collaborator> collaborators) {
	
		// assume that all sorting and ordering has already been carried out
		// loop through the list of collaborators and add them to the new JSON objects
		Collection values = collaborators.values();
		Iterator   iterator = values.iterator();
		
		// declare helper variables
		StringBuilder htmlMarkup   = new StringBuilder("<table id=\"key-collaborators\">");
		String[]      functions    = null;
		String[]      tmp          = null;
		String        firstDate    = null;
		String        lastDate     = null;
		Collaborator  collaborator = null;
		
		// add the header and footer
		htmlMarkup.append("<thead><tr><th>Name</th><th>Period</th><th>Function(s)</th><th>Count</th></tr></thead>");
		htmlMarkup.append("<tfoot><tr><td>Name</td><td>Period</td><td>Function(s)</td><td>Count</td></tr></tfoot>");
		
		
		while(iterator.hasNext()) {
		
			// get the collaborator
			collaborator = (Collaborator)iterator.next();
			
			// start the row
			htmlMarkup.append("<tr id=\"key-collaborator-" + collaborator.getId() + "\">");
			
			// add the cell with the link and name
			htmlMarkup.append("<th scop=\"row\"><a href=\"" + StringEscapeUtils.escapeHtml(collaborator.getUrl()) + "\" title=\"View " + collaborator.getName() + " record in AusStage\">");
			htmlMarkup.append(collaborator.getName() + "</a></th>");
			
			// build the dates
			tmp = DateUtils.getExplodedDate(collaborator.getFirstDate(), "-");
			firstDate = DateUtils.buildDisplayDate(tmp[0], tmp[1], tmp[2]);
			
			tmp = DateUtils.getExplodedDate(collaborator.getLastDate(), "-");
			lastDate = DateUtils.buildDisplayDate(tmp[0], tmp[1], tmp[2]);
			
			// add the cell with the collaboration period
			htmlMarkup.append("<td>" + firstDate + " - " + lastDate + "</td>");
			
			// add the functions
			htmlMarkup.append("<td>" + collaborator.getFunction().replaceAll("\\|", "<br/>") + "</td>");
			
			// add the count
			htmlMarkup.append("<td>" + collaborator.getCollaborations() + "</td>");
			
			// end the row
			htmlMarkup.append("</tr>");			
		}
		
		// end the table
		htmlMarkup.append("</table>");
		
		return htmlMarkup.toString();
	
	} // end the createHTMLOutput method
	
	/**
	 * A method to take a group of collaborators and output XML encoded text
	 *
	 * @param collaborators the list of collaborators
	 * @return              the HTML encoded string
	 */
	private String createXMLOutput(TreeMap<Integer, Collaborator> collaborators) {
		// assume that all sorting and ordering has already been carried out
		// loop through the list of collaborators and add them to the new JSON objects
		Collection values = collaborators.values();
		Iterator   iterator = values.iterator();
		
		// declare helper variables
		StringBuilder xmlMarkup    = new StringBuilder("<?xml version=\"1.0\"?><collaborators>");
		Collaborator  collaborator = null;
		
		while(iterator.hasNext()) {
		
			// get the collaborator
			collaborator = (Collaborator)iterator.next();
			
			xmlMarkup.append("<collaborator id=\"" + collaborator.getId() + "\">");
			
			xmlMarkup.append("<url>" + StringEscapeUtils.escapeXml(collaborator.getUrl()) + "</url>");
			xmlMarkup.append("<givenName>" + collaborator.getGivenName() + "</givenName>");
			xmlMarkup.append("<familyName>" + collaborator.getFamilyName() + "</familyName>");
			xmlMarkup.append("<name>" + collaborator.getName() + "</name>");
			xmlMarkup.append("<function>" + collaborator.getFunction() + "</function>");
			xmlMarkup.append("<firstDate>" + collaborator.getFirstDate() + "</firstDate>");
			xmlMarkup.append("<lastDate>" + collaborator.getLastDate() + "</lastDate>");
			xmlMarkup.append("<collaborations>" + collaborator.getCollaborations() + "</collaborations>");
			xmlMarkup.append("</collaborator>");
		}
		
		// end the table
		xmlMarkup.append("</collaborators>");
		
		return xmlMarkup.toString();
	
	} // end createXMLOutput method
	
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

} // end class definition
