/*
 * This file is part of the AusStage ABS Data Fix App
 *
 * The AusStage ABS Data Fix App is free software: you can 
 * redistribute it and/or modify it under the terms of the GNU General 
 * Public License as published by the Free Software Foundation, either 
 * version 3 of the License, or (at your option) any later version.
 *
 * The AusStage ABS Data Fix App is distributed in the hope 
 * that it will be useful, but WITHOUT ANY WARRANTY; without even the 
 * implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  
 * See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with the AusStage ABS Data Fix App.  
 * If not, see <http://www.gnu.org/licenses/>.
*/

// import additional packages
import java.io.*;
import java.util.*;

// import DOM related classes
import org.w3c.dom.*;
import javax.xml.parsers.*; 
import javax.xml.transform.*; 
import javax.xml.transform.dom.*; 
import javax.xml.transform.stream.*;

// import third party classes
import org.apache.commons.lang.StringEscapeUtils;

/**
 * The PrepareKML class is used to prepare a KML file for later processing. This includes
 * deleting any existing style information, updating the description element for each placemark
 */
public class PrepareKML extends Tasks {

	// define the basic description
	final String DESCRIPTION_TEMPLATE = "<iframe width=\"450px\" height=\"400px\" scrolling=\"auto\" src=\"http://beta.ausstage.edu.au/mapping/xmldata?type=abs-data&id={id}\"</iframe>";

	/**
	 * Constructor for this class
	 *
	 * @param input  the input file
	 * @param output the output file
	 */
	public PrepareKML(File input, File output) {
		
		super(input, output);
		
	} // end constructor
	
	/**
	 * A method to append the collection district information into the 
	 * ABS data XML file for use by the AusStage Mapping Service
	 *
	 * @return true if, and only if, the task completes successfully
	 */
	public boolean doTask() {
	
		/*
		 * declare helper variables
		 */
		
		// XML data 
		Document xmlDoc;
		Element  rootElement;
		
		
		// open the data file
		try {
			// create the xml document object
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder        builder = factory.newDocumentBuilder();
			xmlDoc  = builder.parse(input);
						
			// normalize the document
			//xmlDoc.normalizeDocument();
			
			// get a reference to the root element of the source document
			rootElement = xmlDoc.getDocumentElement();
			
		} catch(javax.xml.parsers.ParserConfigurationException ex) {
			System.err.println("ERROR: Unable to instantiate XML classes\n" + ex.toString());
			return false;
		} catch(org.xml.sax.SAXException ex) {
			System.err.println("ERROR: Unable to parse the input document\n" + ex.toString());
			return false;
		}catch (java.io.IOException ex) {
			System.err.println("ERROR: Unable to read the input xml file\n" + ex);
			return false;
		}
		
		// delete any style nodes
		deleteNodes(xmlDoc, "Style");
		
		// delete any name nodes
		deleteNodes(xmlDoc, "name");
		
		// delete any styleUrl nodes
		deleteNodes(xmlDoc, "styleUrl");
		
		// update the placemarks
		boolean status = updatePlacemarks(xmlDoc);
		
		if(status == false) {
			System.err.println("ERROR: An error occured while processing the Placemark nodes");
			return false;
		}
		
		// delete empty text nodes
		status = deleteEmptyNodes(xmlDoc);
		
		if(status == false) {
			System.err.println("ERROR: An error occured while processing empty text nodes");
			return false;
		}
		
		
		// write the output file
		// output the new document
		try {
		
			// create a transformer 
			TransformerFactory transFactory = TransformerFactory.newInstance();
			Transformer        transformer  = transFactory.newTransformer();
			
			// set some options on the transformer
			transformer.setOutputProperty(OutputKeys.ENCODING, "utf-8");
			transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");

			// get the supporting classes for the transformer
			FileWriter writer = new FileWriter(output);
			StreamResult result = new StreamResult(writer);
			DOMSource    source = new DOMSource(xmlDoc);
			
			// transform the xml document into a string
			transformer.transform(source, result);
			
			// close the output file
			writer.close();
			
		} catch(javax.xml.transform.TransformerException e) {
			System.err.println("ERROR: Unable to transform xml for output\n" + e.toString());
			return false;
		}catch (java.io.IOException ex) {
			System.err.println("ERROR: Unable to write xml file\n" + ex.toString());
			return false;
		}
	
		// if we get this far, everything went ok
		return true;
	} // end doTask method
	
