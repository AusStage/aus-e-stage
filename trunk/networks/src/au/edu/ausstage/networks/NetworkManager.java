package au.edu.ausstage.networks;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import au.edu.ausstage.networks.types.Collaborator;
import au.edu.ausstage.networks.types.Event;
import au.edu.ausstage.networks.types.Network;
import au.edu.ausstage.utils.DateUtils;
import au.edu.ausstage.vocabularies.AusStageURI;


public class NetworkManager {
	public DatabaseManager db = null;
	public Network network = null;
	
	public int degree [][];
	
	//node attribute for graphml export
	public String [][] nodeAttr = {
			{"EventID", "string"}, {"NodeLabel", "string"}, {"EventName", "string"}, {"Venue", "string"},
			{"StartDate", "string"}, {"InDegree", "string"}, {"OutDegree", "string"},
			{"EventURL", "string"}
		};
	//edge attribute for graphml export
	public String [][] edgeAttr = {
			{"ContributorID", "string"}, {"EdgeLabel", "string"}, {"ContributorName", "string"}, {"Roles", "string"},
			{"SourceEventID", "string"}, {"TargetEventID", "string"}, {"ContributorURL", "string"}
	};
	
	public NetworkManager(DatabaseManager db, Network network){
		this.db = db;
		this.network = network;
		
		if(this.db.connect() == false) {
			throw new RuntimeException("Error: Unable to connect to the database");
		}
	}
	
	
	//given an event ID, get its associated contributors set
	public Set<Integer> getAssociatedContributors(int eventId){
	
		Set<Integer> conSet = new HashSet<Integer>();	
		
		if (network.evtId_conSet_map.containsKey(eventId)) 
			conSet = network.evtId_conSet_map.get(eventId);
			
		else{ 
		
			String sql = "SELECT DISTINCT contributorid "
					+ "FROM conevlink "
					+ "WHERE eventid = ? " 	//+ eventId 					
					+ " ORDER BY contributorid";

			conSet = db.getResultfromDB(sql, eventId);
			if (conSet != null)
				network.contributorSet.addAll(conSet);
		
			network.evtId_conSet_map.put(eventId, conSet);
		}
		
		return conSet;
	}
	
	/**
	 * A method to get the details for an event
	 *
	 * @param id the event id
	 *
	 * @return a completed event object
	 */
	public Event getEventDetail(int evtId) {
		
		Event evt = null;
		
		String sql = "SELECT DISTINCT e.eventid, e.event_name, e.first_date, v.venue_name, v.suburb, s.state, c.countryname  "
					+ "FROM events e, venue v, states s, country c "
					+ "WHERE e.eventid = ? " 	//+ evtId
					+ "AND e.venueid = v.venueid "
					+ "AND v.state = s.stateid (+) "
					+ "AND v.countryid = c.countryid (+) "
					+ "ORDER BY e.eventid";

		int[] param = {evtId};
		
		//execute sql statement
		ResultSet results = db.exePreparedStatement(sql, param);
		
		try {
			// 	check to see that data was returned
			if (!results.last()){	
				db.tidyup();
				return null;
			}else
				results.beforeFirst();
		
			//build the event object
			while (results.next() == true) {
				int e_id = results.getInt(1);
				String name = results.getString(2);
				String date = results.getDate(3).toString();
				String venue = results.getString(4);
				String suburb = results.getString(5);
				String state = results.getString(6);
				String country = results.getString(7);
				String venueDetail = venue;
				
				evt = new Event(Integer.toString(e_id));
				// evt.setId(resultSet.getString(1));
				evt.setMyName(name);
				evt.setMyFirstDate(date);				
				if (suburb != null && !suburb.isEmpty())
					venueDetail = venueDetail + ", " + suburb;
				if (country == null && state != null) 
					venueDetail = venueDetail + ", " + state;
				if (country != null && state != null && !state.isEmpty() && country.equalsIgnoreCase("Australia"))
					venueDetail = venueDetail + ", " + state;
				if (country != null && !country.isEmpty() && !country.equalsIgnoreCase("Australia"))
					venueDetail = venueDetail + ", " + country;
					
				evt.setVenue(venueDetail);
							
			}
			network.evtId_evtObj_map.put(evtId, evt);
			
		} catch (SQLException e) {
			results = null;
			e.printStackTrace();
		}
		
		db.tidyup();
		return evt;	
	}
	
