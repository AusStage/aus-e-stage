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

//json
import org.json.simple.*;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

// general java
import java.util.*;
import java.io.PrintWriter;
import java.sql.ResultSet;
import java.sql.SQLException;

// import AusStage related packages
import au.edu.ausstage.utils.*;
import au.edu.ausstage.networks.types.*;

/**
 * A class to manage the export of information
 */
/**
 * @author ehlt_user
 *
 */
/**
 * @author ehlt_user
 *
 */
public class ProtovisEventCentricManager {
	
	public DatabaseManager   db    = null;
	public DataManager rdf = null;
	public Map<Integer, Set<Integer>> evtId_conSet_map = new HashMap<Integer, Set<Integer>>();
	public Map<Integer, List<Event>> conId_sortedEvtList_map = new HashMap<Integer, List<Event>>();
	public Map<Integer, Event> evtId_evtObj_map = new HashMap<Integer, Event>();
	public Map<Integer, Collaborator> conId_conObj_map = new HashMap<Integer, Collaborator>();
	public Set<Integer> nodeSet = new HashSet<Integer>();	//the nodes set for the whole graph
	public Set<Integer> contributorSet = new HashSet<Integer>();
	public List<Event> sortedNodeList = new ArrayList<Event>();
	public Set<Integer>[][] edgeMatrix;
	public int [][] degree;
	public int central_eventId;	
	public boolean debug = false;
	public boolean viaRDF = false;
	
	//node attribute for graphml export
	public String [][] nodeAttr = {
			{"EventID", "string"}, {"Label", "string"}, {"EventName", "string"}, {"Venue", "string"},
			{"StartDate", "string"}, {"Central", "boolean"}, {"InDegree", "string"}, {"OutDegree", "string"},
			{"EventURL", "string"}
		};
	//edge attribute for graphml export
	public String [][] edgeAttr = {
			{"ContributorID", "string"}, {"Label", "string"}, {"ContributorName", "string"}, {"Roles", "string"},
			{"SourceEventID", "string"}, {"TargetEventID", "string"}, {"ContributorURL", "string"}
	};
	
	public String evtURLprefix =  "http://www.ausstage.edu.au/indexdrilldown.jsp?xcid=59&f_event_id=";
	public String conURLprefix = "http://www.ausstage.edu.au/indexdrilldown.jsp?xcid=59&f_contrib_id=";
		
	//first_date comparator used to sort Event nodes
	public EvtComparator evtComp = new EvtComparator();
	
	public ProtovisEventCentricManager(DatabaseManager db) {	
		// store a reference to this DataManager for later
		this.db = db;
		
		if(db.connect() == false) {
			throw new RuntimeException("Error: Unable to connect to the database");
		}
	}
	
	public ProtovisEventCentricManager(DataManager rdf) {
		this.rdf = rdf;
		this.viaRDF = true;
	}
	
	/**
	 * A method to build the JSON object required by the Protovis visualisation library
	 *
	 * @param eventId   the unique identifier of an event
	 * @param radius the number of edges from the central node
	 *
	 * @return the JSON encoded data for use with Protovis
	 */
	@SuppressWarnings("unchecked")
	public String getData(String eventId, int radius, boolean simplify){
		
		Set<Integer> vertexSet = new HashSet<Integer>();  //the nth degree nodes set
		String event_network = null;
		
		//add the central event node 
		vertexSet = setupCentralNode(eventId);
		
		//get the vertex(Event nodes) of graph
		getGraphVertex(radius, vertexSet, simplify);
					
		if (nodeSet != null ){
			
			sortedNodeList = getSortedNodeList();
			
			// get the no-duplicated edges (contributors) of graph
			int numOfNodes = sortedNodeList.size();
			edgeMatrix = new HashSet[numOfNodes][numOfNodes];
			edgeMatrix = getEdges(sortedNodeList);

			if (debug) {
				System.out.println("+++ print debug info: ++++");
				printDebugInfo();
			}
			
			// export to JSON string
			event_network = toJSON(sortedNodeList, edgeMatrix);

			return event_network;
		}else
			return "";
		
	} // end the getData method
	
	
	//add the central event node
	public Set<Integer> setupCentralNode(String eventId){
		Event evt = null;
		Set<Integer> conSet = new HashSet<Integer>();	
		Set<Integer> vertexSet = new HashSet<Integer>();  //the nth degree nodes set
		
		central_eventId = Integer.parseInt(eventId.trim());
		nodeSet.add(central_eventId);
		vertexSet.add(central_eventId);

		conSet = getAssociatedContributors(central_eventId);
		if (conSet != null)
			contributorSet.addAll(conSet);
		evtId_conSet_map.put(central_eventId, conSet);
		evt = getEventDetail(central_eventId);
		evtId_evtObj_map.put(central_eventId, evt);
		
		return vertexSet;
	}
	
