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
 * The PrepareKML class is used to prepare a KML file for later processing. This includes
 * deleting any existing style information, updating the description element for each placemark
 */
public class PrepareKML extends Tasks {

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
		NodeList foundNodes;
		Node currentNode;
		Node parentNode;
		
		// store collections of nodes
		Set<Node> targetNodes = new HashSet<Node>();
		Iterator  targetIterator;
		
		// open the data file
		try {
			// create the xml document object
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder        builder = factory.newDocumentBuilder();
			xmlDoc  = builder.parse(input);
						
			// normalize the document
			xmlDoc.normalizeDocument();
			
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
		
		// delete any style elements
		foundNodes = rootElement.getElementsByTagName("Style");
		
		if(foundNodes.getLength() > 0) {
			System.out.println("INFO: Deleting "  + foundNodes.getLength() + " <Style> nodes");
			
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
			
			// reset the objects
			parentNode = null;
			currentNode = null;
			targetIterator = null;
			targetNodes.clear();
				
		} else {
			System.out.println("INFO: No Style Nodes to delete");
		}
		
		// write the output file
		// output the new document
		try {
		
			// normalise the document before writing
			xmlDoc.normalizeDocument();
		
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