	/**
	 * A method to get the details for an event set
	 *
	 * @param set  the event id set
	 *
	 * @return a list of event objects
	 */
	public List<Event> getEventDetail(Set<Integer> set) {
		
		int[] evtIDArray = new int[set.size()];
		int x = 0;
		for (Integer eID : set) evtIDArray[x++] = eID;

		int SINGLE_BATCH = 1;
		int SMALL_BATCH = 4;
		int MEDIUM_BATCH = 11;
		int LARGE_BATCH = 51;
		int start = 0;
		int totalNumberOfValuesLeftToBatch = set.size();
		Event evt = null;
		List<Event> evtList = new ArrayList<Event>();;
		int j = 0;
		
		while ( totalNumberOfValuesLeftToBatch > 0 ) {
			
			int batchSize = SINGLE_BATCH;
			if ( totalNumberOfValuesLeftToBatch >= LARGE_BATCH ) {
			  batchSize = LARGE_BATCH;
			} else if ( totalNumberOfValuesLeftToBatch >= MEDIUM_BATCH ) {
			  batchSize = MEDIUM_BATCH;
			} else if ( totalNumberOfValuesLeftToBatch >= SMALL_BATCH ) {
			  batchSize = SMALL_BATCH;
			}
			
			String inClause = new String("");			
			for (int i=0; i < batchSize; i++) {
				inClause = inClause + "? ,";
			}
			inClause = inClause.substring(0, inClause.length()-1);
			
			String sql = "SELECT DISTINCT e.eventid, e.event_name, e.first_date, v.venue_name, v.suburb, s.state, c.countryname "
				+ "FROM events e, venue v, states s, country c "
				+ "WHERE e.eventid in (" + inClause + ") "
				+ "AND e.venueid = v.venueid "
				+ "AND v.state = s.stateid (+) "
				+ "AND v.countryid = c.countryid (+) "	
				+ "ORDER BY e.first_date";
	
			ResultSet results = db.exePreparedINStatement(sql, evtIDArray, start, batchSize);
			
			try{
				if (!results.last()) {
					db.tidyup();
					return null;
				} else
				results.beforeFirst();
						
				//build the event object
				while (results.next() == true) {
					int e_id = results.getInt(1);
					String name = results.getString(2);
					String date = results.getDate(3).toString();
					String venue = results.getString(4);
					String suburb = results.getString(5);
					String state = results.getString(6);
					String country = results.getString(7);
					String venueDetail = venue;
					
					evt = new Event(Integer.toString(e_id));
					// evt.setId(resultSet.getString(1));
					evt.setMyName(name);
					evt.setMyFirstDate(date);	
					if (suburb != null && !suburb.isEmpty())
						venueDetail = venueDetail + ", " + suburb;
					if (country == null && state != null) 
						venueDetail = venueDetail + ", " + state;
					if (country != null && state != null && !state.isEmpty() && country.equalsIgnoreCase("Australia"))
						venueDetail = venueDetail + ", " + state;
					if (country != null && !country.isEmpty() && !country.equalsIgnoreCase("Australia"))
						venueDetail = venueDetail + ", " + country;
					
					evt.setVenue(venueDetail);
					evtList.add(evt);
					network.evtId_evtObj_map.put(e_id, evt);
					j++;
				}
				
				db.tidyup();
			} catch (SQLException e) {
				results = null;
				db.tidyup();
				e.printStackTrace();
			}
			totalNumberOfValuesLeftToBatch -= batchSize; 			
			start += batchSize;
		}
					
		return evtList;
		
	}
	