	//get the vertex(Event nodes) of graph
	public void getGraphVertex(int radius, Set<Integer> vertexSet, boolean simplify){
		int eID = 0;
		Event evt = null;	
		Set<Integer> tmpSet = new HashSet<Integer>();
		Set<Integer> preNthDegreeNodeSet = new HashSet<Integer>();
		
		for (int i = 0; i < radius; i++) {
			Set<Integer> nthDegreeNodeSet = new HashSet<Integer>(); // the nth degree nodes set

			for (Iterator it = vertexSet.iterator(); it.hasNext();) {
				eID = (Integer) it.next();

				evt = evtId_evtObj_map.get(eID);
				if (evt != null) {
					if (i >= 1) // 2nd degree
						tmpSet = getFirstDegreeNodes(evt, simplify);
					else
						// 1st degree
						tmpSet = getFirstDegreeNodes(evt, false);

					// get union set of event nodes
					if (tmpSet != null) {
						nodeSet.addAll(tmpSet);
						nthDegreeNodeSet.addAll(tmpSet);
					}
				}
			}
			preNthDegreeNodeSet.addAll(vertexSet);
			nthDegreeNodeSet.removeAll(preNthDegreeNodeSet);
			vertexSet = nthDegreeNodeSet;
			/*
			difference = new HashSet<Integer>(nodeSet);
			difference.removeAll(vertexSet); 
			vertexSet = difference;*/
			 
		}
	}
	
	//get sorted events list according the first date
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public ArrayList<Event> getSortedNodeList(){
		int eID = 0;
		ArrayList<Event> sortedNodeList = new ArrayList<Event>();
		
		// get Events(Nodes) List from evtId_evtObj_map
		for (Iterator it = nodeSet.iterator(); it.hasNext();) {
			eID = (Integer) it.next();
			if (evtId_evtObj_map.get(eID) != null)
				sortedNodeList.add(evtId_evtObj_map.get(eID));
		}

		// Sorting Event List on the basis of Event first Date by passing Comparator
		Collections.sort(sortedNodeList, evtComp);
		
		return sortedNodeList;
	}
	
