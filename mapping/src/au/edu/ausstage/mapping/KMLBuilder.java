/*
 * This file is part of the AusStage Mapping Service
 *
 * The AusStage Mapping Service is free software: you can redistribute
 * it and/or modify it under the terms of the GNU General Public License 
 * as published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * The AusStage Mapping Service is distributed in the hope that it will 
 * be useful, but WITHOUT ANY WARRANTY; without even the implied warranty 
 * of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with the AusStage Mapping Service.  
 * If not, see <http://www.gnu.org/licenses/>.
*/

package au.edu.ausstage.mapping;

// import additional libraries
import java.io.*;
import org.w3c.dom.*;
import javax.xml.parsers.*; 
import javax.xml.transform.*; 
import javax.xml.transform.dom.*; 
import javax.xml.transform.stream.*;

/**
 * A class used to build a KML file
 */
public class KMLBuilder {

	// declare private class variables
	private DocumentBuilderFactory factory;
	private	DocumentBuilder        builder;
	private DOMImplementation      domImpl;
	private Document               xmlDoc;
	private Element                rootElement;
	private Element                rootFolder = null;
	
	/**
	 * Class constructor, initialise the KML DOM
	 */
	public KMLBuilder() {
	
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
		xmlDoc = domImpl.createDocument("http://www.opengis.net/kml/2.2", "kml", null);
		
		// get the root element
		rootElement = this.xmlDoc.getDocumentElement();
		
		// add atom namespace to the root element
		rootElement.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns:atom", "http://www.w3.org/2005/Atom");
		
		// add schema namespace to the root element
		rootElement.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns:xsi", "http://www.w3.org/2001/XMLSchema-instance");
		
		// add reference to the kml schema
		rootElement.setAttribute("xsi:schemaLocation", "http://www.opengis.net/kml/2.2 http://schemas.opengis.net/kml/2.2.0/ogckml22.xsd");
		
	} // end the constructor
	
	/**
	 * A method to create an element to be added to the KML
	 * 
	 * @param name the name of the element
	 *
	 * @return    the newly constructed element
	 */
	public Element createElement(String name) {
	
		// check the parameters
		if(name == null) {
			throw new IllegalArgumentException("The name of the element must be specified");
		}
		
		name = name.trim();
		
		if(name.equals("") == true) {
			throw new IllegalArgumentException("The name of the element must be specified");
		}
		
		// create the element and return it	
		return xmlDoc.createElement(name);
		
	} // end createElement method
	
	/**
	 * A method to create a CDATASection element to be added to this KML export file
	 *
	 * @param content the content of this element
	 *
	 * @return       the CDATASection element
	 */
	public CDATASection createCDATASection(String content) {
		
		if(content == null) {
			throw new IllegalArgumentException("The content of the CDATA Section must be specified");
		}
		
		content = content.trim();
		
		if(content.equals("") == true) {
			throw new IllegalArgumentException("The content of the CDATA Section must be specified");
		}
	
		// create the CDATA element and return it
		return xmlDoc.createCDATASection(content);
		
	} // end createCDATASection method
	
	/**
	 * Add a folder to the KML file
	 * 
	 * @param parentElement an element which is the parent folder
	 * @param folderName    the name of the folder
	 *
	 * @return the newly constructed folder
	 */
	public Element addFolder(Element parentElement, String folderName) {
	
		// check the parameters
		if(folderName == null) {
			throw new IllegalArgumentException("The name of the folder must be specified");
		}
		
		folderName = folderName.trim();
		
		if(folderName.equals("") == true) {
			throw new IllegalArgumentException("The name of the folder must be specified");
		}
	
		// create the folder element
		Element folder = xmlDoc.createElement("Folder");
		
		// create the name element
		Element name = xmlDoc.createElement("name");
		name.setTextContent(folderName);				
		
		// add the name element to the folder
		folder.appendChild(name);
		
		// add the folder to the tree
		rootElement.appendChild(folder);

		if(parentElement == null) {
			// assume we want to add the folder to the root element
			rootElement.appendChild(folder);
		} else {
			// add this folder to the specified folder
			parentElement.appendChild(folder);
		}
		
		// return the new element
		return folder;
	 
	} // end addFolder method
	