	//given a contributor ID and its source event, get contributor details
	//if this contributor not exist before, then create a new Collaborator instance
	//otherwise add roles to the eventRoleMap
	public Collaborator getContributorDetail(int evtId, int conId, boolean exist){
		
		Collaborator con = null;		
		String c_id = ""; // contributorid
		String f_name = ""; // first_name
		String l_name = ""; // last_name
		String roles = ""; // preferredterm. A contributor may have multiple roles at an event
		
		String sql = "SELECT DISTINCT c.contributorid, c.first_name, c.last_name, cfp.preferredterm "
			+ "FROM conevlink ce, contributor c, contributorfunctpreferred cfp "
			+ "WHERE c.contributorid = ? " //+ conId
			+ " AND ce.eventid = ? " //+ evtId 
			+ " AND ce.contributorid = c.contributorid "	
			+ "AND ce.function = cfp.contributorfunctpreferredid (+)";
			
		int[] param = {conId, evtId};
		//execute sql statement
		ResultSet results = db.exePreparedStatement(sql, param);		
				
		// check to see that data was returned
		try {
			if(!results.last()) { 			
				db.tidyup();				
				return null;				
			}else 
				results.beforeFirst();          
			
			while (results.next()) {
				c_id = Integer.toString(results.getInt(1));
				f_name = results.getString(2);
				l_name = results.getString(3);
				String role = results.getString(4);
				if (role != null)	// contributor's function in conevlink table could be null	
					if (roles.equals(""))
						roles = results.getString(4);
					else
						roles = roles + " | " + results.getString(4);					
			}
						
			if (!exist){
				// create a new contributor
				con = new Collaborator(c_id);
				con.setGName(f_name);
				con.setFName(l_name);
				con.setEvtRoleMap(evtId, roles);
				network.conId_conObj_map.put(conId, con);
			}else {
				//add roles to the eventRoleMap
				con = network.conId_conObj_map.get(Integer.parseInt(c_id));
				if (con != null )
					con.setEvtRoleMap(evtId, roles);
			}
			
		} catch (java.sql.SQLException ex) {
			//System.out.println(ex);
			results = null;			
		}
		
		db.tidyup();
		return con;
	}
	
	

	
	//calulate the inDegree (degree[][0]) and outDegree (degree[][1])
	public int[][] getNodeDegree() {
		
		int numberOfNodes = network.nodeSet.size();
		degree  = new int[numberOfNodes][2];
		
		//calculate inDegree[i][0]
		for(int i = 0; i < numberOfNodes; i++){
			if (i == 0)
				degree[i][0] = 0; //the first node has 0 inDegree
			else {
				int in = 0;
				for (int j = 0; j < i; j++)
					if (network.edgeMatrix[j][i] != null)
						in = in + network.edgeMatrix[j][i].size();
				degree[i][0] = in;
			}
		}
		
		//calculate outDegree[i][1]
		for(int i = 0; i < numberOfNodes; i++){
			if (i == (numberOfNodes -1))
				degree[i][1] = 0; //the last node has 0 outDegree
			else {
				int out = 0;
				for(int j = i; j < numberOfNodes; j++)
					if (network.edgeMatrix[i][j] != null)
						out = out + network.edgeMatrix[i][j].size();
				degree[i][1] = out;
			}
			
		}		
		
		return degree;
	}

	
	/*
	 * @param graphType:  directed/undirected
	 * 
	 */
	public Document toGraphMLDOM(Document evtDom){
		
		if (network.nodeSet != null ){			
				
			int numOfNodes = network.nodeSet.size();
			int [][] degree = new int [numOfNodes][2];
			degree = getNodeDegree();
		} else 
			return null;
		
		// get the root element
		Element rootElement = evtDom.getDocumentElement();
		rootElement = createHeaderElements(evtDom, rootElement);
				
		// add the graph element
		Element graph = evtDom.createElement("graph");
		
		String type = "";
		if (network.getType().equalsIgnoreCase("o"))
			type = "organisation";
		else if (network.getType().equalsIgnoreCase("v"))
			type = "venue";
		
		graph.setAttribute("id", type + network.getId());
		graph.setAttribute("edgedefault", network.getGraphType());
		rootElement.appendChild(graph);
		
		//create node element in DOM	
		for (int i = 0; i < network.sortedNodeList.size(); i++){
			Event evt = network.sortedNodeList.get(i);
			if (evt != null) {
				Element node = createNodeElement(evtDom,i, evt);			
				graph.appendChild(node);
			}
		}
		
		//create edge element in DOM
		int eID;
		int cID;
		int edgeIndex = 0;
		Collaborator con = null;
		Set<Integer> conSet = null;
		
		for (int i = 0; i < network.edgeMatrix.length; i++){
			eID = network.sortedNodeList.get(i).getIntId();
			
			for (int j = i + 1; j < network.edgeMatrix[i].length; j++){
				conSet = network.edgeMatrix[i][j];
				if (conSet != null && !conSet.isEmpty()) {

					for (Iterator it = conSet.iterator(); it.hasNext();) {
						cID = (Integer) it.next();					
						if (!network.conId_conObj_map.containsKey(cID))	{											
							con = getContributorDetail(eID, cID, false);														
						} else {								
							con = getContributorDetail(eID, cID, true);	
						}
						if (con != null){
							edgeIndex ++;
							Element edge = createEdgeElement(evtDom, con, i, j, edgeIndex);			
							graph.appendChild(edge);
						}															
							
					}					
				} 
			}
		}
		
		return evtDom;
		
	}
	
