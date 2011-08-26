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
import java.util.List;
import java.util.Set;

import au.edu.ausstage.networks.types.Collaborator;
import au.edu.ausstage.networks.types.Event;
import au.edu.ausstage.networks.types.Network;

public class EvtManager extends NetworkManager {
	public String id = null;
	public String type = null;
	public String graphType = null;
	
		
	/**
	 * @param db DatabaseManager
	 * @param id Organisation ID
	 * @param graphType directed/undirected
	 */
	public EvtManager(DatabaseManager db, String id, String type, String graphType){
		super(db, new Network());
		this.id = id;
		this.type = type;		
		this.graphType = graphType;
		
	}
	
	@SuppressWarnings({ "unchecked", "unused" })
	public Network createOrgOrVenueEvtNetwork(){

		//get node set for the network
		Set<Integer> nodeSet = getNodes();
		//List<Integer> evtList;		
		
		if (nodeSet == null) 
			return null;
		else {
			network.setNodeSet(nodeSet);
			network.setId(id);
			String name = getName();
			network.setName(name);				
			network.setType(type);
			network.setGraphType(graphType);
			
			//get Event detail (node)
			
			if (!nodeSet.isEmpty()){
			  	List<Event> evtList = getEventDetail(nodeSet);
			}
			network.setSortedNodeList(network.sortedNode());
			//network.printSortedNode();
			
		}
		
		//get edge Matrix for the network
		int numOfNodes = nodeSet.size();
		Set<Integer>[][] edgeMatrix = new HashSet[numOfNodes][numOfNodes];
		edgeMatrix = getEdges();
		network.setEdgeMatrix(edgeMatrix);
		
		deleteCycle();
		
		return network;
	}
	
	public Set<Integer> getNodes(){
		Set<Integer> evtSet = new HashSet<Integer>();	
		String sql = "";
		
		if (type.equalsIgnoreCase("o"))
			
			sql = "SELECT DISTINCT o.eventid, e.first_date "
					+ "FROM orgevlink o, events e "
					+ "WHERE o.organisationid = ? " 
					+ "AND o.eventid = e.eventid"
					+ " ORDER BY e.first_date";
		
		else if (type.equalsIgnoreCase("v"))
		
			sql = "SELECT DISTINCT eventid, first_date "
					+ "FROM events " 
					+ "WHERE venueid = ? "
					+ "order by first_date";		
		else 
			return null;
			
		evtSet = db.getResultfromDB(sql, Integer.parseInt(id));
				
		return evtSet;
	}
	
	public String getName(){
		
		String sql = "";
			
		if (type.equalsIgnoreCase("o")) {
			
			sql = "SELECT DISTINCT name "
				+ "FROM organisation "
				+ "WHERE organisationid = ?"; 
			
		} else if (type.equalsIgnoreCase("v")) {
			
			sql = "SELECT DISTINCT venue_name "
				+ "FROM venue "
				+ "WHERE venueid = ?";
		}
			
		int[] param = {Integer.parseInt(id)};
		
		ResultSet results = db.exePreparedStatement(sql, param);				
		String name = "";
		
		try {			
			// 	check to see that data was returned
			if (!results.last()){	
				db.tidyup();
				return null;
			}else 
				results.beforeFirst();
			
			while(results.next() == true) {
				name = results.getString(1);									
			}									
		} catch (java.sql.SQLException ex) {	
			System.out.println("Exception: " + ex.getMessage());
			results = null;
		}
		
		db.tidyup();
		return name;
	}
	
	@SuppressWarnings("unchecked")
	public Set<Integer>[][] getEdges(){		
		
		int numOfNodes = network.sortedNodeList.size();		
		Set<Integer> src_con = new HashSet<Integer>();
		Set<Integer> tar_con = new HashSet<Integer>();
		Set<Integer>[][] edgeMatrix = new HashSet[numOfNodes][numOfNodes];
		               
		for (int i = 0; i < numOfNodes; i++){
			//System.out.println("i = " + i + "  =============");
			int src_evt_id = network.sortedNodeList.get(i).getIntId();
			src_con = getAssociatedContributors(src_evt_id);			
			
			for (int j = i + 1; j < numOfNodes; j++){
				//System.out.print("j = " + j + "  ");
				//since the nodeSet is already sorted according to the start_time (chronological order)
				//and the earlier event should flow to the later event.
				//so there will be no edges from later event to earlier event.
				int tar_evt_id = network.sortedNodeList.get(j).getIntId();	
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
	
}
