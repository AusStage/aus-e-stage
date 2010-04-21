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
		
		if(name == "") {
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
		
		if(content == "") {
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
		
		if(folderName == "") {
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
		
		if(name == "") {
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
		
		if(content == "") {
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
		
		if(name == "" || href == "") {
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
		
		if(content == "") {
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
		
		if(id == "") {
			throw new IllegalArgumentException("The id for the style must be specified");
		}
		
		if(href == "") {
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
		
		if(name == "") {
			throw new IllegalArgumentException("The name of this placemark must be specified");
		}
		
		if(description == null) {
			throw new IllegalArgumentException("The description of this placemark must be specified");
		}
		
		description = description.trim();
		
		if(description == "") {
			throw new IllegalArgumentException("The description of this placemark must be specified");
		}
		
		if(style == null) {
			throw new IllegalArgumentException("The style id for use with this placemark must be specified");
		}
		
		style = style.trim();
		
		if(style == "") {
			throw new IllegalArgumentException("The style id for use with this placemark must be specified");
		}
		
		if(latitude == null) {
			throw new IllegalArgumentException("The latitude for use with this placemark must be specified");
		}
		
		latitude = latitude.trim();
		
		if(latitude == "") {
			throw new IllegalArgumentException("The latitude for use with this placemark must be specified");
		}
		
		if(longitude == null) {
			throw new IllegalArgumentException("The longitude for use with this placemark must be specified");
		}
		
		longitude = longitude.trim();
		
		if(longitude == "") {
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
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			
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
