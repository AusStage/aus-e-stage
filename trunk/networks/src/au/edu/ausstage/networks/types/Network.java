package au.edu.ausstage.networks.types;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Network {
	public String id = "";
	public String name = "";
	public String type = ""; //organisation or venue
	public String graphType = ""; //directed or undirected
	
	public Set<Integer> nodeSet = new HashSet<Integer>();
	public Set<Integer>[][] edgeMatrix;
	public List<Event> sortedNodeList = new ArrayList<Event>();
	public Set<Integer> contributorSet = new HashSet<Integer>();
	public Map<Integer, Event> evtId_evtObj_map = new HashMap<Integer, Event>();	
	public Map<Integer, Set<Integer>> evtId_conSet_map = new HashMap<Integer, Set<Integer>>();
	public Map<Integer, Collaborator> conId_conObj_map = new HashMap<Integer, Collaborator>();
	
	public Network(){}
	
	public Network(Set<Integer> nodeSet, Set<Integer>[][] edgeMatrix){
		this.nodeSet = nodeSet;
		this.edgeMatrix = edgeMatrix;
	}

	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getGraphType() {
		return graphType;
	}

	public void setGraphType(String graphType) {
		this.graphType = graphType;
	}

	public Set<Integer> getNodeSet() {
		return nodeSet;
	}

	public void setNodeSet(Set<Integer> nodeSet) {
		this.nodeSet = nodeSet;
	}

	public Set<Integer>[][] getEdgeMatrix() {
		return edgeMatrix;
	}

	public void setEdgeMatrix(Set<Integer>[][] edgeMatrix) {
		this.edgeMatrix = edgeMatrix;
	}
	
	public Map<Integer, Event> getEvtId_evtObj_map() {
		return evtId_evtObj_map;
	}

	public void setEvtId_evtObj_map(Map<Integer, Event> evtId_evtObj_map) {
		this.evtId_evtObj_map = evtId_evtObj_map;
	}
	
	public Map<Integer, Set<Integer>> getEvtId_conSet_map() {
		return evtId_conSet_map;
	}

	public void setEvtId_conSet_map(Map<Integer, Set<Integer>> evtId_conSet_map) {
		this.evtId_conSet_map = evtId_conSet_map;
	}

	public Map<Integer, Collaborator> getConId_conObj_map() {
		return conId_conObj_map;
	}

	public void setConId_conObj_map(Map<Integer, Collaborator> conId_conObj_map) {
		this.conId_conObj_map = conId_conObj_map;
	}	

	public List<Event> getSortedNodeList() {
		return sortedNodeList;
	}

	public void setSortedNodeList(List<Event> sortedNodeList) {
		this.sortedNodeList = sortedNodeList;
	}

	@SuppressWarnings("unchecked")
	public ArrayList<Event> sortedNode(){
		int eID = 0;
		ArrayList<Event> sortedNodeList = new ArrayList<Event>();
		
		// get Events(Nodes) List from evtId_evtObj_map
		for (Iterator it = nodeSet.iterator(); it.hasNext();) {
			eID = (Integer) it.next();
			if (evtId_evtObj_map.get(eID) != null)
				sortedNodeList.add(evtId_evtObj_map.get(eID));
		}

		EvtComparator evtComp = new EvtComparator();
		// Sorting Event List on the basis of Event first Date by passing Comparator
		Collections.sort(sortedNodeList, evtComp);
		
		return sortedNodeList;
	}
	
	//use depth first degree algorithm to detect cycle (redundant edge) in the graph
	//and delete it from edgeMatrix 
	public void findCycle(int cID, int nodeIndex, boolean[] isVisited, ArrayList<Integer> trace){
		
		if (isVisited[nodeIndex]){
			if((trace.indexOf(nodeIndex))!= -1) {            
                
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
	
	
	public void printSortedNode(){
		int numOfNodes = sortedNodeList.size();
		System.out.println("-----Sorted Node List------");
		for (int i = 0; i < numOfNodes; i++) {
			System.out.print(i + " ");
			System.out.print(sortedNodeList.get(i).getId());
			System.out.print("	" + sortedNodeList.get(i).getFirstDate());
			System.out.println("  " + sortedNodeList.get(i).getName());
		}	
		
	}
	
	public void printEdgeMatrix(){
		Set<Integer> tmpSet = new HashSet<Integer>();
		
		System.out.println("------- edge matrix ------");
		for (int i = 0; i < edgeMatrix.length; i++) {
			System.out.println("i = " + i + "  ");
			for (int x = 0; x < edgeMatrix[i].length; x++) {
				if (x > i) {
					System.out.print("x = " + x + "  ");
					tmpSet = edgeMatrix[i][x];
					if (tmpSet == null)
						System.out.println("Null");
					else if (tmpSet.isEmpty())
						System.out.println("Empty");
					else {
						for (Object element : tmpSet)
							System.out.print(element.toString() + "  ");
						System.out.println();
					}
				}
			}
			System.out.println("--------");
		}
	}
}
