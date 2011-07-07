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
import org.w3c.dom.Document;
import org.w3c.dom.Element;

// general java
import java.util.*;

// import AusStage related packages
import au.edu.ausstage.vocabularies.*;
import au.edu.ausstage.utils.*;
import au.edu.ausstage.networks.types.*;
import au.edu.ausstage.networks.types.Event;

/**
 * A class to manage the export of information
 */
public class ProtovisEgoCentricManager {

	// declare private class level variables
	private DataManager rdf = null;
	//network<contributorID, collaborator>
	public TreeMap<Integer, Collaborator> network = new TreeMap<Integer, Collaborator>();
	public ArrayList<Collaborator> collaborators = new ArrayList<Collaborator> ();
	public HashMap<Integer, CollaborationList> collaborations = new HashMap<Integer, CollaborationList> ();
	//altIndex<collaboratorID, index>
	public TreeMap<Integer, Integer> altIndex = new TreeMap<Integer, Integer>();
	
	//edge attribute for graphml export
	public String [][] edgeAttr = {
			{"SourceContributorID", "string"}, {"TargetContributorID", "string"},
			{"NumOfCollaboration", "int"}, {"FirstDate", "string"}, {"LastDate", "string"}
		};
	
	//node attribute for graphml export
	public String [][] nodeAttr = {
			{"ContributorID", "string"}, {"Label", "string"}, {"ContributorName", "string"}, 
			{"Roles", "string"}, {"Gender", "string"}, {"Nationality", "string"}, {"ContributorURL", "string"}
	};
	
	//public String evtURLprefix =  "http://www.ausstage.edu.au/indexdrilldown.jsp?xcid=59&f_event_id=";
	//public String conURLprefix = "http://www.ausstage.edu.au/indexdrilldown.jsp?xcid=59&f_contrib_id=";
	
