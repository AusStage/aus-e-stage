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
public class ProtovisEgoCentricManager {

	// declare private class level variables
	private DataManager database = null;

	/**
	 * Constructor for this class
	 */
	public ProtovisEgoCentricManager(DataManager database) {	
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
		
		// get an instance of the ExportManager class
		ExportManager export = new ExportManager(database);
		
		// get the network data for this collaborator
		java.util.TreeMap<Integer, Collaborator> network = export.getRawCollaboratorData(id, radius);
		
		/*
		 * add some of the additional required information
		 */
		
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
				
				if(row.get("gender") != null) {
					collaborator.setGender(row.get("gender").toString());
				}
				
				if(row.get("nationality") != null) {
					collaborator.setNationality(row.get("nationality").toString());
				}

			}
			
			// play nice and tidy
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
		
		// add the edges
		while(networkValueIterator.hasNext()) {
		
			// get the next collaborator in the list
			collaborator = (Collaborator)networkValueIterator.next();
			source = Integer.parseInt(collaborator.getId());
			altSourceIndex = (Integer)altIndex.get(source);
			
			// get the list of collaborators
			edgesToMake = collaborator.getCollaboratorsAsArray();
			
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
		if(InputUtils.isValid(database.getContextParam("databaseConnectionString")) == false) {
			throw new RuntimeException("Unable to read the connection string parameter from the web.xml file");
		} else {
			return database.getContextParam("databaseConnectionString");
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
