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
//jena
import com.hp.hpl.jena.query.*;
import com.hp.hpl.jena.tdb.TDBFactory;

//json
import org.json.simple.*;

// general java
import java.util.*;

// import AusStage related packages
import au.edu.ausstage.vocabularies.*;
import au.edu.ausstage.utils.*;

/**
 * A class to manage the export of information
 */
public class ProtovisManager {

	// declare private class level variables
	private DataManager database = null;

	/**
	 * Constructor for this class
	 */
	public ProtovisManager(DataManager database) {	
		// store a reference to this DataManager for later
		this.database = database;
	} // end constructor
	
	/**
	 * A method to build the JSON object required by the Protovis visualisation library
	 *
	 * @param id     the unique identifier of the collaborator
	 * @param radius the number of edges from the central node
	 *
	 * @return the JSON encoded data for use with Protovis
	 */
	@SuppressWarnings("unchecked")
	public String getData(String id, int radius) {
	
		// check on the parameters
		// check the parameters
		if(InputUtils.isValidInt(id) == false) {
			throw new IllegalArgumentException("Error: the id parameter is required");
		}
		
		if(InputUtils.isValidInt(radius, ExportServlet.MIN_DEGREES, ExportServlet.MAX_DEGREES) == false) {
			throw new IllegalArgumentException("Error: the radius parameter must be between " + ExportServlet.MIN_DEGREES + " and " + ExportServlet.MAX_DEGREES);
		}
	
		// get an instance of the ExportManager class
		ExportManager export = new ExportManager(database);
		
		// get the data for this collaborator
		java.util.TreeMap<Integer, Collaborator> network = export.getRawCollaboratorData(id, radius);
		
		// play nice and tidy up
		export = null;
		
		// declare helper variables
		java.util.ArrayList<Collaborator> collaborators = new java.util.ArrayList<Collaborator>();
		
		// define a SPARQL query to get details about a collaborator
		String sparqlQuery = "PREFIX foaf:       <" + FOAF.NS + ">"
						   + "PREFIX ausestage:  <" + AuseStage.NS + "> "
 						   + "SELECT ?collabName ?function  "
						   + "WHERE {  "
						   + "       @ a foaf:Person ; "
						   + "           foaf:name ?collabName; "
						   + "           ausestage:function ?function. "
						   + "} ";

		String queryToExecute = null;
		
		ResultSet     results      = null;
		QuerySolution row          = null;
		Collaborator  collaborator = null;
		
		// loop through the list of collaborators and get additional information
		Collection networkKeys        = network.keySet();
		Iterator   networkKeyIterator = networkKeys.iterator();
		Integer    networkKey         = null;
		Integer    centreId           = Integer.parseInt(id);
		
		// loop through the list of keys
		while(networkKeyIterator.hasNext()) {
		
			// get the key for this collaborator
			networkKey = (Integer)networkKeyIterator.next();
			
			// create a new collaborator object
			collaborator = new Collaborator(networkKey.toString());
			
			// build the query
			queryToExecute = sparqlQuery.replaceAll("@", "<" + AusStageURI.getContributorURI(collaborator.getId()) + ">");
			
			//debug code
			System.out.println("!!! " + AusStageURI.getContributorURI(collaborator.getId()) + " !!!");
			
			// execute the query
			results = database.executeSparqlQuery(queryToExecute);
		
			// add details to this contributor
			while (results.hasNext()) {
				// loop though the resulset
				// get a new row of data
				row = results.nextSolution();
			
				// this is a hack I know
				collaborator.setName(row.get("collabName").toString());
				collaborator.setFunction(row.get("function").toString());
			}
			
			// play nice and tidy yo
			database.tidyUp();
			results = null;
			
			// add the collaborator to the list
			collaborators.add(collaborator);
		}
		
		// find the central collaborator and move them to the head of the array
		networkKey   = collaborators.indexOf(new Collaborator(id));
		collaborator = (Collaborator)collaborators.get(networkKey);
		
		// add the central collaborator to the head of the list
		collaborators.add(0, collaborator);
		
		// remove the old central collaborator object
		collaborators.remove(networkKey + 1);
			
		
		
		
		//debug code
		// build the JSON object
		JSONArray  nodes  = new JSONArray();
		JSONArray  edges  = new JSONArray();
		JSONObject object = new JSONObject();
		
		ListIterator iterator = collaborators.listIterator();
		
		// add the rest of the collaborators
		while(iterator.hasNext()) {
		
			// get the next collaborator in the list
			collaborator = (Collaborator)iterator.next();
			
			// add the collaborator to the list of nodes
			nodes.add(collaboratorToJSONObject(collaborator));

		}
		
		// build the final object
		object.put("nodes", nodes);
		object.put("edges", edges);
		
		return object.toString();
		
		
	
//		// define helper variables
//		// collection of collaborators
//		java.util.TreeMap<Integer, Collaborator> collaborators = new java.util.TreeMap<Integer, Collaborator>();
//		
//		// keep a list of collaborators that we've seen
//		java.util.TreeSet<Integer> previousCollaborators = new java.util.TreeSet<Integer>();
//		
//		// define other helper variables
//		QuerySolution row             = null;
//		Collaborator  collaborator    = null;
//		Collaborator  collaboratorToProcess = null;
//		Collaborator  collaboratorToAdd = null;
//		int           radiusFollowed = 1;
//		int           radiusToFollow = radius;
//		String        queryToExecute  = null;
//		Collection    values          = null;
//		Iterator      iterator        = null;
//	
//		// define the base sparql query
//		String sparqlQuery = "PREFIX foaf:       <" + FOAF.NS + ">"
//						   + "PREFIX ausestage:  <" + AuseStage.NS + "> "
// 						   + "SELECT ?collaborator ?collabName ?function ?collabCount ?collabFirstDate ?collabLastDate "
//						   + "WHERE {  "
//						   + "       @ a foaf:Person ; "
//						   + "                      ausestage:hasCollaboration ?collaboration. "
//						   + "       ?collaboration ausestage:collaborator ?collaborator; "
//						   + "                      ausestage:collaborationCount ?collabCount; "
//						   + "                      ausestage:collaborationFirstDate ?collabFirstDate; "
//						   + "                      ausestage:collaborationLastDate ?collabLastDate. "
//						   + "       ?collaborator  foaf:name ?collabName; "
//						   + "                      ausestage:function ?function. "
//						   + "       FILTER (?collaborator != @) "
//						   + "} ";
//						   
//		// go and get the intial batch of data
//		collaborator = new Collaborator(id);
//		collaborators.put(Integer.parseInt(id), collaborator);
//		previousCollaborators.add(Integer.parseInt(id));
//		
//		// build the query
//		queryToExecute = sparqlQuery.replaceAll("@", "<" + AusStageURI.getContributorURI(id) + ">");
//		
//		// execute the query
//		ResultSet results = database.executeSparqlQuery(queryToExecute);
//		
//		// add the first degree contributors
//		while (results.hasNext()) {
//			// loop though the resulset
//			// get a new row of data
//			row = results.nextSolution();
//			
//			// get any existing collaborator
//			collaboratorToProcess = collaborator.hasCollaborator(new Collaborator(AusStageURI.getId(row.get("collaborator").toString())));
//			
//			if(collaboratorToProcess == null) {
//			
//				// check to see if we've seen this collaborator linked to another before
//				if(previousCollaborators.contains(Integer.parseInt(AusStageURI.getId(row.get("collaborator").toString()))) == false) {
//			
//					// no we haven't so create a new one
//					
//					// start a new collaborator object
//					collaboratorToProcess = new Collaborator(AusStageURI.getId(row.get("collaborator").toString()));
//	
//					// add the details to this collaborator
//					collaboratorToProcess.setName(row.get("collabName").toString());
//					collaboratorToProcess.setCollaborations(Integer.toString(row.get("collabCount").asLiteral().getInt()));
//					collaboratorToProcess.setFirstDate(row.get("collabFirstDate").toString());
//					collaboratorToProcess.setLastDate(row.get("collabLastDate").toString());
//	
//					// add a function
//					collaboratorToProcess.setFunction(row.get("function").toString());
//	
//					// add this collaborator
//					collaborator.addCollaborator(collaboratorToProcess);
//					
//					// store a reference to this collaborator for later
//					previousCollaborators.add(Integer.parseInt(AusStageURI.getId(row.get("collaborator").toString())));
//				}
//				
//			} else {
//			
//				// add the new function to this collaborator
//				collaboratorToProcess.setFunction(row.get("function").toString());
//			}			
//		}
//		
//		// play nice and tidy up
//		results = null;
//		database.tidyUp();
//		
//		// fill in the details of the primary collaborator
//		sparqlQuery = "PREFIX foaf:       <" + FOAF.NS + ">"
//						   + "PREFIX ausestage:  <" + AuseStage.NS + "> "
// 						   + "SELECT ?collabName ?function  "
//						   + "WHERE {  "
//						   + "       @ a foaf:Person ; "
//						   + "           foaf:name ?collabName; "
//						   + "           ausestage:function ?function. "
//						   + "} ";		   

//		// build the query
//		queryToExecute = sparqlQuery.replaceAll("@", "<" + AusStageURI.getContributorURI(id) + ">");
//		
//		// execute the query
//		results = database.executeSparqlQuery(queryToExecute);
//		
//		// add details to this contributor
//		while (results.hasNext()) {
//			// loop though the resulset
//			// get a new row of data
//			row = results.nextSolution();
//			
//			// this is a hack I know
//			collaborator.setName(row.get("collabName").toString());
//			collaborator.setFunction(row.get("function").toString());
//		}
//		
//		// play nice and tidy up
//		results = null;
//		database.tidyUp();
//		
//		// reset the SPARQL query
//		sparqlQuery = "PREFIX foaf:       <" + FOAF.NS + ">"
//						   + "PREFIX ausestage:  <" + AuseStage.NS + "> "
// 						   + "SELECT ?collaborator ?collabName ?function ?collabCount ?collabFirstDate ?collabLastDate "
//						   + "WHERE {  "
//						   + "       @ a foaf:Person ; "
//						   + "                      ausestage:hasCollaboration ?collaboration. "
//						   + "       ?collaboration ausestage:collaborator ?collaborator; "
//						   + "                      ausestage:collaborationCount ?collabCount; "
//						   + "                      ausestage:collaborationFirstDate ?collabFirstDate; "
//						   + "                      ausestage:collaborationLastDate ?collabLastDate. "
//						   + "       ?collaborator  foaf:name ?collabName; "
//						   + "                      ausestage:function ?function. "
//						   + "       FILTER (?collaborator != @) "
//						   + "} ";
//		
//		// treat the one degree network as a special case
//		if(radiusToFollow == 1) {
//		
//			// get the list of contributors attached to this contributor
//			values   = collaborator.getCollaborators();
//			iterator = values.iterator();
//			
//			// loop through the list of collaborators
//			while(iterator.hasNext()) {
//			
//				// loop through the list of collaborators				
//				collaboratorToProcess = (Collaborator)iterator.next();
//				
//				// build the query
//				queryToExecute = sparqlQuery.replaceAll("@", "<" + AusStageURI.getContributorURI(collaboratorToProcess.getId()) + ">");
//				
//				// get the data
//				results = database.executeSparqlQuery(queryToExecute);
//			
//				// loop though the resulset
//				while (results.hasNext()) {

//					// get a new row of data
//					row = results.nextSolution();
//				
//					// get any existing collaborator
//					collaboratorToAdd = collaboratorToProcess.hasCollaborator(new Collaborator(AusStageURI.getId(row.get("collaborator").toString())));
//			
//					if(collaboratorToAdd == null) {
//			
//						// have we seen this collaborator before?
//						if(previousCollaborators.contains(Integer.parseInt(AusStageURI.getId(row.get("collaborator").toString()))) == false) {
//							// no - so start a new collaborator object
//							collaboratorToAdd = new Collaborator(AusStageURI.getId(row.get("collaborator").toString()));
//		
//							// add the details to this collaborator
//							collaboratorToAdd.setName(row.get("collabName").toString());
//							collaboratorToAdd.setCollaborations(Integer.toString(row.get("collabCount").asLiteral().getInt()));
//							collaboratorToAdd.setFirstDate(row.get("collabFirstDate").toString());
//							collaboratorToAdd.setLastDate(row.get("collabLastDate").toString());
//		
//							// add a function
//							collaboratorToAdd.setFunction(row.get("function").toString());
//		
//							// add this collaborator
//							collaboratorToProcess.addCollaborator(collaboratorToProcess);
//							
//							// store a reference to this collaborator for later
//							previousCollaborators.add(Integer.parseInt(AusStageURI.getId(row.get("collaborator").toString())));
//						}
//				
//					} else {
//						// add the new function to this collaborator
//						collaboratorToAdd.setFunction(row.get("function").toString());
//					}
//				}
//			}		
//		}
//		
////		} else {
////		
////			// get the rest of the degrees
////			while(degreesFollowed < degreesToFollow) {
////		
////				// get all of the known collaborators
////				values = collaborators.values();
////				iterator = values.iterator();
////			
////				// loop through the list of collaborators
////				while(iterator.hasNext()) {
////					// get the collaborator
////					collaborator = (Collaborator)iterator.next();
////				
////					// get the list of contributors to process
////					toProcess = collaborator.getCollaboratorsAsArray();
////				
////					// go through them one by one
////					for(int i = 0; i < toProcess.length; i++) {
////						// have we done this collaborator already
////						if(foundCollaborators.contains(Integer.parseInt(toProcess[i])) == false) {
////							// we haven't so process them
////							collaborator = new Collaborator(toProcess[i]);
////							collaborators.put(Integer.parseInt(toProcess[i]), collaborator);
////							foundCollaborators.add(Integer.parseInt(toProcess[i]));
////						
////							// build the query
////							queryToExecute = sparqlQuery.replaceAll("@", "<" + AusStageURI.getContributorURI(toProcess[i]) + ">");
////						
////							// get the data
////							results = database.executeSparqlQuery(queryToExecute);
////							
////							// loop though the resulset
////							while (results.hasNext()) {
////								// get a new row of data
////								row = results.nextSolution();
////			
////								// add the collaboration
////								collaborator.addCollaborator(AusStageURI.getId(row.get("collaborator").toString()));
////							}
////						
////							// play nice and tidy up
////							database.tidyUp();
////						}
////					}
////				}
////			
////				// increment the degrees followed count
////				degreesFollowed++;
////			}
////			
////			// finalise the graph
////			// get all of the known collaborators and use a copy of the current list of collaborators
////			java.util.TreeMap clone = (java.util.TreeMap)collaborators.clone();
////			values = clone.values();
////			iterator = values.iterator();
////		
////			// loop through the list of collaborators
////			while(iterator.hasNext()) {
////				// get the collaborator
////				collaborator = (Collaborator)iterator.next();
////			
////				// get the list of contributors to process
////				toProcess = collaborator.getCollaboratorsAsArray();
////			
////				// go through them one by one
////				for(int i = 0; i < toProcess.length; i++) {
////					// have we done this collaborator already
////					if(foundCollaborators.contains(Integer.parseInt(toProcess[i])) == false) {
////						// we haven't so process them
////						collaborator = new Collaborator(toProcess[i]);
////						collaborators.put(Integer.parseInt(toProcess[i]), collaborator);
////						foundCollaborators.add(Integer.parseInt(toProcess[i]));
////					
////						// build the query
////						queryToExecute = sparqlQuery.replaceAll("@", "<" + AusStageURI.getContributorURI(toProcess[i]) + ">");
////					
////						// get the data
////						results = database.executeSparqlQuery(queryToExecute);
////						
////						// loop though the resulset
////						while (results.hasNext()) {
////							// get a new row of data
////							row = results.nextSolution();
////							
////							// limit to only those collaborators we've seen before
////							if(foundCollaborators.contains(Integer.parseInt(AusStageURI.getId(row.get("collaborator").toString()))) == true) {
////		
////								// add the collaboration
////								collaborator.addCollaborator(AusStageURI.getId(row.get("collaborator").toString()));
////							}
////						}
////					
////						// play nice and tidy up
////						database.tidyUp();
////					}
////				}
////			}
////			
////		}
////
	
	} // end the getData method
	
	/**
	 * A method to build a JSON object
	 *
	 * @param value the collaborator to process
	 * 
	 * @return      the JSON object
	 */
 	@SuppressWarnings("unchecked")
	private JSONObject collaboratorToJSONObject(Collaborator value) {
	
		// double check the parameter
		if(value == null) {
			throw new IllegalArgumentException("The value parameter is required");
		}

		JSONObject object    = new JSONObject();
		JSONArray  functions = new JSONArray();
		
		// add the first object to the array
		object.put("fix", null);
		object.put("id", value.getId());
		object.put("nodeName", value.getName());
		object.put("nodeUrl", LinksManager.getContributorLink(value.getId()));
		
		if(value.getFunctionAsArray() != null) {
		
			String[] functionsArray = value.getFunctionAsArray();
		
			for(int i = 0; i < functionsArray.length; i++) {
				functions.add(functionsArray[i].trim());
			}
		
			object.put("functions", functions);
		} else {
			object.put("functions", new JSONArray());
		}
		
		// return the object
		return object;
	
	} // end the JSONObject method
}
