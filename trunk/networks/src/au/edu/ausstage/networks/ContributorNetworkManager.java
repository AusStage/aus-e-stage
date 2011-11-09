/**
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

/**
 * Contributor by venue Network Manager
 */
public class ContributorNetworkManager extends NetworkManager{
	//edge attribute for graphml export
	public String [][] edgeAttr = {
			{"EventID", "string"}, {"EdgeLabel", "string"}, {"EventName", "string"}, {"Venue", "string"},
			{"StartDate", "string"}, {"SourceContributorID", "string"}, {"TargetContributorID", "string"},
			{"EventURL", "string"}
		};
	//node attribute for graphml export
	public String [][] nodeAttr = {
			{"ContributorID", "string"}, {"NodeLabel", "string"}, {"ContributorName", "string"},
			{"Roles", "string"}, {"Gender", "string"}, {"Nationality", "string"},
			{"ContributorURL", "string"}
	};
	
	/**
	 * @param db DatabaseManager
	 * @param id venue ID
	 * @param type 'o' organisation-based contributor network / 'v' venue-based contributor network 
	 * @param graphType directed/undirected
	 */
	public ContributorNetworkManager(DatabaseManager db, String id, String type, String graphType) {
		super(db, new Network());
		network.setId(id);
		network.setType(type);		
		network.setGraphType(graphType);
	}
	
	@SuppressWarnings("unchecked")
	public Network createVenueConNetwork(){
		//long startTime = System.currentTimeMillis();
		//get node set for the network
		Set<Integer> nodeSet = getNodes();
		//List<Integer> evtList;		
		
		if (nodeSet == null) 
			return null;
		else {
			network.setNodeSet(nodeSet);
			String name = getName();
			network.setName(name);				
			
			//get contributor detail (node)					
			if (!nodeSet.isEmpty())
			  	getContributorsDetail(nodeSet);
			
			network.setContributorList(network.getContributors());
		}
		
		/*long getNodeTime = System.currentTimeMillis();
		System.out.println("\n time to get Nodes is :" + (getNodeTime-startTime)+ "ms");
		*/
		//get edge Matrix for the network
		int numOfNodes = nodeSet.size();
				
		if (numOfNodes != network.contributorList.size()) {
			System.out.println("******* Number of Node is " + numOfNodes);
			System.out.println("******* Number of Contributors is " + network.contributorList.size());
		}
		
/*		int i = 0;
		for(int cID : nodeSet){
			
			if (i >= network.contributorList.size())
				System.out.println(cID);
			else
				System.out.println(cID + "   " + network.contributorList.get(i).getId());
			i++;
		}
		*/
		
		Set<Integer>[][] edgeMatrix = new HashSet[numOfNodes][numOfNodes];
		edgeMatrix = getEdges();
		network.setEdgeMatrix(edgeMatrix);
		
	/*	long matrixTime = System.currentTimeMillis();
		//network.printEdgeMatrix();
		System.out.println("\n time to create matrix is :" + (matrixTime - getNodeTime)+ "ms");*/
				
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
	
	@SuppressWarnings("unused")
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
		
		//for each event in evtSet, get its associated contributors
		//calculate the contributor union to get contributorSet as network nodes
		for (int  evtId: evtSet){
			Set<Integer> conSet = new HashSet<Integer>();			
			conSet = getAssociatedContributors(evtId);
			/*if (conSet != null)
				System.out.println("eID: " + evtId + "   " + conSet.toString());
			else
				System.out.println("eID: " + evtId + "   NULL");*/
		}		
		
		return network.contributorSet;
	}
	
	@SuppressWarnings("unchecked")
	public Set<Integer>[][] getEdges(){		
		
		int numOfNodes = network.nodeSet.size();		
		Set<Integer> src_evt = new HashSet<Integer>();
		Set<Integer> tar_evt = new HashSet<Integer>();
		Set<Integer>[][] edgeMatrix = new HashSet[numOfNodes][numOfNodes];
		List<Integer> nodeList = new ArrayList<Integer>(network.nodeSet);
		               
		for (int i = 0; i < numOfNodes; i++){
			//System.out.println("i = " + i + "  =============");
			int src_con_id = nodeList.get(i);
			src_evt = getAssociatedEvents(src_con_id);			
			
			for (int j = i + 1; j < numOfNodes; j++){
				//System.out.print("j = " + j + "  ");
				//since the contributor network is undirected, the edges between node1 and node2 
				//is the same as edges between node2 and node1
				//so only half of the edge matrix is kept. 
				int tar_con_id = nodeList.get(j);	
				tar_evt = getAssociatedEvents(tar_con_id);				
				
				if (tar_evt != null && !tar_evt.isEmpty() && src_evt != null) {
					Set<Integer> intersection = new HashSet<Integer>(tar_evt);
					intersection.retainAll(src_evt);
					edgeMatrix[i][j] = intersection;
					/*for (Object element : intersection)
						System.out.print(element.toString() + "  ");*/					
				}
				//System.out.println();
			}			
		}
		
		return edgeMatrix;
	}
	
