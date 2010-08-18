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

/**
 * A class to manage the lookup of information
 */
public class LookupManager {

	// declare private class level variables
	private DataManager database = null;

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
	
		// check on the parameters
		if(InputUtils.isValidInt(id) == false || InputUtils.isValid(formatType) == false || InputUtils.isValid(sortType) == false) {
			return null;
		}
	
		// define a Tree Set to store the results
		java.util.LinkedList<Collaborator> collaborators = new java.util.LinkedList<Collaborator>();
		
		// define other helper variables
		QuerySolution row          = null;
		Collaborator  collaborator = null;
	
		// define the base sparql query
		String sparqlQuery = "PREFIX foaf:       <" + FOAF.NS + ">"
						   + "PREFIX ausestage:  <" + AuseStage.NS + "> "
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
		if(sortType.equals("count") == true) {
			sparqlQuery += " ORDER BY DESC(?collabCount)";
		} else if(sortType.equals("name") == true) {
			sparqlQuery += " ORDER BY ?collabFamilyName ?collabGivenName";
		}
						   
		// build a URI from the id
		id = AusStageURI.getContributorURI(id);
		
		// add the contributor URI to the query
		sparqlQuery = sparqlQuery.replaceAll("@", "<" + id + ">");
		
		// execute the query
		ResultSet results = database.executeSparqlQuery(sparqlQuery);
		