	public Element createHeaderElements (Document evtDom, Element rootElement){
		Element key;
			
		// add schema namespace to the root element
		rootElement.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns:xsi", "http://www.w3.org/2001/XMLSchema-instance");
		
		// add reference to the kml schema
		rootElement.setAttribute("xsi:schemaLocation", "http://graphml.graphdrawing.org/xmlns http://graphml.graphdrawing.org/xmlns/1.0/graphml.xsd");
		
		// add some useful comments to the file
		rootElement.appendChild(evtDom.createComment("Graph generated on: " + DateUtils.getCurrentDateAndTime()));
		
		String type = "";
		if (network.getType().equalsIgnoreCase("o"))
			type = "organisation";
		else if (network.getType().equalsIgnoreCase("v"))
			type = "venue";
		
		rootElement.appendChild(evtDom.createComment("Event Network for " + type + ": " + network.getName() + " ( " + network.getId() + " )" ));
		
		//create key element for node
		for(int row = 0; row < nodeAttr.length; row ++){
			key = createKeyElement(evtDom, "node", nodeAttr[row][0],  nodeAttr[row][1]);
			rootElement.appendChild(key);			
		}
				
		//create key element for edge
		for(int row = 0; row < edgeAttr.length; row ++){
			key = createKeyElement(evtDom, "edge", edgeAttr[row][0], edgeAttr[row][1]);
			rootElement.appendChild(key);
		}
				
		return rootElement;
	}
	
	public Element createKeyElement(Document evtDom, String nodeOrEdge, String name, String type){
		Element key;
		
		key = evtDom.createElement("key");
		key.setAttribute("id", name);
		key.setAttribute("for", nodeOrEdge);
		key.setAttribute("attr.name", name);
		key.setAttribute("attr.type", type);
		
		return key;
	}
	
	public Element createNodeElement(Document evtDom, int i, Event evt){
		Element data;
		
		Element node = evtDom.createElement("node");
		node.setAttribute("id", evt.getId());
		
		for(int row = 0; row < nodeAttr.length; row ++){
			data = evtDom.createElement("data");
			data.setAttribute("key", nodeAttr[row][0]);
			
			if (nodeAttr[row][0].equalsIgnoreCase("EventID"))
				data.setTextContent(evt.getId()); 
			else if	(nodeAttr[row][0].equalsIgnoreCase("NodeLabel"))
				data.setTextContent(evt.getName());
			else if (nodeAttr[row][0].equalsIgnoreCase("EventName"))
				data.setTextContent(evt.getName());
			else if (nodeAttr[row][0].equalsIgnoreCase("Venue"))
				data.setTextContent(evt.getVenue());
			else if (nodeAttr[row][0].equalsIgnoreCase("StartDate"))
				data.setTextContent(evt.getFirstDate());
			else if (nodeAttr[row][0].equalsIgnoreCase("InDegree"))
				data.setTextContent(Integer.toString(degree[i][0]));
			else if (nodeAttr[row][0].equalsIgnoreCase("OutDegree"))
				data.setTextContent(Integer.toString(degree[i][1]));
			else if (nodeAttr[row][0].equalsIgnoreCase("EventURL"))
				data.setTextContent(AusStageURI.getEventURL(evt.getId()));
				//data.setTextContent(evtURLprefix + evt.getId());
			
			node.appendChild(data);			
		}
				
		return node;		
	}
	
	public Element createEdgeElement(Document evtDom, Collaborator con, int src, int tar, int index){
		
		Event srcEvt = network.sortedNodeList.get(src);
		Event tarEvt = network.sortedNodeList.get(tar);
		
		Element edge = evtDom.createElement("edge");
		edge.setAttribute("id", "e" + Integer.toString(index));
		edge.setAttribute("source", srcEvt.getId());
		edge.setAttribute("target", tarEvt.getId());
		
		for(int row = 0; row < edgeAttr.length; row ++){
			Element data = evtDom.createElement("data");
			data.setAttribute("key", edgeAttr[row][0]);
			
			if (edgeAttr[row][0].equalsIgnoreCase("ContributorID"))
				data.setTextContent(con.getId()); 
			else if	(edgeAttr[row][0].equalsIgnoreCase("EdgeLabel"))
				data.setTextContent(con.getGFName());
			else if (edgeAttr[row][0].equalsIgnoreCase("ContributorName"))
				data.setTextContent(con.getGFName());
			else if (edgeAttr[row][0].equalsIgnoreCase("Roles"))
				data.setTextContent(con.getEvtRoleMap(srcEvt.getIntId()));
			else if (edgeAttr[row][0].equalsIgnoreCase("SourceEventID"))
				data.setTextContent(srcEvt.getId());
			else if (edgeAttr[row][0].equalsIgnoreCase("TargetEventID")) 
				data.setTextContent(tarEvt.getId());
			else if (edgeAttr[row][0].equalsIgnoreCase("ContributorURL"))
				data.setTextContent(AusStageURI.getContributorURL(con.getId()));
				//data.setTextContent(conURLprefix + con.getId());
				
			edge.appendChild(data);
		}
		return edge;
	}
}