	@SuppressWarnings("rawtypes")
	public Document toGraphMLDOM(Document dom){
		
		if (network.nodeSet == null )			
			return null;
		
		// get the root element
		Element rootElement = dom.getDocumentElement();
		rootElement = createHeaderElements(dom, rootElement);
				
		// add the graph element
		Element graph = dom.createElement("graph");
		
		String type = "";
		if (network.getType().equalsIgnoreCase("o"))
			type = "organisation";
		else if (network.getType().equalsIgnoreCase("v"))
			type = "venue";
		
		graph.setAttribute("id", type + network.getId());
		graph.setAttribute("edgedefault", network.getGraphType());
		rootElement.appendChild(graph);
		
		//create node element in DOM	
		for (int i = 0; i < network.contributorList.size(); i++){
			Collaborator con = network.contributorList.get(i);
			if (con != null) {
				Element node = createNodeElement(dom, con);			
				graph.appendChild(node);
			}
		}
		
		//create edge element in DOM
		//int cID;
		int eID;
		int edgeIndex = 0;
		Event evt = null;
		Set<Integer> evtSet = null;
		
		for (int i = 0; i < network.edgeMatrix.length; i++){
			//cID = network.contributorList.get(i).getIntId();
			
			for (int j = i + 1; j < network.edgeMatrix[i].length; j++){
				evtSet = network.edgeMatrix[i][j];
				if (evtSet != null && !evtSet.isEmpty()) {

					for (Iterator it = evtSet.iterator(); it.hasNext();) {
						eID = (Integer) it.next();																		
						evt = getEventDetail(eID);														
																		
						if (evt != null){
							edgeIndex ++;
							Element edge = createEdgeElement(dom, evt, i, j, edgeIndex);			
							graph.appendChild(edge);
						}															
							
					}					
				} 
			}
		}
		
		return dom;
		
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
	
	public Element createNodeElement(Document dom, Collaborator con){
		Element data;
		
		Element node = dom.createElement("node");
		node.setAttribute("id", con.getId());
		
		for(int row = 0; row < nodeAttr.length; row ++){
			data = dom.createElement("data");
			data.setAttribute("key", nodeAttr[row][0]);
			
			if (nodeAttr[row][0].equalsIgnoreCase("ContributorID"))
				data.setTextContent(con.getId()); 
			else if	(nodeAttr[row][0].equalsIgnoreCase("NodeLabel"))
				data.setTextContent(con.getGFName());
			else if (nodeAttr[row][0].equalsIgnoreCase("ContributorName"))
				data.setTextContent(con.getGFName());
			else if (nodeAttr[row][0].equalsIgnoreCase("Roles"))
				data.setTextContent(con.getFunction());
			else if (nodeAttr[row][0].equalsIgnoreCase("Gender"))
				data.setTextContent(con.getGender());
			else if (nodeAttr[row][0].equalsIgnoreCase("Nationality"))
				data.setTextContent(con.getNationality());
			else if (nodeAttr[row][0].equalsIgnoreCase("ContributorURL"))
				data.setTextContent(AusStageURI.getContributorURL(con.getId()));
				//data.setTextContent(evtURLprefix + evt.getId());
			
			node.appendChild(data);			
		}
				
		return node;		
	}
	
	public Element createEdgeElement(Document dom, Event evt, int src, int tar, int index){
		
		Collaborator srcCon = network.contributorList.get(src);
		Collaborator tarCon = network.contributorList.get(tar);
		
		Element edge = dom.createElement("edge");
		edge.setAttribute("id", "e" + Integer.toString(index));
		edge.setAttribute("source", srcCon.getId());
		edge.setAttribute("target", tarCon.getId());
		
		for(int row = 0; row < edgeAttr.length; row ++){
			Element data = dom.createElement("data");
			data.setAttribute("key", edgeAttr[row][0]);
			
			if (edgeAttr[row][0].equalsIgnoreCase("EventID"))
				data.setTextContent(evt.getId()); 
			else if	(edgeAttr[row][0].equalsIgnoreCase("EdgeLabel"))
				data.setTextContent(evt.getName());
			else if (edgeAttr[row][0].equalsIgnoreCase("EventName"))
				data.setTextContent(evt.getName());
			else if (edgeAttr[row][0].equalsIgnoreCase("Venue"))
				data.setTextContent(evt.getVenue());
			else if (edgeAttr[row][0].equalsIgnoreCase("StartDate"))
				data.setTextContent(evt.getFirstDate());
			else if (edgeAttr[row][0].equalsIgnoreCase("SourceContributorID"))
				data.setTextContent(srcCon.getId());
			else if (edgeAttr[row][0].equalsIgnoreCase("TargetContributorID")) 
				data.setTextContent(tarCon.getId());
			else if (edgeAttr[row][0].equalsIgnoreCase("EventURL"))
				data.setTextContent(AusStageURI.getEventURL(evt.getId()));
				//data.setTextContent(conURLprefix + con.getId());
				
			edge.appendChild(data);
		}
		return edge;
	}

}