	/**
	 * Constructor for this class
	 *
	 * @param rdf an already instantiated instance of the DataManager class
	 */
	public ProtovisEgoCentricManager(DataManager rdf) {	
		// store a reference to this DataManager for later
		this.rdf = rdf;
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
		if(InputUtils.isValidInt(id) == false) {
			throw new IllegalArgumentException("Error: the id parameter is required");
		}
		
		if(InputUtils.isValidInt(radius, ExportServlet.MIN_DEGREES, ExportServlet.MAX_DEGREES) == false) {
			throw new IllegalArgumentException("Error: the radius parameter must be between " + ExportServlet.MIN_DEGREES + " and " + ExportServlet.MAX_DEGREES);
		}
		
		// determine how to build the export
		if(radius == 1) {
			// use the alternate method for an export with a radius of 1
			return alternateData(id);
		} 
	
		/*
		 * get the base network data
		 */
		// get the network data for this collaborator
		network = getRawCollaboratorData(id, radius);
		
		//add some additional required information for contributors
		collaborators = getCollaborators(network, id);
		
		//get collaboration List			
		collaborations = getCollaborations(network);
		
		/*
		 * ajust the order of the collaborators in the array
		 */
		
		// find the central collaborator and move them to the head of the array
		int networkKey = collaborators.indexOf(new Collaborator(id));
		Collaborator collaborator = (Collaborator)collaborators.get(networkKey);
		
		/*
		// add the central collaborator to the head of the list
		collaborators.add(0, collaborator);
		
		// remove the old central collaborator object
		collaborators.remove(networkKey + 1);
		*/
		
		// remove the old central collaborator object
		collaborators.remove(new Collaborator(id));
		
		// add the central collaborator to the end of the list
		collaborators.add(collaborator);
		
		/*
		 * build an alternate index to the array
		 */
		 altIndex = buildAltIndex();		
		
		/*
		 * build the JSON object and array of nodes
		 */		
		JSONObject object = new JSONObject();		
		
		// build the JSON object and arrays of nodes
		object = buildJSONNodes(object,collaborators);				
		
		// build the JSON array of edges
		object = buildJSONEdges(object, id);
	
		return object.toString();

	
	} // end the getData method
	
	
	/**
	 * A method to build a collection of collaborator object representing a network
	 *
	 * @param id     the unique identifier of the root collaborator
	 * @param radius the number of edges required from the central contributor
	 *
	 * @return        the collection of collaborator objects
	 */
	@SuppressWarnings("rawtypes")
	public TreeMap<Integer, Collaborator> getRawCollaboratorData(String id, int radius) {
	
		// check the parameters
		if(InputUtils.isValidInt(id) == false) {
			throw new IllegalArgumentException("Error: the id parameter is required");
		}
		
		if(InputUtils.isValidInt(radius, ExportServlet.MIN_DEGREES, ExportServlet.MAX_DEGREES) == false) {
			throw new IllegalArgumentException("Error: the radius parameter must be between " + ExportServlet.MIN_DEGREES + " and " + ExportServlet.MAX_DEGREES);
		}		
	
		// define helper variables
		// collection of collaborators
		java.util.TreeMap<Integer, Collaborator> cId_cObj_map = new java.util.TreeMap<Integer, Collaborator>();
		
		// set of collaborators that we've already processed
		java.util.TreeSet<Integer> foundCollaboratorsSet = new java.util.TreeSet<Integer>();
		
		// define other helper variables
		QuerySolution row             = null;
		Collaborator  collaborator    = null;
		int           degreesFollowed = 1;
		int           degreesToFollow = radius;
		String        queryToExecute  = null;
		Collection    values          = null;
		Iterator      iterator        = null;
		String[]      toProcess       = null;
		
		// define the base sparql query
		String sparqlQuery = "PREFIX foaf:       <" + FOAF.NS + ">"
						   + "PREFIX ausestage:  <" + AuseStage.NS + "> "
 						   + "SELECT ?collaborator ?collabGivenName ?collabFamilyName "
						   + "WHERE {  "
						   + "       @ a foaf:Person ;  "
						   + "                      ausestage:hasCollaboration ?collaboration.  "
						   + "       ?collaboration ausestage:collaborator ?collaborator. "
						   + "       ?collaborator  foaf:givenName ?collabGivenName; "
						   + "                      foaf:familyName ?collabFamilyName. "
						   + "       FILTER (?collaborator != @) "
						   + "} ";
						   
		// go and get the intial batch of data
		collaborator = new Collaborator(id);
		cId_cObj_map.put(Integer.parseInt(id), collaborator);
		foundCollaboratorsSet.add(Integer.parseInt(id));
		
		// build the query
		queryToExecute = sparqlQuery.replaceAll("@", "<" + AusStageURI.getContributorURI(id) + ">");
		
		// execute the query
		ResultSet results = rdf.executeSparqlQuery(queryToExecute);
		
		// add the first degree contributors
		while (results.hasNext()) {
			// loop though the resulset
			// get a new row of data
			row = results.nextSolution();
			
			// add the collaboration
			collaborator.addCollaborator(AusStageURI.getId(row.get("collaborator").toString()));
			//System.out.println(AusStageURI.getId(row.get("collaborator").toString()) + " - " + row.get("collabGivenName") + " - " + row.get("collabFamilyName"));
		}
		
		// play nice and tidy up
		rdf.tidyUp();
		
		// treat the one degree network as a special case
		if(degreesToFollow == 1) {
		
			// get the list of contributors attached to this contributor
			values   = collaborator.getCollaborators();
			iterator = values.iterator();
			
			// loop through the list of collaborators
			while(iterator.hasNext()) {
			
				// loop through the list of collaborators
				id = (String)iterator.next();
				
				// add a new collaborator
				collaborator = new Collaborator(id);
				
				cId_cObj_map.put(Integer.parseInt(id), collaborator);
				
				// build the query
				queryToExecute = sparqlQuery.replaceAll("@", "<" + AusStageURI.getContributorURI(id) + ">");
				
				// get the data
				results = rdf.executeSparqlQuery(queryToExecute);
			
				// loop though the resulset
				while (results.hasNext()) {
					
					// get a new row of data
					row = results.nextSolution();
					
					if(values.contains(AusStageURI.getId(row.get("collaborator").toString())) == true) {
						collaborator.addCollaborator(AusStageURI.getId(row.get("collaborator").toString()));
					}
				}
				// play nice and tidy up
				rdf.tidyUp();

			}		
		
		} else {
		
			// get the rest of the degrees
			while(degreesFollowed < degreesToFollow) {
		
				// get all of the known collaborators
				values = cId_cObj_map.values();
				iterator = values.iterator();
			
				// loop through the list of collaborators
				while(iterator.hasNext()) {
					// get the collaborator
					collaborator = (Collaborator)iterator.next();
				
					// get the list of contributors to process
					toProcess = collaborator.getCollaboratorsAsArray();
				
					// go through them one by one
					for(int i = 0; i < toProcess.length; i++) {
						// have we done this collaborator already
						if(foundCollaboratorsSet.contains(Integer.parseInt(toProcess[i])) == false) {
							// we haven't so process them
							collaborator = new Collaborator(toProcess[i]);
							cId_cObj_map.put(Integer.parseInt(toProcess[i]), collaborator);
							foundCollaboratorsSet.add(Integer.parseInt(toProcess[i]));
						
							// build the query
							queryToExecute = sparqlQuery.replaceAll("@", "<" + AusStageURI.getContributorURI(toProcess[i]) + ">");
						
							// get the data
							results = rdf.executeSparqlQuery(queryToExecute);
							
							// loop though the resulset
							while (results.hasNext()) {
								// get a new row of data
								row = results.nextSolution();
			
								// add the collaboration
								collaborator.addCollaborator(AusStageURI.getId(row.get("collaborator").toString()));
							}
						
							// play nice and tidy up
							rdf.tidyUp();
						}
					}
				}
			
				// increment the degrees followed count
				degreesFollowed++;
			}
			
			// finalise the graph
			// get all of the known collaborators and use a copy of the current list of collaborators
			java.util.TreeMap clone = (java.util.TreeMap)cId_cObj_map.clone();
			values = clone.values();
			iterator = values.iterator();
		
			// loop through the list of collaborators
			while(iterator.hasNext()) {
				// get the collaborator
				collaborator = (Collaborator)iterator.next();
			
				// get the list of contributors to process
				toProcess = collaborator.getCollaboratorsAsArray();
			
				// go through them one by one
				for(int i = 0; i < toProcess.length; i++) {
					// have we done this collaborator already
					if(foundCollaboratorsSet.contains(Integer.parseInt(toProcess[i])) == false) {
						// we haven't so process them
						collaborator = new Collaborator(toProcess[i]);
						cId_cObj_map.put(Integer.parseInt(toProcess[i]), collaborator);
						foundCollaboratorsSet.add(Integer.parseInt(toProcess[i]));
					
						// build the query
						queryToExecute = sparqlQuery.replaceAll("@", "<" + AusStageURI.getContributorURI(toProcess[i]) + ">");
					
						// get the data
						results = rdf.executeSparqlQuery(queryToExecute);
						
						// loop though the resulset
						while (results.hasNext()) {
							// get a new row of data
							row = results.nextSolution();
							
							// limit to only those collaborators we've seen before
							if(foundCollaboratorsSet.contains(Integer.parseInt(AusStageURI.getId(row.get("collaborator").toString()))) == true) {
		
								// add the collaboration
								collaborator.addCollaborator(AusStageURI.getId(row.get("collaborator").toString()));
							}
						}
					
						// play nice and tidy up
						rdf.tidyUp();
					}
				}
			}
			
		}
			
		return cId_cObj_map;
	
	} // end getRawCollaboratorData method
	
	
	/*
	 * add some of the additional required information for collaborator
	 */
	public ArrayList<Collaborator> getCollaborators(TreeMap<Integer, Collaborator> network, String id){
		
		// declare helper variables
		java.util.ArrayList<Collaborator> collaborators = new java.util.ArrayList<Collaborator>();
		
		// define a SPARQL query to get details about a collaborator
		String sparqlQuery = "PREFIX foaf:       <" + FOAF.NS + ">"
						   + "PREFIX ausestage:  <" + AuseStage.NS + "> "
 						   + "SELECT ?collabName ?function ?gender ?nationality  "
						   + "WHERE {  "
						   + "       @ a foaf:Person ; "
						   + "           foaf:name ?collabName. "
						   + "OPTIONAL {@ ausestage:function ?function} "
						   + "OPTIONAL {@ foaf:gender ?gender} "
						   + "OPTIONAL {@ ausestage:nationality ?nationality} "
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
			results = rdf.executeSparqlQuery(queryToExecute);
		
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
				
				if(row.get("gender") != null) {
					collaborator.setGender(row.get("gender").toString());
				}
				
				if(row.get("nationality") != null) {
					collaborator.setNationality(row.get("nationality").toString());
				}
				
				collaborator.setUrl(AusStageURI.getContributorURL(collaborator.getId()));
			}
			
