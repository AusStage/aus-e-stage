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

// general java
import java.util.*;
import java.io.*;

//xml
import org.w3c.dom.*;
import javax.xml.parsers.*; 
import javax.xml.transform.*; 
import javax.xml.transform.dom.*; 
import javax.xml.transform.stream.*;

// import AusStage related packages
import au.edu.ausstage.vocabularies.*;
import au.edu.ausstage.utils.DateUtils;

/**
 * A class to manage the export of information
 */
public class ExportManager {

	// declare private class level variables
	private DataManager database = null;

	/**
	 * Constructor for this class
	 */
	public ExportManager(DataManager database) {	
		// store a reference to this DataManager for later
		this.database = database;
	} // end constructor
	
	
	/**
	 * A method to lookup the key collaborators for a contributor
	 *
	 * @param id          the unique id of the contributor
	 * @param formatType  the required format of the data
	 * @param degrees     the required number of degrees of separation
	 * @param graphType   the type of graph to created, must be one of directed / undirected
	 * @param printWriter the output stream to use to stream the output to the client
	 */
	public void getSimpleNetwork(String id, String formatType, int degrees, String graphType, PrintWriter printWriter) {
	
		// define helper variables
		// collection of collaborators
		java.util.TreeMap<Integer, Collaborator> collaborators = new java.util.TreeMap<Integer, Collaborator>();
		
		// set of collaborators that we've already processed
		java.util.TreeSet<Integer> foundCollaborators = new java.util.TreeSet<Integer>();
		
		// define other helper variables
		QuerySolution row             = null;
		Collaborator  collaborator    = null;
		int           degreesFollowed = 1;
		int           degreesToFollow = degrees;
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
		collaborators.put(Integer.parseInt(id), collaborator);
		foundCollaborators.add(Integer.parseInt(id));
		
		// build the query
		queryToExecute = sparqlQuery.replaceAll("@", "<" + AusStageURI.getContributorURI(id) + ">");
		
		// execute the query
		ResultSet results = database.executeSparqlQuery(queryToExecute);
		
		// add the first degree contributors
		while (results.hasNext()) {
			// loop though the resulset
			// get a new row of data
			row = results.nextSolution();
			
			// add the collaboration
			collaborator.addCollaborator(AusStageURI.getId(row.get("collaborator").toString()));
		}
		
		// play nice and tidy up
		database.tidyUp();
		
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
				collaborators.put(Integer.parseInt(id), collaborator);
				
				// build the query
				queryToExecute = sparqlQuery.replaceAll("@", "<" + AusStageURI.getContributorURI(id) + ">");
				
				// get the data
				results = database.executeSparqlQuery(queryToExecute);
			
				// loop though the resulset
				while (results.hasNext()) {
					
					// get a new row of data
					row = results.nextSolution();
					
					if(values.contains(AusStageURI.getId(row.get("collaborator").toString())) == true) {
						collaborator.addCollaborator(AusStageURI.getId(row.get("collaborator").toString()));
					}
				}
			}		
		
		} else {
		
			// get the rest of the degrees
			while(degreesFollowed < degreesToFollow) {
		
				// get all of the known collaborators
				values = collaborators.values();
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
						if(foundCollaborators.contains(Integer.parseInt(toProcess[i])) == false) {
							// we haven't so process them
							collaborator = new Collaborator(toProcess[i]);
							collaborators.put(Integer.parseInt(toProcess[i]), collaborator);
							foundCollaborators.add(Integer.parseInt(toProcess[i]));
						
							// build the query
							queryToExecute = sparqlQuery.replaceAll("@", "<" + AusStageURI.getContributorURI(toProcess[i]) + ">");
						
							// get the data
							results = database.executeSparqlQuery(queryToExecute);
							
							// loop though the resulset
							while (results.hasNext()) {
								// get a new row of data
								row = results.nextSolution();
			
								// add the collaboration
								collaborator.addCollaborator(AusStageURI.getId(row.get("collaborator").toString()));
							}
						
							// play nice and tidy up
							database.tidyUp();
						}
					}
				}
			
				// increment the degrees followed count
				degreesFollowed++;
			}
			
			// finalise the graph
			// get all of the known collaborators and use a copy of the current list of collaborators
			java.util.TreeMap clone = (java.util.TreeMap)collaborators.clone();
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
					if(foundCollaborators.contains(Integer.parseInt(toProcess[i])) == false) {
						// we haven't so process them
						collaborator = new Collaborator(toProcess[i]);
						collaborators.put(Integer.parseInt(toProcess[i]), collaborator);
						foundCollaborators.add(Integer.parseInt(toProcess[i]));
					
						// build the query
						queryToExecute = sparqlQuery.replaceAll("@", "<" + AusStageURI.getContributorURI(toProcess[i]) + ">");
					
						// get the data
						results = database.executeSparqlQuery(queryToExecute);
						
						// loop though the resulset
						while (results.hasNext()) {
							// get a new row of data
							row = results.nextSolution();
							
							// limit to only those collaborators we've seen before
							if(foundCollaborators.contains(Integer.parseInt(AusStageURI.getId(row.get("collaborator").toString()))) == true) {
		
								// add the collaboration
								collaborator.addCollaborator(AusStageURI.getId(row.get("collaborator").toString()));
							}
						}
					
						// play nice and tidy up
						database.tidyUp();
					}
				}
			}
			
		}
		
		// dataset is built so time to do something with it
		if(formatType.equals("graphml") == true) {
			buildGraphml(collaborators, graphType, printWriter);
		} else if(formatType.equals("debug")) {
			buildDebugFile(collaborators, graphType, printWriter);
		}	
	} // end getSimpleNetwork method
	
	/**
	 * A method to output the export in the plain text format used for debugging
	 *
	 * @param collaborators the list of collaborators
	 * @param graphType  the type of graph to created, must be one of directed / undirected
 	 * @param printWirter the output stream to use to stream the output to the client
	 */
	private void buildDebugFile(java.util.TreeMap<Integer, Collaborator> collaborators, String graphType, PrintWriter printWriter) {
		
		// add some additional information
		printWriter.print("# Graph generated on: " + DateUtils.getCurrentDateAndTime() + "\n");
		printWriter.print("# Graph generated by: " + database.getContextParam("systemName") + " Version: " + database.getContextParam("systemVersion") + " Build: " + database.getContextParam("buildVersion") + "\n");
		printWriter.print("# Graph Type: " + graphType + "\n");
		
		// loop through the list of contributors
		Collection values   = collaborators.values();
		Iterator   iterator = values.iterator();
		
		while(iterator.hasNext()) {
		
			// get the collaborator
			Collaborator collaborator = (Collaborator)iterator.next();
			
			printWriter.print(collaborator.getId() + " - ");
			
			printWriter.print(java.util.Arrays.toString(collaborator.getCollaboratorsAsArray()).replaceAll("[\\]\\[]", "") + " - ");
			printWriter.print(collaborator.getCollaboratorsAsArray().length + "\n");
		}
		
		//return dataToReturn.toString();
	} // end the buildDebugFile	
	
	/**
	 * A method to output the export in the graphml format
	 *
	 * @param collaborators the list of collaborators
	 * @param graphType  the type of graph to created, must be one of directed / undirected
 	 * @param printWirter the output stream to use to stream the output to the client
	 */
	private void buildGraphml(java.util.TreeMap<Integer, Collaborator> collaborators, String graphType, PrintWriter printWriter) {
		
		// declare helper variables
		/* xml related */
		DocumentBuilderFactory factory;
		DocumentBuilder        builder;
		DOMImplementation      domImpl;
		Document               xmlDoc;
		Element                rootElement;
		Element                graph;
		Element                node;
		Element                edge;
		Element                data;
		
		// keep track of the nodes and edges we've created
		TreeSet<String> nodes = new TreeSet<String>();
		TreeSet<String> edges = new TreeSet<String>();
		
		// manage looping through the list of collaborators
		Collaborator collaborator;
		Collection values;
		Iterator   iterator;
		String[]   edgesToMake;		
		
		// start the graphml
		// create the xml document builder factory object
		factory = DocumentBuilderFactory.newInstance();
		
		// set the factory to be namespace aware
		factory.setNamespaceAware(true);
		
		// create the xml document builder object and get the DOMImplementation object
		try {
			builder = factory.newDocumentBuilder();
		} catch (javax.xml.parsers.ParserConfigurationException ex) {
			throw new RuntimeException(ex);
		}
		
		domImpl = builder.getDOMImplementation();
		
		// create a document with the appropriare default namespace
		xmlDoc = domImpl.createDocument("http://graphml.graphdrawing.org/xmlns", "graphml", null);
		
		// get the root element
		rootElement = xmlDoc.getDocumentElement();
		
		// add schema namespace to the root element
		rootElement.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns:xsi", "http://www.w3.org/2001/XMLSchema-instance");
		
		// add reference to the kml schema
		rootElement.setAttribute("xsi:schemaLocation", "http://graphml.graphdrawing.org/xmlns http://graphml.graphdrawing.org/xmlns/1.0/graphml.xsd");
		
		// add some useful comments to the file
		rootElement.appendChild(xmlDoc.createComment("Graph generated on: " + DateUtils.getCurrentDateAndTime()));
		rootElement.appendChild(xmlDoc.createComment("Graph generated by: " + database.getContextParam("systemName") + " Version: " + database.getContextParam("systemVersion") + " Build: " + database.getContextParam("buildVersion")));
		
		// add the graph element
		graph = xmlDoc.createElement("graph");
		graph.setAttribute("id", "ausstage-graph");
		graph.setAttribute("edgedefault", graphType);
		rootElement.appendChild(graph);
		
		// reset the edge and node variables
		node = null;
		data = null;
		
		// loop through the list of contributors
		values = collaborators.values();
		iterator = values.iterator();
		
		while(iterator.hasNext()) {
		
			// get the collaborator
			collaborator = (Collaborator)iterator.next();
			
			if(nodes.contains(collaborator.getId()) == false) {
			
				// add a node for this contributor
				node = xmlDoc.createElement("node");
				node.setAttribute("id", collaborator.getId());
				
				// add the node to the graph
				graph.appendChild(node);
				
				// store a reference to this contributor so we don't do a node for them
				nodes.add(collaborator.getId());
			}
			
			// get the edges from this contributor
			edgesToMake = collaborator.getCollaboratorsAsArray();
			
			// loop through the edges associated with this collaborator
			for(int i = 0; i < edgesToMake.length; i++) {
			
				if(nodes.contains(edgesToMake[i]) == false) {
					
					// add a node as we can't have an edge to a node that doesn't exist
					node = xmlDoc.createElement("node");
					node.setAttribute("id", edgesToMake[i]);
					graph.appendChild(node);
				
					// store a reference to this contributor so we don't do a node for them
					nodes.add(edgesToMake[i]);
				}
				
				// do not add self referential edges
				if(collaborator.getId().equals(edgesToMake[i]) == false) {
				
					// check to see if an edge already exists
					if(edges.contains(collaborator.getId() + edgesToMake[i]) == false && edges.contains(edgesToMake[i] + collaborator.getId()) == false) {
						// add this edge
						edge = xmlDoc.createElement("edge");
						edge.setAttribute("source", collaborator.getId());
						edge.setAttribute("target", edgesToMake[i]);
					
						graph.appendChild(edge);
				
						// store a reference for this edge
						edges.add(collaborator.getId() + edgesToMake[i]);
						edges.add(edgesToMake[i] + collaborator.getId());
					}
				}
			}			
		}
		
		// turn the XML into a string
		try {
			// create a transformer 
			TransformerFactory transFactory = TransformerFactory.newInstance();
			Transformer        transformer  = transFactory.newTransformer();
			
			// set some options on the transformer
			transformer.setOutputProperty(OutputKeys.ENCODING, "utf-8");
			transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
			transformer.setOutputProperty(OutputKeys.INDENT, "no");
			
			// get a transformer and supporting classes
			//StringWriter writer = new StringWriter();
			StreamResult result = new StreamResult(printWriter);
			DOMSource    source = new DOMSource(xmlDoc);
			
			// transform the xml document into a string
			transformer.transform(source, result);
		} catch (javax.xml.transform.TransformerException ex) {
			throw new RuntimeException("Unable to create the graphml");
		}
	} // end the buildGraphml method
	
	/**
	 * A method to build and return a full edge list
	 *
	 * @param taskType the exact type of edge list to output
	 * @param printWriter the output stream to use to stream the output to the client
	 */
	public void getFullEdgeList(String taskType, PrintWriter printWriter) {
	
		// define helper variables
		String        queryString = null;
		QuerySolution row         = null;
		String        tmp         = null;
		String[]      tmps        = null;
		Integer       firstId     = 0;
		Integer       secondId    = 0;
	
		// determine what type fo query is required
		if(taskType.equals("full-edge-list-with-dups") == true || taskType.equals("full-edge-list-no-dups") == true) {
		
			queryString = "PREFIX foaf:       <http://xmlns.com/foaf/0.1/> "
						+ "PREFIX ausestage:  <http://code.google.com/p/aus-e-stage/wiki/AuseStageOntology#> "
						+ "  "
						+ "SELECT ?contributor ?givenName ?familyName ?collaborator ?collabGivenName ?collabFamilyName "
						+ "WHERE {  "
						+ "       ?contributor   foaf:givenName ?givenName ; "
						+ "                      foaf:familyName ?familyName; "
						+ "                      ausestage:hasCollaboration ?collaboration. "
						+ "       ?collaboration ausestage:collaborator ?collaborator. "
						+ "       ?collaborator  foaf:givenName ?collabGivenName; "
						+ "                      foaf:familyName ?collabFamilyName. "
						+ "       FILTER (?collaborator != ?contributor) "
						+ "}";
		} else {
		
			queryString = "PREFIX foaf:       <http://xmlns.com/foaf/0.1/> "
						+ "PREFIX ausestage:  <http://code.google.com/p/aus-e-stage/wiki/AuseStageOntology#> "
						+ "  "
						+ "SELECT ?contributor ?collaborator "
						+ "WHERE {  "
						+ "       ?contributor   ausestage:hasCollaboration ?collaboration. "
						+ "       ?collaboration ausestage:collaborator ?collaborator. "
						+ "       FILTER (?collaborator != ?contributor) "
						+ "}";
		}
		
		// get the data
		// execute the query
		ResultSet results = database.executeSparqlQuery(queryString);
		
		// output some additional information
		// add some additional information
		printWriter.print("# Data exported on: " + DateUtils.getCurrentDateAndTime() + "\n");
		printWriter.print("# Data exported by: " + database.getContextParam("systemName") + " Version: " + database.getContextParam("systemVersion") + " Build: " + database.getContextParam("buildVersion") + "\n");
		
		// output the data in the format required
		if(taskType.equals("full-edge-list-with-dups") == true) {
		
			// loop through the resultSet
			while (results.hasNext()) {
				// get a new row of data
				row = results.nextSolution();
				
				// first id
				tmp = row.get("contributor").toString().trim();
				tmps = tmp.split(":");
			
				printWriter.print(tmps[2] + "\t");
			
				// first givenName
				printWriter.print(row.get("givenName").toString().trim() + "\t");
			
				// first familyName
				printWriter.print(row.get("familyName").toString().trim() + "\t");
			
				// knows
				tmp = row.get("collaborator").toString().trim();
				tmps = tmp.split(":");
			
				printWriter.print(tmps[2] + "\t");
			
				// second GivenName
				printWriter.print(row.get("collabGivenName").toString().trim() + "\t");
			
				// second FamilyName
				printWriter.print(row.get("collabFamilyName").toString().trim() + "\n");
			}
			
		} else if(taskType.equals("full-edge-list-no-dups") == true) {
		
			// loop through the resultSet
			while (results.hasNext()) {
				// get a new row of data
				row = results.nextSolution();
				
				// first id
				tmp  = row.get("contributor").toString().trim();
				tmps = tmp.split(":");
			
				firstId = Integer.parseInt(tmps[2]);
			
				// second id
				tmp  = row.get("collaborator").toString().trim();
				tmps = tmp.split(":");
			
				secondId = Integer.parseInt(tmps[2]); 

				// determine if we need to add this line			
				if(firstId < secondId) {
				
					// output the data
					printWriter.print(firstId + "\t");
			
					// first givenName
					printWriter.print(row.get("givenName").toString().trim() + "\t");
		
					// first familyName
					printWriter.print(row.get("familyName").toString().trim() + "\t");
		
					// knows
					printWriter.print(secondId + "\t");
		
					// second GivenName
					printWriter.print(row.get("collabGivenName").toString().trim() + "\t");
		
					// second FamilyName
					printWriter.print(row.get("collabFamilyName").toString().trim() + "\n");
				}
			}			
		} else if(taskType.equals("full-edge-list-with-dups-id-only") == true) {
		
			// loop through the resultSet
			while (results.hasNext()) {
				// get a new row of data
				row = results.nextSolution();
				
				// first id
				tmp = row.get("contributor").toString().trim();
				tmps = tmp.split(":");
			
				printWriter.print(tmps[2] + "\t");
			
				// knows
				tmp = row.get("collaborator").toString().trim();
				tmps = tmp.split(":");
			
				printWriter.print(tmps[2] + "\n");
			}
			
		} else if(taskType.equals("full-edge-list-no-dups-id-only") == true) {
		
			// loop through the resultSet
			while (results.hasNext()) {
				// get a new row of data
				row = results.nextSolution();
				
				// first id
				tmp  = row.get("contributor").toString().trim();
				tmps = tmp.split(":");
			
				firstId = Integer.parseInt(tmps[2]);
			
				// second id
				tmp  = row.get("collaborator").toString().trim();
				tmps = tmp.split(":");
			
				secondId = Integer.parseInt(tmps[2]); 

				// determine if we need to add this line			
				if(firstId < secondId) {
				
					// output the data
					printWriter.print(firstId + "\t");
			
					// knows
					printWriter.print(secondId + "\n");
				}
			}
		}
		
		// play nice and tidy up
		database.tidyUp();	
	}
}