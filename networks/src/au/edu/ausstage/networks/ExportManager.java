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
	 * @param id         the unique id of the contributor
	 * @param formatType the required format of the data
	 * @param degrees    the required number of degrees of separation
	 *
	 * @return           the results of the lookup
	 */
	public String getSimpleNetwork(String id, String formatType, String degrees) {
	
		// define helper variables
		// collection of collaborators
		java.util.TreeMap<Integer, Collaborator> collaborators = new java.util.TreeMap<Integer, Collaborator>();
		
		// collection of IDs yet to be processed
		TreeSet<String> idsToGet = new TreeSet<String>();       // collection of IDs that we need to get this run
		TreeSet<String> newIds   = new TreeSet<String>();       // collection of IDs that we need to get in the next run
	
		// define other helper variables
		QuerySolution row             = null;
		Collaborator  collaborator    = null;
		int           degreesFollowed = 1;
		int           degreesToFollow = Integer.parseInt(degrees);
		String        queryToExecute  = null;
		Collection    values          = null;
		Iterator      iterator        = null;
	
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
			
			// add to the list of ids
			idsToGet.add(row.get("collaborator").toString());
		}
		
		// play nice and tidy up
		database.tidyUp();

		
		//loop through the degrees to follow
		while(degreesFollowed < degreesToFollow) {
		
			// get the list of ids to process
			iterator = idsToGet.iterator();
			
			while(iterator.hasNext()) { 
				id = (String)iterator.next();
				
				// only do collaborators we've not seen yet
				if(collaborators.containsKey(new Integer(AusStageURI.getId(id))) == false) {
				
					// build a new collaborator
					id = AusStageURI.getId(id);
					collaborator = new Collaborator(id);
					collaborators.put(Integer.parseInt(id), collaborator);
				
					// build a new query
					queryToExecute = sparqlQuery.replaceAll("@", "<" + AusStageURI.getContributorURI(id) + ">");
		
					// execute the query
					results = database.executeSparqlQuery(queryToExecute);
					
					// add this degree contributors
					while (results.hasNext()) {
						// loop though the resulset
						// get a new row of data
						row = results.nextSolution();
			
						// add the collaboration
						collaborator.addCollaborator(AusStageURI.getId(row.get("collaborator").toString()));
			
						// add to the list of ids
						newIds.add(row.get("collaborator").toString());
					}
					
					//play nice and tidy up
					database.tidyUp();
					
				}
			}
			
			// finished with this degree to reset variables
			idsToGet.clear();
			idsToGet.addAll(newIds);
			newIds.clear();	
			
			// increment the count
			degreesFollowed++;
		}
		
		// dataset is built so time to do something with it
		String dataString = null;
		
		if(formatType.equals("graphml") == true) {
			dataString = buildGraphml(collaborators);
		}
		
		// return the graph data
		return dataString;
		
	
	} // end getSimpleNetwork method
	
	/**
	 * A method to output the export in the graphml format
	 *
	 * @param collaborators the list of collaborators
	 * @return              the XML string representing the graphml
	 */
	private String buildGraphml(java.util.TreeMap<Integer, Collaborator> collaborators) {
		
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
		rootElement.appendChild(xmlDoc.createComment("Graph generated by: " + database.getContextParam("systemName") + " " + database.getContextParam("systemVersion") + " " + database.getContextParam("buildVersion")));
				
		// add the graph element
		graph = xmlDoc.createElement("graph");
		graph.setAttribute("id", "ausstage-graph");
		graph.setAttribute("edgedefault", "undirected");
		rootElement.appendChild(graph);
		
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
			StringWriter writer = new StringWriter();
			StreamResult result = new StreamResult(writer);
			DOMSource    source = new DOMSource(xmlDoc);
			
			// transform the xml document into a string
			transformer.transform(source, result);
			return writer.toString();
		} catch (javax.xml.transform.TransformerException ex) {
			return null;
		}
	} // end the buildGraphml method

}