	/**
	 * Add a folder to the root element of the KML file
	 * 
	 * @param folderName    the name of the folder
	 *
	 * @return the newly constructed folder
	 */
	public Element addFolder(String folderName) {
	
		// add the folder to the tree
		Element folder = addFolder(null, folderName);
		
		// check to see if this is the first folder
		if(rootFolder == null) {
			rootFolder = folder;
		}
		
		// return the new folder element
		return folder;
	} // end addFolder method
	
	/**
	 * A method to add a document element to a folder inside the KML file
	 *
	 * @param parentElement the parent element of the new document element
	 * @param name          the name of the document
	 *
	 * @return              the element representing this document
	 */
	public Element addDocument(Element folder, String name) {
	
		// check the parameters
		if(folder == null && rootFolder == null) {
			throw new IllegalArgumentException("The parent folder of this document must be specified, or the root folder must be created");
		}
		
		if(name == null) {
			throw new IllegalArgumentException("The name of the folder must be specified");
		}
		
		name = name.trim();
		
		if(name.equals("") == true) {
			throw new IllegalArgumentException("The name of the folder must be specified");
		}
	
		// create the document element
		Element document = xmlDoc.createElement("Document");
		
		// create the name element
		Element documentName = xmlDoc.createElement("name");
		documentName.setTextContent(name);
		
		// add the name element to the folder
		document.appendChild(documentName);
		
		// add the document to the element tree
		if(folder == null) {
			rootFolder.appendChild(document);
		} else {
			folder.appendChild(document);
		}
		
		// return this Element
		return document;
		
	} // end addDocument method
	
	
	/**
	 * Add an XML comment to the KML file
	 *
	 * @param parentElement an element which is the parent element of this comment
	 * @param content       the text of the comment
	 */
	public void addComment(Element parentElement, String content) {
	
		// check the parameters
		if(content == null) {
			throw new IllegalArgumentException("The content of the comment must be specified");
		}
		
		// trim the content
		content = content.trim();
		
		if(content.equals("") == true) {
			throw new IllegalArgumentException("The content of the comment must be specified");
		}
		
		// add the comment to the element tree		
		if(parentElement == null) {
			rootElement.appendChild(xmlDoc.createComment(content));
		} else {
			parentElement.appendChild(xmlDoc.createComment(content));
		}
		
	} // end addComment method
	
	/**
	 * Add an XML comment to the KML file as a child element of the root element
	 *
	 * @param content     the text of the comment
	 */
	public void addComment(String content) {
	
		addComment(null, content);
		
	} // end addComment method
	
	/**
	 * A method to add author information to an element
	 * such as a folder or a document
	 *
	 * @param parentElement the parent element in the DOM for this element
	 * @param name          the name of the author
	 * @param href          the href to a webpage containing more information about this map
	 */
	public void addAuthorElement(Element parentElement, String name, String href) {
	
		// check on the parameters
		if(name == null || href == null) {
			throw new IllegalArgumentException("Both the name and href parameters must be specified");
		}
		
		// trim the parameters
		name = name.trim();
		href = href.trim();
		
		if(name.equals("") == true || href.equals("") == true) {
			throw new IllegalArgumentException("Both the name and href parameters must be specified");
		}
	
		// add author information
		Element author = xmlDoc.createElement("atom:author");
		
		Element authorName = xmlDoc.createElement("atom:name");
		authorName.setTextContent(name);
		author.appendChild(authorName);
		
		// add link information
		Element link = xmlDoc.createElement("atom:link");
		link.setAttribute("href", href);
		
		if(parentElement == null) {
			// assume this is to be added to the first folder
			if(rootFolder == null) {
				throw new IllegalArgumentException("No parent element specified and the root folder doesn't exist");
			} else {
				rootFolder.appendChild(author);
				rootFolder.appendChild(link);
			}
		} else {
			// add this to the specified parent
			parentElement.appendChild(author);
			parentElement.appendChild(link);
		}
		
	} // end addAuthorInfoToFolder method
	
