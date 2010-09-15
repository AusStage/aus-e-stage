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
import au.edu.ausstage.networks.types.*;

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
	
		/*
		 * get the base network data
		 */
		
		// get an instance of the ExportManager class
		ExportManager export = new ExportManager(database);
		
		// get the data for this collaborator
		java.util.TreeMap<Integer, Collaborator> network = export.getRawCollaboratorData(id, radius);
		
		/*
		 * add some of the additional required information
		 */
		
		// declare helper variables
		java.util.ArrayList<Collaborator> collaborators = new java.util.ArrayList<Collaborator>();
		
		// define a SPARQL query to get details about a collaborator
		String sparqlQuery = "PREFIX foaf:       <" + FOAF.NS + ">"
						   + "PREFIX ausestage:  <" + AuseStage.NS + "> "
 						   + "SELECT ?collabName ?function  "
						   + "WHERE {  "
						   + "       @ a foaf:Person ; "
						   + "           foaf:name ?collabName. "
						   + "OPTIONAL {@ ausestage:function ?function} "
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
			
			// execute the query
			results = database.executeSparqlQuery(queryToExecute);
		
			// add details to this contributor
			while (results.hasNext()) {
				// loop though the resulset
				// get a new row of data
				row = results.nextSolution();
			
				// add the data to the collaborator
				collaborator.setName(row.get("collabName").toString());
				if(row.get("function") != null) {
					collaborator.setFunction(row.get("function").toString());
				}
			}
			
			// play nice and tidy yo
			database.tidyUp();
			results = null;
			
			// add the collaborator to the list
			collaborators.add(collaborator);
		}
		
		/*
		 * get a list of collaborations
		 */
		java.util.HashMap<Integer, CollaborationList> collaborations = new java.util.HashMap<Integer, CollaborationList>();
		
		// reset the iterator
		networkKeys        = network.keySet();
		networkKeyIterator = networkKeys.iterator();
		networkKey         = null;

		CollaborationList collaborationList;
		
		// loop through the list of keys
		while(networkKeyIterator.hasNext()) {
		
			// get the key
			networkKey = (Integer)networkKeyIterator.next();
		
			// get the list of collaborations for this user
			collaborationList = export.getCollaborationList(networkKey);
			
			// add the collaborationList object to the list
			collaborations.put(networkKey, collaborationList);
		}
		
		// play nice and tidy up
		export = null;			
		
		/*
		 * ajust the order of the collaborators in the array
		 */
		
		// find the central collaborator and move them to the head of the array
		networkKey   = collaborators.indexOf(new Collaborator(id));
		collaborator = (Collaborator)collaborators.get(networkKey);
		
		// add the central collaborator to the head of the list
		collaborators.add(0, collaborator);
		
		// remove the old central collaborator object
		collaborators.remove(networkKey + 1);
		
		/*
		 * build an alternate index to the array
		 */
		
		// build an alternate index to the array
		java.util.TreeMap<Integer, Integer> altIndex = new java.util.TreeMap<Integer, Integer>();
		Integer altIndexer = 0;
		
		// build the alternate index
		ListIterator iterator = collaborators.listIterator();
		
		// add the rest of the collaborators
		while(iterator.hasNext()) {
		
			// get the next collaborator in the list
			collaborator = (Collaborator)iterator.next();
			
			// add the collaborator id with the spot in the index
			altIndex.put(Integer.parseInt(collaborator.getId()), altIndexer);
			
			// increment the index count
			altIndexer++;
		}			
		
		/*
		 * build the JSON object and array of nodes
		 */
		
		// build the JSON object and arrays
		JSONArray  nodes  = new JSONArray();
		JSONArray  edges  = new JSONArray();
		JSONObject object = new JSONObject();
		JSONObject edge   = null;
		
		iterator = collaborators.listIterator();
		
		// add the collaborators
		while(iterator.hasNext()) {
		
			// get the next collaborator in the list
			collaborator = (Collaborator)iterator.next();
			
			// add the collaborator to the list of nodes
			nodes.add(collaboratorToJSONObject(collaborator));

		}
		
		// build the final object
		object.put("nodes", nodes);
		
		
		/*
		 * build the JSON array of edges
		 */
		
		Collection networkValues        = network.values();
		Iterator   networkValueIterator = networkValues.iterator();
		String[] edgesToMake = null;
		altIndexer = 0;
		Integer source = null;
		Integer target = null;
		Integer altSourceIndex = null;
		Collaboration collaboration = null;
		
		// add the collaborators
		while(networkValueIterator.hasNext()) {
		
			// get the next collaborator in the list
			collaborator = (Collaborator)networkValueIterator.next();
			source = Integer.parseInt(collaborator.getId());
			altSourceIndex = (Integer)altIndex.get(source);
			
			// get the list of collaborators
			edgesToMake = collaborator.getCollaboratorsAsArray();
			
			// loop through the list of collaborations
			for(int i = 0; i < edgesToMake.length; i++) {
				// determine the index value for this collaborator
				target = Integer.parseInt(edgesToMake[i]);
				
				if(source < target) {
					altIndexer = (Integer)altIndex.get(target);
			
					// build the new edge
					edge = new JSONObject();
					edge.put("source", altSourceIndex);
					edge.put("target", altIndexer);
					
					// get the additional data about the edge
					collaborationList = collaborations.get(source);
					collaboration = collaborationList.getCollaboration(target);
					
					edge.put("value", collaboration.getCollaborationCount());
					edge.put("firstDate",      collaboration.getFirstDate());
					edge.put("lastDate",       collaboration.getLastDate());					
			
					edges.add(edge);
				}
			}		
		}
		
		object.put("edges", edges);
		
		return object.toString();

	
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
