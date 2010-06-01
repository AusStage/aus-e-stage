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

/**
 * The DataBuilder task is a task that takes an intermediatory XML file
 * and constructs the data XML file that will be used by the AusStage Mapping Service
 * to populate the infoWindows in Google Earth
 */
public class DataBuilder extends Tasks {

	/**
	 * Constructor for this class
	 *
	 * @param input  the input file
	 * @param output the output file
	 */
	public DataBuilder(File input, File output) {
		
		super(input, output);
		
	} // end constructor

	
	/**
	 * A method to build the basic ABS data XML file for use
	 * by the AusStage Mapping Service
	 *
	 * @return true if, and only if, the task completes successfully
	 */
	public boolean doTask() {
	
		/*
		 * declare helper variables
		 */
		 
		// new document		
		Document xmlDoc;
		Element  rootElement;
		
		// source document
		Document sourceDocument;
		Element  sourceRootElement;
		NodeList sourceNodes;
	
		try {
			// create the xml document object
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder        builder = factory.newDocumentBuilder();
			xmlDoc  = builder.newDocument();
			
			// add the root element
			rootElement = xmlDoc.createElement(ROOT_ELEMENT_NAME);
			xmlDoc.appendChild(rootElement);
			
			// read in the data to process
			sourceDocument = builder.parse(input);
			
			// normalize the document
			sourceDocument.normalizeDocument();
			
			// get a reference to the root element of the source document
			sourceRootElement = sourceDocument.getDocumentElement();
			
			// get a list of nodes to process
			sourceNodes = sourceRootElement.getChildNodes();		
			
		} catch(javax.xml.parsers.ParserConfigurationException ex) {
			System.err.println("ERROR: Unable to build xml document\n" + ex.toString());
			return false;
		} catch(org.xml.sax.SAXParseException ex) {
			try {
				System.err.println("ERROR: Specified file isn't an XML file:\n" + input.getCanonicalPath());
				return false;
			} catch (java.io.IOException e) {
				System.err.println("ERROR: Specified file isn't an XML file");
				return false;
			}
		} catch(org.xml.sax.SAXException ex) {
			System.err.println("ERROR: Unable to parse the input document\n" + ex.toString());
			return false;
		}catch (java.io.IOException ex) {
			System.err.println("ERROR: Unable to read the input xml file\n" + ex);
			return false;
		}
		
		// process each of the nodes in turn and build the new document
		for(int i = 0; i < sourceNodes.getLength(); i++) {
		
			// get the node at this position
			Node sourceNode = sourceNodes.item(i);
			
			// check on this type of node
			if(sourceNode.getNodeType() == Node.ELEMENT_NODE) {
			
				// add a new element to the new document
				Element newElement = xmlDoc.createElement(DISTRICT_ELEMENT_NAME);
				rootElement.appendChild(newElement);
			
				// get the id attribute of this source node
				NamedNodeMap sourceAttributes = sourceNode.getAttributes();
				Node         sourceAttribute = sourceAttributes.getNamedItem("id");
			
				// set the attribute of the new node
				newElement.setAttribute("id", sourceAttribute.getNodeValue());
			
				// get the list of child nodes for this node
				NodeList childNodes = sourceNode.getChildNodes();
				
				// loop through the child nodes
				for(int x = 0; x < childNodes.getLength(); x++) {
					
					// get the node at this position
					Node childNode = childNodes.item(x);
					
					// process only elements
					if(childNode.getNodeType() == Node.ELEMENT_NODE) {
						// check on the name of this element and act accordingly
						if(childNode.getNodeName().equals("table") == false) {
							// use this node as a source of attributes
							newElement.setAttribute(childNode.getNodeName(), childNode.getTextContent());
						} else {
							// this is the table so just copy it from the source document to the new document
							Node nodeCopy = xmlDoc.importNode(childNode, true); // import the node into the new document
							nodeCopy = xmlDoc.renameNode(nodeCopy, "", "html"); // rename the node
							newElement.appendChild(nodeCopy);                   // add the node to the tree
						}
					}
				}
			}
		}
		
		// output the new document
		try {
			// create a transformer 
			TransformerFactory transFactory = TransformerFactory.newInstance();
			Transformer        transformer  = transFactory.newTransformer();
			
			// set some options on the transformer
			transformer.setOutputProperty(OutputKeys.ENCODING, "utf-8");
			transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");

			// get a transformer and supporting classes
			StringWriter writer = new StringWriter();
			StreamResult result = new StreamResult(writer);
			DOMSource    source = new DOMSource(xmlDoc);
			
			// transform the xml document into a string
			transformer.transform(source, result);
			
			// open the output file
			FileWriter outputWriter = new FileWriter(output);
			outputWriter.write(writer.toString());
			outputWriter.close();
			
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

} // end class definition