	/**
	 * A method to add author information to the first folder in the KML file
	 *
	 * @param name          the name of the author
	 * @param href          the href to a webpage containing more information about this map
	 */
	public void addAuthorElement(String name, String href) {
	
		addAuthorElement(null, name, href);
	
	} // end addAuthorInfo method
	
	/**
	 * A method to add a description element to the specified element
	 *
	 * @param parentElement the parent element of this description element
	 * @param content       the content of the description element
	 */
	public void addDescriptionElement(Element parentElement, String content) {
	
		// check on the pararmeters
		if(parentElement == null) {
			throw new IllegalArgumentException("A parent element must be specified");
		}
		
		if(content == null) {
			throw new IllegalArgumentException("Content for the description element must be specified");
		}
		
		content = content.trim();
		
		if(content.equals("") == true) {
			throw new IllegalArgumentException("Content for the description element must be specified");
		}
		
		// create the description element
		Element description = xmlDoc.createElement("description");
		description.setTextContent(content);
		
		// add the element as a child to the specified parent 
		parentElement.appendChild(description);
	
	} // end addDescriptionElement method
	
	/**
	 * A method to add an IconStyleElement to the specified element
	 *
	 * @param parentElement the parent element for this IconStyle
	 * @param id            the unique style identifier for this style
	 * @param href          the href (URL) for this style
	 */
	public void addIconStyle(Element parentElement, String id, String href) {
	
		// check on the parameters
		if(parentElement == null && rootFolder == null) {
			throw new IllegalArgumentException("The parent of this style element must be specified or the root folder element must exist");
		}
		
		if(id == null) {
			throw new IllegalArgumentException("The id for the style must be specified");
		}
		
		if(href == null) {
			throw new IllegalArgumentException("The href (URL) for the style must be specified");
		}
		
		id   = id.trim();
		href = href.trim();
		
		if(id.equals("") == true) {
			throw new IllegalArgumentException("The id for the style must be specified");
		}
		
		if(href.equals("") == true) {
			throw new IllegalArgumentException("The href (URL) for the style must be specified");
		}
		
		// build the IconStyle element
		Element style = xmlDoc.createElement("Style");
		
		// add the id attribute
		style.setAttribute("id", id);
		
		// add the icon style element
		Element iconStyle = xmlDoc.createElement("IconStyle");
		style.appendChild(iconStyle);
		
		// add the icon element
		Element icon = xmlDoc.createElement("Icon");
		iconStyle.appendChild(icon);
		
		// add the href element
		Element hrefElement = xmlDoc.createElement("href");
		icon.appendChild(hrefElement);
		
		// add the url to the icon
		hrefElement.setTextContent(href);
		
		// check where to add this style
		if(parentElement == null) {
			//rootFolder.insertBefore(style, rootFolder.getFirstChild());
			rootFolder.appendChild(style);
		} else {
			//parentElement.insertBefore(style, parentElement.getFirstChild());
			parentElement.appendChild(style);
		}
		
	} // end addIconStyle element
	
	/**
	 * A method to add an IconStyleElement to the root folder
	 *
	 * @param id            the unique style identifier for this style
	 * @param href          the href (URL) for this style
	 */
	public void addIconStyle(String id, String href) {
	
		addIconStyle(null, id, href);
		
	} // end addIconStyle element
	
