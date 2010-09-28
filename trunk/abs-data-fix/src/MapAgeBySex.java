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

// import color related classes
import java.awt.Color;


/**
 * The MapAgeBySex class is used to map an Age by Sex dataset, as prepared by the AbsAgeBySex class
 * onto a map
 */
public class MapAgeBySex extends Tasks {

	// declare private class variables
	String dataset = null;

	/**
	 * Constructor for this class
	 *
	 * @param input  the input file
	 * @param output the output file
	 * @param codes  the file with the CD code list
	 */
	public MapAgeBySex(File input, File output, File codes) {
		
		super(input, output, codes);
		
	} // end constructor
	
	/**
	 * A method to set the dataset identifier
	 *
	 * @param the identifier of the dataset to map
	 */
	public void setDataSet(String value) {
		dataset = filterString(value);
	}
	
	/**
	 * A method to map the AgeBySex dataset on a map
	 *
	 * @return true if, and only if, the task completes successfully
	 */
	public boolean doTask() {
	
		// define the colours
		//final String MALE_COLOUR   = "306EFF"; //in html "306EFF";
		//final String FEMALE_COLOUR = "F778A1"; //in html "F778A1";
		//final String TOTAL_COLOUR  = "8E35EF"; //in html "8E35EF";
		final String MALE_COLOUR   = "00FFFF"; // bright blue
		final String FEMALE_COLOUR = "FF0080"; // bright pink
		final String TOTAL_COLOUR  = "FFF600"; // cadmium yellow
		final String LINE_COLOUR   = "FFFFFFFF";
		final double DARKEN_FACTOR = 0.1;
		/*
		final String MALE_COLOUR   = "FF6E30"; //in html "306EFF";
		final String FEMALE_COLOUR = "A178F7"; //in html "F778A1";
		final String TOTAL_COLOUR  = "EF358E"; //in html "8E35EF";
		final String LINE_COLOUR   = "FFFFFFFF";
		*/
		
		// keep track of the ages used
		//String[] usedAges = new String[22]; // maximum size to hold all possible ages
	
		// check on the dataset variable
		if(dataset == null) {
			System.err.println("ERROR: The dataset variable must be set before executing this class");
			System.exit(-1);
		}
		
		// KML data 
		Document kmlDoc;
		Element  rootKmlElement;
		Node     kmlNode = null;
		Node     folderNode = null;
		Node     firstPlacemarkNode = null;
		
		// XML data
		Document xmlDoc;
		Element  rootElement;
		NodeList nodeList;
		Element  currentElement;
		
		// xPath objects
		XPath           xpath;
		XPathExpression expression;
		
		// setup the xPath objects
		xpath = XPathFactory.newInstance().newXPath();
		xpath.setNamespaceContext(new KmlNamespaceContext());
		
		// open the kml file
		try {
			// create the xml document object
			DocumentBuilderFactory kmlFactory = DocumentBuilderFactory.newInstance();
			kmlFactory.setNamespaceAware(true);
			DocumentBuilder        kmlBuilder = kmlFactory.newDocumentBuilder();
			kmlDoc  = kmlBuilder.parse(input);
			
			// get a reference to the root element of the source document
			rootKmlElement = kmlDoc.getDocumentElement();
			
		} catch(javax.xml.parsers.ParserConfigurationException ex) {
			System.err.println("ERROR: Unable to instantiate XML classes\n" + ex.toString());
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
			System.err.println("ERROR: Unable to parse the input KML document\n" + ex.toString());
			return false;
		}catch (java.io.IOException ex) {
			System.err.println("ERROR: Unable to read the input KML document\n" + ex);
			return false;
		}
				
		// open the XML file
		try {
			// create the xml document object
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder        builder = factory.newDocumentBuilder();
			xmlDoc  = builder.parse(codes);
			
			// get a reference to the root element of the source document
			rootElement = xmlDoc.getDocumentElement();
			
		} catch(javax.xml.parsers.ParserConfigurationException ex) {
			System.err.println("ERROR: Unable to instantiate XML classes\n" + ex.toString());
			return false;
		} catch(org.xml.sax.SAXParseException ex) {
			try {
				System.err.println("ERROR: Specified file isn't an XML file:\n" + codes.getCanonicalPath());
				return false;
			} catch (java.io.IOException e) {
				System.err.println("ERROR: Specified file isn't an XML file");
				return false;
			}
		} catch(org.xml.sax.SAXException ex) {
			System.err.println("ERROR: Unable to parse the input KML document\n" + ex.toString());
			return false;
		}catch (java.io.IOException ex) {
			System.err.println("ERROR: Unable to read the input KML document\n" + ex);
			return false;
		}
		
		// build a collection of placemarks
		nodeList = rootElement.getElementsByTagName("district");
		
		// loop through each of the districts
		for(int i = 0; i < nodeList.getLength(); i++) {
		
			// get the current data element
			currentElement = (Element)nodeList.item(i);
			
			// get the district identifier
			String id = currentElement.getAttribute("id");
			String styleUrl = null;
			
			// get the style Url value
			NodeList dataElements = currentElement.getChildNodes();
			
			for(int x = 0; x < dataElements.getLength(); x++) {
				Node dataElement = (Node)dataElements.item(x);
				
				if(dataset.equals("male") && dataElement.getNodeName().equals("AvgMaleAge")) {
					styleUrl = dataElement.getTextContent(); // male
				} else if (dataset.equals("female") && dataElement.getNodeName().equals("AvgFemaleAge")) {
					styleUrl = dataElement.getTextContent(); // female
				} else if(dataset.equals("total") && dataElement.getNodeName().equals("AvgTotalAge")) {
					styleUrl = dataElement.getTextContent(); // total
				}
			}
			
			// double check the style url
			if(styleUrl == null) {
				System.err.println("ERROR: Unable to determine data value for data element with id: " + id);
				return false;
			}
			
			/*
			// exploring the option of adjusting the levels of transparency
			// to match the number of actual averages
			//
			// Possibly not a good idea due to differences between datasets
			//
			// store a copy of this age if necessary
			// look for a reference to this age
			boolean foundAge = false;
			for(int x = 0; x < usedAges.length; x++) {
				if(usedAges[x]!= null && usedAges[x].equals(styleUrl)) {
					foundAge = true;
				}
			}
			
			// was it found?
			if(foundAge == false) {
				// no it wasn't so add it to the array
				foundAge = false;
				for(int x = 0; x < usedAges.length; x++) {
					if(usedAges[x] == null && foundAge == false) {
						// empty spot to populate it with the age
						foundAge = true;
						usedAges[x] = styleUrl;
					}
				}
			}
			
			*/			
			
			// process the styleUrl
			styleUrl = styleUrl.replaceAll(" ", "_");
			styleUrl = styleUrl.replaceAll("/", "");
			styleUrl = styleUrl.toLowerCase();
			styleUrl = "_" + styleUrl;
			
			// debug code
			//System.out.println("INFO: Processing placemark with id: " + id);
			
			try {
				// compile the xpath
				expression = xpath.compile("//kml:Placemark[@id=\"_" + id + "\"]");
			} catch (javax.xml.xpath.XPathExpressionException ex) {
				System.err.println("ERROR: Unable to compile the xPath expression");
				return false;
			}
			
			try {
				// get the matching placemark
				kmlNode = (Node)expression.evaluate(kmlDoc, XPathConstants.NODE);
			} catch (javax.xml.xpath.XPathExpressionException ex) {
				System.err.println("ERROR: Unable to execute the xPath expression");
				return false;
			}
			
			// check to see if something was returned
			if(kmlNode != null) {
				// a placemark was found
				NodeList childKmlNodes = kmlNode.getChildNodes();
				Node     polygonNode = null;
				
				// loop through all of the child nodes looking for the <Polygon> node
				for(int x = 0; x < childKmlNodes.getLength(); x++) {
					// get the node and determine its name
					Node childNode = (Node)childKmlNodes.item(x);
					
					if(childNode.getNodeName().equals("Polygon") || childNode.getNodeName().equals("MultiGeometry")) {
						polygonNode = childNode;
					}
				}
				
				// double check the polygon node
				if(polygonNode == null) {
					System.err.println("ERROR: Unable to locate a <Polygon> or <MultiGeometry> for Placemark with ID: " + id);
					return false;
				}
				
				// build a new style element
				Element styleElement = kmlDoc.createElement("styleUrl");
				styleElement.setTextContent(styleUrl);
				
				// add the style element
				kmlNode.insertBefore(styleElement, polygonNode);				
			
			} else {
				// no placemark found
				System.err.println("INFO: Unable to find Placemark with ID: " + id);
			}
		
		
		} // end processing loop
		
		/*
		 * Add the styles - bit of a hack here, but it will work
		 */
		String useColour = null;
		
		if(dataset.equals("male")) {
			useColour = MALE_COLOUR;
		} else if(dataset.equals("female")) {
			useColour = FEMALE_COLOUR;
		} else if(dataset.equals("total")) {
			useColour = TOTAL_COLOUR;
		}
		
		String[] ages   = {"na", "0-4 years","5-9 years","10-14 years","15-19 years","20-24 years","25-29 years","30-34 years","35-39 years","40-44 years","45-49 years","50-54 years","55-59 years","60-64 years","65-69 years","70-74 years","75-79 years","80-84 years","85-89 years","90-94 years","95-99 years","100 years and over"};
		String[] alphas = {"00","0C","17","23","2E","3A","45","51","5C","68","73","7F","8A","96","A1","AD","B8","C4","CF","DB","E6","F2"};
		
		// prepare the ages array
		for(int i = 0; i < ages.length; i++) {
			ages[i] = ages[i].replaceAll(" ", "_");
			ages[i] = "_" + ages[i];
		}
		
		// get a color object for the chosen colour
		Color colorObject = createColorObject(useColour);
		
		// Find the folder node
		
		try {
			// compile the xpath
			expression = xpath.compile("//kml:Folder[1]");
		} catch (javax.xml.xpath.XPathExpressionException ex) {
			System.err.println("ERROR: Unable to compile the xPath expression");
			return false;
		}
		
		try {
			// get the matching placemark
			folderNode = (Node)expression.evaluate(kmlDoc, XPathConstants.NODE);
		} catch (javax.xml.xpath.XPathExpressionException ex) {
			System.err.println("ERROR: Unable to execute the xPath expression");
			return false;
		}
		
		// check to see if something was returned
		if(folderNode == null) { 
			System.err.println("ERROR: Unable to locate the first Folder node in the KML");
			return false;
		}
		
		// get the first Placemark
		try {
			// compile the xpath
			expression = xpath.compile("//kml:Placemark[1]");
		} catch (javax.xml.xpath.XPathExpressionException ex) {
			System.err.println("ERROR: Unable to compile the xPath expression");
			return false;
		}
		
		try {
			// get the matching placemark
			firstPlacemarkNode = (Node)expression.evaluate(kmlDoc, XPathConstants.NODE);
		} catch (javax.xml.xpath.XPathExpressionException ex) {
			System.err.println("ERROR: Unable to execute the xPath expression");
			return false;
		}
		
		// check to see if something was returned
		if(firstPlacemarkNode == null) { 
			System.err.println("ERROR: Unable to locate the first Placemark node in the KML");
			return false;
		}
		
		
		// add the style nodes
		for(int i = 0; i < ages.length; i++) {
			// create a new style element
			Element style = kmlDoc.createElement("Style");
			
			// add the id
			style.setAttribute("id", ages[i]);
			
			// add the line style Element
			Element lineStyle = kmlDoc.createElement("LineStyle");
			style.appendChild(lineStyle);
			
			// add the line colour element
			Element colour = kmlDoc.createElement("color");
			colour.setTextContent(LINE_COLOUR);
			lineStyle.appendChild(colour);
			
			// add the polystyle element
			Element polyStyle = kmlDoc.createElement("PolyStyle");
			style.appendChild(polyStyle);
			
			// add the colour element
			colour = kmlDoc.createElement("color");
			//colour.setTextContent(alphas[i] + useColour);
			
			// use the colour object to determine the colour,
			// and ensure the N/A colour style is fully transparent
			// use a 50% transparency by default for the coloured areas
			if(i == 0) {
				colour.setTextContent("00FFFFFF");
			} else {
				colour.setTextContent("BE" + colorObjectToHTML(colorObject, true)); //50% transparency 7F / 75% transparency BE
				colorObject = darkerColour(colorObject, DARKEN_FACTOR);
			}
			
			polyStyle.appendChild(colour);
			
			// add this element into the tree
			folderNode.insertBefore(style, firstPlacemarkNode);
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
			DOMSource    source = new DOMSource(kmlDoc);
			
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


		
		// if we get this far everything worked ok
		return true;
	
	} // end doTask method

} // end class definition