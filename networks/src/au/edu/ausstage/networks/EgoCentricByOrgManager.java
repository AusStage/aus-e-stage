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
import org.w3c.dom.Document;
import org.w3c.dom.Element;

// general java
import java.util.*;

// import AusStage related packages
import au.edu.ausstage.vocabularies.*;
import au.edu.ausstage.utils.*;
import au.edu.ausstage.networks.types.*;


/**
 * A class to manage the export of information
 */
public class EgoCentricByOrgManager {

	// declare private class level variables
	private DataManager rdf = null;
	private DatabaseManager db = null;
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
		
	/**
	 * Constructor for this class
	 *
	 * @param rdf an already instantiated instance of the DataManager class
	 */
	public EgoCentricByOrgManager(DataManager rdf, DatabaseManager db) {	
		// store a reference to this DataManager for later
		this.rdf = rdf;
		this.db = db;
	} // end constructor
	
	

	/**
	 * A method to build a collection of collaborator objects representing a network based on an organisation

	 * @param id the id of the central organisation
	 *
	 * @param radius the number of edges required from the central contributor
	 *
	 * @return        the collection of collaborator objects
	 */
	@SuppressWarnings("rawtypes")
	public TreeMap<Integer, Collaborator> getRawCollaboratorData_org(String id, int radius) {
	
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
		String		  contributor_id  = null;
		QuerySolution row             = null;
		Collaborator  collaborator    = null;	
		
		String sql = "SELECT DISTINCT b.eventid   "
					+"FROM events a, orgevlink b "
					+"WHERE b.organisationid = ? "  //+id
					+"AND a.eventid = b.eventid";

		int[] param = { Integer.parseInt(id) };
		java.sql.ResultSet resultSet = db.exePreparedStatement(sql, param);	

		ArrayList<String> eventResults = new ArrayList<String>();	
		
		try {
//		 	check to see that data was returned
			if (!resultSet.last()){	
				db.tidyup();
				return null;
			}else
				resultSet.beforeFirst();
			
			// loop through the resultset
			while(resultSet.next() == true) {
				eventResults.add(resultSet.getString(1));
			}
		
		} catch (java.sql.SQLException ex) {
			System.out.println("Exception: " + ex.getMessage());
			resultSet = null;
		}

		db.tidyup();
		
		//helper
		int first = 0;
		
		//define the query
		String sparqlQuery1 = "PREFIX foaf:       <"+FOAF.NS+"> "
							+"PREFIX ausestage:  <"+AuseStage.NS+"> "
							+"PREFIX event:      <http://purl.org/NET/c4dm/event.owl#> "
							+"PREFIX dcterms:    <http://purl.org/dc/terms/> "
							+"SELECT DISTINCT ?agent ?givenName ?familyName "
							+"WHERE { ";							
		for (String event :eventResults){	
			if (first>0){
				sparqlQuery1 += "UNION ";	
			}	
			first++;
			
			sparqlQuery1 +="{<ausstage:e:"+event+"> a                event:Event; "
	                 +"   event:agent      ?agent. "
					 +"	?agent             a                foaf:Person; "
        	         +"   foaf:givenName   ?givenName; "
            	     +"   foaf:familyName  ?familyName. "
					 +" } ";
		}
		sparqlQuery1 +=" } ";
		
		//execute query
		ResultSet results = rdf.executeSparqlQuery(sparqlQuery1);
		
		//now we transfer the results to a TreeMap <Integer,
		while(results.hasNext()){		
		
			row = results.nextSolution();
			
			contributor_id = AusStageURI.getId(row.get("agent").toString());
			
			collaborator = new Collaborator(contributor_id);
			
			cId_cObj_map.put(Integer.parseInt(contributor_id), collaborator);
			foundCollaboratorsSet.add(Integer.parseInt(contributor_id));
		
		}	
		
		rdf.tidyUp();
		
		return cId_cObj_map;
	
	} // end getRawCollaboratorData_org method
	