	/**
	 * Add a placemark to the KML
	 *
	 * @param document    the document to add the placemark to
	 * @param name        the name of the placemark
	 * @param link        the link for further information
	 * @param description the description for this placemark
	 * @param style       the style for this placemark
	 * @param latitude    the latitidude for this placemark
	 * @param longitude   the longitude for this placemark
	 *
	 * @return            the Element representing this placemark
	 */
	public Element addPlacemark(Element document, String name, String link, String description, String style, String latitude, String longitude) {
	
		// check the parameters
		if(document == null) {
			throw new IllegalArgumentException("The document that holds this placemark must be specified");
		}
		
		if(name == null) {
			throw new IllegalArgumentException("The name of this placemark must be specified");
		}
		
		name = name.trim();
		
		if(name.equals("") == true) {
			throw new IllegalArgumentException("The name of this placemark must be specified");
		}
		
		if(description == null) {
			throw new IllegalArgumentException("The description of this placemark must be specified");
		}
		
		description = description.trim();
		
		if(description.equals("") == true) {
			throw new IllegalArgumentException("The description of this placemark must be specified");
		}
		
		if(style == null) {
			throw new IllegalArgumentException("The style id for use with this placemark must be specified");
		}
		
		style = style.trim();
		
		if(style.equals("") == true) {
			throw new IllegalArgumentException("The style id for use with this placemark must be specified");
		}
		
		if(latitude == null) {
			throw new IllegalArgumentException("The latitude for use with this placemark must be specified");
		}
		
		latitude = latitude.trim();
		
		if(latitude.equals("") == true) {
			throw new IllegalArgumentException("The latitude for use with this placemark must be specified");
		}
		
		if(longitude == null) {
			throw new IllegalArgumentException("The longitude for use with this placemark must be specified");
		}
		
		longitude = longitude.trim();
		
		if(longitude.equals("") == true) {
			throw new IllegalArgumentException("The longitude for use with this placemark must be specified");
		}
		
		// add a placemark to the document
		Element placemark = xmlDoc.createElement("Placemark");
		document.appendChild(placemark);
		
		// add the name to the placemark
		Element eventName = xmlDoc.createElement("name");
		placemark.appendChild(eventName);
		eventName.setTextContent(name);

		// add a link element
		Element eventLink = xmlDoc.createElement("atom:link");
		eventLink.setAttribute("href", link);
		placemark.appendChild(eventLink);
		
		// add the description to the placemark
		Element descriptionElement = xmlDoc.createElement("description");
		placemark.appendChild(descriptionElement);
		
		// create the CDATASection to hold the html
		CDATASection cdata = xmlDoc.createCDATASection(description);
		descriptionElement.appendChild(cdata);
		
		// add the style information
		Element styleElement = xmlDoc.createElement("styleUrl");
		placemark.appendChild(styleElement);
		styleElement.setTextContent(style);
		
		// create the point element
		Element point = xmlDoc.createElement("Point");
		placemark.appendChild(point);
	
		// add the coordinates
		Element coordinates = xmlDoc.createElement("coordinates");
		point.appendChild(coordinates);
		coordinates.setTextContent(latitude + "," + longitude);
		
		// return the placemark
		return placemark;	
	
	} // end addPlacemark method
	
	/**
	 * A function to add the timespan element to a placemark
	 *
	 * @param placemark the placemark to add the timespan element to
	 * @param begin     the date the event began
	 * @param end       the date the event ended
	 */
	public void addTimeSpan(Element placemark, String begin, String end) {
	
		// check the parameters
		if(placemark == null) {
			throw new IllegalArgumentException("A placemark must be specified");
		}
		
		if(begin == null) {
			throw new IllegalArgumentException("The first date/time when the event occured must be defined");
		}
		
		begin = begin.trim();
		end   = end.trim();
		
		if(begin.equals("") == true) {
			throw new IllegalArgumentException("The first date/time when the event occured must be defined");
		}
	
		// declare helper variables
		Node styleElement = null;
		Node childElement = null;
	
		// locate the style Element child
		NodeList children = placemark.getChildNodes();
		
		for(int i = 0; i < children.getLength(); i++) {
			
			// get the child at this position
			childElement = children.item(i);
			
			// compare the name of the node to the one for the style
			if(childElement.getNodeName().equals("styleUrl")) {
				styleElement = childElement;
			}
		}
		
		// check to make sure we found the right element
		if(styleElement == null) {
			throw new RuntimeException("Unable to locate the style element");
		}
		
		// build the timepan element
		Element timeSpan  = xmlDoc.createElement("TimeSpan");
			
		// add the begin element
		Element timeBegin = xmlDoc.createElement("begin");
		timeBegin.setTextContent(begin);
		timeSpan.appendChild(timeBegin);
		
		// add the end element
		Element timeEnd = xmlDoc.createElement("end");
		
		if(end.length() > 0) {
			// set end element to actual known end						
			timeEnd.setTextContent(end);
		} else {
			// set end element to same as begin
			timeEnd.setTextContent(begin);
		}
		
		// add the end element
		timeSpan.appendChild(timeEnd);
		
		// add the element to the placemark
		placemark.insertBefore(timeSpan, styleElement);	
	
	} // end addTimeSpan element
	