	/**
	 * A method to delete all nodes with the specified name
	 *
	 * @param xmlDoc the DOM object containing the XML
	 * @param nodeName the name of the nodes to remove
	 */
	private void deleteNodes(Document xmlDoc, String nodeName) {
	
		// declare helper variables
		Element rootElement;
		NodeList foundNodes;
		Node currentNode;
		Node parentNode;
		
		// store collections of nodes
		Set<Node> targetNodes = new HashSet<Node>();
		Iterator  targetIterator;
	
		// find nodes with the specified name
		rootElement = xmlDoc.getDocumentElement();
		foundNodes = rootElement.getElementsByTagName(nodeName);
		
		if(foundNodes.getLength() > 0) {
			System.out.println("INFO: Deleting "  + foundNodes.getLength() + " <" + nodeName + "> nodes");
			
			// loop through all of the nodes
			for(int i = 0; i < foundNodes.getLength(); i++) {
			
				// get the current node in this list
				currentNode = (Node)foundNodes.item(i);
				
				// store a reference to this node as NodeLists are "live"
				targetNodes.add(currentNode);
			}
			
			// delete these nodes
			targetIterator = targetNodes.iterator();
			
			while(targetIterator.hasNext()) {
			
				// get the node
				currentNode = (Node)targetIterator.next();
				
				// delete the text node after this node, to stop a blank line from appearing
				// in the output
				while(currentNode.hasChildNodes()) {
					currentNode.removeChild(currentNode.getFirstChild());
				}
				
				// get the parent node
				parentNode = currentNode.getParentNode();
				
				// delete this child node
				parentNode.removeChild(currentNode);
			}
				
		} else {
			System.out.println("INFO: No <" + nodeName + "> Nodes to delete");
		}	
	} // end deleteNodes method
	
	/**
	 * A method to fix the placemark nodes by updating the description tag
	 * and adding an id attribute that contains the collection district code
	 *
	 * @param xmlDoc the DOM object containing the XML
	 *
	 * @return true if, and only if, everything went as expected
	 */
	private boolean updatePlacemarks(Document xmlDoc) {
	
		// declare helper variables
		Element rootElement;
		NodeList foundNodes;
		NodeList childNodes;
		Element  currentElement;
		Node     childNode;
		Element descriptionElement;
		CDATASection descriptionCDATA;
		String oldDescription;
		
		// find nodes with the specified name
		rootElement = xmlDoc.getDocumentElement();
		foundNodes = rootElement.getElementsByTagName("Placemark");
		
		if(foundNodes.getLength() > 0) {
			System.out.println("INFO: Updating "  + foundNodes.getLength() + " <Placemark> nodes");
			
			// loop through all of the nodes
			for(int i = 0; i < foundNodes.getLength(); i++) {
			
				// get the current node in this list
				currentElement = (Element)foundNodes.item(i);
				
				// get all the child nodes
				childNodes = currentElement.getChildNodes();
				
				// reset the description element variable
				descriptionElement = null;
				
				// loop through the children looking for the description element
				for(int x = 0; x < childNodes.getLength(); x++) {
					
					// get the current element in the list
					childNode = childNodes.item(x);
					
					if(childNode.getNodeName().equals("description")) {
						descriptionElement = (Element)childNode;
					}
				}
				
				// check the description element variable
				if(descriptionElement == null) {
					System.err.println("INFO: Unable to locate the description element of Placemark node number: " + i);
					return false;
				}
				
				// get the children of the description element
				childNodes = descriptionElement.getChildNodes();
				
				// reset the cdata section
				descriptionCDATA = null;
				
				// loop through the children looking for the description element
				for(int x = 0; x < childNodes.getLength(); x++) {
					
					// get the current element in the list
					childNode = childNodes.item(x);
					
					if(childNode.getNodeType() == Node.CDATA_SECTION_NODE) {
						descriptionCDATA = (CDATASection)childNode;
					}
				}
				
				// double check the descriptionCDATA variable
				if(descriptionCDATA == null) {
					System.err.println("INFO: Unable to locate the text of the description element of Placemark node number: " + i);
					return false;
				}
				
				// get the text stored in the CDATA section
				oldDescription = descriptionCDATA.getData();
				
				// fix the description data as best we can
				oldDescription = oldDescription.replaceAll(" ", ""); // remove spaces
				oldDescription = StringEscapeUtils.unescapeHtml(oldDescription); // unescape any html entities
				
				// find the first and second <i> tags, these should be encapsulating the collection district code
				int firstTag  = oldDescription.indexOf("<i>");
				int secondTag = oldDescription.indexOf("</i>");
				
				if(firstTag == -1 || secondTag == -1) {
					System.err.println("INFO: Unable to locate the <i> tags encapsulating the collection district code at Placemark node number: " + i);
					return false;
				}
				
				// get the collection district code
				String code = oldDescription.substring(firstTag + 3, secondTag);
				
				// add the id to this placemark
				currentElement.setAttribute("id", "_" + code);
				
				// update the description
				descriptionCDATA.setData(DESCRIPTION_TEMPLATE.replace("{id}", code));
				
				// update the name
				Element nameElement = xmlDoc.createElement("name");
				nameElement.setTextContent("Collection District: " + code);
				
				// add the name element
				currentElement.insertBefore(nameElement, descriptionElement);				
			}			
			
		} else {
			System.out.println("INFO: No Placemark nodes found");
			return false;
		}
		
		// everything went as expected
		return true;
		
	} // end updatePlacemarks method
	