	/*
	 *get collaboration information based on organisation - returns a collaborationList.
	 */
	public HashMap<Integer, CollaborationList> getCollaborations_org(String id, TreeMap<Integer, Collaborator> network){
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
			collaborationList = getCollaborationList_org(id, networkKey.toString());
			
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
	public CollaborationList getCollaborationList_org(String org_id, String id) {
	
		// check on the input parameters
		if(InputUtils.isValidInt(id) == false) {
			throw new IllegalArgumentException("The id parameter cannot be null");
		}
		
		// declare helper variables
		CollaborationList list = new CollaborationList(id);
		
		// define other helper variables
		Collaboration collaboration = null;
		
		// define other helper variables
		String  partner   = null;
		Integer count     = null;
		String  firstDate = null;
		String  lastDate  = null;
	
		// define the base sql query
		String sqlQuery = "SELECT distinct count(*), con.contributorid, min(e.first_date), max(e.first_date) "
						+ "FROM contributor con, conevlink c , conevlink c2, orgevlink o, events e "
						+ "WHERE o.organisationid = ? " //+ org_id + " "
						+ "AND c.eventid = O.EVENTID "
						+ "AND e.eventid = O.EVENTID "						
						+ "AND e.eventid = c.EVENTID "						
						+ "AND c.contributorid != ?" //+ id + " "
						+ "AND con.contributorid = c.contributorid "
						+ "AND c2.contributorid = ? " //+ id + " "
						+ "AND c2.eventid = c.eventid "
						+ "GROUP BY con.contributorid, con.first_name ";
		
		int[] param = { Integer.parseInt(org_id), Integer.parseInt(id), Integer.parseInt(id) };
		// execute the query
		java.sql.ResultSet resultSet = db.exePreparedStatement(sqlQuery, param);	

		try {	
//		 	check to see that data was returned
			if (!resultSet.last()){	
				db.tidyup();
				return null;
			}else
				resultSet.beforeFirst();
			
			// loop though the resulset
			while (resultSet.next()) {
				// get the data
				partner   = resultSet.getString(2);
				count     = resultSet.getInt(1);
				firstDate = resultSet.getDate(3).toString();
				lastDate  = resultSet.getDate(4).toString();
				
				// create the collaboration object
				collaboration = new Collaboration(id, partner, count, firstDate, lastDate);
			
				// add the collaboration to the list
				list.addCollaboration(collaboration); 
			}
		
		} catch (java.sql.SQLException ex) {
			System.out.println("Exception: " + ex.getMessage());
			resultSet = null;
		}
		
		db.tidyup();
		// return the list of collaborations
		return list;		
	
	} // end the CollaborationList method
	

	
	
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
	

	@SuppressWarnings("unchecked")
	public Document toGraphMLDOM_org(Document egoDom, String id, int radius, String graphType){
		
		Collaborator collaborator = null;
				
		if(db.connect() == false) {
			throw new RuntimeException("Unable to connect to the database");
		}

		// add the graph element
		Element rootElement = egoDom.getDocumentElement();
		rootElement = createHeaderElements(egoDom, rootElement, graphType);

		Element graph = egoDom.createElement("graph");
		graph.setAttribute("id", "Contributor Network for organisation:" + " ( " + id + " )");
		graph.setAttribute("edgedefault", graphType);
		rootElement.appendChild(graph);
		
		network = getRawCollaboratorData_org(id, radius);
		
		if (network != null) {
			//add some additional required information for contributors
			collaborators = getCollaborators(network, "0");
		
			//get collaboration List			
			collaborations = getCollaborations_org(id, network);
		}else
			return egoDom; 
			
		//create node element in DOM		
		for (int i = 0; i < collaborators.size(); i++){
			Element node = createNodeElement(egoDom,i);			
			graph.appendChild(node);
		}
		
		
		//we have a list of collaborations.
		//loop through the list, for each - create a collaboration
		//								  - create edge.

		//1.iterate through the collaboration hashmap
		Iterator contributorIter = collaborations.values().iterator();
		int edgeIndex = 0;
		//collabCheck used to ensure no doubling up of collaborations. (pretty quick hack, ideally this issue would be sorted before this point)
		Vector collabCheck = new Vector();
		
		while(contributorIter.hasNext()){
			CollaborationList list = (CollaborationList)contributorIter.next();	
			//get the actual collaborations
			if (list != null) {
				
				Iterator collaborationIter = list.getCollaborations().values().iterator();
				//	loop through the hashmap of collaborations
				while(collaborationIter.hasNext()){
				
					Collaboration collaboration = (Collaboration)collaborationIter.next();
				
					if (!collabCheck.contains(collaboration.getPartner()+"-"+collaboration.getCollaborator())){
						//create an edge for each of them
						Element edge = createEdgeElement(egoDom, collaboration, Integer.parseInt(collaboration.getCollaborator())
													, Integer.parseInt(collaboration.getPartner()), edgeIndex);	
						graph.appendChild(edge);
						//	add the collaboration to the list for checking.
						collabCheck.add(collaboration.getCollaborator()+"-"+collaboration.getPartner());
						edgeIndex++;
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
	
}
