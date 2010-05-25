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

// import XPath related classes
import javax.xml.xpath.*;

/**
 * The AppendCDInfo class is used to append collection district information
 * to the ABS data file. The HTML element for each collection district is updated
 * with the collection district code, the Statistical Local Area code and name, as well
 * as the Local Government Area code and name.
 */
public class AppendCDInfo extends Tasks {

	/**
	 * Constructor for this class
	 *
	 * @param input  the input file
	 * @param output the output file
	 * @param codes  the file with the CD code list
	 */
	public AppendCDInfo(File input, File output, File codes) {
		
		super(input, output, codes);
		
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
		// file manipulation
		BufferedReader inputReader = null;
		String[] dataElems;
		String   dataLine;
		
		// data format 
		final int ELEM_LENGTH = 6;
		
		// counters
		int lineCount = 1;
		
		// XML data 
		Document xmlDoc;
		Element  rootElement;
		
		// xPath related variables
		XPath xPath;
		String xPathQuery = "/ABSData/district[@id=\"{id}\"]/html";
		
		// data update variables
		String newData = "<ul>\n<li>Collection District Code: {id}</li>\n<li>Statistical Local Area Code: {slac}</li>\n<li>Statistical Local Area Name: {slan}</li>\n<li>Local Government Area Code: {lgac}</li>\n<li>Local Government Area Name: {lgan}</li>\n</ul>\n";
	
		// open the codes file
		try {
			// open the log file
			inputReader = new BufferedReader(new FileReader(codes));
		} catch(FileNotFoundException ex) {
			System.err.println("ERROR: Unable to open specified file for reading");
			return false;
		}
		
		// open the data file
		try {
			// create the xml document object
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder        builder = factory.newDocumentBuilder();
			xmlDoc  = builder.parse(input);
						
			// normalize the document
			//xmlDoc.normalizeDocument();
			
			// get a reference to the root element of the source document
			//rootElement = xmlDoc.getDocumentElement();
			
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
		
		// build the xpath objects
		try {
			XPathFactory xPathFactory = XPathFactory.newInstance();
			xPath = xPathFactory.newXPath();
		} catch(RuntimeException ex) {
			System.err.println("ERROR: Unable to instantiate XPath classes\n" + ex.toString());
			return false;
		}
		
		// read the data
		try {
		
			// loop through the file line by line
			while((dataLine = inputReader.readLine()) != null) {
			
				// split the line into elements
				dataElems = dataLine.split(",");
				
				// check on the number of elements
				if(dataElems.length != ELEM_LENGTH) {
					System.err.println("ERROR: Unexected data element length, expected: " + ELEM_LENGTH + " got: " + dataElems.length + " at line: " + lineCount);
					return false;
				}
				
				// remove double quotes from all of the data elements
				for(int i = 0; i < dataElems.length; i++) {
					dataElems[i] = dataElems[i].replaceAll("\"", "");
				}
				
				// get the element with this id
				try {
					Node foundNode = (Node)xPath.evaluate(xPathQuery.replace("{id}", dataElems[1]), xmlDoc, XPathConstants.NODE);
					
					// check to see if something was found
					if(foundNode == null) {
						// nothing was found
						System.out.println("INFO: Unable to locate data element with id: " + dataElems[1]);
					} else {
						// a node was found so update it
						//System.out.println("INFO: Updating node with id: " + dataElems[1]);
						
						// get the cdata node
						Node cdata = foundNode.getFirstChild();
						
						// get the content of the node
						String html = cdata.getTextContent();
						
						// add the new data
						String toAppend = newData.replace("{id}", dataElems[1]); // cd code
						toAppend = toAppend.replace("{slac}", dataElems[2]); // sla main code
						toAppend = toAppend.replace("{slan}", dataElems[3]); // sla name
						toAppend = toAppend.replace("{lgac}", dataElems[4]); // lga code
						toAppend = toAppend.replace("{lgan}", dataElems[5]); // lga name
						
						// append the data
						html = toAppend + html;
						
						// put the ammended data back
						cdata.setTextContent(html);												
					}
					
				} catch (javax.xml.xpath.XPathExpressionException ex) {
					System.err.println("ERROR: Unable to execute xpath.\n" + ex.toString());
					return false;
				}				
			}
			
			// close the file containing the codes
			inputReader.close();
			
		} catch(java.io.IOException ex) {
			System.err.println("ERROR: Unable to read from the specified file");
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

} // end class definition