	/**
	 * A method to delete empty text nodes
	 *
	 * @param xmlDoc the DOM object containing the XML
	 *
	 * @return true if, and only if, everything went as expected
	 */
	private boolean deleteEmptyNodes(Document xmlDoc) {
	
		// declare helper variables
		Element rootElement;
		NodeList foundNodes;
		NodeList childNodes;
		Node     childNode;
		Node currentNode;
		Node parentNode;
		
		// store collections of nodes
		Set<Node> targetNodes = new HashSet<Node>();
		Iterator  targetIterator;
		
		// delete the empty text nodes who are children of the Folder node
		String nodeName = "Folder";
	
		// find the first node that we know will have empty text nodes
		rootElement = xmlDoc.getDocumentElement();
		foundNodes = rootElement.getElementsByTagName(nodeName);
		
		if(foundNodes.getLength() > 0) {
			
			// loop through all of the nodes
			for(int i = 0; i < foundNodes.getLength(); i++) {
			
				// get the current node in this list
				currentNode = (Node)foundNodes.item(i);
				
				// get the child nodes
				childNodes = currentNode.getChildNodes();
				
				// loop through all of the child nodes looking for text nodes
				for(int x = 0; x < childNodes.getLength(); x++) {
				
					childNode = (Node)childNodes.item(x);
					
					// see if this is a text node
					if(childNode.getNodeType() == Node.TEXT_NODE) {
						targetNodes.add(childNode);
					}
				} // end child node loop
			} // end found node loop
		} // end if for found nodes
		
		// delete the empty text nodes who are children of the Placemark node
		nodeName = "Placemark";
		foundNodes = rootElement.getElementsByTagName(nodeName);
		
		if(foundNodes.getLength() > 0) {
			
			// loop through all of the nodes
			for(int i = 0; i < foundNodes.getLength(); i++) {
			
				// get the current node in this list
				currentNode = (Node)foundNodes.item(i);
				
				// get the child nodes
				childNodes = currentNode.getChildNodes();
				
				// loop through all of the child nodes looking for text nodes
				for(int x = 0; x < childNodes.getLength(); x++) {
				
					childNode = (Node)childNodes.item(x);
					
					// see if this is a text node
					if(childNode.getNodeType() == Node.TEXT_NODE) {
						targetNodes.add(childNode);
					}
				} // end child node loop
			} // end found node loop
		} // end if for found nodes
		
		// delete these nodes
		targetIterator = targetNodes.iterator();
		
		while(targetIterator.hasNext()) {
		
			// get the node
			currentNode = (Node)targetIterator.next();
			
			// get the parent node
			parentNode = currentNode.getParentNode();
			
			// delete this child node
			parentNode.removeChild(currentNode);
		}
	
		return true;		
	
	} // end deleteEmptyNodes

} // end class definition