	//given an event, get its 1st degree nodes 
	public Set<Integer> getFirstDegreeNodes(Event evt, boolean simplify){
		
		Set<Integer> union = null;
		Set<Integer> conSet = new HashSet<Integer>();	
		Set<Integer> evtSet = new HashSet<Integer>();	
		Set<Integer> priorAfterEvtSet = new HashSet<Integer>();
		List<Event> sortedEvtList;
		
		int cID = 0;
		Set<Integer> events = new HashSet<Integer>();
		
		int eID = evt.getIntId();
		String date = evt.getFirstDate();
		Set<Integer> centralNodeConSet = evtId_conSet_map.get(central_eventId);		

		//get the associated contributors for the event
		if(!evtId_conSet_map.containsKey(eID)){
			conSet = getAssociatedContributors(eID);
			if (conSet != null)
				contributorSet.addAll(conSet);
			evtId_conSet_map.put(eID, conSet);				
		}else {
			conSet = evtId_conSet_map.get(eID);
		}		
	
		if (conSet != null){
			Set<Integer> con_intersection = new HashSet<Integer>(conSet);
		
			if (simplify )			
				con_intersection.retainAll(centralNodeConSet);		
		
			// for each contributor in the conSet, get its associated prior-date and after-date events		
			for (Iterator conIterator = con_intersection.iterator(); conIterator.hasNext();) {
				cID = (Integer) conIterator.next();

				if (!conId_sortedEvtList_map.containsKey(cID)) {
					
					sortedEvtList  = getAssociatedEvents(cID);
					if (sortedEvtList != null){			
						// get the prior and after date events
						priorAfterEvtSet = getPriorAfterEvt(sortedEvtList, evt);											
					}
					conId_sortedEvtList_map.put(cID, sortedEvtList);									
					
				} else {
					sortedEvtList = conId_sortedEvtList_map.get(cID);
					if (sortedEvtList != null)
						// get the prior and after date events
						priorAfterEvtSet = getPriorAfterEvt(sortedEvtList, evt);
				}
				//get the UNION of events Set	
				if (priorAfterEvtSet != null){
					if (union == null){
						union = new HashSet<Integer>(priorAfterEvtSet);
					}else {
						union.addAll(priorAfterEvtSet);
					}
				}
			}
			
			if (union != null)
				//	for each event in the nodeSet, get its associated contributors
				for (Iterator it = union.iterator(); it.hasNext(); ) {
					eID = (Integer) it.next();
					if(!evtId_conSet_map.containsKey(eID)){
						conSet = getAssociatedContributors(eID);
						if (conSet != null)
							contributorSet.addAll(conSet);
						evtId_conSet_map.put(eID, conSet);				
					}		
				}
		}
				
		return union;
	}
	
	//generate the edges of graph
	@SuppressWarnings("unchecked")
	public Set<Integer>[][] getEdges(List<Event> sortedNodeList){		
		
		int numOfNodes = sortedNodeList.size();		
		Set<Integer> src_con = new HashSet<Integer>();
		Set<Integer> tar_con = new HashSet<Integer>();
		
		Event src_evt = null;
		Event tar_evt = null;
		
		for (int i = 0; i < numOfNodes; i++){
			src_evt =  sortedNodeList.get(i);
			src_con = evtId_conSet_map.get(src_evt.getIntId());			
			if (src_con == null)
				System.out.println("====NULL: contributors associated with src_evt_id: " + src_evt.getId());
			else if(src_con.isEmpty())
				System.out.println("====Empty: contributors associated with src_evt_id: " + src_evt.getId());
			else {
				for (int j = i + 1; j < numOfNodes; j++){
					//since the nodeSet is already sorted according to the start_time (chronological order)
					//and the earlier event should flow to the later event.
					//so there will be no edges from later event to earlier event.
					tar_evt = sortedNodeList.get(j);
					tar_con = evtId_conSet_map.get(tar_evt.getIntId());
					if (tar_con == null)
						System.out.println("====NULL: contributors associated with tar_evt_id: " + tar_evt.getId());
					else if(tar_con.isEmpty())
						System.out.println("====Empty: contributors associated with tar_evt_id: " + tar_evt.getId());
					else {
						Set<Integer> intersection = new HashSet<Integer>(tar_con);
						intersection.retainAll(src_con);
						edgeMatrix[i][j] = intersection;
					}
				}
			}
		}
		
		if (debug) {
			System.out.println("********print duplicated edgeMatrix:*********");
			printEdgeMatrix(edgeMatrix);
		}

		//delete duplicated edges using depth first search (DFS)
		for (int cID: contributorSet){
			boolean[] isVisited = new boolean[numOfNodes];
			//cycle from start to current node
			ArrayList<Integer> trace = new ArrayList<Integer>();
			
			//System.out.println("Detect Cycle for contributor: " + cID);
			for (int i = 0; i < numOfNodes; i++)
				 findCycle(cID, i, isVisited, trace); //started from the second earliest event   
		}		
		
		if (debug) {
			System.out.println("********print non-duplicated edgeMatrix:*********");
			printEdgeMatrix(edgeMatrix);
		}
		return edgeMatrix;
	}
	