	/**
	 * A method to add a line style element
	 *
	 * @param parentElement the parent element (document or folder) for this line style
	 * @param styleId    the id attribute for this style
	 * @param lineColour the colour of this line
	 * @param lineWidth  the width of the line
	 */
	public void addLineStyle(Element parentElement, String styleId, String lineColour, String lineWidth) {
	
		// check the parameters
		if(parentElement == null && rootFolder == null) {
			throw new IllegalArgumentException("The parent of this line style element must be specified or the root folder element must exist");
		}
		
		if(styleId == null) {
			throw new IllegalArgumentException("The id for this style must be defined");
		}
		
		if(lineColour == null) {
			throw new IllegalArgumentException("The colour of the line for this style must be defined");
		}
		
		if(lineWidth == null) {
			throw new IllegalArgumentException("The width of the line for this style must be defined");
		}
		
		styleId    = styleId.trim();
		lineColour = lineColour.trim();
		lineWidth  = lineWidth.trim();
		
		if(styleId.equals("") == true) {
			throw new IllegalArgumentException("The id for this style must be defined");
		}
		
		if(lineColour.equals("") == true) {
			throw new IllegalArgumentException("The colour of the line for this style must be defined");
		}
		
		if(lineWidth.equals("") == true) {
			throw new IllegalArgumentException("The width of the line for this style must be defined");
		}
		
		// build the style element
		Element style = xmlDoc.createElement("Style");
		
		// add the id attribute
		style.setAttribute("id", styleId);
		
		// add the line style element
		Element lineStyle = xmlDoc.createElement("LineStyle");
		style.appendChild(lineStyle);
		
		// add the colour element
		Element color = xmlDoc.createElement("color");
		lineStyle.appendChild(color);
		color.setTextContent(lineColour);
		
		// add the width element
		Element width = xmlDoc.createElement("width");
		lineStyle.appendChild(width);
		width.setTextContent(lineWidth);
		
		// add the element to the tree
		// check where to add this style
		if(parentElement == null) {
			rootFolder.insertBefore(style, rootFolder.getLastChild());
			//rootFolder.appendChild(style);
		} else {
			parentElement.insertBefore(style, parentElement.getLastChild());
			//parentElement.appendChild(style);
		}
		
	} // end addLineStyle method
	
	/**
	 * A method to add a trajectory line to the map
	 *
	 * @param parentElement the document to add this trajectory line to
	 * @param name          the name of this trajectory line
 	 * @param styleId       the ID of the style for this trajectory line
	 * @param startCoords   the start coordinates for this trajectory
	 * @param endCoords     the end coordinates for this trajectory
	 */
	public Element addTrajectory(Element parentElement, String name, String styleId, String startCoords, String endCoords) {
	
		// check the parameters
		if(parentElement == null) {
			throw new RuntimeException("The parent document element for this trajectory must be specified");
		}
		
		if(name == null) {
			throw new RuntimeException("The name of this trajectory must be specified");
		}
		
		if(startCoords == null) {
			throw new RuntimeException("The coordinates for the start point of this trajectory must be specified");
		}
		
		if(endCoords == null) {
			throw new RuntimeException("The coordinates for the end point of this trajectory must be specified");
		}
		
		if(styleId == null) {
			throw new RuntimeException("The id for the line style used by this trajectory must be specified");
		}
		
		name        = name.trim();
		startCoords = startCoords.trim();
		endCoords   = endCoords.trim();
		styleId     = styleId.trim();
		
		if(name.equals("") == true) {
			throw new RuntimeException("The name of this trajectory must be specified");
		}
		
		if(startCoords.equals("") == true) {
			throw new RuntimeException("The coordinates for the start point of this trajectory must be specified");
		}
		
		if(endCoords.equals("") == true) {
			throw new RuntimeException("The coordinates for the end point of this trajectory must be specified");
		}
		
		if(styleId.equals("") == true) {
			throw new RuntimeException("The id for the line style used by this trajectory must be specified");
		}
		
		// build the trajectory element
		Element placemark = xmlDoc.createElement("Placemark");
		
		Element nameElement = xmlDoc.createElement("name");
		placemark.appendChild(nameElement);
		nameElement.setTextContent(name);
		
		Element styleUrl = xmlDoc.createElement("styleUrl");
		placemark.appendChild(styleUrl);
		styleUrl.setTextContent(styleId);
		
		// add the lineString element and associated boilerplate
		Element lineString = xmlDoc.createElement("LineString");
			
		Element extrude = xmlDoc.createElement("extrude");
		extrude.setTextContent("0");
		lineString.appendChild(extrude);
			
		Element tessellate = xmlDoc.createElement("tessellate");
		tessellate.setTextContent("1");
		lineString.appendChild(tessellate);
			
		Element altitudeMode = xmlDoc.createElement("altitudeMode");
		altitudeMode.setTextContent("clampToGround");
		lineString.appendChild(altitudeMode);
			
		// add the coordinates
		Element lineCoordinates = xmlDoc.createElement("coordinates");
		lineCoordinates.setTextContent(startCoords + " " + endCoords);
		lineString.appendChild(lineCoordinates);
			
		// add the lineString element to the placemark
		placemark.appendChild(lineString);
		
		// add the trajectory to the document
		parentElement.appendChild(placemark);
		
		// return the new trajectory
		return placemark;
			
	} // end addTrajectory element
	