		// build the dataset
		// use a numeric sort order
		while (results.hasNext()) {
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
				
				// get the name
				collaborator.setGivenName(row.get("collabGivenName").toString());
				collaborator.setFamilyName(row.get("collabFamilyName").toString(), true);
								
				// get the dates
				collaborator.setFirstDate(row.get("firstDate").toString());
				collaborator.setLastDate(row.get("lastDate").toString());
		
				// get the collaboration count
				collaborator.setCollaborations(Integer.toString(row.get("collabCount").asLiteral().getInt()));
			
				// add the url
				collaborator.setUrl(AusStageURI.getURL(row.get("collaborator").toString()));
			
				// add the function
				collaborator.setFunction(row.get("function").toString());
				
				collaborators.add(collaborator);
			}
		}
		
		// play nice and tidy up
		database.tidyUp();
		
		// sort by the id
		if(sortType.equals("id") == true) {
			TreeMap<Integer, Collaborator> collaboratorsToSort = new TreeMap<Integer, Collaborator>();
			
			for(int i = 0; i < collaborators.size(); i++) {
				collaborator = collaborator = collaborators.get(i);
				
				collaboratorsToSort.put(Integer.parseInt(collaborator.getId()), collaborator);
			}
			
			// empty the list
			collaborators.clear();
			
			// add the collaborators back to the list
			Collection values = collaboratorsToSort.values();
			Iterator   iterator = values.iterator();
			
			while(iterator.hasNext()) {
				// get the collaborator
				collaborator = (Collaborator)iterator.next();
				
				collaborators.add(collaborator);
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
	private String createHTMLOutput(LinkedList<Collaborator> collaborators) {
	
		// assume that all sorting and ordering has already been carried out
		// loop through the list of collaborators and build the HTML
		ListIterator iterator = collaborators.listIterator(0);
		
		// declare helper variables
		StringBuilder htmlMarkup   = new StringBuilder("<table id=\"key-collaborators\">");
		String[]      functions    = null;
		String[]      tmp          = null;
		String        firstDate    = null;
		String        lastDate     = null;
		Collaborator  collaborator = null;
		int           count        = 0;
		
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
			
			// increment the count
			count++;		
		}
		
		// end the table
		htmlMarkup.append("</table>");
		
		// add a comment
		htmlMarkup.append("<!-- Contributors listed: " + count + " -->");
		
		return htmlMarkup.toString();
	
	} // end the createHTMLOutput method
	
	/**
	 * A method to take a group of collaborators and output XML encoded text
	 *
	 * @param collaborators the list of collaborators
	 * @return              the HTML encoded string
	 */
	private String createXMLOutput(java.util.LinkedList<Collaborator> collaborators) {
	
		// assume that all sorting and ordering has already been carried out
		// loop through the list of collaborators build the XML
		ListIterator iterator = collaborators.listIterator(0);
		
		// declare helper variables
		StringBuilder xmlMarkup    = new StringBuilder("<?xml version=\"1.0\"?><collaborators>");
		Collaborator  collaborator = null;
		int           count        = 0;
		
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
			
			// increment the count
			count++;
		}
		
		// add a comment
		xmlMarkup.append("<!-- Contributors listed: " + count + " -->");
		
		// end the table
		xmlMarkup.append("</collaborators>");
		
		return xmlMarkup.toString();
	
	} // end createXMLOutput method
	
	/**
	 * Lookup the date and time of creation of various parts of the system
	 *
	 * @param id         the id of the item to lookup
	 * @param formatType the type of format of response
	 */
	public String getCreateDateTime(String id, String formatType) {
	
		// declare helper variables
		String dataString = null;
	
		// determine what type of update date time we're looking for
		if(id.equals("datastore-create-date") == true) {
			
			// define the base sparql query
			String sparqlQuery = "PREFIX ausestage:  <http://code.google.com/p/aus-e-stage/wiki/AuseStageOntology#> "
							   + "SELECT ?updateDate "
							   + "WHERE { "
							   + "       <ausstage:rdf:metadata> a ausestage:rdfMetadata; "
							   + "                               ausestage:tdbCreateDateTime ?updateDate. "
							   + "}";
							   
			// execute the query
			ResultSet results = database.executeSparqlQuery(sparqlQuery);
			
			// check on what was returned
			if(results.hasNext()) {
				// there is a result to process
				QuerySolution row = results.nextSolution();
				
				String createDateTime = row.get("updateDate").toString();
				
				// format the response
				if(formatType.equals("html") == true) {
					dataString = "<p><strong>Dataset created:</strong> " + createDateTime + "</p>";
				} else if(formatType.equals("xml") == true) {
					dataString = "<?xml version=\"1.0\"?><createDateTime>" + createDateTime + "</createDateTime>";
				} else if(formatType.equals("json") == true) {
					dataString = "{ updateDateTime: \"" + createDateTime + "\"}";
				}
			} else {
				// something bad has happened
				return ":(";
			}			
		}
		
		// return the requested data
		return dataString;
	
	} // end the getUpdateDateTime method
	
	/**
	 * A method to lookup the export options
	 *
	 * @return a JSON encoded object listing the options
	 */
	@SuppressWarnings("unchecked")
	public String getExportOptions() {
	
		// define helper variables
		JSONObject object = new JSONObject();
		JSONArray  list   = new JSONArray();
		
		for(int i = 0; i < ExportServlet.EXPORT_TYPES_UI.length; i++) {
			list.add(ExportServlet.EXPORT_TYPES_UI[i]);
		}
		
		object.put("tasks", list);
		
		list = new JSONArray();
		
		for(int i = 0; i < ExportServlet.FORMAT_TYPES.length; i++) {
			list.add(ExportServlet.FORMAT_TYPES[i]);
		}	
		
		object.put("formats", list);
		
		list = new JSONArray();

		String[] radius = new String[ExportServlet.MAX_DEGREES];
		
		for (int i = 0; i < ExportServlet.MAX_DEGREES; i++) {
			list.add(Integer.toString(i + 1));
		}
		
		object.put("radius", list);
		
		return object.toString();	
	
	} //end getExportOptions method
	
	/**
	 * A method to lookup a collaborator
	 *
	 * @param id         the unique identifier for the contributor
	 * @param formatType the data format to use to encode the return value
	 *
	 * @return           information about the collaborator
	 */
	@SuppressWarnings("unchecked")
	public String getCollaborator(String id, String formatType) {
	
		// check on the parameters
		if(InputUtils.isValidInt(id) == false || InputUtils.isValid(formatType) == false) {
			throw new IllegalArgumentException("Both parameters are required");
		}
		
		// define other helper variables
		QuerySolution row          = null;
		Collaborator  collaborator = null;
	
		// define the base sparql query
		String sparqlQuery = "PREFIX foaf:       <" + FOAF.NS + "> "
						   + "PREFIX ausestage:  <" + AuseStage.NS + "> "
 						   + "SELECT ?givenName ?familyName ?function ?gender ?nationality ?collaboratorCount "
						   + "WHERE { "
						   + "       @                a                     foaf:Person;               "
						   + "                        foaf:givenName        ?givenName;                "
						   + "                        foaf:familyName       ?familyName;               "
						   + "                        foaf:gender           ?gender;                   "
						   + "                        ausestage:nationality ?nationality;              "
						   + "                        ausestage:collaboratorCount ?collaboratorCount;  "
						   + "                        ausestage:function    ?function.                 "
						   + "} ";
						   
		// build a URI from the id
		id = AusStageURI.getContributorURI(id);
		
		// add the contributor URI to the query
		sparqlQuery = sparqlQuery.replaceAll("@", "<" + id + ">");
		
		// execute the query
		ResultSet results = database.executeSparqlQuery(sparqlQuery);
		
		// build the dataset
		// use a numeric sort order
		while (results.hasNext()) {
			// loop though the resulset
			// get a new row of data
			row = results.nextSolution();
			
			if(collaborator == null) {			
				// instantiate a collaborator object
				collaborator = new Collaborator(AusStageURI.getId(id));
				
				// get the name
				collaborator.setGivenName(row.get("givenName").toString());
				collaborator.setFamilyName(row.get("familyName").toString(), true);
				
				// get the gender nationality, and collaborator count
				collaborator.setGender(row.get("gender").toString());
				collaborator.setNationality(row.get("nationality").toString());
				
				// get the collaboration count
				collaborator.setCollaborations(Integer.toString(row.get("collaboratorCount").asLiteral().getInt()));
				
				// add a function
				collaborator.setFunction(row.get("function").toString());		

			} else {
			
				// add the function
				collaborator.setFunction(row.get("function").toString());
				
			}
		}
		
		// play nice and tidy up
		database.tidyUp();
		
		// check on what to do
		if(collaborator == null) {
			// no collaborator found
			JSONObject object = new JSONObject();
			object.put("name", "No Collaborator Found");
			return object.toString();
		}	
		
		// define a variable to store the data
		String dataString = null;
		
		if(formatType.equals("html") == true) {
			dataString = collaborator.toHtml();
		} else if(formatType.equals("xml") == true) {
			dataString = collaborator.toXml();
		} else if(formatType.equals("json") == true) {
			dataString = collaborator.toJson();
		}
		
		// return the data
		return dataString;
	
	
	} // end getCollaborator method

} // end class definition