	//use depth first degree algorithm to detect cycle (redundant edge) in the graph
	//and delete it from edgeMatrix 
	public void findCycle(int cID, int nodeIndex, boolean[] isVisited, ArrayList<Integer> trace){
		
	/*	if (debug){
			System.out.println("cID: " + cID);
			System.out.println("nodeIndex: " + nodeIndex);
			System.out.print("isVisited: ");
			for (int i = 0; i < isVisited.length; i++)
				System.out.print(isVisited[i] + " ");
			System.out.println();
			System.out.println("trace: " + trace.toString());
			System.out.println("++++++++++++++++++++++");
		}*/
		
		if (isVisited[nodeIndex]){
			int j;
            if((j = trace.indexOf(nodeIndex))!= -1) {            
                if (debug){
					System.out.print("Cycle:");
					while (j < trace.size()) {
						System.out.print(trace.get(j) + " ");
						j++;
					}
					System.out.print("\n");
                }
                return;
            }
            return;						
		}
		
		isVisited[nodeIndex] = true;	  
	        
	    for(int i = nodeIndex + 1; i < sortedNodeList.size(); i++) {
	    	if (edgeMatrix[nodeIndex][i] != null && edgeMatrix[nodeIndex][i].contains(cID)){
	    		
	    		//contains both nodes -- a cycle detected, delete the edge from the edge set
	    		if (trace.contains(nodeIndex) && trace.contains(i))
	    			edgeMatrix[nodeIndex][i].remove(cID);
	    		else {
	    			if (!trace.contains(nodeIndex))	    	
	    				trace.add(nodeIndex);
	    			if (!trace.contains(i))
	    				trace.add(i);
	    		}
	    		
	    		findCycle(cID, i, isVisited, trace);
	    	}
        }		
	}
	
	//given a graph(nodes, edges), return it as JSON string format
	@SuppressWarnings("unchecked")
	public String toJSON(List<Event> nodes, Set<Integer>[][] edges){
		// build the JSON object and arrays
		JSONArray  json_nodes  = new JSONArray();
		JSONArray  json_edges  = new JSONArray();
		JSONObject json_object = new JSONObject();
		Event evt = null;
		Collaborator con = null;
		Set<Integer> conSet = null;
		int eID;
		int cID;
		
		//add nodes in the list to JSONArray		
		for (int i = 0; i < nodes.size(); i++){
			evt = nodes.get(i);
			if (central_eventId != evt.getIntId())
				json_nodes.add(evt.toJSONObj(i, false));
			else 
				json_nodes.add(evt.toJSONObj(i, true));
		}
						
		//add edges to JSONArray
		for (int i = 0; i < edges.length; i++){
			//System.out.println("*** i = " + i + " ******************************");
			eID = nodes.get(i).getIntId();
			
			for (int j = i + 1; j < edges[i].length; j++){
				//System.out.println("--- j = " + j + " ----");
				conSet = edges[i][j];
				if (!conSet.isEmpty()) {

					for (Iterator it = conSet.iterator(); it.hasNext();) {
						cID = (Integer) it.next();

						if (!conId_conObj_map.containsKey(cID))	{											
							con = getContributorDetail(eID, cID, false);							
							conId_conObj_map.put(cID, con);
						} else {								
							con = getContributorDetail(eID, cID, true);	
						}
						//System.out.println("Source Event ID: " + eID + "   Contributor ID: " + cID);

						if (con != null)
							json_edges.add(con.toJSONObj(i, j, eID));
						else {
							System.out.println("contributor: " + cID + " is null!");
						}
					}					

				} else if (conSet == null) {
					//System.out.println("+++ NULL ++++");
				} else if (conSet.isEmpty()) {
					//System.out.println("+++ Empty ++++");
				}
			}
		}
		
		//add nodes and edges JSONArray to JSONObject
		json_object.put("nodes", json_nodes);
		json_object.put("edges", json_edges);
		
		return json_object.toString();		
	}
	
