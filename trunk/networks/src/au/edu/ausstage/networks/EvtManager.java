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

import java.sql.ResultSet;
import java.util.ArrayList;
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

//Event Network manager
public class EvtManager extends NetworkManager {
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
	
		
	/**
	 * @param db DatabaseManager
	 * @param id Organisation ID / Venue ID
	 * @param type 'o' organisation-based event network / 'v' venue-based event network 
	 * @param graphType directed/undirected
	 */
	public EvtManager(DatabaseManager db, String id, String type, String graphType){
		super(db, new Network());
		network.setId(id);
		network.setType(type);		
		network.setGraphType(graphType);
		
	}
	
	@SuppressWarnings({ "unchecked", "unused" })
	public Network createOrgOrVenueEvtNetwork(){

		long startTime = System.currentTimeMillis();
		//get node set for the network
		Set<Integer> nodeSet = getNodes();
		//List<Integer> evtList;		
		
		if (nodeSet == null) 
			return null;
		else {
			network.setNodeSet(nodeSet);
			String name = getName();
			network.setName(name);		
			
			//get Event detail (node)					
			if (!nodeSet.isEmpty())
			  	getEventsDetail(nodeSet);
			
			network.setSortedEventList(network.sortedNode());
			//network.printSortedNode();
			
		}
		
		long getNodeTime = System.currentTimeMillis();
		//System.out.println("\n time to get sorted Nodes is :" + (getNodeTime-startTime)+ "ms");
		
		//get edge Matrix for the network
		int numOfNodes = nodeSet.size();
		Set<Integer>[][] edgeMatrix = new HashSet[numOfNodes][numOfNodes];
		edgeMatrix = getEdges();
		network.setEdgeMatrix(edgeMatrix);
		
		long matrixTime = System.currentTimeMillis();
		//network.printEdgeMatrix();
		//System.out.println("\n time to create matrix is :" + (matrixTime - getNodeTime)+ "ms");
		
		deleteCycle();
		long delcycleTime = System.currentTimeMillis();	
		//network.printEdgeMatrix();
		//System.out.println("\n time to delete cycle is :" + (delcycleTime - matrixTime)+ "ms");
				
		//get contributor detail (edge)
/*		for (int i = 0; i < numOfNodes; i++){
			int eID = evtList.get(i);
			
			for (int j = i + 1; j < numOfNodes; j++){
				List<Integer> conList = new ArrayList<Integer>(edgeMatrix[i][j]);
				
				for (int x = 0; x < conList.size(); x ++) {
					int cID = conList.get(x);
					if (!network.conId_conObj_map.containsKey(cID))	{											
						Collaborator con = getContributorDetail(eID, cID, false);													
					} else {								
						Collaborator con = getContributorDetail(eID, cID, true);	
					}
				}
			}
		}*/
				
		return network;
	}
	
	public Set<Integer> getNodes(){
		Set<Integer> evtSet = new HashSet<Integer>();	
		String sql = "";
		
		if (network.type.equalsIgnoreCase("o"))
			
			sql = "SELECT DISTINCT o.eventid, e.first_date "
					+ "FROM orgevlink o, events e "
					+ "WHERE o.organisationid = ? " 
					+ "AND o.eventid = e.eventid"
					+ " ORDER BY e.first_date";
		
		else if (network.type.equalsIgnoreCase("v"))
		
			sql = "SELECT DISTINCT eventid, first_date "
					+ "FROM events " 
					+ "WHERE venueid = ? "
					+ "order by first_date";		
		else 
			return null;
			
		evtSet = db.getResultfromDB(sql, Integer.parseInt(network.id));
				
		return evtSet;
	}
	
	@SuppressWarnings("unchecked")
	public Set<Integer>[][] getEdges(){		
		
		int numOfNodes = network.sortedEventList.size();		
		Set<Integer> src_con = new HashSet<Integer>();
		Set<Integer> tar_con = new HashSet<Integer>();
		Set<Integer>[][] edgeMatrix = new HashSet[numOfNodes][numOfNodes];
		               
		for (int i = 0; i < numOfNodes; i++){
			//System.out.println("i = " + i + "  =============");
			int src_evt_id = network.sortedEventList.get(i).getIntId();
			src_con = getAssociatedContributors(src_evt_id);			
			
			for (int j = i + 1; j < numOfNodes; j++){
				//System.out.print("j = " + j + "  ");
				//since the nodeSet is already sorted according to the start_time (chronological order)
				//and the earlier event should flow to the later event.
				//so there will be no edges from later event to earlier event.
				int tar_evt_id = network.sortedEventList.get(j).getIntId();	
				tar_con = getAssociatedContributors(tar_evt_id);				
				
				if (tar_con != null && !tar_con.isEmpty() && src_con != null) {
					Set<Integer> intersection = new HashSet<Integer>(tar_con);
					intersection.retainAll(src_con);
					edgeMatrix[i][j] = intersection;
					/*for (Object element : intersection)
						System.out.print(element.toString() + "  ");*/					
				}
				//System.out.println();
			}			
		}
		
		return edgeMatrix;
	}
	
	public void deleteCycle(){
		
		int numOfNodes = network.nodeSet.size();
		for (int cID : network.contributorSet) {
			boolean[] isVisited = new boolean[numOfNodes];
			// cycle from start to current node
			ArrayList<Integer> trace = new ArrayList<Integer>();

			// System.out.println("Detect Cycle for contributor: " + cID);
			for (int i = 0; i < numOfNodes; i++)
				network.findCycle(cID, i, isVisited, trace); // started from the second earliest event
		}
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
		for (int i = 0; i < network.sortedEventList.size(); i++){
			Event evt = network.sortedEventList.get(i);
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
			eID = network.sortedEventList.get(i).getIntId();
			
			for (int j = i + 1; j < network.edgeMatrix[i].length; j++){
				conSet = network.edgeMatrix[i][j];
				if (conSet != null && !conSet.isEmpty()) {

					for (Iterator it = conSet.iterator(); it.hasNext();) {
						cID = (Integer) it.next();													
						con = getContributorDetail(eID, cID);														
						
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
		
		Event srcEvt = network.sortedEventList.get(src);
		Event tarEvt = network.sortedEventList.get(tar);
		
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