			// play nice and tidy
			rdf.tidyUp();
			results = null;
			
			// add the collaborator to the list
			collaborators.add(collaborator);
		}
		
		return collaborators;
	}
	
	
	@SuppressWarnings("rawtypes")
	public TreeMap<Integer, Integer> buildAltIndex(){
		
		// build an alternate index to the array
		TreeMap<Integer, Integer> index = new TreeMap<Integer, Integer>();
		Integer altIndexer = 0;
		
		// build the alternate index
		ListIterator iterator = collaborators.listIterator();
		
		// add the rest of the collaborators
		while(iterator.hasNext()) {
		
			// get the next collaborator in the list
			Collaborator collaborator = (Collaborator)iterator.next();
			
			// add the collaborator id with the spot in the index
			index.put(Integer.parseInt(collaborator.getId()), altIndexer);
			
			// increment the index count
			altIndexer++;
		}
		return index;
	}
	
	/*
	 * get a list of collaborations
	 */
	public HashMap<Integer, CollaborationList> getCollaborations(TreeMap<Integer, Collaborator> network){
		HashMap<Integer, CollaborationList> collaborations = new java.util.HashMap<Integer, CollaborationList>();

		// reset the iterator
		Set<Integer> networkKeys = network.keySet();
		Iterator<Integer> networkKeyIterator = networkKeys.iterator();
		Integer networkKey = null;

		CollaborationList collaborationList;

		// loop through the list of keys
		while (networkKeyIterator.hasNext()) {

			// get the key
			networkKey = (Integer) networkKeyIterator.next();

			// get the list of collaborations for this user
			collaborationList = getCollaborationList(networkKey);

			// add the collaborationList object to the list
			collaborations.put(networkKey, collaborationList);
		}
	
		return collaborations;
	}
	
	/**
	 * A method to build a list of Collaboration objects representing a list of collaborations associated with a collaborator
	 *
	 * @param id the unique identifier of a collaborator
	 *
	 * @return   a CollaborationList object containing a list of Collaboration objects
	 */
	public CollaborationList getCollaborationList(Integer id) {
		return getCollaborationList(Integer.toString(id));
	}
	
	/**
	 * A method to build a list of Collaboration objects representing a list of collaborations associated with a collaborator
	 *
	 * @param id the unique identifier of a collaborator
	 *
	 * @return   a CollaborationList object containing a list of Collaboration objects
	 */
	public CollaborationList getCollaborationList(String id) {
	
		// check on the input parameters
		if(InputUtils.isValidInt(id) == false) {
			throw new IllegalArgumentException("The id parameter cannot be null");
		}
		
		// declare helper variables
		CollaborationList list = new CollaborationList(id);
		
		// define other helper variables
		QuerySolution row           = null;
		Collaboration collaboration = null;
		
		// define other helper variables
		String  partner   = null;
		Integer count     = null;
		String  firstDate = null;
		String  lastDate  = null;
	
		// define the base sparql query
		String sparqlQuery = "PREFIX foaf:       <" + FOAF.NS + ">"
						   + "PREFIX ausestage:  <" + AuseStage.NS + "> "
 						   + "SELECT ?collaborator ?firstDate ?lastDate ?collabCount "
 						   + "WHERE {  "
 						   + "       @ a foaf:Person ; "
   						   + "ausestage:hasCollaboration ?collaboration. "
 						   + "       ?collaboration ausestage:collaborator ?collaborator; "
 						   + "                      ausestage:collaborationFirstDate ?firstDate; " 
 						   + "                      ausestage:collaborationLastDate ?lastDate; "
 						   + "                      ausestage:collaborationCount ?collabCount. "
 						   + "       FILTER (?collaborator != @) "
 						   + "} ";
		
		// build the query
		String queryToExecute = sparqlQuery.replaceAll("@", "<" + AusStageURI.getContributorURI(id) + ">");
		
		// execute the query
		ResultSet results = rdf.executeSparqlQuery(queryToExecute);
		
		// add the first degree contributors
		while (results.hasNext()) {
			// loop though the resulset
			// get a new row of data
			row = results.nextSolution();
			
			// get the data
			partner   = AusStageURI.getId(row.get("collaborator").toString());
			count     = row.get("collabCount").asLiteral().getInt();
			firstDate = row.get("firstDate").toString();
			lastDate  = row.get("lastDate").toString();
			
			// create the collaboration object
			collaboration = new Collaboration(id, partner, count, firstDate, lastDate);
			
			// add the collaboration to the list
			list.addCollaboration(collaboration); 
		}
		
		// play nice and tidy up
		rdf.tidyUp();
		
		// return the list of collaborations
		return list;		
	
	} // end the CollaborationList method
	
	
	public JSONObject buildJSONNodes(JSONObject object, ArrayList<Collaborator> collaborators){
		
		ListIterator<Collaborator> iterator = collaborators.listIterator();
		JSONArray  nodes  = new JSONArray();
		// add the collaborators
		while(iterator.hasNext()) {
		
			// get the next collaborator in the list
			Collaborator collaborator = (Collaborator)iterator.next();
			
			// add the collaborator to the list of nodes
			nodes.add(collaboratorToJSONObject(collaborator));

		}
		
		// build the final object
		object.put("nodes", nodes);
		
		return object;
	}
	
	@SuppressWarnings("unchecked")
	public JSONObject buildJSONEdges(JSONObject object, String id){
		
		Collection networkValues        = network.values();
		Iterator   networkValueIterator = networkValues.iterator();
		String[] edgesToMake = null;
		int altIndexer = 0;
		Integer source = null;
		Integer target = null;
		Integer altSourceIndex = null;
		Collaboration collaboration = null;
		JSONObject edge   = null;
		JSONArray  edges  = new JSONArray();
		
		// add the edges
		while(networkValueIterator.hasNext()) {
		
			// get the next collaborator in the list
			Collaborator collaborator = (Collaborator)networkValueIterator.next();
			source = Integer.parseInt(collaborator.getId());
			altSourceIndex = (Integer)altIndex.get(source);
			
			// get the list of collaborators
			edgesToMake = collaborator.getCollaboratorsAsArray();
			
			CollaborationList collaborationList;
			// treat the edges to the central node different to the other edges
			if(source == Integer.parseInt(id)) {
			
				// edges to the central node
				// loop through the list of collaborations
				for(int i = 0; i < edgesToMake.length; i++) {
					// determine the index value for this collaborator
					target = Integer.parseInt(edgesToMake[i]);
				
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
			} else {
				// other edges
				// loop through the list of collaborations
				for(int i = 0; i < edgesToMake.length; i++) {
					// determine the index value for this collaborator
					target = Integer.parseInt(edgesToMake[i]);
				
					altIndexer = (Integer)altIndex.get(target);
		
					if(source < target) {
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
	
		}
		
		object.put("edges", edges);
		
		return object;
	}
	
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
		
		object.put("gender", value.getGender());
		object.put("nationality", value.getNationality());
		
		// return the object
		return object;
	
	} // end the JSONObject method
	
	/**
	 * A private method to read the connection string parameter from the web.xml file
	 *
	 * @return the database connection string
	 */
	private String getConnectionString() {
		if(InputUtils.isValid(rdf.getContextParam("databaseConnectionString")) == false) {
			throw new RuntimeException("Unable to read the connection string parameter from the web.xml file");
		} else {
			return rdf.getContextParam("databaseConnectionString");
		}
	}

	/**
	 * A private method to use an alternate algorithm to build a protovise export
	 *
	 * @param id the unique identifier of the collaborator
	 *
	 * @return   the JSON encoded protovis export
	 */
	@SuppressWarnings("unchecked")
	private String alternateData(String id) {
	
		// instantiate a connection to the database
		DbManager database = new DbManager(getConnectionString());
		
		if(database.connect() == false) {
			throw new RuntimeException("Unable to connect to the database");
		}
		
		// declare the sql variables
		String sql = "SELECT f.contributorid AS contrib1, e.contributorid AS contrib2, COUNT(f.eventid) as collaborations,  "
				   + "       MIN(CONCAT(events.yyyyfirst_date, CONCAT('-', CONCAT(events.mmfirst_date, CONCAT('-', events.ddfirst_date))))) as first_date,  "
				   + "       MAX(CONCAT(events.yyyylast_date, CONCAT('-', CONCAT(events.mmlast_date, CONCAT('-', events.ddlast_date))))) as last_date  "
				   + "FROM conevlink f, (SELECT DISTINCT d.contributorid, d.eventid "
				   + "                   FROM conevlink d, (SELECT DISTINCT b.contributorid "
				   + "                                      FROM conevlink b, (SELECT DISTINCT eventid FROM conevlink WHERE contributorid = ?) a "
				   + "                                      WHERE b.eventid = a.eventid) c "
				   + "                   WHERE d.contributorid = c.contributorid) e, "
				   + "                   (SELECT DISTINCT b.contributorid "
				   + "                    FROM conevlink b, (SELECT DISTINCT eventid FROM conevlink WHERE contributorid = ?) a "
				   + "                    WHERE b.eventid = a.eventid) g, "
				   + "     events "
				   + "WHERE e.eventid = f.eventid "
				   + "AND f.contributorid = g.contributorid "
				   + "AND f.eventid = events.eventid "
				   + "GROUP BY f.contributorid, e.contributorid";
				   
		// define the paramaters
		String[] sqlParameters = {id, id};

		// get the data
		DbObjects results = database.executePreparedStatement(sql, sqlParameters);
		
		// check to see that data was returned
		if(results == null) {
			// return an empty JSON Array
			return new JSONArray().toString();
		}
		
		// instantiate helper objects
		ArrayList<Node>    nodes      = new ArrayList<Node>();
		ArrayList<Integer> nodesIndex = new ArrayList<Integer>();
		HashSet<Integer>   nodesToGet = new HashSet<Integer>();
		Node               node       = null;
		
		HashSet<Edge> edges = new HashSet<Edge>();
		Edge          edge  = null;
		
		Integer source = null;
		Integer target = null;
		
		// build the result data
		java.sql.ResultSet resultSet = results.getResultSet();
		
		// build the list of results
		try {
		
			// loop through the resultset
			while(resultSet.next() == true) {
			
				// check to see if we've seen this node before
				source = new Integer(resultSet.getString(1));
				target = new Integer(resultSet.getString(2));
				
				if(source < target) {
				
					if(nodesToGet.contains(source) == false) {
						nodesToGet.add(source);
					}
									
					if(nodesToGet.contains(target) == false) {
						nodesToGet.add(target);
					}
				
					// build a new edge object
					edge = new Edge();
				
					edge.setSource(source);
					edge.setTarget(target);
					edge.setValue(new Integer(resultSet.getString(3)));
					edge.setFirstDate(resultSet.getString(4));
					edge.setLastDate(resultSet.getString(5));
				
					edges.add(edge);				
				}
			}
		
		} catch (java.sql.SQLException ex) {
			// return an empty JSON Array
			return new JSONArray().toString();
		}
		
		// play nice and tidy up
		resultSet = null;
		results.tidyUp();
		results = null;
		
		// get the rest of the information about the nodes
		
		// redefine the sql variables
		sql = "SELECT c.first_name, c.last_name, DECODE(g.gender, null, 'Unknown', g.gender) as gender, DECODE(c.nationality, null, 'Unknown', c.nationality), cp.preferredterm "
			+ "FROM contributor c, gendermenu g, contributorfunctpreferred cp, contfunctlink cl "
			+ "WHERE c.contributorid = ? " 
			+ "AND c.gender = g.genderid (+) "
			+ "AND c.contributorid = cl.contributorid (+) "
			+ "AND cl.contributorfunctpreferredid = cp.contributorfunctpreferredid (+)";
			
		sqlParameters = new String[1];
		
		// loop through the list of contributors
		Iterator iterator = nodesToGet.iterator();
		
		while(iterator.hasNext() == true) {
			
			// reuse one of the integer variables
			source = (Integer)iterator.next();			
			sqlParameters[0] = source.toString();
			
			// reset the node variable
			node = null;
			
			results = database.executePreparedStatement(sql, sqlParameters);
		
			// check to see that data was returned
			if(results == null) {
				// return an empty JSON Array
				return new JSONArray().toString();
			}
			
			resultSet = results.getResultSet();
		
			// build the list of results
			try {
	
				// loop through the resultset
				while(resultSet.next() == true) {
				
					if(node == null) {
						node = new Node();
						
						// build the new node
						node.setId(source);
						node.setName(resultSet.getString(1) + " " + resultSet.getString(2));
						node.setGender(resultSet.getString(3));
						node.setNationality(resultSet.getString(4));
						
						// deal with those contributors that do not have functions
						if(resultSet.getString(5) != null) {
							node.addFunction(resultSet.getString(5));
						} else {
							node.addFunction("Unknown");
						}
						
						node.setUrl(LinksManager.getContributorLink(source.toString()));
					} else {
						node.addFunction(resultSet.getString(5));
					}				
				}
				
			} catch (java.sql.SQLException ex) {
				// return an empty JSON Array
				return new JSONArray().toString();
			}
			
			// play nice and tidy up
			resultSet = null;
			results.tidyUp();
			results = null;
			
			// add the node to the list
			nodes.add(node);
			nodesIndex.add(source);
		}
		
		// finalise the list of nodes
		source = new Integer(id);
		target = nodesIndex.indexOf(source);
		node = (Node)nodes.get(target);
		nodes.remove(target.intValue());
		nodesIndex.remove(nodesIndex.indexOf(source));
		nodes.add(node);
		nodesIndex.add(source);
		
		//reindex the edges
		iterator = edges.iterator();
		
		while(iterator.hasNext() == true) {

			edge = (Edge)iterator.next();
			edge.setSource(nodesIndex.indexOf(edge.getSource()));
			edge.setTarget(nodesIndex.indexOf(edge.getTarget()));
		}
		
//		try {
//			// can't call finalize directly as it has protected scope
//			//database.finalize();
//			
//		} catch (Throwable e) {
//			e.printStackTrace();
//		}
		
		
		//declare JSON related variables
		JSONObject object     = new JSONObject();
		JSONArray  nodesList  = new JSONArray();
		JSONArray  edgesList  = new JSONArray();
		
		// add the list of nodes to the list
		nodesList.addAll(nodes);
		
		// add the list of edges to the list
		edgesList.addAll(edges);
		
		// build the final object
		object.put("nodes", nodesList);
		object.put("edges", edgesList);
		
		// return the JSON string
		return object.toString();	
	}
	
	@SuppressWarnings("unchecked")
	public Document toGraphMLDOM(Document egoDom, String id, int radius, String graphType){
		
		// check on the parameters
		if(InputUtils.isValidInt(id) == false) {
			throw new IllegalArgumentException("Error: the id parameter is required");
		}
		
		if(InputUtils.isValidInt(radius, ExportServlet.MIN_DEGREES, ExportServlet.MAX_DEGREES) == false) {
			throw new IllegalArgumentException("Error: the radius parameter must be between " + ExportServlet.MIN_DEGREES + " and " + ExportServlet.MAX_DEGREES);
		}
		
		/*// determine how to build the export
		if(radius == 1) {
			// use the alternate method for an export with a radius of 1
			return alternateData(id);
		} */
	
		/*
		 * get the base network data
		 */
		// get the network data for this collaborator
		network = getRawCollaboratorData(id, radius);
		
		//add some additional required information for contributors
		collaborators = getCollaborators(network, id);
		
		//get collaboration List			
		collaborations = getCollaborations(network);
		
		/*
		 * ajust the order of the collaborators in the array
		 */
		
		// find the central collaborator and move them to the head of the array
		int networkKey = collaborators.indexOf(new Collaborator(id));
		Collaborator center_collaborator = (Collaborator)collaborators.get(networkKey);
		
		// remove the old central collaborator object
		collaborators.remove(new Collaborator(id));
		
		// add the central collaborator to the end of the list
		collaborators.add(center_collaborator);
		
		// build an alternate index to the array
		//altIndex = buildAltIndex();
			
		Element rootElement = egoDom.getDocumentElement();
		rootElement = createHeaderElements(egoDom, rootElement, graphType);

		// add the graph element
		Element graph = egoDom.createElement("graph");
		graph.setAttribute("id", "Contributor Network for contributor: " +  center_collaborator.getName() + "( " + id + " )");
		graph.setAttribute("edgedefault", graphType);
		rootElement.appendChild(graph);
		
		//create node element in DOM		
		for (int i = 0; i < collaborators.size(); i++){
			Element node = createNodeElement(egoDom,i);			
			graph.appendChild(node);
		}
		
		//create edge element in DOM
		Collection networkValues        = network.values();
		Iterator   networkValueIterator = networkValues.iterator();
		String[] edgesToMake = null;
		
		Integer source = null;
		Integer target = null;
		//Integer altSource = null;
		//Integer altTarget = 0;
		Collaboration collaboration = null;
		int edgeIndex = 0;
		
		// add the edges
		while(networkValueIterator.hasNext()) {
		
			// get the next collaborator in the list
			Collaborator collaborator = (Collaborator)networkValueIterator.next();
			source = Integer.parseInt(collaborator.getId());
			//altSource = (Integer)altIndex.get(source);
			
			// get the list of collaborators
			edgesToMake = collaborator.getCollaboratorsAsArray();
			
			CollaborationList collaborationList;
			// treat the edges to the central node different to the other edges
			if(source == Integer.parseInt(id)) {
			
				// edges to the central node
				// loop through the list of collaborations
				for(int i = 0; i < edgesToMake.length; i++) {
					// determine the index value for this collaborator
					target = Integer.parseInt(edgesToMake[i]);				
					//altTarget = (Integer)altIndex.get(target);
					
					// get the additional data about the edge
					collaborationList = collaborations.get(source);
					collaboration = collaborationList.getCollaboration(Integer.parseInt(edgesToMake[i]));
					
					Element edge = createEdgeElement(egoDom, collaboration, source, target, edgeIndex);
					graph.appendChild(edge);
					edgeIndex ++;
				}
			} else {
				// other edges
				// loop through the list of collaborations
				for(int i = 0; i < edgesToMake.length; i++) {
					// determine the index value for this collaborator
					target = Integer.parseInt(edgesToMake[i]);				
					//altTarget = (Integer)altIndex.get(target);
		
					if(source < target) {
										
						// get the additional data about the edge
						collaborationList = collaborations.get(source);
						collaboration = collaborationList.getCollaboration(target);
				
						Element edge = createEdgeElement(egoDom, collaboration, source, target, edgeIndex);
						graph.appendChild(edge);
						edgeIndex ++;
					}
				}
			}
	
		}
		return egoDom;
	}
	
	public Element createHeaderElements (Document egoDom, Element rootElement, String graphType){
		Element key;
			
		// add schema namespace to the root element
		rootElement.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns:xsi", "http://www.w3.org/2001/XMLSchema-instance");
		
		// add reference to the kml schema
		rootElement.setAttribute("xsi:schemaLocation", "http://graphml.graphdrawing.org/xmlns http://graphml.graphdrawing.org/xmlns/1.0/graphml.xsd");
		
		// add some useful comments to the file
		rootElement.appendChild(egoDom.createComment("Graph generated on: " + DateUtils.getCurrentDateAndTime()));
//		rootElement.appendChild(xmlDoc.createComment("Graph generated by: " + rdf.getContextParam("systemName") + " Version: " + rdf.getContextParam("systemVersion") + " Build: " + rdf.getContextParam("buildVersion")));
		
		//create key element for node
		for(int row = 0; row < nodeAttr.length; row ++){
			key = createKeyElement(egoDom, "node", nodeAttr[row][0],  nodeAttr[row][1]);
			rootElement.appendChild(key);			
		}
				
		//create key element for edge
		for(int row = 0; row < edgeAttr.length; row ++){
			key = createKeyElement(egoDom, "edge", edgeAttr[row][0], edgeAttr[row][1]);
			rootElement.appendChild(key);
		}
				
		return rootElement;
	}
	
	public Element createKeyElement(Document egoDom, String nodeOrEdge, String name, String type){
		Element key;
		
		key = egoDom.createElement("key");
		key.setAttribute("id", name);
		key.setAttribute("for", nodeOrEdge);
		key.setAttribute("attr.name", name);
		key.setAttribute("attr.type", type);
		
		return key;
	}
	
	public Element createNodeElement(Document dom, int i){
		Element data;
		
		Collaborator col = collaborators.get(i);
		Element node = dom.createElement("node");
		node.setAttribute("id", col.getId());
		
		for(int row = 0; row < nodeAttr.length; row ++){
			data = dom.createElement("data");
			data.setAttribute("key", nodeAttr[row][0]);
			
			if (nodeAttr[row][0].equalsIgnoreCase("ContributorID"))
				data.setTextContent(col.getId()); 
			else if	(nodeAttr[row][0].equalsIgnoreCase("Label"))
				data.setTextContent(col.getName());
			else if (nodeAttr[row][0].equalsIgnoreCase("ContributorName"))
				data.setTextContent(col.getName());
			else if (nodeAttr[row][0].equalsIgnoreCase("Roles")) 
				data.setTextContent(col.getFunction());
			else if (nodeAttr[row][0].equalsIgnoreCase("Gender")) 
				data.setTextContent(col.getGender());
			else if (nodeAttr[row][0].equalsIgnoreCase("Nationality")) 
				data.setTextContent(col.getNationality());
			else if (nodeAttr[row][0].equalsIgnoreCase("ContributorURL"))
				data.setTextContent(col.getUrl());	
				//data.setTextContent(conURLprefix + col.getId());			
			
			node.appendChild(data);			
		}
				
		return node;		
	}
	
	public Element createEdgeElement(Document evtDom, Collaboration collaboration, int src, int tar, int index){
				
		Element edge = evtDom.createElement("edge");
		edge.setAttribute("id", "e" + Integer.toString(index));
		edge.setAttribute("source", Integer.toString(src));
		edge.setAttribute("target", Integer.toString(tar));
		
		for(int row = 0; row < edgeAttr.length; row ++){
			Element data = evtDom.createElement("data");
			data.setAttribute("key", edgeAttr[row][0]);
			
			if (edgeAttr[row][0].equalsIgnoreCase("SourceContributorID"))
				data.setTextContent(Integer.toString(src)); 
			else if	(edgeAttr[row][0].equalsIgnoreCase("TargetContributorID"))
				data.setTextContent(Integer.toString(tar));
			else if	(edgeAttr[row][0].equalsIgnoreCase("NumOfCollaboration"))
				data.setTextContent(Integer.toString(collaboration.getCollaborationCount()));
			else if (edgeAttr[row][0].equalsIgnoreCase("FirstDate"))
				data.setTextContent(collaboration.getFirstDate());
			else if (edgeAttr[row][0].equalsIgnoreCase("LastDate"))
				data.setTextContent(collaboration.getLastDate());
				
			edge.appendChild(data);
		}
		return edge;
	}
	
	/**
	 * A private class used to represent a node in the export
	 */
	private class Node implements JSONAware {
	
		// declare private class variables
		Integer id          = null;
		String  name        = null;
		String  url         = null;
		String  gender      = null;
		String  nationality = null;
		java.util.ArrayList<String> functions;		
	
		/**
		 * Constructor for this class
		 */
		public Node(Integer id, String name, String url, String gender, String nationality) {
			
			// store the details of this node
			this.id          = id;
			this.name        = name;
			this.url         = url;
			this.gender      = gender;
			this.nationality = nationality;
			
			// instantiate other variables
			functions = new ArrayList<String>();
		}
		
		public Node() {
			
			// instantiate other variables
			functions = new ArrayList<String>();
		}
		
		/*
		 * get and set methods
		 */
		public Integer getId() {
			return id;
		}
		
		public void setId(Integer value) {
			this.id = value;
		}
		
		public String getName() {
			return name;
		}
		
		public void setName(String value) {
			this.name = value;
		}
		
		public String getUrl() {
			return url;
		}
		
		public void setUrl(String value) {
			this.url = value;
		}
		
		public String getGender() {
			return gender;
		}
		
		public void setGender(String value) {
			this.gender = value;
		}
		
		public String getNationality() {
			return nationality;
		}
		
		public void setNationality(String value) {
			this.nationality = value;
		}
		
		public void addFunction(String value) {
			functions.add(value);
		}
		
		public java.util.ArrayList<String> getFunctions() {
			return functions;
		}
		
		public String[] getFunctionArray() {
			return functions.toArray(new String[1]);
		}
		
		@SuppressWarnings("unchecked")
		public JSONArray getFunctionJSONArray() {
			String[] functions = getFunctionArray();
			
			if(functions.length > 0) {
			
				JSONArray list = new JSONArray();
				
				for(int i = 0; i < functions.length; i++) {
					list.add(functions[i]);
				}
				
				return list;
			
			} else {
				return new JSONArray();
			}
		}
		
		@SuppressWarnings("unchecked")
		public JSONObject getJSONObject() {
			JSONObject object = new JSONObject();
			
			object.put("id", id);
			object.put("nodeName", name);
			object.put("nodeUrl", url);
			object.put("gender", gender);
			object.put("nationality", nationality);
			object.put("functions", getFunctionJSONArray());
			
			return object;
		}
		
		@SuppressWarnings("unchecked")
		public String toJSONString(){
			return getJSONObject().toString();
		}
	}
	
	/**
	 * A private class used to represent an edge in the export
	 */
	private class Edge implements JSONAware {
	
		// declare private variables
		Integer source;
		Integer target;
		String  firstDate;
		String  lastDate;
		Integer value;
		
		public Edge(Integer source, Integer target, String firstDate, String lastDate, Integer value) {
			this.source = source;
			this.target = target;
			this.firstDate = firstDate;
			this.lastDate = lastDate;
			this.value = value;
		}
		
		public Edge() {};
		
		public void setSource(Integer value) {
			this.source = value;
		}
		
		public Integer getSource() {
			return source;
		}
		
		public void setTarget(Integer value) {
			this.target = value;
		}
		
		public Integer getTarget() {
			return target;
		}
		
		public void setFirstDate(String value) {
			firstDate = value;
		}
		
		public String getFirstDate() {
			return firstDate;
		}
		
		public void setLastDate(String value) {
			lastDate = value;
		} 
		
		public String getLastDate() {
			return lastDate;
		}
		
		public void setValue(Integer value) {
			this.value = value;
		}
		
		public Integer getValue() {
			return value;
		}
		
		@SuppressWarnings("unchecked")
		public JSONObject getJSONObject() {
			JSONObject object = new JSONObject();
			
			object.put("source", source);
			object.put("target", target);
			object.put("firstDate", firstDate);
			object.put("lastDate", lastDate);
			object.put("value", value);
			
			return object;
		}
		
		@SuppressWarnings("unchecked")
		public String toJSONString(){
			return getJSONObject().toString();
		}		
	}
}