	/**
	 * A private method to get the details for an event
	 *
	 * @param id the event id
	 *
	 * @return a completed event object
	 */
	private Event getEventDetail(int evtId) {
		
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
				db.finalize();
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
				if (state != null && !state.isEmpty() && country.equalsIgnoreCase("Australia"))
					venueDetail = venueDetail + ", " + state;
				if (country != null && !country.isEmpty() && !country.equalsIgnoreCase("Australia"))
					venueDetail = venueDetail + ", " + country;
					
				evt.setVenue(venueDetail);
							
			}
			evtId_evtObj_map.put(evtId, evt);
			
		} catch (SQLException e) {
			results = null;
			e.printStackTrace();
		}
		
		db.finalize();
		return evt;	
	}
	
	//given a contributor ID and its source event, 
	//get contributor details  and return a Collaborator instance
	private Collaborator getContributorDetail(int evtId, int conId, boolean exist){
		
		Collaborator con = null;		
		String c_id = ""; // contributorid
		String function = ""; // function
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
				db.finalize();				
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
			}else {
				//add roles to the eventRoleMap
				con = conId_conObj_map.get(Integer.parseInt(c_id));
				if (con != null )
					con.setEvtRoleMap(evtId, roles);
			}
			
		} catch (java.sql.SQLException ex) {
			//System.out.println(ex);
			results = null;			
		}
		
		db.finalize();
		return con;
	}
	
	//given an event ID, get its associated contributors set
	private Set<Integer> getAssociatedContributors(int eventId){
		Set<Integer> conSet = new HashSet<Integer>();	
		
		String sql = "SELECT DISTINCT contributorid "
					+ "FROM conevlink "
					+ "WHERE eventid = ? " 	//+ eventId 					
					+ " ORDER BY contributorid";

		conSet = db.getResultfromDB(sql, eventId);
				
		return conSet;
	}
	
	//given an contributor ID, get its associated events set 
	@SuppressWarnings("unchecked")
	private List<Event>  getAssociatedEvents(int conId){
		
		Set<Integer> evtSet = new HashSet<Integer>();		
		int eID;
		Event evt;
		
		if (debug)
	  		System.out.println("===== sort the associated events for contributor: " + conId);
	  		
		String sql = "SELECT DISTINCT eventid "
			+ "FROM conevlink "
			+ "WHERE contributorid = ? " 	//+ conId 					
			+ " ORDER BY eventid";

		evtSet = db.getResultfromDB(sql, conId);		
		
		if (evtSet != null) {
		  	List<Event> sortedEvtList = db.selectBatchingEventDetails(evtSet);
		  	if (sortedEvtList == null || sortedEvtList.isEmpty())
		  		return null;
		  	for (int i = 0; i < sortedEvtList.size(); i++) {
		  		evtId_evtObj_map.put(sortedEvtList.get(i).getIntId(), sortedEvtList.get(i));
			}
		  	
		/*List<Event> sortedNodeList =  new ArrayList<Event>();
		for (Iterator it = evtSet.iterator(); it.hasNext(); ) {
			eID = (Integer)it.next();
			if (eID != 0) { //the eID could be 0 after changing null to integer
				evt = getEventDetail(eID);
				if (evt != null)
					sortedNodeList.add(evt);
				else 
					System.out.println("event ID: " + eID + " is null in conevlink table! and associated conID is: " + conId);
			}
		}*/
		
		  	//sort the events according to firstdate to select immediate-prior and after events.
		  	Collections.sort(sortedEvtList, evtComp);
		  	
		  	if (debug){
		  		printSortedNode(sortedEvtList);
		  	}
		
		  	return sortedEvtList;
		}else 
			return null;
	}
	
	private Set<Integer> getPriorAfterEvt(List<Event> sortedEvtList, Event event){
		Set<Integer> priorAfterSet = new HashSet<Integer>();
	
		int priorEvtID = 0;
		int afterEvtID = 0;
		
		for (int i = 0; i < sortedEvtList.size(); i ++){	
			if (sortedEvtList.get(i).getIntId()== event.getIntId()){
				if ((i - 1) >= 0) 
					priorEvtID = sortedEvtList.get(i - 1).getIntId();
				if ((i + 1) < sortedEvtList.size())
					afterEvtID = sortedEvtList.get(i + 1).getIntId();
			}
			
		}
		
		if (priorEvtID != 0 ) priorAfterSet.add(priorEvtID);
		if (afterEvtID != 0) priorAfterSet.add(afterEvtID);
		
		//System.out.println("Date: " + event.getFirstDate() + " --PriorEvtID: " + priorEvtID + "  PriorDate: " + priorDate + " --AfterEvtID: " + afterEvtID + "  AfterDate: "+ afterDate);
		
		//the priorAfterSet could be empty: no prior and after events
		//the priorAfterSet could only have prior event or after event
		//the priorAfterSet could have both prior and after events.
		return priorAfterSet;	
	}			
	
	public void printEdgeMatrix(Set<Integer>[][] matrix){
		Set<Integer> tmpSet = new HashSet<Integer>();
		
		for (int i = 0; i < matrix.length; i++) {
			System.out.print("i = " + i + "  ");
			for (int x = 0; x < matrix[i].length; x++) {
				if (x > i) {
					System.out.print("x = " + x + "  ");
					tmpSet = matrix[i][x];
					if (tmpSet.isEmpty())
						System.out.println("Empty");
					else if (tmpSet == null)
						System.out.println("Null");
					else {
						for (Object element : tmpSet)
							System.out.print(element.toString() + "  ");
						System.out.println();
					}
				}
			}
		}
	}
	
	public void printDebugInfo(){
		// print union set
		/*System.out.println("========= nodeSet: ===========");
		System.out.println("Number of nodes: " + nodeSet.size());
		for (Object element : nodeSet) {
			System.out.print(element.toString() + " ");
		}*/
		System.out.println("========== Sorted node list: ===========");
		printSortedNode(sortedNodeList);

		// print conId_sortedEvtList_map
		System.out.println();
		System.out.println("=========== conId_sortedEvtList_map: ==========");
		System.out.println("Number of contributors in the map: " + conId_sortedEvtList_map.size());
		for (Map.Entry<Integer, List<Event>> e : conId_sortedEvtList_map.entrySet()){
		    //System.out.println(e.getKey() + ": " + e.getValue());
			System.out.println("cID: " + e.getKey());
			int numOfNodes = e.getValue().size();
			
			for (int i = 0; i < numOfNodes; i++) {
				System.out.print(e.getValue().get(i).getId() + " / ");
			}	
			System.out.println();
		}
		
		// print evtId_conSet_map
		System.out.println("=========== evtId_conSet_map: ===========");
		System.out.println("Number of events in the map: " + evtId_conSet_map.size());
		for (Map.Entry<Integer, Set<Integer>> e : evtId_conSet_map.entrySet())
		    System.out.println(e.getKey() + ": " + e.getValue());

		System.out.println("=========== evtId_evtObj_map: ===========");
		System.out.println("Number of events in the map: " + evtId_evtObj_map.size());
		for (Map.Entry<Integer, Event> e : evtId_evtObj_map.entrySet())			
		    System.out.println(e.getKey() + ": " + e.getValue().getName());
		
		System.out.println("=========== conId_conObj_map: ===========");
		System.out.println("Number of contributors in the map: " + conId_conObj_map.size());
		for (Map.Entry<Integer, Collaborator> e: conId_conObj_map.entrySet())
			System.out.println(e.getKey() + ": " + e.getValue().getName());
	}
	
	public void printSortedNode(List<Event> sortedNodeList){
		int numOfNodes = sortedNodeList.size();
		
		for (int i = 0; i < numOfNodes; i++) {
			System.out.print(i + " ");
			System.out.print(sortedNodeList.get(i).getId());
			System.out.print("	" + sortedNodeList.get(i).getFirstDate());
			System.out.println("  " + sortedNodeList.get(i).getName());
		}	
		
	}

	
	/**
	 * A method to build the Graphml file for event network
	 *
	 * @param evtDom    org.w3c.dom.Document
	 * @param eventId   the unique identifier of an event
	 * @param formatType GraphML
	 * @param radius    the number of edges from the central node
	 * @param simplify  whether retrieve events (2nd degree nodes) for all contributors or 
	 * 					only for those contributors involved in the central node
	 * @param graphType directed/undirected
	 * 
	 */
	@SuppressWarnings("unchecked")
	public Document toGraphMLDOM(Document evtDom, String eventId, String formatType, int radius, boolean simplify, String graphType){
				
		Set<Integer> vertexSet = new HashSet<Integer>();  //the nth degree nodes set
		
		//add the central event node 
		vertexSet = setupCentralNode(eventId);
		
		//get the vertex(Event nodes) of graph
		getGraphVertex(radius, vertexSet, simplify);
					
		if (nodeSet != null ){			
			sortedNodeList = getSortedNodeList();
			
			// get the no-duplicated edges (contributors) of graph
			int numOfNodes = sortedNodeList.size();
			edgeMatrix = new HashSet[numOfNodes][numOfNodes];
			edgeMatrix = getEdges(sortedNodeList);			
			
			degree = new int [numOfNodes][2];
			degree = getNodeDegree(sortedNodeList, edgeMatrix);
		} else 
			return null;
		
		//build DOM after getting the event network internal data structure
		// get the root element
		Element rootElement = evtDom.getDocumentElement();
		rootElement = createHeaderElements(evtDom, rootElement, graphType);
		
		Event evt = evtId_evtObj_map.get(Integer.parseInt(eventId));
		
		if (evt == null) return null;
		
		// add the graph element
		Element graph = evtDom.createElement("graph");
		graph.setAttribute("id", "Event Network for Event: " + evt.getName() + " ( " + eventId + " )");
		graph.setAttribute("edgedefault", graphType);
		rootElement.appendChild(graph);
		
		//create node element in DOM		
		for (int i = 0; i < sortedNodeList.size(); i++){
			Element node = createNodeElement(evtDom,i);			
			graph.appendChild(node);
		}
		
		int eID;
		int cID;
		int edgeIndex = 0;
		Collaborator con = null;
		Set<Integer> conSet = null;
		
		//create edge element in DOM
		for (int i = 0; i < edgeMatrix.length; i++){
			eID = sortedNodeList.get(i).getIntId();
			
			for (int j = i + 1; j < edgeMatrix[i].length; j++){
				conSet = edgeMatrix[i][j];
				if (!conSet.isEmpty()) {

					for (Iterator it = conSet.iterator(); it.hasNext();) {
						cID = (Integer) it.next();

						if (!conId_conObj_map.containsKey(cID))	{											
							con = getContributorDetail(eID, cID, false);							
							conId_conObj_map.put(cID, con);
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
	
	public Element createHeaderElements (Document evtDom, Element rootElement, String graphType){
		Element key;
			
		// add schema namespace to the root element
		rootElement.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns:xsi", "http://www.w3.org/2001/XMLSchema-instance");
		
		// add reference to the kml schema
		rootElement.setAttribute("xsi:schemaLocation", "http://graphml.graphdrawing.org/xmlns http://graphml.graphdrawing.org/xmlns/1.0/graphml.xsd");
		
		// add some useful comments to the file
		rootElement.appendChild(evtDom.createComment("Graph generated on: " + DateUtils.getCurrentDateAndTime()));
//		rootElement.appendChild(xmlDoc.createComment("Graph generated by: " + rdf.getContextParam("systemName") + " Version: " + rdf.getContextParam("systemVersion") + " Build: " + rdf.getContextParam("buildVersion")));
		
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
	
	public Element createNodeElement(Document evtDom, int i){
		Element data;
		
		Event evt = sortedNodeList.get(i);
		Element node = evtDom.createElement("node");
		node.setAttribute("id", evt.getId());
		
		for(int row = 0; row < nodeAttr.length; row ++){
			data = evtDom.createElement("data");
			data.setAttribute("key", nodeAttr[row][0]);
			
			if (nodeAttr[row][0].equalsIgnoreCase("EventID"))
				data.setTextContent(evt.getId()); 
			else if	(nodeAttr[row][0].equalsIgnoreCase("Label"))
				data.setTextContent(evt.getName());
			else if (nodeAttr[row][0].equalsIgnoreCase("EventName"))
				data.setTextContent(evt.getName());
			else if (nodeAttr[row][0].equalsIgnoreCase("Venue"))
				data.setTextContent(evt.getVenue());
			else if (nodeAttr[row][0].equalsIgnoreCase("StartDate"))
				data.setTextContent(evt.getFirstDate());
			else if (nodeAttr[row][0].equalsIgnoreCase("Central")) {
				if (central_eventId != evt.getIntId())
					data.setTextContent("false");
				else 
					data.setTextContent("true");
			} else if (nodeAttr[row][0].equalsIgnoreCase("InDegree")){
				data.setTextContent(Integer.toString(degree[i][0]));
			} else if (nodeAttr[row][0].equalsIgnoreCase("OutDegree")){
				data.setTextContent(Integer.toString(degree[i][1]));
			} else if (nodeAttr[row][0].equalsIgnoreCase("EventURL")){
				data.setTextContent(evtURLprefix + evt.getId());
			}
			
			node.appendChild(data);			
		}
				
		return node;		
	}
	
	public Element createEdgeElement(Document evtDom, Collaborator con, int src, int tar, int index){
			
		Event srcEvt = sortedNodeList.get(src);
		Event tarEvt = sortedNodeList.get(tar);
		
		Element edge = evtDom.createElement("edge");
		edge.setAttribute("id", "e" + Integer.toString(index));
		edge.setAttribute("source", srcEvt.getId());
		edge.setAttribute("target", tarEvt.getId());
		
		for(int row = 0; row < edgeAttr.length; row ++){
			Element data = evtDom.createElement("data");
			data.setAttribute("key", edgeAttr[row][0]);
			
			if (edgeAttr[row][0].equalsIgnoreCase("ContributorID"))
				data.setTextContent(con.getId()); 
			else if	(edgeAttr[row][0].equalsIgnoreCase("Label"))
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
				data.setTextContent(conURLprefix + con.getId());
				
			edge.appendChild(data);
		}
		return edge;
	}
	
	//calulate the inDegree (degree[][0]) and outDegree (degree[][1])
	public int[][] getNodeDegree(List<Event> sortedNodeList, Set<Integer>[][] edgeMatrix) {
		
		int numberOfNodes = sortedNodeList.size();
		int degree [][] = new int[numberOfNodes][2];
		
		//calculate inDegree[i][0]
		for(int i = 0; i < numberOfNodes; i++){
			if (i == 0)
				degree[i][0] = 0; //the first node has 0 inDegree
			else {
				int in = 0;
				for (int j = 0; j < i; j++)
					if (edgeMatrix[j][i] != null)
						in = in + edgeMatrix[j][i].size();
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
					if (edgeMatrix[i][j] != null)
						out = out + edgeMatrix[i][j].size();
				degree[i][1] = out;
			}
			
		}		
		
		return degree;
	}
	
}