	/**
	 * A method to add a line style element to the root folder
	 *
	 * @param styleId    the id attribute for this style
	 * @param lineColour the colour of this line
	 * @param lineWidth  the width of the line
	 */
	public void addLineStyle(String styleId, String lineColour, String lineWidth) {
		addLineStyle(null, styleId, lineColour, lineWidth);
	} // end add LineStyle method
	
	/**
	 * A method to determine the colour in a gradient at a particular index, assuming a zero based index
	 * The gradient is from pure red to pure yellow, with the alpha channel set to fully opaque
	 *
	 * @param index   the index number for this part of the colour gradient
	 * @param maximum the maximum number of parts in the gradient
	 *
	 * @return    the colour value in KML notation - AABBGGRR
	 */
	public String getGradientColour(int index, int maximum) {
	
		// declare helper variables
		String redHex   = "FF";
		String greenHex = null;
		String blueHex  = "00";
		
		// declare values for the formula
		float minGreen = 0;
		float maxGreen = 255;
		
		// change the integers to floats
		float myIndex = index;
		float myMaximum = maximum;
		
		// calculate the green value & round to nearest integer
		int greenValue =  Math.round(minGreen + ((maxGreen - minGreen) * (myIndex / (myMaximum -1))));
		
		// convert the green value to hex
		greenHex = Integer.toHexString(greenValue).toUpperCase();
		
		// ensure the hex is the right length
		if(greenHex.length() == 1) {
			greenHex = "0" + greenHex;
		}
		
		// return the colour value
		return "FF" + blueHex + greenHex + redHex;
	
	} // end getGradientColour method
	
	/**
	 * A method to normalise a potential colour index into the allowed range
	 *
	 * @param index   the current index
	 * @param maximum the current maximum index
	 *
	 * @return        the normalised value
	 */
	public int getNormalisedColourIndex(int index, int maximum) {
	
		// declare constants
		final float allowedMin = 1;
		final float allowedMax = 255;
		final float rangeMin   = 1;
		
		// convert parameters to floats
		float rangeIndex = index;
		float rangeMax   = maximum;
		
		// calculate the index		
		int newIndex =  Math.round(((allowedMax - allowedMin) / (rangeMax - rangeMin)) * (rangeIndex - rangeMin) + allowedMin);
		
		// sanitise the index and return
		if(newIndex < 2) {
			return 2;
		}else {
			return newIndex;
		}

	} // end getNormalisedColourIndex method
	
	/**
	 * A method to return the string representation of this KML file
	 *
	 * @return a string containing the XML that makes up this KML file
	 */
	public String toString() {
	
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
			throw new RuntimeException("Unable to convert DOM to string", ex);
		}
	
	} // end toString method

} // end class definition
